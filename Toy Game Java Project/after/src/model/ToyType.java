package model;

public enum ToyType {
	TEDDY_BEAR(2), TOY_CAR(5), TOY_PLANE(7), RC_CAR(15), TRAIN_SET(25), TRANSFORM_ROBOT(55);

	private int price;

	private ToyType(int price) {
		this.price = price;
	}

	public int getPrice() {
		return price;
	}
}