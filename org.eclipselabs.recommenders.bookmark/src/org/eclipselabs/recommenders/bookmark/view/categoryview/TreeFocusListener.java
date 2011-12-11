package org.eclipselabs.recommenders.bookmark.view.categoryview;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ControlNotifier;

public class TreeFocusListener implements FocusListener {

	private ControlNotifier notifier = null;
	private BookmarkView view=null;
	
	public TreeFocusListener(BookmarkView view, ControlNotifier notifier) {
		this.view = view;
		this.notifier = notifier;
	}

	@Override
	public void focusGained(FocusEvent e) {
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		view.getView().getTree().deselectAll();
		notifier.fire();
	}

}
