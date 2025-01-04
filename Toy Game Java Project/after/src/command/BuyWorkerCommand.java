package command;

import model.PlayerData;
import model.PlayerData.Memento;

public class BuyWorkerCommand implements Command {
	private PlayerData playerData;
	private Memento previousData;

	@Override
	public void execute() {
		previousData = playerData.saveState();
		System.out.println("Bought a level 1 worker for 500 gold");
		playerData.buyWorker();
	}

	public BuyWorkerCommand(PlayerData playerData) {
		super();
		this.playerData = playerData;
	}

	@Override
	public void undo() {
		playerData.restoreState(previousData);
	}
}
