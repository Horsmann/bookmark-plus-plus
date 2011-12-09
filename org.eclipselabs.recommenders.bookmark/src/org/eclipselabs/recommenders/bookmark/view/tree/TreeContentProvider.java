package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

public class TreeContentProvider implements ITreeContentProvider {
	@Override
	public Object[] getChildren(Object element) {
		return ((TreeNode) element).getChildren();
	}

	@Override
	public Object getParent(Object element) {
		return ((TreeNode) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((TreeNode) element).hasChildren();

	}

	@Override
	public Object[] getElements(Object element) {
		return getChildren(element);
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
