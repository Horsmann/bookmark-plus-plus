package org.eclipselabs.recommenders.bookmark.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipselabs.recommenders.bookmark.commands.DeleteInferredBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.DeleteSingleBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.junit.Before;
import org.junit.Test;

public class DeleteInferredBookmarksCommandTest {

    private BookmarkCommandInvoker invoker;
    private BookmarkModel model;
    private Category category;
    private FileBookmark fileBookmarkA;
    private JavaElementBookmark compUnitA;
    private JavaElementBookmark type;
    private JavaElementBookmark varMon;
    private String typePath;

    @Test
    public void testDeletionWithBookmarkedNodesAsChildren() {
        compUnitA.setInferred(true);
        invoker.invoke(new DeleteInferredBookmarksCommand(compUnitA));
        assertEquals(varMon, ((JavaElementBookmark) model.getCategories().get(0).getBookmarks().get(0))
                .getChildElements().get(0).getChildElements().get(0));
    }

    @Test
    public void testDeletionOfLeaf() {
        deleteTheBookmarkedElementAndTriggerRecursiveDeletion();
        assertEquals(1, model.getCategories().get(0).getBookmarks().size());
        assertTrue(model.getCategories().get(0).getBookmarks().get(0) instanceof FileBookmark);
    }

    @Test
    public void testDeletionOfLeafWitherOtherBookmarkedElementsInHierarchy() {
        compUnitA.setInferred(false);
        deleteTheBookmarkedElementAndTriggerRecursiveDeletion();
        assertEquals(2, model.getCategories().get(0).getBookmarks().size());
        assertTrue(model.getCategories().get(0).getBookmarks().get(0) instanceof JavaElementBookmark);
    }

    private void deleteTheBookmarkedElementAndTriggerRecursiveDeletion() {
        invoker.invoke(new DeleteSingleBookmarkCommand(varMon));
        invoker.invoke(new DeleteInferredBookmarksCommand(type));
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
        category.add(fileBookmarkA);
    }

    private void setRelationshipOfJavaElements() {
        compUnitA.setParent(category);
        category.add(compUnitA);
        type.addChildElement(varMon);
        compUnitA.addChildElement(type);
    }

    private void createComponents() {
        createJavaElements();
        createIFiles();
    }

    private void createJavaElements() {
        category = new Category("JUnit");
        compUnitA = new JavaElementBookmark("=LKJLD/src<test.project{MyEnum.java", true);
        typePath = "=LKJLD/src<test.project{MyEnum.java[MyEnum";
        type = new JavaElementBookmark(typePath, true);
        varMon = new JavaElementBookmark(typePath + "^MONDAY", false);

    }

    private void createIFiles() {
        IPath location = Path.fromOSString("../LKJLD/fileA.txt");
        IFile fileA = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
        fileBookmarkA = new FileBookmark(fileA, true);
    }

}
