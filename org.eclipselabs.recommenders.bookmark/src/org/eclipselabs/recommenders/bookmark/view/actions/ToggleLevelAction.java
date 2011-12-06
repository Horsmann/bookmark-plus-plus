package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.views.BookmarkView;

public class ToggleLevelAction extends Action implements SelfEnabling{
	
	private BookmarkView bm;
	private TreeModel model;
	
	HashMap<Object, String> expandedNodes=null;
	
	public ToggleLevelAction(BookmarkView bm, TreeModel model) {
		this.bm = bm;
		this.model = model;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_TOGGLE_LEVEL));
		this.setToolTipText("Toggles between the default view and the category the selected item is in");
		this.setText("Toggle between levels");
	}
	
	@Override
	public void run() {
		List<IStructuredSelection> selection = TreeUtil.getTreeSelections(bm.getActiveViewer());
		
		if (model.isHeadEqualRoot())
		{
			if (selection.size() <= 0)
				return;
			
			Object object = selection.get(0);
			
			TreeNode node = (TreeNode) object;
			TreeNode bookmark = TreeUtil.getBookmarkNode(node);
			model.setHeadNode(bookmark);
			
			Object [] expanded = bm.getActiveViewer().getExpandedElements();
			expandedNodes = new HashMap<Object, String>();
			AddExpandedNodesToHashMap(expanded);
			
			bm.activateToggledView();
			
			bm.getActiveViewer().setInput(null);
			bm.getActiveViewer().setInput(bookmark);
			bm.getActiveViewer().setExpandedElements(expanded);
		}
		else
		{
			Object [] expanded = bm.getActiveViewer().getExpandedElements();
			AddExpandedNodesToHashMap(expanded);
			model.resetHeadToRoot();
			
			bm.activateDefaultView();
			
			bm.getActiveViewer().setInput(null);
			bm.getActiveViewer().setInput(model.getModelHead());
			Object [] allExpanded = getExpandedNodes();
			bm.getActiveViewer().setExpandedElements(allExpanded);
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
