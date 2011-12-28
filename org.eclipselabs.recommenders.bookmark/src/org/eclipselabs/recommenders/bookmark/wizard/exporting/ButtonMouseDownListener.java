package org.eclipselabs.recommenders.bookmark.wizard.exporting;

import java.io.File;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class ButtonMouseDownListener
	implements Listener
{
	private final Text textField;
	private final BookmarkExportWizardPage bookmarkExportWizardPage;

	public ButtonMouseDownListener(Text textField,
			BookmarkExportWizardPage bookmarkImportWizardPage)
	{
		this.textField = textField;
		this.bookmarkExportWizardPage = bookmarkImportWizardPage;
	}

	@Override
	public void handleEvent(Event event)
	{
		File file = ExportDialog.showDialog();

		if (file != null) {
			textField.setText(file.getAbsolutePath());
			bookmarkExportWizardPage.setExportFile(file);
			bookmarkExportWizardPage.setPageComplete(true);
			System.out.println("export button mouse down");
		}

	}
}
