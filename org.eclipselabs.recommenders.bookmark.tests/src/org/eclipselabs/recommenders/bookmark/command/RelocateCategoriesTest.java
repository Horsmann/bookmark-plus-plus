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

public class RelocateCategoriesTest {

    private BookmarkModel model;
    private BookmarkCommandInvoker invoker;
    private JavaElementBookmark compUnitA;
    private JavaElementBookmark compUnitB;
    private JavaElementBookmark compUnitC;
    private Category categoryA;
    private Category categoryB;
    private Category categoryC;

    @Test
    public void testReordering() {
        moveCompUnitAafterCompuUnitC();
        assertEquals(categoryB, model.getCategories().get(0));
        assertEquals(categoryC, model.getCategories().get(1));
        assertEquals(categoryA, model.getCategories().get(2));
    }

    private void moveCompUnitAafterCompuUnitC() {
        invoker.invoke(new ReorderBookmarksCommand(categoryC, new IBookmarkModelComponent[] { categoryA }, false));
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
        model.add(categoryA);
        model.add(categoryB);
        model.add(categoryC);
    }

    private void setRelationshipOfComponents() {
        compUnitA.setParent(categoryA);
        compUnitB.setParent(categoryB);
        compUnitC.setParent(categoryC);
        categoryA.add(compUnitA);
        categoryB.add(compUnitB);
        categoryC.add(compUnitC);

    }

    private void createComponents() {
        categoryA = new Category("A");
        categoryB = new Category("B");
        categoryC = new Category("C");
        compUnitA = new JavaElementBookmark("=LKJLD/src<test.project{AAA.java", true);
        compUnitB = new JavaElementBookmark("=LKJLD/src<test.project{BBB.java", true);
        compUnitC = new JavaElementBookmark("=LKJLD/src<test.project{CCC.java", true);
    }

}
