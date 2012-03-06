package org.eclipselabs.recommenders.bookmark.command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.eclipselabs.recommenders.bookmark.commands.AddCategoryCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.combo.HideableComboViewer;
import org.junit.Before;
import org.junit.Test;

public class AddCategoryCommandTest {

    private BookmarkModel model;
    private Category categoryB;
    private Category categoryA;
    private BookmarkCommandInvoker commandInvoker;
    private HideableComboViewer combo;

    @Test
    public void testAddCategory() {
        commandInvoker.invoke(new AddCategoryCommand(combo));
        assertEquals(3, model.getCategories().size());
        assertEquals(categoryA, model.getCategories().get(0));
        assertEquals(categoryB, model.getCategories().get(1));
    }

    @Before
    public void setUp() {
        buildModel();
        buildInvoker();
        createComboViewerMock();
    }

    private void createComboViewerMock() {
        combo = mock(HideableComboViewer.class);
        when(combo.isVisible()).thenReturn(true);
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
        model.add(categoryA);
        model.add(categoryB);
    }

    private void createComponents() {
        categoryA = new Category("CategoryA");
        categoryB = new Category("CategoryB");
        model = new BookmarkModel();
    }
}
