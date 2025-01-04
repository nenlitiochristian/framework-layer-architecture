package command;

import model.PlayerData;
import model.PlayerData.Memento;

public class UpgradeWorkerCommand implements Command {
	private Memento previousData;
	private PlayerData playerData;
	private int workerLevel;

	@Override
	public void execute() {
		previousData = playerData.saveState();
		System.out.println("Upgraded a level " + (workerLevel) + " worker for "
				+ playerData.getWorkerList().getUpgradePrice(workerLevel) + " gold");
		playerData.upgradeWorker(workerLevel);
	}

	@Override
	public void undo() {
		playerData.restoreState(previousData);
	}
}
