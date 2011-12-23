package org.eclipselabs.recommenders.bookmark.importWizards;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;

public class TextFieldKeyListener
	implements Listener
{
	private final BookmarkImportWizardPage bookmarkImportWizardPage;
	private final Text textField;

	public TextFieldKeyListener(Text textField,
			BookmarkImportWizardPage bookmarkImportWizardPage)
	{
		this.textField = textField;
		this.bookmarkImportWizardPage = bookmarkImportWizardPage;
	}

	@Override
	public void handleEvent(Event event)
	{
		IFile iFile = TreeValueConverter.attemptTransformationToIFile(textField
				.getText());

		if (iFile == null) {
			bookmarkImportWizardPage.setPageComplete(false);
			return;
		}

		File file = iFile.getFullPath().toFile();

		if (isValid(file)) {
			bookmarkImportWizardPage.setSelectedFile(iFile);
			bookmarkImportWizardPage.setPageComplete(true);
			
		}
		else {
			bookmarkImportWizardPage.setSelectedFile(null);
			bookmarkImportWizardPage.setPageComplete(false);
		}

	}

	private boolean isValid(File file)
	{
		return file.exists()
				&& !file.isDirectory()
				&& (file.getAbsoluteFile().length() > 3)
				&& file.getAbsolutePath()
						.substring(file.getAbsolutePath().length() - 3)
						.compareTo(".bm") == 0;
	}

}
