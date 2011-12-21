package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipselabs.recommenders.bookmark.util.ResourceAvailabilityValidator;
import org.junit.Test;

public class ResourceAvailabilityValidatorTest
{
	@Test
	public void testIsResourceAvailableForOpenedProjectWithExistingObject()
	{
		IJavaElement element = getOpenProjectAndExistingObjectJavaElement();
		boolean isAvailable = ResourceAvailabilityValidator
				.isResourceAvailable(element);

		assertTrue(isAvailable);
	}

	private IJavaElement getOpenProjectAndExistingObjectJavaElement()
	{
		IJavaElement element = mock(IJavaElement.class);
		IResource resource = mock(IResource.class);
		IJavaProject javaProject = mock(IJavaProject.class);
		IProject project = mock(IProject.class);
		when(project.isOpen()).thenReturn(true);
		when(resource.exists()).thenReturn(true);
		when(element.getResource()).thenReturn(resource);
		when(element.getJavaProject()).thenReturn(javaProject);
		when(element.exists()).thenReturn(true);
		when(javaProject.getProject()).thenReturn(project);
		return element;
	}

	@Test
	public void testIsResourceAvailableForOpenedProjectWithNonExistingObject()
	{
		IJavaElement element = getOpenProjectButNonExistingObjectJavaElement();
		boolean isAvailable = ResourceAvailabilityValidator
				.isResourceAvailable(element);

		assertFalse(isAvailable);
	}

	private IJavaElement getOpenProjectButNonExistingObjectJavaElement()
	{
		IJavaElement element = mock(IJavaElement.class);
		IJavaProject javaProject = mock(IJavaProject.class);
		IProject project = mock(IProject.class);
		when(element.getJavaProject()).thenReturn(javaProject);
		when(javaProject.getProject()).thenReturn(project);
		return element;
	}

	@Test
	public void testIsResourceAvailableForAnyObject()
	{
		Object object = new Object();
		boolean isAvailable = ResourceAvailabilityValidator
				.isResourceAvailable(object);

		assertFalse(isAvailable);
	}

	@Test
	public void testdoesProjectExistForIJavaElement()
	{
		Object element = getIJavaElement();
		boolean exist = ResourceAvailabilityValidator
				.doesAssociatedProjectExists(element);

		assertTrue(exist);
	}

	@Test
	public void testdoesProjectExistForIFile()
	{
		Object ifile = getIFile();
		boolean exist = ResourceAvailabilityValidator
				.doesAssociatedProjectExists(ifile);

		assertTrue(exist);
	}

	@Test
	public void testdoesProjectExistForAnyObject()
	{
		Object object = new Object();

		boolean exist = ResourceAvailabilityValidator
				.doesAssociatedProjectExists(object);

		assertFalse(exist);
	}

	@Test
	public void testIsProjectOpenForAnyObject()
	{
		Object object = new Object();
		boolean isOpen = ResourceAvailabilityValidator
				.isAssociatedProjectOpen(object);

		assertFalse(isOpen);
	}

	@Test
	public void testIsProjectOpenForIFile()
	{
		Object ifile = getIFile();
		boolean isOpen = ResourceAvailabilityValidator
				.isAssociatedProjectOpen(ifile);

		assertTrue(isOpen);
	}

	@Test
	public void testIsProjectOpenForIJavaElement()
	{
		Object javaElement = getIJavaElement();
		boolean isOpen = ResourceAvailabilityValidator
				.isAssociatedProjectOpen(javaElement);

		assertTrue(isOpen);
	}

	@Test
	public void testObjectExistenceField()
	{
		Object field = getIField();
		boolean exist = ResourceAvailabilityValidator
				.doesReferecedObjectExists(field);

		assertTrue(exist);
	}

	@Test
	public void testObjectExistenceMethod()
	{
		Object method = getIMethod();
		boolean exist = ResourceAvailabilityValidator
				.doesReferecedObjectExists(method);

		assertTrue(exist);

	}

	@Test
	public void testObjectExistenceType()
	{
		Object type = getIType();
		boolean exist = ResourceAvailabilityValidator
				.doesReferecedObjectExists(type);

		assertTrue(exist);

	}

	@Test
	public void testObjectExistenceIJavaElement()
	{
		Object type = getIJavaElement();
		boolean exist = ResourceAvailabilityValidator
				.doesReferecedObjectExists(type);

		assertTrue(exist);

	}

	@Test
	public void testObjectExistenceIFile()
	{
		Object type = getIFile();
		boolean exist = ResourceAvailabilityValidator
				.doesReferecedObjectExists(type);

		assertTrue(exist);

	}

	@Test
	public void testObjectExistenceUnknownObject()
	{
		Object object = new Object();
		boolean exist = ResourceAvailabilityValidator
				.doesReferecedObjectExists(object);

		assertFalse(exist);

	}

	private IFile getIFile()
	{
		IFile ifile = mock(IFile.class);

		IProject project = mock(IProject.class);
		when(project.exists()).thenReturn(true);
		when(project.isOpen()).thenReturn(true);
		when(ifile.getProject()).thenReturn(project);
		when(ifile.exists()).thenReturn(true);
		return ifile;
	}

	private IJavaElement getIJavaElement()
	{
		IJavaElement element = mock(IJavaElement.class);
		IJavaProject javaProject = mock(IJavaProject.class);
		IProject project = mock(IProject.class);
		IResource resource = mock(IResource.class);

		when(resource.exists()).thenReturn(true);
		when(project.exists()).thenReturn(true);
		when(project.isOpen()).thenReturn(true);

		when(element.getJavaProject()).thenReturn(javaProject);
		when(javaProject.getProject()).thenReturn(project);
		when(element.getResource()).thenReturn(resource);
		return element;
	}

	private IType getIType()
	{
		IType type = mock(IType.class);
		when(type.exists()).thenReturn(true);

		return type;
	}

	private IMethod getIMethod()
	{
		IMethod method = mock(IMethod.class);
		when(method.exists()).thenReturn(true);

		return method;
	}

	private IField getIField()
	{
		IField field = mock(IField.class);
		when(field.exists()).thenReturn(true);
		return field;
	}

}
