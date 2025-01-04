package main;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import game.Game;
import model.PlayerData;
import utils.PlayerDataRepository;
import utils.Repository;

public class Main {
	private Scanner scan = new Scanner(System.in);
	private Repository repository = new PlayerDataRepository();

	private Main() {
		boolean gameOver = false;
		while (!gameOver) {
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
				currentPlayer = makeNewPlayer();
				if (currentPlayer == null)
					break;
				new Game(scan, currentPlayer);
				break;
			case 2:
				currentPlayer = loadExistingPlayer();
				if (currentPlayer == null)
					break;
				new Game(scan, currentPlayer);
				break;
			case 3:
				showLeaderboard();
				break;
			case 4:
				gameOver = true;
				break;
			}
		}
		scan.close();
	}

	public PlayerData makeNewPlayer() {
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

	public boolean playerNameAlreadyExists(String username) {
		return repository.getAllPlayerData().stream().anyMatch(data -> data.getUsername().equals(username));
	}

	public void showLeaderboard() {
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

	public PlayerData loadExistingPlayer() {
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

	public static void main(String[] args) {
		new Main();
	}
}