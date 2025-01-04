package factory;

import model.Order;
import model.Toy;
import model.ToyType;
import utils.RandomNumber;

public class OrderFactory {

	public Order createOrder(int workerAmount, int difficulty) {
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
		return new Order(new Toy(toyType, quantity), level, countdown);
	}
}
