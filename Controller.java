import java.util.ArrayList;
public class Controller {
	public class NuclearWar { // example of perfect OOP
		public NuclearWar() {
			System.out.println();
			System.out.println("ATTENTION!!1111 Nuclear war has begun");
			System.out.println();
		}
		public void attack() {
			try {
				for (Space s : Space.values())
					if (!s.bunker)
						Weather.lightning(s);
			//} catch (EveryoneShockedException ex) {
			//	System.out.println(ex.getMessage());
			} finally {
				System.out.println();
				System.out.println("It was a nuclear explosion");
				System.out.println();
			}
		}
	}
	public static class Weather {
		public static void lightning(Space s) {
			try {
				for (int i = 0; i < Space.w; i++)
					for (int j = 0; j < Space.h; j++)
						if (s.creatures[i][j] != null) {
							s.creatures[i][j].shock();
							if (Animal.aliveOnes == 0) throw new EveryoneShockedException();
						}
			} catch (EveryoneShockedException ex) {
				System.out.println(ex.getMessage());
			} finally {
				System.out.println("Explosion in " + s);
			}
		}
	}

	private static ArrayList<Animal> creatures = new ArrayList<Animal>();
	public static void add(Human h, Space s, int x, int y) {
		if ((x < 0) || (x >= Space.w) || (y < 0) || (y >= Space.h)) {
			System.out.println("Wrong position: wrong x or y");
			return;
		}
		if (s.creatures[x][y] == null) {
			h.currentSpace = s;
	                h.x = x;
	                h.y = y;
	                h.currentSpace.creatures[x][y] = h; 
			creatures.add(h);
			Animal.aliveOnes++;
		}
		else System.out.println("Wrong position: is busy");
	}
	public static void add(Resource r, Space s, int x, int y) {
		if ((x < 0) || (x >= Space.w) || (y < 0) || (y >= Space.h)) {
			System.out.println("Wrong position: wrong x or y");
			return;
		}
		if (s.objects[x][y] == null) {
			r.currentSpace = s;
	                r.x = x;
	                r.y = y;
	                r.currentSpace.objects[x][y] = r; 
		}
		else System.out.println("Wrong position: is busy");
	}

	public static void travel(Human h,Space destSpace,int destX,int destY){
		System.out.println(h.name + " travels to " + destSpace.name + "(" + destX+","+destY+")");
		if (h.shocked) {
			System.out.println(h.name + " is shocked and can't travel");
			return;
		}
		for (int i = 0; i < 120; i++) {
			int generalX = h.currentSpace.order*Space.w + h.x;
			int generalY = h.currentSpace.order*Space.h + h.y;
			int generalDestX = destSpace.order*Space.w + destX;
			int generalDestY = destSpace.order*Space.h + destY;
			int hui = 2;
			if (generalY < generalDestY) {
				h.jump();
			}
			if (generalY > generalDestY) {
				h.fall();
			}
			if (generalX < generalDestX) {
				int xx;
				if (h.x == 9) {
					xx = 0;
					if (h.currentSpace.prev.creatures[xx][h.y] != null)
						h.jump();
				}
				else {
					xx = h.x+1;
					if (h.currentSpace.creatures[xx][h.y] != null)
						h.jump();
				}
				h.moveRight();
			}
			if (generalX > generalDestX) {
				int xx;
				if (h.x == 0) {
					xx = 9;
					if (h.currentSpace.prev.creatures[xx][h.y] != null)
						h.jump();
				}
				else {
					xx = h.x-1;
					if (h.currentSpace.creatures[xx][h.y] != null)
						h.jump();
				}
				
				//TODO moveLeft
				h.moveLeft();
			}
			if ((generalX==generalDestX)&&(generalY==generalDestY))
				break;
			//tick - TODO implement
			tick();
			if ((generalX==generalDestX)&&(generalY==generalDestY))
				break;
			System.out.println(h.name+" at: "+h.currentSpace.name + "(" + h.x+","+h.y+")");
		}
		if ((h.x != destX) || (h.y != destY)) {
			System.out.println(h.name+": Can't reach destination for some reason (usually because of gravity or destination place is occupied)");
			System.out.println(h.x+" "+h.y+" != "+destX+" "+destY);
		}
	}

	public static void tick() {
		for (Animal a: creatures)
			if (checkGravity(a))
				a.fall();
	}
	private static boolean checkGravity(Animal a) {
		if (a.x > 0) {
			Animal a2 = a.currentSpace.creatures[a.x-1][a.y];
			if ((a2!=null) && (a2.lunit))
				return true;
		}
		if (a.y > 0) {
			Animal a2 = a.currentSpace.creatures[a.x][a.y-1];
			if ((a2!=null) && (a2.lunit))
				return true;
		}
		if (a.x < Space.w-1) {
			Animal a2 = a.currentSpace.creatures[a.x+1][a.y];
			if ((a2!=null) && (a2.lunit))
				return true;
		}
		if (a.y < Space.h-1 ) {
			Animal a2 = a.currentSpace.creatures[a.x][a.y+1];
			if ((a2!=null) && (a2.lunit))
				return true;
		}
		if (a.lunit) return true;
		if (a.currentSpace.gravity)
			return true;
		return false;
	}

	public static void comment(String s) {
		System.out.println();
		System.out.println("--> " + s + " <--");
		System.out.println();
	}
	public static void main (String[] args) {
		Controller c = new Controller();
		Genius znaika = new Genius("Znaika");
		Human pilulkin = new Human("Pilulkin");
		add(znaika, Space.CAVE, 1,1);
		add(pilulkin, Space.CAVE, 2,1);
		add(new Resource("Marijuana semen"),Space.ROCKET_NIN,1,0);
		add(new Resource("Semen"),Space.ROCKET_NIN,2,0);
		add(new Resource("Watering can"), Space.ROCKET_FIS, 5,0);

		comment("Znaika vzletel k potolku");
		travel(znaika, Space.CAVE, 3,4);

		comment("Znaika reshil chto v ROCKET_FIS ne hvataet lunita");
		znaika.mine(Source.LUNIT_PIT);
		pilulkin.mine(Source.ANTILUNIT_PIT);
		travel(znaika, Space.ROCKET_FIS,1,0);
		travel(pilulkin, Space.ROCKET_FIS,2,0);
		znaika.drop();
		pilulkin.drop();

		comment("Znaika vspomnil chto koe-chto zabyl v ROCKET_NIN");
		travel(znaika, Space.ROCKET_NIN,1,0);
		travel(pilulkin,Space.ROCKET_NIN,2,0);
		znaika.take();
		pilulkin.take();
		travel(znaika, Space.ROCKET_FIS,4,0);
		travel(pilulkin, Space.ROCKET_FIS,3,0);
		znaika.drop();
		pilulkin.drop();



		NuclearWar nw = c.new NuclearWar();

		comment("Znaika ohuel i reshil spryatatsya. No Pilulkin fanat Fallout i reshil ostatsya");
		travel(znaika, Space.CAVE, 5,1);
		nw.attack();

		comment("Znaika vspomnil kak Pilulkin uchil ego medicine");
		travel(znaika, Space.ROCKET_FIS, 5,0);
		znaika.take();
		znaika.pour(pilulkin);

		comment("Znaika i pilulkin poshli buhat'");
		travel(znaika, Space.CANTINA, 2,0);
		travel(pilulkin, Space.CANTINA, 3,0);

		comment("Znaika i pilulkin pobuhali boyaryshnik. Vot kakoi korotyshka byl etot Znaika");
	}
}
