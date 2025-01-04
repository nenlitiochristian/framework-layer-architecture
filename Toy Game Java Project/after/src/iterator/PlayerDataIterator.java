package iterator;

import java.util.List;

import model.PlayerData;

public class PlayerDataIterator implements Iterator<PlayerData> {
	private final List<PlayerData> playerDataList;
	private int position = 0;

	public PlayerDataIterator(List<PlayerData> playerDataList) {
		this.playerDataList = playerDataList;
	}

	@Override
	public boolean hasNext() {
		return position < playerDataList.size();
	}

	@Override
	public PlayerData next() {
		if (!hasNext()) {
			throw new IndexOutOfBoundsException("No more elements in the iterator");
		}
		return playerDataList.get(position++);
	}
}
