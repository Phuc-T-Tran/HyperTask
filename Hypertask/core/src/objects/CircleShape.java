package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.phuctran.hypertask.HyperTask;

public class CircleShape extends Actor {
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		ShapeRenderer shapes = HyperTask.shapes;
		
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapes.begin();
		
		shapes.set(ShapeType.Filled);
		shapes.setColor(getColor());
		
		shapes.identity();
		shapes.translate(getX() + getWidth() / 2, getY() + getWidth() / 2, 0);
		shapes.scale(getScaleX(), getScaleY(), 1);
		shapes.rotate(0, 0, 1, getRotation());
		
		shapes.circle(0, 0, getWidth() / 2);
		
		shapes.identity();
		
		shapes.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
}
