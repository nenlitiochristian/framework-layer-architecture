package observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {
	protected List<Observer> observers = new ArrayList<>();

	public void subscribe(Observer o) {
		observers.add(o);
	}

	protected void notify(String message) {
		for (Observer o : observers) {
			o.receive(message);
		}
	}
}
