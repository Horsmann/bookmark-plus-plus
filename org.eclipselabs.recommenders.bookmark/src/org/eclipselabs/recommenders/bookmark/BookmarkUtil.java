package org.eclipselabs.recommenders.bookmark;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;

public class BookmarkUtil {

    // external would be stuff for instance from .class files
    public static boolean isInternalElement(IJavaElement element) {
        return true;
    }

    public static boolean isBookmarkable(Object value) {

        return value instanceof IMethod || value instanceof IType || value instanceof IField
                || value instanceof IImportDeclaration || value instanceof IImportContainer
                || value instanceof IPackageDeclaration || value instanceof ICompilationUnit
                || value instanceof IClassFile;
    }

    public static boolean doesReferencedIJavaElementExist(IJavaElement element) {
        return element.exists();
    }

}
