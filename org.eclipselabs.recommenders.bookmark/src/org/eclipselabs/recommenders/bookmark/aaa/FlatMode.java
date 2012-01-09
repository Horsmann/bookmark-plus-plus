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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;

public class FlatMode
	extends LabelProvider
	implements ITreeContentProvider
{

	private final TreeViewer treeViewer;
	private BookmarkModel model;

	public FlatMode(final TreeViewer treeViewer)
	{
		this.treeViewer = treeViewer;
	}

	public void activate()
	{
		treeViewer.setContentProvider(this);
		treeViewer.setLabelProvider(this);
	}

	public void setModel(final BookmarkModel model)
	{
		this.model = model;
		treeViewer.setInput(this);
	}

	@Override
	public Object[] getElements(final Object inputElement)
	{
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(final Object parentElement)
	{
		if (isRoot(parentElement)) {
			return model.getCategories().toArray();
		}
		else if (parentElement instanceof Category) {
			final Category category = (Category) parentElement;
			return category.getBookmarks().toArray();
		}
		return new Object[0];
	}

	private boolean isRoot(final Object parentElement)
	{
		return parentElement == this;
	}

	@Override
	public Object getParent(final Object element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(final Object element)
	{
		if (isRoot(element)) {
			return true;
		}
		else if (element instanceof Category) {
			return ((Category) element).getBookmarks().size() > 0;
		}
		return false;
	}

	@Override
	public Image getImage(final Object element)
	{

		if (element instanceof Category) {
			AbstractUIPlugin plugin = Activator.getDefault();
			ImageRegistry registry = plugin.getImageRegistry();

			return registry.get(Activator.ICON_BOOKMARK);
		}

		if (element instanceof FileBookmark) {
			JavaElementLabelProvider jelp = new JavaElementLabelProvider(
					JavaElementLabelProvider.SHOW_RETURN_TYPE
							| JavaElementLabelProvider.SHOW_SMALL_ICONS
							| JavaElementLabelProvider.SHOW_DEFAULT);

			FileBookmark filebookmark = (FileBookmark) element;
			return jelp.getImage(filebookmark.getFile());
		}

		if (element instanceof JavaElementBookmark) {
			JavaElementBookmark javaElementBookmark = (JavaElementBookmark) element;
			IJavaElement javaElement = TreeValueConverter
					.attemptTransformationToIJavaElement(javaElementBookmark
							.getHandleId());

			JavaElementLabelProvider jelp = new JavaElementLabelProvider(
					JavaElementLabelProvider.SHOW_RETURN_TYPE
							| JavaElementLabelProvider.SHOW_SMALL_ICONS
							| JavaElementLabelProvider.SHOW_DEFAULT);

			return jelp.getImage(javaElement);

		}

		return null;
	}

	@Override
	public String getText(final Object element)
	{
		if (element instanceof Category) {
			return ((Category) element).getLabel();
		}
		else if (element instanceof FileBookmark) {
			return ((FileBookmark) element).getFile().toString();
		}
		else if (element instanceof JavaElementBookmark) {
			JavaElementBookmark javaElementBookmark = (JavaElementBookmark) element;
			IJavaElement javaElement = TreeValueConverter
					.attemptTransformationToIJavaElement(javaElementBookmark
							.getHandleId());

			JavaElementLabelProvider jelp = new JavaElementLabelProvider(
					JavaElementLabelProvider.SHOW_RETURN_TYPE
							| JavaElementLabelProvider.SHOW_SMALL_ICONS
							| JavaElementLabelProvider.SHOW_DEFAULT);

			return jelp.getText(javaElement);
		}
		throw new RuntimeException("Not implemented; Should never be reached");
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput)
	{
	}

}
