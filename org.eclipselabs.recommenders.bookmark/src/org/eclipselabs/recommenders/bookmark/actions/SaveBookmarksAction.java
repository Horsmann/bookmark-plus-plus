package org.eclipselabs.recommenders.bookmark.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import org.eclipse.jface.action.Action;
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

public class SaveBookmarksAction extends Action {

	private TreeModel model;
	
	public SaveBookmarksAction(TreeModel model) {
		this.model = model;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_SAVE_BOOKMARKS));
	}

	@Override
	public void run() {

		File file = showFileSaveDialog();

		if (file != null) {

			TreeNode root = createPreSerializedTree();
			if (root != null) {
				String serializedTree = serializeTreeToString(root);
				if (serializedTree != null) {
					writeStringToFile(file, serializedTree);
				}
			}

		}

		// headNode = new TreeNode("sdfsd");
		// TreeNode bm = new TreeNode("xxx", true);
		// headNode.addChild(bm);

		// GsonBuilder gsonbuilder = new GsonBuilder().serializeNulls();

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

	private String serializeTreeToString(TreeNode root) {
		Gson gson = new Gson();
		Type typeOfSrc = new TypeToken<TreeNode>() {
		}.getType();
		String gsonTreeString = gson.toJson(root, typeOfSrc);
		return gsonTreeString;
	}

	/**
	 * The values of the TreeNodes are transformed to a string representation,
	 * the parent<->child relationship is reduced to parent->child (removing
	 * circles)
	 * 
	 * @return
	 */
	private TreeNode createPreSerializedTree() {
		
		return TreeDeSerializer.serializeTree(model.getModelRoot());
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
