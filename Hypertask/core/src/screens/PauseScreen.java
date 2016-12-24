package screens;

import static com.phuctran.hypertask.Constants.BUTTON_BACK;
import static com.phuctran.hypertask.Constants.CLICK_VOLUME;
import static com.phuctran.hypertask.Constants.COLOR_TEXT;
import static com.phuctran.hypertask.Constants.FONT_LARGE;
import static com.phuctran.hypertask.Constants.SOUND_CLICK;
import static com.phuctran.hypertask.Constants.V_HEIGHT;
import static com.phuctran.hypertask.Constants.V_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.AudioManager;
import com.phuctran.hypertask.HyperTask;

import objects.RectShape;
import objects.TriangleShape;

public class PauseScreen extends GameScreen {
	private RectShape background;
	private Label pausedText;
	
	public PauseScreen(HyperTask game) {
		super(game);
		addBackground();
		addText();
		addButtons();
	}

	@Override
	public void show() {
		// Allow the user to back-key to menu
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(stage);
		
		// Disallow screen switching for 0.5f seconds
		stage.addAction(Actions.delay(0.5f));
	}
	
	@Override
	public void handleInput(float delta) {
//		if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
//			if (!stage.getRoot().hasActions()) {
//				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
//				game.getLastScreen().reset();
//				game.setScreen(game.getMenuScreen());
//			}
//		}
	}

	@Override
	public void update(float delta) {
		stage.act(delta);
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
		//Draw on top of the PlayScreen
		game.getLastScreen().draw(batch);
		stage.draw();
	}
	
	private void addBackground() {
		background = new RectShape();
		background.setBounds(0, 0, V_WIDTH, V_HEIGHT);
		background.setColor(Color.BLACK.cpy());
		background.getColor().a = 0.5f;
		stage.addActor(background);
	}
	
	private void addText() {
		BitmapFont font = HyperTask.res.getFont(FONT_LARGE);
		pausedText = new Label("PAUSED", new Label.LabelStyle(font, COLOR_TEXT));
		pausedText.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.8f, Align.center);
		stage.addActor(pausedText);
	}
	
	private void addButtons() {
		TriangleShape play = new TriangleShape();
		play.setRotation(270);
		play.setSize(148, 148);
		play.setOrigin(play.getWidth() / 2, play.getHeight() / 2);
		play.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.5f, Align.center);
		play.getColor().a = COLOR_TEXT.a;
		play.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!stage.getRoot().hasActions()) {
					game.setScreen(game.getLastScreen());
				}
			}});
		stage.addActor(play);
		
		Image menu = new Image(HyperTask.res.getTexture(BUTTON_BACK));
		menu.getColor().a = COLOR_TEXT.a;
		menu.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.2f, Align.center);
		menu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!stage.getRoot().hasActions()) {
					AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
					game.getLastScreen().reset();
					game.setScreen(game.getMenuScreen());
				}
			}
		});
		stage.addActor(menu);
	}
}
