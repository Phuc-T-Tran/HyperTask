package minigames;

import static com.phuctran.hypertask.Constants.PPM;

import com.badlogic.gdx.Gdx;
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

public class CatchGame extends Minigame {
	private final float BALL_GRAVITYSCALE = 0.6f;//0.16f;
	private final float BALL_RADIUS = 18;
	private final float PLAYER_HEIGHT = 68;
	private final float PLAYER_THICKNESS = 8;
	private final float PLAYER_WIDTH = 86;
	private final float MAX_PLAYER_SPEED = 32f;
	private final float RESTITUTION = 0.15f;
	private final float SPEED_MUL = 1.3f;
	
	private Player player;
	private int ballsSpawned, ballsCaught;
	private float nextSpawn;
	
	public CatchGame(World world, Rectangle bounds) {
		super(GameType.Catch, world, bounds);
		
		configureWalls();
		
		// Create the player
		player = new Player(world);
		objects.add(player);
		
		nextSpawn = SPAWN_TIME_FIRST;
		ballsCaught = ballsSpawned = 0;
	}
	
	@Override
	public void handleInput(float delta) {
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			if (isTouched(i)) {
				Vector2 touchPos = HyperInput.getTouchPos(i);
				Vector2 playerPos = player.getBody().getPosition().cpy().scl(PPM);
				float amount = touchPos.x - playerPos.x;
				float offset = (PLAYER_WIDTH + PLAYER_THICKNESS) / 2 + BALL_RADIUS * 2;
				if (playerPos.x + amount < bounds.x + offset)
					amount = bounds.x + offset - playerPos.x;
				else if (playerPos.x + amount > bounds.x + bounds.width - offset)
					amount = bounds.x + bounds.width - offset - playerPos.x;
				
				// MathUtils.clamp sucks
				if (amount < -MAX_PLAYER_SPEED)
					amount = -MAX_PLAYER_SPEED;
				else if (amount > MAX_PLAYER_SPEED)
					amount = MAX_PLAYER_SPEED;
				
				player.setLinearVelocity(amount, 0);
				return;
			}
		}
		
		// No contacts found. Stop moving
		player.setLinearVelocity(0, 0);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Spawn balls
		if (time > nextSpawn && ballsSpawned < OBJECT_COUNT[level]) {
			time = 0;
			nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
			
			objects.add(0, new Ball(world));
			ballsSpawned++;
		}
	}
	
	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.DragHorizontal);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon);
	}
	
	private void configureWalls() {
		botWall.getFixture().setSensor(true);
		leftWall.getFixture().setRestitution(RESTITUTION);
		rightWall.getFixture().setRestitution(RESTITUTION);
		topWall.getFixture().setRestitution(RESTITUTION);
	}
	
	private class Player extends RectObject {
		RectObject leftSide, rightSide;
		
		public Player(World world) {
			super(world, "player", BodyType.KinematicBody,
					new Vector2((bounds.x + bounds.width / 2) / PPM,
					            (bounds.y + bounds.height * 0.10f + PLAYER_THICKNESS / 2) / PPM),
					PLAYER_WIDTH - PLAYER_THICKNESS * 2, PLAYER_THICKNESS);
			fixture.setFriction(0);
			fixture.setRestitution(RESTITUTION);
			
			// Spawn animation
			setColor(color.cpy().mul(COLOR_DARK).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
			
			createSides();
		}
		
		public void setLinearVelocity(float vx, float vy) {
			body.setLinearVelocity(vx, vy);
			leftSide.getBody().setLinearVelocity(vx, vy);
			rightSide.getBody().setLinearVelocity(vx, vy);
		}
		
		private void createSides() {
			leftSide = new RectObject(world, "playerLeft", BodyType.KinematicBody,
					new Vector2((bounds.x + (bounds.width - PLAYER_WIDTH + PLAYER_THICKNESS) / 2) / PPM,
							    (bounds.y + bounds.height * 0.10f + PLAYER_HEIGHT / 2) / PPM),
					PLAYER_THICKNESS, PLAYER_HEIGHT);
			leftSide.getFixture().setFriction(0);
			leftSide.getFixture().setRestitution(RESTITUTION);
			leftSide.setColor(color.cpy().mul(COLOR_DARK).mul(1,1,1,0));
			leftSide.addAction(Actions.fadeIn(FADE_SLOW));
			objects.add(leftSide);
			
			rightSide = new RectObject(world, "playerRight", BodyType.KinematicBody,
					new Vector2((bounds.x + (bounds.width + PLAYER_WIDTH - PLAYER_THICKNESS) / 2) / PPM,
							    (bounds.y + bounds.height * 0.10f + PLAYER_HEIGHT / 2) / PPM),
					PLAYER_THICKNESS, PLAYER_HEIGHT);
			rightSide.getFixture().setFriction(0);
			rightSide.getFixture().setRestitution(RESTITUTION);
			rightSide.setColor(color.cpy().mul(COLOR_DARK).mul(1,1,1,0));
			rightSide.addAction(Actions.fadeIn(FADE_SLOW));
			objects.add(rightSide);
		}
	}
	
	private class Ball extends CircleObject {
		public Ball(World world) {
			super(world, "ball", BodyType.DynamicBody,
					new Vector2(bounds.x + MathUtils.random(BALL_RADIUS * 2, bounds.width - BALL_RADIUS * 2),
							    bounds.y + bounds.height), BALL_RADIUS);
			body.setGravityScale(BALL_GRAVITYSCALE);
			
			// Apply slight left / right velocity to stop the ball from landing on the bucket
			body.setLinearVelocity(body.getPosition().x * PPM > bounds.x + bounds.width / 2 ? -0.3f : 0.3f, 0);

			// Spawn animation
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_MEDIUM));
		}
		
		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "player") {
				addAction(Actions.sequence(Actions.fadeOut(FADE_FAST), Actions.run(new Runnable() {
					@Override
					public void run() {
						isDead = true;
						ballsCaught++;
						if (ballsCaught >= OBJECT_COUNT[level])
							hasWon = true;
					}
				})));
			}
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			if (body.getLinearVelocity().y < -OBJECT_SPEED[level] * SPEED_MUL) {
				body.setLinearVelocity(body.getLinearVelocity().x, -OBJECT_SPEED[level] * SPEED_MUL);
			}
			
			if (body.getPosition().y * PPM < bounds.y)
				hasLost = true;
		}
	}
}
