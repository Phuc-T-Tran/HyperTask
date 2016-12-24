package minigames;

import static com.phuctran.hypertask.Constants.COLOR_MAIN;
import static com.phuctran.hypertask.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

public class ShootGame extends Minigame {
	private final float OBJECT_SIZE = 48f;
	private final float GROUND_HEIGHT = 96;	
	private final float BULLET_WIDTH = 8;
	private final float BULLET_HEIGHT = 48;
	private final float BULLET_SPEED = 26;
	private final float BULLET_COOLDOWN = 0.33f;
	
	private Player player;
	float nextSpawn, spikesSpawned, spikesDone, shootTime;
	
	public ShootGame(World world, Rectangle bounds) {
		super(GameType.Shoot, world, bounds);
		player = new Player(world);
		objects.add(player);
		objects.add(new Ground(world));
		
		time = 0;
		nextSpawn = SPAWN_TIME_FIRST;
		spikesSpawned = 0;
		spikesDone = 0;
		shootTime = BULLET_COOLDOWN;
	}

	@Override
	public void handleInput(float delta) {
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			if (isTouched(i) && shootTime >= BULLET_COOLDOWN) {
				objects.add(new Bullet(world, HyperInput.getTouchPos(i)));
				shootTime = 0;
				break;
			}
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		shootTime += delta;
		if (shootTime >= BULLET_COOLDOWN)
			shootTime = BULLET_COOLDOWN;
		
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
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.68f, Align.center);
		stage.addActor(icon);
	}
	
	private class Player extends TriangleObject {
		public Player(World world) {
			super(world, "player", BodyType.DynamicBody,
					new Vector2((bounds.x + bounds.width / 2) / PPM, (bounds.y + GROUND_HEIGHT + OBJECT_SIZE / 2) / PPM), OBJECT_SIZE);
			body.setSleepingAllowed(false);
			body.setGravityScale(0);
			fixture.setSensor(true);
			
			// Spawn animation
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
	}
	
	private class Ground extends RectObject {
		public Ground(World world) {
			super(world, "ground", BodyType.DynamicBody,
					new Vector2((bounds.x + bounds.width / 2) / PPM, (bounds.y + GROUND_HEIGHT / 2) / PPM), bounds.width, GROUND_HEIGHT);
			body.setSleepingAllowed(false);
			body.setGravityScale(0);
			fixture.setSensor(true);
			
			// Spawn animation
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.fadeIn(GameObject.FADE_SLOW));
		}
	}
	
	private class Spike extends TriangleObject {		
		public Spike(World world) {
			super(world, "spike", BodyType.KinematicBody,
					new Vector2(MathUtils.random(bounds.x + OBJECT_SIZE / 2 + bounds.width * 0.1f, bounds.x + bounds.width * 0.9f - OBJECT_SIZE / 2) / PPM,
							    (bounds.y + bounds.height - OBJECT_SIZE / 2) / PPM), OBJECT_SIZE);
			body.setSleepingAllowed(false);
			fixture.setSensor(true);
			
			// TODO: Sin-wave downwards
			body.setTransform(body.getPosition(), body.getAngle() + (float)Math.toRadians(180));
			body.setLinearVelocity(new Vector2(0, -OBJECT_SPEED[level]));
			
			// Spawn animation
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_MEDIUM));
		}
		
		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "player" || other.getName() == "ground") {
				hasLost = true;
			}
			else if (other.getName() == "bullet") {
				body.setLinearVelocity(0, 0);
				setColor(Color.WHITE.cpy().mul(1,1,1,0.75f));
				addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(GameObject.FADE_FAST),
                                                            Actions.scaleBy(2f, 2f, GameObject.FADE_FAST)),
						                   Actions.run(new Runnable() {
											@Override
											public void run() {
												kill();
												spikesDone++;
												if (spikesDone >= OBJECT_COUNT[level]) {
													hasWon = true;
												}
											}
										})));
			}
		}
	}
	
	private class Bullet extends RectObject {
		public Bullet(World world, Vector2 target) {
			super(world, "bullet", BodyType.DynamicBody,
					new Vector2(player.getBody().getPosition().x, player.getBody().getPosition().y + BULLET_HEIGHT / 2 / PPM), BULLET_WIDTH, BULLET_HEIGHT);
			body.setSleepingAllowed(false);
			body.setGravityScale(0);
			fixture.setSensor(true);
			
			Vector2 vec = target.cpy().sub(body.getPosition().cpy().scl(PPM)).nor().setLength(BULLET_SPEED);
			body.setLinearVelocity(vec);
			body.setTransform(body.getPosition(), (float)Math.toRadians(vec.angle() - 90f));
			
			// Spawn animation
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_FAST / 2));
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			if (outOfBounds()) {
				addAction(Actions.sequence(Actions.fadeOut(FADE_FAST / 2), Actions.run(new Runnable() {
					@Override
					public void run() {
						kill();
					}
				})));
			}
		}
		
		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "spike" ||
					 other.getName() == "leftWall" || other.getName() == "rightWall" || 
					 other.getName() == "topWall" || other.getName() == "bottWall") {
				kill();
			}
		}
		
		private boolean outOfBounds() {
			boolean verticalBounds = body.getPosition().y * PPM + OBJECT_SIZE > bounds.y + bounds.height ||
								     body.getPosition().y * PPM - OBJECT_SIZE < bounds.y;
			boolean horizontalBounds = body.getPosition().x * PPM + OBJECT_SIZE > bounds.x + bounds.width ||
					                   body.getPosition().x * PPM - OBJECT_SIZE < bounds.x;
					                   
           	return verticalBounds || horizontalBounds;
		}
	}
}
