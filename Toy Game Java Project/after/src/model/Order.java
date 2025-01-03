package model;

import observer.Observable;
import utils.RandomNumber;

public class Order extends Observable {
	private final Toy orderedToy;
	private final int level;
	private int countdown;

	public Order(int workerAmount, int difficulty) {
		int quantity = RandomNumber.generate(0, difficulty - 1) + RandomNumber.generate(0, 55 + difficulty - 1) + 100;
		int level = (RandomNumber.generate(0, difficulty - 1) % 5) + 1;
		int countdown = (quantity / workerAmount) / RandomNumber.generate(1, difficulty);
		ToyType toyType;
		switch (RandomNumber.generate(1, 6)) {
		case 1:
			toyType = ToyType.TEDDY_BEAR;
			break;
		case 2:
			toyType = ToyType.TOY_CAR;
			break;
		case 3:
			toyType = ToyType.TOY_PLANE;
			break;
		case 4:
			toyType = ToyType.RC_CAR;
			break;
		case 5:
			toyType = ToyType.TRAIN_SET;
			break;
		case 6:
			toyType = ToyType.TRANSFORM_ROBOT;
			break;
		default:
			throw new IllegalStateException("RandomNumber.generate(1, 6) generated numbers outside of 1 to 6 idk how");
		}
		this.orderedToy = new Toy(toyType, quantity);
		this.level = level;
		this.countdown = countdown;
	}

	// use this only when loading an existing player
	public Order(Toy orderedToy, int level, int countdown) {
		this.orderedToy = orderedToy;
		this.level = level;
		this.countdown = countdown;
	}

	public Toy getToy() {
		return orderedToy;
	}

	public int getLevel() {
		return level;
	}

	public int getCountdown() {
		return countdown;
	}

	public boolean isOverdue() {
		return countdown <= 0;
	}

	public int finishOrder() {
		return orderedToy.sellToy(orderedToy.getToyAmount());
	}

	public Order decrementCountdown(int countdown) {
		countdown--;
		notify("updated");
		return this;
	}
}