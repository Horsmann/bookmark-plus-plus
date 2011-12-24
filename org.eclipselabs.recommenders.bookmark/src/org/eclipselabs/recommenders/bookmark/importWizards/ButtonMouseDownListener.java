package org.eclipselabs.recommenders.bookmark.importWizards;

import java.io.File;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

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
			bookmarkImportWizardPage.setPageComplete(true);
		}

	}
}
