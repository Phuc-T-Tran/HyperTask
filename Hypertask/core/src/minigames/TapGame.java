package minigames;

import static com.phuctran.hypertask.Constants.PPM;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
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
import objects.CircleShape;
import objects.GameObject;
import objects.TimerBar;
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public class TapGame extends Minigame {
	private final float OBJECT_SIZE = 86f;
	
	private int numTapped, numSpawned;
	private float nextSpawn;
	
	public TapGame(World world, Rectangle bounds) {
		super(GameType.Tap, world, bounds);
		numTapped = numSpawned = 0;
		nextSpawn = SPAWN_TIME_FIRST;
	}

	@Override
	public void handleInput(float delta) {
		for (GameObject o : objects) {
			if (o.getName() == "tappable")
				((Tappable)o).handleInput(delta);
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Spawn squares
		if (time > nextSpawn && numSpawned < OBJECT_COUNT[level]) {
			time = 0;
			numSpawned++;
			nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
			spawnSquare();
		}
	}

	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.Tap);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon);
	}
	
	private void spawnSquare() {
		Vector2 spawnPos = new Vector2();
		boolean conflictingSpawns;
		
		// Randomly choose a spawn until it does not conflict with any other squares
		do {
			conflictingSpawns = false;
			spawnPos.set(MathUtils.random(bounds.x + OBJECT_SIZE / 2, bounds.x + bounds.width - OBJECT_SIZE / 2),
                         MathUtils.random(bounds.y + OBJECT_SIZE / 2, bounds.y + bounds.height - OBJECT_SIZE / 2));
			
			for (GameObject o : objects) {
				Vector2 oPos = o.getBody().getPosition().cpy().scl(PPM);
				Rectangle bounds = new Rectangle(oPos.x - OBJECT_SIZE, oPos.y - OBJECT_SIZE, OBJECT_SIZE * 2f, OBJECT_SIZE * 2f);
				
				if (bounds.contains(spawnPos)) {
					conflictingSpawns = true;
					break;
				}
			}
		} while (conflictingSpawns);
		
		objects.add(new Tappable(world, spawnPos));
	}
	
	private class Tappable extends CircleObject {
		private float timeAlive;
		private CircleShape circle;
		
		public Tappable(World world, Vector2 position) {
			super(world, "tappable", BodyType.StaticBody, position, OBJECT_SIZE / 2);

			setColor(color.cpy().mul(COLOR_LIGHT).mul(1f,1f,1f,0));
			addAction(Actions.alpha(0.2f, FADE_MEDIUM));
			
			circle = new CircleShape();
			circle.setBounds(position.x * PPM - OBJECT_SIZE / 2, position.y * PPM - OBJECT_SIZE / 2, OBJECT_SIZE, OBJECT_SIZE);
			circle.setColor(color.cpy().mul(COLOR_LIGHT).mul(1,1,1,0));
			circle.addAction(Actions.fadeIn(FADE_MEDIUM));
		}
		
		public void handleInput(float delta) {
			for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
				if (justTouched(i)) {
					Vector2 touchPos = HyperInput.getTouchPos(i);
					Vector2 objectPos = body.getPosition().cpy().scl(PPM);
					
					// Check if tap is within object's bounds
					Rectangle objectBounds = new Rectangle(objectPos.x - OBJECT_SIZE / 2, objectPos.y - OBJECT_SIZE / 2, OBJECT_SIZE, OBJECT_SIZE);
					if (objectBounds.contains(touchPos)) {
						// Object was tapped. Play flashing animation and then remove the object
						setColor(Color.WHITE.cpy().mul(1,1,1,0.75f));//getColor().a = 1;
						addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(FADE_FAST),
				                                                    Actions.scaleBy(1.3f, 1.3f, FADE_FAST)),
								                   Actions.run(new Runnable() {
														@Override
														public void run() {
															kill();
															numTapped++;
															if (numTapped >= OBJECT_COUNT[level])
																hasWon = true;
														}
													})));
						circle.addAction(Actions.fadeOut(FADE_FAST));
						return;
					}
				}
			}
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			circle.act(delta);
			
			timeAlive += delta;
			float tapTime = OBJECT_TIME[level];
			if (timeAlive > tapTime + FADE_MEDIUM)
				timeAlive = tapTime + FADE_MEDIUM;
				
			circle.setScale(1 - (timeAlive / (tapTime + FADE_MEDIUM)));
			if (timeAlive >= tapTime + FADE_MEDIUM) {
				hasLost = true;
			}
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			circle.draw(batch, parentAlpha);
		}
	}
}