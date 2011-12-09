package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreeNodesToExistingBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreeNodesToNewBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.tree.commands.CreateNewBookmarkAddAsNodeCommand;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class TreeDropListener implements DropTargetListener {

	private final TreeViewer viewer;
	private TreeModel model;

	/**
	 * The drag listener of the <b>same</b> view the drop listener is listening
	 * for. By having access to the corresponding drag listener a drag action
	 * triggered within the view can be distinguished from a drag action started
	 * in a foreign view
	 */
	private TreeDragListener dragListener = null;

	public TreeDropListener(TreeViewer viewer, TreeModel model,
			TreeDragListener localViewsDragListener) {
		this.viewer = viewer;
		this.model = model;
		this.dragListener = localViewsDragListener;
	}

	@Override
	public void drop(DropTargetEvent event) {

		try {
			if (dragListener.isDragInProgress())
				processDropEventWithDragFromWithinTheView(event);
			else
				processDropEventWithDragInitiatedFromOutsideTheView(event);

			TreeSerializerFacade.serializeToDefaultLocation(viewer, model);

			saveNewTreeModelState();

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		viewer.refresh();
	}

	@Override
	public void dropAccept(DropTargetEvent event) {

		if (!isValidDrop(event))
			event.detail = DND.DROP_NONE;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		event.detail = DND.DROP_LINK;
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
	}

	@Override
	public void dragOver(DropTargetEvent event) {
	}

	private void saveNewTreeModelState() {
		TreeSerializerFacade.serializeToDefaultLocation(viewer, model);
	}

	private void processDropEventWithDragFromWithinTheView(DropTargetEvent event)
			throws JavaModelException {
		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(viewer);

		// Randbedingung wo der Drop beim internen droppen nicht korrekt die
		// Struktur nach oben aufbaut

		if (didDropOccurInEmptyArea(event)) {

			if (selections.size() > 0) {

				// TreeNode node = (TreeNode)selections.get(0);

				// TreeNode nodeCopy = TreeUtil.copyTreePath(node);

				// TreeNode dropTarget = (TreeNode) getTarget(event);
				// TreeNode bookmarkOfDropTarget = TreeUtil
				// .getBookmarkNode(dropTarget);

				// if (didDropOccurInEmptyArea(bookmarkOfDropTarget)) {

				new AddTreeNodesToNewBookmark(viewer, model).execute();
				// createNewBookmarkAndAdd(node, nodeCopy);
				// }

			}
			return;
		}

		for (int i = 0; i < selections.size(); i++) {

			TreeNode node = (TreeNode) selections.get(i);
			

			TreeNode dropTarget = (TreeNode) getTarget(event);
			TreeNode bookmarkOfDropTarget = TreeUtil
					.getBookmarkNode(dropTarget);

			new AddTreeNodesToExistingBookmark(viewer, bookmarkOfDropTarget, node).execute();

			// if (didDropOccurInEmptyArea(bookmarkOfDropTarget)) {
			// createNewBookmarkAndAdd(node, nodeCopy);
			// continue;
			// }

//			 if (TreeUtil.isDuplicate(bookmarkOfDropTarget, node))
//			 continue;
//			
//			 TreeNode merged = null;
//			 if ((merged = TreeUtil.attemptMerge(bookmarkOfDropTarget,
//			 nodeCopy)) != null) {
//			 TreeUtil.unlink(node);
//			 TreeUtil.showNodeExpanded(viewer, merged);
//			 continue;
//			 }
//			
//			 node.getParent().removeChild(node);
//			 TreeNode head =
//			 TreeUtil.climbUpUntilLevelBelowBookmark(nodeCopy);
//			 bookmarkOfDropTarget.addChild(head);
//			 TreeUtil.showNodeExpanded(viewer, head);

		}
		
//		for (int i = 0; i < selections.size(); i++) {
//
//			TreeNode node = (TreeNode) selections.get(i);
//			TreeUtil.unlink(node);
//		}
	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			DropTargetEvent event) throws JavaModelException {

		TreePath[] treePath = getTreePath(event);
		TreeNode target = (TreeNode) getTarget(event);
		TreeNode bookmark = TreeUtil.getBookmarkNode(target);

		if (bookmark != null) {
			new AddTreepathsToExistingBookmarkCommand(viewer, bookmark,
					treePath).execute();
		} else {
			createNewBookmarkAddAsNode(treePath);
		}

	}

	private boolean isValidDrop(DropTargetEvent event) {

		if (dragListener.isDragInProgress()) {
			List<IStructuredSelection> selectedList = TreeUtil
					.getTreeSelections(viewer);
			TreeNode target = (TreeNode) getTarget(event);

			for (int i = 0; i < selectedList.size(); i++) {
				TreeNode node = (TreeNode) selectedList.get(i);

				if (TreeUtil.causesRecursion(node, target))
					return false;

				if (node == target)
					return false;
			}
		}
		return true;
	}

	private TreePath[] getTreePath(DropTargetEvent event) {
		TreeSelection treeSelection = null;
		TreePath[] treePath = null;
		if (event.data instanceof TreeSelection) {
			treeSelection = (TreeSelection) event.data;
			treePath = treeSelection.getPaths();
		}
		return treePath;
	}

	private void createNewBookmarkAddAsNode(TreePath[] treePath)
			throws JavaModelException {

		new CreateNewBookmarkAddAsNodeCommand(model, viewer, treePath)
				.execute();
	}

	private boolean didDropOccurInEmptyArea(DropTargetEvent event) {
		TreeNode dropTarget = (TreeNode) getTarget(event);
		TreeNode bookmarkOfDropTarget = TreeUtil.getBookmarkNode(dropTarget);

		return bookmarkOfDropTarget == null;
	}

	// private void createNewBookmarkAndAdd(TreeNode node, TreeNode nodeCopy) {
	// TreeNode bookmark = TreeUtil.makeBookmarkNode();
	//
	// while (nodeCopy.getParent() != null)
	// nodeCopy = nodeCopy.getParent();
	//
	// bookmark.addChild(nodeCopy);
	// model.getModelRoot().addChild(bookmark);
	// TreeUtil.showNodeExpanded(viewer, bookmark);
	// TreeUtil.unlink(node);
	// }

	private Object getTarget(DropTargetEvent event) {
		return ((event.item == null) ? null : event.item.getData());
	}
}