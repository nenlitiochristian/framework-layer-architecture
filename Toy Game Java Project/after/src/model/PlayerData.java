package model;

import factory.OrderFactory;
import game.ToyList;
import game.WorkerList;
import observer.Observable;
import observer.Observer;
import utils.RandomNumber;

public class PlayerData extends Observable implements Observer {
	private final String username;
	private int money;
	private int difficulty;
	private int ordersDone;
	private int currentExperience;
	private boolean isFinished;

	private Order currentOrder;
	private ToyList myToys;
	private WorkerList myWorkers;

	// constructor private, memaksa client harus bikin lewat builder
	private PlayerData(String username, int money, int difficulty, int ordersDone, int currentExperience,
			boolean isFinished, WorkerList myWorkers, ToyList myToys, Order currentOrder) {
		this.username = username;
		this.money = money;
		this.difficulty = difficulty;
		this.ordersDone = ordersDone;
		this.currentExperience = currentExperience;
		this.isFinished = isFinished;

		this.myWorkers = myWorkers;
		myWorkers.subscribe(this);
		this.myToys = myToys;
		myToys.subscribe(this);
		this.currentOrder = currentOrder;
		currentOrder.subscribe(this);
	}

	// sebenernya bisa bikin di file tersendiri, tapi dengan bikin di satu spot
	// akan lebih jelas dan gaperlu cari-cari di file lain
	public static class PlayerDataBuilder {
		private int money = 0;
		private int difficulty = 1;
		private int ordersDone = 0;
		private int currentExperience = 0;
		private boolean isFinished = false;

		private WorkerList myWorkers = null;
		private ToyList myToys = null;
		private Order currentOrder = null;

		public PlayerDataBuilder money(int money) {
			this.money = money;
			return this;
		}

		public PlayerDataBuilder difficulty(int difficulty) {
			this.difficulty = difficulty;
			return this;
		}

		public PlayerDataBuilder ordersDone(int ordersDone) {
			this.ordersDone = ordersDone;
			return this;
		}

		public PlayerDataBuilder currentExperience(int currentExperience) {
			this.currentExperience = currentExperience;
			return this;
		}

		public PlayerDataBuilder isFinished(boolean isFinished) {
			this.isFinished = isFinished;
			return this;
		}

		public PlayerDataBuilder myWorkers(WorkerList myWorkers) {
			this.myWorkers = myWorkers;
			return this;
		}

		public PlayerDataBuilder myToys(ToyList myToys) {
			this.myToys = myToys;
			return this;
		}

		public PlayerDataBuilder currentOrder(Order currentOrder) {
			this.currentOrder = currentOrder;
			return this;
		}

		// build step memaksa username dikasih, karena required
		public PlayerData build(String username) {
			if (myWorkers == null) {
				myWorkers = new WorkerList();
			}
			if (myToys == null) {
				myToys = new ToyList();
			}
			if (currentOrder == null) {
				OrderFactory factory = new OrderFactory();
				currentOrder = factory.createOrder(myWorkers.getTotalWorkers(), difficulty);
			}
			return new PlayerData(username, money, difficulty, ordersDone, currentExperience, isFinished, myWorkers,
					myToys, currentOrder);
		}
	}

	public int decideWorkhours() {
		return RandomNumber.generate(7, 15);
	}

	public int makeToys(int workhours) {
		ToyType currentType = currentOrder.getToy().getToyType();
		int toysMade = (int) myWorkers.getTotalWorkersWeighted() * workhours / currentOrder.getLevel();
		myToys.getToy(currentType).addToy(toysMade);
		return toysMade;
	}

	public boolean orderCanBeFinished() {
		ToyType currentType = currentOrder.getToy().getToyType();
		if (myToys.getToy(currentType).getToyAmount() >= currentOrder.getToy().getToyAmount())
			return true;

		return false;
	}

	public int finishOrder() {
		ToyType currentType = currentOrder.getToy().getToyType();
		int amountSold = currentOrder.getToy().getToyAmount();
		int earnedMoney = myToys.getToy(currentType).sellToy(amountSold);
		OrderFactory factory = new OrderFactory();
		setCurrentOrder(factory.createOrder(myWorkers.getTotalWorkers(), difficulty));
		ordersDone++;

		earnExperience(1);
		earnMoney(earnedMoney);
		return earnedMoney;
	}

	private int calculateLevelUpTarget(int x) {
		if (x == 1 || x == 2)
			return 3;
		return calculateLevelUpTarget(x - 1) + calculateLevelUpTarget(x - 2)
				+ (int) (0.5 * calculateLevelUpTarget(x - 1));
	}

	public boolean tryUpgradingWorker(int workerLevel) {
		if (workerLevel == WorkerList.MAX_WORKER_LEVEL)
			return false;
		if (money < myWorkers.getUpgradePrice(workerLevel))
			return false;
		spendMoney(myWorkers.getUpgradePrice(workerLevel));
		myWorkers.upgradeWorkers(workerLevel);
		return true;
	}

	public String getUsername() {
		return username;
	}

	public int getMoney() {
		return money;
	}

	public int getCurrentExperience() {
		return currentExperience;
	}

	public int getOrdersDone() {
		return ordersDone;
	}

	public Order getCurrentOrderData() {
		return currentOrder;
	}

	public ToyList getToyList() {
		return myToys;
	}

	public WorkerList getWorkerList() {
		return myWorkers;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public PlayerData buyWorker() {
		money -= 500;
		myWorkers.addNewWorker();
		notify("updated");
		return this;
	}

	public PlayerData earnExperience(int amount) {
		currentExperience += amount;

		while (currentExperience >= calculateLevelUpTarget(difficulty)) {
			currentExperience -= calculateLevelUpTarget(difficulty);
			difficulty++;
		}

		notify("updated");
		return this;
	}

	public PlayerData earnMoney(int amount) {
		money += amount;
		notify("updated");
		return this;
	}

	public PlayerData spendMoney(int amount) {
		money -= amount;
		notify("updated");
		return this;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
		notify("updated");
	}

	@Override
	public void receive(String message) {
		notify(message);
	}

	private void setCurrentOrder(Order currentOrder) {
		this.currentOrder = currentOrder;
		this.currentOrder.subscribe(this);
		notify("updated");
	}

	public Order getOrder() {
		return currentOrder;
	}

	public boolean isFinished() {
		return isFinished;
	}

}