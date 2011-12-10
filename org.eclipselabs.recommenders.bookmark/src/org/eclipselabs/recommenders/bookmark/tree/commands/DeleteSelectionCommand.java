package org.eclipselabs.recommenders.bookmark.tree.commands;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class DeleteSelectionCommand implements TreeCommand {
	
	private BookmarkView viewer=null;
	
	public DeleteSelectionCommand(BookmarkView viewer) {
		this.viewer = viewer;
	}

	@Override
	public void execute() {
		List<IStructuredSelection> selections = TreeUtil.getTreeSelections(viewer.getView());
		for (int i=0; i < selections.size(); i++) {
			TreeNode node = (TreeNode) selections.get(i);
			TreeUtil.unlink(node);
		}
		
		viewer.getView().refresh();
		viewer.updateControls();
	}

}
