package org.eclipselabs.recommenders.bookmark.aaa.wizard.importing;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public class NameOfCategoryTextFieldListener
	implements ModifyListener
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

	public boolean isValid()
	{
		return !(nameOfNewCategory.getText().compareTo("") == 0);
	}

	@Override
	public void modifyText(ModifyEvent e)
	{
		if (!isValid()) {
			bookmarkImportWizardPage.setPageComplete(false);
		}
		else {
			bookmarkImportWizardPage.getChecker().checkCompletion();
		}

	}

}
