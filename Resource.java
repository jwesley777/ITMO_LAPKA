import java.io.Serializable;

public class Resource implements Serializable {
	String name;
	Space currentSpace;
	int x;
	int y;
	
	Resource(String name) { this.name = name; }
	Resource(String name, Space currentSpace, int x, int y) {
		this.name = name;
		this.currentSpace = currentSpace;
		this.x = x;
		this.y = y;
		currentSpace.objects[x][y] = this;
	}
	

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (getClass() != o.getClass()) return false;
		Resource that = (Resource) o;
		if (this.name.equals(that.name)) return true;
		return false;
	}
	public String toString() {
		return this.name + " " + currentSpace + " " + x + " " + y;
	}

	// int compareTo(T that) {

	//}
	
	public static void main(String[] args) {
		// TEST THINGS
		Resource r = new Resource("chlen", Space.CANTINA, 2,2);
		Resource r2 = new Resource("chlen", Space.CANTINA, 2,3);
		Resource r3 = new Resource("nechlen", Space.CANTINA, 2,4);
		System.out.println(r.equals(r2));
		System.out.println(r.equals(r3));
		System.out.println(r);

		// lABA5 STUFF

	}
}

