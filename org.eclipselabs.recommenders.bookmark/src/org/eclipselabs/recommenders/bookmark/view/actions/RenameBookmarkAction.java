package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.RenameBookmark;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class RenameBookmarkAction extends Action implements SelfEnabling {

	private BookmarkView viewer = null;

	public RenameBookmarkAction(BookmarkView viewer) {
		this.viewer = viewer;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_BOOKMARK_RENAME));
		this.setToolTipText("Renames a bookmark");
		this.setText("Rename Bookmark");
		this.setEnabled(false);
	}

	@Override
	public void run() {

		TreeItem[] items = viewer.getView().getTree().getSelection();

		if (items.length > 0) {
			new RenameBookmark(viewer, items[0]).execute();
		}
	}

	@Override
	public void updateEnabledStatus() {
		List<IStructuredSelection> list = TreeUtil.getTreeSelections(viewer
				.getView());
		if (isSingleBookmarkNodeSelected(list)) {
			this.setEnabled(true);
			return;
		}

		this.setEnabled(false);
	}

	private boolean isSingleBookmarkNodeSelected(List<IStructuredSelection> list) {
		return list.size() == 1 && list.get(0) instanceof TreeNode
				&& ((TreeNode) list.get(0)).isBookmarkNode();
	}
}
