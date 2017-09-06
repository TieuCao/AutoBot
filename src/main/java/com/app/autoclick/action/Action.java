package com.app.autoclick.action;

public abstract class Action {
	protected int axisX;
	protected int axisY;
	protected int waitTime;

	public Action(int axisX, int axisY, int waitTime) {
		this.axisX = axisX;
		this.axisY = axisY;
		this.waitTime = waitTime;
	}

	public int getAxisX() {
		return axisX;
	}

	public void setAxisX(int axisX) {
		this.axisX = axisX;
	}

	public int getAxisY() {
		return axisY;
	}

	public void setAxisY(int axisY) {
		this.axisY = axisY;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

}
