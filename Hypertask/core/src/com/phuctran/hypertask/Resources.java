package com.phuctran.hypertask;

import static com.phuctran.hypertask.Constants.*;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Resources {
	private HashMap<String, Texture> textures;
	private HashMap<String, BitmapFont> fonts;
	private HashMap<String, Music> music;
	private HashMap<String, Sound> sounds;
	
	public Resources() {
		textures = new HashMap<String, Texture>();
		fonts = new HashMap<String, BitmapFont>();
		music = new HashMap<String, Music>();
		sounds = new HashMap<String, Sound>();
		
		loadResources();
	}
	
	private void loadResources() {
		// Textures
		for (int i = 0; i < GameNames.length; i++)
			loadTexture(GameNames[i], GameSymbolPaths[i]);
		
		loadTexture(BUTTON_PLAY, BUTTON_PLAY_PATH);
		loadTexture(BUTTON_BACK, BUTTON_BACK_PATH);
		loadTexture(BUTTON_RETRY, BUTTON_RETRY_PATH);
		
		loadTexture(BUTTON_OPTIONS, BUTTON_OPTIONS_PATH);
		loadTexture(BUTTON_STATS, BUTTON_STATS_PATH);
		loadTexture(BUTTON_INFO, BUTTON_INFO_PATH);
		loadTexture(BUTTON_LEADERBOARD, BUTTON_LEADERBOARD_PATH);
		
		loadTexture(BUTTON_MUSIC_ON, BUTTON_MUSIC_ON_PATH);
		loadTexture(BUTTON_MUSIC_OFF, BUTTON_MUSIC_OFF_PATH);
		loadTexture(BUTTON_SOUND_ON, BUTTON_SOUND_ON_PATH);
		loadTexture(BUTTON_SOUND_OFF, BUTTON_SOUND_OFF_PATH);
		
		loadTexture(ICON_TAP, ICON_TAP_PATH);
		loadTexture(ICON_HOLD, ICON_HOLD_PATH);
		loadTexture(ICON_DRAG, ICON_DRAG_PATH);
		loadTexture(ICON_DRAGHORIZONTAL, ICON_DRAGHORIZONTAL_PATH);
		
		loadTexture(BORDER, BORDER_PATH);
		
		// Fonts
		loadFont(FONT_LARGE, FONT, FONT_SIZE_LARGE);
		loadFont(FONT_MEDIUM, FONT, FONT_SIZE_MEDIUM);
		loadFont(FONT_SMALL, FONT, FONT_SIZE_SMALL);
		
		// Audio
		loadMusic(MUSIC_MENU, MUSIC_MENU_PATH);
		loadMusic(MUSIC_PLAY, MUSIC_PLAY_PATH);
		loadSound(SOUND_SCORE, SOUND_SCORE_PATH);
		loadSound(SOUND_CLICK, SOUND_CLICK_PATH);
	}
	
	public void loadTexture(String name, String path) {
		textures.put(name, new Texture(Gdx.files.internal(path)));
	}
	
	public void loadFont(String name, String path, int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.color = COLOR_TEXT;
		fonts.put(name, generator.generateFont(parameter));
		generator.dispose();
	}
	
	public void loadMusic(String name, String path) {
		music.put(name, Gdx.audio.newMusic(Gdx.files.internal(path)));
	}
	
	public void loadSound(String name, String path) {
		sounds.put(name, Gdx.audio.newSound(Gdx.files.internal(path)));
	}
	
	public Texture getTexture(String name) {
		return textures.get(name);
	}
	
	public BitmapFont getFont(String name) {
		return fonts.get(name);
	}
	
	public Music getMusic(String name) {
		return music.get(name);
	}
	
	public Sound getSound(String name) {
		return sounds.get(name);
	}
	
	public void dispose() {
		for (Texture texture : textures.values()) {
			texture.dispose();
		}
		textures.clear();
		
		for (BitmapFont font : fonts.values()) {
			font.dispose();
		}
		fonts.clear();
		
		for (Music music : music.values()) {
			music.dispose();
		}
		music.clear();
		
		for (Sound sound : sounds.values()) {
			sound.dispose();
		}
		sounds.clear();
	}
}
