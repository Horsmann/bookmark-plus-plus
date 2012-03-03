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
package org.eclipselabs.recommenders.bookmark.commands;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.BookmarkUtil;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.DefaultDropStrategy.ExternalFile;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class AddBookmarksCommand implements IBookmarkModelCommand {

    private final Object[] elements;
    private BookmarkModel model;
    private Category category;
    private final BookmarkCommandInvoker commandInvoker;
    private final Optional<IBookmarkModelComponent> dropTarget;
    private final Optional<String> nameForNewCategory;
    private final boolean isDropBeforeTarget;
    private boolean newBookmarkCreated = false;

    public AddBookmarksCommand(final Object[] elements, BookmarkCommandInvoker commandInvoker,
            boolean isDropBeforeTarget, final Optional<IBookmarkModelComponent> dropTarget,
            Optional<String> nameForNewCategory) {
        this.dropTarget = dropTarget;
        this.elements = elements;
        this.nameForNewCategory = nameForNewCategory;
        this.commandInvoker = commandInvoker;
        this.isDropBeforeTarget = isDropBeforeTarget;
    }

    @Override
    public void execute(final BookmarkModel model) {
        Category category = findCategory();
        execute(model, category);
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        this.category = category;
        this.model = model;
        List<IBookmarkModelComponent> createdElements = Lists.newLinkedList();

        Object[] inversedOrderElements = inverseElementOrder();

        for (Object element : inversedOrderElements) {
            if (isIJavaElement(element)) {
                Optional<JavaElementBookmark> created = processJavaElement((IJavaElement) element);
                if (created.isPresent()) {
                    createdElements.add(created.get());
                }
            } else if (element instanceof IFile) {
                Optional<FileBookmark> file = processFile((IFile) element);
                if (file.isPresent()) {
                    createdElements.add(file.get());
                }
            } else if (element instanceof ExternalFile) {
                Optional<FileBookmark> file = processExternalFile(((ExternalFile) element).file);
                if (file.isPresent()) {
                    createdElements.add(file.get());
                }
            }
        }

        if (createdElements.size() > 0) {
            addCategoryToModel();
            sortInIfDropAndTargetShareSameParent(createdElements);
        }
    }

    private Optional<FileBookmark> processExternalFile(IFile file) {
        Optional<FileBookmark> processFile = processFile(file);
        if (processFile.isPresent()) {
            processFile.get().setLocatedInWorkspace(false);
        }
        return processFile;
    }

    private Object[] inverseElementOrder() {
        List<Object> inverse = Lists.newArrayList();
        for (int i = elements.length - 1; i >= 0; i--) {
            inverse.add(elements[i]);
        }

        return inverse.toArray();
    }

    private boolean isIJavaElement(Object element) {
        return (element instanceof IJavaElement && BookmarkUtil.isInternalElement((IJavaElement) element));
    }

    private void addCategoryToModel() {
        for (Category cat : model.getCategories()) {
            if (cat == category) {
                return;
            }
        }
        model.add(category);
    }

    private void sortInIfDropAndTargetShareSameParent(List<IBookmarkModelComponent> createdElements) {
        if (dropTarget.isPresent()) {
            for (IBookmarkModelComponent component : createdElements) {
                if (hasSameParentAsTarget(component)) {
                    commandInvoker.invoke(new ReorderBookmarksCommand(dropTarget.get(),
                            new IBookmarkModelComponent[] { component }, isDropBeforeTarget));
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
            Category category = null;
            if (nameForNewCategory.isPresent()) {
                category = new Category(nameForNewCategory.get());
            } else {
                category = new Category("New Category");
            }
            return category;
        }
    }

    private Category getCategoryOf(IBookmarkModelComponent component) {

        if (component instanceof Category) {
            return (Category) component;
        }

        return getCategoryOf(component.getParent());
    }

    private Optional<FileBookmark> processFile(final IFile file) {
        DoesIFileAlreadyExistsVisitor visitor = new DoesIFileAlreadyExistsVisitor(
                FileBookmark.getRelativeFilePath(file));
        category.accept(visitor);
        if (visitor.doesAlreadyExists()) {
            return Optional.absent();
        }
        return Optional.of(new FileBookmark(file, category));
    }

    private Optional<JavaElementBookmark> processJavaElement(final IJavaElement javaElement) {
        Optional<JavaElementBookmark> created = Optional.absent();
        if (BookmarkUtil.isBookmarkable(javaElement)) {
            JavaElementBookmark createBookmark = createJavaElementBookmark(javaElement);
            if (newBookmarkCreated) {
                createBookmark.setInferred(false);
                created = Optional.of(createBookmark);
                newBookmarkCreated = false;
            }
        }
        return created;
    }

    private JavaElementBookmark createJavaElementBookmark(IJavaElement javaElement) {
        IJavaElement parent = javaElement.getParent();
        if (BookmarkUtil.isBookmarkable(parent)) {
            JavaElementBookmark bookmarkParent = createJavaElementBookmark(parent);
            return searchJavaElementBelowBookmarkParent(bookmarkParent, javaElement);
        } else {
            return searchJavaElementInCategory(javaElement);
        }

    }

    private JavaElementBookmark searchJavaElementBelowBookmarkParent(JavaElementBookmark bookmarkParent,
            IJavaElement javaElement) {
        FindJavaElementHandleVisitor visitor = new FindJavaElementHandleVisitor(javaElement.getHandleIdentifier());
        bookmarkParent.accept(visitor);
        if (visitor.getFoundElement().isPresent()) {
            return getFoundElement(visitor);
        }
        return createNewJavaElementBookmarkWithExistingParent(bookmarkParent, javaElement);
    }

    private JavaElementBookmark createNewJavaElementBookmarkWithExistingParent(JavaElementBookmark bookmarkParent,
            IJavaElement javaElement) {
        JavaElementBookmark newBookmark = new JavaElementBookmark(javaElement.getHandleIdentifier(), true,
                bookmarkParent);
        newBookmark.setExpanded(true);
        newBookmarkCreated = true;
        return newBookmark;
    }

    private JavaElementBookmark searchJavaElementInCategory(IJavaElement javaElement) {
        FindJavaElementHandleVisitor visitor = new FindJavaElementHandleVisitor(javaElement.getHandleIdentifier());
        category.accept(visitor);
        if (visitor.getFoundElement().isPresent()) {
            return getFoundElement(visitor);
        }
        return createNewJavaElementBookmarkWithCategoryAsParent(javaElement);
    }

    private JavaElementBookmark getFoundElement(FindJavaElementHandleVisitor visitor) {
        JavaElementBookmark foundBookmark = visitor.getFoundElement().get();
        foundBookmark.setExpanded(true);
        return foundBookmark;
    }

    private JavaElementBookmark createNewJavaElementBookmarkWithCategoryAsParent(IJavaElement javaElement) {
        JavaElementBookmark newBookmark = new JavaElementBookmark(javaElement.getHandleIdentifier(), true, category);
        newBookmark.setExpanded(true);
        newBookmarkCreated = true;
        return newBookmark;
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

    public class DoesIFileAlreadyExistsVisitor implements IModelVisitor {

        private boolean exists = false;
        private final String fileId;

        public DoesIFileAlreadyExistsVisitor(String fileId) {
            this.fileId = fileId;
        }

        public boolean doesAlreadyExists() {
            return exists;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
            if (FileBookmark.getRelativeFilePath(fileBookmark.getFile()).equals(fileId)) {
                exists = true;
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
        }

    }

}
