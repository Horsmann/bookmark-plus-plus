package org.eclipselabs.recommenders.bookmark.copyCutPaste;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class CutHandler
	extends CopyHandler
{

	public CutHandler(ViewManager manager)
	{
		super(manager);
	}

	@Override
	public Object execute(ExecutionEvent event)
		throws ExecutionException
	{
		super.execute(event);

		removeSelections();

		if (manager.isViewFlattened()) {
			BMNode head = manager.getModel().getModelHead();
			manager.activateFlattenedModus(head);
		}
		manager.getActiveBookmarkView().getView().refresh();

		return null;
	}

	private void removeSelections()
	{
		TreeViewer viewer = manager.getActiveBookmarkView().getView();
		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(viewer);

		for (int i = 0; i < selections.size(); i++) {
			BMNode node = (BMNode) selections.get(i);
			BMNode refNode = TreeUtil.getReference(node);

			manager.getExpandedStorage().removeNode(refNode);

			if (refNode != node) {
				TreeUtil.unlink(refNode);
			}
			TreeUtil.unlink(node);
		}
	}

}
