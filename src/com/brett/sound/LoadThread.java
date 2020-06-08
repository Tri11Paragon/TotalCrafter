package com.brett.sound;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
*
* @author brett
* @date Jun. 6, 2020
*/

public class LoadThread extends Thread {
	
	// reference to all the files to load
    private ArrayList<File> f;
	
	public LoadThread(ArrayList<File> f) {
		this.f = f;
		// no reason to start a thread that won't do anything
		if (f.size() == 0)
			return;
		this.start();
	}
	
	@Override
	public void run() {
		// load all files inside the file array.
		for (int i = 0; i < f.size(); i++) {
			try {
				System.out.println(f.get(i).toURI().toURL());
				MusicMaster.musicSystem.loadSound(new URL("file:resources/sound/music/" + f.get(i).getName().replace(' ', '_')), f.get(i).getName().replace(' ', '_'));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
