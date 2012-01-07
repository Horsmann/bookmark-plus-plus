package org.eclipselabs.recommenders.bookmark.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeLabelProvider;

public class WizardTreeViewerLabelProvider
	extends TreeLabelProvider
{

	public WizardTreeViewerLabelProvider(ViewManager manager)
	{
		super(manager);
	}

	@Override
	protected boolean showExtendInformationInName(Object value)
	{
		return (value instanceof IJavaElement) || (value instanceof IFile);
	}

}
