package command;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
	private List<Command> commandStack = new ArrayList<>();

	public CommandManager() {
		super();
	}

	public void executeCommand(Command command) {
		command.execute();
		commandStack.add(command);
	}

	public boolean canUndoCommand() {
		return commandStack.isEmpty();
	}

	public void undoCommand() {
		if (!canUndoCommand()) {
			return;
		}
		Command previous = commandStack.get(commandStack.size() - 1);
		previous.undo();
	}
}
