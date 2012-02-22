package org.eclipselabs.recommenders.bookmark.visitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.BookmarkUtil;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

public class IsResourceAvailableVisitor implements IModelVisitor {

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
        boolean exists = BookmarkUtil.doesReferencedIJavaElementExist(element);

        isAvailable = isOpen && exists;
    }

}
