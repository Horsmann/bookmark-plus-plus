package org.eclipselabs.recommenders.bookmark.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeDeSerializer;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class LoadBookmarksAction extends Action {

	private TreeViewer viewer;
	private TreeModel model;

	public LoadBookmarksAction(TreeViewer viewer, TreeModel model) {
		super();
		this.viewer = viewer;
		this.model = model;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_LOAD_BOOKMARKS));
	}

	@Override
	public void run() {

		File file = showFileOpenDialog();
		if (file != null) {
			String filecontent = readSelectedFile(file);
			if (filecontent != null) {

				TreeNode root = TreeDeSerializer
						.deSerializeTreeFromGSonString(filecontent);

				model.setRootNode(root);
				viewer.setInput(root);
			}
		}

	}

	private String readSelectedFile(File file) {
		BufferedReader reader = null;
		String content = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));

			String readline = null;
			while ((readline = reader.readLine()) != null)
				content = (content == null) ? readline : (content = content
						+ "\n" + readline);
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
		return content;
	}

	private File showFileOpenDialog() {
		Shell shell = Display.getCurrent().getActiveShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] { "*.bm" });
		String fileName = fileDialog.open();
		if (fileName != null)
			return new File(fileName);

		return null;
	}

}
