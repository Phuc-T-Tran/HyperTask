package screens;

import static com.phuctran.hypertask.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.AudioManager;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.HyperInput;
import com.phuctran.hypertask.HyperTask;

import actions.LabelScaleToAction;
import minigames.Minigame;
import objects.RectShape;
import objects.TriangleShape;
import ui.CreditsWindow;
import ui.LevelSelector;
import ui.OptionsWindow;
import ui.StatsWindow;

public class MenuScreen extends GameScreen {
	private static final float MENU_FADEIN_TIME = 0.4f;
	private static final float MENU_FADEOUT_TIME = 0.3f;
	
	private static final float SYMBOL_SIZE = 64;
	private static final float SYMBOL_SPEED_MIN = 2;
	private static final float SYMBOL_SPEED_MAX = 4;
	private static final float SYMBOL_ROTATION_MIN = 0.5f;
	private static final float SYMBOL_ROTATION_MAX = 1f;
	private static final float SYMBOL_COUNT = 80;
	
	// Game Mode
	public static enum GameMode { Classic, Endless, Practice};
	private GameMode mode;
	
	// Background
	private RectShape background;
	
	// Title and Menu
	private Label title, modeLabel;
	private Image options, stats, info, leaderboard;
	private TriangleShape leftArrow, rightArrow, play;
	private Group optionsWindow, statsWindow, creditsWindow;
	private LevelSelector levels;
	private Group mainGroup, buttonGroup;
	
	public MenuScreen(HyperTask game) {
		super(game);
		
		// Construct the menu
		addBackground();
		addSymbols();
		addMainGroup();
		addButtonGroup();
		addWindows();
		
		// TODO: Load the last used mode from game data
		mode = GameMode.Classic;
		modeLabel.setText(mode.toString().toUpperCase());
	}

	@Override
	public void show() {
		// Allow the user to back-key out of the game
		Gdx.input.setCatchBackKey(false);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, HyperTask.input));	
		
		// Fade in all actors
		stage.addAction(Actions.sequence(Actions.alpha(0), Actions.alpha(1f, MENU_FADEIN_TIME)));
		
		AudioManager.instance().setMusicVolume(0.5f);
		AudioManager.instance().play(HyperTask.res.getMusic(MUSIC_MENU), true);

		Minigame.setLevel(0);
	}
	
	@Override
	public void handleInput(float delta) {
		// The hacks are real..
		if (optionsWindow.isVisible()) {
			Rectangle bounds = new Rectangle(BOUNDS_LEFT.x + BOUNDS_LEFT.width / 2 + 26, BOUNDS_MIDDLE.y + 106, BOUNDS_RIGHT.x - 26 * 2, BOUNDS_MIDDLE.height - 106 * 2);
			if (HyperInput.justTouched() && !bounds.contains(HyperInput.getTouchPos())) {
				optionsWindow.addAction(Actions.sequence(Actions.fadeOut(0.2f), Actions.visible(false)));
				mainGroup.setVisible(true);
				buttonGroup.setVisible(true);
			}
		}
		else if (statsWindow.isVisible()) {
			Rectangle bounds = new Rectangle(BOUNDS_LEFT.x + BOUNDS_LEFT.width / 2 + 26, BOUNDS_MIDDLE.y + 106, BOUNDS_RIGHT.x - 26 * 2, BOUNDS_MIDDLE.height - 106 * 2);
			if (HyperInput.justTouched() && !bounds.contains(HyperInput.getTouchPos())) {
				statsWindow.addAction(Actions.sequence(Actions.fadeOut(0.2f), Actions.visible(false)));
				mainGroup.setVisible(true);
				buttonGroup.setVisible(true);
			}
		}
		else if (creditsWindow.isVisible()) {
			Rectangle bounds = new Rectangle(BOUNDS_LEFT.x + BOUNDS_LEFT.width / 2 + 26, BOUNDS_MIDDLE.y + 106, BOUNDS_RIGHT.x - 26 * 2, BOUNDS_MIDDLE.height - 106 * 2);
			if (HyperInput.justTouched() && !bounds.contains(HyperInput.getTouchPos())) {
				creditsWindow.addAction(Actions.sequence(Actions.fadeOut(0.2f), Actions.visible(false)));
				mainGroup.setVisible(true);
				buttonGroup.setVisible(true);
			}
		}
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		HyperTask.world.step(delta, 6, 2);
		stage.act(delta);
		
		// Move anything that has fallen off screen back to the top
		for (Actor a : stage.getActors()) {
			if (a.getY() + a.getHeight() < 0) {
				a.setPosition(MathUtils.random(V_WIDTH), V_HEIGHT + a.getHeight());
			}
		}
	}

	@Override
	public void render(float delta) {		
		handleInput(delta);
		update(delta);
		
		clearScreen();
		draw(HyperTask.batch);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.setProjectionMatrix(HyperTask.camera.combined);
		batch.begin();
		background.draw(batch, 1);
		batch.end();
		stage.draw();
		
		//HyperTask.dbRenderer.render(HyperTask.world, HyperTask.worldCamera.combined);
	}

	@Override
	public void hide() {
		AudioManager.instance().pause();
	}
	
	// This could be public, but there is no need right now.
	private void setGameMode(GameMode mode) {
		this.mode = mode;
		modeLabel.setText(mode.toString().toUpperCase());
		
		LabelScaleToAction scaleUp = new LabelScaleToAction(modeLabel, 1.2f, 0.1f);
		LabelScaleToAction scaleDown = new LabelScaleToAction(modeLabel, 1f, 0.1f);
		modeLabel.addAction(Actions.sequence(scaleUp, scaleDown));
	}
	
	private void addBackground() {
		background = new RectShape();
		background.setBounds(0,0,V_WIDTH,V_HEIGHT);
		background.setColor(COLOR_MAIN);
	}
	
	private void addSymbols() {
		// Randomly spawn symbols
		for (int i = 0; i < SYMBOL_COUNT; i++) {
			GameType type = GameType.values()[i % NUM_GAMETYPES];
			
			Image symbol = new Image(HyperTask.res.getTexture(GameNames[type.ordinal()]));
			symbol.setSize(SYMBOL_SIZE, SYMBOL_SIZE);
			symbol.setPosition(MathUtils.random(V_WIDTH), MathUtils.random(V_HEIGHT));
			symbol.setColor(GameColors[type.ordinal()]);
			symbol.setOrigin(Align.center); // Not rotating around the center is actually kind of nice.
			
			// Apply some randomization to the symbol
			float scaling = MathUtils.random(0.3f, 1f);
			float speed = MathUtils.random(SYMBOL_SPEED_MIN, SYMBOL_SPEED_MAX) * scaling;
			float rotation = MathUtils.random(SYMBOL_ROTATION_MIN, SYMBOL_ROTATION_MAX) * scaling * MathUtils.randomSign();
			symbol.setScale(scaling);
			symbol.getColor().a = scaling;
			symbol.addAction(Actions.forever(Actions.parallel(Actions.moveBy(0, -speed), Actions.rotateBy(rotation))));

			stage.addActor(symbol);
		}
	}
	
	private void addMainGroup() {
		mainGroup = new Group();
		
		addTitle();
		addPlayButton();
		addModeSelector();
		
		// addLevelSelector
		levels = new LevelSelector();
		levels.setSize(550, 50);
		levels.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.08f, Align.center);
		levels.setVisible(false);
		mainGroup.addActor(levels);
		
		stage.addActor(mainGroup);
	}
	
	private void addTitle() {
		title = new Label(STRING_MENU_TITLE, new Label.LabelStyle(HyperTask.res.getFont(FONT_LARGE), COLOR_TEXT));
		title.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.8f, Align.center);
		mainGroup.addActor(title);
	}
	
	private void addPlayButton() {
		play = new TriangleShape();
		play.setSize(148, 148);
		play.setOrigin(play.getWidth() / 2, play.getHeight() / 2);
		play.rotateBy(270);
		play.setColor(COLOR_TEXT);
		play.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.5f, Align.center);
		play.addListener(new ClickListener() {
		@Override
		public void  clicked(InputEvent event, float x, float y) {
			if (!stage.getRoot().hasActions()) {
				stage.getRoot().addAction(Actions.sequence(Actions.fadeOut(MENU_FADEOUT_TIME), Actions.run(new Runnable() {
					@Override
					public void run() {
						switch(mode) {
						case Classic:
							HyperTask.res.getMusic(MUSIC_MENU).setPosition(0);
							game.getPlayScreen().reset();
							game.setScreen(game.getPlayScreen());
							break;
						case Endless:
							HyperTask.res.getMusic(MUSIC_MENU).setPosition(0);
							game.getEndlessScreen().reset();
							game.setScreen(game.getEndlessScreen());
							break;
						case Practice:
							game.setScreen(game.getPracticeScreen());
							break;
						}
					}
				})));
			}
		}});
		mainGroup.addActor(play);
	}
	
	private void addModeSelector() {
		BitmapFont fontMedium = HyperTask.res.getFont(FONT_MEDIUM);
		
		// Mode Label
		modeLabel = new Label(STRING_MENU_PRACTICE, new Label.LabelStyle(fontMedium, COLOR_TEXT));
		modeLabel.setAlignment(Align.center);
		modeLabel.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.2f, Align.center);
		mainGroup.addActor(modeLabel);
		
		// Left and right buttons
		float sizeMult = 0.6f;
		
		leftArrow = new TriangleShape();
		leftArrow.setSize(modeLabel.getHeight() * sizeMult, modeLabel.getHeight() * sizeMult);
		leftArrow.setOrigin(leftArrow.getWidth() / 2, leftArrow.getHeight() / 2);
		leftArrow.rotateBy(90);
		leftArrow.setColor(COLOR_TEXT);
		leftArrow.setPosition(modeLabel.getX() - leftArrow.getWidth() * 1.75f, V_HEIGHT * 0.20f, Align.center);
		leftArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y){
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				leftArrow.addAction(Actions.sequence(Actions.scaleTo(1.4f, 1.4f, 0.1f), Actions.scaleTo(1f, 1f, 0.1f)));
				
				int modeNum = mode.ordinal() - 1;
				if (modeNum < 0)
					modeNum = GameMode.values().length - 1;
				
				setGameMode(GameMode.values()[modeNum]);
				if (GameMode.values()[modeNum] == GameMode.Endless)
					levels.setVisible(true);
				else
					levels.setVisible(false);
			}
		});
		mainGroup.addActor(leftArrow);
		
		rightArrow = new TriangleShape();
		rightArrow.setSize(modeLabel.getHeight() * sizeMult, modeLabel.getHeight() * sizeMult);
		rightArrow.setOrigin(rightArrow.getWidth() / 2, rightArrow.getHeight() / 2);
		rightArrow.rotateBy(270);
		rightArrow.setColor(COLOR_TEXT);
		rightArrow.setPosition(modeLabel.getX() + modeLabel.getWidth() + rightArrow.getWidth() * 1.75f, V_HEIGHT * 0.20f, Align.center);
		rightArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y){
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				rightArrow.addAction(Actions.sequence(Actions.scaleTo(1.4f, 1.4f, 0.1f), Actions.scaleTo(1f, 1f, 0.1f)));
				
				int modeNum = mode.ordinal() + 1;
				if (modeNum > GameMode.values().length - 1)
					modeNum = 0;
				
				setGameMode(GameMode.values()[modeNum]);
				if (GameMode.values()[modeNum] == GameMode.Endless)
					levels.setVisible(true);
				else
					levels.setVisible(false);
			}
		});
		mainGroup.addActor(rightArrow);
	}
	
	private void addButtonGroup() {
		buttonGroup = new Group();
		
		// Options Button
		options = new Image(HyperTask.res.getTexture(BUTTON_OPTIONS));
		options.setColor(COLOR_TEXT);
		options.setSize(64, 64);
		options.setPosition(V_WIDTH * 0.95f, V_HEIGHT * 0.08f, Align.center);
		options.addListener(new ClickListener() {
			@Override
			public void  clicked(InputEvent event, float x, float y) {
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				optionsWindow.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.2f)));
				mainGroup.setVisible(false);
				buttonGroup.setVisible(false);
			}
		});
		buttonGroup.addActor(options);
		
		// Stats Button
		stats = new Image(HyperTask.res.getTexture(BUTTON_STATS));
		stats.setColor(COLOR_TEXT);
		stats.setSize(64, 64);
		stats.setPosition(V_WIDTH * 0.875f, V_HEIGHT * 0.08f, Align.center);
		stats.addListener(new ClickListener() {
			@Override
			public void  clicked(InputEvent event, float x, float y) {
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				statsWindow.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.2f)));
				mainGroup.setVisible(false);
				buttonGroup.setVisible(false);
			}
		});
		buttonGroup.addActor(stats);
		
		// Info Button
		info = new Image(HyperTask.res.getTexture(BUTTON_INFO));
		info.setColor(COLOR_TEXT);
		info.setSize(64, 64);
		info.setPosition(V_WIDTH * 0.05f, V_HEIGHT * 0.08f, Align.center);
		info.addListener(new ClickListener() {
			@Override
			public void  clicked(InputEvent event, float x, float y) {
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				//creditsWindow.addAction(Actions.sequence(Actions.visible(true), Actions.fadeIn(0.2f)));
				mainGroup.setVisible(false);
				buttonGroup.setVisible(false);
			}
		});
		buttonGroup.addActor(info);
		
		// Leaderboard Button
		leaderboard = new Image(HyperTask.res.getTexture(BUTTON_LEADERBOARD));
		leaderboard.setColor(COLOR_TEXT);
		leaderboard.setSize(64, 64);
		leaderboard.setPosition(V_WIDTH * 0.125f, V_HEIGHT * 0.08f, Align.center);
		leaderboard.addListener(new ClickListener() {
			@Override
			public void  clicked(InputEvent event, float x, float y) {
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				HyperTask.playServices.showLeaderboard();
			}
		});
		buttonGroup.addActor(leaderboard);
		
		stage.addActor(buttonGroup);
	}
	
	private void addWindows() {
		optionsWindow = new OptionsWindow();
		optionsWindow.setVisible(false);
		stage.addActor(optionsWindow);
		
		statsWindow = new StatsWindow();
		statsWindow.setVisible(false);
		stage.addActor(statsWindow);
		
		creditsWindow = new CreditsWindow();
		creditsWindow.setVisible(false);
		stage.addActor(creditsWindow);
	}
}
