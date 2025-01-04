package main;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import game.GameManagerFacade;
import iterator.Iterator;
import model.PlayerData;

public class Main {
	private Scanner scan = new Scanner(System.in);
	private GameManagerFacade gameManager = new GameManagerFacade();

	public Main() {
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

			try {
				switch (userChoice) {
				case 1 -> startNewGame();
				case 2 -> loadGame();
				case 3 -> showLeaderboard();
				case 4 -> gameOver = true;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		scan.close();
	}

	private void startNewGame() {
		System.out.print("Input player's name [0 to go back]: ");
		String username = scan.next();
		if (username.equals("0"))
			return;

		try {
			PlayerData player = gameManager.createNewPlayer(username);
			gameManager.startGame(scan, player);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

	private void loadGame() {
		List<PlayerData> players = gameManager.getUnfinishedPlayers();
		if (players.isEmpty()) {
			System.out.println("No player found, returning in 3 seconds...");
			sleep(3);
			return;
		}

		for (int i = 0; i < players.size(); i++) {
			System.out.printf("%d. %s\n", i + 1, players.get(i).getUsername());
		}

		System.out.print("Pick a save file [0 to cancel]: ");
		int choice;
		try {
			choice = Integer.parseInt(scan.next()) - 1;
		} catch (NumberFormatException e) {
			return;
		}
		if (choice < 0)
			return;

		try {
			PlayerData player = gameManager.loadPlayer(choice, players);
			gameManager.startGame(scan, player);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void showLeaderboard() {
		Iterator<PlayerData> leaderboard = gameManager.getLeaderboard();

		int count = 0;
		System.out.println("Toy Factory Manager Leaderboard");
		System.out.println("==================================================");
		while (leaderboard.hasNext()) {
			if (count == 0) {
				System.out.printf(" %-3s | %-20s | %-11s | %-5s\n", "No.", "Username", "Orders Done", "Money");
			}
			PlayerData player = leaderboard.next();
			System.out.printf(" %-3d | %-20s | %-11d | %-5d\n", count + 1, player.getUsername(), player.getOrdersDone(),
					player.getMoney());
			count++;
		}

		if (count == 0) {
			System.out.println("No data found....");
		}
		System.out.println("==================================================");
		System.out.println("Press enter to go back...");
		try {
			System.in.read();
		} catch (Exception ignored) {
		}
	}

	private void sleep(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException ignored) {
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
