package com.brett.engine.ui.screen;

import java.util.List;

import com.brett.engine.Loader;
import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.ui.AnchorPoint;
import com.brett.engine.ui.ButtonEvent;
import com.brett.engine.ui.UIButton;
import com.brett.engine.ui.UIElement;
import com.brett.engine.ui.UIMenu;
import com.brett.engine.ui.UITexture;
import com.brett.engine.ui.console.Console;
import com.brett.engine.ui.font.UIText;

/**
 * @author Brett
 * @date Jun. 21, 2020
 */

public class MainMenu extends Screen {

	private Screen singlePlayer;
	private Screen multiPlayer;
	
	public MainMenu() {
		UIMenu c = Console.init();
		singlePlayer = new SinglePlayer(c);
		multiPlayer = new MultiPlayer(c);
	}

	@Override
	public void onSwitch() {
		elements.add(new UITexture(Loader.l.loadTexture("gui/dirt"), -1, -1, 32, 32, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT).setAnchorPoint(AnchorPoint.FULLFIXED));
		UIText singlePlayerButtonText = new UIText("Single Player", 250, "mono", -82, 12-100, 500, 500).setAnchorPoint(AnchorPoint.CENTER);
		addText(singlePlayerButtonText);
		UIText.updateTextMesh(singlePlayerButtonText);
		elements.add(new UIButton(Loader.l.loadTexture("gui/button"), Loader.l.loadTexture("gui/buttonsel"), -150, -100, 300, 50).setEvent(new ButtonEvent() {
			@Override
			public boolean event(String data) {
				ScreenManager.switchScreen(singlePlayer);
				return true;
			}
		}).setText(singlePlayerButtonText).setAnchorPoint(AnchorPoint.CENTER));
		UIText multiPlayerButtonText = new UIText("Multi Player", 250, "mono", -82, 12-40, 500, 500).setAnchorPoint(AnchorPoint.CENTER);
		addText(multiPlayerButtonText);
		UIText.updateTextMesh(multiPlayerButtonText);
		elements.add(new UIButton(Loader.l.loadTexture("gui/button"), Loader.l.loadTexture("gui/buttonsel"), -150, -40, 300, 50).setEvent(new ButtonEvent() {
			@Override
			public boolean event(String data) {
				ScreenManager.switchScreen(multiPlayer);
				return true;
			}
		}).setText(multiPlayerButtonText).setAnchorPoint(AnchorPoint.CENTER));
		super.onSwitch();
	}

	@Override
	public List<UIElement> render() {
		return super.render();
	}

	@Override
	public void onLeave() {
		super.onLeave();
	}

}
