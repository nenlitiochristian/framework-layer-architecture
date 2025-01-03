package utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import game.ToyList;
import game.WorkerList;
import model.Order;
import model.PlayerData;
import model.PlayerData.PlayerDataBuilder;
import model.Toy;
import model.ToyType;

public class PlayerDataRepository implements Repository {
	private Database database = Database.getInstance();

	@Override
	public List<PlayerData> getAllPlayerData() {
		List<PlayerData> playerDataList = new ArrayList<>();
		String query = "SELECT * FROM users";

		try (ResultSet rs = database.execQuery(query)) {
			while (rs.next()) {
				// Use temporary variables to hold data
				String username = rs.getString("username");
				int money = rs.getInt("money");
				int ordersDone = rs.getInt("orders_done");
				int currentExperience = rs.getInt("current_experience");
				boolean isFinished = rs.getBoolean("finished");

				String orderedToyName = rs.getString("ordered_toy_name");
				int difficulty = rs.getInt("difficulty");
				int orderedToyAmount = rs.getInt("ordered_toy_amount");
				int countdown = rs.getInt("countdown");
				Toy orderedToy = new Toy(ToyType.valueOf(orderedToyName), orderedToyAmount);
				Order order = new Order(orderedToy, difficulty, countdown);

				int teddyBearsOwned = rs.getInt("teddy_bears_owned");
				int toyCarsOwned = rs.getInt("toy_cars_owned");
				int toyPlanesOwned = rs.getInt("toy_planes_owned");
				int rcCarsOwned = rs.getInt("rc_cars_owned");
				int trainSetsOwned = rs.getInt("train_sets_owned");
				int transformRobotsOwned = rs.getInt("transform_robots_owned");
				ToyList toys = new ToyList();
				toys.addToy(ToyType.TEDDY_BEAR, teddyBearsOwned);
				toys.addToy(ToyType.TOY_CAR, toyCarsOwned);
				toys.addToy(ToyType.TOY_PLANE, toyPlanesOwned);
				toys.addToy(ToyType.RC_CAR, rcCarsOwned);
				toys.addToy(ToyType.TRAIN_SET, trainSetsOwned);
				toys.addToy(ToyType.TRANSFORM_ROBOT, transformRobotsOwned);

				int levelOneWorkers = rs.getInt("level_one_workers");
				int levelTwoWorkers = rs.getInt("level_two_workers");
				int levelThreeWorkers = rs.getInt("level_three_workers");
				int levelFourWorkers = rs.getInt("level_four_workers");
				int levelFiveWorkers = rs.getInt("level_five_workers");
				WorkerList workers = new WorkerList(levelOneWorkers, levelTwoWorkers, levelThreeWorkers,
						levelFourWorkers, levelFiveWorkers);

				// ketika kita butuh bikin secara detail bisa
				// disebut satu per satu sesuai kebutuhan
				PlayerDataBuilder builder = new PlayerDataBuilder();
				PlayerData data = builder.money(money).ordersDone(ordersDone).currentExperience(currentExperience)
						.isFinished(isFinished).currentOrder(order).myToys(toys).myWorkers(workers).build(username);
				playerDataList.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return playerDataList;
	}

	@Override
	public void savePlayerData(PlayerData data) {
		String query = "INSERT INTO users (username, money, difficulty, orders_done, current_experience, ordered_toy_name, "
				+ "ordered_toy_amount, countdown, teddy_bears_owned, toy_cars_owned, toy_planes_owned, rc_cars_owned, "
				+ "train_sets_owned, transform_robots_owned, level_one_workers, level_two_workers, level_three_workers, level_four_workers, level_five_workers) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE money = ?, difficulty = ?, orders_done = ?, current_experience = ?, ordered_toy_name = ?, "
				+ "ordered_toy_amount = ?, countdown = ?, teddy_bears_owned = ?, toy_cars_owned = ?, toy_planes_owned = ?, "
				+ "rc_cars_owned = ?, train_sets_owned = ?, transform_robots_owned = ?, level_one_workers = ?, level_two_workers = ?, "
				+ "level_three_workers = ?, level_four_workers = ?, level_five_workers = ?, finished = ?";

		try (PreparedStatement ps = database.addQuery(query)) {
			// Extracting data from composed classes
			String username = data.getUsername();
			int money = data.getMoney();
			int difficulty = data.getDifficulty();
			int ordersDone = data.getOrdersDone();
			int currentExperience = data.getCurrentExperience();

			Order order = data.getOrder();
			Toy orderedToy = order.getToy();
			String orderedToyName = orderedToy.getToyType().toString();
			int orderedToyAmount = orderedToy.getToyAmount();
			int countdown = order.getCountdown();

			ToyList toys = data.getToyList();
			int teddyBearsOwned = toys.getToy(ToyType.TEDDY_BEAR).getToyAmount();
			int toyCarsOwned = toys.getToy(ToyType.TOY_CAR).getToyAmount();
			int toyPlanesOwned = toys.getToy(ToyType.TOY_PLANE).getToyAmount();
			int rcCarsOwned = toys.getToy(ToyType.RC_CAR).getToyAmount();
			int trainSetsOwned = toys.getToy(ToyType.TRAIN_SET).getToyAmount();
			int transformRobotsOwned = toys.getToy(ToyType.TRANSFORM_ROBOT).getToyAmount();

			WorkerList workers = data.getWorkerList();
			int levelOneWorkers = workers.getWorker(1);
			int levelTwoWorkers = workers.getWorker(2);
			int levelThreeWorkers = workers.getWorker(3);
			int levelFourWorkers = workers.getWorker(4);
			int levelFiveWorkers = workers.getWorker(5);

			boolean finished = data.isFinished();

			// Insert parameters
			ps.setString(1, username);
			ps.setInt(2, money);
			ps.setInt(3, difficulty);
			ps.setInt(4, ordersDone);
			ps.setInt(5, currentExperience);
			ps.setString(6, orderedToyName);
			ps.setInt(7, orderedToyAmount);
			ps.setInt(8, countdown);
			ps.setInt(9, teddyBearsOwned);
			ps.setInt(10, toyCarsOwned);
			ps.setInt(11, toyPlanesOwned);
			ps.setInt(12, rcCarsOwned);
			ps.setInt(13, trainSetsOwned);
			ps.setInt(14, transformRobotsOwned);
			ps.setInt(15, levelOneWorkers);
			ps.setInt(16, levelTwoWorkers);
			ps.setInt(17, levelThreeWorkers);
			ps.setInt(18, levelFourWorkers);
			ps.setInt(19, levelFiveWorkers);

			// Duplicate Key Update parameters
			ps.setInt(20, money);
			ps.setInt(21, difficulty);
			ps.setInt(22, ordersDone);
			ps.setInt(23, currentExperience);
			ps.setString(24, orderedToyName);
			ps.setInt(25, orderedToyAmount);
			ps.setInt(26, countdown);
			ps.setInt(27, teddyBearsOwned);
			ps.setInt(28, toyCarsOwned);
			ps.setInt(29, toyPlanesOwned);
			ps.setInt(30, rcCarsOwned);
			ps.setInt(31, trainSetsOwned);
			ps.setInt(32, transformRobotsOwned);
			ps.setInt(33, levelOneWorkers);
			ps.setInt(34, levelTwoWorkers);
			ps.setInt(35, levelThreeWorkers);
			ps.setInt(36, levelFourWorkers);
			ps.setInt(37, levelFiveWorkers);
			ps.setBoolean(38, finished);

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
