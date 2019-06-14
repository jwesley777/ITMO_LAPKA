public class Genius extends Human {
	Genius(String name) { super(name); }
	Genius(String name, Space currentSpace, int x, int y) {
		super(name,currentSpace,x,y);
	}
	public void resetGravity() {
		this.currentSpace.resetGravity();
		if (shocked) {
			System.out.println(this + " is shocked and can't move");
			return;
		}
	}
	public void disableGravity() {
		if (shocked) {
			System.out.println(this + " is shocked and can't move");
			return;
		}
		this.currentSpace.disableGravity();
	}
}
