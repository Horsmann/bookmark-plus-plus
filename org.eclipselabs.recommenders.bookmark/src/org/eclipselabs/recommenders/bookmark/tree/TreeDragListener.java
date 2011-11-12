package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class TreeDragListener implements DragSourceListener {

	private final TreeViewer viewer;
	private TreeNode dragNode;
	private boolean dropPerformed;

	public TreeDragListener(TreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		if (dropPerformed)
			viewer.refresh();
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		dragNode = (TreeNode) selection.getFirstElement();

		if (dragNode.isBookmarkNode()) {
			event.doit = false;
			return;
		}

		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = dragNode.getText();
			dropPerformed = true;
		} else if (ResourceTransfer.getInstance().isSupportedType(
				event.dataType)) {
			event.data = dragNode.getText();
			dropPerformed = true;
		}

	}

	@Override
	public void dragStart(DragSourceEvent event) {
		dropPerformed = false;
	}
}
