package org.eclipselabs.recommenders.bookmark.view.categoryview.combo;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.view.categoryview.CategoryView;

public class ComboSaveButtonListener
	implements Listener
{

	private final Combo combo;
	private final Button button;
	private final CategoryView view;

	public ComboSaveButtonListener(CategoryView view, Button save, Combo combo)
	{
		this.view = view;
		this.button = save;
		this.combo = combo;
	}

	@Override
	public void handleEvent(Event event)
	{
		String text = combo.getText();
		BMNode head = view.getModel().getModelHead();

		head.setValue(text);
		
		view.refreshCategories();

		button.setEnabled(false);
	}

}
