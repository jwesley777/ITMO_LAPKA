import java.io.Serializable;

abstract class Animal implements Movable, Serializable {
	static int aliveOnes = 0;
	Space currentSpace;
	boolean shocked;
	int x;
	int y;
	boolean lunit;	
	void moveLeft() { // fix leftmost edge
		if (shocked) {
			System.out.println(this + " is shocked and can't move");
			return;
		}
		currentSpace.creatures[x--][y] = null;
		if (x < 0) {
			currentSpace = currentSpace.prev;
			x = 9;
			if (currentSpace.creatures[x][y] == null)
				currentSpace.creatures[x][y] = this;
			else {
				currentSpace = currentSpace.next;
				x = 0;
				currentSpace.creatures[x][y] = this;
			}
			return;
		}
		if (currentSpace.creatures[x][y]==null)
			currentSpace.creatures[x][y] = this;
		else {
			x++;
			currentSpace.creatures[x][y] = this;
		}
	}
	void moveRight() { // fix leftmost edge
		if (shocked) {
			System.out.println(this + " is shocked and can't move");
			return;
		}
		currentSpace.creatures[x++][y] = null;
		if (x > 9) {
			currentSpace = currentSpace.next;
			x = 0;
			if (currentSpace.creatures[x][y] == null) 
				currentSpace.creatures[x][y] = this;
			else {
				currentSpace = currentSpace.prev;
				x = 9;
				currentSpace.creatures[x][y] = this;
			}
			return;
		}
		if (currentSpace.creatures[x][y]==null)
			currentSpace.creatures[x][y]= this;
		else {
			x--;
			currentSpace.creatures[x][y] = this;
		}
	}
	void jump() {
		if (shocked) {
			System.out.println(this + " is shocked and can't move");
			return;
		}
		currentSpace.creatures[x][y++] = null;
		if ((y > 4) || (currentSpace.creatures[x][y]!=null))
			y--;
		currentSpace.creatures[x][y] = this;

	}
	void fall() {
		currentSpace.creatures[x][y--] = null;
		if ((y < 0) || (currentSpace.creatures[x][y]!=null))
			y++;
		currentSpace.creatures[x][y] = this;

	}
	public boolean isGrounded() {
		return y==0;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	void shock() {
		shocked = true;
		aliveOnes--;
		System.out.println(this + " is shocked");
	}
}
