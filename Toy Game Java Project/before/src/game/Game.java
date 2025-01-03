package game;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import model.PlayerData;
import model.Toy;
import model.ToyType;
import utils.PlayerDataRepository;
import utils.Repository;

public class Game {
	private Repository repository = new PlayerDataRepository();

	public Game() {
		Scanner scan = new Scanner(System.in);
		boolean gameOver = false;
		while (!gameOver) {
			clearScreen();
			System.out.println("Toy Factory Manager");
			System.out.println("1. New Game");
			System.out.println("2. Load Game");
			System.out.println("3. Highscore");
			System.out.println("4. Exit");
			System.out.print(">> ");

			int userChoice = -1;
			try {
				userChoice = Integer.parseInt(scan.next());
			} catch (NumberFormatException e) {
				continue;
			}

			PlayerData currentPlayer = null;
			switch (userChoice) {
			case 1:
				currentPlayer = makeNewPlayer(scan);
				if (currentPlayer == null)
					break;
				startGame(scan, currentPlayer);
				break;
			case 2:
				currentPlayer = loadExistingPlayer(scan);
				if (currentPlayer == null)
					break;
				startGame(scan, currentPlayer);
				break;
			case 3:
				showLeaderboard(scan);
				break;
			case 4:
				gameOver = true;
				break;
			}
		}
		scan.close();
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
			System.out.println("3. Manage Workers");
			System.out.println("4. Exit Game (Your progress is always saved automatically)");
			System.out.println("5. End Game");
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
				manageWorkers(playerData, scan);
				break;
			case 4:
				continueGame = false;
				break;

			case 5:
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

	public PlayerData makeNewPlayer(Scanner scan) {
		clearScreen();
		System.out.println("Toy Factory Manager");
		while (true) {
			System.out.print("Input player's name [0 to go back]: ");

			String userInput = scan.next();
			if (userInput.charAt(0) == '0' && userInput.length() == 1) {
				return null;
			} else if (userInput.length() < 3 || userInput.length() > 20) {
				System.out.println("Name must be between 3 to 20 characters");
				continue;
			} else if (playerNameAlreadyExists(userInput)) {
				System.out.println("User with that name already exists!");
				continue;
			}

			PlayerData newPlayer = new PlayerData(userInput);
			repository.savePlayerData(newPlayer);
			return newPlayer;
		}
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

	public boolean playerNameAlreadyExists(String username) {
		return repository.getAllPlayerData().stream().anyMatch(data -> data.getUsername().equals(username));
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
