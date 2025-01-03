package model;

public class Toy {
	private final ToyType toyType;
	private int toyAmount = 0;

	public Toy(ToyType toyType, int toyAmount) {
		this.toyType = toyType;
		this.addToy(toyAmount);
	}

	public ToyType getToyType() {
		return toyType;
	}

	public String getToyName() {
		switch (toyType) {
		case RC_CAR:
			return "RC Car";
		case TEDDY_BEAR:
			return "Teddy Bear";
		case TOY_CAR:
			return "Toy Car";
		case TOY_PLANE:
			return "Toy Plane";
		case TRAIN_SET:
			return "Train Set";
		case TRANSFORM_ROBOT:
			return "Transform Robot";
		}
		return "Error";
	}

	public int getToyPrice() {
		return toyType.getPrice();
	}

	public int getToyAmount() {
		return toyAmount;
	}

	public Toy addToy(int amount) {
		toyAmount += amount;
		return this;
	}

	public int sellToy(int toyAmount) {
		if (this.toyAmount < toyAmount)
			throw new IllegalArgumentException("Not enough toys to sell");
		if (toyAmount < 0)
			throw new IllegalArgumentException("Amount can not be negative");
		this.toyAmount -= toyAmount;
		return toyAmount * toyType.getPrice();
	}

}