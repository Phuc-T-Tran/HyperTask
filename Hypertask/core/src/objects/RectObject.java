package objects;

import static com.phuctran.hypertask.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.phuctran.hypertask.HyperTask;

public class RectObject extends GameObject {
	
	public RectObject(World world, String name, BodyType bodyType, Vector2 position, float width, float height) {
		super(world, name);
		createBody(bodyType, position, width, height);		
		setBounds(0, 0, width, height);
		setWidth(width);
		setHeight(height);
		setOrigin(width / 2, height / 2);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		ShapeRenderer shapes = HyperTask.shapes;
		
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapes.begin();
		
		shapes.set(ShapeType.Filled);
		shapes.setColor(getColor());

		shapes.rect(getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

		shapes.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
	
	private void createBody(BodyType bodyType, Vector2 position, float width, float height) {
		BodyDef bdef = new BodyDef();
		bdef.type = bodyType;
		bdef.position.set(position);
		body = world.createBody(bdef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2 / PPM, height / 2 / PPM);
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fixture = body.createFixture(fdef);
		fixture.setUserData(this);
		
		shape.dispose();
	}
}
