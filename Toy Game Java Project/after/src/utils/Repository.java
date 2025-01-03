package utils;

import java.util.List;

import model.PlayerData;

public interface Repository {
	public List<PlayerData> getAllPlayerData();

	public void savePlayerData(PlayerData data);
}
