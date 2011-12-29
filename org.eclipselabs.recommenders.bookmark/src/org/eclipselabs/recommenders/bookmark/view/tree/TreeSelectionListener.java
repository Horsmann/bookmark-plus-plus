package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipselabs.recommenders.bookmark.view.ControlNotifier;

public class TreeSelectionListener
	implements SelectionListener
{

	private ControlNotifier notifier = null;

	public TreeSelectionListener(ControlNotifier notifier)
	{
		this.notifier = notifier;
	}

	@Override
	public void widgetSelected(SelectionEvent e)
	{
		notifier.fire();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{

	}

}
