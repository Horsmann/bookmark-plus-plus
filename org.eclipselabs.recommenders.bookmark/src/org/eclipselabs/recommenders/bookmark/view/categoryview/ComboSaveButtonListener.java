package org.eclipselabs.recommenders.bookmark.view.categoryview;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;

public class ComboSaveButtonListener implements Listener
{

	private final Combo combo;
	private final TreeModel model;
	private final Button button;

	public ComboSaveButtonListener(Button save, Combo combo, TreeModel model)
	{
		this.button = save;
		this.combo = combo;
		this.model = model;
	}

	@Override
	public void handleEvent(Event event)
	{
		String text = combo.getText();
		BMNode head = model.getModelHead();
		
		head.setValue(text);
		
		button.setEnabled(false);
	}

}
