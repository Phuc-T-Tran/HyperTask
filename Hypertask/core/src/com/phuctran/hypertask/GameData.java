package com.phuctran.hypertask;

import com.badlogic.gdx.Gdx;
import com.phuctran.hypertask.Constants.GameType;

public class GameData {
	public static final String KEY_PREFERENCES = "hypertask";
	private static com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences(KEY_PREFERENCES);
	
	// Stats
	public static final String KEY_BEST = "best";
	public static final String KEY_GAMES_PLAYED = "gamesPlayed";
	public static final String KEY_WINS = "Wins";
	public static final String KEY_LOSSES = "Losses";
	public static final String KEY_AD_COUNTER = "adCounter";
	
	// Tutorials
	public static final String KEY_TUTORIALS = "tutorials";
	public static final String VAL_TUTORIALS_ON = "tutorialsOn";
	public static final String VAL_TUTORIALS_OFF = "tutorialsOff";
	public static final String VAL_TUTORIALS_ALWAYS_ON = "tutorialsAlwaysOn";
	public static final String KEY_TUTORIAL_DONE = "TutorialDone";
	public static final String KEY_NOADS = "noAds";
	
	// Audio
	public static final String KEY_MUSIC = "music";
	public static final String KEY_SOUND = "sound";
	
	public static int getInt( String key, int defValue ) {
		return prefs.getInteger( key, defValue );
	}
	
	public static boolean getBoolean( String key, boolean defValue ) {
		return prefs.getBoolean( key, defValue );
	}
	
	public static String getString( String key, String defValue ) {
		return prefs.getString( key, defValue );
	}
	
	public static void put( String key, int value ) {
		prefs.putInteger( key, value );
		prefs.flush();
	}
	
	public static void put( String key, boolean value ) {
		prefs.putBoolean( key, value );
		prefs.flush();
	}
	
	public static void put( String key, String value ) {
		prefs.putString( key, value );
		prefs.flush();
	}
	
	public static void incrInt( String key ) {
		put(key, getInt(key, 0) + 1);
	}
	
	public static void incrInt( String key, int value ) {
		put(key, getInt(key, 0) + value);
	}
	
	public static void toggle( String key ) {
		put(key, !getBoolean(key, false));
	}
	
	public static void resetAll() {
		resetStatistics();
		resetTutorials();
		resetAudio();
	}
	
	public static void resetStatistics() {
		put(KEY_BEST, 0);
		put(KEY_GAMES_PLAYED, 0);
		
		for (GameType type : GameType.values()) {
			put(type.toString() + KEY_WINS, 0);
			put(type.toString() + KEY_LOSSES, 0);
		}
	}
	
	public static void resetTutorials() {
		for (GameType type : GameType.values())
			put(type.toString() + KEY_TUTORIAL_DONE, false);
		put(KEY_TUTORIALS, VAL_TUTORIALS_ON);
	}
	
	public static void resetAudio() {
		put(KEY_MUSIC, true);
		put(KEY_SOUND, true);
	}
}
