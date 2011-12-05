package org.eclipselabs.recommenders.bookmark.tree.commands;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class DeleteSelectionCommand implements TreeCommand {
	
	private TreeViewer viewer=null;
	
	public DeleteSelectionCommand(TreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void execute() {
		List<IStructuredSelection> selections = TreeUtil.getTreeSelections(viewer);
		for (int i=0; i < selections.size(); i++) {
			TreeNode node = (TreeNode) selections.get(i);
			TreeUtil.unlink(node);
		}
		
		viewer.refresh();
	}

}
