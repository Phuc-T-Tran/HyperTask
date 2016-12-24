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
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.phuctran.hypertask.HyperTask;

public class PolyObject extends GameObject {
	private Vector2[] vertices;
	private Vector2[] drawVerts;
	private float thickness;
	
	public PolyObject(World world, String name, BodyType bodyType, Vector2 position, Vector2[] vertices, Vector2[] drawVerts, float size, float thickness) {
		super(world, name);
		
		this.vertices = vertices;
		this.drawVerts = drawVerts;
		this.thickness = thickness;
		
		setBounds(0, 0, size, size);
		setWidth(size);
		setHeight(size);
		setOrigin(position.x, position.y);
		
		createBody(bodyType, position);
	}
	
	private void createBody(BodyType bodyType, Vector2 position) {		
		BodyDef bdef = new BodyDef();
		bdef.type = bodyType;
		bdef.position.set(position);
		body = world.createBody(bdef);
		
		ChainShape shape = new ChainShape();
		shape.createChain(vertices);
		
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
		
		for (int i = 0; i < drawVerts.length - 1; i++) {
			Vector2 v1 = new Vector2(drawVerts[i].x * PPM + getX() + getWidth() / 2, drawVerts[i].y * PPM + getY() + getHeight() / 2);
			Vector2 v2 = new Vector2(drawVerts[i + 1].x * PPM + getX() + getWidth() / 2, drawVerts[i + 1].y * PPM + getY() + getHeight() / 2);
			shapes.rectLine(v1, v2, thickness);
		}
		
		shapes.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}
}
