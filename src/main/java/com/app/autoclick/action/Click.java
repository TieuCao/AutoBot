package com.app.autoclick.action;

import java.awt.event.InputEvent;

public class Click extends Action {
	private ClickType type;

	public Click(ClickType type, int axisX, int axisY, int waitTime) {
		super(axisX, axisY, waitTime);
		this.type = type;
	}

	public ClickType getType() {
		return type;
	}

	public void setType(ClickType type) {
		this.type = type;
	}

	public enum ClickType {
		LEFT(InputEvent.BUTTON1_DOWN_MASK), RIGHT(InputEvent.BUTTON3_DOWN_MASK), CENTER(InputEvent.BUTTON2_DOWN_MASK);

		private int value;

		private ClickType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
