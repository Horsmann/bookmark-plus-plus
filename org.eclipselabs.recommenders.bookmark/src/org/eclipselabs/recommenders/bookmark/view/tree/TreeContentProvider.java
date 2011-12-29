package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;

public class TreeContentProvider
	implements ITreeContentProvider
{
	@Override
	public Object[] getChildren(Object element)
	{
		return ((BMNode) element).getChildren();
	}

	@Override
	public Object getParent(Object element)
	{
		return ((BMNode) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element)
	{
		return ((BMNode) element).hasChildren();

	}

	@Override
	public Object[] getElements(Object element)
	{
		return getChildren(element);
	}

	@Override
	public void dispose()
	{

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
}
