package objects;

import static com.phuctran.hypertask.Constants.PPM;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class GameObject extends Actor {
	public static final float COLOR_LIGHT = 1.2f;
	public static final float COLOR_DARK = 0.6f;//0.72f;
	public static final float FADE_FAST = 0.15f;
	public static final float FADE_MEDIUM = 0.35f;
	public static final float FADE_SLOW = 0.55f;
	
	protected World world;
	protected String name;
	
	// Body and fixture must be created by subclasses
	protected Body body;
	protected Fixture fixture;
	
	protected boolean isDead;
	
	public GameObject(World world, String name) {
		this.world = world;
		this.name = name;
		
		isDead = false;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		// Update actor to overlap physics body
		setPosition(body.getPosition().x * PPM - getWidth() / 2, body.getPosition().y * PPM - getHeight() / 2);
		setRotation((float)Math.toDegrees(body.getAngle()));
	}
	
	public void beginCollision(GameObject other) {};
	public void endCollision(GameObject other) {};
	
	public void kill() {
		isDead = true;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public Body getBody() {
		return body;
	}
	
	public Fixture getFixture() {
		return fixture;
	}
	
	public String getName() {
		return name;
	}
	
	public void dispose() {
		remove();
		body.getWorld().destroyBody(body);
	}
}
