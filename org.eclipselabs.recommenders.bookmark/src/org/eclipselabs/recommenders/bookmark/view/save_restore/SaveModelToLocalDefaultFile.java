package org.eclipselabs.recommenders.bookmark.view.save_restore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeDeSerializer;

public class SaveModelToLocalDefaultFile {

	private TreeModel model;
	private TreeViewer viewer;

	public SaveModelToLocalDefaultFile(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;
	}

	public void saveChanges() {
		TreeNode root = model.getModelRoot();
		viewer.refresh();
		Object [] expanded = viewer.getExpandedElements();
		String serializedString = TreeDeSerializer.serializeTreeToGson(root, expanded);
		saveSerializedTreeModel(serializedString);
	}

	private void saveSerializedTreeModel(String string) {

		IPath stateLocation = Activator.getDefault().getStateLocation();
		File stateFile = stateLocation.append(Activator.AUTOSAVE_FILE).toFile();

		writeStringToFile(stateFile, string);
	}

	private void writeStringToFile(File file, String serializedTree) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));

			writer.write(serializedTree);
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
