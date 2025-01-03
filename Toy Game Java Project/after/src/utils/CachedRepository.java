package utils;

import java.util.List;

import model.PlayerData;

public class CachedRepository implements Repository {
	private Repository repository;
	private List<PlayerData> playerDatas = null;

	public CachedRepository(Repository repo) {
		repository = repo;
	}

	@Override
	public List<PlayerData> getAllPlayerData() {
		if (playerDatas == null) {
			playerDatas = repository.getAllPlayerData();
		}
		return playerDatas;
	}

	@Override
	public void savePlayerData(PlayerData data) {
		// karena data berubah, cachenya sudah invalid
		repository.savePlayerData(data);
		playerDatas = null;
	}

}
