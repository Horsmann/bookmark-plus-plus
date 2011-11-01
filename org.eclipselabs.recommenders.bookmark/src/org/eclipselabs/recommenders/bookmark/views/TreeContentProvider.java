package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TreeContentProvider implements ITreeContentProvider {
	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof TreeParent) {
			return ((TreeParent) element).getChildren();
		}

		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof TreeObject) {
			return ((TreeObject) element).getParent();
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TreeParent)
			return ((TreeParent) element).hasChildren();
		return false;

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
