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
import objects.TriangleObject;
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public class GuardGame extends Minigame {
	private final float OBJECT_SIZE = 42;
	private final float SHIELD_DISTANCE = OBJECT_SIZE * 1.2f;
	private final float SHIELD_WIDTH = OBJECT_SIZE * 2;
	private final float SHIELD_HEIGHT = OBJECT_SIZE / 4;
	private final float SPIKE_DISTANCE = (bounds.width - OBJECT_SIZE) / 2 / PPM;
	private final float SPIKE_DELAY = 0.85f;
	private final float SPIKE_OFFSET = 4 / PPM; // Additional distance from walls
	
	private Player player;
	private Shield shield;
	private int spikesBlocked, spikesSpawned;
	private float nextSpawn;
	
	public GuardGame(World world, Rectangle bounds) {
		super(GameType.Guard, world, bounds);
		spikesBlocked = spikesSpawned = 0;
		nextSpawn = SPAWN_TIME_FIRST;
		
		player = new Player(world);
		objects.add(player);
		
		Vector2 playerPos = player.getBody().getPosition();
		Vector2 shieldPos = playerPos.cpy().add(0, SHIELD_DISTANCE / PPM);
		shield = new Shield(world, shieldPos);
		objects.add(shield);
	}

	@Override
	public void handleInput(float delta) {
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			if (isTouched(i)) {
				Vector2 touchPos = HyperInput.getTouchPos(i);
				Vector2 newPos = player.getBody().getPosition().cpy();
				boolean rotate = false;
				
				if (touchPos.x >= bounds.x + bounds.width / 2 - OBJECT_SIZE * 1.5f &&
					touchPos.x <= bounds.x + bounds.width / 2 + OBJECT_SIZE * 1.5f) {
						// Above or below the player
						if (touchPos.y <= bounds.height / 2)
							newPos.add(0, -SHIELD_DISTANCE / PPM);
						else
							newPos.add(0, SHIELD_DISTANCE / PPM);
					}
				else if (touchPos.y >= bounds.height / 2 - OBJECT_SIZE * 1.5f &&
					     touchPos.y <= bounds.height / 2 + OBJECT_SIZE * 1.5f) {
					// To the left or right of the player
					rotate = true;
					if (touchPos.x <= bounds.x + bounds.width / 2)
						newPos.add(-SHIELD_DISTANCE / PPM, 0);
					else
						newPos.add(SHIELD_DISTANCE / PPM, 0);
				}
				else {
					continue;
				}
				
				shield.getBody().setTransform(newPos, rotate ? 90 * (float)Math.PI / 180 : 0);
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
			spikesSpawned++;
			nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
			spawnSpike();
		}
	}
	
	@Override
	public void addTutorial(Stage stage) {
		float iconScale = 0.9f;
		
		TutorialIcon left = new TutorialIcon(IconType.Tap);
		left.setPosition(bounds.x + 32, bounds.y + bounds.height * 0.5f, Align.center);
		left.setScale(iconScale);
		stage.addActor(left);
		
		TutorialIcon right = new TutorialIcon(IconType.Tap);
		right.setPosition(bounds.x + bounds.width - 32, bounds.y + bounds.height * 0.5f, Align.center);
		right.setScale(iconScale);
		stage.addActor(right);
		
		TutorialIcon top = new TutorialIcon(IconType.Tap);
		top.setPosition(bounds.x + bounds.width * 0.5f, bounds.y + bounds.height * 0.5f + (bounds.width / 2 - 32), Align.center);
		top.setScale(iconScale);
		stage.addActor(top);
		
		TutorialIcon bot = new TutorialIcon(IconType.Tap);
		bot.setPosition(bounds.x + bounds.width * 0.5f, bounds.y + bounds.height * 0.5f - (bounds.width / 2 - 32), Align.center);
		bot.setScale(iconScale);
		stage.addActor(bot);
	}
	
	private void spawnSpike() {
		int val = MathUtils.random(1,4);
		Vector2 playerPos = player.getBody().getPosition();
		Vector2 spawnPosition = new Vector2();
		Vector2 spawnVelocity = new Vector2();
		
		switch(val) {
		case 1:
			spawnPosition.set(playerPos.x, playerPos.y + SPIKE_DISTANCE);
			spawnVelocity.set(0, -OBJECT_SPEED[level]);
			break;
		case 2:
			spawnPosition.set(playerPos.x + SPIKE_DISTANCE - SPIKE_OFFSET, playerPos.y);
			spawnVelocity.set(-OBJECT_SPEED[level], 0);
			break;
		case 3:
			spawnPosition.set(playerPos.x, playerPos.y - SPIKE_DISTANCE);
			spawnVelocity.set(0, OBJECT_SPEED[level]);
			break;
		case 4:
			spawnPosition.set(playerPos.x - SPIKE_DISTANCE + SPIKE_OFFSET, playerPos.y);
			spawnVelocity.set(OBJECT_SPEED[level], 0);
			break;
		}
		
		objects.add(0, new Spike(world, spawnPosition, spawnVelocity));
		
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
			if (other.getName() == "spike") {
				hasLost = true;
			}
		}
	}
	
	private class Shield extends RectObject {
		public Shield(World world, Vector2 position) {
			super(world, "shield", BodyType.DynamicBody, position, SHIELD_WIDTH, SHIELD_HEIGHT);
			body.setGravityScale(0);
			fixture.setSensor(true);
			
			setColor(color.cpy().mul(COLOR_DARK).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
	}
	
	private class Spike extends TriangleObject {
		public Spike(World world, Vector2 position, final Vector2 velocity) {
			super(world, "spike", BodyType.KinematicBody, position, OBJECT_SIZE);
			body.setTransform(body.getPosition(), (float)Math.toRadians(velocity.angle() - 90f));
			
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.sequence(Actions.fadeIn(FADE_MEDIUM), Actions.delay(SPIKE_DELAY), Actions.run(new Runnable() {
				@Override
				public void run() {
					body.setLinearVelocity(velocity);
				}
			})));
		}
		
		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "shield") {
				// Stop, fade out, then disappear
				body.setLinearVelocity(0, 0);
				addAction(Actions.sequence(Actions.fadeOut(FADE_MEDIUM), Actions.run(new Runnable() {
					@Override
					public void run() {
						isDead = true;
						spikesBlocked++;
						if (spikesBlocked >= OBJECT_COUNT[level])
							hasWon = true;
					}
				})));
			}
		}
	}
}