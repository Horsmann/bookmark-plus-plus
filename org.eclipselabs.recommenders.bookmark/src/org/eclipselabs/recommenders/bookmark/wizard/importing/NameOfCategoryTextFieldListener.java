package org.eclipselabs.recommenders.bookmark.wizard.importing;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;

public class NameOfCategoryTextFieldListener
	implements KeyListener
{

	private final BookmarkImportWizardPage bookmarkImportWizardPage;
	private final Text nameOfNewCategory;

	public NameOfCategoryTextFieldListener(
			BookmarkImportWizardPage bookmarkImportWizardPage,
			Text nameOfNewCategory)
	{
		this.bookmarkImportWizardPage = bookmarkImportWizardPage;
		this.nameOfNewCategory = nameOfNewCategory;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (nameOfNewCategory.getText().compareTo("") == 0) {
			bookmarkImportWizardPage.setPageComplete(false);
		}
		else {
			bookmarkImportWizardPage.updateCompletionStatus();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub

	}

}
