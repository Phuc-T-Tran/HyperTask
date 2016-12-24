package ui;

import static com.phuctran.hypertask.Constants.BOUNDS_LEFT;
import static com.phuctran.hypertask.Constants.BOUNDS_MIDDLE;
import static com.phuctran.hypertask.Constants.BOUNDS_RIGHT;
import static com.phuctran.hypertask.Constants.COLOR_MAIN;
import static com.phuctran.hypertask.Constants.COLOR_TEXT;
import static com.phuctran.hypertask.Constants.FONT_MEDIUM;
import static com.phuctran.hypertask.Constants.FONT_SMALL;
import static com.phuctran.hypertask.Constants.V_HEIGHT;
import static com.phuctran.hypertask.Constants.V_WIDTH;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.HyperTask;

import objects.RectShape;

public class CreditsWindow extends Group {
	public CreditsWindow() {
		addBackground();
		addTitle();
		addPanel();
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
		
		Label title = new Label("CREDITS", new Label.LabelStyle(HyperTask.res.getFont(FONT_MEDIUM), COLOR_TEXT));
		title.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.81f, Align.center);
		
		addActor(back);
		addActor(title);
	}
	
	private void addPanel() {
		CreditsScrollPanel panel = new CreditsScrollPanel();
		addActor(panel);
	}
	
	private class CreditsScrollPanel extends Table {
		public CreditsScrollPanel() {
			Table table = new Table();
			float padding = 30;
			table.row().expandX().pad(padding);
			Label gameBy = new Label("-HyperTask-\nDeveloped by Phuc Tran\nThanks LibGDX!", new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
			gameBy.setAlignment(Align.center, Align.center);
			table.add(gameBy);
			
			table.row().expandX().pad(padding);
			Label gameMusic = new Label("-Game Music-\nHyperbola by Tejaswi\nLicensed under CC 3.0 BY-SA", new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
			gameMusic.setAlignment(Align.center, Align.center);
			table.add(gameMusic);
			
			table.row().expandX().pad(padding);
			Label tutIcons = new Label("-Tutorial Icons-\nIcons from www.simpleicons.com", new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
			tutIcons.setAlignment(Align.center, Align.center);
			table.add(tutIcons);
			
			final ScrollPane scroll = new ScrollPane(table);
			scroll.setOverscroll(false, true);
			scroll.setScrollingDisabled(true, false);
			
			setBounds(BOUNDS_LEFT.x + BOUNDS_LEFT.width / 2 + 26, BOUNDS_MIDDLE.y + 116, BOUNDS_RIGHT.x - 52, V_HEIGHT * 0.58f);
			add(scroll).expand().fill().align(Align.left);
		}
	}
}
