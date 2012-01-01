package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;

public class ViewPartListener
	implements IPartListener2
{

	private final ViewManager manager;

	public ViewPartListener(ViewManager manager)
	{
		this.manager = manager;
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef)
	{

	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef)
	{

	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef)
	{
		manager.getExpandedStorage().addCurrentlyExpandedNodes();
		manager.saveModelState();
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef)
	{

	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef)
	{

	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef)
	{

	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef)
	{

	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef)
	{

	}

}
