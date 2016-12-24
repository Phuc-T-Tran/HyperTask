package minigames;

import static com.phuctran.hypertask.Constants.PPM;

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

public class SquaresGame extends Minigame {
	private final float OBJECT_SIZE = 58f;
	
	private int numTapped, numSpawned;
	private float nextSpawn;
	
	public SquaresGame(World world, Rectangle bounds) {
		super(GameType.Squares, world, bounds);
		numTapped = numSpawned = 0;
		nextSpawn = SPAWN_TIME_FIRST;
	}

	@Override
	public void handleInput(float delta) {
		for (GameObject o : objects) {
			if (o.getName() == "square")
				((Square)o).handleInput(delta);
			else if (o.getName() == "triangle")
				((Triangle)o).handleInput(delta);
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
			spawnRow();
		}
	}

	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon icon = new TutorialIcon(IconType.Tap);
		icon.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.32f, Align.center);
		stage.addActor(icon);
	}
	
	private void spawnRow() {
		Vector2 pos1 = new Vector2(bounds.x + OBJECT_SIZE / 2 + 48.5f / 2, bounds.y + bounds.height);
		Vector2 pos2 = new Vector2(pos1.x + OBJECT_SIZE + 48.5f, bounds.y + bounds.height);
		Vector2 pos3 = new Vector2(pos2.x + OBJECT_SIZE + 48.5f, bounds.y + bounds.height);
		Vector2 pos4 = new Vector2(pos3.x + OBJECT_SIZE + 48.5f, bounds.y + bounds.height);
		Vector2[] vecs = new Vector2[] { pos1, pos2, pos3, pos4 };
		int i = MathUtils.random(3);
		
		Square square = new Square(world, vecs[i].scl(1/PPM));
		objects.add(square);
		for (int j = 0; j < 3; j++) {
			i++;
			if (i > 3)
				i = 0;
			Triangle triangle = new Triangle(world, vecs[i].scl(1/PPM));
			objects.add(triangle);
		}
	}
	
	private class Square extends RectObject {
		public Square(World world, Vector2 position) {
			super(world, "square", BodyType.KinematicBody, position, OBJECT_SIZE, OBJECT_SIZE);
			body.setLinearVelocity(0, -OBJECT_SPEED[level]);
			
			setColor(color.cpy().mul(COLOR_LIGHT).mul(1f,1f,1f,0));
			addAction(Actions.fadeIn(FADE_MEDIUM));
		}
		
		public void handleInput(float delta) {
			for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
				if (justTouched(i)) {
					Vector2 touchPos = HyperInput.getTouchPos(i);
					Vector2 objectPos = body.getPosition().cpy().scl(PPM);
					
					// Check if tap is within object's bounds
					float extra = 40f;
					Rectangle objectBounds = new Rectangle(objectPos.x - OBJECT_SIZE / 2 - extra, objectPos.y - OBJECT_SIZE / 2 - extra,
							                               OBJECT_SIZE + extra * 2, OBJECT_SIZE + extra * 2);
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
					}
				}
			}
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			if (body.getPosition().y * PPM - OBJECT_SIZE / 2 < bounds.y)
				hasLost = true;
		}
	}
	
	private class Triangle extends TriangleObject {
		public Triangle(World world, Vector2 position) {
			super(world, "triangle", BodyType.KinematicBody, position, OBJECT_SIZE);
			body.setLinearVelocity(0, -OBJECT_SPEED[level]);
			
			setColor(color.cpy().mul(COLOR_DARK).mul(1f,1f,1f,0));
			addAction(Actions.fadeIn(FADE_MEDIUM));
		}
		
		public void handleInput(float delta) {
			for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
				if (justTouched(i)) {
					Vector2 touchPos = HyperInput.getTouchPos(i);
					Vector2 objectPos = body.getPosition().cpy().scl(PPM);
					
					// Check if tap is within object's bounds
					Rectangle objectBounds = new Rectangle(objectPos.x - OBJECT_SIZE / 2, objectPos.y - OBJECT_SIZE / 2, OBJECT_SIZE, OBJECT_SIZE);
					if (objectBounds.contains(touchPos)) {
						hasLost = true;
						return;
					}
				}
			}
		}
	}
}