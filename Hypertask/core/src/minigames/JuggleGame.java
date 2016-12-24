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

public class JuggleGame extends Minigame {
	private final float BALL_RADIUS = 18;
	private final float PADDLE_WIDTH = 110;
	private final float PADDLE_HEIGHT = 22;
	private final float PADDLE_DISTANCE = 0.10f;
	private final float MAX_PLAYER_SPEED = 32f;
	//private final float BALL_GRAVITYSCALE = 0.4f;
	private final float GRAVITY_DIV = 21;
	private final float SPEED_SCALE = 0.3f;
	
	private Paddle player;
	private Ball ball;
	private float ballsJuggled;
	
	public JuggleGame(World world, Rectangle bounds) {
		super(GameType.Juggle, world, bounds);
		
		ballsJuggled = 0;
		
		// Create the paddles
		player = new Paddle(world, new Vector2((bounds.x + bounds.width / 2) / PPM, (bounds.y + bounds.height * PADDLE_DISTANCE) / PPM));
		objects.add(player);
		
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
		
		// Check win condition
		if (ball.getBody().getPosition().y * PPM < 0)
			hasLost = true;
	}
	
	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.DragHorizontal);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.2f, Align.center);
		stage.addActor(icon);
	}
	
	private void configureWalls() {
		topWall.getFixture().setRestitution(1f);
		leftWall.getFixture().setRestitution(1f);
		rightWall.getFixture().setRestitution(1f);
		
		botWall.getFixture().setSensor(true);
	}

	private class Paddle extends RectObject {
		public Paddle(World world, Vector2 position) {
			super(world, "player", BodyType.KinematicBody,
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
			super(world, "ball", BodyType.DynamicBody, new Vector2(bounds.x + bounds.width / 2, bounds.y + bounds.height - BALL_RADIUS), BALL_RADIUS);
			body.setBullet(true);
			body.setGravityScale(0);
			fixture.setFriction(0);

			// Spawn animation
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.sequence(Actions.fadeIn(FADE_SLOW), Actions.run(new Runnable() {
				@Override
				public void run() {
					body.setGravityScale(OBJECT_SPEED[level] / GRAVITY_DIV);
					body.applyLinearImpulse(new Vector2(MathUtils.randomSign() * OBJECT_SPEED[level] * SPEED_SCALE, 0), body.getPosition(), true);
				}
			})));
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);

			// Move ball back if it is pushed out of the wall.. Not sure how else to stop this.
//			if (body.getPosition().x < (bounds.x + BALL_RADIUS) / PPM)
//				body.setTransform((bounds.x + BALL_RADIUS) / PPM, body.getPosition().y, 0);
//			if (body.getPosition().x > (bounds.x + bounds.width - BALL_RADIUS) / PPM)
//				body.setTransform((bounds.x + bounds.width - BALL_RADIUS) / PPM, body.getPosition().y, 0);
		}
		
		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "player") {
				ballsJuggled++;
				if (ballsJuggled >= OBJECT_COUNT[level]) {
					addAction(Actions.sequence(Actions.fadeOut(FADE_MEDIUM), Actions.run(new Runnable() {
						@Override
						public void run() {
							hasWon = true;
						}
					})));
				}
			}
			else if (other.getName() == "leftWall" || other.getName() == "rightWall") {
				// Slight, random angle changes
				body.setLinearVelocity(body.getLinearVelocity().cpy().setAngle(body.getLinearVelocity().angle() + 2 * MathUtils.randomSign()));
			}
		}
	}
}
