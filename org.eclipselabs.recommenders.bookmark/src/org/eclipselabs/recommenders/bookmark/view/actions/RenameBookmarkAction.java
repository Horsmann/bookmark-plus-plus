package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.RenameBookmark;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class RenameBookmarkAction
	extends Action
	implements SelfEnabling
{

	private final ViewManager manager;

	public RenameBookmarkAction(ViewManager manager)
	{
		this.manager = manager;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_BOOKMARK_RENAME));
		this.setToolTipText("Renames a bookmark");
		this.setText("Rename Bookmark");
		this.setEnabled(false);
	}

	@Override
	public void run()
	{

		BookmarkView viewer = manager.getActiveBookmarkView();
		Tree tree = viewer.getView().getTree();

		TreeItem[] items = tree.getSelection();

		if (items.length > 0) {
			new RenameBookmark(manager, items[0]).execute();
		}
	}

	@Override
	public void updateEnabledStatus()
	{

		BookmarkView viewer = manager.getActiveBookmarkView();

		List<IStructuredSelection> list = TreeUtil.getTreeSelections(viewer
				.getView());
		if (isSingleBookmarkNodeSelected(list)) {
			this.setEnabled(true);
			return;
		}

		this.setEnabled(false);
	}

	private boolean isSingleBookmarkNodeSelected(List<IStructuredSelection> list)
	{
		return list.size() == 1 && list.get(0) instanceof TreeNode
				&& ((TreeNode) list.get(0)).isBookmarkNode();
	}
}
