package com.brett.voxel.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import com.brett.renderer.DisplaySource;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.font.GUIDynamicText;
import com.brett.renderer.font.GUIText;
import com.brett.renderer.font.fontRendering.TextMaster;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.UIButton;
import com.brett.renderer.gui.UIControl;
import com.brett.renderer.gui.UIElement;
import com.brett.renderer.gui.UIMaster;
import com.brett.renderer.gui.UISlider;
import com.brett.tools.EventQueue;
import com.brett.tools.SettingsLoader;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.renderer.VoxelRenderer;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.chunk.ChunkStore;
import com.brett.world.cameras.Camera;

/**
*
* @author brett
* @date Mar. 2, 2020
*/

public class MainMenu implements DisplaySource {
	
	protected static MainMenu menu;
	protected static Options options;
	
	private GUIRenderer renderer;
	private List<UIElement> elements =  Collections.synchronizedList(new ArrayList<UIElement>());
	private List<UIButton> buttons = Collections.synchronizedList(new ArrayList<UIButton>());
	private List<GUIText> texts = Collections.synchronizedList(new ArrayList<GUIText>());
	private VoxelRenderer vrenderer;
	private UIMaster master;
	private Loader loader;
	
	public MainMenu(UIMaster master, MasterRenderer renderer, Camera camera, VoxelWorld world, Loader loader) {
		MainMenu.menu = this;
		this.vrenderer = new VoxelRenderer(renderer, camera, world);
		this.renderer = master.getRenderer();
		this.master = master;
		this.loader = loader;
		options = new Options(master, loader);
		init();
	}
	
	public void init() {
		int localWidth = Display.getWidth()/2;
		elements.add(master.createUITexture(loader.loadSpecialTexture("dirt"), -1, -1, 0, 0, Display.getWidth(), Display.getHeight(), Display.getWidth()/32, Display.getHeight()/32));
		elements.add(master.createUITexture(loader.loadSpecialTexture("gui/banner"), -1, -1, localWidth-640/2, 100, 640, 360/2));
		UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new SinglePlayer(vrenderer), master, localWidth-200, 320, 400, 60);
		GUIText t = master.createDynamicText("Single Player", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 335, 400, true);
		UIButton op = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), options, localWidth-200, 390, 400, 60);
		GUIText opt = master.createDynamicText("Options", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 405, 400, true);
		TextMaster.loadText(t);
		TextMaster.loadText(opt);
		texts.add(t);
		texts.add(opt);
		elements.add(b);
		elements.add(op);
		buttons.add(b);
		buttons.add(op);
	}

	@Override
	public void render() {
		GL11.glClearColor(1, 1, 1, 1);
		renderer.render(elements);
		for (int i = 0; i < buttons.size(); i++)
			buttons.get(i).update();
	}
	
	public class Options implements UIControl,DisplaySource {
		
		private UIMaster master;
		private Loader loader;
		private GUIDynamicText senstiv;
		private GUIDynamicText rendd;
		private GUIDynamicText fovv;
		
		public Options(UIMaster master, Loader loader) {
			this.master = master;
			this.loader = loader;
		}
		
		public void init() {
			int localWidth = Display.getWidth()/2;
			int height = Display.getHeight();
			elements.add(master.createUITexture(loader.loadSpecialTexture("dirt"), -1, -1, 0, 0, Display.getWidth(), Display.getHeight(), Display.getWidth()/32, Display.getHeight()/32));
			UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Main(), master, localWidth-200, height-65, 400, 60);
			GUIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, localWidth-200, height-50, 400, true);
			TextMaster.loadText(bbt);
			texts.add(bbt);
			buttons.add(b);
			elements.add(b);
			
			UISlider senstivSlid = new UISlider("sensitivity", loader.loadSpecialTexture("gui/slider"), loader.loadSpecialTexture("gui/button"), options, master, 120, 120, 300, 60);
			senstivSlid.setPercent(SettingsLoader.SENSITIVITY);
			senstiv = master.createDynamicText("Sensitivity: " + Math.round(senstivSlid.getPercent()*100) + "%", 1.5f, VoxelScreenManager.monospaced, 120, 135, 300, true);
			TextMaster.loadText(senstiv);
			texts.add(senstiv);
			buttons.add(senstivSlid);
			elements.add(senstivSlid);
			
			UISlider renddSlid = new UISlider("rend", loader.loadSpecialTexture("gui/slider"), loader.loadSpecialTexture("gui/button"), options, master, 440, 120, 400, 60);
			renddSlid.setPercent(ChunkStore.renderDistance/20d);
			rendd = master.createDynamicText("Render Distance: " + ChunkStore.renderDistance, 1.5f, VoxelScreenManager.monospaced, (440), 135, 400, true);
			TextMaster.loadText(rendd);
			texts.add(rendd);
			buttons.add(renddSlid);
			elements.add(renddSlid);
			
			UISlider fovSlid = new UISlider("fov", loader.loadSpecialTexture("gui/slider"), loader.loadSpecialTexture("gui/button"), options, master, 860, 120, 300, 60);
			fovSlid.setPercent((MasterRenderer.FOV-60)/122);
			fovv = master.createDynamicText("FOV: " + MasterRenderer.FOV, 1.5f, VoxelScreenManager.monospaced, (860), 135, 300, true);
			TextMaster.loadText(fovv);
			texts.add(fovv);
			buttons.add(fovSlid);
			elements.add(fovSlid);
			
		}
		
		@Override
		public void event(String data) {
			if (data == null) {
				for (GUIText t : texts)
					TextMaster.removeText(t);
				texts.clear();
				elements.clear();
				buttons.clear();
				this.init();
				VoxelScreenManager.changeDisplaySource(this);
				EventQueue.doEvent(1);
			} else {
				String[] datas = data.split(":");
				double percent = Double.parseDouble(datas[1]);
				if (datas[0].contentEquals("sensitivity")) {
					TextMaster.removeText(senstiv);
					senstiv.changeTextNoUpdate("Sensitivity: " + (Math.round(percent*100)) + "%");
					TextMaster.loadText(senstiv);
					SettingsLoader.SENSITIVITY = percent;
				}
				if (datas[0].contentEquals("rend")) {
					ChunkStore.renderDistance = (int) Math.round((20 * percent) + 1);
					TextMaster.removeText(rendd);
					rendd.changeTextNoUpdate("Render Distance: " + ChunkStore.renderDistance);
					TextMaster.loadText(rendd);
				}
				if (datas[0].contentEquals("fov")) {
					MasterRenderer.FOV = (int) Math.round((62 * percent) + 60);
					TextMaster.removeText(fovv);
					fovv.changeTextNoUpdate("FOV: " + MasterRenderer.FOV);
					TextMaster.loadText(fovv);
				}
			}
		}

		@Override
		public void render() {
			GL11.glClearColor(1, 1, 1, 1);
			renderer.render(elements);
			for (int i = 0; i < buttons.size(); i++)
				buttons.get(i).update();
		}
		
	}
	
	public class Main implements UIControl {

		@Override
		public void event(String data) {
			for (GUIText t : texts)
				TextMaster.removeText(t);
			texts.clear();
			elements.clear();
			buttons.clear();
			menu.init();
			VoxelScreenManager.changeDisplaySource(menu);
		}
		
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
			elements.clear();
			buttons.clear();
			texts.clear();
			VoxelScreenManager.changeDisplaySource(renderer);
			VoxelScreenManager.ui.addCenteredTexture(loader.loadTexture("crosshair"), -1, -1, 0, 0, 16, 16);
			EventQueue.doEvent(0);
		}
		
	}

}
