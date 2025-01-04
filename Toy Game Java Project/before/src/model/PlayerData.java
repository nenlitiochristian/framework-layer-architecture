package model;

import game.ToyList;
import game.WorkerList;
import utils.RandomNumber;

public class PlayerData {
	private final String username;
	private int money;
	private int difficulty;
	private int ordersDone;
	private int currentExperience;
	private boolean isFinished;

	private Order currentOrder;
	private ToyList myToys;
	private WorkerList myWorkers;

	public PlayerData(String username) {
		this.username = username;
		money = 0;
		difficulty = 1;
		ordersDone = 0;

		myWorkers = new WorkerList();
		myToys = new ToyList();
		currentOrder = new Order(myWorkers.getTotalWorkers(), difficulty);
	}

	public PlayerData(String username, int money, int difficulty, int ordersDone, int currentExperience,
			boolean isFinished, WorkerList myWorkers, ToyList myToys, Order currentOrder) {
		this.username = username;
		this.money = money;
		this.difficulty = difficulty;
		this.ordersDone = ordersDone;
		this.currentExperience = currentExperience;
		this.isFinished = isFinished;

		this.myWorkers = myWorkers;
		this.myToys = myToys;
		this.currentOrder = currentOrder;
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
		currentOrder = new Order(myWorkers.getTotalWorkers(), difficulty);
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
		return this;
	}

	public PlayerData earnExperience(int amount) {
		currentExperience += amount;

		while (currentExperience >= calculateLevelUpTarget(difficulty)) {
			currentExperience -= calculateLevelUpTarget(difficulty);
			difficulty++;
		}

		return this;
	}

	public PlayerData earnMoney(int amount) {
		money += amount;
		return this;
	}

	public PlayerData spendMoney(int amount) {
		money -= amount;
		return this;
	}

	public Order getOrder() {
		return currentOrder;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public ToyList getMyToys() {
		return myToys;
	}

	public WorkerList getMyWorkers() {
		return myWorkers;
	}
}