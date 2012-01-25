/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipselabs.recommenders.bookmark.aaa.wizard.exporting;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class BookmarkExportWizardPage
	extends WizardPage
{


	protected BookmarkExportWizardPage(String pageName)
	{
		super(pageName, pageName, null);
		setDescription("Export bookmarks to an external file\nNo selection = export all");
	}

	@Override
	public void createControl(Composite parent)
	{

	}


}
