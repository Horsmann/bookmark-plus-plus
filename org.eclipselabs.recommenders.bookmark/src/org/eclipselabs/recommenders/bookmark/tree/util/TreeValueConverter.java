package org.eclipselabs.recommenders.bookmark.tree.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

public class TreeValueConverter
{

	public static String getStringIdentification(Object value)
	{
		String id = null;
		if (value instanceof IJavaElement) {
			id = ((IJavaElement) value).getHandleIdentifier();
		}
		else if (value instanceof IFile) {

			IFile file = (IFile) value;

			id = file.getFullPath()
					.makeRelativeTo(file.getProjectRelativePath()).toOSString();

		}
		else if (value instanceof String) {
			id = (String) value;
		}

		return id;
	}

	public static IFile attemptTransformationToIFile(String value)
	{
		try {
			IPath location = Path.fromOSString((String) value);
			IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(location);
			return file;
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static IJavaElement attemptTransformationToIJavaElement(String value)
	{
		IJavaElement element = JavaCore.create(value);
		return element;
	}

}
