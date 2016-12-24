package actions;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class SpriteAlphaAction extends TemporalAction {
	private Sprite sprite;
	private float start, end;
	
	public SpriteAlphaAction() {
		start = 0;
		end = 0;
	}
	
	@Override
	public void update(float percent) {
		if (sprite == null) return;
		sprite.setAlpha(start + (end - start) * percent);
	}
	
	@Override
	public void begin() {
		start = sprite.getColor().a;
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public void setAlpha(float a) {
		end = a;
	}
}
