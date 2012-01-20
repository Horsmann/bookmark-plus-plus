package org.eclipselabs.recommenders.bookmark.aaa.visitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
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

    private class IsResourceAvailableVisitor implements IModelVisitor {

        private boolean isAvailable = false;

        public boolean isAvailable() {
            return isAvailable;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
            IFile file = fileBookmark.getFile();
            IProject project = file.getProject();
            boolean isOpen = project.isOpen();
            boolean exists = file.exists();

            isAvailable = isOpen && exists;
        }

        @Override
        public void visit(Category category) {
            isAvailable = true;
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            IJavaElement element = javaElementBookmark.getJavaElement();
            IProject project = element.getJavaProject().getProject();
            boolean isOpen = project.isOpen();
            boolean exists = doesReferencedIJavaElementExist(element);

            isAvailable = isOpen && exists;
        }

        private boolean doesReferencedIJavaElementExist(IJavaElement element) {
            if (element instanceof IField) {
                IField field = (IField) element;
                return field.exists();
            }

            if (element instanceof IMethod) {
                IMethod method = (IMethod) element;
                return method.exists();
            }

            if (element instanceof IType) {
                IType type = (IType) element;
                return type.exists();
            }

            if (element instanceof ICompilationUnit) {
                IJavaElement unit = (ICompilationUnit) element;
                return unit.exists();
            }
            return false;
        }

    }

}
