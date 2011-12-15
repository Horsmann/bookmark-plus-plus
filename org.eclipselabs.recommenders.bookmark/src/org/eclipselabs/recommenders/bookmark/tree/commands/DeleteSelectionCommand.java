package org.eclipselabs.recommenders.bookmark.tree.commands;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class DeleteSelectionCommand
	implements TreeCommand
{

	private BookmarkView viewer = null;

	public DeleteSelectionCommand(BookmarkView viewer)
	{
		this.viewer = viewer;
	}

	@Override
	public void execute()
	{
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

		BMNode flatRoot = viewer.getFlatModel().getModelRoot();

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
		TreeSerializerFacade.serializeToDefaultLocation(viewer.getView(),
				viewer.getModel());
	}

}
