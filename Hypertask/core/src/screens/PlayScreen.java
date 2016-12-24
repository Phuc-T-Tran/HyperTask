package screens;

import static com.phuctran.hypertask.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.AudioManager;
import com.phuctran.hypertask.GameData;
import com.phuctran.hypertask.HyperTask;

import actions.LabelScaleToAction;
import containers.PlayContainer;
import minigames.Minigame;
import objects.RectShape;

public class PlayScreen extends GameScreen {
	public static final float LEFT_DELAY = 13.85f;//7f;
	public static final float RIGHT_DELAY = 28.15f;
	public static final int[] LEVEL_POINTS_NEEDED = new int[] { 5, 10, 20, 40, 60 };
	
	private RectShape background;
	private PlayContainer[] games;
	
	private Group labelGroup;
	private Label scoreLabel, levelLabel;
	
	private int score;
	private int[] wins;
	private GameType loseType;
	
	public PlayScreen(HyperTask game) {
		super(game);
		
		addBackground();
		addLabels();
		addContainers();
		
		reset();
	}
	
	@Override
	public void reset() {
		// Reset level and score
		setLevel(0);
		setScore(0);
		wins = new int[NUM_GAMETYPES];
		loseType = null;
		
		// Reset containers
		PlayContainer.lastTypes.clear();
		for (PlayContainer g : games) {
			g.reset();
		}
		
		stage.clear();
		labelGroup.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.3f)));
		
		// Reset audio and sync to first roulette
		HyperTask.res.getMusic(MUSIC_PLAY).setPosition(0.51f);
		
		// Start spinning the middle game, and spin the others after a delay
		games[1].spin();
		stage.addAction(Actions.sequence(Actions.delay(LEFT_DELAY),
										 Actions.run(new Runnable() {
										 	 @Override
										 	 public void run() {
												 games[0].spin();
											 }
										 }),
										 Actions.delay(RIGHT_DELAY),
										 Actions.run(new Runnable() {
											 @Override
											 public void run() {
												 games[2].spin();
											 }
										 })));
	}
	
	@Override
	public void show() {
		AudioManager.instance().setMusicVolume(1f);
		AudioManager.instance().play(HyperTask.res.getMusic(MUSIC_PLAY));
		
		HyperTask.input.clear();
		Gdx.input.setInputProcessor(HyperTask.input);
		Gdx.input.setCatchBackKey(true);
		
		labelGroup.setVisible(true);
	}
	
	@Override
	public void handleInput(float delta) {
		if (Gdx.input.isKeyPressed(Keys.BACK)) {
			AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
			game.setScreen(game.getPauseScreen());
		}
		
		for (PlayContainer g : games) {
			g.handleInput(delta);
		}
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Skip silence at end of track and loop
		if (HyperTask.res.getMusic(MUSIC_PLAY).getPosition() > 421)
			HyperTask.res.getMusic(MUSIC_PLAY).setPosition(0f);
		
		HyperTask.world.step(delta, 6, 2);
		stage.act(delta);
		
		labelGroup.act(delta);
		background.act(delta);
		
		// Update each game
		for (final PlayContainer g : games) {
			
			GameType type = null;
			if (g.getMinigame() != null)
				type = g.getMinigame().getType();
			
			g.update(delta);
			if (g.hasWon) {
				wins[type.ordinal()]++;
				addPoint();
				g.spin();
			}
			else if (g.hasLost) {
				loseType = type;
				lose(g.getBounds());
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
		
		float parentAlpha = stage.getRoot().getColor().a;
		background.draw(batch, 1);
		stage.draw();
		for (PlayContainer g : games) {
			g.draw(batch, parentAlpha);
		}
		if (labelGroup.isVisible())
			labelGroup.draw(batch, parentAlpha);
		
		batch.end();
		
		//HyperTask.dbRenderer.render(HyperTask.world, HyperTask.worldCamera.combined);
	}
	
	public void addPoint() {		
		// Update score and play sound
		setScore(score + 1);
		AudioManager.instance().play(HyperTask.res.getSound(SOUND_SCORE), SCORE_VOLUME, MathUtils.random(1f, 1.2f), 0);
		
		// Check if we have leveled up
		for (int i = 0; i < Minigame.MAX_LEVEL; i++) {
			if (score == LEVEL_POINTS_NEEDED[i]) {
				levelUp();
				return;
			}
		}
		
		// We did not level up. Play scoring animation
		LabelScaleToAction scaleUp = new LabelScaleToAction(scoreLabel, 1.8f, 0.1f);
		LabelScaleToAction scaleDown = new LabelScaleToAction(scoreLabel, 1f, 0.1f);
		scoreLabel.addAction(Actions.sequence(scaleUp, scaleDown));
	}
	
	public void setScore(int score) {
		this.score = score;
		scoreLabel.setText(Integer.toString(score));
	}
	
	public void levelUp() {
		setLevel(Minigame.level + 1);
		
		// Flash animation
		scoreLabel.addAction(Actions.sequence(Actions.repeat(2, Actions.sequence(Actions.fadeOut(0.2f), Actions.fadeIn(0.2f))),
				                        Actions.delay(0.8f),
				                        Actions.run(new Runnable() {
											@Override
											public void run() {
												scoreLabel.setText(Integer.toString(score));
											}
										})));
	}
	
	public void setLevel(int level) {
		Minigame.setLevel(level);
		scoreLabel.setText(STRING_LEVEL_UP);
		updateLevelText();
	}
	
	public void lose(Rectangle bounds) {
		AudioManager.instance().stop();
		game.getLoseScreen().updateGameData(score, wins, loseType, bounds);
		game.setScreen(game.getLoseScreen());
	}
	
	@Override
	public void hide() {
		AudioManager.instance().pause();
		
		labelGroup.setVisible(false);
	}
	
	@Override
	public void pause() {
		game.setScreen(game.getPauseScreen());
	}

	@Override
	public void dispose() {
		for (PlayContainer g : games) {
			g.dispose();
		}
		super.dispose();
	}
	
	public int getScore() {
		return score;
	}
	
	private void addBackground() {
		background = new RectShape();
		background.setBounds(0,0,V_WIDTH,V_HEIGHT);
		background.setColor(COLOR_MAIN);
	}
	
	private void addContainers() {
		games = new PlayContainer[3];
		games[0] = new PlayContainer(stage, BOUNDS_LEFT);
		games[1] = new PlayContainer(stage, BOUNDS_MIDDLE);
		games[2] = new PlayContainer(stage, BOUNDS_RIGHT);
	}
	
	private void addLabels() {
		// Score Label
		scoreLabel = new Label(Integer.toString(score), new Label.LabelStyle(HyperTask.res.getFont(FONT_MEDIUM), COLOR_TEXT));
		scoreLabel.setPosition((V_WIDTH - scoreLabel.getWidth()) / 2, V_HEIGHT * 0.84f - scoreLabel.getHeight() / 2);
		scoreLabel.setAlignment(Align.center);
		
		// Level Label
		levelLabel = new Label("", new Label.LabelStyle(HyperTask.res.getFont(FONT_LARGE), COLOR_TEXT));
		levelLabel.setPosition((V_WIDTH - levelLabel.getWidth()) / 2, V_HEIGHT * 0.82f - levelLabel.getHeight() / 2);
		levelLabel.setAlignment(Align.center);
		updateLevelText();
		
		labelGroup = new Group();
		labelGroup.addActor(scoreLabel);
		labelGroup.addActor(levelLabel);
	}
	
	private void updateLevelText() {
		String dots = "";
		for (int i = 0; i < Minigame.level; i++) {
			dots += ".";
		}
		levelLabel.setText(dots);
	}
}
