package containers;

import static com.phuctran.hypertask.Constants.COLOR_MAIN;
import static com.phuctran.hypertask.Constants.GameColors;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.phuctran.hypertask.Constants.GameType;

import minigames.CatchGame;
import minigames.ClimbGame;
import minigames.CollectGame;
import minigames.DodgeGame;
import minigames.FallGame;
import minigames.FlyGame;
import minigames.GuardGame;
import minigames.JuggleGame;
import minigames.JumpGame;
import minigames.MatchGame;
import minigames.Minigame;
import minigames.PongGame;
import minigames.ShootGame;
import minigames.SquaresGame;
import minigames.TapGame;
import minigames.TimingGame;
import objects.RectShape;

public abstract class GameContainer {
	public static final float BG_FADEIN = 0.6f;
	public static final float BG_FADEOUT = 0.4f;
	
	protected Rectangle bounds;
	protected RectShape background;
	protected Minigame minigame;
	
	public boolean hasWon, hasLost;
	
	public GameContainer(Rectangle bounds) {
		this.bounds = bounds;
		
		initBackground();
		minigame = null;
	}
	
	public void handleInput(float delta) {		
		if (minigame != null)
			minigame.handleInput(delta);
	}
	
	public void update(float delta) {
		background.act(delta);
		
		if (minigame != null) {
			minigame.update(delta);
			
			if (minigame.hasWon())
				winGame();
			else if (minigame.hasLost())
				loseGame();
		}
	}

	public void draw(SpriteBatch batch, float parentAlpha) {
		background.draw(batch, parentAlpha);
		if (minigame != null)
			minigame.draw(batch, parentAlpha);
	}
	
	protected abstract void winGame();
	protected abstract void loseGame();
	
	public void setGame(World world, GameType type) {
		removeGame();
		
		switch(type) {
			case Catch:
				minigame = new CatchGame(world, bounds);
				break;
			case Climb:
				minigame = new ClimbGame(world, bounds);
				break;
			case Dodge:
				minigame = new DodgeGame(world, bounds);
				break;
			case Fall:
				minigame = new FallGame(world, bounds);
				break;
			case Juggle:
				minigame = new JuggleGame(world, bounds);
				break;
			case Guard:
				minigame = new GuardGame(world, bounds);
				break;
			case Jump:
				minigame = new JumpGame(world, bounds);
				break;
			case Pong:
				minigame = new PongGame(world, bounds);
				break;
			case Tap:
				minigame = new TapGame(world, bounds);
				break;
			case Fly:
				minigame = new FlyGame(world, bounds);
				break;
			case Shoot:
				minigame = new ShootGame(world, bounds);
				break;
			case Collect:
				minigame = new CollectGame(world, bounds);
				break;
			case Match:
				minigame = new MatchGame(world, bounds);
				break;
			case Squares:
				minigame = new SquaresGame(world, bounds);
				break;
			case Timing:
				minigame = new TimingGame(world, bounds);
				break;
			default:
				minigame = null;
				return;
		}
		
		background.addAction(Actions.color(GameColors[type.ordinal()], 0.5f));
	}
	
	public void dispose() {
		removeGame();
		background.clearActions();
	}
	
	public void removeGame() {
		if (minigame != null) {
			minigame.dispose();
			minigame = null;
		}
		hasWon = false;
		hasLost = false;
	}
	
	public void reset() {
		background.clearActions();
		background.setColor(COLOR_MAIN);
		removeGame();
	}
	
	public Rectangle getBounds() { return bounds; }
	public RectShape getBackground() { return background; }
	public Minigame getMinigame() { return minigame; }
	
	private void initBackground() {
		background = new RectShape();
		background.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		background.setColor(COLOR_MAIN);
	}
}
