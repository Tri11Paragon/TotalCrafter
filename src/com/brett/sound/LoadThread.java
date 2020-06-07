package com.brett.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.newdawn.slick.openal.OggData;
import org.newdawn.slick.openal.OggDecoder;

import com.brett.renderer.datatypes.Tuple;

/**
*
* @author brett
* @date Jun. 6, 2020
*/

public class LoadThread {
	
	public LoadThread(File f) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// decode the ogg data
					OggDecoder decoder = new OggDecoder();
					OggData data = decoder.getData(new BufferedInputStream(new FileInputStream(f)));
					// load it for loading.
					MusicMaster.unprocessedMusic.add(new Tuple<String, OggData> (f.getName(), data));
					MusicMaster.amount++;
				} catch (Exception e) {}
			}
		}).start();
	}
	
}
