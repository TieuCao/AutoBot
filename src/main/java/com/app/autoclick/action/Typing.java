package com.app.autoclick.action;

public class Typing extends Action {
	private String message;

	public Typing(int cursorX, int cursorY, int timeWait, String message) {
		super(cursorX, cursorY, timeWait);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}
