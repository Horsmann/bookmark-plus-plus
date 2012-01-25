package org.eclipselabs.recommenders.bookmark.aaa.wizard.exporting;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class BookmarkExportWizard
	extends Wizard
	implements IExportWizard
{

	private BookmarkExportWizardPage mainPage;

	public BookmarkExportWizard()
	{
		super();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		mainPage = new BookmarkExportWizardPage("Export Bookmarks");
	}

	@Override
	public void addPages()
	{
		super.addPages();
		addPage(mainPage);
	}

	@Override
	public boolean performFinish()
	{
	    System.out.println("perform finished");

		return true;
	}

}
