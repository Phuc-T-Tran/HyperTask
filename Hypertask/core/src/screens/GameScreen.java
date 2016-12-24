package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.phuctran.hypertask.HyperTask;

public abstract class GameScreen extends ScreenAdapter {
	// Game reference
	protected HyperTask game;
	
	// Stage
	protected Stage stage;
	
	public GameScreen(HyperTask game) {
		this.game = game;
		stage = new Stage(HyperTask.view);
	}
	
	public void handleInput(float delta) {};
	
	/*
	 * Update the time this screen has been active
	 */
	public void update(float delta) {};

	/*
	 * Called every loop
	 */
	@Override
	public void render(float delta) {};
	
	/*
	 * Only draws and does not handle input or update
	 */
	public void draw(SpriteBatch batch) {};

	/*
	 *  Return the screen to it's initial state
	 */
	public void reset() {};
	
	@Override
	public void dispose() {
		stage.dispose();
		super.dispose();
	}
	
	protected void clearScreen() {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}
