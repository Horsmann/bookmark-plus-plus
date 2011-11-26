package org.eclipselabs.recommenders.bookmark.view.save_restore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IPath;
import org.eclipselabs.recommenders.bookmark.Activator;

public class BookmarkLoader {

	public static String[] loadFile(File file) throws IOException {
		return readFileContent(file);
	}

	public static String[] loadDefaultFile() throws IOException {
		IPath stateLocation = Activator.getDefault().getStateLocation();
		File stateFile = stateLocation.append(Activator.AUTOSAVE_FILE).toFile();
		return readFileContent(stateFile);
	}

	private static String[] readFileContent(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF-8"));

		String line = null;
		String serialized = "";
		while ((line = reader.readLine()) != null) {
			serialized += line + "\n";
		}

		if (serialized.compareTo("") == 0) // No file exist
			return new String[] {};

		String[] lines = serialized.split("\n");
		return lines;
	}

}
