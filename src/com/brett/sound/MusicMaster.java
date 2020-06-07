package com.brett.sound;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.openal.OggData;

import com.brett.renderer.datatypes.Tuple;

/**
*
* @author brett
* @date Jun. 6, 2020
*/

public class MusicMaster {
	
	// music folder location
	public static String musicLocation = "resources/sound/music/";
	
	// list of all loaded music
	public static volatile List<Integer> listOfMusic = Collections.synchronizedList(new ArrayList<Integer>());
	// list of unprocessed musics
	public static volatile List<Tuple<String, OggData>> unprocessedMusic = new ArrayList<Tuple<String, OggData>>();
	// used to keep game from loading until all musics have benn loaded.
	public static volatile int amount = 0;
	
	public static void init() {
		// music folder reference.
		File musicFolder = new File(musicLocation);
		// get all the files in the music folder.
		File[] musics = musicFolder.listFiles();
		// make sure there is music in the folder
		if (musics.length < 1)
			return;
		System.out.println("Init Music");
		long start = System.currentTimeMillis();
		// loop through all the musics in the music folder.
		for (int i = 0; i < musics.length; i++) {
			// check to make sure we are not loading some stupid file the user
			// leaves in the music folder
			if (musics[i].getName().contains(".ogg")) {
				// load music with threads
				// Java got mad when I had the loader thread
				// in this class as either an inner type or
				// as a inner class. Java is weird.
				new LoadThread(musics[i]);
				System.out.println("Loading music ['" + musics[i].getName() + "']");
			}
		}
		// prevent the game from loading until we are 100% sure the music has been loaded completely.
		while (amount != musics.length) {
			// might as well make use of the wasted thread time and
			// process any loaded music.
			processMusic();
		}
		// just check to make sure that we processed all the pesky data
		// could use a do while so that way we don't need this?
		// again I like to make sure
		processMusic();
		System.out.println("Music took " + (System.currentTimeMillis() - start) + "ms to load.");
	}

	/**
	 * You may be asking why not load this in another thread?
	 * wouldn't that be faster? You'd be right that it would be faster
	 * however OpenGL doesn't exist in the other threads and therefore can't
	 * have stuff loaded to it. (It also doesn't take THAT long to do it this way)
	 *  :(
	 * (Switching context to the other thread is slow)
	 * 
	 * This processes all the unprocessed music from the threads.
	 */
	private static void processMusic() {
		// make sure there is music to load
		if (unprocessedMusic.size() > 0) {
			// loop through all musics
			for (int i = 0; i < unprocessedMusic.size(); i++) {
				// process it
				Tuple<String, OggData> datas = unprocessedMusic.get(i);
				listOfMusic.add(AudioController.loadS(datas.getX(), datas.getY()));
				// remove music as it has been processed.
				unprocessedMusic.remove(i);
			}
		}
	}
	
	public static void update() {
		
	}
	
}
