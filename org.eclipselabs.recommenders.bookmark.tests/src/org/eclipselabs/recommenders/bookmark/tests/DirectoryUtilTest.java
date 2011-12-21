package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.bookmark.util.DirectoryUtil;
import org.junit.Test;

public class DirectoryUtilTest
{

	@Test
	public void testGetCompilationUnitNameForNonCompilationUnitJavaElement()
	{
		IJavaElement element = createIMethodJavaElement();
		String expected = "IMy.java";
		String actually = DirectoryUtil.getCompilationUnitName(element);

		assertEquals(expected, actually);
	}

	@Test
	public void testGetCompilationUnitNameForCompilationUnitJavaelement()
	{
		IJavaElement element = createICompilationUnitJavaElement();
		String expected = "IMy.java";
		String actually = DirectoryUtil.getCompilationUnitName(element);

		assertEquals(expected, actually);
	}
	
	@Test
	public void testGetCompilationUnitNameForAnyIJavaElement() {
		IJavaElement element = mock(IJavaElement.class);
		String expected = "";
		String actually = DirectoryUtil.getCompilationUnitName(element);

		assertEquals(expected, actually);
	}
	

	@Test
	public void testGetProjectNameWithJavaElement()
	{
		IJavaElement element = createIMethodJavaElement();
		String expected = "LKJLD";
		String actually = DirectoryUtil.getProjectName(element);

		assertEquals(expected, actually);
	}

	@Test
	public void testGetProjectNameWithIFile()
	{
		IFile ifile = createIFile();
		String expected = "TestProj";
		String actually = DirectoryUtil.getProjectName(ifile);

		assertEquals(expected, actually);
	}
	
	@Test
	public void testGetProjectNameForAnyObject()
	{
		Object object = new Object();
		String actually = DirectoryUtil.getProjectName(object);
		String expected = "";

		assertEquals(expected, actually);
	}

	private IFile createIFile()
	{
		IFile ifile = TreeValueConverter
				.attemptTransformationToIFile("../../TestProj/resource/project.properties");
		return ifile;
	}
	
	private IJavaElement createICompilationUnitJavaElement()
	{
		String idOfElement = "=LKJLD/src<test.project{IMy.java";
		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement(idOfElement);
		return element;
	}

	private IJavaElement createIMethodJavaElement()
	{
		String idOfElement = "=LKJLD/src<test.project{IMy.java[IMy~add~I";
		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement(idOfElement);
		return element;
	}

}
