package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;

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
		saveNewTreeModelState();
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

	private void saveNewTreeModelState()
	{
		BookmarkView viewer = manager.getActiveBookmarkView();
		TreeModel model = manager.getModel();

		TreeSerializerFacade
				.serializeToDefaultLocation(viewer.getView(), model);
	}

}
