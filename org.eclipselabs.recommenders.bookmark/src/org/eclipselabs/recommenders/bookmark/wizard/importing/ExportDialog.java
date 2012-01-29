package org.eclipselabs.recommenders.bookmark.wizard.importing;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.recommenders.bookmark.Activator;

public class ExportDialog
{

	public static File showDialog()
	{
		Shell shell = Display.getCurrent().getActiveShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setFilterExtensions(new String[] { "*"
				+ Activator.fileEnding });
		String fileName = fileDialog.open();
		if (fileName != null) {
			return new File(fileName);
		}

		return null;
	}

}
