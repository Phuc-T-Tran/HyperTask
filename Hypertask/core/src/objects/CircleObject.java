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
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.phuctran.hypertask.HyperTask;

public class CircleObject extends GameObject {
	private float radius;
	
	public CircleObject(World world, String name, BodyType bodyType, Vector2 position, float radius) {
		super(world, name);
		this.radius = radius;
		setBounds(0,0, radius * 2, radius * 2);
		setOrigin(radius, radius);
		setWidth(radius * 2);
		setHeight(radius * 2);
		createBody(bodyType, position);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		ShapeRenderer shapes = HyperTask.shapes;
		
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapes.begin();
		
		shapes.set(ShapeType.Filled);
		shapes.setColor(getColor());
		
		shapes.identity();
		shapes.translate(getX() + radius, getY() + radius, 0);
		shapes.scale(getScaleX(), getScaleY(), 1);
		shapes.rotate(0, 0, 1, getRotation());
		
		shapes.circle(0, 0, radius);
		
		shapes.identity();
		
		shapes.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
	
	private void createBody(BodyType bodyType, Vector2 position) {
		BodyDef bdef = new BodyDef();
		bdef.type = bodyType;
		bdef.position.set(position.scl(1 / PPM));
		body = world.createBody(bdef);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(radius / PPM);
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fixture = body.createFixture(fdef);
		fixture.setUserData(this);
		
		shape.dispose();
	}
}
