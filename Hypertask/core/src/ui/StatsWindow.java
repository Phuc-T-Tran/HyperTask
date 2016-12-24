package ui;

import static com.phuctran.hypertask.Constants.BOUNDS_LEFT;
import static com.phuctran.hypertask.Constants.BOUNDS_MIDDLE;
import static com.phuctran.hypertask.Constants.BOUNDS_RIGHT;
import static com.phuctran.hypertask.Constants.COLOR_MAIN;
import static com.phuctran.hypertask.Constants.COLOR_TEXT;
import static com.phuctran.hypertask.Constants.FONT_MEDIUM;
import static com.phuctran.hypertask.Constants.FONT_SMALL;
import static com.phuctran.hypertask.Constants.GameColors;
import static com.phuctran.hypertask.Constants.GameNames;
import static com.phuctran.hypertask.Constants.NUM_GAMETYPES;
import static com.phuctran.hypertask.Constants.V_HEIGHT;
import static com.phuctran.hypertask.Constants.V_WIDTH;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.GameData;
import com.phuctran.hypertask.HyperTask;

import objects.RectShape;

public class StatsWindow extends Group {
	public StatsWindow() {
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
		
		Label title = new Label("STATISTICS", new Label.LabelStyle(HyperTask.res.getFont(FONT_MEDIUM), COLOR_TEXT));
		title.setPosition(V_WIDTH * 0.5f, V_HEIGHT * 0.81f, Align.center);
		
		addActor(back);
		addActor(title);
	}
	
	private void addPanel() {
		StatsScrollPanel panel = new StatsScrollPanel();
		addActor(panel);
		
		RectShape line = new RectShape();
		line.setColor(Color.WHITE.cpy().mul(1,1,1,0.4f));
		line.setBounds(V_WIDTH * 0.58f, V_HEIGHT * 0.18f, 2, V_HEIGHT * 0.52f);
		addActor(line);
	}
	
	private class StatsScrollPanel extends Table {
		private Label bestNum, playedNum;
		private ArrayList<Label> statLabels;
		
		public StatsScrollPanel() {
			statLabels = new ArrayList<Label>();
			
			Table table = new Table();
			
			table.row().expandX().pad(20);
			Label best = new Label("Best", new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
			bestNum = new Label(Integer.toString(GameData.getInt(GameData.KEY_BEST, 0)), new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
			table.add(best);
			table.add(bestNum);
			
			table.row().expandX().pad(20);
			Label played = new Label("Played", new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
			playedNum = new Label(Integer.toString(GameData.getInt(GameData.KEY_GAMES_PLAYED, 0)), new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
			table.add(played);
			table.add(playedNum);
			
			for (int i = 0; i < NUM_GAMETYPES; i++) {
				table.row().expandX().pad(20);

				Table iconTable = new Table();
				
				final GameType type = GameType.values()[i];
				final String name = GameNames[type.ordinal()];
				Image img = new Image(HyperTask.res.getTexture(name));
				img.setSize(78, 78);
				img.setColor(GameColors[type.ordinal()]);
				
				Label nameLabel = new Label(name, new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
				
				iconTable.row().expandX().pad(20);
				iconTable.add(img);
				iconTable.add(nameLabel);
				table.add(iconTable);
				
				// Add wins / losses
				Label stats = new Label(Integer.toString(GameData.getInt(type.toString() + GameData.KEY_WINS, 0)) + " / " +
				                        Integer.toString(GameData.getInt(type.toString() + GameData.KEY_LOSSES, 0)),
						new Label.LabelStyle(HyperTask.res.getFont(FONT_SMALL), COLOR_TEXT));
				statLabels.add(stats);
				table.add(stats);
			}
			
			final ScrollPane scroll = new ScrollPane(table);
			scroll.setOverscroll(false, true);
			scroll.setScrollingDisabled(true, false);
			
			setBounds(BOUNDS_LEFT.x + BOUNDS_LEFT.width / 2 + 26, BOUNDS_MIDDLE.y + 106, BOUNDS_RIGHT.x - 52, V_HEIGHT * 0.58f);
			add(scroll).expand().fill().align(Align.left);
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			bestNum.setText(Integer.toString(GameData.getInt(GameData.KEY_BEST, 0)));
			playedNum.setText(Integer.toString(GameData.getInt(GameData.KEY_GAMES_PLAYED, 0)));
			for (int i = 0; i < NUM_GAMETYPES; i++) {
				final GameType type = GameType.values()[i];
				statLabels.get(i).setText(Integer.toString(GameData.getInt(type.toString() + GameData.KEY_WINS, 0)) + " / " +
                                          Integer.toString(GameData.getInt(type.toString() + GameData.KEY_LOSSES, 0)));
			}
		}
	}
}
