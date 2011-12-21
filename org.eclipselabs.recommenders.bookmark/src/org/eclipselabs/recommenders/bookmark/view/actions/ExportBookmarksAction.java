package org.eclipselabs.recommenders.bookmark.view.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;
import org.eclipselabs.recommenders.dialog.ExportDialog;

public class ExportBookmarksAction
	extends Action
{

	private final ViewManager manager;

	public ExportBookmarksAction(ViewManager manager)
	{
		this.manager = manager;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_SAVE_BOOKMARKS));
		this.setToolTipText("Exports all bookmarks");
		this.setText("Export Bookmarks");
	}

	@Override
	public void run()
	{

		File file = ExportDialog.showDialog();

		if (file != null) {
			TreeViewer viewer = manager.getActiveBookmarkView().getView();
			TreeModel model = manager.getActiveBookmarkView().getModel();
			TreeSerializerFacade.serialize(viewer, model, file);
		}

	}

}
