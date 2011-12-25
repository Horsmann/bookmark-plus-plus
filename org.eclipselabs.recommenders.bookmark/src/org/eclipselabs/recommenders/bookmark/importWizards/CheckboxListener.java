package org.eclipselabs.recommenders.bookmark.importWizards;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class CheckboxListener
	implements Listener
{

	private final Text nameOfNewCategory;

	public CheckboxListener(Text nameOfNewCategory)
	{
		this.nameOfNewCategory = nameOfNewCategory;
	}

	@Override
	public void handleEvent(Event event)
	{
		if (nameOfNewCategory.isEnabled()) {
			nameOfNewCategory.setEnabled(false);
		}
		else {
			nameOfNewCategory.setEnabled(true);
		}

	}

}
