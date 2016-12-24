package minigames;

import static com.phuctran.hypertask.Constants.GameColors;
import static com.phuctran.hypertask.Constants.GameNames;
import static com.phuctran.hypertask.Constants.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.HyperInput;

import objects.GameObject;
import objects.RectObject;
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public abstract class Minigame {
	public static final int MAX_LEVEL = 5;
	
	                                  /* LEVEL  0      1      2      3      4       5     */
	protected static float SPAWN_TIME_MIN[] = { 3.50f, 3.00f, 2.50f, 2.25f, 1.75f,  1.25f };
	protected static float SPAWN_TIME_MAX[] = { 6.00f, 5.00f, 4.00f, 3.00f, 2.50f,  1.75f };
	protected static float OBJECT_SPEED[]   = { 4.50f, 5.00f, 5.50f, 7.00f, 8.50f, 10.00f };
	protected static float OBJECT_TIME[]    = {     3,     3, 2.75f, 2.75f, 2.50f,  2.25f };
	protected static int   OBJECT_COUNT[]   = {     3,     3,     3,     3,     3,      3 };
	protected static float SPAWN_TIME_FIRST = 1.75f;
	public static int level = 0;
	
	protected GameType type;
	protected World world;
	protected Rectangle bounds;
	
	protected String name;
	protected Color color;
	
	protected RectObject topWall, botWall, leftWall, rightWall;
	protected ArrayList<GameObject> objects;
	
	protected float time;
	protected boolean hasWon, hasLost;
	
	public Minigame(GameType type, World world, Rectangle bounds) {
		this.type = type;
		this.world = world;
		this.bounds = bounds;
		
		name = GameNames[type.ordinal()];
		color = GameColors[type.ordinal()];
		objects = new ArrayList<GameObject>();
		
		time = 0;
		hasWon = hasLost = false;
		
		createWalls();
	}
	
	public void handleInput(float delta) {};
	
	public void update(float delta) {
		time += delta;
		
		// Update all game objects
		for (int i = 0; i < objects.size(); i++) {
			GameObject o = objects.get(i);

			// Dispose and remove dead objects
			o.act(delta);
			if (o.isDead()) {
				o.dispose();
				objects.remove(o);
				i--;
			}
		}
	}
	
	public void draw(SpriteBatch batch, float parentAlpha) {
		for (GameObject o : objects) {
			o.draw(batch, parentAlpha);
		}
	}
	
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.Tap);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon);
	};
	
	public void dispose() {
		for (GameObject o : objects) {
			o.dispose();
		}
		objects.clear();
	}
	
	public static void incrementLevel() {
		Minigame.level = Math.min(Minigame.level + 1, MAX_LEVEL);
	}
	
	public static void setLevel(int level) {
		Minigame.level = MathUtils.clamp(level, 0, MAX_LEVEL);
	}
	
	/* INPUT METHODS */
	public boolean justTouched() { return HyperInput.justTouched() && bounds.contains(HyperInput.getTouchPos()); }
	public boolean justTouched(int i) { return HyperInput.justTouched(i) && bounds.contains(HyperInput.getTouchPos(i)); }
	public boolean isTouched() { return HyperInput.isTouched() && bounds.contains(HyperInput.getTouchPos()); }
	public boolean isTouched(int i) { return HyperInput.isTouched(i) && bounds.contains(HyperInput.getTouchPos(i)); }
	
	/* GETTER METHODS */
	public GameType getType() { return type; }
	public Rectangle getBounds() { return bounds; }
	public String getName() { return name; }
	public Color getColor() { return color; }
	public boolean hasWon() { return hasWon; }
	public boolean hasLost() { return hasLost; }
	
	private void createWalls() {
		topWall = new RectObject(world, "topWall", BodyType.StaticBody,
				new Vector2((bounds.x + bounds.width / 2) / PPM, bounds.height / PPM), bounds.width, 1);
		topWall.getColor().a = 0;
		objects.add(topWall);
		
		botWall = new RectObject(world, "botWall", BodyType.StaticBody,
				new Vector2((bounds.x + bounds.width / 2) / PPM, 0), bounds.width, 1);
		botWall.getColor().a = 0;
		objects.add(botWall);
		
		leftWall = new RectObject(world, "leftWall", BodyType.StaticBody,
				new Vector2(bounds.x / PPM, bounds.height / 2 / PPM), 1, bounds.height);
		leftWall.getColor().a = 0;
		objects.add(leftWall);
		
		rightWall = new RectObject(world, "rightWall", BodyType.StaticBody,
				new Vector2((bounds.x + bounds.width) / PPM, bounds.height / 2 / PPM), 1, bounds.height);
		rightWall.getColor().a = 0;
		objects.add(rightWall);
	}
}
