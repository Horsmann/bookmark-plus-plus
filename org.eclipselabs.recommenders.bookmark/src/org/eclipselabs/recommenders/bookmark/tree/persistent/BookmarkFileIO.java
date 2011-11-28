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

public class BookmarkFileIO {

	public static void writeToFile(File file, String serializedTree) {
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

	public static String[] readFromFile(File file) {
		return readFileContent(file);
	}

	public static String[] loadFromDefaultFile() {
		IPath stateLocation = Activator.getDefault().getStateLocation();
		File stateFile = stateLocation.append(Activator.AUTOSAVE_FILE).toFile();
		return readFileContent(stateFile);
	}

	private static String[] readFileContent(File file) {
		BufferedReader reader = null;
		String[] lines = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));

			String line = null;
			String serialized = "";
			while ((line = reader.readLine()) != null) {
				serialized += line + "\n";
			}

			if (serialized.compareTo("") == 0) // No file exist
				return new String[] {};

			lines = serialized.split("\n");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// Ignore silently
			// There is no file at first start up
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lines;
	}

}
