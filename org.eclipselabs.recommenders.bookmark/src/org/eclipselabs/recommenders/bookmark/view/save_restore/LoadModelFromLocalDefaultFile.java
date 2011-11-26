package org.eclipselabs.recommenders.bookmark.view.save_restore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeDeSerializer;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class LoadModelFromLocalDefaultFile {
	private TreeModel model;
	private TreeViewer viewer;

	public LoadModelFromLocalDefaultFile(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;
	}

	public void load() throws IOException {
		IPath stateLocation = Activator.getDefault().getStateLocation();
		File stateFile = stateLocation.append(Activator.AUTOSAVE_FILE).toFile();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(stateFile), "UTF-8"));

		String line = null;
		String serialized = "";
		while ((line = reader.readLine()) != null){
			serialized += line + "\n";
		}
		
		if (serialized.compareTo("")==0) //No file exist
			return;

		String[] lines = serialized.split("\n");

		String serializedTree = lines[0];
		String expandedNodes = lines[1];

		TreeNode newRoot = TreeDeSerializer
				.deSerializeTreeFromGSonString(serializedTree);

		model.setModelRoot(newRoot);
		viewer.setInput(model.getModelRoot());

		String[] idsExpandedNodes = expandedNodes.split(";");
		for (String id : idsExpandedNodes) {
			TreeNode node = TreeUtil.locateNodeWithEqualID(id, newRoot);
			if (node != null)
				viewer.setExpandedState(node, true);
		}
		
		viewer.refresh();
	}
}
