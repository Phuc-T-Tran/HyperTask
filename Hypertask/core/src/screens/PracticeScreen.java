package screens;

import static com.phuctran.hypertask.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.AudioManager;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.HyperTask;

import containers.PracticeContainer;
import minigames.Minigame;
import objects.RectShape;
import ui.LevelSelector;
import ui.PracticeScrollPanel;

public class PracticeScreen extends GameScreen {
	public static final float FADEIN_TIME = 0.35f;
	private RectShape background;
	private PracticeScrollPanel scrollPanel;
	private Label leftTitle, rightTitle;
	private PracticeContainer container;
	private Image icon;
	private GameType currentType;
	
	public PracticeScreen(HyperTask game) {
		super(game);
		
		currentType = GameType.values()[MathUtils.random(NUM_GAMETYPES - 1)];
		
		initBackground();
		initLeft();
		initMiddle();
		initRight();
	}

	@Override
	public void show() {
		// Audio
		AudioManager.instance().play();
		
		// Allow the user to back-key to menu
		Gdx.input.setCatchBackKey(true);
		HyperTask.input.clear();
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, HyperTask.input));
		
		// Create minigame
		Minigame.setLevel(0);
		currentType = GameType.values()[MathUtils.random(NUM_GAMETYPES - 1)];
		setGame(currentType);
		
		// Fade in
		stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(FADEIN_TIME)));
		background.clearActions();
	}
	
	@Override
	public void hide() {
		container.dispose();
		stage.getRoot().clearActions();
	}
	
	@Override
	public void dispose() {
		container.dispose();
		super.dispose();
	}
	
	@Override
	public void handleInput(float delta) {
		if (Gdx.input.isKeyPressed(Keys.BACK) && !stage.getRoot().hasActions()) {
			container.removeGame();
			container.getBackground().addAction(Actions.fadeOut(FADEIN_TIME));
			stage.addAction(Actions.sequence(Actions.fadeOut(FADEIN_TIME), Actions.run(new Runnable() {
				@Override
				public void run() {
					game.setScreen(game.getMenuScreen());
				}
			})));
		}
		
		container.handleInput(delta);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		HyperTask.world.step(delta, 6, 2);
		stage.act(delta);
		
		// Update and replace
		container.update(delta);
		if (container.getMinigame() == null && !container.getBackground().hasActions()) {
			container.getBackground().addAction(Actions.sequence(Actions.delay(0.15f), Actions.run(new Runnable() {

				@Override
				public void run() {
					if (container.getMinigame() == null)
						container.setGame(HyperTask.world, currentType);
				}
				  
			  })));
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
		stage.draw();
		container.draw(batch, stage.getRoot().getColor().a);
		batch.end();
		
		//HyperTask.dbRenderer.render(HyperTask.world, HyperTask.worldCamera.combined);
	}

	public void setGame(GameType type) {
		if (container.getMinigame() != null && container.getMinigame().getType() == type) return;
		
		currentType = type;
		container.setGame(HyperTask.world, type);
		
		rightTitle.setText(container.getMinigame().getName());
		
		icon.setDrawable(new TextureRegionDrawable(new TextureRegion(HyperTask.res.getTexture(currentType.toString()))));
		icon.setColor(GameColors[currentType.ordinal()]);
	}
	
	private void initBackground() {
		background = new RectShape();
		background.setBounds(0, 0, V_WIDTH, V_HEIGHT);
		background.setColor(COLOR_MAIN);
		stage.addActor(background);
	}
	
	private void initLeft() {
		// Title
		leftTitle = new Label("Practice", new Label.LabelStyle(HyperTask.res.getFont(FONT_MEDIUM), COLOR_TEXT));
		leftTitle.setOrigin(Align.center);
		leftTitle.setAlignment(Align.center);
		leftTitle.setPosition(BOUNDS_LEFT.x + (BOUNDS_LEFT.width - leftTitle.getWidth()) / 2,
				              BOUNDS_LEFT.y + BOUNDS_LEFT.height * 0.9f - leftTitle.getHeight() / 2);
		
		RectShape pBg = new RectShape();
		pBg.setColor(COLOR_MAIN.cpy().mul(1.4f));
		pBg.setBounds(BOUNDS_LEFT.x, leftTitle.getY(), BOUNDS_LEFT.width, leftTitle.getHeight());
		
		stage.addActor(pBg);
		stage.addActor(leftTitle);
		
		// Scroll panel
		Rectangle panelBounds = new Rectangle(BOUNDS_LEFT);
		panelBounds.height -= 140;
		scrollPanel = new PracticeScrollPanel(this, panelBounds);
		stage.addActor(scrollPanel);
	}
	
	private void initMiddle() {
		container = new PracticeContainer(BOUNDS_MIDDLE);
	}
	
	private void initRight() {
		// Title
		rightTitle = new Label("...", new Label.LabelStyle(HyperTask.res.getFont(FONT_MEDIUM), COLOR_TEXT));
		rightTitle.setOrigin(Align.center);
		rightTitle.setAlignment(Align.center);
		rightTitle.setPosition(BOUNDS_RIGHT.x + BOUNDS_RIGHT.width / 2, BOUNDS_RIGHT.y + BOUNDS_RIGHT.height * 0.9f, Align.center);
		
		RectShape tBg = new RectShape();
		tBg.setColor(COLOR_MAIN.cpy().mul(1.4f));
		tBg.setBounds(BOUNDS_RIGHT.x, rightTitle.getY(), BOUNDS_RIGHT.width, rightTitle.getHeight());
		
		stage.addActor(tBg);
		stage.addActor(rightTitle);
		
		// Icon
		icon = new Image(HyperTask.res.getTexture(currentType.toString()));
		icon.setSize(128, 128);
		icon.setPosition(BOUNDS_RIGHT.x + BOUNDS_RIGHT.width * 0.5f, BOUNDS_RIGHT.y + BOUNDS_RIGHT.height * 0.5f, Align.center);
		stage.addActor(icon);
		
		// Level Selector
		LevelSelector levels = new LevelSelector();
		levels.setPosition(BOUNDS_RIGHT.x + BOUNDS_RIGHT.width / 2, BOUNDS_RIGHT.y + BOUNDS_RIGHT.height * 0.10f, Align.center);
		levels.leftArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				container.setGame(HyperTask.world,  currentType);
			}
		});
		levels.rightArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				container.setGame(HyperTask.world,  currentType);
			}
		});
		stage.addActor(levels);
	}
}
