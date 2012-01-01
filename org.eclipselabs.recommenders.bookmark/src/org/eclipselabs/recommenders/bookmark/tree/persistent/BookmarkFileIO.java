package org.eclipselabs.recommenders.bookmark.tree.persistent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.IPath;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class BookmarkFileIO
{

	public static void writeSerializedTreeToFile(File file,
			String serializedTree)
	{
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), Persistent.ENCODING));

			writer.write(serializedTree);
			writer.close();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void appendFlatAndToggledStateToFile(File file,
			ViewManager manager)
	{
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true), Persistent.ENCODING));

			writer.append("\n");

			TreeModel model = manager.getModel();
			Integer index = getIndexOfSelectedCategoryInModel(model);
			writer.append(index.toString());

			writer.append("\t");

			if (manager.isViewToggled()) {
				writer.append(Persistent.TOGGLED);
			}

			writer.append("\t");
			if (manager.isViewFlattened()) {
				writer.append(Persistent.FLAT);
			}

			writer.close();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int getIndexOfSelectedCategoryInModel(TreeModel model)
	{
		int index = -1;
		int numCat = model.getModelRoot().getChildren().length;
		BMNode head = model.getModelHead();

		for (int i = 0; i < numCat; i++) {
			BMNode node = model.getModelRoot().getChildren()[i];
			if (node == head) {
				index = i;
				break;
			}
		}

		return index;
	}

	public static String[] readFromFile(File file)
	{
		return readFileContent(file);
	}

	public static String[] loadFromDefaultFile()
	{
		IPath stateLocation = Activator.getDefault().getStateLocation();
		File stateFile = stateLocation.append(Activator.AUTOSAVE_FILE).toFile();
		return readFileContent(stateFile);
	}

	private static String[] readFileContent(File file)
	{
		BufferedReader reader = null;
		String[] lines = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), Persistent.ENCODING));

			String line = null;
			String serialized = "";
			while ((line = reader.readLine()) != null) {
				serialized += line + "\n";
			}

			if (serialized.compareTo("") == 0) {
				return new String[] {};
			}

			lines = serialized.split("\n");

		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			// Ignore silently
			// There is no file at first start up
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return lines;
	}

}
