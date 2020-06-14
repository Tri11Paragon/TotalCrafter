package com.brett.renderer.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;

import com.brett.renderer.Loader;
import com.brett.renderer.font.UIDynamicText;
import com.brett.renderer.font.fontRendering.StaticText;
import com.brett.tools.SettingsLoader;
import com.brett.voxel.VoxelScreenManager;

/**
*
* @author brett
* 2020-06-13
*/

public class EscapeMenu implements IMenu {
	
	private List<UIElement> elements = new ArrayList<UIElement>();
	private List<UIDynamicText> texts = new ArrayList<UIDynamicText>();
	// this need to be private as we need to do stuff when this is changed.
	private boolean enabled = false;
	
	public EscapeMenu(UIMaster master, Loader loader) {
		//elements.add(master.createCenteredTexture(-1, -1, -1, 0, 0, 200, 200, new Vector3f(0,0,0)));
		int localWidth = Display.getWidth()/2;
		// i really don't know what to put for this
		// its literally just constructors for these classes
		// Paired with some anonymous inner types for event response
		// plus adding them to a list
		// they are all kind of the same
		// adds in the music slider
		UIDynamicText music = master.createDynamicText("Music: " + (int)(SettingsLoader.MUSIC * 100) + "%", 1.5f, VoxelScreenManager.monospaced, (localWidth+50), 135, 300, true);
		UISlider musicSlid = new UISlider("music", loader.loadSpecialTexture("gui/slider"), loader.loadSpecialTexture("gui/button"), new UIControl() {
			@Override
			public void event(String data) {
				// just internal control over the music volume.
				SettingsLoader.MUSIC = Double.parseDouble(data.split(":")[1]);
				StaticText.removeText(music);
				music.changeTextNoUpdate("Music: " + (int)(SettingsLoader.MUSIC * 100) + "%");
				StaticText.loadText(music);
			}
		}, master, localWidth+50, 120, 300, 60);
		musicSlid.setPercent(SettingsLoader.MUSIC);
		// add to the list of stuff
		texts.add(music);
		elements.add(musicSlid);
		
		// create the sensitivity silider
		UIDynamicText senstiv = master.createDynamicText("Sensitivity: " + Math.round(SettingsLoader.SENSITIVITY*100) + "%", 1.5f, VoxelScreenManager.monospaced, localWidth-350, 135, 300, true);
		UISlider senstivSlid = new UISlider("sensitivity", loader.loadSpecialTexture("gui/slider"), loader.loadSpecialTexture("gui/button"), new UIControl() {
			@Override
			public void event(String data) {
				SettingsLoader.SENSITIVITY = Double.parseDouble(data.split(":")[1]);
				StaticText.removeText(senstiv);
				senstiv.changeTextNoUpdate("Sensitivity: " + Math.round(SettingsLoader.SENSITIVITY*100) + "%");
				StaticText.loadText(senstiv);
			}
		}, master, localWidth-350, 120, 300, 60);
		senstivSlid.setPercent(SettingsLoader.SENSITIVITY);
		texts.add(senstiv);
		elements.add(senstivSlid);
		
		
		
	}
	
	@Override
	public List<UIElement> render(UIMaster ui) {
		if (enabled) {
			return elements;
		} else
			return null;
	}

	@Override
	public void update() {
		// weird that I have to do this here...
		// actually not really
		if (enabled) {
			for (int i = 0; i < elements.size(); i++)
				elements.get(i).update();
		}
	}
	
	public void setEnabled(boolean b) {
		enabled = b;
		if (enabled) {
			for (int i = 0; i < texts.size(); i++) {
				StaticText.loadText(texts.get(i));
			}
		} else {
			for (int i = 0; i < texts.size(); i++) {
				StaticText.removeText(texts.get(i));
			}
		}
	}

	@Override
	public List<UIElement> secondardRender(UIMaster ui) {
		return null;
	}
	
}
