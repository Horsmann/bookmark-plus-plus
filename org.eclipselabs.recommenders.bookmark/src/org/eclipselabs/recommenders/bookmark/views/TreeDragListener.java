package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.vogella.Todo;

public class TreeDragListener implements DragSourceListener {

	private final TreeViewer viewer;
	private TreeNode dragNode;
	private boolean deleteOldNode;

	public TreeDragListener(TreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		if (dragNode != null && deleteOldNode) {
			dragNode.removeAllChildren();
			dragNode.getParent().removeChild(dragNode);
		}

		viewer.refresh();
		System.out.println("Finshed Drag");
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		// Here you do the convertion to the type which is expected.
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		dragNode = (TreeNode) selection.getFirstElement();

		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = dragNode.getName();
			deleteOldNode = true;
		} else if (ResourceTransfer.getInstance().isSupportedType(
				event.dataType)) {
			event.data = dragNode.getName();
			deleteOldNode = true;
		}

	}

	@Override
	public void dragStart(DragSourceEvent event) {
		deleteOldNode = false;
	}
}
