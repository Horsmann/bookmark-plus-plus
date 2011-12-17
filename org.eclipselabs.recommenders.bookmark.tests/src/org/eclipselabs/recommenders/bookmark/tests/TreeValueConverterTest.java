package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.junit.Test;

public class TreeValueConverterTest {

	@Test
	public void testIFileCreationForValidString() throws CoreException {
		setUpDummyProject();
		String filestring = "../../TestProj/resource/project.properties";
		IFile file = TreeValueConverter
				.attemptTransformationToIFile(filestring);
		assertNotNull(file);
		removeDummyProject();
	}

	@Test
	public void testNullValueForInvalidIFileString() {
		String string = "I am an invalid IFile String";
		IFile file = TreeValueConverter.attemptTransformationToIFile(string);
		assertNull(file);
	}

	private void removeDummyProject() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("TestProj");
		project.delete(true, true, null);
	}

	private void setUpDummyProject() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("TestProj");
		project.create(null);
		project.open(null);
		project.getFolder("resource");
		project.getFile("project.properties");

	}

}
