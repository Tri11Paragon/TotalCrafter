package com.brett.engine.ui.screen;

import java.util.List;

import org.joml.Vector3f;

import com.brett.engine.cameras.CreativeCamera;
import com.brett.engine.ui.UIElement;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class SinglePlayer extends Screen {

	public CreativeCamera camera;
	
	
	public SinglePlayer() {
		
	}
	
	public void onSwitch() {
		super.onSwitch();
		camera = new CreativeCamera(new Vector3f());
	}
	
	public void onLeave() {
		super.onLeave();
	}
	
	@Override
	public List<UIElement> render() {
		camera.move();
		return super.render();
	}
	
	public void close() {
		super.close();
	}
	
}
