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

import objects.GameObject;
import objects.RectObject;
import objects.TriangleObject;
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public class ClimbGame extends Minigame {
	private final float OBJECT_SIZE = 48f;
	private final float JUMP_VELOCITY = 28;
	private final float WALL_WIDTH = 24;
	
	private Player player;
	float nextSpawn, spikesSpawned, spikesDone;
	
	public ClimbGame(World world, Rectangle bounds) {
		super(GameType.Climb, world, bounds);
		
		// Add player and side walls
		player = new Player(world);
		objects.add(player);
		objects.add(new Wall(world, new Vector2((bounds.x + WALL_WIDTH / 2) / PPM, (bounds.y + bounds.height / 2) / PPM)));
		objects.add(new Wall(world, new Vector2((bounds.x + bounds.width - WALL_WIDTH / 2) / PPM, (bounds.y + bounds.height / 2) / PPM)));
		
		time = 0;
		nextSpawn = SPAWN_TIME_FIRST;
		spikesSpawned = 0;
		spikesDone = 0;
	}

	@Override
	public void handleInput(float delta) {
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			if (justTouched(i) && player.canJump()) {
				if ((player.getX() <= bounds.x + bounds.width / 2 && HyperInput.getTouchPos(i).x > bounds.x + bounds.width / 2) ||
					(player.getX() > bounds.x + bounds.width / 2 && HyperInput.getTouchPos(i).x <= bounds.x + bounds.width / 2))
				player.jump();
				break;
			}
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Spawn a spike
		if (time > nextSpawn && spikesSpawned < OBJECT_COUNT[level]) {
			spikesSpawned++;
			time = 0;
			objects.add(new Spike(world));
			nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
		}
	}
	
	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon1 = new TutorialIcon(IconType.Tap);
		icon1.setPosition(bounds.x + bounds.width * 0.3f, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon1);
		
		TutorialIcon icon2 = new TutorialIcon(IconType.Tap);
		icon2.setPosition(bounds.x + bounds.width * 0.7f, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon2);
	}
	
	private class Player extends RectObject {
		public boolean onWall;
		
		public Player(World world) {
			super(world, "player", BodyType.DynamicBody,
					new Vector2((bounds.x + WALL_WIDTH) / PPM, (bounds.y + bounds.height / 3) / PPM), OBJECT_SIZE, OBJECT_SIZE);
			body.setGravityScale(0);
			
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
		
		public void jump() {
			float sign = (body.getPosition().x * PPM < bounds.x + bounds.width / 2) ? 1 : -1;
			body.applyLinearImpulse(new Vector2(JUMP_VELOCITY * sign, 0), player.getBody().getPosition(), true);
		}

		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "wall")
				onWall = true;
			if (other.getName() == "spike" && body.getPosition().x * PPM < other.getBody().getPosition().x * PPM + OBJECT_SIZE / 2)
				hasLost = true;
		}

		@Override
		public void endCollision(GameObject other) {
			if (other.getName() == "wall")
				onWall = false;
		}
		
		public boolean canJump() {
			return (onWall && body.getLinearVelocity().x == 0);
		}
	}
	
	private class Wall extends RectObject {
		public Wall(World world, Vector2 position) {
			super(world, "wall", BodyType.StaticBody, position, WALL_WIDTH, bounds.height);
			fixture.setFriction(0);
			
			// Spawn animation
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.fadeIn(GameObject.FADE_SLOW));
		}
	}
	
	private class Spike extends TriangleObject {		
		public Spike(World world) {
			super(world, "spike", BodyType.KinematicBody,
					new Vector2(MathUtils.randomBoolean() ? (bounds.x + WALL_WIDTH + OBJECT_SIZE / 2) / PPM :
						                                    (bounds.x + bounds.width - WALL_WIDTH - OBJECT_SIZE / 2) / PPM,
							    (bounds.y + bounds.height - OBJECT_SIZE / 2) / PPM),
					OBJECT_SIZE);
			fixture.setSensor(true);
			// Slide down
			body.setTransform(body.getPosition(), (body.getPosition().x * PPM < bounds.x + bounds.width / 2) ? (float)Math.toRadians(270) : (float)Math.toRadians(90));
			body.setLinearVelocity(new Vector2(0, -OBJECT_SPEED[level]));
			
			// Spawn animation
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_MEDIUM));
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			// Fade out after passing player
			if (body.getPosition().y * PPM < bounds.y + bounds.height * 0.2f)
				addAction(Actions.fadeOut(FADE_MEDIUM));
			
			// Remove this spike if it reaches the edge of the minigame
			if (body.getPosition().y * PPM  - OBJECT_SIZE / 2 < bounds.y) {
				isDead = true;
				spikesDone++;
				if (spikesDone >= OBJECT_COUNT[level])
					hasWon = true;
			}
		}
	}
}
