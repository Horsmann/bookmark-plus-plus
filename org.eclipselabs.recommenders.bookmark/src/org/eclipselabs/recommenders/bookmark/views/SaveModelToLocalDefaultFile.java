package org.eclipselabs.recommenders.bookmark.views;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.IPath;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeDeSerializer;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;

public class SaveModelToLocalDefaultFile {

	private TreeModel model;

	public SaveModelToLocalDefaultFile(TreeModel model) {
		this.model = model;
	}

	public void saveChanges() {
		String serializedString = TreeDeSerializer.serializeTreeToGson(model
				.getModelRoot());
		saveInMetaData(serializedString);
	}

	private void saveInMetaData(String string) {
		System.out.println("Save has been called");

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
