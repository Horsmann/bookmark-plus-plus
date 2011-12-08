package org.eclipselabs.recommenders.dialog;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ExportDialog {
	
	public static File showDialog() {
		Shell shell = Display.getCurrent().getActiveShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setFilterExtensions(new String[] { "*.bm" });
		String fileName = fileDialog.open();
		if (fileName != null)
			return new File(fileName);

		return null;
	}

}