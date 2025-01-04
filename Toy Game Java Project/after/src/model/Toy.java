package model;

import prototype.DeepClonable;

public class Toy implements DeepClonable<Toy> {
	private final ToyType toyType;
	private int toyAmount = 0;

	@Override
	public Toy deepClone() {
		return new Toy(toyType, toyAmount);
	}

	public Toy(ToyType toyType, int toyAmount) {
		this.toyType = toyType;
		this.addToy(toyAmount);
	}

	public ToyType getToyType() {
		return toyType;
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