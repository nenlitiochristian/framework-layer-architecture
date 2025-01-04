package game;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import command.BuyWorkerCommand;
import command.CommandManager;
import command.ProduceToysCommand;
import command.UpgradeWorkerCommand;
import model.PlayerData;
import model.Toy;
import model.ToyType;
import observer.Observer;
import utils.CachedRepository;
import utils.PlayerDataRepository;
import utils.Repository;

public class Game implements Observer {
	private Repository repository = new CachedRepository(new PlayerDataRepository());
	private PlayerData currentPlayer;
	private CommandManager invoker;
	private Scanner scan;

	@Override
	public void receive(String message) {
		if (message.equals("updated")) {
			repository.savePlayerData(currentPlayer);
		}
	}

	public Game(Scanner scan, PlayerData playerData) {
		currentPlayer = playerData;
		invoker = new CommandManager();
		this.scan = scan;
		startGame();
	}

	public void startGame() {
		currentPlayer.subscribe(this);
		int userChoice = -1;
		boolean continueGame = true;
		do {
			clearScreen();

			if (currentPlayer.getCurrentOrderData().getCountdown() <= 0) {
				showPlayerData(currentPlayer, "Order");
				System.out.println("You ran out of time!");
				System.out.println("Ending game...");
				endGame(currentPlayer);
				System.out.println("Press enter to go back...");
				try {
					System.in.read();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}

			showPlayerData(currentPlayer, "Order", "Player");
			System.out.println("1. Show Toy List");
			System.out.println("2. Produce Toys");
			System.out.println("3. Undo previous action");
			System.out.println("4. Manage Workers");
			System.out.println("5. Exit Game (Your progress is always saved automatically)");
			System.out.println("6. End Game");
			System.out.print(">> ");

			try {
				userChoice = Integer.parseInt(scan.next());
			} catch (NumberFormatException e) {
				userChoice = -1;
				continue;
			}
			switch (userChoice) {
			case 1:
				showToyList(currentPlayer);
				break;
			case 2:
				produceToys(currentPlayer);
				break;
			case 3:
				undoLastAction();
				break;
			case 4:
				manageWorkers(currentPlayer, scan);
				break;
			case 5:
				continueGame = false;
				break;
			case 6:
				clearScreen();
				String yesOrNo = "";
				boolean keepLooping = true;
				while (keepLooping) {
					System.out.print(
							"Are you sure you want to end your game? (you cannot load this save after this) [Y/N]: ");
					yesOrNo = scan.next();
					if (yesOrNo.length() == 1) {
						switch (yesOrNo.charAt(0)) {
						case 'Y':
						case 'y':
							endGame(currentPlayer);
							keepLooping = false;
							continueGame = false;
							break;
						case 'N':
						case 'n':
							keepLooping = false;
						}
					}
				}
				break;

			default:
				break;
			}
		} while (continueGame);
	}

	private void undoLastAction() {
		if (invoker.canUndoCommand()) {
			invoker.undoCommand();
		} else {
			System.out.println("There are no actions to undo!");
		}
	}

	private void showToyList(PlayerData playerData) {
		clearScreen();
		showPlayerData(playerData, "Order", "ToyList");
		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void produceToys(PlayerData playerData) {
		int workhours = playerData.decideWorkhours();
		for (String progressBar = "#"; progressBar.length() < "##########".length(); progressBar += "#") {
			clearScreen();
			System.out.println("Producing toys...");
			System.out.printf("[%-10s] %d%%\n", progressBar, progressBar.length() * 10);

			try {
				TimeUnit.MILLISECONDS.sleep(workhours * 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		clearScreen();
		showPlayerData(playerData, "Order");
		invoker.executeCommand(new ProduceToysCommand(playerData, workhours));

		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void manageWorkers(PlayerData playerData, Scanner scan) {
		int userChoice = -1;
		while (true) {
			clearScreen();
			showPlayerData(playerData, "WorkerList");
			System.out.println("Money: " + playerData.getMoney());
			System.out.println("1. Hire new worker ");
			System.out.println("2. Upgrade worker");
			System.out.println("3. Go back");
			System.out.print(">> ");

			try {
				userChoice = Integer.parseInt(scan.next());
			} catch (NumberFormatException e) {
				userChoice = -1;
				continue;
			}
			switch (userChoice) {
			case 1:
				buyNewWorker(playerData, scan);
				break;
			case 2:
				upgradeWorker(playerData, scan);
				break;
			case 3:
				return;
			default:
				break;
			}
		}
	}

	private void buyNewWorker(PlayerData playerData, Scanner scan) {
		clearScreen();
		showPlayerData(playerData, "WorkerList");
		String userChoice = "";
		boolean keepLooping = true;
		while (keepLooping) {
			System.out.print("Hire a new worker for 500 gold [Y/N]: ");
			userChoice = scan.next();
			if (userChoice.length() == 1) {
				switch (userChoice.charAt(0)) {
				case 'Y':
				case 'y':
					keepLooping = false;
					break;
				case 'N':
				case 'n':
					return;
				}
			}
		}

		if (playerData.getMoney() >= 500) {
			invoker.executeCommand(new BuyWorkerCommand(currentPlayer));
		} else {
			System.out.println("Not enough money");
		}

		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void upgradeWorker(PlayerData playerData, Scanner scan) {
		clearScreen();
		showPlayerData(playerData, "WorkerList");
		System.out.println(" No. | Upgrade | Price");
		int level = 1;
		int[] prices = { 600, 800, 1100, 1500 };
		for (int price : prices) {
			System.out.println("  " + level + "  | " + level + " -> " + (level + 1) + " | " + price);
			level++;
		}
		System.out.println("==================================");

		int userChoice = -1;
		while (true) {
			System.out.print("Input which upgrade you want to buy [only applies to one worker]: ");
			try {
				userChoice = Integer.parseInt(scan.next());
			} catch (NumberFormatException e) {
				userChoice = -1;
				continue;
			}
			if (userChoice >= 1 && userChoice <= 4)
				break;
		}

		int workerLevel = userChoice;

		String yesOrNo = "";
		boolean keepLooping = true;
		while (keepLooping) {
			System.out.print("Upgrade your worker for " + playerData.getWorkerList().getUpgradePrice(workerLevel)
					+ " gold [Y/N]: ");
			yesOrNo = scan.next();

			if (yesOrNo.length() == 1) {
				switch (yesOrNo.charAt(0)) {
				case 'Y':
				case 'y':
					keepLooping = false;
					break;
				case 'N':
				case 'n':
					return;
				}
			}
		}

		if (playerData.getWorkerList().getWorker(workerLevel) < 1) {
			System.out.println("You don't have a worker of that type");
		} else if (playerData.getMoney() < playerData.getWorkerList().getUpgradePrice(workerLevel)) {
			System.out.println("Not enough money");
		} else {
			invoker.executeCommand(new UpgradeWorkerCommand());
		}

		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void endGame(PlayerData playerData) {
		// setiap perubahan automatis memberikan notification
		// dimana Game akan receive dan masukkin ke db
		playerData.setFinished(true);
	}

	public void showPlayerData(PlayerData playerData, String... modes) {
		System.out.println(" " + playerData.getUsername() + "'s Factory");
		System.out.println("==================================");
		for (String mode : modes) {
			switch (mode) {
			case "Order":
				System.out.println(" Current Order");
				System.out.printf(" Toy Type: %s\n",
						playerData.getCurrentOrderData().getToy().getToyType().getToyName());
				System.out.printf(" Level: %s\n", playerData.getCurrentOrderData().getLevel());
				System.out.printf(" Quantity: %s\n", playerData.getCurrentOrderData().getToy().getToyAmount());
				System.out.printf(" Time: %s\n", playerData.getCurrentOrderData().getCountdown());
				System.out.println("==================================");
				break;

			case "Player":
				System.out.println(" " + playerData.getUsername() + "'s Data");
				System.out.printf(" Money: %s\n", playerData.getMoney());
				System.out.printf(" Orders Done: %s\n", playerData.getOrdersDone());
				System.out.println("==================================");
				break;

			case "ToyList":
				ToyList tempTL = playerData.getToyList();
				System.out.println(" Toy Name         | Price | Amount ");
				ToyType[] toyTypes = { ToyType.TEDDY_BEAR, ToyType.TOY_CAR, ToyType.TOY_PLANE, ToyType.RC_CAR,
						ToyType.TRAIN_SET, ToyType.TRANSFORM_ROBOT };
				for (ToyType toyType : toyTypes) {
					Toy toy = tempTL.getToy(toyType);
					System.out.printf(" %-16s | %-5d | %-5d \n", toy.getToyType().getToyName(), toy.getToyPrice(),
							toy.getToyAmount());
				}
				System.out.println("==================================");
				break;

			case "WorkerList":
				WorkerList tempWL = playerData.getWorkerList();
				System.out.println(" " + playerData.getUsername() + "'s Worker List");
				for (int workerLevel = 1; workerLevel <= WorkerList.MAX_WORKER_LEVEL; workerLevel++) {
					System.out.printf(" Level %d Worker : %d\n", workerLevel, tempWL.getWorker(workerLevel));
				}
				System.out.println("==================================");
				break;

			}
		}
	}

	public boolean playerNameAlreadyExists(String username) {
		return repository.getAllPlayerData().stream().anyMatch(data -> data.getUsername().equals(username));
	}

	public void clearScreen() {
		System.out.println("");
	}
}
