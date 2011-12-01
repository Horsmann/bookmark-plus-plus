package org.eclipselabs.recommenders.bookmark.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

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

		if (value instanceof IField) {
			IField field = (IField) value;
			boolean exist = field.exists();
			return exist;
		}

		if (value instanceof IMethod) {
			IMethod method = (IMethod) value;
			boolean exist = method.exists();
			return exist;
		}

		if (value instanceof IType) {
			IType type = (IType) value;
			boolean exist = type.exists();
			return exist;
		}

		if (value instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) value;
			IResource resource = element.getResource();
			boolean exist = resource.exists();
			return exist;
		}

		if (value instanceof IFile) {
			IFile file = (IFile) value;
			boolean exist = file.exists();
			return exist;
		}

		return false;
	}

}
