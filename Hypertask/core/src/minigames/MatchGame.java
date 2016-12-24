package minigames;

import static com.phuctran.hypertask.Constants.COLOR_MAIN;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.HyperInput;

import objects.GameObject;
import objects.TriangleShape;
import ui.TutorialIcon;
import ui.TutorialIcon.IconType;

public class MatchGame extends Minigame {
	private final float PLAYER_SIZE = 48;
	//private final float TRIANGLE_SIZE = 400;
	private final float TRIANGLE_ALPHA = 0.3f;
	private final float SPEED_MUL = 15;
	
	private Player player;
	private ArrayList<TriangleShape> triangles;
	private int spikesBlocked, spikesSpawned;
	private float nextSpawn;
	
	public MatchGame(World world, Rectangle bounds) {
		super(GameType.Match, world, bounds);
		spikesBlocked = spikesSpawned = 0;
		nextSpawn = SPAWN_TIME_FIRST;
		
		player = new Player();
		triangles = new ArrayList<TriangleShape>();
	}

	@Override
	public void handleInput(float delta) {
		for (int i = 0; i < HyperInput.MAX_CONTACTS; i++) {
			if (isTouched(i)) {
				Vector2 touchPos = HyperInput.getTouchPos(i);
				float angle = 0;
				
				if (touchPos.x >= bounds.x + bounds.width / 2 - PLAYER_SIZE * 1.5f &&
					touchPos.x <= bounds.x + bounds.width / 2 + PLAYER_SIZE * 1.5f) {
						// Above or below the player
						if (touchPos.y <= bounds.height / 2)
							angle = 180;
						else
							angle = 0;
					}
				else if (touchPos.y >= bounds.height / 2 - PLAYER_SIZE * 1.5f &&
					     touchPos.y <= bounds.height / 2 + PLAYER_SIZE * 1.5f) {
					// To the left or right of the player
					if (touchPos.x <= bounds.x + bounds.width / 2)
						angle = 90;
					else
						angle = 270;
				}
				else {
					continue;
				}
				
				player.setRotation(angle);
				break;
			}
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		player.act(delta);
		
		for (TriangleShape triangle : triangles) {
			triangle.act(delta);
		}
		
		// Spawn spikes
		if (time > nextSpawn && spikesSpawned < OBJECT_COUNT[level]) {
			time = 0;
			spikesSpawned++;
			nextSpawn = MathUtils.random(SPAWN_TIME_MIN[level], SPAWN_TIME_MAX[level]);
			triangles.add(new Triangle());
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		for (TriangleShape triangle : triangles) {
			triangle.draw(batch, parentAlpha);
		}
		player.draw(batch, parentAlpha);
	}
	
	@Override
	public void addTutorial(Stage stage) {
		TutorialIcon left = new TutorialIcon(IconType.Tap);
		left.setPosition(bounds.x + 32, bounds.y + bounds.height * 0.5f, Align.center);
		stage.addActor(left);
		
		TutorialIcon right = new TutorialIcon(IconType.Tap);
		right.setPosition(bounds.x + bounds.width - 32, bounds.y + bounds.height * 0.5f, Align.center);
		stage.addActor(right);
		
		TutorialIcon top = new TutorialIcon(IconType.Tap);
		top.setPosition(bounds.x + bounds.width * 0.5f, bounds.y + bounds.height * 0.5f + (bounds.width / 2 - 32), Align.center);
		stage.addActor(top);
		
		TutorialIcon bot = new TutorialIcon(IconType.Tap);
		bot.setPosition(bounds.x + bounds.width * 0.5f, bounds.y + bounds.height * 0.5f - (bounds.width / 2 - 32), Align.center);
		stage.addActor(bot);
	}
	
	private class Player extends TriangleShape {
		public Player() {
			setBounds(0, 0, PLAYER_SIZE, PLAYER_SIZE);
			setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2, Align.center);
			setColor(color.cpy().mul(GameObject.COLOR_LIGHT).mul(1,1,1,0));
			addAction(Actions.fadeIn(GameObject.FADE_SLOW));
		}
	}
	
	private class Triangle extends TriangleShape {
		public Triangle() {
			float angle = 0;
			float offsetAmount = -10;
			float offsetX = 0;
			float offsetY = 0;
			switch(MathUtils.random(1,4)) {
			case 1:
				angle = 0;
				offsetY = -offsetAmount;
				break;
			case 2:
				angle = 90;
				offsetX = offsetAmount;
				break;
			case 3:
				angle = 180;
				offsetY = offsetAmount;
				break;
			case 4:
				angle = 270;
				offsetX = -offsetAmount;
				break;
			}
			
			setRotation(angle);
			setBounds(0, 0, bounds.width, bounds.width);
			setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2, Align.center);
			setColor(COLOR_MAIN.cpy().mul(1,1,1,0));
			addAction(Actions.sequence(Actions.parallel(Actions.alpha(TRIANGLE_ALPHA, GameObject.FADE_MEDIUM),
					                                    Actions.scaleTo(PLAYER_SIZE / bounds.width, PLAYER_SIZE / bounds.width, 1 / OBJECT_SPEED[level] * SPEED_MUL)),
									   Actions.run(new Runnable() {
										@Override
										public void run() {
											checkMatch();
										}
									   })));
		}
		
		private void checkMatch() {
			if (player.getRotation() == this.getRotation()) {
				setColor(Color.WHITE.cpy().mul(1, 1, 1, TRIANGLE_ALPHA));
				addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(GameObject.FADE_FAST),
					                   Actions.scaleBy(1.05f, 1.05f, GameObject.FADE_FAST)),
									   Actions.run(new Runnable() {
										@Override
										public void run() {
											spikesBlocked++;
											if (spikesBlocked >= OBJECT_COUNT[level]) {
												hasWon = true;
											}
											triangles.remove(this);
										}
									   })));
			}
			else
				hasLost = true;
		}
	}
}