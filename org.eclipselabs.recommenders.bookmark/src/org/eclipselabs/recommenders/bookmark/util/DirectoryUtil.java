package org.eclipselabs.recommenders.bookmark.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

public class DirectoryUtil
{
	public static String getProjectName(IJavaElement element)
	{
		IResource resource = element.getResource();
		IProject project = resource.getProject();

		String projectName = project.getName();

		return projectName;
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
