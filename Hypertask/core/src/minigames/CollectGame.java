package minigames;

import static com.phuctran.hypertask.Constants.PPM;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
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
import objects.CircleShape;
import objects.GameObject;
import objects.TriangleObject;
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public class CollectGame extends Minigame {
	private final float OBJECT_SIZE = 48f;
	private final float SPAWN_DISTANCE_MIN = bounds.width / 2;
	private final float SPAWN_DISTANCE_MAX = bounds.height - OBJECT_SIZE * 2;
	private final float TOUCH_LERP = 0.18f;
	
	private Player player;
	private int circlesHit, circlesSpawned;
	private float nextSpawn;
	
	// A smaller rectangle for objects to spawn in
	private Rectangle spawnBounds;
	
	public CollectGame(World world, Rectangle bounds) {
		super(GameType.Collect, world, bounds);
		circlesHit = circlesSpawned = 0;
		nextSpawn = SPAWN_TIME_FIRST;
		
		player = new Player(world);
		objects.add(player);
		spawnBounds = new Rectangle(bounds.x + OBJECT_SIZE, bounds.y + OBJECT_SIZE,
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
				
				player.getBody().setTransform(touchLerp.x / PPM, touchLerp.y / PPM, (float)Math.toRadians(touchLerp.cpy().sub(playerPos).angle() - 90));
				break;
			}
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Spawn spikes
		if (time > nextSpawn && circlesSpawned < OBJECT_COUNT[level]) {
			time = 0;
			nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
			
			circlesSpawned++;
			spawnCircle();
		}
	}
	
	private void spawnCircle() {
		// Spawn a circle randomly in a radius around the player
		Vector2 playerPos = player.getBody().getPosition().cpy().scl(PPM);
		Vector2 spawnPosition = new Vector2();
		
		do {
			spawnPosition.set(MathUtils.random(SPAWN_DISTANCE_MIN, SPAWN_DISTANCE_MAX), 0);
			spawnPosition.rotate(MathUtils.random(360));
			spawnPosition.add(playerPos);
		} while (!spawnBounds.contains(spawnPosition));
		
		objects.add(0, new Circle(world, spawnPosition));
	}
	
	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.Drag);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon);
	}
	
	private class Player extends TriangleObject {
		public Player(World world) {
			super(world, "player", BodyType.DynamicBody,
					new Vector2((bounds.x + bounds.width / 2 ) / PPM, bounds.height / 2 / PPM),
					OBJECT_SIZE);
			body.setGravityScale(0);
			body.setSleepingAllowed(false);
			fixture.setSensor(true);
			
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
	}
	
	private class Circle extends CircleObject {
		private float timeAlive;
		private CircleShape circle;
		
		public Circle(World world, Vector2 position) {
			super(world, "circle", BodyType.StaticBody, position, OBJECT_SIZE * 0.75f);
			body.setSleepingAllowed(false);
			
			setColor(color.cpy().mul(COLOR_DARK).mul(1,1,1,0));
			addAction(Actions.alpha(0.2f, FADE_MEDIUM));
			
			circle = new CircleShape();
			circle.setBounds(position.x * PPM - OBJECT_SIZE * 0.75f, position.y * PPM - OBJECT_SIZE * 0.75f, OBJECT_SIZE * 1.5f, OBJECT_SIZE * 1.5f);
			circle.setColor(color.cpy().mul(COLOR_DARK).mul(1,1,1,0));
			circle.addAction(Actions.fadeIn(FADE_MEDIUM));
		}

		@Override
		public void act(float delta) {
			super.act(delta);
			circle.act(delta);
			
			float circleTime = OBJECT_TIME[level] + 0.25f;
			timeAlive += delta;
			if (timeAlive > circleTime + FADE_MEDIUM)
				timeAlive = circleTime + FADE_MEDIUM;
				
			circle.setScale(1 - (timeAlive / (circleTime + FADE_MEDIUM)));
			if (timeAlive >= circleTime + FADE_MEDIUM) {
				hasLost = true;
			}
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			circle.draw(batch, parentAlpha);
		}
		
		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "player") {
				setColor(Color.WHITE.cpy().mul(1,1,1,0.75f));
				addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(GameObject.FADE_FAST),
                                                            Actions.scaleBy(2.4f, 2.4f, GameObject.FADE_FAST)),
						                   Actions.run(new Runnable() {
											@Override
											public void run() {
												kill();
												circlesHit++;
												if (circlesHit >= OBJECT_COUNT[level]) {
													hasWon = true;
												}
											}
										})));
				circle.addAction(Actions.fadeOut(FADE_FAST));
			}
		}
	}
}