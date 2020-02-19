package com.brett.sound;

import java.io.IOException;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

/**
*
* @author brett
*
*/

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException {

		AudioController.init();
		AudioController.setListenerData(0, 0, 0);
		
		int buffer = AudioController.loadSound("bounce.ogg");
		AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE_CLAMPED);
		AudioSource source = new AudioSource();
		source.setLooping(true);
		source.play(buffer);
		source.setReferenceDistance(6);
		source.setRollOffFactor(5);
		source.setMaxDistance(15);
		
		float xpos = 8;
		source.setPosition(xpos, 0, 0);
		
		char c = ' ';
		while (c != 'q') {
			//c = (char)System.in.read();
			
			xpos -= 0.03f;
			System.out.println(xpos);
			source.setPosition(xpos, 0, 0);
			Thread.sleep(10);
		}
		
		source.delete();
		AudioController.cleanup();
		
	}

}
