package org.eclipselabs.recommenders.bookmark.importWizards;

import java.io.File;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;

public class ButtonMouseDownListener
	implements Listener
{
	private final Text textField;
	private final BookmarkImportWizardPage bookmarkImportWizardPage;

	public ButtonMouseDownListener(Text textField,
			BookmarkImportWizardPage bookmarkImportWizardPage)
	{
		this.textField = textField;
		this.bookmarkImportWizardPage = bookmarkImportWizardPage;
	}

	@Override
	public void handleEvent(Event event)
	{
		File file = ImportDialog.showDialog();

		if (file != null) {
			textField.setText(file.getAbsolutePath());
			bookmarkImportWizardPage.setImportFile(file);

			String[] data = BookmarkFileIO.readFromFile(file);
			if (data.length > 0) {
				RestoredTree rstTree = TreeDeserializerFacade
						.deserialize(data[0]);
				BMNode node = rstTree.getRoot();
				bookmarkImportWizardPage.getView().setInput(node);
				bookmarkImportWizardPage.getView().expandAll();
			}

			bookmarkImportWizardPage.setPageComplete(true);
		}

	}
}
