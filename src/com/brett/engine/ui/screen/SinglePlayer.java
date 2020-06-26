package com.brett.engine.ui.screen;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.brett.engine.cameras.CreativeCamera;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.tools.Maths;
import com.brett.engine.ui.AnchorPoint;
import com.brett.engine.ui.UIButton;
import com.brett.engine.ui.UIElement;
import com.brett.engine.ui.UISlider;
import com.brett.engine.ui.UITexture;
import com.brett.engine.ui.font.UIText;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class SinglePlayer extends Screen {

	public static Matrix4f viewMatrix;
	public CreativeCamera camera;
	
	public SinglePlayer() {
		
	}
	
	@Override
	public void onSwitch() {
		super.onSwitch();
		//elements.add(new UITexture(ScreenManager.loader.loadTexture("dirt"), -2, -2, 0, 0, 200, 200, AnchorPoint.CENTER).setBoundingBox(200, 200, 200, 200));
		//UIText text = new UIText("he l loe there", 250.0f, "mono", 600, 300, 20);
		//elements.add(new UIButton(ScreenManager.loader.loadTexture("dirt"), ScreenManager.loader.loadTexture("clay"), 600, 300, 115, 100).setText(text));
		
		UITexture bar = new UITexture(ScreenManager.loader.loadTexture("coal_block"), -1, -1, 50, 50, 10, 30);
		elements.add(new UISlider(ScreenManager.loader.loadTexture("sand"), bar, 50, 50, 300, 30));
		elements.add(bar);
		camera = new CreativeCamera(new Vector3f());
	}
	
	@Override
	public void onLeave() {
		super.onLeave();
	}
	
	@Override
	public List<UIElement> render() {
		camera.move();
		viewMatrix = Maths.createViewMatrix(camera);
		return super.render();
	}
	
	@Override
	public Map<String, List<UIText>> renderText() {
		return super.renderText();
	}
	
	@Override
	public void close() {
		super.close();
	}
	
}
