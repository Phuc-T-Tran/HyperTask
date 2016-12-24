package com.phuctran.hypertask;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class HyperInput extends InputAdapter {
	public static int MAX_CONTACTS = 10;
	
	private class TouchData {
		public int x = -1;
		public int y = -1;
		
		public boolean isTouched = false;
		public boolean justTouched = false;
	}
	public static ArrayList<TouchData> touches;
	
	
	public HyperInput() {
		clear();
	}
	
	public void clear() {
		touches = new ArrayList<TouchData>();
		for (int i = 0; i < MAX_CONTACTS; i++)
			touches.add(new TouchData());
	}
	
	public static boolean justTouched() {
		return touches.get(0).justTouched;
	}
	
	public static boolean justTouched(int pointer) {
		if (pointer >= 0 && pointer < MAX_CONTACTS)
			return touches.get(pointer).justTouched;
		else
			return true;
	}
	
	public static boolean isTouched() {
		return touches.get(0).isTouched;
	}
	
	public static boolean isTouched(int pointer) {
		if (pointer >= 0 && pointer < MAX_CONTACTS)
			return touches.get(pointer).isTouched;
		else
			return true;
	}
	
	public static Vector2 getTouchPos() {
		Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		HyperTask.camera.unproject(touchPos);
		return new Vector2(touchPos.x, touchPos.y);
	}
	
	public static Vector2 getTouchPos(int index) {
		Vector3 touchPos = new Vector3(Gdx.input.getX(index), Gdx.input.getY(index), 0);
		HyperTask.camera.unproject(touchPos);
		return new Vector2(touchPos.x, touchPos.y);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (pointer >= 0 && pointer < MAX_CONTACTS) {
			touches.get(pointer).x = screenX;
			touches.get(pointer).y = screenY;
			touches.get(pointer).justTouched = true;
			touches.get(pointer).isTouched = true;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (pointer >= 0 && pointer < MAX_CONTACTS) {
			touches.get(pointer).x = 0;
			touches.get(pointer).y = 0;
			touches.get(pointer).justTouched = false;
			touches.get(pointer).isTouched = false;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (pointer >= 0 && pointer < MAX_CONTACTS) {
			touches.get(pointer).x = screenX;
			touches.get(pointer).y = screenY;
			touches.get(pointer).justTouched = false;
			touches.get(pointer).isTouched = true;
		}
		return true;
	}
}
