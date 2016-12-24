package minigames;

import static com.phuctran.hypertask.Constants.COLOR_MAIN;
import static com.phuctran.hypertask.Constants.PPM;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.HyperInput;

import objects.CircleObject;
import objects.GameObject;
import objects.RectObject;
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public class FallGame extends Minigame {
	private final float BALL_RADIUS = 24f;
	private final float BALL_SPEED = 44f;
	private final float BALL_SPAWN_Y = bounds.y + bounds.height * 0.6f;
	private final float GAP_WIDTH = 84f;
	private final float WALL_SIZE = 24;
	private final float WALL_FRICTION = 0.1f;
	private final float BALL_GRAVITYSCALE = 2.4f;
	
	private Player player;
	private float lastGap;
	
	public FallGame(World world, Rectangle bounds) {
		super(GameType.Fall, world, bounds);
		
		player = new Player(world);
		objects.add(player);
		
		// Create starting walls
		time = 0.5f;
		createWall(true, OBJECT_SPEED[level] / 2 * (time + SPAWN_TIME_MIN[level] * 2));
		createWall(true, OBJECT_SPEED[level] / 2 * (time + SPAWN_TIME_MIN[level]));
		createWall(false, OBJECT_SPEED[level] / 2 * time);
		
		//if (OBJECT_SPEED[level] / 2 * SPAWN_TIME_MIN[level] * 2 < bounds.y + bounds.height * 0.6f)
		//	createWall(OBJECT_SPEED[level] / 2 * SPAWN_TIME_MIN[level] * 2);
		configureWalls();
	}

	@Override
	public void handleInput(float delta) {
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			if (isTouched(i)) {
				Vector2 touchPos = HyperInput.getTouchPos(i);
				Vector2 playerPos = player.getBody().getPosition().cpy().scl(PPM);
				float amount = (touchPos.x > playerPos.x) ? Math.min( BALL_SPEED, touchPos.x - playerPos.x)
						                                  : Math.max(-BALL_SPEED, touchPos.x - playerPos.x);
				player.getBody().applyForceToCenter(amount, 0, true);
				break;
			}
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Spawn walls
		if (time > SPAWN_TIME_MIN[level]) {
			time = 0;
			createWall(false, 0);
		}
		
		// Check if player has reached top or bottom of game
		if (player.getBody().getPosition().y < 0)
			hasWon = true;
		else if (player.getBody().getPosition().y * PPM + BALL_RADIUS > bounds.height)
			hasLost = true;
	}
	
	private void createWall(boolean noMiddleGap, float y) {
		float gapLocation = MathUtils.random(bounds.x + WALL_SIZE * 2, bounds.x + bounds.width - GAP_WIDTH - WALL_SIZE * 2);

		// Reroll while we get middle gaps or the gap is too close to the last one
		while ( (noMiddleGap && (gapLocation + GAP_WIDTH / 2 >= bounds.x + bounds.width / 2 - GAP_WIDTH)
				             && (gapLocation + GAP_WIDTH / 2 <= bounds.x + bounds.width / 2 + GAP_WIDTH))
				|| Math.abs(gapLocation - lastGap) < GAP_WIDTH) {
			gapLocation = MathUtils.random(bounds.x + WALL_SIZE * 2, bounds.x + bounds.width - GAP_WIDTH - WALL_SIZE * 2);
		}
		lastGap = gapLocation;
		
		float leftWidth = gapLocation - bounds.x - WALL_SIZE;
		float rightWidth = bounds.width - (gapLocation - bounds.x) - GAP_WIDTH - WALL_SIZE;
		
		objects.add(new Wall(world, new Vector2((bounds.x + leftWidth / 2 + WALL_SIZE) / PPM, y), leftWidth));
		objects.add(new Wall(world, new Vector2((bounds.x + bounds.width - rightWidth / 2 - WALL_SIZE) / PPM, y), rightWidth));
	}
	
	private void configureWalls() {
		topWall.getBody().setActive(false);
		botWall.getBody().setActive(false);
		
		GameObject leftBound = new RectObject(world, "leftBound", BodyType.StaticBody,
				new Vector2((bounds.x + WALL_SIZE / 2) / PPM, bounds.height / 2 / PPM), WALL_SIZE, bounds.height);
		leftBound.setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
		leftBound.addAction(Actions.fadeIn(GameObject.FADE_SLOW));
		objects.add(leftBound);
		
		GameObject rightBound = new RectObject(world, "righttBound", BodyType.StaticBody,
				new Vector2((bounds.x + bounds.width - WALL_SIZE / 2) / PPM, bounds.height / 2 / PPM), WALL_SIZE, bounds.height);
		rightBound.setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
		rightBound.addAction(Actions.fadeIn(GameObject.FADE_SLOW));
		objects.add(rightBound);
	}
	
	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.DragHorizontal);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon);
	}
	
	private class Player extends CircleObject {
		public Player(World world) {
			super(world, "player", BodyType.DynamicBody,
					new Vector2(bounds.x + bounds.width / 2 - BALL_RADIUS, BALL_SPAWN_Y),
					BALL_RADIUS);
			body.setGravityScale(BALL_GRAVITYSCALE);
			
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
	}
	
	private class Wall extends RectObject {
		public Wall(World world, Vector2 position, float width) {
			super(world, "wall", BodyType.KinematicBody, position, width, WALL_SIZE);
			body.setLinearVelocity(new Vector2(0, OBJECT_SPEED[level] / 2));
			fixture.setFriction(WALL_FRICTION);
			
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
	}
}