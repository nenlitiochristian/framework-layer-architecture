package game;

import java.util.ArrayList;
import java.util.List;

import model.Toy;
import model.ToyType;
import observer.Observable;

public class ToyList extends Observable {
	private List<Toy> toys = new ArrayList<>();

	public ToyList() {
		toys.add(new Toy(ToyType.TEDDY_BEAR, 0));
		toys.add(new Toy(ToyType.TOY_CAR, 0));
		toys.add(new Toy(ToyType.TOY_PLANE, 0));
		toys.add(new Toy(ToyType.RC_CAR, 0));
		toys.add(new Toy(ToyType.TRAIN_SET, 0));
		toys.add(new Toy(ToyType.TRANSFORM_ROBOT, 0));
	}

	public Toy getToy(ToyType toyType) {
		for (Toy toy : toys) {
			if (toy.getToyType() == toyType) {
				return toy;
			}
		}
		return null;
	}

	public ToyList addToy(ToyType toyType, int amount) {
		for (Toy toy : toys) {
			if (toy.getToyType() == toyType) {
				toy.addToy(amount);
				notify("updated");
			}
		}
		return this;
	}

	public int sellToy(ToyType toyType, int amount) {
		for (Toy toy : toys) {
			if (toy.getToyType() == toyType) {
				int money = toy.sellToy(amount);
				notify("updated");
				return money;
			}
		}
		throw new IllegalArgumentException("Invalid toy type");
	}
}