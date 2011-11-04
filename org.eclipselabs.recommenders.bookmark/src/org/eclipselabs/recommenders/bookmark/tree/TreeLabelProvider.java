package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TreeLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof TreeObject)
			return ((TreeObject) element).getName();
		return "";
	}

	@Override
	public Image getImage(Object element) {
		
		
		
		return null;
	}
}
