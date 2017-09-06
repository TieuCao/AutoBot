package com.app.autoclick.service;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
	private volatile boolean isStop;
	private List<Command> lstCmd;
	private static CommandManager unique;

	private CommandManager() {
		lstCmd = new ArrayList<>();
	};

	public static synchronized CommandManager getInstance() {
		if (unique == null)
			unique = new CommandManager();
		return unique;
	}

	public List<Command> getLstCmd() {
		return lstCmd;
	}

	public void setLstCmd(List<Command> lstCmd) {
		this.lstCmd = lstCmd;
	}

	public void addCommand(Command cmd) {
		lstCmd.add(cmd);
	}

	public void addCommand(List<Command> lstCmd) {
		this.lstCmd.addAll(lstCmd);
	}

	/**
	 * specify task in list is execute
	 */
	public void execute(int pos) throws Exception {
		if (pos < 0 || pos >= lstCmd.size())
			return;
		lstCmd.get(pos).execute();
	}

	/**
	 * run one time all task
	 * 
	 * @throws Exception
	 */
	public void executeAllOnlyOnceTime() throws Exception {
		for (Command cmd : lstCmd) {
			cmd.execute();
		}
	}

	/**
	 * loop all task with specific time interval. Unit is millisecond
	 */
	public void executeAll(int timeInterval) throws Exception {
		executeAll(0, timeInterval);
	}

	/**
	 * Determine time wait that before executing and loop all task with specific time interval. Unit is millisecond
	 */
	public void executeAll(int timeWait, int timeInterval) throws Exception {
		Thread.sleep(timeWait);
		isStop = false;
		L1: while (!isStop) {
			for (Command cmd : lstCmd) {
				if (isStop)
					break L1;
				cmd.execute();
			}
			Thread.sleep(timeInterval);
		}
	}

	/**
	 * stop all task
	 */
	public void stopAll() {
		isStop = true;
	}

	/**
	 * remove all task
	 */
	public void clearAll() {
		lstCmd.clear();
	}

}
