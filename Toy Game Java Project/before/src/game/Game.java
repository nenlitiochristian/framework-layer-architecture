package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import model.Order;
import model.PlayerData;
import model.Toy;
import model.ToyType;
import utils.PlayerDataRepository;
import utils.Repository;

public class Game {
	private Repository repository = new PlayerDataRepository();
	private List<PlayerData> previousDatas = new ArrayList<>();

	public Game(Scanner scan, PlayerData playerData) {
		startGame(scan, playerData);
	}

	public void startGame(Scanner scan, PlayerData playerData) {
		int userChoice = -1;
		boolean continueGame = true;
		do {
			clearScreen();
			repository.savePlayerData(playerData);

			if (playerData.getCurrentOrderData().getCountdown() <= 0) {
				showPlayerData(playerData, "Order");
				System.out.println("You ran out of time!");
				System.out.println("Ending game...");
				endGame(playerData);
				System.out.println("Press enter to go back...");
				try {
					System.in.read();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}

			showPlayerData(playerData, "Order", "Player");
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
				showToyList(playerData);
				break;
			case 2:
				produceToys(playerData);
				break;
			case 3:
				playerData = undoLastAction();
				break;
			case 4:
				manageWorkers(playerData, scan);
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
							endGame(playerData);
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

	private PlayerData undoLastAction() {
		if (previousDatas.isEmpty()) {
			System.out.println("Can't undo further!");
		}
		return previousDatas.remove(previousDatas.size() - 1);
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

	public static PlayerData copyPlayerData(PlayerData playerData) {
		WorkerList originalWorkers = playerData.getMyWorkers();
		WorkerList copiedWorkers = new WorkerList(originalWorkers.getWorker(1), originalWorkers.getWorker(2),
				originalWorkers.getWorker(3), originalWorkers.getWorker(4), originalWorkers.getWorker(5));

		ToyList originalToys = playerData.getMyToys();
		ToyList copiedToys = new ToyList();
		for (ToyType toyType : ToyType.values()) {
			copiedToys.addToy(toyType, originalToys.getToy(toyType).getToyAmount());
		}

		Order originalOrder = playerData.getCurrentOrderData();
		Order copiedOrder = null;
		if (originalOrder != null) {
			Toy copiedToy = new Toy(originalOrder.getToy().getToyType(), originalOrder.getToy().getToyAmount());
			copiedOrder = new Order(copiedToy, originalOrder.getLevel(), originalOrder.getCountdown());
		}

		return new PlayerData(playerData.getUsername(), playerData.getMoney(), playerData.getDifficulty(),
				playerData.getOrdersDone(), playerData.getCurrentExperience(), playerData.isFinished(), copiedWorkers,
				copiedToys, copiedOrder);
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
		// simpan dulu sebelum bikn toys dan finish order
		PlayerData copy = copyPlayerData(playerData);
		previousDatas.add(copy);

		clearScreen();
		showPlayerData(playerData, "Order");
		System.out.println(
				"Your factory has produced " + playerData.makeToys(workhours) + " toys in " + workhours + " hours");

		if (playerData.orderCanBeFinished()) {
			System.out.println("You have finished an order and received " + playerData.finishOrder() + "gold");
		}

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
			System.out.println("Bought a level 1 worker for 500 gold");
			playerData.buyWorker();
			previousDatas.add(copyPlayerData(playerData));
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
		} else if (playerData.tryUpgradingWorker(workerLevel)) {
			System.out.println("Upgraded a level " + (workerLevel) + " worker for "
					+ playerData.getWorkerList().getUpgradePrice(workerLevel) + " gold");
			previousDatas.add(copyPlayerData(playerData));
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

	private void endGame(PlayerData playerData) {
		playerData.setFinished(true);
		repository.savePlayerData(playerData);
	}

	public PlayerData loadExistingPlayer(Scanner scan) {
		clearScreen();
		System.out.println("Toy Factory Manager");

		// we can only play games that aren't finished yet
		List<PlayerData> playerDatas = repository.getAllPlayerData().stream().filter(data -> !data.isFinished())
				.collect(Collectors.toList());

		// print all available usernames
		int playerCount = 0;
		for (PlayerData data : playerDatas) {
			playerCount++;
			System.out.printf("%d. %s\n", playerCount, data.getUsername());
		}

		if (playerCount == 0) {
			System.out.println("No player found, returning in 3 seconds...");
			try {
				TimeUnit.SECONDS.sleep(3);
				return null;
			} catch (InterruptedException e) {
				System.out.println("Sleep() failed");
				return null;
			}
		}

		int userInput;
		while (true) {
			System.out.println("Pick a save file [0 to cancel]: ");
			try {
				userInput = Integer.parseInt(scan.next());
			} catch (NumberFormatException e) {
				continue;
			}
			if (userInput == 0) {
				return null;
			} else if (userInput >= 1 && userInput <= playerCount)
				return playerDatas.get(playerCount - 1);
		}
	}

	public void showPlayerData(PlayerData playerData, String... modes) {
		System.out.println(" " + playerData.getUsername() + "'s Factory");
		System.out.println("==================================");
		for (String mode : modes) {
			switch (mode) {
			case "Order":
				System.out.println(" Current Order");
				System.out.printf(" Toy Type: %s\n", playerData.getCurrentOrderData().getToy().getToyName());
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
					System.out.printf(" %-16s | %-5d | %-5d \n", toy.getToyName(), toy.getToyPrice(),
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

	public void showLeaderboard(Scanner scan) {
		// we only show finished games in the leaderboard
		List<PlayerData> playerDatas = repository.getAllPlayerData().stream().filter(data -> data.isFinished())
				.collect(Collectors.toList());

		System.out.println("Toy Factory Manager Leaderboard");
		System.out.println("==================================================");
		if (playerDatas.isEmpty()) {
			System.out.println("No data found....");
			System.out.println("Press enter to go back...");
			try {
				System.in.read();
			} catch (Exception e) {
				return;
			}
			return;
		}

		playerDatas.sort((a, b) -> a.getOrdersDone() - b.getOrdersDone());

		// print the sorted data
		System.out.printf(" %-3s | %-20s | %-11s | %-5s\n", "No.", "Username", "Orders Done", "Money");
		for (int i = 0; i < playerDatas.size(); i++) {
			System.out.printf(" %-3s | %-20s | %-11s | %-5s\n", "No.", playerDatas.get(i).getUsername(),
					playerDatas.get(i).getOrdersDone(), playerDatas.get(i).getMoney());
		}
		System.out.println("==================================================");
		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception e) {
			return;
		}
		return;
	}

	// with help from
	// https://stackoverflow.com/questions/2979383/how-to-clear-the-console/33379766#33379766
	// also with some help from my friend
	public void clearScreen() {
		// as a backup if everything fails
		// in this case just give some space to separate menus
		System.out.printf("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

		// if ANSI is supported use ANSI
		if (System.console() != null && System.getenv().get("TERM") != null) {
			System.out.print("\033[H\033[2J");
			System.out.flush();
		}
		// if ANSI is not supported try using cmd /c cls
		else {
			try {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
