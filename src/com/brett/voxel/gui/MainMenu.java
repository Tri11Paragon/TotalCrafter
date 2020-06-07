package com.brett.voxel.gui;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
import com.brett.renderer.gui.UITextBox;
import com.brett.tools.EventQueue;
import com.brett.tools.SettingsLoader;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.networking.Client;
import com.brett.voxel.renderer.VoxelRenderer;
import com.brett.voxel.world.LevelLoader;
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
	private String seedData = "";
	public static String ip = "";
	public static String username = "";
	
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
		elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, Display.getWidth(), Display.getHeight(), Display.getWidth()/32, Display.getHeight()/32));
		elements.add(master.createUITexture(loader.loadSpecialTexture("gui/banner"), -1, -1, localWidth-640/2, 100, 640, 360/2));
		UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new SinglePlayer(), master, localWidth-200, 320, 400, 60);
		GUIText t = master.createDynamicText("Single Player", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 335, 400, true);
		UIButton bm = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new MultiPlayer(), master, localWidth-200, 390, 400, 60);
		GUIText tm = master.createDynamicText("Multi Player", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 405, 400, true);
		UIButton op = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), options, localWidth-200, 460, 400, 60);
		GUIText opt = master.createDynamicText("Options", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 475, 400, true);
		UIButton opc = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Credits(), localWidth-200,460 + 70, 400, 60);
		GUIText optc = master.createDynamicText("Credits", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 475 + 70, 400, true);
		TextMaster.loadText(t);
		TextMaster.loadText(opt);
		TextMaster.loadText(optc);
		TextMaster.loadText(tm);
		texts.add(t);
		texts.add(tm);
		texts.add(opt);
		texts.add(optc);
		elements.add(b);
		elements.add(bm);
		elements.add(op);
		elements.add(opc);
		buttons.add(b);
		buttons.add(bm);
		buttons.add(op);
		buttons.add(opc);
	}

	@Override
	public void render() {
		GL11.glClearColor(1, 1, 1, 1);
		renderer.render(elements);
		for (int i = 0; i < buttons.size(); i++)
			buttons.get(i).update();
	}

	public class Credits implements UIControl{

		@Override
		public void event(String data) {
			int width = Display.getWidth()/2;
			int height = Display.getHeight();
			for (GUIText t : texts)
				TextMaster.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, Display.getWidth(), Display.getHeight(), Display.getWidth()/32, Display.getHeight()/32));
			UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Main(), master, width-200, height-65, 400, 60);
			GUIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, width-200, height-50, 400, true);
			TextMaster.loadText(bbt);
			texts.add(bbt);
			buttons.add(b);
			elements.add(b);
			
			String text = "Main Programmer: Brett Terpstra \n"
					+ "\n"
					+ "\n"
					+ "Entity Modeler: Joey Barclay \n"
					+ "\n"
					+ "\n"
					+ "Textures: Brett / Minecraft\n"
					+ "\n"
					+ "\n"
					+ "Music: Brett Kilner / Brett Terpstra";
			
			GUIText btextmax = master.createDynamicText(text, 1.5f, VoxelScreenManager.monospaced, 0, 150, width*2, true);
			TextMaster.loadText(btextmax);
			texts.add(btextmax);
		}
		
	}
	
	public class MultiPlayer implements UIControl {

		@Override
		public void event(String data) {
			int width = Display.getWidth()/2;
			int height = Display.getHeight();
			for (GUIText t : texts)
				TextMaster.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, Display.getWidth(), Display.getHeight(), Display.getWidth()/32, Display.getHeight()/32));
			UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Main(), master, width+200, height-65, 400, 60);
			GUIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, width+200, height-50, 400, true);
			TextMaster.loadText(bbt);
			texts.add(bbt);
			buttons.add(b);
			elements.add(b);
			
			GUIText textIP = master.createDynamicText("Enter IP address below:", 1.0f, VoxelScreenManager.monospaced, width-205, height/2+65, 400, false);
			GUIText tbt = master.createDynamicText("", 1.0f, VoxelScreenManager.monospaced, width-190, height/2+120, 400, false);
			UITextBox tb = new UITextBox(loader.loadSpecialTexture("gui/slider"), new UIControl() {
				@Override
				public void event(String data) {
					ip = data;
					TextMaster.removeText(tbt);
					tbt.setText(data);
					TextMaster.loadText(tbt);
				}
			}, 35, width-200, height/2+100, 400, 60);
			tb.inputTextBuffer = ip;
			tbt.setText(tb.inputTextBuffer);
			GUIText textUsername = master.createDynamicText("Enter username below:", 1.0f, VoxelScreenManager.monospaced, width-205, height/2-135, 400, false);
			GUIText tbtuser = master.createDynamicText("", 1.0f, VoxelScreenManager.monospaced, width-190, height/2-85, 400, false);
			UITextBox tbuser = new UITextBox(loader.loadSpecialTexture("gui/slider"), new UIControl() {
				@Override
				public void event(String data) {
					username = data;
					TextMaster.removeText(tbtuser);
					tbtuser.setText(data);
					TextMaster.loadText(tbtuser);
				}
			}, 35, width-200, height/2-100, 400, 60);
			tbuser.inputTextBuffer = username;
			tbtuser.setText(username);
			TextMaster.loadText(tbt);
			TextMaster.loadText(textUsername);
			TextMaster.loadText(tbtuser);
			TextMaster.loadText(textIP);
			texts.add(tbt);
			texts.add(textIP);
			texts.add(textUsername);
			texts.add(tbtuser);
			buttons.add(tb);
			elements.add(tb);
			buttons.add(tbuser);
			elements.add(tbuser);
			
			UIButton bg = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Connect(), master, width-200, height/2+200, 400, 60);
			GUIText bbtg = master.createDynamicText("Connect", 1.5f, VoxelScreenManager.monospaced, width-200, height/2+200+15, 400, true);
			TextMaster.loadText(bbtg);
			texts.add(bbtg);
			buttons.add(bg);
			elements.add(bg);
		}
		
	}
	
	public class Connect implements UIControl{
		
		@Override
		public void event(String data) {
			VoxelWorld.isRemote = true;
			if (username.trim().isEmpty() || username.replace(" ", "").contentEquals("")) {
				int name = 0;
				for (int i = 0; i < 32; i++)
					name += new Random().nextInt(Short.MAX_VALUE);
				username = "player" + name; 
			}
			VoxelWorld.localClient = new Client(ip, username, VoxelScreenManager.world);
			while (!VoxelWorld.localClient.connected) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for (GUIText t : texts)
				TextMaster.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			VoxelScreenManager.world.init();
			VoxelScreenManager.changeDisplaySource(vrenderer);
			VoxelScreenManager.ui.addCenteredTexture(loader.loadTexture("crosshair"), -1, -1, 0, 0, 16, 16);
			EventQueue.doEvent(0);
		}
	}
	
	public class SinglePlayer implements UIControl {
		
		public SinglePlayer() {
			
		}
		
		@Override
		public void event(String d) {
			int width = Display.getWidth()/2;
			int height = Display.getHeight();
			for (GUIText t : texts)
				TextMaster.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, Display.getWidth(), Display.getHeight(), Display.getWidth()/32, Display.getHeight()/32));
			UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Main(), master, width-200, height-65, 400, 60);
			GUIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, width-200, height-50, 400, true);
			TextMaster.loadText(bbt);
			texts.add(bbt);
			buttons.add(b);
			elements.add(b);
			
			UIButton bw1 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w1/"), master, width-200, 85, 400, 60);
			UIButton bw1d = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new DeleteWorld("w1/"), master, width+230, 90, 50, 50);
			GUIText bbtw1 = master.createDynamicText("World 1 - " + folderFormat("worlds/w1"), 1.5f, VoxelScreenManager.monospaced, width-200, 100, 400, true);
			TextMaster.loadText(bbtw1);
			texts.add(bbtw1);
			buttons.add(bw1);
			elements.add(bw1);
			buttons.add(bw1d);
			elements.add(bw1d);
			
			UIButton bw2 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w2/"), master, width-200, 85 + 75, 400, 60);
			UIButton bw2d = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new DeleteWorld("w2/"), master, width+230, 85+75+5, 50, 50);
			GUIText bbtw2 = master.createDynamicText("World 2 - " + folderFormat("worlds/w2"), 1.5f, VoxelScreenManager.monospaced, width-200, 175, 400, true);
			TextMaster.loadText(bbtw2);
			texts.add(bbtw2);
			buttons.add(bw2);
			elements.add(bw2);
			buttons.add(bw2d);
			elements.add(bw2d);
			
			UIButton bw3 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w3/"), master, width-200, 235, 400, 60);
			UIButton bw3d = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new DeleteWorld("w3/"), master, width+230, 240, 50, 50);
			GUIText bbtw3 = master.createDynamicText("World 3 - " + folderFormat("worlds/w3"), 1.5f, VoxelScreenManager.monospaced, width-200, 250, 400, true);
			TextMaster.loadText(bbtw3);
			texts.add(bbtw3);
			buttons.add(bw3);
			elements.add(bw3);
			buttons.add(bw3d);
			elements.add(bw3d);
			
			UIButton bw4 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w4/"), master, width-200, 385, 400, 60);
			UIButton bw4d = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new DeleteWorld("w4/"), master, width+230, 390, 50, 50);
			GUIText bbtw4 = master.createDynamicText("World 4 - " + folderFormat("worlds/w4"), 1.5f, VoxelScreenManager.monospaced, width-200, 400, 400, true);
			TextMaster.loadText(bbtw4);
			texts.add(bbtw4);
			buttons.add(bw4);
			elements.add(bw4);
			buttons.add(bw4d);
			elements.add(bw4d);
			
			UIButton bw5 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w5/"), master, width-200, 460, 400, 60);
			UIButton bw5d = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new DeleteWorld("w5/"), master, width+230, 465, 50, 50);
			GUIText bbtw5 = master.createDynamicText("World 5 - " + folderFormat("worlds/w5"), 1.5f, VoxelScreenManager.monospaced, width-200, 475, 400, true);
			TextMaster.loadText(bbtw5);
			texts.add(bbtw5);
			buttons.add(bw5);
			elements.add(bw5);
			buttons.add(bw5d);
			elements.add(bw5d);
			
			UIButton bw6 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w6/"), master, width-200, 535, 400, 60);
			UIButton bw6d = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new DeleteWorld("w6/"), master, width+230, 540, 50, 50);
			GUIText bbtw6 = master.createDynamicText("World 6 - " + folderFormat("worlds/w6"), 1.5f, VoxelScreenManager.monospaced, width-200, 550, 400, true);
			TextMaster.loadText(bbtw6);
			texts.add(bbtw6);
			buttons.add(bw6);
			elements.add(bw6);
			buttons.add(bw6d);
			elements.add(bw6d);
		}
		
		public long folderSize(File dir) {
		    long length = 0;
		    if (!dir.exists())
		    	return length;
		    for (File file : dir.listFiles()) {
		        if (file.isFile())
		            length += file.length();
		        else
		            length += folderSize(file);
		    }
		    return length;
		}
		
		DecimalFormat fr = new DecimalFormat("#,###,###.##");
		public String folderFormat(String dir) {
			long size = folderSize(new File(dir));
			if (size < Math.pow(2, 30)) {
				return fr.format(size / (Math.pow(2, 20))) + "MB";
			} else
				return fr.format(size / (Math.pow(2, 30))) + "GB";
		}
		
	}
	
	public class DeleteWorld implements UIControl {
		private String data;
		
		public DeleteWorld( String data) {
			this.data = data;
		}

		@Override
		public void event(String data) {
			for (GUIText t : texts)
				TextMaster.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			int width = Display.getWidth()/2;
			int height = Display.getHeight();
			elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, Display.getWidth(), Display.getHeight(), Display.getWidth()/32, Display.getHeight()/32));
			UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Main(), master, width-200, height-65, 400, 60);
			GUIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, width-200, height-50, 400, true);
			TextMaster.loadText(bbt);
			texts.add(bbt);
			buttons.add(b);
			elements.add(b);
			
			GUIText areyousure = master.createDynamicText("Are you sure you want to delete " + this.data + "? (this can't be undone!)", 1.5f, VoxelScreenManager.monospaced, 
					width/2, height/2, width, true);
			TextMaster.loadText(areyousure);
			texts.add(areyousure);
			
			UIButton by = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new TheyAreSure(this.data), master, width+300, height-65, 150, 60);
			GUIText bbty = master.createDynamicText("Delete", 1.5f, VoxelScreenManager.monospaced, width+325, height-50, 100, true);
			TextMaster.loadText(bbty);
			texts.add(bbty);
			buttons.add(by);
			elements.add(by);
		}
	}
	
	public class TheyAreSure implements UIControl {
		private String data;
		
		public TheyAreSure( String data) {
			this.data = data;
		}
		
		@Override
		public void event(String data) {
			deleteWorld(new File("worlds/"+this.data));
			deleteWorldFolder(new File("worlds/"+this.data));
			new File("worlds/"+this.data).delete();
			for (GUIText t : texts)
				TextMaster.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			new SinglePlayer().event("eee");;
		}
	}
	
	private void deleteWorld(File f) {
	    if (!f.exists())
	    	return;
	    for (File file : f.listFiles()) {
	        if (file.isFile())
	            file.delete();
	        else
	        	deleteWorld(file);
	    }
	}
	
	private void deleteWorldFolder(File f) {
		if (!f.exists())
			return;
	    for (File file : f.listFiles()) {
	        if (file.isFile())
	            file.delete();
	        else {
	        	if (file.listFiles().length > 0) {
	        		deleteWorldFolder(file);
	        	} else {
	        		file.delete();
	        	}
	        }
	    }
	}
	
	public class CreateWorld implements UIControl {
		
		private VoxelRenderer renderer;
		private String data;
		
		public CreateWorld(VoxelRenderer renderer, String data) {
			this.renderer = renderer;
			this.data = data;
		}
		
		@Override
		public void event(String data) {
			for (GUIText t : texts)
				TextMaster.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			ChunkStore.worldLocation = "worlds/" + this.data;
			String stS = "";
			if (seedData.trim().length() > 0) {
				char[] stdchar = seedData.toCharArray();
				for (int i = 0; i < stdchar.length; i++) {
					stS += (int)(stdchar[i]);
				}
				String first = stS.substring(0, stS.length()/4);
				String mid = stS.substring(stS.length()/4, stS.length()/4 + stS.length()/4);
				// i wrote this at 12:30am. Give me a break
				String mid2 = stS.substring(stS.length()/4 + stS.length()/4, stS.length()/4 + stS.length()/4 + stS.length()/4);
				String last = stS.substring(stS.length()/4 + stS.length()/4 + stS.length()/4, stS.length());
				try {
					LevelLoader.seed = Long.parseLong(first);
				} catch (Exception e) {}
				try {
					LevelLoader.seed += Long.parseLong(mid);
				} catch (Exception e) {}
				try {
					LevelLoader.seed += Long.parseLong(mid2);
				} catch (Exception e) {}
				try {
					LevelLoader.seed += Long.parseLong(last);
				} catch (Exception e) {}
			} else {
				LevelLoader.seed = (long)(new Random().nextInt(Integer.MAX_VALUE)) + (long)(new Random().nextInt(Integer.MAX_VALUE));
			}
			
			VoxelScreenManager.world.init();			
			VoxelScreenManager.changeDisplaySource(renderer);
			VoxelScreenManager.ui.addCenteredTexture(loader.loadTexture("crosshair"), -1, -1, 0, 0, 16, 16);
			EventQueue.doEvent(0);
		}
		
	}
	
	public class StartWorld implements UIControl {
		
		private VoxelRenderer renderer;
		private String data;
		
		public StartWorld(VoxelRenderer renderer, String data) {
			this.renderer = renderer;
			this.data = data;
		}
		
		@Override
		public void event(String data) {
			for (GUIText t : texts)
				TextMaster.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			if (new File("worlds/"+this.data).exists()) {
				ChunkStore.worldLocation = "worlds/" + this.data;
				VoxelScreenManager.world.init();
				VoxelScreenManager.changeDisplaySource(renderer);
				VoxelScreenManager.ui.addCenteredTexture(loader.loadTexture("crosshair"), -1, -1, 0, 0, 16, 16);
				EventQueue.doEvent(0);
			} else {
				int width = Display.getWidth()/2;
				int height = Display.getHeight();
				
				elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, Display.getWidth(), Display.getHeight(), Display.getWidth()/32, Display.getHeight()/32));
				UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new SinglePlayer(), master, width-200, height-65, 400, 60);
				GUIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, width-200, height-50, 400, true);
				TextMaster.loadText(bbt);
				texts.add(bbt);
				buttons.add(b);
				elements.add(b);
				
				GUIText areyousure = master.createDynamicText("Enter seed to be used for world generation. Blank for random.", 1.5f, VoxelScreenManager.monospaced, 
						width/2, height/2 - 120, width, true);
				TextMaster.loadText(areyousure);
				texts.add(areyousure);
				
				GUIText tbt = master.createDynamicText("694", 1.5f, VoxelScreenManager.monospaced, width-190, height/2-190, 400, false);
				UITextBox tb = new UITextBox(loader.loadSpecialTexture("gui/slider"), new UIControl() {
					@Override
					public void event(String data) {
						seedData = data;
						TextMaster.removeText(tbt);
						tbt.setText(data);
						TextMaster.loadText(tbt);
					}
				}, 23, width-200, height/2-200, 400, 60);
				tb.inputTextBuffer = "694";
				TextMaster.loadText(tbt);
				texts.add(tbt);
				buttons.add(tb);
				elements.add(tb);
				
				UIButton bg = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new CreateWorld(renderer, this.data), master, width-200, height/2+200, 400, 60);
				GUIText bbtg = master.createDynamicText("Generate World", 1.5f, VoxelScreenManager.monospaced, width-200, height/2+200+15, 400, true);
				TextMaster.loadText(bbtg);
				texts.add(bbtg);
				buttons.add(bg);
				elements.add(bg);
			}
		}
		
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
			elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, Display.getWidth(), Display.getHeight(), Display.getWidth()/32, Display.getHeight()/32));
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

}
