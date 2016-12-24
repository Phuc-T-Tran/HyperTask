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

public class TriangleObject extends GameObject {
	private Vector2[] vertices;
	
	public TriangleObject(World world, String name, BodyType bodyType, Vector2 position, float size) {
		super(world, name);
		
		vertices = new Vector2[] {
				new Vector2(-size / 2 / PPM, -size / 2 / PPM),
				new Vector2(size / 2 / PPM, -size / 2 / PPM),
				new Vector2(0, size / 2 / PPM)};
		
		createBody(bodyType, position);
		setBounds(0, 0, size, size);
		setWidth(size);
		setHeight(size);
	}
	
	private void createBody(BodyType bodyType, Vector2 position) {
		BodyDef bdef = new BodyDef();
		bdef.type = bodyType;
		bdef.position.set(position);
		body = world.createBody(bdef);
		
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fixture = body.createFixture(fdef);
		fixture.setUserData(this);
		
		shape.dispose();
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
		shapes.translate(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);
		shapes.scale(getScaleX(), getScaleY(), 1);
		shapes.rotate(0, 0, 1, getRotation());
		
		shapes.triangle(vertices[0].x * PPM, vertices[0].y * PPM,
				        vertices[1].x * PPM, vertices[1].y * PPM,
				        vertices[2].x * PPM, vertices[2].y * PPM);
		
		shapes.identity();
		shapes.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
}
