package com.brett.sound;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.brett.tools.SettingsLoader;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

/**
*
* @author brett
* @date Jun. 6, 2020
*/

public class MusicMaster {
	
	// music folder location
	public static String musicLocation = "resources/sound/music/";
	// list of all the musics
	public static List<String> musics = new ArrayList<String>();
	// music system reference
	public static SoundSystem musicSystem;
	// random number generator
	private static Random rand = new Random(694 + System.currentTimeMillis());
	
	public static void init() {
		
		// pualscode sound system init
		// taken from the tutorial.
		try {
			SoundSystemConfig.setSoundFilesPackage(musicLocation);
			SoundSystemConfig.addLibrary( LibraryLWJGLOpenAL.class );
			SoundSystemConfig.setCodec( "ogg", CodecJOgg.class ); 
		} catch (SoundSystemException e) {
			e.printStackTrace();
		}
		try {
			musicSystem = new SoundSystem( LibraryLWJGLOpenAL.class );
		} catch (SoundSystemException e) {
			e.printStackTrace();
		} 
		
		// music folder reference.
		File musicFolder = new File(musicLocation);
		// get all the files in the music folder.
		File[] musics = musicFolder.listFiles();
		// make sure there is music in the folder
		if (musics.length < 1)
			return;
		long start = System.currentTimeMillis();
		// loop through all the musics in the music folder.
		for (int i = 0; i < musics.length; i++) {
			// check to make sure we are not loading some stupid file the user
			// left in the music folder
			if (musics[i].getName().contains(".ogg")) {
				// add it to the list of music files
				MusicMaster.musics.add(musics[i].getName().replace(' ', '_'));
				System.out.println("Loading music ['" + musics[i].getName() + "']");
			}
		}
		System.out.println("Music took " + (System.currentTimeMillis() - start) + "ms to load.");
	}
	
	// last time since we played music.
	// make sure that there wont be music for the first ten minutes after startup this way.
	private static long lastTime = System.currentTimeMillis();
	public static void update() {
		if (musicSystem.playing() || musics.size() == 0) {
			return;
		}
		// play if random and enough time has past. (5 minutes)
		if (rand.nextInt(5000) == 10 && (System.currentTimeMillis() - lastTime > (600000))) {
			// play some music.
			String music = musics.get(rand.nextInt(musics.size()));
			System.out.println("Now playing '" + music + "'");
			try {
				// plays the music
				// on any menu.
				musicSystem.setVolume("MUSICPLAYER", (float) SettingsLoader.MUSIC);
				musicSystem.backgroundMusic("MUSICPLAYER", new URL("file:resources/sound/music/" + music), music, false);
				musicSystem.setVolume("MUSICPLAYER", (float) SettingsLoader.MUSIC);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// keep reference to the last time we played music.
			lastTime = System.currentTimeMillis();
		}
	}
	
	// cleanup music systems.
	public static void cleanup() {
		musicSystem.cleanup();
	}
	
}
