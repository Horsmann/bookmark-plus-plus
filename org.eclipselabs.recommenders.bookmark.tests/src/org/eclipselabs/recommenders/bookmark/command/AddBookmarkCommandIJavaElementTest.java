package org.eclipselabs.recommenders.bookmark.command;

import static org.junit.Assert.*;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipselabs.recommenders.bookmark.commands.AddBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

public class AddBookmarkCommandIJavaElementTest {

    BookmarkCommandInvoker invoker;
    JavaElementBookmark dropTarget;
    BookmarkModel model;

    @Test
    public void testInsertWithAbsentDropTargetAndProvidedCategoryName() {
        String categoryName = "NEWNAME";
        insertElementToModelAbsentDropTarget(categoryName);

        assertEquals(2, model.getCategories().size());
        assertEquals(categoryName, model.getCategories().get(1).getLabel());
        assertEquals(1, model.getCategories().get(0).getBookmarks().size());
        assertEquals(1, ((JavaElementBookmark) model.getCategories().get(1).getBookmarks().get(0)).getChildElements()
                .size());
        assertEquals(1, ((JavaElementBookmark) model.getCategories().get(1).getBookmarks().get(0)).getChildElements()
                .get(0).getChildElements().size());
    }

    private void insertElementToModelAbsentDropTarget(String categoryName) {
        String handleId = "=LKJLD/src<test.project{XXX.java[XXX^ZZZZZ";
        IJavaElement javaElement = JavaCore.create(handleId);
        Optional<String> name = Optional.absent();
        if (categoryName != null) {
            name = Optional.of(categoryName);
        }
        Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
        invoker.invoke(new AddBookmarksCommand(new Object[] { javaElement }, invoker, true, dropTarget, name));
    }

    @Test
    public void testInsertNewJavaElement() {
        String handleId = "=LKJLD/src<test.project{XXX.java[XXX^ZZZZZ";
        insertElementToModel(handleId);
        JavaElementBookmark target = ((JavaElementBookmark) model.getCategories().get(0).getBookmarks().get(1))
                .getChildElements().get(0).getChildElements().get(0);
        assertEquals(handleId, target.getHandleId());
        assertFalse(target.isInferredNode());
        assertTrue(((IBookmark) target.getParent()).isInferredNode());
        assertTrue(((IBookmark) target.getParent().getParent()).isInferredNode());

    }

    @Test
    public void testInsertNewJavaElementIntoExistingStructure() {
        String handleId = "=LKJLD/src<test.project{MyEnum.java[MyEnum^SATURDAY";
        insertElementToModel(handleId);
        JavaElementBookmark target = ((JavaElementBookmark) model.getCategories().get(0).getBookmarks().get(0))
                .getChildElements().get(0).getChildElements().get(0);
        assertEquals(handleId, target.getHandleId());
        assertFalse(target.isInferredNode());
        assertTrue(((IBookmark) target.getParent()).isInferredNode());
        assertTrue(((IBookmark) target.getParent().getParent()).isInferredNode());

    }

    @Test
    public void testInsertOfAlreadyExistingJavaElement() {
        String handleId = "=LKJLD/src<test.project{MyEnum.java[MyEnum";
        insertElementToModel(handleId);
        JavaElementBookmark target = ((JavaElementBookmark) model.getCategories().get(0).getBookmarks().get(0))
                .getChildElements().get(0);
        assertEquals(handleId, target.getHandleId());
        assertTrue(target.isInferredNode());
    }

    private void insertElementToModel(String handleId) {
        IJavaElement javaElement = JavaCore.create(handleId);
        Optional<String> name = Optional.absent();
        invoker.invoke(new AddBookmarksCommand(new Object[] { javaElement }, invoker, true, Optional
                .of((IBookmarkModelComponent) dropTarget), name));
    }

    @Before
    public void setUp() {
        model = buildModel();
        initCommandInvoker();

    }

    private void initCommandInvoker() {
        invoker = new BookmarkCommandInvoker() {

            @Override
            public void invoke(IBookmarkModelCommand command) {
                command.execute(model);
            }
        };
    }

    private BookmarkModel buildModel() {
        Category category = new Category("JUnit");
        String compUnitPath = "=LKJLD/src<test.project{MyEnum.java";
        JavaElementBookmark compUnit = new JavaElementBookmark(compUnitPath, true);
        compUnit.setParent(category);
        JavaElementBookmark type = new JavaElementBookmark(compUnitPath + "[MyEnum", true);
        JavaElementBookmark variable = new JavaElementBookmark(compUnitPath + "[MyEnum^FRIDAY", false);
        dropTarget = variable;
        JavaElementBookmark method = new JavaElementBookmark(compUnitPath + "[MyEnum~printMessage", false);

        type.addChildElement(variable);
        type.addChildElement(method);
        compUnit.addChildElement(type);
        category.add(compUnit);
        model = new BookmarkModel();
        model.add(category);
        return model;
    }
}
