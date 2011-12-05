package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.LinkedList;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipselabs.recommenders.bookmark.view.actions.SelfEnabling;

public class TreeSelectionListener implements SelectionListener{
	
	private LinkedList<SelfEnabling> selectionDependendSelfEnablers;
	
	public TreeSelectionListener() {
		selectionDependendSelfEnablers = new LinkedList<SelfEnabling>();
	}
	
	public void add(SelfEnabling selfEnabling){
		selectionDependendSelfEnablers.add(selfEnabling);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		for (SelfEnabling se : selectionDependendSelfEnablers){
			se.updateEnabledStatus();
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
