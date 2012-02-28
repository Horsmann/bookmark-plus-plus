package org.eclipselabs.recommenders.bookmark.command;

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

    @Test
    public void testMoveMondayWednesdayBehindSaturday() {
        performReordering();
    }

    private void performReordering() {
        invoker.invoke(new ReorderBookmarksCommand(varSat, new IBookmarkModelComponent[] { varMon, varWed }, false));        
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
        Category category = new Category("JUnit");
        JavaElementBookmark compUnit = new JavaElementBookmark("=LKJLD/src<test.project{MyEnum.java", true);
        compUnit.setParent(category);
        typePath = "=LKJLD/src<test.project{MyEnum.java[MyEnum";
        JavaElementBookmark type = new JavaElementBookmark(typePath, true);
        varMon = new JavaElementBookmark(typePath + "^MONDAY", false);
        JavaElementBookmark varTue = new JavaElementBookmark(typePath + "^TUESDAY", false);
        varWed = new JavaElementBookmark(typePath + "^WEDNESDAY", false);
        JavaElementBookmark varThu = new JavaElementBookmark(typePath + "^THURSDAY", false);
        JavaElementBookmark varFri = new JavaElementBookmark(typePath + "^FRIDAY", false);
        varSat = new JavaElementBookmark(typePath + "^SATURDAY", false);
        JavaElementBookmark varSun = new JavaElementBookmark(typePath + "^SUNDAY", false);
        type.addChildElement(varMon);
        type.addChildElement(varTue);
        type.addChildElement(varWed);
        type.addChildElement(varThu);
        type.addChildElement(varFri);
        type.addChildElement(varSat);
        type.addChildElement(varSun);
        compUnit.addChildElement(type);

        model = new BookmarkModel();
        model.add(category);
    }

}
