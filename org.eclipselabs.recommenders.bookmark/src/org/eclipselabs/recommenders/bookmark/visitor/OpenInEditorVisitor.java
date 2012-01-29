package org.eclipselabs.recommenders.bookmark.visitor;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

public class OpenInEditorVisitor implements IModelVisitor {
    private final IWorkbenchPage activePage;
    private final boolean openChildElements;

    public OpenInEditorVisitor(IWorkbenchPage activePage, boolean openChildElements) {
        this.activePage = activePage;
        this.openChildElements = openChildElements;
    }

    @Override
    public void visit(FileBookmark fileBookmark) {
        try {

            IsResourceAvailableVisitor availVisitor = new IsResourceAvailableVisitor();
            fileBookmark.accept(availVisitor);

            if (availVisitor.isAvailable()) {
                IDE.openEditor(activePage, fileBookmark.getFile());
            }

        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Category category) {
        for (IBookmark bookmark : category.getBookmarks()) {
            bookmark.accept(this);
        }
    }

    @Override
    public void visit(JavaElementBookmark javaElementBookmark) {
        try {

            IsResourceAvailableVisitor availVisitor = new IsResourceAvailableVisitor();
            javaElementBookmark.accept(availVisitor);

            if (availVisitor.isAvailable()) {
                IEditorPart part = JavaUI.openInEditor(javaElementBookmark.getJavaElement());
                JavaUI.revealInEditor(part, javaElementBookmark.getJavaElement());
            }
        } catch (PartInitException e) {
            e.printStackTrace();
        } catch (JavaModelException e) {
            e.printStackTrace();
        }

        if (openChildElements) {
            for (JavaElementBookmark child : javaElementBookmark.getChildElements()) {
                child.accept(this);
            }
        }
    }

  

}
