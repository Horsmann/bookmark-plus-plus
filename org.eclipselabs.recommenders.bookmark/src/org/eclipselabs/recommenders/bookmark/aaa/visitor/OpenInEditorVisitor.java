package org.eclipselabs.recommenders.bookmark.aaa.visitor;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

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
            IDE.openEditor(activePage, fileBookmark.getFile());
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
            IEditorPart part = JavaUI.openInEditor(javaElementBookmark.getJavaElement());
            JavaUI.revealInEditor(part, javaElementBookmark.getJavaElement());
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
