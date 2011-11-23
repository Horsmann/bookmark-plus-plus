package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;

public class TreeDoubleclickListener implements IDoubleClickListener {

	private Action action = null;

	public TreeDoubleclickListener(final Action action) {
		this.action = action;
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {

		action.run();
	}
}
