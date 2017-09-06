package com.app.autoclick.service.click;

import java.awt.Robot;

import com.app.autoclick.action.Click;
import com.app.autoclick.service.Command;

public class ClickCommand implements Command {
	protected Robot robot;
	protected Click click;

	public ClickCommand(Click click) throws Exception {
		this.robot = new Robot();
		this.click = click;
	}

	protected void click(int x, int y, int buttonType, int timeWait) {
		robot.waitForIdle();
		robot.delay(timeWait);
		robot.mouseMove(x, y);
		robot.mousePress(buttonType); // depend on button of mouse is pressed
		robot.mouseRelease(buttonType); // depend on button of mouse is released
	}

	@Override
	public void execute() throws Exception {
		click(click.getAxisX(), click.getAxisY(), click.getType().getValue(), click.getWaitTime());
	}
}
