package com.phuctran.hypertask;

import static com.phuctran.hypertask.Constants.GRAVITY;
import static com.phuctran.hypertask.Constants.MUSIC_MENU;
import static com.phuctran.hypertask.Constants.MUSIC_MENU_PATH;
import static com.phuctran.hypertask.Constants.MUSIC_PLAY;
import static com.phuctran.hypertask.Constants.MUSIC_PLAY_PATH;
import static com.phuctran.hypertask.Constants.PPM;
import static com.phuctran.hypertask.Constants.SOUND_SCORE;
import static com.phuctran.hypertask.Constants.SOUND_SCORE_PATH;
import static com.phuctran.hypertask.Constants.V_HEIGHT;
import static com.phuctran.hypertask.Constants.V_WIDTH;

import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import google.ActionResolver;
import google.AdController;
import google.PlayServices;
import screens.EndlessScreen;
import screens.GameScreen;
import screens.LoseScreen;
import screens.MenuScreen;
import screens.PauseScreen;
import screens.PlayScreen;
import screens.PracticeScreen;

public class HyperTask extends Game {
	// Input
	public static HyperInput input;
	
	// Cameras and Views
	public static OrthographicCamera camera;
	public static OrthographicCamera worldCamera;
	public static Viewport view;
	
	// Graphics
	public static Resources     res;
	public static SpriteBatch   batch;
	public static ShapeRenderer shapes;
	
	// Box2D World
	public static World              world;
	public static Box2DDebugRenderer dbRenderer;
	
	// Game Screens
	private MenuScreen  menu;
	private PlayScreen  play;
	private PracticeScreen practice;
	private PauseScreen pause;
	private LoseScreen  lose;
	private EndlessScreen  endless;
	private GameScreen lastScreen;
	
	public static AdController adController;
	public static PlayServices playServices;
	
	public HyperTask(AdController adController, PlayServices playServices) {
		this.adController = adController;
		this.playServices = playServices;
	}
	
	@Override
	public void create() {
		// Input
		input = new HyperInput();
		
		// Cameras and Views
		camera = new OrthographicCamera();
		camera.setToOrtho(false, V_WIDTH, V_HEIGHT);
		camera.update();
		worldCamera = new OrthographicCamera();
		worldCamera.setToOrtho(false, V_WIDTH / PPM, V_HEIGHT / PPM);
		worldCamera.update();
		view = new StretchViewport(V_WIDTH, V_HEIGHT, camera);
		
		// Graphics
		res    = new Resources();
		batch  = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
		shapes = new ShapeRenderer();
		shapes.setAutoShapeType(true);
		shapes.setProjectionMatrix(camera.combined);
		
		// Box2D World
		world = new World(GRAVITY, true);
		world.setContactListener(new WorldContactListener());
		dbRenderer = new Box2DDebugRenderer();
		
		// Audio
		AudioManager audio = AudioManager.instance();
		audio.setMusicEnabled(GameData.getBoolean(GameData.KEY_MUSIC, true));
		audio.setSoundEnabled(GameData.getBoolean(GameData.KEY_SOUND, true));
		
		// Game Screens
		menu  = new MenuScreen(this);
		play  = new PlayScreen(this);
		pause = new PauseScreen(this);
		practice = new PracticeScreen(this);
		lose  = new LoseScreen(this);
		endless  = new EndlessScreen(this);
		
		// TODO: Simply setting the screen causes lag, so using timer for now
		Timer.instance().scheduleTask(new Task() {
			@Override
			public void run() {
				setScreen(menu);
			}}, 0.1f);
		//setScreen(menu);
	}

	@Override
	public void dispose() {		
		if (play != null)
			play.dispose();
		if (menu != null)
			menu.dispose();
		if (pause != null)
			pause.dispose();
		if (lose != null)
			lose.dispose();
		if (practice != null)
			practice.dispose();
		if (endless != null)
			endless.dispose();
		
		if (world != null)
			world.dispose();
		if (dbRenderer != null)
			dbRenderer.dispose();
		
		if (res != null)
			res.dispose();
		if (batch != null)
			batch.dispose();
		if (shapes != null)
			shapes.dispose();
	}
	
	public MenuScreen getMenuScreen() { return menu; }
	public PlayScreen getPlayScreen() { return play; }	
	public PauseScreen getPauseScreen() { return pause; }	
	public PracticeScreen getPracticeScreen() { return practice; }	
	public LoseScreen getLoseScreen() { return lose; }	
	public EndlessScreen getEndlessScreen() { return endless; }
	public GameScreen getLastScreen() { return lastScreen; }
	
	@Override
	public void setScreen(Screen screen) {
		lastScreen = (GameScreen)getScreen();
		super.setScreen(screen);
	}
}
