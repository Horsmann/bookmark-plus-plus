package org.eclipselabs.recommenders.bookmark.view.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.util.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.util.ObjectConverter;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeDeSerializer;

public class ExportBookmarksAction extends Action {

	private TreeModel model;
	private TreeViewer viewer;

	public ExportBookmarksAction(TreeViewer viewer, TreeModel model) {
		this.model = model;
		this.viewer = viewer;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_SAVE_BOOKMARKS));
		this.setToolTipText("Exports the collection of bookmarks");
	}

	@Override
	public void run() {

		File file = showFileSaveDialog();

		if (file != null) {
			ObjectConverter converter = new GsonConverter();
			String serializedTree = TreeDeSerializer.serializeTree(model
					.getModelRoot(), viewer.getExpandedElements(), converter);
			if (serializedTree != null) {
				writeStringToFile(file, serializedTree);
			}
		}

	}

	private void writeStringToFile(File file, String serializedTree) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));

			writer.write(serializedTree);
			writer.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File showFileSaveDialog() {
		Shell shell = Display.getCurrent().getActiveShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setFilterExtensions(new String[] { "*.bm" });
		String fileName = fileDialog.open();
		if (fileName != null)
			return new File(fileName);

		return null;
	}

}
