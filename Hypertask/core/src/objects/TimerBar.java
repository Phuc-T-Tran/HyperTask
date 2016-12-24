package objects;

import static com.phuctran.hypertask.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.phuctran.hypertask.HyperTask;

public class TimerBar extends Actor {

	private float time, duration;
	
	public TimerBar(float duration) {
		time = 0;
		this.duration = duration;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		time += delta;
		if (time >= duration) {
			time = duration;
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		ShapeRenderer shapes = HyperTask.shapes;
		
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapes.begin();
		shapes.set(ShapeType.Filled);
		Color color = getColor().cpy();
		color.a *= parentAlpha;
		
		shapes.setColor(COLOR_TEXT);
		shapes.rect(getX(), getY(), getWidth() * getScaleX(), getHeight() * getScaleY());
		
		shapes.setColor(COLOR_TEXT.cpy().mul(1,1,1,0.5f));
		shapes.rect(getX(), getY(), getWidth() * getScaleX() * (time / duration), getHeight() * getScaleY());
		
		shapes.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
}
