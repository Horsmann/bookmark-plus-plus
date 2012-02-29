package org.eclipselabs.recommenders.bookmark.command;

import static org.junit.Assert.*;

import org.eclipselabs.recommenders.bookmark.commands.DeleteAllBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.junit.Before;
import org.junit.Test;

public class DeleteAllBookmarksCommandTest {
    private BookmarkCommandInvoker invoker;
    private BookmarkModel model;
    private Category category;
    private JavaElementBookmark compUnit;
    private JavaElementBookmark type;
    private JavaElementBookmark varMon;
    private JavaElementBookmark varTue;
    private JavaElementBookmark varWed;
    private JavaElementBookmark varThu;
    private JavaElementBookmark varFri;
    private JavaElementBookmark varSat;
    private JavaElementBookmark varSun;
    
    @Test
    public void testeDeleteAll() {
        invoker.invoke(new DeleteAllBookmarksCommand());
        assertEquals(0, model.getCategories().size());
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
        String typePath = "=LKJLD/src<test.project{MyEnum.java[MyEnum";
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
