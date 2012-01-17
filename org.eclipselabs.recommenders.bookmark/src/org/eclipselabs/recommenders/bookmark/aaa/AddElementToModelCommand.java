/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 */
package org.eclipselabs.recommenders.bookmark.aaa;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class AddElementToModelCommand implements IBookmarkModelCommand {

    private final Optional<IBookmarkModelComponent> dropTarget;
    private final Object[] elements;
    private BookmarkModel model;
    private Category category;
    private Tree tree;
    private Point point;
    private final BookmarkCommandInvoker commandInvoker;

    public AddElementToModelCommand(final Optional<IBookmarkModelComponent> dropTarget, final Object[] elements,
            Tree tree, Point point, BookmarkCommandInvoker commandInvoker) {
        this.dropTarget = dropTarget;
        this.elements = elements;
        this.tree = tree;
        this.point = point;
        this.commandInvoker = commandInvoker;
    }

    @Override
    public void execute(final BookmarkModel model) {
        this.model = model;
        this.category = findCategory();

        List<IBookmarkModelComponent> createdElements = Lists.newLinkedList();

        for (Object element : elements) {

            if (element instanceof IJavaElement) {
                Optional<JavaElementBookmark> created = processJavaElement((IJavaElement) element);
                if (created.isPresent()) {
                    createdElements.add(created.get());
                }
            } else if (element instanceof IFile) {
                createdElements.add(processFile((IFile) element));
            }
        }

        sortInIfDropAndTargetShareSameParent(createdElements);

    }

    private void sortInIfDropAndTargetShareSameParent(List<IBookmarkModelComponent> createdElements) {
        if (dropTarget.isPresent()) {
            for (IBookmarkModelComponent component : createdElements) {
                if (hasSameParentAsTarget(component)) {
                    commandInvoker.invoke(new RelocateNodesCommand(dropTarget.get(),
                            new IBookmarkModelComponent[] { component }, point, tree));
                }
            }
        }
    }

    private boolean hasSameParentAsTarget(IBookmarkModelComponent component) {

        return component.getParent() == dropTarget.get().getParent();
    }

    private Category findCategory() {
        if (dropTarget.isPresent()) {
            final IBookmarkModelComponent target = dropTarget.get();

            return getCategoryOf(target);
        } else {
            Category category = new Category("New Category");
            model.add(category);
            return category;
        }
    }

    private Category getCategoryOf(IBookmarkModelComponent component) {

        if (component instanceof Category) {
            return (Category) component;
        }

        return getCategoryOf(component.getParent());
    }

    private FileBookmark processFile(final IFile file) {
        return new FileBookmark(file, category);
    }

    private Optional<JavaElementBookmark> processJavaElement(final IJavaElement javaElement) {

        Optional<JavaElementBookmark> created = Optional.absent();

        if (isBookmarkable(javaElement)) {
            JavaElementBookmark createJavaElementBookmark = createJavaElementBookmark(javaElement);
            createJavaElementBookmark.setInferred(false);
            created = Optional.of(createJavaElementBookmark);
        }

        return created;
    }

    private JavaElementBookmark createJavaElementBookmark(IJavaElement javaElement) {
        IJavaElement parent = javaElement.getParent();
        if (isBookmarkable(parent)) {
            JavaElementBookmark bookmarkParent = createJavaElementBookmark(parent);
            FindJavaElementHandleVisitor visitor = new FindJavaElementHandleVisitor(javaElement.getHandleIdentifier());
            bookmarkParent.accept(visitor);
            if (visitor.getFoundElement().isPresent()) {
                JavaElementBookmark foundBookmark = visitor.getFoundElement().get();
                foundBookmark.setExpanded(true);
                return foundBookmark;
            } else {
                JavaElementBookmark newBookmark = new JavaElementBookmark(javaElement.getHandleIdentifier(), true,
                        bookmarkParent);
                newBookmark.setExpanded(true);
                // bookmarkParent.addChildElement(newBookmark);

                return newBookmark;
            }

        } else {
            FindJavaElementHandleVisitor visitor = new FindJavaElementHandleVisitor(javaElement.getHandleIdentifier());
            category.accept(visitor);
            if (visitor.getFoundElement().isPresent()) {
                JavaElementBookmark foundBookmark = visitor.getFoundElement().get();
                foundBookmark.setExpanded(true);
                return foundBookmark;
            } else {

                JavaElementBookmark newBookmark = new JavaElementBookmark(javaElement.getHandleIdentifier(), true,
                        category);
                newBookmark.setExpanded(true);
                // category.add(newBookmark);

                return newBookmark;
            }
        }

    }

    private boolean isBookmarkable(Object value) {
        return (value instanceof ICompilationUnit) || isValueInTypeHierarchyBelowICompilationUnit(value);
    }

    private boolean isValueInTypeHierarchyBelowICompilationUnit(Object value) {
        return value instanceof IMethod || value instanceof IType || value instanceof IField
                || value instanceof IImportDeclaration || value instanceof IImportContainer
                || value instanceof IPackageDeclaration;
    }

    private class FindJavaElementHandleVisitor implements IModelVisitor {

        private final String javaElementHandleId;
        private Optional<JavaElementBookmark> foundJavaElement = Optional.absent();

        public FindJavaElementHandleVisitor(String javaElementHandleId) {
            this.javaElementHandleId = javaElementHandleId;

        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {
            for (IBookmark bookmark : category.getBookmarks()) {
                bookmark.accept(this);
            }
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            if (javaElementBookmark.getHandleId().equals(javaElementHandleId)) {
                foundJavaElement = Optional.of(javaElementBookmark);
            } else {
                for (JavaElementBookmark child : javaElementBookmark.getChildElements()) {
                    child.accept(this);
                }
            }

        }

        public Optional<JavaElementBookmark> getFoundElement() {
            return foundJavaElement;
        }

    }
}
