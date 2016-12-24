package com.phuctran.hypertask;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Constants {
	/* -=GAME=- */
	public static final int V_WIDTH = 1280;
	public static final int V_HEIGHT = 720;
	public static final int MINIGAME_WIDTH = 426;
	public static final int MINIGAME_HEIGHT = 720;
	public static final Rectangle BOUNDS_LEFT   = new Rectangle( 0,                       0, MINIGAME_WIDTH, MINIGAME_HEIGHT);
	public static final Rectangle BOUNDS_MIDDLE = new Rectangle( MINIGAME_WIDTH + 1,      0, MINIGAME_WIDTH, MINIGAME_HEIGHT);
	public static final Rectangle BOUNDS_RIGHT  = new Rectangle((MINIGAME_WIDTH + 1) * 2, 0, MINIGAME_WIDTH, MINIGAME_HEIGHT);
	
	public static final String FONT = "fonts/Electrolize/Electrolize-Regular.ttf";
	public static final Color COLOR_TEXT = new Color(1f,1f,1f,0.92f);
	public static final Color COLOR_MAIN = new Color(0.16f,0.16f,0.16f,1f);
	
	/* -=BOX2D=- */
	public static final float PPM = 32;
	public static final Vector2 GRAVITY = new Vector2(0, -36f);
	
	/* -= UI =- */
	public static final String BUTTON_PLAY = "button_play";
	public static final String BUTTON_PLAY_PATH = "ui/play.png";
	public static final String BUTTON_BACK = "button_back";
	public static final String BUTTON_BACK_PATH = "ui/back.png";
	public static final String BUTTON_RETRY = "button_retry";
	public static final String BUTTON_RETRY_PATH = "ui/retry.png";
	
	public static final String BUTTON_OPTIONS = "button_options";
	public static final String BUTTON_OPTIONS_PATH = "ui/options.png";
	public static final String BUTTON_STATS = "button_stats";
	public static final String BUTTON_STATS_PATH = "ui/stats.png";
	public static final String BUTTON_INFO = "button_info";
	public static final String BUTTON_INFO_PATH = "ui/info.png";
	public static final String BUTTON_LEADERBOARD = "button_leaderboard";
	public static final String BUTTON_LEADERBOARD_PATH = "ui/leaderboard.png";
	
	public static final String BUTTON_MUSIC_ON = "button_musicON";
	public static final String BUTTON_MUSIC_ON_PATH = "ui/musicON.png";
	public static final String BUTTON_MUSIC_OFF = "button_musicOFF";
	public static final String BUTTON_MUSIC_OFF_PATH = "ui/musicOFF.png";
	public static final String BUTTON_SOUND_ON = "button_soundON";
	public static final String BUTTON_SOUND_ON_PATH = "ui/soundON.png";
	public static final String BUTTON_SOUND_OFF = "button_soundOFF";
	public static final String BUTTON_SOUND_OFF_PATH = "ui/soundOFF.png";
	
	public static final String ICON_TAP = "icon_tap";
	public static final String ICON_TAP_PATH = "ui/tap.png";
	public static final String ICON_HOLD = "icon_hold";
	public static final String ICON_HOLD_PATH = "ui/hold.png";
	public static final String ICON_DRAG = "icon_drag";
	public static final String ICON_DRAG_PATH = "ui/drag.png";
	public static final String ICON_DRAGHORIZONTAL = "icon_draghorizontal";
	public static final String ICON_DRAGHORIZONTAL_PATH = "ui/dragHorizontal.png";
	
	public static final String BORDER = "border";
	public static final String BORDER_PATH = "ui/border.png";
	
	/* -=TEXT=- */
	public static final String STRING_MENU_TITLE = "HYPERTASK";
	public static final String STRING_MENU_PLAY = "PLAY";
	public static final String STRING_MENU_PRACTICE = "CLASSIC";
	public static final String STRING_LOSE_TEXT = "GAME OVER";
	public static final String STRING_LEVEL_UP = "LEVEL UP!";
	public static final String FONT_LARGE = "font_large";
	public static final String FONT_MEDIUM = "font_medium";
	public static final String FONT_SMALL = "font_small";
	public static final int FONT_SIZE_LARGE = 128;
	public static final int FONT_SIZE_MEDIUM = 64;
	public static final int FONT_SIZE_SMALL = 42;
	
	/* -=MUSIC AND SOUNDS=- */
	public static final String MUSIC_MENU = "music_menu";
	public static final String MUSIC_MENU_PATH = "music/menu.ogg";
	public static final String MUSIC_PLAY = "music_play";
	public static final String MUSIC_PLAY_PATH = "music/Hyperbola.mp3";
	public static final String SOUND_SCORE = "sound_score";
	public static final String SOUND_SCORE_PATH = "sounds/score.wav";
	public static final String SOUND_CLICK = "sound_click";
	public static final String SOUND_CLICK_PATH = "sounds/click.wav";
	public static final float SCORE_VOLUME = 0.2f;
	public static final float CLICK_VOLUME = 0.2f;
	
	public static final String[] TOUCH_PATHS = { "ui/tap.png", "ui/hold.png", "ui/drag.png", "ui/dragHorizontal.png" };
	
	/* -=MINIGAME=- */
	public static enum GameType { Catch, Squares, Fly, Tap, Timing, Climb, Juggle, Dodge, Fall, Match, Guard, Collect,
		                          Jump, Shoot, Pong, };
	public static final int NUM_GAMETYPES = GameType.values().length;
	public static final String[] GameNames = { "Catch", "Squares", "Fly", "Tap", "Timing", "Climb", "Juggle", "Dodge",
			"Fall", "Match", "Guard", "Collect", "Jump", "Shoot", "Pong", };
	public static final Color[] GameColors = {
			new Color(0xFF9696CC), // Catch   Red 1
			new Color(0xFF96A4CC), // Squares Red 2
			new Color(0xFF96B4CC), // Fly     Red 3
			new Color(0xFFD4A8CC), // Tap     Orange 1
			new Color(0xFFEF8ACC), // Timing  Yellow 1
			new Color(0xFFFF8ACC), // Climb   Yellow 2
			new Color(0x96FFB0CC), // Juggle  Green 1
			new Color(0x96FF96CC), // Dodge   Green 2
			new Color(0x96FFCBCC), // Fall    Green 3
			new Color(0x96E0FFCC), // Match   Blue 1
			new Color(0x96CAFFCC), // Guard   Blue 2
			new Color(0x96A9FFCC), // Collect Blue 3
			new Color(0xA796FFCC), // Jump    Purple 1
			new Color(0xD096FFCC), // Shoot   Purple 2
			new Color(0xFFA3FFCC), // Pong    Pink 1
	};
	public static final String[] GameSymbolPaths = {
			"symbols/catch.png",
			"symbols/squares.png",
			"symbols/fly.png",
			"symbols/tap.png",
			"symbols/timing.png",
			"symbols/climb.png",
			"symbols/juggle.png",
			"symbols/dodge.png",
			"symbols/fall.png",
			"symbols/match.png",
			"symbols/guard.png",
			"symbols/collect.png",
			"symbols/jump.png",
			"symbols/shoot.png",
			"symbols/pong.png",
	};
	
	// GOOGLE PLAY SERVICES
	public static final String ACH_LEVEL_1 = "CgkIuuqV9aYOEAIQAQ";
	public static final String ACH_LEVEL_2 = "CgkIuuqV9aYOEAIQAg";
	public static final String ACH_LEVEL_3 = "CgkIuuqV9aYOEAIQAw";
	public static final String ACH_LEVEL_4 = "CgkIuuqV9aYOEAIQAA";
	public static final String ACH_LEVEL_5 = "CgkIuuqV9aYOEAIQBw";
	public static final String ACH_GODLY = "CgkIuuqV9aYOEAIQBA";
	
	public static final int AD_INTERVAL = 3;
	
	
		/* -=TODO LIST=- */
	// GENERAL - Statistics line looks bad when numbers get bigger
	// LOSE    - Buttons, better graphics for scores, cooler high score animation
	//         - Maybe split it up into 3 screens, one of them has stats
	// GUARD   - Add an animation to the paddle when it moves?
	// ENDLESS - Keep a combo counter, keep as a statistic
	
		/* -=FEATURE IDEA LIST=- */
	// Achievements  - Medals for point milestones. Bronze = 10pts, Silver = 25pts, ...
	//               - "Games Learned - 4 / 25"
	
		/* -=NEW MINIGAME IDEAS=- */
	// HIDE            - Four squares red and green. Move to the green one. The red ones, after a delay, spawn enclosing walls. killing anything within.
	//                 - Name could use some work
	// PARACHUTE       - Triangle falls slowly from the top, must land on a platform at the bottom. Funky movement required.
	// BREAK / DESTROY
	// SOLVE           - Simple puzzle? What kind of puzzle though D:
	// REFLECT         - Crap, this is just pong I think..
	// COPY            - Simon says put the square in this corner, triangle in this corner..?
	// REPEAT
	// EAT             - Pacman, Snake, Agar???
	// CLEAN
	// TOPPLE          - A small pillar is on top of something. You need to shoot 3 balls at it to knock it down. (Problem - narrow screens..)
	// HOP
	// SPIN
	// FLAP
	// BOWLING
	// SLASH           - Maybe something similar to Fruit Ninja's pomegranate thingy.
	// CUT             - Probably hard to make but: Cuttable ropes spawn, that's all.
	// PULL
	// COUNT           - Number appears, must place that many into box.
	//                 - Timer goes down, then when numBox = numNeeded, start a timer.
	//                 - If numBox = numNeeded still, winGame (prevents just throwing everything to box)
	// FIND
	// DRAW / GESTURE  - Image of a gesture appears with time limit
	// RUN
	// UMBRELLA        - Hold umbrella shape overtop of moving target. "Rain" drops down and bounces off umbrella
	// KICK
	// SUPER HEXAGON
	// DRIVE           - Muffett (Undertale) fight (three tracks, incoming obstacles)
	// SCROLL
	// ZOOM
	// ORDER           - Three dark shapes are at the top in a certain order
	//                 - Three light shapes at bottom must be put in correct order before time runs out
	// STACK           - Oh boy. Probably more like the 2D arcade stacker game than the app. But how to lose? Time limits?
}
