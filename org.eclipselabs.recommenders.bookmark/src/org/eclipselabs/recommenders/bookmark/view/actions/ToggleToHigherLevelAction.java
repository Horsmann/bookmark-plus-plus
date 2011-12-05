package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

public class ToggleToHigherLevelAction extends Action {

	private TreeViewer viewer;
	private TreeModel model;

	public ToggleToHigherLevelAction(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_TOGGLE_TO_HIGHER_LEVEL));
		this.setToolTipText("Toggles the current view to see only the selection childs");
		this.setText("Toggle to lower level");
	}

	@Override
	public void run() {

		if (model.isHeadEqualRoot())
			return;

		TreeNode currentHead = model.getModelHead();
		TreeNode higherLevel = currentHead.getParent();
		model.setHeadNode(higherLevel);
//		Object [] expanded = viewer.getExpandedElements();
		viewer.setInput(null);
		viewer.setInput(model.getModelHead());
		viewer.refresh();
//		viewer.setExpandedElements(expanded);

	}

}
