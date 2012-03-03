package org.eclipselabs.recommenders.bookmark.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;

public class BookmarkModelClonerTest {

    private Category categoryA;
    private Category categoryB;
    private JavaElementBookmark compUnitA1;
    private JavaElementBookmark compUnitA2;
    private JavaElementBookmark compUnitB1;
    private JavaElementBookmark compUnitB2;
    private JavaElementBookmark compUnitB3;
    private BookmarkModel masterModel;
    private JavaElementBookmark typeA11;
    private FileBookmark fileBM;
    private IFile ifile;

    @Test
    public void testModelCloner() {
        BookmarkModelCloner bookmarkModelCloner = new BookmarkModelCloner(masterModel);
        BookmarkModel clone = bookmarkModelCloner.clone();
        compareCategoryLevel(clone);
        compareCompilationUnitLevel(clone);
        compareTypeLevel(clone);
    }

    private void compareTypeLevel(BookmarkModel clone) {
        assertEquals(compUnitA1.getChildElements().size(), ((JavaElementBookmark) clone.getCategories().get(0)
                .getBookmarks().get(0)).getChildElements().size());
    }

    private void compareCompilationUnitLevel(BookmarkModel clone) {
        Category mA = masterModel.getCategories().get(0);
        Category mB = masterModel.getCategories().get(1);
        Category cA = clone.getCategories().get(0);
        Category cB = clone.getCategories().get(1);

        assertEquals(mA.getBookmarks().size(), cA.getBookmarks().size());
        assertNotSame(compUnitA1, cA.getBookmarks().get(0));
        assertEquals(compUnitA1.getHandleId(), ((JavaElementBookmark) cA.getBookmarks().get(0)).getHandleId());
        assertNotSame(compUnitA2, cA.getBookmarks().get(1));
        assertEquals(compUnitA2.getHandleId(), ((JavaElementBookmark) cA.getBookmarks().get(1)).getHandleId());
        assertNotSame(fileBM, cA.getBookmarks().get(2));
        assertEquals(FileBookmark.getRelativePath(ifile),
                FileBookmark.getRelativePath(((FileBookmark) cA.getBookmarks().get(2)).getFile()));

        assertEquals(mB.getBookmarks().size(), cB.getBookmarks().size());
        assertNotSame(compUnitB1, cB.getBookmarks().get(0));
        assertEquals(compUnitB1.getHandleId(), ((JavaElementBookmark) cB.getBookmarks().get(0)).getHandleId());
        assertNotSame(compUnitB2, cB.getBookmarks().get(1));
        assertEquals(compUnitB2.getHandleId(), ((JavaElementBookmark) cB.getBookmarks().get(1)).getHandleId());
        assertNotSame(compUnitB3, cB.getBookmarks().get(2));
        assertEquals(compUnitB3.getHandleId(), ((JavaElementBookmark) cB.getBookmarks().get(2)).getHandleId());
    }

    private void compareCategoryLevel(BookmarkModel clone) {
        assertEquals(masterModel.getCategories().size(), clone.getCategories().size());
        assertNotSame(masterModel.getCategories().get(0), clone.getCategories().get(0));
        assertEquals(masterModel.getCategories().get(0).getLabel(), masterModel.getCategories().get(0).getLabel());
        assertNotSame(masterModel.getCategories().get(1), clone.getCategories().get(1));
        assertEquals(masterModel.getCategories().get(1).getLabel(), masterModel.getCategories().get(1).getLabel());
    }

    @Before
    public void setUp() {
        buildModel();
    }

    private void buildModel() {
        createComponents();
        setRelationship();
    }

    private void setRelationship() {
        compUnitA1.addChildElement(typeA11);
        categoryA.add(compUnitA1);
        categoryA.add(compUnitA2);
        categoryA.add(fileBM);
        categoryB.add(compUnitB1);
        categoryB.add(compUnitB2);
        categoryB.add(compUnitB3);
        masterModel.add(categoryA);
        masterModel.add(categoryB);
    }

    private void createComponents() {
        categoryA = new Category("CategoryA");
        categoryB = new Category("CategoryB");
        compUnitA1 = new JavaElementBookmark("=LKJLD/src<test.project{A1.java", true);
        typeA11 = new JavaElementBookmark("=LKJLD/src<test.project{A1.java[A1", false);
        compUnitA2 = new JavaElementBookmark("=LKJLD/src<test.project{A2.java", false);

        IPath location = Path.fromOSString("../LKJLD/testfile.txt");
        ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
        fileBM = new FileBookmark(ifile);
        compUnitB1 = new JavaElementBookmark("=LKJLD/src<test.project{B1.java", false);
        compUnitB2 = new JavaElementBookmark("=LKJLD/src<test.project{B2.java", false);
        compUnitB3 = new JavaElementBookmark("=LKJLD/src<test.project{B3.java", false);

        masterModel = new BookmarkModel();
    }

}
