package model;

public enum ToyType {
	TEDDY_BEAR("Teddy Bear", 2), TOY_CAR("Toy Car", 5), TOY_PLANE("Toy Plane", 7), RC_CAR("RC Car", 15),
	TRAIN_SET("Train Set", 25), TRANSFORM_ROBOT("Transform Robot", 55);

	private final String toyName;
	private final int price;

	private ToyType(String toyName, int price) {
		this.toyName = toyName;
		this.price = price;
	}

	public String getToyName() {
		return toyName;
	}

	public int getPrice() {
		return price;
	}
}
