package org.eclipselabs.recommenders.bookmark.aaa.wizard.importing;

public class CompletionChecker
{

	private final FilePathModifyListener filePathListener;
	private final CheckboxListener checkBoxListener;
	private final NameOfCategoryTextFieldListener nameOfCategoryListener;
	private final BookmarkImportWizardPage bookmarkImportWizardPage;

	public CompletionChecker(BookmarkImportWizardPage bookmarkImportWizardPage,
			FilePathModifyListener filePathListener,
			CheckboxListener checkBoxListener,
			NameOfCategoryTextFieldListener nameOfCategoryListener)
	{
		this.bookmarkImportWizardPage = bookmarkImportWizardPage;
		this.filePathListener = filePathListener;
		this.checkBoxListener = checkBoxListener;
		this.nameOfCategoryListener = nameOfCategoryListener;
	}

	public void checkCompletion()
	{

		boolean isFilePathVaid = filePathListener.isValid();
		boolean isNewCategoryNameValid = (checkBoxListener.isEnabled() ? nameOfCategoryListener
				.isValid() : true);

		if (isFilePathVaid && isNewCategoryNameValid) {
			bookmarkImportWizardPage.setPageComplete(true);
		}
		else {
			bookmarkImportWizardPage.setPageComplete(false);
		}
	}
}
