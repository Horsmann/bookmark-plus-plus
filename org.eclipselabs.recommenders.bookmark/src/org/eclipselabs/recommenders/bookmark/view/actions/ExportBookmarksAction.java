package org.eclipselabs.recommenders.bookmark.view.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.dialog.ExportDialog;

public class ExportBookmarksAction extends Action {

	private BookmarkView viewer;

	public ExportBookmarksAction(BookmarkView viewer) {
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_SAVE_BOOKMARKS));
		this.setToolTipText("Exports all bookmarks");
		this.setText("Export Bookmarks");
	}

	@Override
	public void run() {

		File file = ExportDialog.showDialog();

		if (file != null) {
			TreeSerializerFacade.serialize(viewer.getView(), viewer.getModel(),
					file);
		}

	}

}
