package command;

import model.PlayerData;
import model.PlayerData.Memento;

public class ProduceToysCommand implements Command {

	private PlayerData playerData;
	private Memento previousData;
	private int workhours;

	public ProduceToysCommand(PlayerData playerData, int workhours) {
		super();
		this.playerData = playerData;
		this.workhours = workhours;
	}

	@Override
	public void execute() {
		previousData = playerData.saveState();
		System.out.println(
				"Your factory has produced " + playerData.makeToys(workhours) + " toys in " + workhours + " hours");

		if (playerData.orderCanBeFinished()) {
			System.out.println("You have finished an order and received " + playerData.finishOrder() + "gold");
		}
	}

	@Override
	public void undo() {
		playerData.restoreState(previousData);
	}

}
