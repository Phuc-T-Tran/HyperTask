package containers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.HyperTask;

import screens.PracticeScreen;

public class PracticeContainer extends GameContainer {
	public PracticeContainer(Rectangle bounds) {
		super(bounds);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		background.draw(batch, 1);
		
		if (minigame != null)
			minigame.draw(batch, parentAlpha);
	}
	
	@Override
	protected void winGame() {
		// Background flashes white, then reset game
		Color oldColor = new Color(background.getColor());
		dispose();
		background.addAction(Actions.sequence(Actions.color(Color.WHITE, 0.15f), Actions.color(oldColor, 0.15f)));
	}
	
	@Override
	protected void loseGame() {
		// Background flashes red, then reset game
		Color oldColor = new Color(background.getColor());
		dispose();
		background.addAction(Actions.sequence(Actions.color(Color.RED, 0.15f), Actions.color(oldColor, 0.15f)));
	}
}