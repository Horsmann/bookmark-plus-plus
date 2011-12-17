package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.bookmark.util.DirectoryUtil;
import org.junit.Before;
import org.junit.Test;

public class DirectoryUtilTest
{

	IJavaElement element;

	@Test
	public void getCompilationUnitName()
	{
		String expected = "IMy.java";
		String actually = DirectoryUtil.getCompilationUnitName(element);

		assertEquals(expected, actually);
	}

	@Test
	public void testGetProjectName()
	{

		String expected = "LKJLD";
		String actually = DirectoryUtil.getProjectName(element);

		assertEquals(expected, actually);
	}


	@Before
	public void setUp()
	{
		element = createJavaElement();
	}

	private IJavaElement createJavaElement()
	{
		String idOfElement = "=LKJLD/src<test.project{IMy.java[IMy~add~I";
		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement(idOfElement);
		return element;
	}

}
