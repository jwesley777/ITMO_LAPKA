public enum Space {
	CANTINA("CANTINA",true,0),
	ROCKET_FIS("ROCKET_FIS",true,1),
	ROCKET_NIN("ROCKET_NIN", true,2),
	CAVE("CAVE",false,3),
	DESERT("DESERT",true,4),
	BLACK_HOLE("BLACK_HOLE", false,5), // TVOYA MAMA AHAHAHAH SVERHTYAZHOLAYA AAHHAHAHAHAHAAHAHHAHAHAHAHA
	ANATOMIC_PARK("ANATOMIC_PARK", true,6),
	CITADEL("CITADEL", true,7);



	
	Resource[][] objects;
	Animal[][] creatures;
	final static int w = 10;
	final static int h = 5;
	boolean defaultGravity;
	boolean gravity;
	Space prev;
	Space next;
	String name;
	int order;
	boolean bunker;

	Space(String name, boolean defaultGravity, int order){
		this.name= name;
		this.defaultGravity = defaultGravity;
		this.gravity = this.defaultGravity;
		this.creatures = new Animal[w][h];
		this.objects = new Resource[w][h];
		this.order = order;
	}
	
	static {
		CANTINA.next = ROCKET_FIS;
		ROCKET_FIS.prev = CANTINA;
		ROCKET_FIS.next = ROCKET_NIN;
		ROCKET_NIN.prev = ROCKET_FIS;
		ROCKET_NIN.next = CAVE;
		CAVE.prev = ROCKET_NIN;
		CAVE.next = DESERT;
		DESERT.prev = CAVE;
		DESERT.next = BLACK_HOLE;
		BLACK_HOLE.prev = DESERT;
		BLACK_HOLE.next = ANATOMIC_PARK;
		ANATOMIC_PARK.prev = BLACK_HOLE;
		ANATOMIC_PARK.next = CITADEL;
		CITADEL.prev = ANATOMIC_PARK;
		CAVE.bunker = true;
	}
	
	void printContents() {
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
				if (objects[i][j] != null)
					System.out.println(objects[i][j]);
		System.out.println();
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
				if (creatures[i][j] != null)
					System.out.println(creatures[i][j]);
		System.out.println();
	}
	void resetGravity() {
		gravity = defaultGravity;
	}
	void disableGravity() {
		gravity = false;
	}
	
	public String toString() {
		return name;
	}

	public static void main(String[] args) {

	}
}
