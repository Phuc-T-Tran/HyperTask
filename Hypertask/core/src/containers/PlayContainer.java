package containers;

import static com.phuctran.hypertask.Constants.COLOR_TEXT;
import static com.phuctran.hypertask.Constants.FONT_MEDIUM;
import static com.phuctran.hypertask.Constants.GameColors;
import static com.phuctran.hypertask.Constants.GameNames;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.GameData;
import com.phuctran.hypertask.HyperTask;

import actions.LabelScaleToAction;

public class PlayContainer extends GameContainer {
	public static final float SPIN_TIME = 1.2f;
	public static ArrayList<GameType> lastTypes = new ArrayList<GameType>();
	
	private Stage stage;
	private SymbolRoulette roulette;
	private Label gameText;
	private float time;
	
	public PlayContainer(Stage stage, Rectangle bounds) {
		super(bounds);
		this.stage = stage;
		
		//this.screen = screen;
		roulette = new SymbolRoulette(bounds);
		initGameText();
		
		time = 0;
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		roulette.update(delta);
		if (roulette.spinning()) {
			time += delta;
			
			if (time >= SPIN_TIME) {
				time = 0;
				final GameType type = roulette.stop();
				
				background.addAction(Actions.color(GameColors[type.ordinal()], BG_FADEIN));
				Timer.schedule(new Task() {
					@Override
					public void run() {
						setGame(HyperTask.world, type);
					}
				}, BG_FADEIN / 2);
				
				Timer.schedule(new Task() {
					@Override
					public void run() {
						flashGameText(GameNames[type.ordinal()]);
						if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_ALWAYS_ON)) {
							// Tutorials always on, add tutorial
							minigame.addTutorial(stage);
						}
						else if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_ON)) {
							// Tutorials on. Check if done this tutorial, and if not, add it and update value
							if (!GameData.getBoolean(type.toString() + GameData.KEY_TUTORIAL_DONE, false)) {
								minigame.addTutorial(stage);
								GameData.put(type.toString() + GameData.KEY_TUTORIAL_DONE, true);
							}
						}
					}
				}, BG_FADEIN);
			}
		}
		
		gameText.act(delta);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		background.draw(batch, parentAlpha);
		roulette.draw(batch, parentAlpha);
		
		if (minigame != null)
			minigame.draw(batch, parentAlpha);
		
		gameText.draw(batch, parentAlpha);
	}
	
	@Override
	protected void winGame() {
		removeGame();
		
		// Flash animation
		background.addAction(Actions.sequence(Actions.color(Color.WHITE, 0.15f),
				  							  Actions.color(background.getColor().cpy(), BG_FADEOUT)));
		
		hasWon = true;
	}
	
	@Override
	protected void loseGame() {
		hasLost = true;
	}
	
	public void spin() {
		hasWon = false;
		hasLost = false;
		roulette.start();
		time = 0;
	}
	
	@Override
	public void reset() {
		super.reset();
		roulette.cancel();
		gameText.clearActions();
		gameText.getColor().a = 0;
	}
	
	public void cancel() {
		roulette.cancel();
	}
	
	private void flashGameText(String text) {
		gameText.setText(text);
		gameText.setFontScale(1.6f);
		
		LabelScaleToAction scaleDown = new LabelScaleToAction(gameText, 1f, 0.2f);
		gameText.addAction(Actions.sequence(Actions.parallel(scaleDown, Actions.alpha(COLOR_TEXT.a, 0.2f)), Actions.delay(0.3f), Actions.fadeOut(0.5f)));
	}
	
	private void initGameText() {				
		// Initialize score label
		BitmapFont font = HyperTask.res.getFont(FONT_MEDIUM);
		gameText = new Label("", new Label.LabelStyle(font, COLOR_TEXT));
		gameText.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.32f, Align.center);
		gameText.setAlignment(Align.center);
		gameText.getColor().a = 0;
	}
}
