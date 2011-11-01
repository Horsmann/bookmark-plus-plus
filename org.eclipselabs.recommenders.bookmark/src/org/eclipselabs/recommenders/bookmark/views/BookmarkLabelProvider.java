package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.viewers.LabelProvider;

public class BookmarkLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		String elementText = (String) element;
		return elementText;
	}

}
