package org.eclipselabs.recommenders.bookmark.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

				TreeNode root = deSerializeString(filecontent);

				if (root != null) {
					model.setRootNode(root);
					viewer.setInput(root);
				}
			}
		}

	}

	private TreeNode deSerializeString(String filecontent) {
		Gson gson = new Gson();
		Type typeOfSrc = new TypeToken<TreeNode>() {
		}.getType();
		TreeNode rootNode = gson.fromJson(filecontent, typeOfSrc);

		TreeNode deSerializedRoot = TreeDeSerializer.deSerializeTree(rootNode);
		return deSerializedRoot;
	}

	private String readSelectedFile(File file) {
		BufferedReader reader = null;
		String readline = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			readline = reader.readLine();
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
		return readline;
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
