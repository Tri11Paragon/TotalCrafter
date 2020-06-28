package com.brett.engine.data.datatypes;

import java.util.Map;
import java.util.Optional;

/**
* @author Brett
* @date Jun. 27, 2020
*/

public class AnimatedMesh {
	
	private Map<String, Animation> animations;
	private Animation currentAnimation;
	private Mesh[] meshes;
	
	public AnimatedMesh(Mesh[] meshes, Map<String, Animation> animations) {
		this.meshes = meshes;
        this.animations = animations;
        Optional<Map.Entry<String, Animation>> entry = animations.entrySet().stream().findFirst();
        currentAnimation = entry.isPresent() ? entry.get().getValue() : null;
    }
	
	public Mesh[] getMeshes() {
		return meshes;
	}
	
	public Animation getAnimation(String name) {
        return animations.get(name);
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(Animation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }
	
}
