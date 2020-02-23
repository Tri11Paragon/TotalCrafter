package com.brett.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.openal.OggData;
import org.newdawn.slick.openal.OggDecoder;
import org.newdawn.slick.util.Log;

/**
*
* @author brett
*
*/

public class AudioController {

	private static List<Integer> buffers = new ArrayList<Integer>();
	private static HashMap<String, Integer> loaded = new HashMap<String, Integer>();
	
	public static void init() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets position of the listener
	 */
	public static void setListenerData(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}
	
	public static void setListenerPosition(Vector3f f) {
		AL10.alListener3f(AL10.AL_POSITION, f.x, f.y, f.z);
	}
	
	public static int[] loadSoundFolder(String folder) {
		folder = "resources/sound/" + folder;
		File sfolder = new File(folder);
		File[] sfiles = sfolder.listFiles();
		List<Integer> ints = new ArrayList<Integer>();
		for (int i = 0; i < sfiles.length; i++) {
			if (sfiles[i].isFile()) {
				if (sfiles[i].getName().endsWith(".ogg"))
					ints.add(loadS(sfiles[i].getAbsolutePath()));
			}
		}
		// java for some reason does not have easy object[] to int[] conversion :/
		int[] is = new int[ints.size()];
		for (int i = 0; i < is.length; i++)
			is[i] = ints.get(i);
		
		return is;
	}
	
	public static int loadSound(String file) {
		return loadS("resources/sound/" + file);
	}
	
	public static int loadS(String file) {
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		try {
			buffer = getOgg(new BufferedInputStream(new FileInputStream(file)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}
	
	public static void cleanup() {
		for (int buffer : buffers) {
			AL10.alDeleteBuffers(buffer);
		}
		AL.destroy();
	}
	
	// i stole this from the slick util jar that i've imported as a build jar
	// but his code doesn't work because im already doing the init inside this class.
	// so i made it work for here.
	// TODO: load this myself for faster loading.
	
	private static int getOgg(InputStream in) throws IOException {
		return getOgg(in.toString(), in);
	}
	private static int getOgg(String ref, InputStream in) throws IOException {
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
	
}