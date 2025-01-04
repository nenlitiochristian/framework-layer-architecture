package model;

import observer.Observable;
import prototype.DeepClonable;

public class Order extends Observable implements DeepClonable<Order> {
	private final Toy orderedToy;
	private final int level;
	private int countdown;

	@Override
	public Order deepClone() {
		return new Order(orderedToy.deepClone(), level, countdown);
	}

	public Order(Toy orderedToy, int level, int countdown) {
		this.orderedToy = orderedToy;
		this.level = level;
		this.countdown = countdown;
	}

	public boolean isOverdue() {
		return countdown <= 0;
	}

	public int finishOrder() {
		return orderedToy.sellToy(orderedToy.getToyAmount());
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

	public Order decrementCountdown(int countdown) {
		countdown--;
		notify("updated");
		return this;
	}

}