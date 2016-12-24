package minigames;

import static com.phuctran.hypertask.Constants.PPM;

import java.util.ArrayList;

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
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public class TimingGame extends Minigame {
	private final float BAR_WIDTH = 36f;
	private final float BAR_HEIGHT = 340f;
	private final float PLAYER_HEIGHT = 80f;
	
	private Player player;
	private ArrayList<Bar> bars;
	float nextSpawn, barsSpawned, barsDone;
	
	public TimingGame(World world, Rectangle bounds) {
		super(GameType.Timing, world, bounds);
		
		// Add player and side walls
		player = new Player(world);
		objects.add(player);
		bars = new ArrayList<Bar>();
		
		time = 0;
		nextSpawn = SPAWN_TIME_FIRST;
		barsSpawned = 0;
		barsDone = 0;
	}

	@Override
	public void handleInput(float delta) {
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			if (justTouched(i)) {
				for (final Bar b : bars) {
					if (b.isTouchingPlayer()) {
						if (b.hasActions())
							return;
						
						// Object was tapped. Play flashing animation and then remove the object
						b.setColor(Color.WHITE.cpy().mul(1,1,1,0.75f));//getColor().a = 1;
						b.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(GameObject.FADE_FAST),
				                                                    Actions.scaleBy(1.3f, 1.3f, GameObject.FADE_FAST)),
								                   Actions.run(new Runnable() {
														@Override
														public void run() {
															b.kill();
															barsDone++;
															if (barsDone >= OBJECT_COUNT[level]) {
																hasWon = true;
															}
														}
													})));
						return;
					}
				}
				hasLost = true;
			}
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Spawn a spike
		if (time > nextSpawn && barsSpawned < OBJECT_COUNT[level]) {
			barsSpawned++;
			time = 0;
			Bar bar = new Bar(world);
			objects.add(bar);
			bars.add(bar);
			nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
		}
	}
	
	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.Tap);
		icon.setPosition(bounds.x + bounds.width * 0.5f, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon);
	}
	
	private class Player extends RectObject {
		public Player(World world) {
			super(world, "player", BodyType.StaticBody,
					new Vector2((bounds.x + bounds.width / 2) / PPM, (bounds.y + bounds.height * 0.15f) / PPM), bounds.width, PLAYER_HEIGHT);
			setColor(color.cpy().mul(COLOR_DARK).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_SLOW));
		}
	}
	
	private class Bar extends RectObject {
		private boolean touchingPlayer;
		
		public Bar(World world) {
			super(world, "bar", BodyType.DynamicBody,
					new Vector2((bounds.x + bounds.width * 0.5f) / PPM, (bounds.y + bounds.height) / PPM), BAR_WIDTH, BAR_HEIGHT);
			fixture.setSensor(true);
			body.setSleepingAllowed(false);
			body.setGravityScale(0);
			
			touchingPlayer = false;
			
			// Slide down
			body.setTransform(body.getPosition(), (body.getPosition().x * PPM < bounds.x + bounds.width / 2) ? (float)Math.toRadians(270) : (float)Math.toRadians(90));
			body.setLinearVelocity(new Vector2(0, -OBJECT_SPEED[level]));
			
			// Spawn animation
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(FADE_MEDIUM));
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			// Lose if it reaches the edge of the minigame
			if (body.getPosition().y * PPM  - BAR_WIDTH / 2 < bounds.y) {
				hasLost = true;
			}
		}
		
		public boolean isTouchingPlayer() {
			return touchingPlayer;
		}
		
		@Override
		public void beginCollision(GameObject other) {
			if (other.getName() == "player") {
				touchingPlayer = true;
			}
		}
		
		@Override
		public void endCollision(GameObject other) {
			if (other.getName() == "player") {
				touchingPlayer = false;
			}
		}
	}
}
