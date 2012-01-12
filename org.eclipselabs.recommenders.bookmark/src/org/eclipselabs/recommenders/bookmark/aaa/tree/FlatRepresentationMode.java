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
package org.eclipselabs.recommenders.bookmark.aaa.tree;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.tree.ITreeExpansionVisitor.Action;

import com.google.common.collect.Lists;

public class FlatRepresentationMode implements IRepresentationMode {

    @Override
    public IChildrenResolverVisitor createChildrenResolverVisitor() {
        return new IChildrenResolverVisitor() {

            private List<IBookmark> childBookmarks;

            @Override
            public boolean hasChildren() {
                return !childBookmarks.isEmpty();
            }

            @Override
            public List<? extends IBookmark> getChildren() {
                return childBookmarks;
            }

            @Override
            public void visit(final FileBookmark fileBookmark) {
            }

            @Override
            public void visit(final Category category) {
                childBookmarks = Lists.newLinkedList();
                for (final IBookmark bookmark : category.getBookmarks()) {
                    bookmark.accept(this);
                }
            }

            @Override
            public void visit(final JavaElementBookmark javaElementBookmark) {
                if (childBookmarks == null) {
                    childBookmarks = Lists.newLinkedList();
                } else if (!javaElementBookmark.isInferredNode()) {
                    childBookmarks.add(javaElementBookmark);
                }
                visitChilds(javaElementBookmark);
            }

            private void visitChilds(final JavaElementBookmark javaElementBookmark) {
                for (final JavaElementBookmark childElement : javaElementBookmark.getChildElements()) {
                    childElement.accept(this);
                }
            }

        };
    }

    @Override
    public ILabelProviderVisitor createLabelProviderVisitor() {
        return new ILabelProviderVisitor() {

            private String label;

            private final JavaElementLabelProvider jelp = new JavaElementLabelProvider(
                    JavaElementLabelProvider.SHOW_RETURN_TYPE | JavaElementLabelProvider.SHOW_SMALL_ICONS
                            | JavaElementLabelProvider.SHOW_DEFAULT);

            @Override
            public String getLabel() {
                return label;
            }

            @Override
            public void visit(final FileBookmark fileBookmark) {
                label = fileBookmark.getFile().toString();

            }

            @Override
            public void visit(final Category category) {
                label = category.getLabel();

            }

            @Override
            public void visit(final JavaElementBookmark javaElementBookmark) {
                final IJavaElement javaElement = JavaCore.create(javaElementBookmark.getHandleId());

                label = jelp.getText(javaElement);
            }

        };
    }

    @Override
    public ITreeExpansionVisitor createTreeExpansionVisitor(final Action expansion) {
        return new ITreeExpansionVisitor() {

            @Override
            public void visit(final FileBookmark fileBookmark) {

            }

            @Override
            public void visit(final Category category) {
                category.setExpanded(expansion == Action.EXPAND);
            }

            @Override
            public void visit(final JavaElementBookmark javaElementBookmark) {

            }

        };
    }

    @Override
    public Object[] getExpandedItems(final BookmarkModel model) {
        final List<Object> expandedItems = Lists.newLinkedList();
        final IModelVisitor visitor = new IModelVisitor() {

            @Override
            public void visit(final Category category) {
                if (category.isExpanded()) {
                    expandedItems.add(category);
                }
                for (final IBookmark bookmark : category.getBookmarks()) {
                    bookmark.accept(this);
                }
            }

            @Override
            public void visit(final FileBookmark fileBookmark) {

            }

            @Override
            public void visit(final JavaElementBookmark javaElementBookmark) {

            }
        };
        for (final Category category : model.getCategories()) {
            visitor.visit(category);
        }
        return expandedItems.toArray();
    }

    @Override
    public IImageProviderVisitor createImageProviderVisitor() {
        return new IImageProviderVisitor() {
            private Image image;

            private final JavaElementLabelProvider jelp = new JavaElementLabelProvider(
                    JavaElementLabelProvider.SHOW_RETURN_TYPE | JavaElementLabelProvider.SHOW_SMALL_ICONS
                            | JavaElementLabelProvider.SHOW_DEFAULT);

            @Override
            public void visit(JavaElementBookmark javaElementBookmark) {
                final IJavaElement javaElement = JavaCore.create(javaElementBookmark.getHandleId());

                image = jelp.getImage(javaElement);
            }

            @Override
            public void visit(Category category) {
                final AbstractUIPlugin plugin = Activator.getDefault();
                final ImageRegistry registry = plugin.getImageRegistry();
                image = registry.get(Activator.ICON_BOOKMARK);
            }

            @Override
            public void visit(FileBookmark fileBookmark) {

                image = jelp.getImage(fileBookmark.getFile());

            }

            @Override
            public Image getImage() {
                return image;
            }

        };
    }

}
