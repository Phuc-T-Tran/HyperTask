package minigames;

import static com.phuctran.hypertask.Constants.COLOR_MAIN;
import static com.phuctran.hypertask.Constants.PPM;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.HyperInput;

import objects.CircleObject;
import objects.GameObject;
import objects.TriangleObject;
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public class DodgeGame extends Minigame {
	private final float OBJECT_SIZE = 48f;
	private final float SPAWN_DISTANCE_MIN = bounds.width / 2;
	private final float SPAWN_DISTANCE_MAX = bounds.height - OBJECT_SIZE * 2;
	private final float SPIKE_DELAY = 0.5f;
	private final float TOUCH_LERP = 0.18f;
	
	private Player player;
	private int spikesDodged, spikesSpawned;
	private float nextSpawn;
	
	// A smaller rectangle for spikes. They can only spawn within it and fade out when they leave it
	private Rectangle spikeBounds;
	
	public DodgeGame(World world, Rectangle bounds) {
		super(GameType.Dodge, world, bounds);
		spikesDodged = spikesSpawned = 0;
		nextSpawn = SPAWN_TIME_FIRST;
		
		player = new Player(world);
		objects.add(player);
		spikeBounds = new Rectangle(bounds.x + OBJECT_SIZE, bounds.y + OBJECT_SIZE,
	                                bounds.width - OBJECT_SIZE * 2, bounds.height - OBJECT_SIZE * 2);
	}

	@Override
	public void handleInput(float delta) {
		// Move player towards touch pos
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			if (isTouched(i)) {
				Body playerBody = player.getBody();
				Vector2 touchPos = HyperInput.getTouchPos(i);
				Vector2 playerPos = playerBody.getPosition().cpy().scl(PPM);
				
				// Calculate linear interpolation
				Vector2 touchLerp = new Vector2(playerPos.x + (touchPos.x - playerPos.x) * TOUCH_LERP, playerPos.y + (touchPos.y - playerPos.y) * TOUCH_LERP);
				
				// Adjust position to stay in bounds
				if (touchLerp.x < bounds.x + OBJECT_SIZE / 2)
					touchLerp.x = bounds.x + OBJECT_SIZE / 2;
				if (touchLerp.x > bounds.x + bounds.width - OBJECT_SIZE / 2)
					touchLerp.x = bounds.x + bounds.width - OBJECT_SIZE / 2;
				if (touchLerp.y < OBJECT_SIZE / 2)
					touchLerp.y = OBJECT_SIZE / 2;
				if (touchLerp.y > bounds.height - OBJECT_SIZE / 2)
					touchLerp.y = bounds.height - OBJECT_SIZE / 2;
				
				player.getBody().setTransform(touchLerp.x / PPM, touchLerp.y / PPM, 0);
				break;
			}
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Spawn spikes
		if (time > nextSpawn && spikesSpawned < OBJECT_COUNT[level]) {
			time = 0;
			nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
			
			spikesSpawned++;
			spawnSpike();
		}
	}
	
	private void spawnSpike() {
		// Spawn a spike randomly in a radius around the player
		Vector2 playerPos = player.getBody().getPosition().cpy().scl(PPM);
		Vector2 spawnPosition = new Vector2();
		
		do {
			spawnPosition.set(MathUtils.random(SPAWN_DISTANCE_MIN, SPAWN_DISTANCE_MAX), 0);
			spawnPosition.rotate(MathUtils.random(360));
			spawnPosition.add(playerPos);
		} while (!spikeBounds.contains(spawnPosition));
		
		// Point the spike towards the player
		Vector2 spawnVelocity = playerPos.sub(spawnPosition).nor().setLength(OBJECT_SPEED[level]);
		
		objects.add(0, new Spike(world, spawnPosition.scl(1/PPM), spawnVelocity));
	}
	
	private class Player extends CircleObject {
		public Player(World world) {
			super(world, "player", BodyType.DynamicBody,
					new Vector2(bounds.x + bounds.width / 2, bounds.height / 2),
					OBJECT_SIZE / 2);
			body.setGravityScale(0);
			fixture.setSensor(true);
			
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
		
		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "spike")
				hasLost = true;
		}
	}
	
	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.Drag);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon);
	}
	
	private class Spike extends TriangleObject {
		public Spike(World world, Vector2 position, final Vector2 velocity) {
			super(world, "spike", BodyType.KinematicBody, position, OBJECT_SIZE);
			body.setTransform(body.getPosition(), (float)Math.toRadians(velocity.angle() - 90f));
			body.setSleepingAllowed(false);
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.sequence(Actions.fadeIn(FADE_MEDIUM), Actions.delay(SPIKE_DELAY), Actions.run(new Runnable() {
				@Override
				public void run() {
					body.setLinearVelocity(velocity);
				}
			})));
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			// Fade out when leaving bounds, then die
			if (!spikeBounds.contains(body.getPosition().cpy().scl(PPM))) {
				addAction(Actions.sequence(Actions.fadeOut(FADE_FAST), Actions.run(new Runnable() {
					@Override
					public void run() {
						kill();
						spikesDodged++;
						if (spikesDodged >= OBJECT_COUNT[level])
							hasWon = true;
					}
				})));
			}
		}
	}
}