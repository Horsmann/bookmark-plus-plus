package org.eclipselabs.recommenders.bookmark.command;

import static org.junit.Assert.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.commands.ReorderBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.junit.Before;
import org.junit.Test;

public class RelocateBookmarksCommandTest {

    private BookmarkModel model;
    private String typePath;
    private JavaElementBookmark varMon;
    private JavaElementBookmark varWed;
    private JavaElementBookmark varSat;
    private BookmarkCommandInvoker invoker;
    private JavaElementBookmark type;
    private JavaElementBookmark varTue;
    private JavaElementBookmark varThu;
    private JavaElementBookmark varFri;
    private JavaElementBookmark varSun;
    private Category category;
    private JavaElementBookmark compUnitA;
    private JavaElementBookmark compUnitB;
    private JavaElementBookmark compUnitC;
    private FileBookmark fileBookmarkA;
    private FileBookmark fileBookmarkB;

    @Test
    public void testMoveFileBbeforeCompilationUnitC() {
        invoker.invoke(new ReorderBookmarksCommand(compUnitC, new IBookmarkModelComponent[] { fileBookmarkB }, true));
        assertEquals(compUnitA, category.getBookmarks().get(0));
        assertEquals(compUnitB, category.getBookmarks().get(1));
        assertEquals(fileBookmarkB, category.getBookmarks().get(2));
        assertEquals(compUnitC, category.getBookmarks().get(3));
        assertEquals(fileBookmarkA, category.getBookmarks().get(4));
    }

    @Test
    public void testMoveCompilationUnitBandCbeforeCompilationUnitA() {
        invoker.invoke(new ReorderBookmarksCommand(compUnitA, new IBookmarkModelComponent[] { compUnitB, compUnitC },
                true));
        assertEquals(compUnitB, category.getBookmarks().get(0));
        assertEquals(compUnitC, category.getBookmarks().get(1));
        assertEquals(compUnitA, category.getBookmarks().get(2));
    }

    @Test
    public void testMoveMondayWednesdayBehindSaturday() {
        invoker.invoke(new ReorderBookmarksCommand(varSat, new IBookmarkModelComponent[] { varMon, varWed }, false));
        assertEquals(type.getChildElements().get(0), varTue);
        assertEquals(type.getChildElements().get(1), varThu);
        assertEquals(type.getChildElements().get(2), varFri);
        assertEquals(type.getChildElements().get(3), varSat);
        assertEquals(type.getChildElements().get(4), varMon);
        assertEquals(type.getChildElements().get(5), varWed);
        assertEquals(type.getChildElements().get(6), varSun);
    }

    @Test
    public void testIfAllBookmarksOfALevelAreSelected() {
        invoker.invoke(new ReorderBookmarksCommand(varWed, new IBookmarkModelComponent[] { varMon, varTue, varWed,
                varThu, varFri, varSat, varSun }, true));
        assertEquals(type.getChildElements().get(0), varMon);
        assertEquals(type.getChildElements().get(1), varTue);
        assertEquals(type.getChildElements().get(2), varWed);
        assertEquals(type.getChildElements().get(3), varThu);
        assertEquals(type.getChildElements().get(4), varFri);
        assertEquals(type.getChildElements().get(5), varSat);
        assertEquals(type.getChildElements().get(6), varSun);
    }

    @Before
    public void setUp() {
        buildModel();
        buildCommandInvoker();
    }

    private void buildCommandInvoker() {
        invoker = new BookmarkCommandInvoker() {

            @Override
            public void invoke(IBookmarkModelCommand command) {
                command.execute(model, null);
            }
        };
    }

    private void buildModel() {
        createComponents();
        setRelationshipOfComponents();
        model = new BookmarkModel();
        model.add(category);
    }

    private void setRelationshipOfComponents() {
        setRelationshipOfJavaElements();
        setRelationshipOfIFile();

    }

    private void setRelationshipOfIFile() {
        fileBookmarkA.setParent(category);
        fileBookmarkB.setParent(category);
        category.add(fileBookmarkA);
        category.add(fileBookmarkB);
    }

    private void setRelationshipOfJavaElements() {
        compUnitA.setParent(category);
        compUnitB.setParent(category);
        compUnitC.setParent(category);
        category.add(compUnitA);
        category.add(compUnitB);
        category.add(compUnitC);
        type.addChildElement(varMon);
        type.addChildElement(varTue);
        type.addChildElement(varWed);
        type.addChildElement(varThu);
        type.addChildElement(varFri);
        type.addChildElement(varSat);
        type.addChildElement(varSun);
        compUnitA.addChildElement(type);
    }

    private void createComponents() {
        createJavaElements();
        createIFiles();
    }

    private void createJavaElements() {
        category = new Category("JUnit");
        compUnitA = new JavaElementBookmark("=LKJLD/src<test.project{MyEnum.java", true);
        compUnitB = new JavaElementBookmark("=LKJLD/src<test.project{B.java", false);
        compUnitC = new JavaElementBookmark("=LKJLD/src<test.project{C.java", false);
        typePath = "=LKJLD/src<test.project{MyEnum.java[MyEnum";
        type = new JavaElementBookmark(typePath, true);
        varMon = new JavaElementBookmark(typePath + "^MONDAY", false);
        varTue = new JavaElementBookmark(typePath + "^TUESDAY", false);
        varWed = new JavaElementBookmark(typePath + "^WEDNESDAY", false);
        varThu = new JavaElementBookmark(typePath + "^THURSDAY", false);
        varFri = new JavaElementBookmark(typePath + "^FRIDAY", false);
        varSat = new JavaElementBookmark(typePath + "^SATURDAY", false);
        varSun = new JavaElementBookmark(typePath + "^SUNDAY", false);

    }

    private void createIFiles() {
        IPath location = Path.fromOSString("../LKJLD/fileA.txt");
        IFile fileA = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
        fileBookmarkA = new FileBookmark(fileA, true);

        location = Path.fromOSString("../LKJLD/fileB.txt");
        IFile fileB = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
        fileBookmarkB = new FileBookmark(fileB, true);
    }

}
