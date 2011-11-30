package org.eclipselabs.recommenders.bookmark.util;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;

public class ResourceAvailabilityValidator {

	public static boolean isResourceAvailable(Object value) {
		return isAssociatedProjectOpen(value)
				&& doesReferecedObjectExists(value)
				&& doesAssociatedProjectExists(value);
	}

	public static boolean isAssociatedProjectOpen(Object value) {
		if (value instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) value;
			IProject project = element.getJavaProject().getProject();
			boolean isOpen = project.isOpen();
			return isOpen;
		}

		if (value instanceof IFile) {
			IFile file = (IFile) value;
			IProject project = file.getProject();
			boolean isOpen = project.isOpen();
			return isOpen;
		}

		return false;
	}

	public static boolean doesAssociatedProjectExists(Object value) {
		if (value instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) value;
			IProject project = element.getJavaProject().getProject();
			boolean doesExist = project.exists();
			return doesExist;
		}

		if (value instanceof IFile) {
			IFile file = (IFile) value;
			IProject project = file.getProject();
			boolean doesExist = project.exists();
			return doesExist;
		}

		return false;
	}

	public static boolean doesReferecedObjectExists(Object value) {

		if (value instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) value;
			IResource resource = element.getResource();
			boolean doesExist = resource.exists();
			
//			boolean x = element.getJavaModel().exists();

			if (doesExist == false) {
				File file = resource.getRawLocation().toFile();
				if (file != null) {
					doesExist = file.exists();
				}
			}

			// boolean doesExist = element.exists();
			return doesExist;
		}

		if (value instanceof IFile) {
			IFile file = (IFile) value;
			boolean doesExist = file.exists();
			return doesExist;
		}

		return true;
	}

}
