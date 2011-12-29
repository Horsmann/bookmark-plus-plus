package org.eclipselabs.recommenders.bookmark.wizard.importing;

import java.io.File;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;

public class FilePathModifyListener
	implements Listener
{
	private final BookmarkImportWizardPage bookmarkImportWizardPage;
	private final Text textField;

	public FilePathModifyListener(Text textField,
			BookmarkImportWizardPage bookmarkImportWizardPage)
	{
		this.textField = textField;
		this.bookmarkImportWizardPage = bookmarkImportWizardPage;
	}

	@Override
	public void handleEvent(Event event)
	{
		File file = new File(textField.getText());

		if (isValid(file)) {

			setFileContentForViewer(file);

			bookmarkImportWizardPage.setImportFile(file);
			bookmarkImportWizardPage.setPageComplete(true);
			bookmarkImportWizardPage.getChecker().checkCompletion();
		}
		else {
			bookmarkImportWizardPage.getView().setInput(null);
			bookmarkImportWizardPage.setImportFile(null);
			bookmarkImportWizardPage.setPageComplete(false);
		}

	}

	private void setFileContentForViewer(File file)
	{
		String[] data = BookmarkFileIO.readFromFile(file);

		if (data.length > 0) {
			RestoredTree restore = TreeDeserializerFacade.deserialize(data[0]);
			bookmarkImportWizardPage.getView().setInput(restore.getRoot());
			bookmarkImportWizardPage.getView().expandAll();
		}

	}

	public boolean isValid()
	{
		File file = new File(textField.getText());
		return isValid(file);
	}

	private boolean isValid(File file)
	{
		return file.exists() && !file.isDirectory()
				&& hasCorrectFileEnding(file);
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
