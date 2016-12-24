package actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class LabelScaleToAction extends TemporalAction {
	private Label label;
	private float start, end;
	
	public LabelScaleToAction() {
		start = 0;
		end = 0;
	}
	
	public LabelScaleToAction(Label label, float scale, float duration) {
		setLabel(label);
		setScale(scale);
		setDuration(duration);
	}
	
	@Override
	public void update(float percent) {
		if (label == null) return;
		label.setFontScale(start + (end - start) * percent);
	}
	
	@Override
	public void begin() {
		start = label.getFontScaleX();
	}
	
	public void setLabel(Label label) {
		this.label = label;
	}
	
	public void setScale(float scale) {
		end = scale;
	}
}
