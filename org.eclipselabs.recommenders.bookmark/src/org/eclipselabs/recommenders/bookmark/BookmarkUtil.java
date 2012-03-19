package org.eclipselabs.recommenders.bookmark;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;

public class BookmarkUtil {

    public static boolean isBookmarkable(Object value) {

        return value instanceof IMethod || value instanceof IType || value instanceof IField
                || value instanceof IImportDeclaration || value instanceof IImportContainer
                || value instanceof IPackageDeclaration || value instanceof ICompilationUnit
                || value instanceof IClassFile;
    }

}
