package ui;

import static com.phuctran.hypertask.Constants.CLICK_VOLUME;
import static com.phuctran.hypertask.Constants.COLOR_TEXT;
import static com.phuctran.hypertask.Constants.FONT_MEDIUM;
import static com.phuctran.hypertask.Constants.FONT_SMALL;
import static com.phuctran.hypertask.Constants.SOUND_CLICK;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.AudioManager;
import com.phuctran.hypertask.HyperTask;

import actions.LabelScaleToAction;
import minigames.Minigame;
import objects.TriangleShape;

public class LevelSelector extends Table {
	public Label levelLabel;
	public TriangleShape leftArrow, rightArrow;
	
	public LevelSelector() {
		// Mode Label
		levelLabel = new Label("LEVEL " + Integer.toString(Minigame.level), new Label.LabelStyle(HyperTask.res.getFont(FONT_MEDIUM), COLOR_TEXT));
		levelLabel.setAlignment(Align.center);

		// Left and right buttons
		float arrowSize = 46;
		leftArrow = new TriangleShape();
		leftArrow.setSize(46, 46);
		leftArrow.setOrigin(leftArrow.getWidth() / 2, leftArrow.getHeight() / 2);
		leftArrow.rotateBy(90);
		leftArrow.setColor(COLOR_TEXT);
		leftArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y){
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				leftArrow.addAction(Actions.sequence(Actions.scaleTo(1.4f, 1.4f, 0.1f), Actions.scaleTo(1f, 1f, 0.1f)));
				int level = Minigame.level - 1;
				if (level < 0)
					level = Minigame.MAX_LEVEL;
				Minigame.setLevel(level);
				LabelScaleToAction scaleUp = new LabelScaleToAction(levelLabel, 1.2f, 0.1f);
				LabelScaleToAction scaleDown = new LabelScaleToAction(levelLabel, 1f, 0.1f);
				levelLabel.addAction(Actions.sequence(scaleUp, scaleDown));
			}
		});
		
		rightArrow = new TriangleShape();
		rightArrow.setSize(46, 46);
		rightArrow.setOrigin(rightArrow.getWidth() / 2, rightArrow.getHeight() / 2);
		rightArrow.rotateBy(270);
		rightArrow.setColor(COLOR_TEXT);
		rightArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y){
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				rightArrow.addAction(Actions.sequence(Actions.scaleTo(1.2f, 1.2f, 0.1f), Actions.scaleTo(1f, 1f, 0.1f)));
				int level = Minigame.level + 1;
				if (level > Minigame.MAX_LEVEL)
					level = 0;
				Minigame.setLevel(level);
				LabelScaleToAction scaleUp = new LabelScaleToAction(levelLabel, 1.2f, 0.1f);
				LabelScaleToAction scaleDown = new LabelScaleToAction(levelLabel, 1f, 0.1f);
				levelLabel.addAction(Actions.sequence(scaleUp, scaleDown));
			}
		});
		
		setBounds(0,0,400,50);
		add(leftArrow).expandX();
		add(levelLabel).expandX();
		add(rightArrow).expandX();
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		levelLabel.setText("LEVEL " + Integer.toString(Minigame.level));
	}
}
