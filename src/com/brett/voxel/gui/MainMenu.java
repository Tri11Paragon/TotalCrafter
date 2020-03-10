package com.brett.voxel.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import com.brett.renderer.DisplaySource;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.font.GUIText;
import com.brett.renderer.font.fontRendering.TextMaster;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.UIButton;
import com.brett.renderer.gui.UIControl;
import com.brett.renderer.gui.UIElement;
import com.brett.renderer.gui.UIMaster;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.renderer.VoxelRenderer;
import com.brett.voxel.world.VoxelWorld;
import com.brett.world.cameras.Camera;

/**
*
* @author brett
* @date Mar. 2, 2020
*/

public class MainMenu implements DisplaySource {
	
	private GUIRenderer renderer;
	private List<UIElement> elements = new ArrayList<UIElement>();
	private List<UIButton> buttons = new ArrayList<UIButton>();
	private List<GUIText> texts = new ArrayList<GUIText>();
	private VoxelRenderer vrenderer;
	
	public MainMenu(UIMaster master, MasterRenderer renderer, Camera camera, VoxelWorld world, Loader loader) {
		this.vrenderer = new VoxelRenderer(renderer, camera, world);
		this.renderer = master.getRenderer();
		int localWidth = Display.getWidth()/2;
		elements.add(master.createUITexture(loader.loadTexture("icon/logo"), -1, -1, 0, 0, Display.getWidth(), Display.getHeight()));
		elements.add(master.createUITexture(loader.loadTexture("icon/banner"), -1, -1, localWidth-1000/3, 100, 1000, 288));
		UIButton b = new UIButton(loader.loadTexture("dirt"), loader.loadTexture("dirt"), new SinglePlayer(vrenderer), master, localWidth-200, 350, 400, 75);
		GUIText t = master.createDynamicText("Single Player", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 370, 400, true);
		TextMaster.loadText(t);
		master.addCenteredTexture(loader.loadTexture("crosshair"), -1, -1, 0, 0, 16, 16);
		texts.add(t);
		elements.add(b);
		buttons.add(b);
	}

	@Override
	public void render() {
		GL11.glClearColor(1, 1, 1, 1);
		renderer.render(elements);
		for (UIButton b : buttons)
			b.update();
	}
	
	public class SinglePlayer implements UIControl {

		private VoxelRenderer renderer;
		
		public SinglePlayer(VoxelRenderer renderer) {
			this.renderer = renderer;
		}
		
		@Override
		public void event(String data) {
			for (GUIText t : texts)
				TextMaster.removeText(t);
			VoxelScreenManager.changeDisplaySource(renderer);
		}
		
	}

}
