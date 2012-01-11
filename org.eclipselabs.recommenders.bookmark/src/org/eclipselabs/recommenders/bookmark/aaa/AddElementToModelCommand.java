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

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

import com.google.common.base.Optional;

public class AddElementToModelCommand
	implements IBookmarkModelCommand
{

	private final Optional<IBookmarkModelComponent> dropTarget;
	private final Object element;
	private BookmarkModel model;
	private Category category;

	public AddElementToModelCommand(
			final Optional<IBookmarkModelComponent> dropTarget,
			final Object element)
	{
		this.dropTarget = dropTarget;
		this.element = element;
	}

	@Override
	public void execute(final BookmarkModel model)
	{
		this.model = model;
		this.category = findCategory();

		if (element instanceof IJavaElement) {
			processJavaElement((IJavaElement) element);
		}
		else if (element instanceof IFile) {
			processFile((IFile) element);
		}
	}

	private Category findCategory()
	{
		if (dropTarget.isPresent()) {
			final IBookmarkModelComponent dropTargetComponent = dropTarget
					.get();
			for (final Category category : model.getCategories()) {
				if (category == dropTargetComponent) {
					return category;
				}
				else {
					final FindComponentModelVisitor visitor = new FindComponentModelVisitor(
							dropTargetComponent);
					dropTargetComponent.accept(visitor);
					if (visitor.foundComponent()) {
						return category;
					}
				}
			}
			throw new IllegalStateException(
					"Should not be reachable. Dropped on an element not existing in model.");
		}
		else {
			return new Category("New Category");
		}
	}

	private void processFile(final IFile file)
	{
		category.add(new FileBookmark(file));
	}

	private void processJavaElement(final IJavaElement javaElement)
	{

		if (isBookmarkable(javaElement)) {
			createJavaElementBookmark(javaElement);
		}

	}

	private JavaElementBookmark createJavaElementBookmark(IJavaElement javaElement)
	{
		IJavaElement parent = javaElement.getParent();
		if (isBookmarkable(parent)) {
			JavaElementBookmark bookmarkParent = createJavaElementBookmark(parent);
			FindJavaElementHandleVisitor visitor = new FindJavaElementHandleVisitor(
					javaElement.getHandleIdentifier());
			bookmarkParent.accept(visitor);
			if (visitor.getFoundElement().isPresent()) {
				return visitor.getFoundElement().get();
			}
			else {
				JavaElementBookmark newBookmark = new JavaElementBookmark(
						javaElement.getHandleIdentifier(), true);
				bookmarkParent.addChildElement(newBookmark);

				return newBookmark;
			}

		}
		else {
			FindJavaElementHandleVisitor visitor = new FindJavaElementHandleVisitor(
					javaElement.getHandleIdentifier());
			category.accept(visitor);
			if (visitor.getFoundElement().isPresent()) {
				return visitor.getFoundElement().get();
			}
			else {

				JavaElementBookmark newBookmark = new JavaElementBookmark(
						javaElement.getHandleIdentifier(), true);
				category.add(newBookmark);

				return newBookmark;
			}
		}

	}

	private boolean isBookmarkable(Object value)
	{
		return (value instanceof ICompilationUnit)
				|| isValueInTypeHierarchyBelowICompilationUnit(value);
	}

	private boolean isValueInTypeHierarchyBelowICompilationUnit(Object value)
	{
		return value instanceof IMethod || value instanceof IType
				|| value instanceof IField
				|| value instanceof IImportDeclaration
				|| value instanceof IImportContainer
				|| value instanceof IPackageDeclaration;
	}

	private class FindComponentModelVisitor
		implements IModelVisitor
	{

		private boolean foundComponent;
		private final IBookmarkModelComponent dropTargetComponent;

		public FindComponentModelVisitor(
				final IBookmarkModelComponent dropTargetComponent)
		{
			this.dropTargetComponent = dropTargetComponent;
		}

		public boolean foundComponent()
		{
			return foundComponent;
		}

		@Override
		public void visit(final FileBookmark fileBookmark)
		{
			if (fileBookmark == dropTargetComponent) {
				foundComponent = true;
			}
		}

		@Override
		public void visit(final Category category)
		{
		}

		@Override
		public void visit(final JavaElementBookmark javaElementBookmark)
		{
			if (javaElementBookmark == dropTargetComponent) {
				foundComponent = true;
			}
			else {
				for (final JavaElementBookmark childElement : javaElementBookmark
						.getChildElements()) {
					childElement.accept(this);
				}
			}
		}

	}

	private class FindJavaElementHandleVisitor
		implements IModelVisitor
	{

		private final String javaElementHandleId;
		private Optional<JavaElementBookmark> foundJavaElement = Optional
				.absent();

		public FindJavaElementHandleVisitor(String javaElementHandleId)
		{
			this.javaElementHandleId = javaElementHandleId;

		}

		@Override
		public void visit(FileBookmark fileBookmark)
		{
		}

		@Override
		public void visit(Category category)
		{
			for (IBookmark bookmark : category.getBookmarks()) {
				bookmark.accept(this);
			}
		}

		@Override
		public void visit(JavaElementBookmark javaElementBookmark)
		{
			if (javaElementBookmark.getHandleId().equals(javaElementHandleId)) {
				foundJavaElement = Optional.of(javaElementBookmark);
			}else{
				for (JavaElementBookmark child: javaElementBookmark.getChildElements()){
					child.accept(this);
				}
			}
			
		}

		public Optional<JavaElementBookmark> getFoundElement()
		{
			return foundJavaElement;
		}

	}
}
