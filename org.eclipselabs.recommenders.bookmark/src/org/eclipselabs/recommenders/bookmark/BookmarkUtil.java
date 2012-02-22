package org.eclipselabs.recommenders.bookmark;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;

public class BookmarkUtil {

    //external would be stuff for instance from .class files
    public static boolean isInternalElement(IJavaElement element) {
        return element.getResource() != null;
    }
    
    public static boolean isBookmarkable(Object value) {
        return (value instanceof ICompilationUnit) || isValueInTypeHierarchyBelowICompilationUnit(value);
    }

    private static boolean isValueInTypeHierarchyBelowICompilationUnit(Object value) {
        return value instanceof IMethod || value instanceof IType || value instanceof IField
                || value instanceof IImportDeclaration || value instanceof IImportContainer
                || value instanceof IPackageDeclaration;
    }
    
    public static boolean doesReferencedIJavaElementExist(IJavaElement element) {
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
