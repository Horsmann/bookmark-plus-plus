package org.eclipselabs.recommenders.bookmark.tree.commands;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.tree.FlatTreeNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
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

			Object node = selections.get(i);

			if (node instanceof FlatTreeNode) {
				FlatTreeNode flatNode = (FlatTreeNode) node;
				processFlattened(flatNode);
				TreeUtil.unlink((FlatTreeNode) node);
			}
			else {

				TreeUtil.unlink((TreeNode) node);
			}
		}

		viewer.getView().refresh(true);
		viewer.updateControls();

		saveNewTreeModelState();
	}

	private void processFlattened(FlatTreeNode flatNode)
	{
		TreeNode ref = flatNode.getReferencingNode();

		FlatTreeNode flatRoot = viewer.getFlatModel().getModelRoot();

		iterateAllNodesOfFlatModelAndDeleteDescendantNodes(ref, flatRoot);

		TreeUtil.unlink(ref);
	}

	private void iterateAllNodesOfFlatModelAndDeleteDescendantNodes(
			TreeNode ref, FlatTreeNode flatRoot)
	{
		// Model root hat keine Kinder????
		for (FlatTreeNode child : flatRoot.getChildren()) {
			boolean isDescendant = TreeUtil.isDescendant(ref,
					child.getReferencingNode());
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
