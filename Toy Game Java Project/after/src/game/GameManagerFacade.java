package game;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import iterator.Iterator;
import iterator.PlayerDataIterator;
import model.PlayerData;
import utils.PlayerDataRepository;
import utils.Repository;

public class GameManagerFacade {
	private Repository repository;

	public GameManagerFacade() {
		this.repository = new PlayerDataRepository();
	}

	public Iterator<PlayerData> getLeaderboard() {
		return new PlayerDataIterator(repository.getAllPlayerData().stream().filter(PlayerData::isFinished)
				.sorted((a, b) -> b.getOrdersDone() - a.getOrdersDone()).collect(Collectors.toList()));
	}

	public PlayerData createNewPlayer(String username) {
		if (username.length() < 3 || username.length() > 20) {
			throw new IllegalArgumentException("Name must be between 3 to 20 characters");
		}

		if (repository.getAllPlayerData().stream().anyMatch(data -> data.getUsername().equals(username))) {
			throw new IllegalArgumentException("User with that name already exists!");
		}

		PlayerData newPlayer = new PlayerData.PlayerDataBuilder().build(username);
		repository.savePlayerData(newPlayer);
		return newPlayer;
	}

	public List<PlayerData> getUnfinishedPlayers() {
		return repository.getAllPlayerData().stream().filter(data -> !data.isFinished()).collect(Collectors.toList());
	}

	public PlayerData loadPlayer(int index, List<PlayerData> playerList) {
		if (index < 0 || index >= playerList.size()) {
			throw new IndexOutOfBoundsException("Invalid player index");
		}
		return playerList.get(index);
	}

	public void startGame(Scanner scanner, PlayerData player) {
		new Game(scanner, player);
	}
}
