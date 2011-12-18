package org.eclipselabs.recommenders.bookmark.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class CopyHandler
	extends AbstractHandler
{
	
	private ViewManager manager;

	public CopyHandler(ViewManager manager)
	{
		this.manager = manager;
	}

	@Override
	public Object execute(ExecutionEvent event)
		throws ExecutionException
	{
		TreeViewer viewer = manager.getActiveBookmarkView().getView();
		List<IStructuredSelection> selections = TreeUtil.getTreeSelections(viewer);

		for (int i = 0; i < selections.size(); i++) {
			BMNode node = (BMNode)selections.get(i);
			System.err.println(node.getValue().toString());
		}
		
		
		return null;
	}

}
