import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Date;
import java.sql.*;

public class Human extends Animal implements IHands, Comparable<Human>, Serializable, iQuery {
	Resource inHands;

	int size = 180;
	OffsetDateTime date;
	String name;
	
	public Human(String name) { this.name = name; }
	Human(String name, Space currentSpace, int x, int y) {
		this.name = name;
		this.currentSpace = currentSpace;
		this.x = x;
		this.y = y;
		currentSpace.creatures[x][y] = this;
		date = OffsetDateTime.now();
	}

	Human(String name, int size, Space currentSpace, int x, int y) {
		this.name = name;
		this.currentSpace = currentSpace;
		this.x = x;
		this.y = y;
		currentSpace.creatures[x][y] = this;
		date = OffsetDateTime.now();
		this.size = size;
	}
	Human(String name, int size, Space currentSpace, int x, int y, OffsetDateTime date) {
		this.name = name;
		this.currentSpace = currentSpace;
		this.x = x;
		this.y = y;
		currentSpace.creatures[x][y] = this;
		this.date = date;
		this.size = size;
	}

	public void take() {
		if (shocked) { // perfect exception handling #1
			System.out.println(this + " is shocked and can't move");
			return;
		}
		if (!isGrounded()) { // perfect exception handling #2
			System.out.println("Must be grounded to take smthng");
			return;
		}
		if (currentSpace.objects[x][0]!= null) { // perfect exception handling #3
			inHands = currentSpace.objects[x][0];
			inHands.currentSpace = null;
            System.out.println(name + " takes " + inHands.name);

			currentSpace.objects[x][y] = null;
		}
	}
	public void drop() {
		if (shocked) { // perfect exception handling #4
			System.out.println(this + " is shocked and can't move");
			return;
		}
		if (inHands==null) { // perfect exception handling #5
			System.out.println(name + ": nothing to drop");
			return;
		}
		if (currentSpace.objects[x][0] == null) { // perfect exception handling #6
			currentSpace.objects[x][0] = inHands;
            System.out.println(name + " drops " + inHands.name);
			inHands.currentSpace = currentSpace;
			inHands.x = x;
			inHands.y = y;
			inHands = null;
		}
		else {
			System.out.println(name + ": can't drop item. This place is occupied by another item");
		}
	}
	public void consumeLunit() {
		if (shocked) { // perfect exception handling #7
			System.out.println(this + " is shocked and can't move");
			return;
		}
		try { // superior exception handling
			if (inHands==null || !inHands.name.equals("Lunit")) throw new NoItemException(this, "Lunit");
			lunit = true;
			inHands = null;
		}
		catch (NoItemException ex) {
			System.out.println(ex.getMessage());
		}

	}
	public void mine(Source s) {
		if (shocked) { // perfect exception handling #8
			System.out.println(this + " is shocked and can't move");
			return;
		}
		if (inHands == null) { // perfect exception handling #9
			Resource r = s.mine();
			inHands = r;
			System.out.println(name + " mines " + inHands.name);
		}
		else System.out.println(name + "'s hands заняты");
	}
	public void printHandsContent() {
		System.out.println(inHands);
	}
	public void pour(Animal a) {
		if (shocked) { // perfect exception handling #10
			System.out.println(this + " is shocked and can't move");
			return;
		}
		if (currentSpace != a.currentSpace) { // perfect exception handling #11
			System.out.println(this + " is too far away from " + a + " and can't pour water on him");
		}
		try { // superior exception handling
			if (inHands==null || !inHands.name.equals("Watering can")) throw new NoItemException(this, "Watering can");
			Water water = new Water() {
				public void hit(Animal a) {
					a.shocked = false;
					System.out.println(Human.this + " poured some water on " + a);
					System.out.println(a + " opened his eyes");
				}
			};
			water.hit(a);
		}
		catch (NoItemException ex) {
			System.out.println(ex.getMessage());
		}
	}
	public String toString() { return name+
			" ("+size+", "+date+ ")"
			+" in "+currentSpace+" ("+x+", "+y+")"; }
	
	private static void test () {
		Human h = new Human("Luke",Space.CANTINA, 1,1);
		h.inHands = new Resource("Watering can");
		Human h2 = new Human("Obi Wan", Space.CANTINA, 2,1);
		h.pour(h2);
	}

	public int compareTo(Human that) {
		return this.name.compareTo(that.name);
	}
/*
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (getClass() != o.getClass()) return false;
		Human that = (Human) o;

		if (this.name.equals(that.name)) return true;
		return false;
	}
*/
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (getClass() != o.getClass()) return false;
		Human that = (Human) o;

		if (!this.name.equals(that.name)) return false;
		if (!this.currentSpace.equals(that.currentSpace)) return false;
		if (this.x != that.x) return false;
		if (this.y != that.y) return false;

		return true;
	}

	public String getInsertSqlQuery(String login, String password) {

		String query = String.format("insert into %s (%s, %s, %s, %s, %s, %s, time) values ('%s', '%s', %d, %d, %d, %d, %s)",
				DBConst.HUMAN_TABLE,DBConst.USERS_LOGIN,DBConst.HUMAN_NAME,DBConst.HUMAN_SIZE,
				DBConst.HUMAN_X,DBConst.HUMAN_Y,DBConst.HUMAN_SPACE,
				login, name,size,x,y,currentSpace.ordinal(), "?");
		System.out.println(query);
		return query;
	}

	public String getInsIfMaxSqlQuery(String login, String password) {
		String query = String.format("insert into humans (login, name, size, x, y, space, time) select '%s', '%s', %d, %d, %d, %d, %s where not exists (select max(name) from humans where name > '%s')",
				login, name, size, x, y, currentSpace.ordinal(), "?",login,name
				);
		System.out.println(query);
		return query;
	}

	public String getDelSqlQuery(String login, String password) {
		String query =  String.format("delete from %s where %s = '%s' and %s = '%s' and %s = %d and %s = %d and %s = %d and %s = %d",
				DBConst.HUMAN_TABLE,DBConst.USERS_LOGIN,login,DBConst.HUMAN_NAME,name,DBConst.HUMAN_SIZE,size,
				DBConst.HUMAN_X,x,DBConst.HUMAN_Y,y,DBConst.HUMAN_SPACE,currentSpace.ordinal());
		System.out.println(query);
		return query;
	}

	public String getDeleteLowerSqlQuery(String login, String password) {
		return String.format("delete from %s where %s='%s' and %s < '%s'",
				DBConst.HUMAN_TABLE, DBConst.USERS_LOGIN, login, DBConst.HUMAN_NAME,name);
	}

	interface Water {
		void hit(Animal a);
	}
}
