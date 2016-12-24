package containers;

import static com.phuctran.hypertask.Constants.GameColors;
import static com.phuctran.hypertask.Constants.GameNames;
import static com.phuctran.hypertask.Constants.NUM_GAMETYPES;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.HyperTask;

public class SymbolRoulette extends Group {
	private static final float SPIN_SPEED = 24; // Speed at which symbols spin
	private static final float SPAWN_RATE = 0.1f; // Interval at which symbols appear
	
	private Rectangle bounds;
	private ArrayList<RouletteSymbol> symbols;
	
	private int index;
	private boolean spinning;
	private float spawnTimer;
	
	public SymbolRoulette(Rectangle bounds) {
		this.bounds = bounds;
		symbols = new ArrayList<RouletteSymbol>();
	
		index = 0;
		spinning = false;
		spawnTimer = SPAWN_RATE;
		
		setOrigin(Align.center);
		debugAll();
	}
	
	public void update(float delta) {
		super.act(delta);
		
		if (spinning) {
			spawnTimer += delta;
			
			// Shoot a symbol downwards every SPIN_RATE seconds
			if (spawnTimer >= SPAWN_RATE) {
				spawnTimer = 0;
				
				RouletteSymbol symbol = new RouletteSymbol(index);
				symbol.getColor().a = 0;
				symbol.addAction(Actions.fadeIn(0.05f));
				symbol.addAction(Actions.forever(Actions.moveBy(0, -SPIN_SPEED)));
				symbols.add(symbol);
				addActor(symbol);

				index = (index + 1) % GameType.values().length;
			}
		}
	}
	
	public void start() {
		if (spinning) return;
		
		spinning = true;
		spawnTimer = SPAWN_RATE;
		index = MathUtils.random(NUM_GAMETYPES - 1);
		
		clearChildren();
	}
	
	public GameType stop() {
		if (spinning == false) return null;
		spinning = false;

		// Get an acceptable GameType
		GameType indexType = GameType.values()[index];
		while (PlayContainer.lastTypes.contains(indexType)) {
			index = MathUtils.random(NUM_GAMETYPES - 1);
			indexType = GameType.values()[index];
		}
		PlayContainer.lastTypes.add(indexType);
		if (PlayContainer.lastTypes.size() > 5) {
			PlayContainer.lastTypes.remove(0);
		}
		
		// Hide all symbols
		for (RouletteSymbol symbol : symbols) {
			symbol.addAction(Actions.fadeOut(0.05f));
		}
		
		// Show the symbol we landed on
		Image symbol = new Image(HyperTask.res.getTexture(GameNames[index]));
		symbol.setOrigin(Align.center);
		symbol.setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2, Align.center);
		symbol.setColor(GameColors[index]);
		symbol.addAction(Actions.sequence(Actions.scaleTo(4f, 4f, 0.2f), Actions.delay(0.18f), Actions.scaleTo(1f, 1f, 0.18f)));
		addActor(symbol);

		return indexType;
	}
	
	public void cancel() {
		spinning = false;
		spawnTimer = SPAWN_RATE;
		index = MathUtils.random(NUM_GAMETYPES - 1);
		
		clearChildren();
	}
	
	public boolean spinning() { return spinning; }
	
	private class RouletteSymbol extends Image {
		private boolean isDone = false;
		
		public RouletteSymbol(int type) {
			super(HyperTask.res.getTexture(GameNames[type]));
			setPosition(bounds.x + bounds.width / 2, bounds.y + bounds.height * 0.65f, Align.center);
			setColor(GameColors[type]);
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			
			if (getY() < bounds.y + bounds.height * 0.4f && !isDone) {
				isDone = true;
				addAction(Actions.sequence(Actions.fadeOut(0.05f), Actions.removeActor()));
			}
		}
	}
}
