package minigames;

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
import objects.RectObject;
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public class PongGame extends Minigame {
	private final float BALL_RADIUS = 18;
	private final float PADDLE_WIDTH = 110;
	private final float PADDLE_HEIGHT = 22;
	private final float PADDLE_DISTANCE = 0.10f;
	private final float ENEMY_SPEED = 3.5f;
	private final float MAX_PLAYER_SPEED = 32f;
	private final float PADDLE_SIGHT = bounds.height * 0.65f;
	private final float SPEED_MUL = 1.4f; // Ball moves faster than most game objects
	private final float MIN_ANGLE_HORIZONTAL = 20; // For ball
	private final float MIN_ANGLE_VERTICAL = 10; // For ball
	private final float FIRST_ANGLE_LOW = 10f;
	private final float FIRST_ANGLE_HIGH = 20f;
	
	private Paddle player, enemy;
	private Ball ball;
	private float numBounces;
	
	public PongGame(World world, Rectangle bounds) {
		super(GameType.Pong, world, bounds);
		
		numBounces = 0;
		
		// Create the paddles
		player = new Paddle("player", world, new Vector2((bounds.x + bounds.width / 2) / PPM, (bounds.y + bounds.height * PADDLE_DISTANCE) / PPM));
		enemy = new Paddle("enemy", world, new Vector2((bounds.x + bounds.width / 2) / PPM, (bounds.y + bounds.height * (1 - PADDLE_DISTANCE)) / PPM));
		objects.add(player);
		objects.add(enemy);
		
		// Create the ball
		ball = new Ball(world);
		objects.add(ball);
		
		configureWalls();
	}
	
	@Override
	public void handleInput(float delta) {
		Body playerBody = player.getBody();
		Vector2 playerPos = playerBody.getPosition().cpy().scl(PPM);
		
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			Vector2 touchPos = HyperInput.getTouchPos(i);
			if (isTouched(i)) {
				float amount = touchPos.x - playerPos.x;
				if (playerPos.x + amount < bounds.x + PADDLE_WIDTH / 2)
					amount = bounds.x + PADDLE_WIDTH / 2 - playerPos.x;
				else if (playerPos.x + amount > bounds.x + bounds.width - PADDLE_WIDTH / 2)
					amount = bounds.x + bounds.width - PADDLE_WIDTH / 2 - playerPos.x;
				
				// MathUtils.clamp sucks
				if (amount < -MAX_PLAYER_SPEED)
					amount = -MAX_PLAYER_SPEED;
				else if (amount > MAX_PLAYER_SPEED)
					amount = MAX_PLAYER_SPEED;
				
				playerBody.setLinearVelocity(amount, 0);
				return;
			}
		}
		
		// No touches found
		playerBody.setLinearVelocity(0,0);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		updateAI();
		
		// Check win condition
		if (ball.getBody().getPosition().y * PPM < 0)
			hasLost = true;
		else if (ball.getBody().getPosition().y * PPM > bounds.height)
			hasWon = true;
	}
	
	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.DragHorizontal);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.2f, Align.center);
		stage.addActor(icon);
	}
	
	private void updateAI() {
		// Update AI
		Vector2 ballPos = ball.getBody().getPosition().cpy().scl(PPM);
		Vector2 enemyPos = enemy.getBody().getPosition().cpy().scl(PPM);
		
		// Ball is in vision and moving toward enemy
		if (ballPos.y > PADDLE_SIGHT && ball.getBody().getLinearVelocity().y > 0) {
			float amount = ballPos.x - enemyPos.x;
			amount = (amount > 0) ? Math.min(ENEMY_SPEED, amount) : Math.max(-ENEMY_SPEED, amount);
			
			if (enemyPos.x + amount < bounds.x + PADDLE_WIDTH / 2)
				amount = bounds.x + PADDLE_WIDTH / 2 - enemyPos.x;
			else if (enemyPos.x + amount > bounds.x + bounds.width - PADDLE_WIDTH / 2)
				amount = bounds.x + bounds.width - PADDLE_WIDTH / 2 - enemyPos.x;
			
			enemy.getBody().setLinearVelocity(amount, 0);
		}
		else
			enemy.getBody().setLinearVelocity(0, 0);
	}
	
	private void configureWalls() {
		topWall.getFixture().setSensor(true);
		botWall.getFixture().setSensor(true);
		leftWall.getFixture().setRestitution(1f);
		rightWall.getFixture().setRestitution(1f);
	}

	private class Paddle extends RectObject {
		public Paddle(String name, World world, Vector2 position) {
			super(world, name, BodyType.KinematicBody,
					position,
					PADDLE_WIDTH, PADDLE_HEIGHT);

			fixture.setFriction(0);
			fixture.setRestitution(1f);
			
			// Spawn animation
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
	}
	
	private class Ball extends CircleObject {
		public Ball(World world) {
			super(world, "ball", BodyType.DynamicBody,
					new Vector2(bounds.x + bounds.width / 2, bounds.height / 2), BALL_RADIUS);
			body.setGravityScale(0);
			body.setBullet(true);
			fixture.setFriction(0);
			fixture.setRestitution(1f);

			// Spawn animation
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.sequence(Actions.fadeIn(FADE_SLOW), Actions.run(new Runnable() {
				@Override
				public void run() {
					// Shoot upwards towards the enemy paddle at a random angle
					Vector2 ballVel = new Vector2(0, OBJECT_SPEED[level] * SPEED_MUL);
					ballVel.rotate(MathUtils.random(1) == 0 ? MathUtils.random(-FIRST_ANGLE_HIGH, -FIRST_ANGLE_LOW) : MathUtils.random(FIRST_ANGLE_LOW, FIRST_ANGLE_HIGH));
					body.setLinearVelocity(ballVel);
				}
			})));
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			// Enforce a minimum angle
			float angle = body.getLinearVelocity().angle();
			if (angle >= 0 && angle < MIN_ANGLE_HORIZONTAL)
				angle = MIN_ANGLE_HORIZONTAL;
			else if (angle <= 360 && angle > 360 - MIN_ANGLE_HORIZONTAL)
				angle = 360 - MIN_ANGLE_HORIZONTAL;
			else if (angle >= 180 && angle < 180 + MIN_ANGLE_HORIZONTAL)
				angle = 180 + MIN_ANGLE_HORIZONTAL;
			else if (angle <= 180 && angle > 180 - MIN_ANGLE_HORIZONTAL)
				angle = 180 - MIN_ANGLE_HORIZONTAL;
			
			// Vertical
			else if (angle >= 90 && angle <= 90 + MIN_ANGLE_VERTICAL)
				angle = 90 + MIN_ANGLE_VERTICAL;
			else if (angle <= 90 && angle >= 90 - MIN_ANGLE_VERTICAL)
				angle = 90 - MIN_ANGLE_VERTICAL;
			else if (angle >= 270 && angle <= 270 + MIN_ANGLE_VERTICAL)
				angle = 270 + MIN_ANGLE_VERTICAL;
			else if (angle <= 270 && angle >= 270 - MIN_ANGLE_VERTICAL)
				angle = 270 - MIN_ANGLE_VERTICAL;
			
			// Set angle and cap the length of our velocity
			body.setLinearVelocity(body.getLinearVelocity().cpy().setAngle(angle).setLength(OBJECT_SPEED[level] * SPEED_MUL));
			
			// Move ball back if it is pushed out of the wall.. Not sure how else to stop this.
			if (body.getPosition().x < (bounds.x + BALL_RADIUS) / PPM)
				body.setTransform((bounds.x + BALL_RADIUS) / PPM, body.getPosition().y, 0);
			if (body.getPosition().x > (bounds.x + bounds.width - BALL_RADIUS) / PPM)
				body.setTransform((bounds.x + bounds.width - BALL_RADIUS) / PPM, body.getPosition().y, 0);
		}
		
		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "leftWall" || other.getName() == "rightWall") {
				// Slight, random angle changes to stop loops
				body.setLinearVelocity(body.getLinearVelocity().cpy().setAngle(body.getLinearVelocity().angle() + 2 * MathUtils.randomSign()));
			}
			else if (other.getName() == "player") {
				numBounces++;
				if (numBounces >= OBJECT_COUNT[level]) {
					addAction(Actions.sequence(Actions.fadeOut(FADE_MEDIUM), Actions.run(new Runnable() {
						@Override
						public void run() {
							hasWon = true;
						}
					})));
				}
			}
		}
	}
}
