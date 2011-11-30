package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;

public class RefreshViewAction extends Action {

	private TreeViewer viewer;

	public RefreshViewAction(TreeViewer viewer) {
		this.viewer = viewer;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_REFRESH_VIEW));
		this.setToolTipText("<html>Refreshes the view and updates the labeling<br>(available/closed/not available)</html>");
		this.setText("Refresh");
	}

	@Override
	public void run() {
		viewer.refresh(true);
	}

}
