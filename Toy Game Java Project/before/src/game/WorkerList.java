package game;

public class WorkerList {
	public static final int MAX_WORKER_LEVEL = 5;
	private int[] workerAmounts = new int[MAX_WORKER_LEVEL];

	public WorkerList() {
		this.addNewWorker().addNewWorker().addNewWorker().addNewWorker().addNewWorker();
	}

	// use this only when loading an existing player
	public WorkerList(int levelOneWorkerAmount, int levelTwoWorkerAmount, int levelThreeWorkerAmount,
			int levelFourWorkerAmount, int levelFiveWorkerAmount) {
		workerAmounts[0] = levelOneWorkerAmount;
		workerAmounts[1] = levelTwoWorkerAmount;
		workerAmounts[2] = levelThreeWorkerAmount;
		workerAmounts[3] = levelFourWorkerAmount;
		workerAmounts[4] = levelFiveWorkerAmount;
	}

	public WorkerList addNewWorker() {
		workerAmounts[0]++;
		return this;
	}

	public int getUpgradePrice(int workerLevel) {
		switch (workerLevel) {
		case 1:
			return 600;
		case 2:
			return 800;
		case 3:
			return 1100;
		case 4:
			return 1500;
		default:
			return 999999;
		}
	}

	public WorkerList upgradeWorkers(int level) {
		if (level >= MAX_WORKER_LEVEL || level < 0) {
			return this;
		}

		workerAmounts[level - 1]--;
		workerAmounts[level]++;
		return this;
	}

	public int getWorker(int level) {
		return workerAmounts[level - 1];
	}

	public int getTotalWorkers() {
		int total = 0;
		for (int amount : workerAmounts) {
			total += amount;
		}
		return total;
	}

	public int getTotalWorkersWeighted() {
		int total = 0;
		int level = 1;
		for (int amount : workerAmounts) {
			total += amount * level;
			level++;
		}
		return total;
	}
}