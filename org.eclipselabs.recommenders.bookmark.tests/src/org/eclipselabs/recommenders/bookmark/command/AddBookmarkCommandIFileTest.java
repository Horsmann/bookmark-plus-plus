package org.eclipselabs.recommenders.bookmark.command;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipselabs.recommenders.bookmark.commands.AddBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

public class AddBookmarkCommandIFileTest {

    BookmarkCommandInvoker invoker;
    BookmarkModel model;
    private Category category;
    String relativePath = "../LKJLD/testfile.txt";

    @Test
    public void testInsertFile() {
        insertFileToModel();
        FileBookmark target = getElementInModelAtTargetPosition();
        assertEquals(relativePath, FileBookmark.getPath(target.getFile()));
    }

    @Test
    public void testInsertOfAlreadyExistingFile() {
        insertFileToModel();
        insertFileToModel();
        FileBookmark target = getElementInModelAtTargetPosition();
        assertEquals(relativePath, FileBookmark.getPath(target.getFile()));
        assertEquals(1, category.getBookmarks().size());
    }

    private FileBookmark getElementInModelAtTargetPosition() {
        return ((FileBookmark) model.getCategories().get(0).getBookmarks().get(0));
    }

    private void insertFileToModel() {

        IPath location = Path.fromOSString("../LKJLD/testfile.txt");
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
        Optional<String> name = Optional.absent();
        Optional<IBookmarkModelComponent> dropTarget = Optional.of((IBookmarkModelComponent) category);
        invoker.invoke(new AddBookmarksCommand(new Object[] { file }, invoker, true, dropTarget, name));
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
        category = new Category("JUnit");
        model = new BookmarkModel();
        model.add(category);
        return model;
    }
}
