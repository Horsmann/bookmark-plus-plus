package org.eclipselabs.recommenders.bookmark.command;

import static org.junit.Assert.*;

import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.commands.ReorderBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
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
    private JavaElementBookmark compUnit;

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
                command.execute(model);
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
        compUnit.setParent(category);
        category.add(compUnit);
        type.addChildElement(varMon);
        type.addChildElement(varTue);
        type.addChildElement(varWed);
        type.addChildElement(varThu);
        type.addChildElement(varFri);
        type.addChildElement(varSat);
        type.addChildElement(varSun);
        compUnit.addChildElement(type);

    }

    private void createComponents() {
        category = new Category("JUnit");
        compUnit = new JavaElementBookmark("=LKJLD/src<test.project{MyEnum.java", true);
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

}
