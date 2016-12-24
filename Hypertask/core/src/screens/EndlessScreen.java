package screens;

import static com.phuctran.hypertask.Constants.BOUNDS_LEFT;
import static com.phuctran.hypertask.Constants.BOUNDS_MIDDLE;
import static com.phuctran.hypertask.Constants.BOUNDS_RIGHT;
import static com.phuctran.hypertask.Constants.COLOR_MAIN;
import static com.phuctran.hypertask.Constants.MUSIC_PLAY;
import static com.phuctran.hypertask.Constants.V_HEIGHT;
import static com.phuctran.hypertask.Constants.V_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.phuctran.hypertask.AudioManager;
import com.phuctran.hypertask.HyperTask;

import containers.PlayContainer;
import objects.RectShape;

public class EndlessScreen extends GameScreen {
	public static final float LEFT_DELAY = 7f;
	public static final float RIGHT_DELAY = 6.85f;
	
	private RectShape background;
	private PlayContainer[] games;
	
	public EndlessScreen(HyperTask game) {
		super(game);
		
		addBackground();
		addContainers();
		reset();
	}
	
	@Override
	public void reset() {
		// Configure music
		AudioManager.instance().stop();
		HyperTask.res.getMusic(MUSIC_PLAY).setPosition(0.51f); // Sync to first roulette

		// Reset containers
		PlayContainer.lastTypes.clear();
		for (PlayContainer g : games) {
			g.reset();
		}
		
		stage.getRoot().clearActions();
		stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.3f)));
		background.clearActions();
		
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
		AudioManager.instance().play(HyperTask.res.getMusic(MUSIC_PLAY), true);
		
		HyperTask.input.clear();
		Gdx.input.setInputProcessor(HyperTask.input);
		Gdx.input.setCatchBackKey(true);
	}
	
	@Override
	public void handleInput(float delta) {
		if (Gdx.input.isKeyPressed(Keys.BACK))
			game.setScreen(game.getPauseScreen());
		
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
		
		// Update each game
		for (final PlayContainer g : games) {
			g.update(delta);
			if (g.hasWon) {
				g.spin();
			}
			else if (g.hasLost) {
				Color oldColor = new Color(g.getBackground().getColor());
				g.removeGame();
				g.cancel();
				g.getBackground().addAction(Actions.sequence(Actions.color(Color.RED, 0.15f), Actions.run(new Runnable() {
					@Override
					public void run() {
						if (g.getMinigame() == null)
							g.spin();
					}
				}), Actions.color(oldColor, 0.25f)));
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
		for (PlayContainer g : games) {
			g.draw(batch, parentAlpha);
		}
		
		batch.end();
		
		//HyperTask.dbRenderer.render(HyperTask.world, HyperTask.worldCamera.combined);
	}
	
	@Override
	public void hide() {
		AudioManager.instance().pause();
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
	
	private void addBackground() {
		background = new RectShape();
		background.setBounds(0,0,V_WIDTH,V_HEIGHT);
		background.setColor(COLOR_MAIN);
		stage.addActor(background);
	}
	
	private void addContainers() {
		games = new PlayContainer[3];
		games[0] = new PlayContainer(stage, BOUNDS_LEFT);
		games[1] = new PlayContainer(stage, BOUNDS_MIDDLE);
		games[2] = new PlayContainer(stage, BOUNDS_RIGHT);
	}
}
