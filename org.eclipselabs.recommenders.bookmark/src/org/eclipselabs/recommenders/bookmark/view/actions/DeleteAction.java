package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.commands.DeleteSelectionCommand;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class DeleteAction extends Action implements SelfEnabling {

	private BookmarkView viewer;

	public DeleteAction(BookmarkView viewer) {
		this.viewer = viewer;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_DELETE));
		this.setToolTipText("Delete selected item");
		this.setText("Delete Selection");
		this.setEnabled(false);
	}

	@Override
	public void run() {
		new DeleteSelectionCommand(viewer).execute();
	}

	@Override
	public void updateEnabledStatus() {
		
		List<IStructuredSelection> list = TreeUtil.getTreeSelections(viewer.getView());
		if (list.size() == 0) {
			this.setEnabled(false);
			return;
		}

		this.setEnabled(true);
	}
}
