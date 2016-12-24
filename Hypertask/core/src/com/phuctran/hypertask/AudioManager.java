package com.phuctran.hypertask;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
	private boolean musicEnabled, soundEnabled;
	private float musicVol, soundVol;
	private Music currentMusic;
	
	private static AudioManager instance;
	public static AudioManager instance() {
		if (instance == null) {
			instance = new AudioManager();
		}
		return instance;
	}
	
	private AudioManager() {
		musicEnabled = true;
		soundEnabled = true;
		musicVol = 1;
		soundVol = 1;
		currentMusic = null;
	}
	
	public void play() {
		if (currentMusic != null) {
			currentMusic.setVolume(musicEnabled ? musicVol : 0);
			currentMusic.play();
		}
	}
	
	public void play(Music music) {
		currentMusic = music;
		music.setVolume(musicEnabled ? musicVol : 0);
		music.play();
	}
	
	public void play(Music music, boolean loop) {
		music.setLooping(loop);
		play(music);
	}
	
	public void stop() {
		if (currentMusic != null)
			currentMusic.stop();
	}
	
	public void pause() {
		if (currentMusic != null)
			currentMusic.pause();
	}
	
	public void play(Sound sound) {
		if (soundEnabled)
			sound.play();
	}
	
	public void play(Sound sound, float volume) {
		sound.play(soundEnabled ? volume : 0);
	}
	
	public void play(Sound sound, float volume, float pitch, float pan) {
		sound.play(soundEnabled ? volume : 0, pitch, pan);
	}
	
	public void setMusicEnabled(boolean enabled) {
		musicEnabled = enabled;
		if (currentMusic != null) {
			currentMusic.setVolume(musicEnabled ? musicVol : 0);
		}
	}
	
	public void setSoundEnabled(boolean enabled) {
		soundEnabled = enabled;
	}
	
	public void setMusicVolume(float volume) {
		musicVol = volume;
	}
	
	public void setSoundVolume(float volume) {
		soundVol = volume;
	}
}
