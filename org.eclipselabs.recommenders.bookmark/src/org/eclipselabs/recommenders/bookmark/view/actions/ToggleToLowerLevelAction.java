package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class ToggleToLowerLevelAction extends Action{
	
	private TreeViewer viewer;
	private TreeModel model;
	
	public ToggleToLowerLevelAction(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_TOGGLE_TO_LOWER_LEVEL));
		this.setToolTipText("Toggles the current view to see only the selection childs");
		this.setText("Toggle to lower level");
	}
	
	@Override
	public void run() {
		List<IStructuredSelection> selection = TreeUtil.getTreeSelections(viewer);
		
		if (selection.size() <= 0)
			return;
		
		Object object = selection.get(0);
		if (object instanceof TreeNode) {
			TreeNode node = (TreeNode) object;
			model.setHeadNode(node);
//			Object [] expanded = viewer.getExpandedElements();
			viewer.setInput(null);
			viewer.setInput(node);
			viewer.refresh();
//			viewer.setExpandedElements(expanded);
		}
		
	}

}
