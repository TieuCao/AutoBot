package com.app.autoclick.service.type;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import com.app.autoclick.action.Typing;
import com.app.autoclick.service.Command;

public class TypingCommand implements Command {
	protected Robot robot;
	protected Typing typing;

	public TypingCommand(Typing typing) throws Exception {
		this.typing = typing;
		this.robot = new Robot();

		StringSelection selection = new StringSelection(typing.getMessage());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, null);
	}

	@Override
	public void execute() throws Exception {
		robot.setAutoWaitForIdle(true);
		robot.delay(typing.getWaitTime()); // Stopping duration before executing command

		clickLeft(typing.getAxisX(), typing.getAxisY());

		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}

	protected final void clickLeft(int x, int y) {
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

}
