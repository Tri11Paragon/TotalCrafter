package com.brett.sound;

import org.lwjgl.openal.AL10;

/**
*
* @author brett
* source of audip
*/
public class AudioSource {

	private int sourceID;
	
	public AudioSource() {
		try {
			// generate the source
			//sourceID = AL10.alGenSources();
			// default setup of the sound source
			//AL10.alSourcef(sourceID, AL10.AL_GAIN, 1);
			//AL10.alSourcef(sourceID, AL10.AL_PITCH, 1);
			//AL10.alSource3f(sourceID, AL10.AL_POSITION, 0, 0, 0);
		} catch (Exception e) {}
	}
	
	public AudioSource(int buffer) {
		try {
			// generate the source
			//sourceID = AL10.alGenSources();
			// default setup of the sound source
			//AL10.alSourcef(sourceID, AL10.AL_GAIN, 1);
			//AL10.alSourcef(sourceID, AL10.AL_PITCH, 1);
			//AL10.alSource3f(sourceID, AL10.AL_POSITION, 0, 0, 0);
			// set the playback buffer (the sound we are playing)
			//AL10.alSourcei(sourceID, AL10.AL_BUFFER, buffer);
			// play it
			//AL10.alSourcePlay(sourceID);
		} catch (Exception e) {}
	}
	
	/**
	 * plays a sound.
	 */
	public void play(int buffer) {
		// stop playing anything before we try playing something
		stop();
		// play the new sound
		//AL10.alSourcei(sourceID, AL10.AL_BUFFER, buffer);
		//AL10.alSourcePlay(sourceID);
	}
	
	/**
	 * toggles if the source is playing or not
	 */
	public void togglePlaying() {
		if (isPlaying())
			pause();
		else
			continuePlaying();
	}
	
	public void delete() {
		stop();
		//AL10.alDeleteSources(sourceID);
	}
	
	public void pause() {
		//AL10.alSourcePause(sourceID);
	}
	
	public void continuePlaying() {
		//AL10.alSourcePlay(sourceID);
	}
	
	public void stop() {
		//AL10.alSourceStop(sourceID);
	}
	
	public AudioSource setMaxDistance(float f) {
		//AL10.alSourcef(sourceID, AL10.AL_MAX_DISTANCE, f);
		return this;
	}
	
	public AudioSource setRollOffFactor(float f) {
		//AL10.alSourcef(sourceID, AL10.AL_ROLLOFF_FACTOR, f);
		return this;
	}

	/**
	 * Used to set the distance where the gain will be 1.
	 * @param f distance from source
	 */
	public AudioSource setReferenceDistance(float f) {
		//AL10.alSourcef(sourceID, AL10.AL_REFERENCE_DISTANCE, f);
		return this;
	}
	
	public AudioSource setVolume(float f) {
		//AL10.alSourcef(sourceID, AL10.AL_GAIN, f);
		return this;
	}
	
	public AudioSource setPitch(float f) {
		//AL10.alSourcef(sourceID, AL10.AL_PITCH, 1);
		return this;
	}
	
	public AudioSource setVelocity(float x, float y, float z) {
		//AL10.alSource3f(sourceID, AL10.AL_VELOCITY, x, y, z);
		return this;
	}
	
	public AudioSource setLooping(boolean loopAudio) {
		//AL10.alSourcei(sourceID, AL10.AL_LOOPING, loopAudio ? AL10.AL_TRUE : AL10.AL_FALSE);
		return this;
	}
	
	public boolean isPlaying() {
		//return AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
		return false;
	}
	
	public void setPosition(float x, float y, float z) {
		//AL10.alSource3f(sourceID, AL10.AL_POSITION, x, y, z);
	}
	
}
