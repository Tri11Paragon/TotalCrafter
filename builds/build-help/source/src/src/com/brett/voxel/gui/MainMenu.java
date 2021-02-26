package com.brett.voxel.gui;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.brett.DisplayManager;
import com.brett.cameras.Camera;
import com.brett.renderer.DisplaySource;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.font.UIDynamicText;
import com.brett.renderer.font.UIText;
import com.brett.renderer.font.fontRendering.StaticText;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.UIButton;
import com.brett.renderer.gui.UIControl;
import com.brett.renderer.gui.UIElement;
import com.brett.renderer.gui.UIMaster;
import com.brett.renderer.gui.UISlider;
import com.brett.renderer.gui.UITextBox;
import com.brett.sound.MusicMaster;
import com.brett.tools.EventQueue;
import com.brett.tools.SettingsLoader;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.networking.Client;
import com.brett.voxel.renderer.VoxelRenderer;
import com.brett.voxel.tools.LevelLoader;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.chunk.ChunkStore;

/**
*
* @author brett
* @date Mar. 2, 2020
*/

public class MainMenu implements DisplaySource {
	
	protected static MainMenu menu;
	protected static Options options;
	
	public static String ip = "";
	public static String username = "";
	public static boolean ingame = false;
	
	private VoxelRenderer vrenderer;
	private UIMaster master;
	private Loader loader;
	private GUIRenderer renderer;
	
	private List<UIElement> elements =  Collections.synchronizedList(new ArrayList<UIElement>());
	private List<UIButton> buttons = Collections.synchronizedList(new ArrayList<UIButton>());
	private List<UIText> texts = Collections.synchronizedList(new ArrayList<UIText>());
	
	private String seedData = "";
	
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
		// create and add the main menu
		int localWidth = DisplayManager.WIDTH/2;
		// add the background dirt
		elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, DisplayManager.WIDTH/32, DisplayManager.HEIGHT/32));
		// add the banner image
		elements.add(master.createUITexture(loader.loadSpecialTexture("gui/banner"), -1, -1, localWidth-640/2, 100, 640, 360/2));
		// add all the buttons and their text.
		UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new SinglePlayer(), master, localWidth-200, 320, 400, 60);
		UIText t = master.createDynamicText("Singleplayer", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 335, 400, true);
		UIButton bm = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new MultiPlayer(), master, localWidth-200, 390, 400, 60);
		UIText tm = master.createDynamicText("Multiplayer", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 405, 400, true);
		UIButton op = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), options, localWidth-200, 460, 400, 60);
		UIText opt = master.createDynamicText("Options", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 475, 400, true);
		UIButton opc = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Credits(), localWidth-200,460 + 70, 400, 60);
		UIText optc = master.createDynamicText("Credits", 1.5f, VoxelScreenManager.monospaced, localWidth-200, 475 + 70, 400, true);
		StaticText.loadText(t);
		StaticText.loadText(opt);
		StaticText.loadText(optc);
		StaticText.loadText(tm);
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
		// clear the screen and render elements.
		GL11.glClearColor(1, 1, 1, 1);
		renderer.render(elements);
		for (int i = 0; i < buttons.size(); i++)
			buttons.get(i).update();
	}

	public class Credits implements UIControl{

		@Override
		public void event(String data) {
			// clear the screen
			int width = DisplayManager.WIDTH/2;
			int height = DisplayManager.HEIGHT;
			for (UIText t : texts)
				StaticText.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			// add in the dirt texture
			elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, DisplayManager.WIDTH/32, DisplayManager.HEIGHT/32));
			// add in back button
			UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Main(), master, width-200, height-65, 400, 60);
			UIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, width-200, height-50, 400, true);
			StaticText.loadText(bbt);
			texts.add(bbt);
			buttons.add(b);
			elements.add(b);
			// credits text
			String text = "Main Programmer: Brett Terpstra \n"
					+ "\n"
					+ "Entity Modeler: Joey Barclay \n"
					+ "\n"
					+ "Textures: Brett / Minecraft\n"
					+ "\n"
					+ "Music: Brett Kilner / Brett Terpstra\n"
					+ "\n"
					+ "Annoying Guy: Daniel D.\n"
					+ "\n"
					+ "Idea Man(Sometimes): Josiah McMillian\n"
					+ "\n"
					+ "Helpful Internet Guy: Karl";
			
			UIText btextmax = master.createDynamicText(text, 1.5f, VoxelScreenManager.monospaced, 0, 150, width*2, true);
			StaticText.loadText(btextmax);
			texts.add(btextmax);
		}
		
	}
	
	public class MultiPlayer implements UIControl {

		@Override
		public void event(String data) {
			// clear the screen
			int width = DisplayManager.WIDTH/2;
			int height = DisplayManager.HEIGHT;
			for (UIText t : texts)
				StaticText.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			// add dirt texture
			elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, DisplayManager.WIDTH/32, DisplayManager.HEIGHT/32));
			// back button
			UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Main(), master, width+200, height-65, 400, 60);
			UIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, width+200, height-50, 400, true);
			StaticText.loadText(bbt);
			texts.add(bbt);
			buttons.add(b);
			elements.add(b);
			
			// multi player warning
			UIText warning = master.createDynamicText("!! Multiplayer is in very early alpha, and as a result may be buggy \n "
					+ "or not have features available implimented yet. !!", 1.0f, VoxelScreenManager.monospaced, 0, 50, width*2, true);
			texts.add(warning);
			StaticText.loadText(warning);
			
			// ip input
			UIText textIP = master.createDynamicText("Enter IP address below:", 1.0f, VoxelScreenManager.monospaced, width-205, height/2+65, 400, false);
			UIText tbt = master.createDynamicText("", 1.0f, VoxelScreenManager.monospaced, width-190, height/2+120, 400, false);
			UITextBox tb = new UITextBox(loader.loadSpecialTexture("gui/slider"), new UIControl() {
				@Override
				public void event(String data) {
					// set the ip and change the text
					ip = data;
					StaticText.removeText(tbt);
					tbt.setText(data);
					StaticText.loadText(tbt);
				}
			}, 35, width-200, height/2+100, 400, 60);
			tb.inputTextBuffer = ip;
			tbt.setText(tb.inputTextBuffer);
			// username input
			UIText textUsername = master.createDynamicText("Enter username below:", 1.0f, VoxelScreenManager.monospaced, width-205, height/2-135, 400, false);
			UIText tbtuser = master.createDynamicText("", 1.0f, VoxelScreenManager.monospaced, width-190, height/2-85, 400, false);
			UITextBox tbuser = new UITextBox(loader.loadSpecialTexture("gui/slider"), new UIControl() {
				@Override
				public void event(String data) {
					username = data;
					StaticText.removeText(tbtuser);
					tbtuser.setText(data);
					StaticText.loadText(tbtuser);
				}
			}, 35, width-200, height/2-100, 400, 60);
			tbuser.inputTextBuffer = username;
			tbtuser.setText(username);
			StaticText.loadText(tbt);
			StaticText.loadText(textUsername);
			StaticText.loadText(tbtuser);
			StaticText.loadText(textIP);
			texts.add(tbt);
			texts.add(textIP);
			texts.add(textUsername);
			texts.add(tbtuser);
			buttons.add(tb);
			elements.add(tb);
			buttons.add(tbuser);
			elements.add(tbuser);
			
			// connect button
			UIButton bg = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Connect(), master, width-200, height/2+200, 400, 60);
			UIText bbtg = master.createDynamicText("Connect", 1.5f, VoxelScreenManager.monospaced, width-200, height/2+200+15, 400, true);
			StaticText.loadText(bbtg);
			texts.add(bbtg);
			buttons.add(bg);
			elements.add(bg);
		}
		
	}
	
	public class Connect implements UIControl{
		
		@Override
		public void event(String data) {
			// connects to the server
			VoxelWorld.isRemote = true;
			// make sure the player has a name
			if (username.trim().isEmpty() || username.replace(" ", "").contentEquals("")) {
				int name = 0;
				for (int i = 0; i < 32; i++)
					name += new Random().nextInt(Short.MAX_VALUE);
				username = "player" + name; 
			}
			// connect
			VoxelWorld.localClient = new Client(ip, username, VoxelScreenManager.world);
			// don't show anything until it has connected.
			while (!VoxelWorld.localClient.connected) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// change to the game.
			for (UIText t : texts)
				StaticText.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			ingame = true;
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
			// clear screen
			int width = DisplayManager.WIDTH/2;
			int height = DisplayManager.HEIGHT;
			for (UIText t : texts)
				StaticText.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			// dirt and back buttons.
			elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, DisplayManager.WIDTH/32, DisplayManager.HEIGHT/32));
			UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Main(), master, width-200, height-65, 400, 60);
			UIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, width-200, height-50, 400, true);
			StaticText.loadText(bbt);
			texts.add(bbt);
			buttons.add(b);
			elements.add(b);
			
			// world buttons
			// 6 worlds allowed.
			UIButton bw1 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w1/"), master, width-200, 85, 400, 60);
			UIButton bw1d = new UIButton(loader.loadSpecialTexture("gui/garbage"), loader.loadSpecialTexture("gui/garbagesel"), new DeleteWorld("w1/"), master, width+230, 90, 50, 50);
			UIText bbtw1 = master.createDynamicText("World 1 - " + folderFormat("worlds/w1"), 1.5f, VoxelScreenManager.monospaced, width-200, 100, 400, true);
			StaticText.loadText(bbtw1);
			texts.add(bbtw1);
			buttons.add(bw1);
			elements.add(bw1);
			buttons.add(bw1d);
			elements.add(bw1d);
			
			UIButton bw2 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w2/"), master, width-200, 85 + 75, 400, 60);
			UIButton bw2d = new UIButton(loader.loadSpecialTexture("gui/garbage"), loader.loadSpecialTexture("gui/garbagesel"), new DeleteWorld("w2/"), master, width+230, 85+75+5, 50, 50);
			UIText bbtw2 = master.createDynamicText("World 2 - " + folderFormat("worlds/w2"), 1.5f, VoxelScreenManager.monospaced, width-200, 175, 400, true);
			StaticText.loadText(bbtw2);
			texts.add(bbtw2);
			buttons.add(bw2);
			elements.add(bw2);
			buttons.add(bw2d);
			elements.add(bw2d);
			
			UIButton bw3 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w3/"), master, width-200, 235, 400, 60);
			UIButton bw3d = new UIButton(loader.loadSpecialTexture("gui/garbage"), loader.loadSpecialTexture("gui/garbagesel"), new DeleteWorld("w3/"), master, width+230, 240, 50, 50);
			UIText bbtw3 = master.createDynamicText("World 3 - " + folderFormat("worlds/w3"), 1.5f, VoxelScreenManager.monospaced, width-200, 250, 400, true);
			StaticText.loadText(bbtw3);
			texts.add(bbtw3);
			buttons.add(bw3);
			elements.add(bw3);
			buttons.add(bw3d);
			elements.add(bw3d);
			
			UIButton bw4 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w4/"), master, width-200, 385, 400, 60);
			UIButton bw4d = new UIButton(loader.loadSpecialTexture("gui/garbage"), loader.loadSpecialTexture("gui/garbagesel"), new DeleteWorld("w4/"), master, width+230, 390, 50, 50);
			UIText bbtw4 = master.createDynamicText("World 4 - " + folderFormat("worlds/w4"), 1.5f, VoxelScreenManager.monospaced, width-200, 400, 400, true);
			StaticText.loadText(bbtw4);
			texts.add(bbtw4);
			buttons.add(bw4);
			elements.add(bw4);
			buttons.add(bw4d);
			elements.add(bw4d);
			
			UIButton bw5 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w5/"), master, width-200, 460, 400, 60);
			UIButton bw5d = new UIButton(loader.loadSpecialTexture("gui/garbage"), loader.loadSpecialTexture("gui/garbagesel"), new DeleteWorld("w5/"), master, width+230, 465, 50, 50);
			UIText bbtw5 = master.createDynamicText("World 5 - " + folderFormat("worlds/w5"), 1.5f, VoxelScreenManager.monospaced, width-200, 475, 400, true);
			StaticText.loadText(bbtw5);
			texts.add(bbtw5);
			buttons.add(bw5);
			elements.add(bw5);
			buttons.add(bw5d);
			elements.add(bw5d);
			
			// this was causing issues and I don't have time to fix it
			// this is friday today/
			/*UIButton bw6 = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new StartWorld(vrenderer, "w6/"), master, width-200, 535, 400, 60);
			UIButton bw6d = new UIButton(loader.loadSpecialTexture("gui/garbage"), loader.loadSpecialTexture("gui/garbagesel"), new DeleteWorld("w6/"), master, width+230, 540, 50, 50);
			UIText bbtw6 = master.createDynamicText("World 6 - " + folderFormat("worlds/w6"), 1.5f, VoxelScreenManager.monospaced, width-200, 550, 400, true);
			StaticText.loadText(bbtw6);
			texts.add(bbtw6);
			buttons.add(bw6);
			elements.add(bw6);
			buttons.add(bw6d);
			elements.add(bw6d);*/
		}
		
		/**
		 * returns the size of a folder.
		 */
		public long folderSize(File dir) {
		    long length = 0;
		    if (!dir.exists())
		    	return length;
		    // recursively add sizes
		    for (File file : dir.listFiles()) {
		        if (file.isFile())
		            length += file.length();
		        else
		            length += folderSize(file);
		    }
		    return length;
		}
		
		DecimalFormat fr = new DecimalFormat("#,###,###.##");
		/**
		 * formats the folder size correctly for text.
		 */
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
			// clear the screen
			for (UIText t : texts)
				StaticText.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			int width = DisplayManager.WIDTH/2;
			int height = DisplayManager.HEIGHT;
			// add the dirt texture
			elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, DisplayManager.WIDTH/32, DisplayManager.HEIGHT/32));
			// back button
			UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Main(), master, width-200, height-65, 400, 60);
			UIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, width-200, height-50, 400, true);
			StaticText.loadText(bbt);
			texts.add(bbt);
			buttons.add(b);
			elements.add(b);
			
			// make sure they want to delete the world
			UIText areyousure = master.createDynamicText("Are you sure you want to delete " + this.data + "? (this can't be undone!)", 1.5f, VoxelScreenManager.monospaced, 
					width/2, height/2, width, true);
			StaticText.loadText(areyousure);
			texts.add(areyousure);
			
			// delete button
			UIButton by = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new TheyAreSure(this.data), master, width+300, height-65, 150, 60);
			UIText bbty = master.createDynamicText("Delete", 1.5f, VoxelScreenManager.monospaced, width+325, height-50, 100, true);
			StaticText.loadText(bbty);
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
			// delete the world.
			deleteWorld(new File("worlds/"+this.data));
			deleteWorldFolder(new File("worlds/"+this.data));
			new File("worlds/"+this.data).delete();
			for (UIText t : texts)
				StaticText.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			new SinglePlayer().event("eee");;
		}
	}
	
	/**
	 * deletes the world file
	 * this won't delete folders.
	 */
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
	
	/**
	 * deletes a world folders
	 */
	private void deleteWorldFolder(File f) {
		if (!f.exists())
			return;
	    for (File file : f.listFiles()) {
	        if (file.isFile())
	            file.delete();
	        else {
	        	// delete all the files then delete the folder
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
			// clear the screen
			for (UIText t : texts)
				StaticText.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			ChunkStore.worldLocation = "worlds/" + this.data;
			String stS = "";
			// make sure the seed exists
			if (seedData.trim().length() > 0) {
				// convert the string into a seed
				char[] stdchar = seedData.toCharArray();
				for (int i = 0; i < stdchar.length; i++) {
					stS += (int)(stdchar[i]);
				}
				// since the string has the chance of being much longer then the max long
				// we split it into 4 sections
				String first = stS.substring(0, stS.length()/4);
				String mid = stS.substring(stS.length()/4, stS.length()/4 + stS.length()/4);
				// i wrote this at 00:30. Give me a break
				String mid2 = stS.substring(stS.length()/4 + stS.length()/4, stS.length()/4 + stS.length()/4 + stS.length()/4);
				String last = stS.substring(stS.length()/4 + stS.length()/4 + stS.length()/4, stS.length());
				// then we add them to the seed.
				try {
					// first should always exist.
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
				// generate a random seed if there is no seed inputed.
				LevelLoader.seed = (long)(new Random().nextInt(Integer.MAX_VALUE)) + (long)(new Random().nextInt(Integer.MAX_VALUE));
			}
			
			// init and switch the world.
			ingame = true;
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
			// clear the screen
			for (UIText t : texts)
				StaticText.removeText(t);
			elements.clear();
			buttons.clear();
			texts.clear();
			// starts the world if the world folder exists
			// if not then we ask them about creating a world
			if (new File("worlds/"+this.data).exists()) {
				// start the world
				ChunkStore.worldLocation = "worlds/" + this.data;
				ingame = true;
				VoxelScreenManager.world.init();
				VoxelScreenManager.changeDisplaySource(renderer);
				VoxelScreenManager.ui.addCenteredTexture(loader.loadTexture("crosshair"), -1, -1, 0, 0, 16, 16);
				EventQueue.doEvent(0);
			} else {
				int width = DisplayManager.WIDTH/2;
				int height = DisplayManager.HEIGHT;
				
				// add dirt and back buttons
				elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, DisplayManager.WIDTH/32, DisplayManager.HEIGHT/32));
				UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new SinglePlayer(), master, width-200, height-65, 400, 60);
				UIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, width-200, height-50, 400, true);
				StaticText.loadText(bbt);
				texts.add(bbt);
				buttons.add(b);
				elements.add(b);
				
				// ask them to enter a seed
				UIText seedtextman = master.createDynamicText("Enter seed to be used for world generation. Blank for random.", 1.5f, VoxelScreenManager.monospaced, 
						width/2, height/2 - 120, width, true);
				StaticText.loadText(seedtextman);
				texts.add(seedtextman);
				
				// default seed is 694.
				// seed input box
				UIText tbt = master.createDynamicText("694", 1.5f, VoxelScreenManager.monospaced, width-190, height/2-190, 400, false);
				UITextBox tb = new UITextBox(loader.loadSpecialTexture("gui/slider"), new UIControl() {
					@Override
					public void event(String data) {
						seedData = data;
						StaticText.removeText(tbt);
						tbt.setText(data);
						StaticText.loadText(tbt);
					}
				}, 23, width-200, height/2-200, 400, 60);
				tb.inputTextBuffer = "694";
				StaticText.loadText(tbt);
				texts.add(tbt);
				buttons.add(tb);
				elements.add(tb);
				
				// generate world button
				UIButton bg = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new CreateWorld(renderer, this.data), master, width-200, height/2+200, 400, 60);
				UIText bbtg = master.createDynamicText("Generate World", 1.5f, VoxelScreenManager.monospaced, width-200, height/2+200+15, 400, true);
				StaticText.loadText(bbtg);
				texts.add(bbtg);
				buttons.add(bg);
				elements.add(bg);
			}
		}
		
	}
	
	public class Options implements UIControl,DisplaySource {
		
		private UIMaster master;
		private Loader loader;
		private UIDynamicText senstiv;
		private UIDynamicText rendd;
		private UIDynamicText fovv;
		private UIDynamicText music;
		
		public Options(UIMaster master, Loader loader) {
			this.master = master;
			this.loader = loader;
		}
		
		public void init() {
			int localWidth = DisplayManager.WIDTH/2;
			int height = DisplayManager.HEIGHT;
			// dirt and back button
			elements.add(master.createUITexture(loader.loadSpecialTexture("gui/dirt"), -1, -1, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, DisplayManager.WIDTH/32, DisplayManager.HEIGHT/32));
			UIButton b = new UIButton(loader.loadSpecialTexture("gui/button"), loader.loadSpecialTexture("gui/buttonsel"), new Main(), master, localWidth-200, height-65, 400, 60);
			UIText bbt = master.createDynamicText("Back", 1.5f, VoxelScreenManager.monospaced, localWidth-200, height-50, 400, true);
			StaticText.loadText(bbt);
			texts.add(bbt);
			buttons.add(b);
			elements.add(b);
			
			// sensitivity slider
			UISlider senstivSlid = new UISlider("sensitivity", loader.loadSpecialTexture("gui/slider"), loader.loadSpecialTexture("gui/button"), options, master, 120, 120, 300, 60);
			// set its percent to the saved percent
			senstivSlid.setPercent(SettingsLoader.SENSITIVITY);
			senstiv = master.createDynamicText("Sensitivity: " + Math.round(senstivSlid.getPercent()*100) + "%", 1.5f, VoxelScreenManager.monospaced, 120, 135, 300, true);
			StaticText.loadText(senstiv);
			texts.add(senstiv);
			buttons.add(senstivSlid);
			elements.add(senstivSlid);
			
			// render distance slider
			UISlider renddSlid = new UISlider("rend", loader.loadSpecialTexture("gui/slider"), loader.loadSpecialTexture("gui/button"), options, master, 440, 120, 400, 60);
			// set to saved distance
			renddSlid.setPercent(ChunkStore.renderDistance/20d);
			rendd = master.createDynamicText("Render Distance: " + ChunkStore.renderDistance, 1.5f, VoxelScreenManager.monospaced, (440), 135, 400, true);
			StaticText.loadText(rendd);
			texts.add(rendd);
			buttons.add(renddSlid);
			elements.add(renddSlid);
			
			// field of view slider
			UISlider fovSlid = new UISlider("fov", loader.loadSpecialTexture("gui/slider"), loader.loadSpecialTexture("gui/button"), options, master, 860, 120, 300, 60);
			// set to saved value
			fovSlid.setPercent((MasterRenderer.FOV-60)/122);
			fovv = master.createDynamicText("FOV: " + MasterRenderer.FOV, 1.5f, VoxelScreenManager.monospaced, (860), 135, 300, true);
			StaticText.loadText(fovv);
			texts.add(fovv);
			buttons.add(fovSlid);
			elements.add(fovSlid);
			
			// music volume slider
			UISlider musicSlid = new UISlider("music", loader.loadSpecialTexture("gui/slider"), loader.loadSpecialTexture("gui/button"), options, master, 860, 240, 300, 60);
			musicSlid.setPercent(SettingsLoader.MUSIC);
			music = master.createDynamicText("Music: " + (int)(SettingsLoader.MUSIC * 100) + "%", 1.5f, VoxelScreenManager.monospaced, (860), 255, 300, true);
			StaticText.loadText(music);
			texts.add(music);
			buttons.add(musicSlid);
			elements.add(musicSlid);
			
		}
		
		@Override
		public void event(String data) {
			if (data == null) {
				// return to options menu if there was no data
				for (UIText t : texts)
					StaticText.removeText(t);
				texts.clear();
				elements.clear();
				buttons.clear();
				this.init();
				VoxelScreenManager.changeDisplaySource(this);
				EventQueue.doEvent(1);
			} else {
				// change the data based on what was changed
				String[] datas = data.split(":");
				// get the percent from the slider data
				double percent = Double.parseDouble(datas[1]);
				if (datas[0].contentEquals("sensitivity")) {
					// update the sensitivity text
					StaticText.removeText(senstiv);
					senstiv.changeTextNoUpdate("Sensitivity: " + (Math.round(percent*100)) + "%");
					StaticText.loadText(senstiv);
					// change the value
					SettingsLoader.SENSITIVITY = percent;
				}
				if (datas[0].contentEquals("rend")) {
					// update the render distance. the math is to limit the size of the render distance.
					ChunkStore.renderDistance = (int) Math.round((20 * percent) + 1);
					// update the text
					StaticText.removeText(rendd);
					rendd.changeTextNoUpdate("Render Distance: " + ChunkStore.renderDistance);
					StaticText.loadText(rendd);
				}
				if (datas[0].contentEquals("fov")) {
					// update the FOV, math limits it to 60-120
					MasterRenderer.FOV = (int) Math.round((60 * percent) + 60);
					// update the text
					StaticText.removeText(fovv);
					fovv.changeTextNoUpdate("FOV: " + MasterRenderer.FOV);
					StaticText.loadText(fovv);
				}
				if (datas[0].contentEquals("music")) {
					// change the music percent
					SettingsLoader.MUSIC = percent;
					// try to change the volume
					try {
						MusicMaster.musicSystem.setVolume("MUSICPLAYER", (float) percent);
					} catch (Exception e) {}
					// change the text
					StaticText.removeText(music);
					music.changeTextNoUpdate("Music: " + (int)(SettingsLoader.MUSIC * 100) + "%");
					StaticText.loadText(music);
				}
			}
		}

		@Override
		public void render() {
			// not sure why this is here and it shouldn't be needed or be used
			// I'm not going to remove it just in case
			GL11.glClearColor(1, 1, 1, 1);
			renderer.render(elements);
			for (int i = 0; i < buttons.size(); i++)
				buttons.get(i).update();
		}
		
	}
	
	public class Main implements UIControl {

		@Override
		public void event(String data) {
			// changes to main menu
			for (UIText t : texts)
				StaticText.removeText(t);
			texts.clear();
			elements.clear();
			buttons.clear();
			menu.init();
			VoxelScreenManager.changeDisplaySource(menu);
		}
		
	}

}
