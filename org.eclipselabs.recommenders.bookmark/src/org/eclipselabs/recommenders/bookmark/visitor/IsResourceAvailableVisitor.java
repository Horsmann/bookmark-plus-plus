package org.eclipselabs.recommenders.bookmark.visitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
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

        if (element instanceof IPackageDeclaration) {
            IPackageDeclaration pack = (IPackageDeclaration) element;
            return pack.exists();
        }
        
        if (element instanceof IImportDeclaration) {
            IImportDeclaration impDec = (IImportDeclaration) element;
            return impDec.exists();
        }

        if (element instanceof ICompilationUnit) {
            IJavaElement unit = (ICompilationUnit) element;
            return unit.exists();
        }
        return false;
    }

}
