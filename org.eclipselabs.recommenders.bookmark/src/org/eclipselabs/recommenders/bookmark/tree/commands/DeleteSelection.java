package org.eclipselabs.recommenders.bookmark.tree.commands;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class DeleteSelection
	implements TreeCommand
{

	private ViewManager manager;

	public DeleteSelection(ViewManager manager)
	{
		this.manager = manager;
	}

	@Override
	public void execute()
	{
		BookmarkView viewer = manager.getActiveBookmarkView();
		
		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(viewer.getView());
		for (int i = 0; i < selections.size(); i++) {

			BMNode node = (BMNode) selections.get(i);

			if (node.hasReference()) {
				processFlattened(node);
			}

			TreeUtil.unlink(node);
		}

		viewer.getView().refresh(true);
		viewer.updateControls();

		saveNewTreeModelState();
	}

	private void processFlattened(BMNode flatNode)
	{
		BMNode ref = flatNode.getReference();

		BMNode flatRoot = manager.getFlatModel().getModelRoot();

		iterateAllNodesOfFlatModelAndDeleteDescendantNodes(ref, flatRoot);

		TreeUtil.unlink(ref);
	}

	private void iterateAllNodesOfFlatModelAndDeleteDescendantNodes(BMNode ref,
			BMNode flatRoot)
	{
		for (BMNode child : flatRoot.getChildren()) {
			boolean isDescendant = TreeUtil.isDescendant(ref,
					child.getReference());
			if (isDescendant) {
				TreeUtil.unlink(child);
			}
		}
	}

	private void saveNewTreeModelState()
	{
		BookmarkView viewer = manager.getActiveBookmarkView();
		TreeModel model = manager.getModel();
		
		TreeSerializerFacade.serializeToDefaultLocation(viewer.getView(),
				model);
	}

}
