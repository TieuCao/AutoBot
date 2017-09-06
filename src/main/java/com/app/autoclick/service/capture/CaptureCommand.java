package com.app.autoclick.service.capture;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import com.app.autoclick.action.Capture;
import com.app.autoclick.service.Command;

public class CaptureCommand implements Command {
	protected Robot robot;
	protected Capture capture;
	private boolean appendTimestamp;

	public CaptureCommand(Capture capture) throws Exception {
		this.appendTimestamp = true;
		this.capture = capture;
		this.robot = new Robot();
	}

	public boolean isAppendTimestamp() {
		return appendTimestamp;
	}

	public void setAppendTimestamp(boolean appendTimestamp) {
		this.appendTimestamp = appendTimestamp;
	}

	public void execute() throws IOException {
		robot.setAutoWaitForIdle(true);
		robot.delay(capture.getWaitTime());

		int x = capture.getAxisX();
		int y = capture.getAxisY();
		int dx = capture.getDestX();
		int dy = capture.getDestY();
		String savePath = capture.getSavePath();
		String[] tokens = savePath.split("[.]");
		String imageType = tokens[tokens.length - 1];

		switch (imageType.toLowerCase()) {
			case "png":
				imageType = "png";
				break;
			case "jpg":
				imageType = "jpg";
				break;
			case "jpeg":
				imageType = "jpeg";
				break;
			case "gif":
				imageType = "gif";
				break;
			default:
				imageType = "png";
				break;
		}

		BufferedImage imgBuffer = robot.createScreenCapture(new Rectangle(x, y, dx - x, dy - y));

		if (!appendTimestamp)
			ImageIO.write(imgBuffer, imageType, new File(savePath));
		else
			ImageIO.write(imgBuffer, imageType, new File(addTimestampToFileName(savePath, imageType)));
	}

	private String addTimestampToFileName(String savePath, String imageType) {
		int index = savePath.toLowerCase().lastIndexOf("." + imageType);
		if (index == -1)
			return String.format("%s_%d.%s", savePath, new Date().getTime(), imageType);
		return String.format("%s_%d.%s", savePath.substring(0, index), new Date().getTime(), imageType);
	}
}
