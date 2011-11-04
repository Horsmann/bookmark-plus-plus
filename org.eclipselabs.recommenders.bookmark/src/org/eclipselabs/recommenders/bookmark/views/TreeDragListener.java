package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

public class TreeDragListener implements DragSourceListener {

	private TreeViewer viewer;
	private TreeNode dragNode=null;

	public TreeDragListener(TreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		System.err.println("Drag started");
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		
		dragNode = (TreeNode) selection.getFirstElement();
		
		

		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = dragNode.getName();
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		if (dragNode != null && event.doit) {
		dragNode.getParent().removeChild(dragNode);
		dragNode.removeAllChildren();
		}
		dragNode = null;
		viewer.refresh();
		System.err.println("Finshed Drag");
	}

}
