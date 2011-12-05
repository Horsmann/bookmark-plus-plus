package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddToExistingBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

//@SuppressWarnings("restriction")
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
		for (int i = 0; i < selections.size(); i++) {

			TreeNode node = (TreeNode) selections.get(i);
			TreeNode nodeCopy = TreeUtil.copyTreePath(node);

			TreeNode dropTarget = (TreeNode) getTarget(event);
			TreeNode targetBookmark = TreeUtil.getBookmarkNode(dropTarget);

			if (didDropOccurInEmptyArea(targetBookmark)) {
				createNewBookmarkAndAdd(node, nodeCopy);
				continue;
			}

			if (TreeUtil.isDuplicate(targetBookmark, node))
				continue;

			TreeNode merged = null;
			if ((merged = TreeUtil.attemptMerge(targetBookmark, nodeCopy)) != null) {
				unlink(node);
				TreeUtil.showNodeExpanded(viewer, merged);
				continue;
			}

			node.getParent().removeChild(node);
			TreeNode head = TreeUtil.climbUpUntilLevelBelowBookmark(nodeCopy);
			targetBookmark.addChild(head);
			TreeUtil.showNodeExpanded(viewer, head);

		}
	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			DropTargetEvent event) throws JavaModelException {

		TreePath[] treePath = getTreePath(event);
		TreeNode target = (TreeNode) getTarget(event);
		TreeNode bookmark = TreeUtil.getBookmarkNode(target);

		if (bookmark != null) {
//			addToExistingBookmark(bookmark, treePath);
			new AddToExistingBookmarkCommand(viewer, bookmark, treePath).execute();
		} else {
			createNewBookmarkAddAsNode(treePath);
		}

	}

//	private void addToExistingBookmark(TreeNode bookmark, TreePath[] treePath)
//			throws JavaModelException {
//		for (int i = 0; i < treePath.length; i++) {
//			TreeNode added = TreeUtil.addNodesToExistingBookmark(bookmark,
//					treePath[i]);
//
//			if (added != null) {
//				TreeUtil.showNodeExpanded(viewer, added);
//			}
//		}
//	}

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

		TreeNode bookmark = makeBookmarkNode();

		model.getModelRoot().addChild(bookmark);

		for (int i = 0; i < treePath.length; i++) {
			TreeNode node = TreeUtil.addNodesToExistingBookmark(bookmark,
					treePath[i]);
			TreeUtil.showNodeExpanded(viewer, node);
		}

	}

	private TreeNode makeBookmarkNode() {
		return new TreeNode("New Bookmark", true);
	}

	private void unlink(TreeNode node) {
		node.getParent().removeChild(node);
		node.setParent(null);
	}

	private boolean didDropOccurInEmptyArea(TreeNode targetBookmark) {
		return targetBookmark == null;
	}

	private void createNewBookmarkAndAdd(TreeNode node, TreeNode nodeCopy) {
		TreeNode bookmark = makeBookmarkNode();

		while (nodeCopy.getParent() != null)
			nodeCopy = nodeCopy.getParent();

		bookmark.addChild(nodeCopy);
		model.getModelRoot().addChild(bookmark);
		TreeUtil.showNodeExpanded(viewer, bookmark);
		unlink(node);
	}

	private Object getTarget(DropTargetEvent event) {
		return ((event.item == null) ? null : event.item.getData());
	}
}