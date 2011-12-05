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
		this.setToolTipText("Refreshes the view and updates the labeling (available/closed/not available)");
		this.setText("Refresh");
	}

	@Override
	public void run() {
		viewer.refresh(true);
	}


}
