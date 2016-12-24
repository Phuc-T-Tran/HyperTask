package com.phuctran.hypertask;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import objects.GameObject;

public class WorldContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		if (fixA != null && fixA.getUserData() != null && fixB != null && fixB.getUserData() != null) {
			GameObject objA = (GameObject)(fixA.getUserData());
			GameObject objB = (GameObject)(fixB.getUserData());
	
			if (objA == null || objB == null) return;
			objA.beginCollision(objB);
			
			// In case the first part removes any objects
			if (objA != null && objB != null)
				objB.beginCollision(objA);
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		if (fixA != null && fixA.getUserData() != null && fixB != null && fixB.getUserData() != null) {
			GameObject objA = (GameObject)(fixA.getUserData());
			GameObject objB = (GameObject)(fixB.getUserData());
	
			if (objA == null || objB == null) return;
			objA.endCollision(objB);
			
			// In case the first part removes any objects
			if (objA != null && objB != null)
				objB.endCollision(objA);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
