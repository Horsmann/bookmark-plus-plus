package org.eclipselabs.recommenders.bookmark.visitor;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
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
        if (fileBookmark.isInWorkspace()) {
            IFile file = fileBookmark.getFile();
            IProject project = file.getProject();
            boolean isOpen = project.isOpen();
            boolean exists = file.exists();
            isAvailable = isOpen && exists;
        } else {
            File file = fileBookmark.getFile().getFullPath().toFile();
            isAvailable = file.exists();
        }
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
        boolean exists = element.exists();
        isAvailable = isOpen && exists;
    }

}
