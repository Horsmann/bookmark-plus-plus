package org.eclipselabs.recommenders.bookmark.wizard.exporting;

import java.io.File;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.Activator;

public class TextFieldKeyListener
	implements Listener
{
	private final BookmarkExportWizardPage bookmarkExportWizardPage;
	private final Text textField;

	public TextFieldKeyListener(Text textField,
			BookmarkExportWizardPage bookmarkImportWizardPage)
	{
		this.textField = textField;
		this.bookmarkExportWizardPage = bookmarkImportWizardPage;
	}

	@Override
	public void handleEvent(Event event)
	{
		File file = new File(textField.getText());

		if (isValid(file)) {
			bookmarkExportWizardPage.setExportFile(file);
			bookmarkExportWizardPage.setPageComplete(true);
			return;
		}

		bookmarkExportWizardPage.setExportFile(null);
		bookmarkExportWizardPage.setPageComplete(false);
	}


	private boolean isValid(File file)
	{
		return file != null && hasCorrectFileEnding(file);
	}

	private boolean hasCorrectFileEnding(File file)
	{
		String fileEnding = Activator.fileEnding;
		int suffixLen = fileEnding.length();

		String filePath = file.getAbsolutePath();
		int pathLen = filePath.length();

		if (pathLen - suffixLen < 0) {
			return false;
		}

		String currentFileEnding = filePath.substring(pathLen - suffixLen);

		boolean isEqual = currentFileEnding.compareTo(fileEnding) == 0;

		return isEqual;
	}

}
