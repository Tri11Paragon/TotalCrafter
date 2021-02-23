package com.brett.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;

import sun.rmi.runtime.Log;

/**
*
* @author brett
*
*/

public class AudioController {

	private static List<Integer> buffers = new ArrayList<Integer>();
	private static HashMap<String, Integer> loaded = new HashMap<String, Integer>();
	private static FloatBuffer listndata = BufferUtils.createFloatBuffer(6);
	
	public static void init() {
		// the way we assign position and rotation and stuff and what not
		// weird i know.
		listndata.put( 0, 0 );
		listndata.put( 1, 0 );
		listndata.put( 2, 0 );
		listndata.put( 3, 0 );
		listndata.put( 4, 1 );
		listndata.put( 5, 0 );
		try {
			// creates OpenAL instance
			ALC.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets position of the listener
	 */
	public static void setListenerData(float x, float y, float z, float rx, float ry, float rz) {
		try {
			// sets position. OpenAL has support for stuff like doppler effect.
			// im not using it.
			AL10.alListener3f(AL10.AL_POSITION, x, y, z);
			AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
			// set the rotation of the camera
			listndata.put( 0, rx);
			listndata.put( 1, ry );
			listndata.put( 2, rz );
			// apply it to OpenAl
			AL10.alListenerfv(AL10.AL_ORIENTATION, listndata);
			} catch (Exception e) {}
	}
	
	/**
	 * sets position of the listener
	 */
	public static void setListenerPosition(Vector3f f, float rx, float ry, float rz) {
		try {
			// same thing as ^ but with a vec3
			AL10.alListener3f(AL10.AL_POSITION, f.x, f.y, f.z);
			AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
			listndata.put( 0, rx);
			listndata.put( 1, ry );
			listndata.put( 2, rz );
			AL10.alListenerfv(AL10.AL_ORIENTATION, listndata);
		} catch (Exception e) {}
	}
	
	/**
	 * loads an entire folder of sounds. Starts in resources/sound/
	 */
	/*public static int[] loadSoundFolder(String folder) {
		try {
			folder = "resources/sound/" + folder;
			File sfolder = new File(folder);
			File[] sfiles = sfolder.listFiles();
			List<Integer> ints = new ArrayList<Integer>();
			// goes through all files in a folder
			// adds them if they are ogg files.
			for (int i = 0; i < sfiles.length; i++) {
				if (sfiles[i].isFile()) {
					if (sfiles[i].getName().endsWith(".ogg"))
						ints.add(loadS(sfiles[i].getAbsolutePath()));
				}
			}
			// java for some reason does not have easy object[] to int[] conversion :/
			// or else i'd use list.toArray();
			int[] is = new int[ints.size()];
			for (int i = 0; i < is.length; i++)
				is[i] = ints.get(i);
			
			return is;
			} catch (Exception e) {}
		return null;
	}*/
	
	/**
	 * loads a single sound file. (OGG)
	 */
	/*public static int loadSound(String file) {
		try {
			return loadS("resources/sound/" + file);
		} catch (Exception e) {}
		return 0;
	}*/
	
	/**
	 * loads a sound from a sound file (ogg)
	 */
	/*public static int loadS(String file) {
		// make sure we have a window open (not running server)
		//TODO: RE-ADD THIS
		//if (GLContext.getCapabilities() == null)
		//	return 0;
		// generate a buffer for this sound
		int buffer = AL10.alGenBuffers();
		// for deletion (should know this by now)
		buffers.add(buffer);
		try {
			// convert the ogg
			buffer = getOgg(new BufferedInputStream(new FileInputStream(file)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}*/
	
	/**
	 * deletes all active buffers
	 */
	public static void cleanup() {
		for (int i = 0; i < buffers.size(); i++) {
			AL10.alDeleteBuffers(buffers.get(i));
		}
		//AL.destroy();
	}
	
	// i stole this from the slick util jar that i've imported as a build jar
	// but his code doesn't work because im already doing the init inside this class.
	// so i made it work for here.
	// TODO: load this myself for faster loading.
	// (everything below the line I didn't write)
	// --------------------------------------------
	
	/*public static int getOgg(InputStream in) throws IOException {
		return getOgg(in.toString(), in);
	}
	public static int getOgg(String ref, InputStream in) throws IOException {
		int buffer = -1;
		
		if (loaded.get(ref) != null) {
			buffer = ((Integer) loaded.get(ref)).intValue();
		} else {
			try {
				IntBuffer buf = BufferUtils.createIntBuffer(1);
				
				OggDecoder decoder = new OggDecoder();
				OggData ogg = decoder.getData(in);
				
				AL10.alGenBuffers(buf);
				AL10.alBufferData(buf.get(0), ogg.channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, ogg.data, ogg.rate);
				
				loaded.put(ref,new Integer(buf.get(0)));
				                     
				buffer = buf.get(0);
			} catch (Exception e) {
				Log.error(e);
				Sys.alert("Error","Failed to load: "+ref+" - "+e.getMessage());
				throw new IOException("Unable to load: "+ref);
			}
		}
		
		if (buffer == -1) {
			throw new IOException("Unable to load: "+ref);
		}
		
		return buffer;
	}
	
	public static int getOgg(String ref, OggData ogg) throws IOException {
		int buffer = -1;
		
		if (loaded.get(ref) != null) {
			buffer = ((Integer) loaded.get(ref)).intValue();
		} else {
			try {
				IntBuffer buf = BufferUtils.createIntBuffer(1);

				AL10.alGenBuffers(buf);
				AL10.alBufferData(buf.get(0), ogg.channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, ogg.data, ogg.rate);
				
				loaded.put(ref,new Integer(buf.get(0)));
				                     
				buffer = buf.get(0);
			} catch (Exception e) {
				Log.error(e);
				Sys.alert("Error","Failed to load: "+ref+" - "+e.getMessage());
				throw new IOException("Unable to load: "+ref);
			}
		}
		
		if (buffer == -1) {
			throw new IOException("Unable to load: "+ref);
		}
		
		return buffer;
	}*/
	
}
