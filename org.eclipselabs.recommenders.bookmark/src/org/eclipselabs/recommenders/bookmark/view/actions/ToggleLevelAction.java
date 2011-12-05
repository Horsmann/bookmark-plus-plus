package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class ToggleLevelAction extends Action implements SelfEnabling{
	
	private TreeViewer viewer;
	private TreeModel model;
	
	HashMap<Object, String> expandedNodes=null;
	
	public ToggleLevelAction(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_TOGGLE_LEVEL));
		this.setToolTipText("Toggles between the default view and the category the selected item is in");
		this.setText("Toggle between levels");
	}
	
	@Override
	public void run() {
		List<IStructuredSelection> selection = TreeUtil.getTreeSelections(viewer);
		
		if (model.isHeadEqualRoot())
		{
			if (selection.size() <= 0)
				return;
			
			Object object = selection.get(0);
			
			TreeNode node = (TreeNode) object;
			TreeNode bookmark = TreeUtil.getBookmarkNode(node);
			model.setHeadNode(bookmark);
			
			Object [] expanded = viewer.getExpandedElements();
			expandedNodes = new HashMap<Object, String>();
			AddExpandedNodesToHashMap(expanded);
			
			viewer.setInput(null);
			viewer.setInput(bookmark);
			viewer.setExpandedElements(expanded);
		}
		else
		{
			Object [] expanded = viewer.getExpandedElements();
			AddExpandedNodesToHashMap(expanded);
			model.resetHeadToRoot();
			viewer.setInput(null);
			viewer.setInput(model.getModelHead());
			Object [] allExpanded = getExpandedNodes();
			viewer.setExpandedElements(allExpanded);
		}
		
	}

	private void AddExpandedNodesToHashMap(Object[] expanded) {
		for (Object o : expanded)
			if (expandedNodes.get(o)==null)
				expandedNodes.put(o, "");		
	}
	
	private Object [] getExpandedNodes() {
		return expandedNodes.keySet().toArray();
	}

	@Override
	public void updateEnabledStatus() {
		// TODO Auto-generated method stub
		
	}

}
