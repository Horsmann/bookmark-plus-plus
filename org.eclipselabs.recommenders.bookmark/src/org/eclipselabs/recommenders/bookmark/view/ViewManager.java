package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.part.ViewPart;

public interface ViewManager {
	
	public ViewPart getViewPart();
	
	public void activateNextView();
	
	public TreeViewer getActiveViewer();

}