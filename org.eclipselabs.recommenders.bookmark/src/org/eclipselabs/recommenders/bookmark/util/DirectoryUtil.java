package org.eclipselabs.recommenders.bookmark.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

public class DirectoryUtil
{
	public static String getProjectName(Object value)
	{

		if (value instanceof IJavaElement) {
			return getProjectNameOfIJavaElement(value);
		}

		if (value instanceof IFile) {
			return getProjectOfIFile(value);
		}

		return "";
	}

	private static String getProjectOfIFile(Object value)
	{
		IFile iFile = (IFile) value;
		IProject iProject = iFile.getProject();
		String project = iProject.getName();
		return project;
	}

	private static String getProjectNameOfIJavaElement(Object value)
	{
		IJavaElement element = (IJavaElement) value;
		IResource iResource = element.getResource();
		IProject iProject = iResource.getProject();
		String name = iProject.getName();
		return name;
	}

	public static String getCompilationUnitName(IJavaElement element)
	{
		
		ICompilationUnit compilationUnit = seekCompilationUnit(element);

		if (compilationUnit == null) {
			return "";
		}

		String compilationUnitName = compilationUnit.getElementName();

		return compilationUnitName;
	}

	private static ICompilationUnit seekCompilationUnit(IJavaElement element)
	{
		if (element instanceof ICompilationUnit) {
			return (ICompilationUnit) element;
		}

		while ((element = element.getParent()) != null) {
			if (element instanceof ICompilationUnit) {
				return (ICompilationUnit) element;
			}
		}

		return null;
	}

}
