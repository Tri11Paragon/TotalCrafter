package com.brett.engine.ui.screen;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.brett.engine.cameras.CreativeCamera;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.tools.Maths;
import com.brett.engine.ui.UIButton;
import com.brett.engine.ui.UIElement;
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
		elements.add(new UITexture(ScreenManager.loader.loadTexture("dirt"), -2, -2, 50, 50, 16, 16));
		elements.add(new UIButton(ScreenManager.loader.loadTexture("dirt"), ScreenManager.loader.loadTexture("clay"), 500, 120, 100, 300));
		addText(UIText.updateTextMesh(new UIText("hello", 5.0f, "mono", new Vector2f(1,0), 500, false)));
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
	public void close() {
		super.close();
	}
	
}
