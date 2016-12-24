package ui;

import static com.phuctran.hypertask.Constants.COLOR_TEXT;
import static com.phuctran.hypertask.Constants.ICON_DRAG;
import static com.phuctran.hypertask.Constants.ICON_DRAGHORIZONTAL;
import static com.phuctran.hypertask.Constants.ICON_HOLD;
import static com.phuctran.hypertask.Constants.ICON_TAP;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.phuctran.hypertask.HyperTask;

public class TutorialIcon extends Image {
	public enum IconType { Tap, Hold, Drag, DragHorizontal }
	public TutorialIcon(IconType type) {
		TextureRegion texture = new TextureRegion();
		switch(type) {
		case Tap:
			texture.setRegion(HyperTask.res.getTexture(ICON_TAP));
			break;
		case Hold:
			texture.setRegion(HyperTask.res.getTexture(ICON_HOLD));
			break;
		case Drag:
			texture.setRegion(HyperTask.res.getTexture(ICON_DRAG));
			break;
		case DragHorizontal:
			texture.setRegion(HyperTask.res.getTexture(ICON_DRAGHORIZONTAL));
			break;
		}
		
		setDrawable(new TextureRegionDrawable(texture));
		setScaling(Scaling.stretch);
		setAlign(Align.center);
		setSize(getPrefWidth(), getPrefHeight());
		
		setColor(COLOR_TEXT.cpy().mul(1,1,1,0.8f));
		//setSize(64,64);
		addAction(Actions.sequence(Actions.alpha(0),
				                   Actions.delay(0.65f),
				                   Actions.fadeIn(0.35f),
				                   Actions.delay(1.75f),
				                   Actions.fadeOut(0.35f),
				                   Actions.removeActor()));
	}
}
