package minigames;

import static com.phuctran.hypertask.Constants.COLOR_MAIN;
import static com.phuctran.hypertask.Constants.PPM;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.phuctran.hypertask.Constants.GameType;

import objects.GameObject;
import objects.RectObject;
import objects.TriangleObject;

public class FroggerGame extends Minigame {
	private final float OBJECT_SIZE = 48;
	private final float JUMP_VELOCITY = 16;
	private final float GROUND_HEIGHT = 96;
	private final float SPIKE_SPEED = 7;
	private final float SPIKE_COUNT = 3;
	
	private Player player;
	float nextSpawn;
	float spikesSpawned;
	float spikesDone;
	
	public FroggerGame(World world, Rectangle bounds) {
		super(GameType.Frogger, world, bounds);
		player = new Player(world);
		objects.add(player);
		
		time = 0;
		nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
		spikesSpawned = 0;
		spikesDone = 0;
	}

	@Override
	public void handleInput(float delta) {
		if (justTouched() && player.canJump())
			player.jump();
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Spawn a spike
		if (time > nextSpawn && spikesSpawned < SPIKE_COUNT) {
			spikesSpawned++;
			time = 0;
			objects.add(new Spike(world));
			nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
		}
		
		// Check for win condition
		if (spikesDone >= SPIKE_COUNT)
			hasWon = true;
		if (player.isDead())
			hasLost = true;
	}
	
	private class Player extends RectObject {
		public Player(World world) {
			super(world, "player", BodyType.DynamicBody,
					new Vector2((bounds.x + bounds.width / 4) / PPM, bounds.height / 4 / PPM), OBJECT_SIZE, OBJECT_SIZE);
			
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
		
		public void jump() {
			body.applyLinearImpulse(new Vector2(0, JUMP_VELOCITY), player.getBody().getPosition(), true);
		}

		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "spike")
				isDead = true;
		}
		
		public boolean canJump() {
			return (body.getLinearVelocity().y == 0);
		}
	}
	
	private class Spike extends TriangleObject {		
		public Spike(World world) {
			super(world, "spike", BodyType.KinematicBody,
					new Vector2((bounds.x + bounds.width - OBJECT_SIZE / 2) / PPM, GROUND_HEIGHT / PPM),
					OBJECT_SIZE);

			// Slide to the left
			body.setLinearVelocity(new Vector2(-SPIKE_SPEED,0));
			
			// Spawn animation
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.fadeIn(0.35f));
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			// Fade out after passing player
			if (body.getPosition().x + OBJECT_SIZE / 2 / PPM <= player.getBody().getPosition().x)
				addAction(Actions.fadeIn(0.35f));
			
			// Remove this spike if it reaches the edge of the minigame
			if (body.getPosition().x * PPM < bounds.x) {
				isDead = true;
				spikesDone++;
			}
		}
	}
}
