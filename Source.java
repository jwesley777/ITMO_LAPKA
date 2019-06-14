enum Source {
	LUNIT_PIT("Lunit", 100, Space.CAVE),
	ANTILUNIT_PIT("Antilunit", 100, Space.CAVE);
	String name;
	int amount;
	Space space;
	
	Source(String name, int amount, Space space) {
		this.name = name;
		this.amount = amount;
		this.space = space;
	}
	
	public int check() { return amount; }
	public Resource mine() { return new Resource(this.name); }
}
