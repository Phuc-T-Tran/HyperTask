/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package ui;

import static com.phuctran.hypertask.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.phuctran.hypertask.Constants.GameType;
import com.phuctran.hypertask.AudioManager;
import com.phuctran.hypertask.HyperTask;

import screens.PracticeScreen;

public class PracticeScrollPanel extends Table {
	public static final int NUM_COLUMNS = 2;
	
	private final PracticeScreen screen;
	
	public PracticeScrollPanel(final PracticeScreen screen, Rectangle bounds) {
		this.screen = screen;
		
		Table table = new Table();
		int i = 0;
		while (i < NUM_GAMETYPES) {
			table.row().expandX().pad(40);
			
			for (int j = 0; j < NUM_COLUMNS; j++) {
				final GameType type = GameType.values()[i % GameType.values().length];
				final String name = GameNames[type.ordinal()];
				final Image img = new Image(HyperTask.res.getTexture(name));
				img.setSize(78, 78);
				img.setColor(GameColors[type.ordinal()]);
				img.setOrigin(Align.center);
				//img.setScale(1.2f);
				img.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						AudioManager.instance().play(HyperTask.res.getSound(SOUND_CLICK), CLICK_VOLUME);
						img.addAction(Actions.sequence(Actions.scaleTo(1.8f, 1.8f, 0.1f), Actions.scaleTo(1, 1, 0.1f)));
						screen.setGame(type);
					}
				});
				table.add(img);
				i++;
				if (i >= NUM_GAMETYPES)
					break;
			}
		}
		
		final ScrollPane scroll = new ScrollPane(table);
		scroll.setOverscroll(false, true);
		scroll.setScrollingDisabled(true, false);
		
		setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		add(scroll).expand().fill();
	}
}