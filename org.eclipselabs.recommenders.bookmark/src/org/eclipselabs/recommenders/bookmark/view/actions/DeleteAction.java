package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.commands.DeleteSelection;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class DeleteAction extends Action implements SelfEnabling {


	private final ViewManager manager;

	public DeleteAction(ViewManager manager) {
		this.manager = manager;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_DELETE));
		this.setToolTipText("Delete selected item");
		this.setText("Delete Selection");
		this.setEnabled(false);
	}

	@Override
	public void run() {
		new DeleteSelection(manager).execute();
	}

	@Override
	public void updateEnabledStatus() {
		BookmarkView viewer = manager.getActiveBookmarkView();
		List<IStructuredSelection> list = TreeUtil.getTreeSelections(viewer.getView());
		if (list.size() == 0) {
			this.setEnabled(false);
			return;
		}

		this.setEnabled(true);
	}
}
