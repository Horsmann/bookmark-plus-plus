package org.eclipselabs.recommenders.bookmark.importWizards;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.dialog.ImportDialog;

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
		IFile iFile = TreeValueConverter.attemptTransformationToIFile(file
				.getAbsolutePath());

		if (iFile != null) {
			textField.setText(iFile.getFullPath().toOSString());
			bookmarkImportWizardPage.setSelectedFile(iFile);
			bookmarkImportWizardPage.setPageComplete(true);
		}

	}
}
