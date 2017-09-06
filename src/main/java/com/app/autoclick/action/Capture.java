package com.app.autoclick.action;

public class Capture extends Action {
	protected int destX, destY;
	protected String savePath;

	public Capture(int fromX, int fromY, int destX, int destY, int timeWait, String pathSave) {
		super(fromX, fromY, timeWait);
		this.destX = destX;
		this.destY = destY;
		this.savePath = pathSave;
	}

	public int getDestX() {
		return destX;
	}

	public void setDestX(int destX) {
		this.destX = destX;
	}

	public int getDestY() {
		return destY;
	}

	public void setDestY(int destY) {
		this.destY = destY;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setPathSave(String pathSave) {
		this.savePath = pathSave;
	}

}
