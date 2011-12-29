package org.eclipselabs.recommenders.bookmark.view;

import java.util.LinkedList;

import org.eclipselabs.recommenders.bookmark.view.actions.SelfEnabling;

public class ControlNotifier
{

	private LinkedList<SelfEnabling> selfEnablers = new LinkedList<SelfEnabling>();

	public void add(SelfEnabling se)
	{
		selfEnablers.add(se);
	}

	public void fire()
	{
		for (SelfEnabling se : selfEnablers) {
			se.updateEnabledStatus();
		}
	}

}
