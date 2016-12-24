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

public class JumpGame extends Minigame {
	private final float OBJECT_SIZE = 48f;
	private final float JUMP_VELOCITY = 16;
	private final float GROUND_HEIGHT = 96;	
	private final float BUFFER = 3; // The player may touch the last X pixels of a spike
	private final float MIN_SPEED = 5.5f;
	
	private Player player;
	float nextSpawn, spikesSpawned, spikesDone;
	
	public JumpGame(World world, Rectangle bounds) {
		super(GameType.Jump, world, bounds);
		
		player = new Player(world);
		objects.add(player);
		objects.add(new Ground(world));
		
		time = 0;
		nextSpawn = SPAWN_TIME_FIRST;
		spikesSpawned = 0;
		spikesDone = 0;
	}

	@Override
	public void handleInput(float delta) {
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			if (justTouched(i) && player.canJump()) {
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
		TutorialIcon icon = new TutorialIcon(IconType.Tap);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon);
	}
	
	private class Player extends RectObject {
		public boolean onGround;
		
		public Player(World world) {
			super(world, "player", BodyType.DynamicBody,
					new Vector2((bounds.x + bounds.width / 4) / PPM, (bounds.y + bounds.height / 4) / PPM), OBJECT_SIZE, OBJECT_SIZE);
			
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
		
		public void jump() {
			body.applyLinearImpulse(new Vector2(0, JUMP_VELOCITY), player.getBody().getPosition(), true);
		}

		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "ground")
				onGround = true;
			if (other.getName() == "spike" && body.getPosition().x * PPM < other.getBody().getPosition().x * PPM + OBJECT_SIZE / 2 - BUFFER)
				hasLost = true;
		}

		@Override
		public void endCollision(GameObject other) {
			if (other.getName() == "ground")
				onGround = false;
		}
		
		public boolean canJump() {
			return (onGround && body.getLinearVelocity().y == 0);
		}
	}
	
	private class Ground extends RectObject {
		public Ground(World world) {
			super(world, "ground", BodyType.StaticBody,
					new Vector2((bounds.x + bounds.width / 2) / PPM, (bounds.y + GROUND_HEIGHT / 2) / PPM), bounds.width, GROUND_HEIGHT);
			fixture.setFriction(0);
			
			// Spawn animation
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.fadeIn(GameObject.FADE_SLOW));
		}
	}
	
	private class Spike extends TriangleObject {		
		public Spike(World world) {
			super(world, "spike", BodyType.KinematicBody,
					new Vector2((bounds.x + bounds.width - OBJECT_SIZE / 2) / PPM, (bounds.y + GROUND_HEIGHT + OBJECT_SIZE / 2) / PPM),
					OBJECT_SIZE);
			fixture.setSensor(true);
			// Slide to the left
			body.setLinearVelocity(new Vector2(Math.min(-MIN_SPEED, -OBJECT_SPEED[level]), 0));
			
			// Spawn animation
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_FAST));
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			// Fade out after passing player
			if (body.getPosition().x < player.getBody().getPosition().x)
				addAction(Actions.fadeOut(FADE_FAST));
			
			// Remove this spike if it reaches the edge of the minigame
			if (body.getPosition().x * PPM  - OBJECT_SIZE / 2 < bounds.x) {
				isDead = true;
				spikesDone++;
				if (spikesDone >= OBJECT_COUNT[level])
					hasWon = true;
			}
		}
	}
}
