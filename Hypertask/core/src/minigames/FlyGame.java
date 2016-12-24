package minigames;

import static com.phuctran.hypertask.Constants.COLOR_MAIN;
import static com.phuctran.hypertask.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.HyperInput;
import com.phuctran.hypertask.HyperTask;

import objects.GameObject;
import objects.RectObject;
import objects.TriangleObject;
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public class FlyGame extends Minigame {
	private final float OBJECT_SIZE = 48f;
	private final float WALL_SIZE = 48;
	private final float WALL_HEIGHT_MIN = bounds.height * 0.15f;
	private final float WALL_HEIGHT_MAX = bounds.height * 0.45f;
	private final float PLAYER_FORCE = 80f;
	private final float PLAYER_MAX_SPEED = 14f;
	
	private Player player;
	private int wallsSpawned, wallsPassed;
	private float nextSpawn;
	
	public FlyGame(World world, Rectangle bounds) {
		super(GameType.Fly, world, bounds);
		
		wallsSpawned = wallsPassed = 0;
		nextSpawn = SPAWN_TIME_FIRST;
		
		// Add player
		player = new Player(world);
		objects.add(player);
		
		// Create ground and ceiling
		RectObject ceiling = new RectObject(world, "ceiling", BodyType.StaticBody,
				new Vector2(bounds.x + bounds.width / 2, bounds.y + bounds.height - WALL_SIZE / 2).scl(1/PPM),
				bounds.width, WALL_SIZE);
		ceiling.setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
		ceiling.addAction(Actions.fadeIn(GameObject.FADE_SLOW));
		objects.add(ceiling);
		
		RectObject ground = new RectObject(world, "ground", BodyType.StaticBody,
				new Vector2(bounds.x + bounds.width / 2, bounds.y + WALL_SIZE / 2).scl(1/PPM),
				bounds.width, WALL_SIZE);
		ground.setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
		ground.addAction(Actions.fadeIn(GameObject.FADE_SLOW));
		objects.add(ground);
	}

	@Override
	public void handleInput(float delta) {
		if (player.touchingCeiling) return;
		
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			if (isTouched(i)) {
				// Apply "upwards wind"
				if (player.getBody().getLinearVelocity().y < PLAYER_MAX_SPEED)
					player.getBody().applyForceToCenter(0, PLAYER_FORCE, true);
				return;
			}
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Spawn walls
		if (time > nextSpawn && wallsSpawned < OBJECT_COUNT[level]) {
			time = 0;
			nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
			
			objects.add(new Wall(world, MathUtils.random(WALL_HEIGHT_MIN, WALL_HEIGHT_MAX), MathUtils.random(1) == 1));
			wallsSpawned++;
		}
	}
	
	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.Hold);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon);
	}
	
	private class Player extends TriangleObject {
		public boolean touchingCeiling = false;
		
		public Player(World world) {
			super(world, "player", BodyType.DynamicBody,
					new Vector2(bounds.x + bounds.width / 4, bounds.y + bounds.height / 2).scl(1/PPM), OBJECT_SIZE);

			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.sequence(Actions.fadeIn(FADE_SLOW)));
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			body.setTransform(new Vector2((bounds.x + bounds.width / 4) / PPM, body.getPosition().y),
					(float)Math.toRadians(240 + 60 * (body.getPosition().y * PPM / (bounds.y + bounds.height))));
		}
		
		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "wall")
				hasLost = true;
			if (other.getName() == "ceiling") {
				touchingCeiling = true;
			}
		}
		
		@Override
		public void endCollision(GameObject other) {
			if (other.getName() == "ceiling") {
				touchingCeiling = false;
			}
		}
	}
	
	private class Wall extends RectObject {
		private TriangleObject tip;
		
		public Wall(World world, float height, boolean top) {
			super(world, "wall", BodyType.KinematicBody,
					new Vector2(bounds.x + bounds.width - WALL_SIZE / 2,
							    top ? bounds.y + bounds.height - height / 2 : bounds.y + height / 2).scl(1/PPM),
					WALL_SIZE, height);
			tip = new TriangleObject(world, "wall", BodyType.KinematicBody,
					new Vector2(bounds.x + bounds.width - WALL_SIZE / 2,
							    top ? bounds.y + bounds.height - height - OBJECT_SIZE / 2
							        : bounds.y + height + OBJECT_SIZE / 2).scl(1/PPM), WALL_SIZE);
			objects.add(tip);
			
			// Flip triangle upside down if needed
			if (top)
				tip.getBody().setTransform(tip.getBody().getPosition(), (float)Math.toRadians(180));
			
			body.setLinearVelocity(new Vector2(-OBJECT_SPEED[level] / 2, 0));
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
			
			tip.setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			tip.addAction(Actions.fadeIn(FADE_SLOW));
			tip.getBody().setLinearVelocity(new Vector2(-OBJECT_SPEED[level] / 2, 0));
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			// Fade out after passing player
			if (body.getPosition().x < player.getBody().getPosition().x - OBJECT_SIZE / PPM) {
				addAction(Actions.fadeOut(FADE_MEDIUM));
				tip.addAction(Actions.fadeOut(FADE_MEDIUM));
			}
			
			// Remove this spike if it reaches the edge of the minigame
			if (body.getPosition().x * PPM  - OBJECT_SIZE / 2 < bounds.x) {
				isDead = true;
				tip.kill();
				
				wallsPassed++;
				if (wallsPassed >= OBJECT_COUNT[level])
					hasWon = true;
			}
		}
	}
}