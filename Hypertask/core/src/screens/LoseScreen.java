package screens;

import static com.phuctran.hypertask.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
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
import com.phuctran.hypertask.GameData;
import com.phuctran.hypertask.HyperTask;
import com.phuctran.hypertask.Constants.GameType;

import actions.LabelScaleToAction;
import objects.RectShape;

public class LoseScreen extends GameScreen {
	private static final float SYMBOL_SIZE = 64;
	private static final float SYMBOL_SPEED_MIN = 2;
	private static final float SYMBOL_SPEED_MAX = 4;
	private static final float SYMBOL_ROTATION_MIN = 0.5f;
	private static final float SYMBOL_ROTATION_MAX = 1f;
	private static final float SYMBOL_COUNT = 30;
	
	private final float RED_FADEIN_TIME = 0.15f;
	private final float RED_FADEOUT_TIME = 0.5f;
	private final float RED_PULSE_TIME = 1.5f;
	private final float FADEIN_TIME = 0.4f;
	private final float FADEOUT_TIME = 0.3f;
	private final float OVERLAY_ALPHA = 0.6f;
	
	private final float SCORE_Y = V_HEIGHT * 0.8f;
	private final float BEST_Y = V_HEIGHT * 0.7f;
	private final float BUTTON_Y = V_HEIGHT * 0.2f;
	private final float NEWBEST_Y = V_HEIGHT * 0.65f;
	private final float BG_HEIGHT = 180;
	private final float BG_WIDTH_OFFSET = 25;
	
	private Group scoreGroup, bestGroup, buttonGroup;
	private RectShape loserTint, overlay;
	private Rectangle losingBounds;
	private Label scoreLabel, bestLabel;//, bestNum;
	private Image retry, menu;

	private boolean isNewBest; // Decides the layout of the scores
	
	public LoseScreen(HyperTask game) {
		super(game);
		losingBounds = new Rectangle();
	}

	@Override
	public void show() {
		// Allow the user to back-key to menu
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(stage);
		
		constructStage();
		addOverlayAnimations();

		GameData.incrInt(GameData.KEY_AD_COUNTER);
		stage.addAction(Actions.sequence(Actions.delay(RED_FADEOUT_TIME), Actions.run(new Runnable() {
			@Override
			public void run() {
				if (GameData.getInt(GameData.KEY_AD_COUNTER, 0) >= AD_INTERVAL && HyperTask.adController.isWifiConnected()) {
					GameData.put(GameData.KEY_AD_COUNTER, 0);
					HyperTask.adController.showInterstitialAd(new Runnable() {
						@Override
						public void run() {
							addGroupAnimations();
						}
					});
				}
				else {
					addGroupAnimations();
				}
			}
		})));
	}
	
	private void addOverlayAnimations() {
		loserTint.addAction(Actions.sequence(Actions.alpha(0),
                Actions.alpha(0.8f, RED_FADEIN_TIME),
                Actions.fadeOut(RED_FADEOUT_TIME),
                Actions.delay(FADEIN_TIME),
                Actions.forever(Actions.sequence(Actions.alpha(0.7f, RED_PULSE_TIME),
               		                          Actions.fadeOut(RED_PULSE_TIME)))));
		// Screen darkens after the red flash
		overlay.addAction(Actions.sequence(Actions.color(Color.BLACK.cpy().mul(1,1,1,0)),
		              Actions.delay(RED_FADEOUT_TIME),
		              Actions.alpha(OVERLAY_ALPHA, FADEIN_TIME)));
	}
	
	private void addGroupAnimations() {
		// Groups fade in
		scoreGroup.addAction(Actions.sequence(Actions.alpha(0),
				Actions.delay(0.25f),
                Actions.alpha(COLOR_TEXT.a, FADEIN_TIME)));
		bestGroup.addAction(Actions.sequence(Actions.alpha(0),
				Actions.delay(0.5f),
                Actions.alpha(COLOR_TEXT.a, FADEIN_TIME)));
		buttonGroup.addAction(Actions.sequence(Actions.alpha(0),
		                     Actions.delay(0.75f),
		                     Actions.alpha(COLOR_TEXT.a, FADEIN_TIME)));
		
		if (isNewBest) {
			// Number zooms in
			LabelScaleToAction scaleUp = new LabelScaleToAction(scoreLabel, 2f, 0.2f);
			LabelScaleToAction scaleDown = new LabelScaleToAction(scoreLabel, 1f, 0.4f);
			LabelScaleToAction scaleUpForever = new LabelScaleToAction(scoreLabel, 1.2f, 0.1f);
			LabelScaleToAction scaleDownForever = new LabelScaleToAction(scoreLabel, 1f, 0.2f);
			scoreLabel.addAction(Actions.sequence(Actions.delay(RED_FADEIN_TIME + RED_FADEOUT_TIME + 0.25f),
					                           scaleUp,
					                           scaleDown,
					                           Actions.forever(Actions.sequence(Actions.delay(0.75f),
					                        		                            scaleUpForever,
					                        		                            scaleDownForever))));
		}
	}
	
	public void updateGameData(int score, int[] wins, GameType loss, Rectangle loseBounds) {
		// Update best
		if (score > GameData.getInt(GameData.KEY_BEST, 0)) {
			GameData.put(GameData.KEY_BEST, score);
			isNewBest = true;
		}
		
		// Update minigame wins/losses
		GameData.incrInt(GameData.KEY_GAMES_PLAYED);
		for (int i = 0; i < NUM_GAMETYPES; i++) {
			int val = wins[i];
			GameData.incrInt(GameType.values()[i].toString() + GameData.KEY_WINS, val);
		}
		GameData.incrInt(loss.toString() + GameData.KEY_LOSSES);
		
		// Update losingBounds
		losingBounds = loseBounds;
		
		updatePlayServices(score);
	}
	
	private void constructStage() {
		stage.clear();
		stage.getRoot().getColor().a = 1;
		
		// Add components
		addOverlays();
		addScore();
		if (isNewBest) {
			addNewBest();
		}
		else {
			addBest();
		}	
		addButtons();
	}
	
	@Override
	public void hide() {
		isNewBest = false;
		for (Actor a : stage.getActors()) {
			a.clearActions();
		}
	}
	
	@Override
	public void handleInput(float delta) {
		if (Gdx.input.isKeyPressed(Keys.BACK) && !stage.getRoot().hasActions()) {
			game.getPlayScreen().reset();
			game.setScreen(game.getMenuScreen());
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		stage.act(delta);
		overlay.act(delta);
		
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
		game.getPlayScreen().draw(batch);
		batch.setProjectionMatrix(HyperTask.camera.combined);
		
		batch.begin();
		overlay.draw(batch, 1);
		batch.end();
		
		stage.draw();
	}
	
	private void updatePlayServices(int score) {
		HyperTask.playServices.submitScore(score);
		
		// TODO: remove else statements
		if (score >= 100) {
			HyperTask.playServices.unlockAchievement(ACH_GODLY);
		}
		if (score >= PlayScreen.LEVEL_POINTS_NEEDED[4]) {
			HyperTask.playServices.unlockAchievement(ACH_LEVEL_5);
		}
		if (score >= PlayScreen.LEVEL_POINTS_NEEDED[3]) {
			HyperTask.playServices.unlockAchievement(ACH_LEVEL_4);
		}
		if (score >= PlayScreen.LEVEL_POINTS_NEEDED[2]) {
			HyperTask.playServices.unlockAchievement(ACH_LEVEL_3);
		}
		if (score >= PlayScreen.LEVEL_POINTS_NEEDED[1]) {
			HyperTask.playServices.unlockAchievement(ACH_LEVEL_2);
		}
		if (score >= PlayScreen.LEVEL_POINTS_NEEDED[0]) {
			HyperTask.playServices.unlockAchievement(ACH_LEVEL_1);
		}
	}
	
	private void addOverlays() {
		// Red losing tint over the losing game
		loserTint = new RectShape();
		loserTint.setColor(Color.RED.cpy().mul(1,1,1,0));
		loserTint.setBounds(losingBounds.x, losingBounds.y, losingBounds.width, losingBounds.height);
		stage.addActor(loserTint);
		
		// Dark overlay over the entire screen
		overlay = new RectShape();
		overlay.setBounds(0,0,V_WIDTH,V_HEIGHT);
		overlay.setColor(Color.BLACK.cpy().mul(1,1,1,0));
		//stage.addActor(overlay);
	}
	
	private void addScore() {
		scoreGroup = new Group();
		scoreGroup.getColor().a = 0;
		
		// Create the label
		scoreLabel = new Label(Integer.toString(game.getPlayScreen().getScore()),
				new Label.LabelStyle(HyperTask.res.getFont(FONT_LARGE), COLOR_TEXT));
		scoreLabel.setAlignment(Align.center);
		scoreLabel.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.82f, Align.center);
		
		// Add the components
		scoreGroup.addActor(scoreLabel);
		stage.addActor(scoreGroup);
	}
	
	private void addBest() {
		bestGroup = new Group();
		bestGroup.getColor().a = 0;
		
		// Create the label
		bestLabel = new Label("BEST " + GameData.getInt(GameData.KEY_BEST, 0), new Label.LabelStyle(HyperTask.res.getFont(FONT_MEDIUM), COLOR_TEXT));
		bestLabel.setAlignment(Align.center);
		bestLabel.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.7f, Align.center);

		// Add the components
		bestGroup.addActor(bestLabel);
		stage.addActor(bestGroup);
	}
	
	private void addNewBest() {
		bestGroup = new Group();
		bestGroup.getColor().a = 0;

		// Create the labels
		bestLabel = new Label("NEW BEST!", new Label.LabelStyle(HyperTask.res.getFont(FONT_MEDIUM), COLOR_TEXT));
		bestLabel.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.7f, Align.center);

		// Add the components
		bestGroup.addActor(bestLabel);
		stage.addActor(bestGroup);
	}
	
	private void addButtons() {
		buttonGroup = new Group();
		buttonGroup.getColor().a = 0;
		
		// Retry button
		retry = new Image(HyperTask.res.getTexture(BUTTON_RETRY));
		retry.setSize(200, 200);
		retry.getColor().a = COLOR_TEXT.a;
		retry.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.47f, Align.center);
		retry.addListener(new ClickListener() {
			@Override
			public void  clicked(InputEvent event, float x, float y) {
				if (!buttonGroup.hasActions()) {
					stage.addAction(Actions.sequence(Actions.fadeOut(FADEOUT_TIME), Actions.delay(0.2f), Actions.run(new Runnable() {
						@Override
						public void run() {
							game.getPlayScreen().reset();
							game.setScreen(game.getPlayScreen());
						}
					})));
					overlay.clearActions();
					overlay.addAction(Actions.sequence(Actions.delay(0.2f),
							                           Actions.parallel(Actions.color(COLOR_MAIN, FADEOUT_TIME),
							                        		            Actions.fadeIn(FADEOUT_TIME))));
				}
			}});
		
		// Menu button
		menu = new Image(HyperTask.res.getTexture(BUTTON_BACK));
		menu.getColor().a = COLOR_TEXT.a;
		menu.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.22f, Align.center);
		menu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!buttonGroup.hasActions()) {
					AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
					game.getLastScreen().reset();
					game.setScreen(game.getMenuScreen());
				}
			}});
		
		// Add the components
		buttonGroup.addActor(retry);
		buttonGroup.addActor(menu);
		stage.addActor(buttonGroup);
	}
}
