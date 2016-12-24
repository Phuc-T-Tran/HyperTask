package ui;

import static com.phuctran.hypertask.Constants.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.phuctran.hypertask.AudioManager;
import com.phuctran.hypertask.GameData;
import com.phuctran.hypertask.HyperTask;

import actions.LabelScaleToAction;
import minigames.Minigame;
import objects.RectShape;
import objects.TriangleShape;

public class OptionsWindow extends Group {
	private Image musicIcon, soundIcon;
	private Label musicLabel, soundLabel, tutorialLabel;
	
	public OptionsWindow() {
		addBackground();
		addTitle();
		addMusicButton();
		addSoundButton();
		addTutorialSelector();
		addResetTutorials();
		addResetStats();
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
	}
	
	private void addBackground() {
		float padX = 26;
		float padY = 106;
		
		// Create the background
		RectShape background = new RectShape();
		background.setColor(COLOR_MAIN.cpy().mul(0.8f));
		background.setBounds(BOUNDS_LEFT.x + BOUNDS_LEFT.width / 2 + padX,
				BOUNDS_MIDDLE.y + padY,
				BOUNDS_RIGHT.x - padX * 2,
				BOUNDS_MIDDLE.height - padY * 2);
		
		addActor(background);
	}
	
	private void addTitle() {
		RectShape back = new RectShape();
		back.setColor(COLOR_MAIN.cpy().mul(1.2f));
		back.setBounds(BOUNDS_LEFT.x + BOUNDS_LEFT.width / 2 + 26, V_HEIGHT * 0.76f, BOUNDS_RIGHT.x - 26 * 2, 80);
		
		Label title = new Label("OPTIONS", new Label.LabelStyle(HyperTask.res.getFont(FONT_MEDIUM), COLOR_TEXT));
		title.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.81f, Align.center);
		
		addActor(back);
		addActor(title);
	}
	
	private void addMusicButton() {
		Table musicGroup = new Table();
		
		// Create the icon
		musicIcon = new Image(getMusicIcon());
		musicIcon.setColor(COLOR_TEXT);
		musicIcon.setScaling(Scaling.fit);
		musicIcon.setSize(64, 64);
		
		// Create the label
		musicLabel = new Label(GameData.getBoolean(GameData.KEY_MUSIC, true) ? "Music On" : "Music Off",
				new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
		
		// Add a click listener to the group
		musicGroup.addListener(new ClickListener() {
			@Override
			public void  clicked(InputEvent event, float x, float y) {
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				GameData.toggle(GameData.KEY_MUSIC);
				AudioManager.instance().setMusicEnabled(GameData.getBoolean(GameData.KEY_MUSIC, true));
				musicIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(getMusicIcon())));
				musicLabel.setText(GameData.getBoolean(GameData.KEY_MUSIC, true) ? "Music On" : "Music Off");
			}
		});
		
		// Add the components
		musicGroup.setSize(300, 64);
		musicGroup.setPosition(V_WIDTH * 0.33f, V_HEIGHT * 0.65f, Align.center);
		musicGroup.add(musicIcon);
		musicGroup.add(musicLabel);
		addActor(musicGroup);
	}
	
	private void addSoundButton() {
		Table soundGroup = new Table();
		
		// Create the icon
		soundIcon = new Image(getSoundIcon());
		soundIcon.setColor(COLOR_TEXT);
		soundIcon.setScaling(Scaling.fit);
		soundIcon.setSize(64, 64);
		
		// Create the label
		soundLabel = new Label(GameData.getBoolean(GameData.KEY_SOUND, true) ? "Sound On" : "Sound Off",
				new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
		
		// Add a click listener
		soundGroup.addListener(new ClickListener() {
			@Override
			public void  clicked(InputEvent event, float x, float y) {
				GameData.toggle(GameData.KEY_SOUND);
				AudioManager.instance().setSoundEnabled(GameData.getBoolean(GameData.KEY_SOUND, true));
				soundIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(getSoundIcon())));
				soundLabel.setText(GameData.getBoolean(GameData.KEY_SOUND, true) ? "Sound On" : "Sound Off");
				
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
			}
		});
		
		// Add the components
		soundGroup.setSize(300, 64);
		soundGroup.setPosition(V_WIDTH * 0.65f, V_HEIGHT * 0.65f, Align.center);
		soundGroup.add(soundIcon);
		soundGroup.add(soundLabel);
		addActor(soundGroup);
	}
	
	private void addTutorialSelector() {
		Table table = new Table();
		
		// Mode Label
		tutorialLabel = new Label(getTutorialString(), new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
		tutorialLabel.setAlignment(Align.center);
		
		// Left and right buttons
		float arrowSize = 46;
		final TriangleShape leftArrow = new TriangleShape();
		leftArrow.setSize(arrowSize, arrowSize);
		leftArrow.setOrigin(leftArrow.getWidth() / 2, leftArrow.getHeight() / 2);
		leftArrow.rotateBy(90);
		leftArrow.setColor(COLOR_TEXT);
		leftArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y){
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				
				// Update boolean
				if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_ON)) {
					GameData.put(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ALWAYS_ON);
				}
				else if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_OFF)) {
					GameData.put(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON);
				}
				else if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_ALWAYS_ON)) {
					GameData.put(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_OFF);
				}
				
				// Play animations
				leftArrow.addAction(Actions.sequence(Actions.scaleTo(1.4f, 1.4f, 0.1f), Actions.scaleTo(1f, 1f, 0.1f)));
				LabelScaleToAction scaleUp = new LabelScaleToAction(tutorialLabel, 1.2f, 0.1f);
				LabelScaleToAction scaleDown = new LabelScaleToAction(tutorialLabel, 1f, 0.1f);
				tutorialLabel.addAction(Actions.sequence(scaleUp, scaleDown));
				
				// Update label
				tutorialLabel.setText(getTutorialString());
			}
		});
		
		final TriangleShape rightArrow = new TriangleShape();
		rightArrow.setSize(arrowSize, arrowSize);
		rightArrow.setOrigin(rightArrow.getWidth() / 2, rightArrow.getHeight() / 2);
		rightArrow.rotateBy(270);
		rightArrow.setColor(COLOR_TEXT);
		rightArrow.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y){
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				
				// Update boolean
				if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_ON)) {
					GameData.put(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_OFF);
				}
				else if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_OFF)) {
					GameData.put(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ALWAYS_ON);
				}
				else if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_ALWAYS_ON)) {
					GameData.put(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON);
				}
				
				// Add animations
				rightArrow.addAction(Actions.sequence(Actions.scaleTo(1.4f, 1.4f, 0.1f), Actions.scaleTo(1f, 1f, 0.1f)));
				LabelScaleToAction scaleUp = new LabelScaleToAction(tutorialLabel, 1.2f, 0.1f);
				LabelScaleToAction scaleDown = new LabelScaleToAction(tutorialLabel, 1f, 0.1f);
				tutorialLabel.addAction(Actions.sequence(scaleUp, scaleDown));
				
				// Update label
				tutorialLabel.setText(getTutorialString());
			}
		});
		
		table.setBounds(0,0,800,50);
		table.add(leftArrow).expandX();
		table.add(tutorialLabel).expandX();
		table.add(rightArrow).expandX();
		table.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.45f, Align.center);
		addActor(table);
	}
	
	private void addResetTutorials() {
		Label resetTutorials = new Label("Reset Tutorials", new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
		resetTutorials.setPosition(V_WIDTH * 0.35f, V_HEIGHT * 0.25f, Align.center);
		resetTutorials.addListener(new ClickListener() {
			@Override
			public void  clicked(InputEvent event, float x, float y) {
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				GameData.resetTutorials();
				
				// Update tutorialLabel
				tutorialLabel.setText(getTutorialString());
			}
		});
		
		Image border = new Image(HyperTask.res.getTexture(BORDER));
		border.setPosition(V_WIDTH * 0.35f, V_HEIGHT * 0.25f, Align.center);
		
		addActor(border);
		addActor(resetTutorials);
	}
	
	private void addResetStats() {
		Label resetStats = new Label("Reset Statistics", new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
		resetStats.setPosition(V_WIDTH * 0.65f, V_HEIGHT * 0.25f, Align.center);
		resetStats.addListener(new ClickListener() {
			@Override
			public void  clicked(InputEvent event, float x, float y) {
				AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
				GameData.resetStatistics();
			}
		});
		
		Image border = new Image(HyperTask.res.getTexture(BORDER));
		border.setPosition(V_WIDTH * 0.65f, V_HEIGHT * 0.25f, Align.center);
		
		addActor(border);
		addActor(resetStats);
	}
	
	private Texture getMusicIcon() {
		return GameData.getBoolean(GameData.KEY_MUSIC, true) ? HyperTask.res.getTexture(BUTTON_MUSIC_ON) : HyperTask.res.getTexture(BUTTON_MUSIC_OFF);
	}
	
	private Texture getSoundIcon() {
		return GameData.getBoolean(GameData.KEY_SOUND, true) ? HyperTask.res.getTexture(BUTTON_SOUND_ON) : HyperTask.res.getTexture(BUTTON_SOUND_OFF);
	}
	
	private String getTutorialString() {
		String str = new String();
		if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_ON)) {
			str = "Tutorials On";
		}
		else if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_OFF)) {
			str = "Tutorials Off";
		}
		else if (GameData.getString(GameData.KEY_TUTORIALS, GameData.VAL_TUTORIALS_ON).equals(GameData.VAL_TUTORIALS_ALWAYS_ON)) {
			str = "Tutorials Always On";
		}
		
		return str;
	}
}
