package org.eclipselabs.recommenders.bookmark.wizard.importing;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class CheckboxListener
	implements Listener
{

	private final Text nameOfNewCategory;
	private final BookmarkImportWizardPage bookmarkImportWizardPage;

	public CheckboxListener(BookmarkImportWizardPage bookmarkImportWizardPage,
			Text nameOfNewCategory)
	{
		this.bookmarkImportWizardPage = bookmarkImportWizardPage;
		this.nameOfNewCategory = nameOfNewCategory;
	}

	@Override
	public void handleEvent(Event event)
	{
		if (nameOfNewCategory.isEnabled()) {
			nameOfNewCategory.setEnabled(false);
		}
		else {
			nameOfNewCategory.setEnabled(true);
		}
		bookmarkImportWizardPage.getChecker().checkCompletion();
	}

	public boolean isEnabled()
	{
		return nameOfNewCategory.isEnabled();
	}

}
