package org.eclipselabs.recommenders.bookmark.command;

import static org.junit.Assert.assertEquals;

import org.eclipselabs.recommenders.bookmark.commands.DeleteSingleBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.junit.Before;
import org.junit.Test;

public class DeleteSingleBookmarkCommandTest {

    private BookmarkModel model;
    private Category categoryB;
    private Category categoryA;
    private JavaElementBookmark compilationUnit;
    private BookmarkCommandInvoker commandInvoker;

    @Test
    public void testDeleltionOfCategory() {
        commandInvoker.invoke(new DeleteSingleBookmarkCommand(categoryB, commandInvoker));
        assertEquals(1, model.getCategories().size());
        assertEquals(categoryA, model.getCategories().get(0));
    }

    @Test
    public void testDeleltionOfBookmark() {
        commandInvoker.invoke(new DeleteSingleBookmarkCommand(compilationUnit, commandInvoker));
        assertEquals(2, model.getCategories().size());
        assertEquals(categoryA, model.getCategories().get(0));
        assertEquals(0, model.getCategories().get(0).getBookmarks().size());
        assertEquals(categoryB, model.getCategories().get(1));
    }

    @Before
    public void setUp() {
        buildModel();
        buildInvoker();
    }

    private void buildInvoker() {
        commandInvoker = new BookmarkCommandInvoker() {

            @Override
            public void invoke(IBookmarkModelCommand command) {
                command.execute(model, null);
            }
        };
    }

    private void buildModel() {
        createComponents();
        setRelationship();
    }

    private void setRelationship() {
        categoryA.add(compilationUnit);
        model.add(categoryA);
        model.add(categoryB);
    }

    private void createComponents() {
        categoryA = new Category("CategoryA");
        compilationUnit = new JavaElementBookmark("=LKJLD/src<test.project{A.java", false);
        categoryB = new Category("CategoryB");
        model = new BookmarkModel();
    }

}
