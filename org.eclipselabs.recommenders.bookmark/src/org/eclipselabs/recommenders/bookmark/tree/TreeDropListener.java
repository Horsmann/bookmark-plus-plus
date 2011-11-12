package org.eclipselabs.recommenders.bookmark.tree;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class TreeDropListener implements DropTargetListener {

	private final TreeViewer viewer;
	private TreeModel model;
	private TreeNode targetNode = null;

	public TreeDropListener(TreeViewer viewer, TreeModel model) {
		// super(viewer);
		this.viewer = viewer;
		this.model = model;
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

	@Override
	public void dropAccept(DropTargetEvent event) {

		if (!isValidDrop(event))
			event.detail = DND.DROP_NONE;
	}

	private boolean isValidDrop(DropTargetEvent event) {

		// Iterate the selected items that are being droped
		List<IStructuredSelection> selectedList = getTreeSelections();
		for (int i = 0; i < selectedList.size(); i++) {
			TreeNode node = (TreeNode) selectedList.get(i);

			// TODO: Kein drop ausführen, wenn knoten im selben baum landen
			// würde
			TreeNode target = (TreeNode) getTarget(event);
			if (causesRecursion(node, target)) {
				return false;
			}

			if (node == target)
				return false;

		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private List<IStructuredSelection> getTreeSelections() {
		ISelection selection = viewer.getSelection();
		if (selection == null)
			return Collections.emptyList();
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
			return ((IStructuredSelection) selection).toList();

		return Collections.emptyList();
	}

	private boolean causesRecursion(TreeNode source, TreeNode target) {

		if (target == null)
			return false;

		TreeNode targetParent = target.getParent();

		if (targetParent == null)
			return false;
		else if (targetParent == source)
			return true;
		else
			return causesRecursion(source, targetParent);

	}

	@Override
	public void drop(DropTargetEvent event) {

		if (getTarget(event) != null && getTarget(event) instanceof TreeNode) {
			targetNode = (TreeNode) getTarget(event);

			// Get the bookmark node, any drop must be added to a bookmark
			while (!targetNode.isBookmarkNode())
				targetNode = targetNode.getParent();
		}

		/*
		 * Wenn alles LocalSelection läuft, krieg ich überall her nur ein
		 * LocalSelectionObjekt, und meine IResource Verarbeitung schlägt fehl.
		 */

		TreeSelection sel = null;
		if (event.data instanceof TreeSelection)
			sel = (TreeSelection) event.data;

		TreePath[] p = sel.getPaths();

		for (int i = 0; i < p.length; i++) {
			System.err.println("Iteration: " + i);
			// System.err.println("First: " + p[i].getFirstSegment());
			// System.err.println("Last: " + p[i].getLastSegment());
			for (int j = 0; j < p[i].getSegmentCount(); j++) {
				System.err.println("Index  " + j + ": " + p[i].getSegment(j));

				if (p[i].getSegment(j) instanceof IMember) {
					IMember o = (IMember) p[i].getSegment(j);
					System.err.println("Ele name: " + o.getElementName());
					System.err.println("o.getJavaProject()"
							+ o.getJavaProject().getElementName());
				}

				if (p[i].getSegment(j) instanceof IJavaElement) {
					IJavaElement o = (IJavaElement) p[i].getSegment(j);
					System.err.println("Ele name: " + o.getElementName());
					System.err.println("o.getJavaProject()"
							+ o.getJavaProject().getElementName());
				}

				if (p[i].getSegment(j) instanceof IFile) {
					IFile o = (IFile) p[i].getSegment(j);
					System.err.println("Ele name: " + o.getName());
				}

				if (p[i].getSegment(j) instanceof IType) {
					IType o = (IType) p[i].getSegment(j);
					System.err.println("Ele name: " + o.getElementName());
					System.err.println("o.getJavaProject()"
							+ o.getJavaProject().getElementName());
				}
				if (p[i].getSegment(j) instanceof IField) {
					IField o = (IField) p[i].getSegment(j);
					System.err.println("Ele name: " + o.getElementName());
					System.err.println("o.getJavaProject()"
							+ o.getJavaProject().getElementName());
				}
				if (p[i].getSegment(j) instanceof IMethod) {
					IMethod o = (IMethod) p[i].getSegment(j);
					System.err.println("Ele name: " + o.getElementName());
					System.err.println("o.getJavaProject()"
							+ o.getJavaProject().getElementName());
				}

			}
			System.err.println("\n");
		}

		for (int i = 0; i < p.length; i++) {

			if (p[i].getSegment(0) instanceof IMember) {
				IMember o = (IMember) p[i].getSegment(0);
				System.err.println("Ele name: " + o.getElementName());
				System.err.println("o.getJavaProject()"
						+ o.getJavaProject().getElementName());
			}

			if (p[i].getSegment(0) instanceof IJavaElement) {
				IJavaElement o = (IJavaElement) p[i].getSegment(0);
				System.err.println("Ele name: " + o.getElementName());
				System.err.println("o.getJavaProject()"
						+ o.getJavaProject().getElementName());
			}

			if (p[i].getSegment(0) instanceof IFile) {
				IFile o = (IFile) p[i].getSegment(0);
				System.err.println("Ele name: " + o.getName());
			}

			if (p[i].getSegment(0) instanceof IType) {
				IType o = (IType) p[i].getSegment(0);
				System.err.println("Ele name: " + o.getElementName());
				System.err.println("o.getJavaProject()"
						+ o.getJavaProject().getElementName());
			}
			if (p[i].getSegment(0) instanceof IField) {
				IField o = (IField) p[i].getSegment(0);
				System.err.println("Ele name: " + o.getElementName());
				System.err.println("o.getJavaProject()"
						+ o.getJavaProject().getElementName());
			}
			if (p[i].getSegment(0) instanceof IMethod) {
				IMethod o = (IMethod) p[i].getSegment(0);
				System.err.println("Ele name: " + o.getElementName());
				System.err.println("o.getJavaProject()"
						+ o.getJavaProject().getElementName());
			}

			if (p[i].getSegmentCount() > 1) {

				if (p[i].getSegment(1) instanceof IType) {
					IType o = (IType) p[i].getSegment(1);
					System.err.println("Ele name: " + o.getElementName());
					System.err.println("o.getJavaProject()"
							+ o.getJavaProject().getElementName());
				}

				if (p[i].getSegment(1) instanceof IMethod) {
					IMethod o = (IMethod) p[i].getSegment(1);
					System.err.println("Ele name: " + o.getElementName());
					System.err.println("o.getJavaProject()"
							+ o.getJavaProject().getElementName());
				}

				if (p[i].getSegment(1) instanceof IFile) {
					IFile o = (IFile) p[i].getSegment(1);
					System.err.println("Ele name: " + o.getName());
					System.err.println("Project: " + o.getProject().getName());
				}

				if (p[i].getSegment(1) instanceof IJavaElement) {
					IJavaElement o = (IJavaElement) p[i].getSegment(1);
					System.err.println("Ele name: " + o.getElementName());
					System.err.println("Project: "
							+ o.getJavaProject().getElementName());
				}

				if (p[i].getSegment(1) instanceof IMember) {
					IMember o = (IMember) p[i].getSegment(1);
					System.err.println("Ele name: " + o.getElementName());
					System.err.println("o.getJavaProject()"
							+ o.getJavaProject().getElementName());

				}
				if (p[i].getSegment(1) instanceof ICompilationUnit) {
					ICompilationUnit o = (ICompilationUnit) p[i].getSegment(1);
					System.err.println("Ele name: " + o.getElementName());
					System.err.println("o.getJavaProject()"
							+ o.getJavaProject().getElementName());

				}
			}

		}

		if (isInsideViewDrop(event))
			processDropEventWithDragFromWithinTheView();
		else {
			int xy = p[0].getSegmentCount();
			processDropEventWithDragInitiatedFromOutsideTheView(p[0]
					.getSegment(xy - 1));
		}

		targetNode = null;

	}

	private boolean isInsideViewDrop(DropTargetEvent event) {
		return !getTreeSelections().isEmpty()
				&& !(event.data instanceof IResource[]);
	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			Object object) {

		// if (object instanceof IType) {
		// IResource[] resources = (IResource[]) object.data;
		// IType t = (IType) object;

		if (targetNode != null) {
			createNewNodeAndAddAsChildToTargetNode(object);
		} else {
			createNewTopLevelNode(object);
		}
		// }

	}

	private void createNewTopLevelNode(Object t) {
		TreePath[] treeExpansion = viewer.getExpandedTreePaths();

		TreeNode bookmarkNode = new TreeNode("New Bookmark", true);

		TreeNode refNode = new TreeNode(t);
		bookmarkNode.addChild(refNode);
		refNode.setParent(bookmarkNode);
		model.getModelRoot().addChild(bookmarkNode);
		viewer.refresh();
		viewer.setExpandedTreePaths(treeExpansion);
	}

	private void processDropEventWithDragFromWithinTheView() {
		List<IStructuredSelection> selections = getTreeSelections();
		for (int i = 0; i < selections.size(); i++) {
			TreeNode node = (TreeNode) selections.get(i);

			if (isValidSourceAndTarget(node)) {
				TreeNode sourceNode = (TreeNode) node;

				sourceNode.getParent().removeChild(sourceNode);

				sourceNode.setParent(targetNode);
				targetNode.addChild(sourceNode);
			}
		}
	}

	private boolean isValidSourceAndTarget(TreeNode node) {
		return (node instanceof TreeNode && targetNode != null);
	}

	private void createNewNodeAndAddAsChildToTargetNode(Object t) {
		TreeNode newNode = new TreeNode(t);
		newNode.setParent(targetNode);
		targetNode.addChild(newNode);

		TreePath[] treeExpansion = viewer.getExpandedTreePaths();

		viewer.refresh();

		viewer.setExpandedTreePaths(treeExpansion);

	}

	private Object getTarget(DropTargetEvent event) {
		return ((event.item == null) ? null : event.item.getData());
	}

}