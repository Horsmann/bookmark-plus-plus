package org.eclipselabs.recommenders.bookmark.command;

import static org.junit.Assert.*;

import org.eclipselabs.recommenders.bookmark.commands.ChangeBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

public class ChangeBookmarksCommandTest {

    private Category categoryA;
    private Category categoryB;
    private JavaElementBookmark compUnitA2;
    private JavaElementBookmark compUnitA1;
    private JavaElementBookmark compUnitB1;
    private JavaElementBookmark compUnitB2;
    private JavaElementBookmark compUnitB3;
    private BookmarkModel model;
    private BookmarkCommandInvoker commandInvoker;
    private JavaElementBookmark typeA11;

    @Test
    public void testMoveTypeOfCompilationUnitA1ToCatB() {
        moveTypeOfCompilationUnitA1ToCategoryB();
        assertEquals(1, categoryA.getBookmarks().size());
        assertEquals(4, categoryB.getBookmarks().size());
        assertEquals(typeA11.getHandleId(), ((JavaElementBookmark) categoryB.getBookmarks().get(3)).getChildElements()
                .get(0).getHandleId());
    }

    private void moveTypeOfCompilationUnitA1ToCategoryB() {
        Optional<IBookmarkModelComponent> dropTarget = Optional.of((IBookmarkModelComponent) categoryB);
        Optional<String> categoryName = Optional.absent();
        commandInvoker.invoke(new ChangeBookmarksCommand(new IBookmarkModelComponent[] { typeA11 }, false, commandInvoker, true,
                dropTarget, categoryName));
    }

    @Test
    public void testCopyCompilationUnitA2FromCatAToCatB() {
        copyCompilationUnitA2ToCategoryB();
        assertEquals(4, categoryB.getBookmarks().size());
        assertEquals(compUnitA2.getHandleId(), ((JavaElementBookmark) categoryB.getBookmarks().get(3)).getHandleId());
    }

    private void copyCompilationUnitA2ToCategoryB() {
        Optional<IBookmarkModelComponent> dropTarget = Optional.of((IBookmarkModelComponent) categoryB);
        Optional<String> categoryName = Optional.absent();
        commandInvoker.invoke(new ChangeBookmarksCommand(new IBookmarkModelComponent[] { compUnitA2 }, true, commandInvoker, false,
                dropTarget, categoryName));
    }

    @Test
    public void testAbortedDrag() {
        executeAbortingCommand();
        assertEquals(compUnitB1, categoryB.getBookmarks().get(0));
        assertEquals(compUnitB2, categoryB.getBookmarks().get(1));
        assertEquals(compUnitB3, categoryB.getBookmarks().get(2));
    }

    private void executeAbortingCommand() {
        Optional<IBookmarkModelComponent> dropTarget = Optional.of((IBookmarkModelComponent) compUnitB3);
        Optional<String> categoryName = Optional.absent();
        commandInvoker.invoke(new ChangeBookmarksCommand(new IBookmarkModelComponent[] { compUnitB1, compUnitB2 }, true,
                commandInvoker, false, dropTarget, categoryName));
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
                command.execute(model);
            }
        };
    }

    private void buildModel() {
        createComponents();
        setRelationship();
    }

    private void setRelationship() {
        compUnitA1.addChildElement(typeA11);
        categoryA.add(compUnitA1);
        categoryA.add(compUnitA2);
        categoryB.add(compUnitB1);
        categoryB.add(compUnitB2);
        categoryB.add(compUnitB3);
        model.add(categoryA);
        model.add(categoryB);
    }

    private void createComponents() {
        categoryA = new Category("CategoryA");
        categoryB = new Category("CategoryB");
        compUnitA1 = new JavaElementBookmark("=LKJLD/src<test.project{A1.java", true);
        typeA11 = new JavaElementBookmark("=LKJLD/src<test.project{A1.java[A1", false);
        compUnitA2 = new JavaElementBookmark("=LKJLD/src<test.project{A2.java", false);
        compUnitB1 = new JavaElementBookmark("=LKJLD/src<test.project{B1.java", false);
        compUnitB2 = new JavaElementBookmark("=LKJLD/src<test.project{B2.java", false);
        compUnitB3 = new JavaElementBookmark("=LKJLD/src<test.project{B3.java", false);
        model = new BookmarkModel();
    }

}
