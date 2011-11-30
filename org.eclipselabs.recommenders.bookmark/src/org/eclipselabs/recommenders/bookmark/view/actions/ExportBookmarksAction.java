package org.eclipselabs.recommenders.bookmark.view.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.view.dialog.ExportDialog;

public class ExportBookmarksAction extends Action {

	private TreeModel model;
	private TreeViewer viewer;

	public ExportBookmarksAction(TreeViewer viewer, TreeModel model) {
		this.model = model;
		this.viewer = viewer;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_SAVE_BOOKMARKS));
		this.setToolTipText("Exports the collection of bookmarks");
	}

	@Override
	public void run() {

		File file = ExportDialog.showDialog();

		if (file != null) {
			TreeSerializerFacade.serialize(viewer, model, file);
		}

	}

}
