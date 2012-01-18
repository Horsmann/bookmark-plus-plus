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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.RemoveBookmarkModelComponentVisitor;

public class RepresentationSwitchableTreeViewer {

    private IRepresentationMode currentMode;
    private TreeViewer treeViewer;
    private BookmarkModel model;

    public RepresentationSwitchableTreeViewer(final Composite parent, final IRepresentationMode initialMode,
            final BookmarkModel model) {
        this.model = model;

        createTreeViewer(parent);

        currentMode = initialMode;
        treeViewer.setContentProvider(new SwitchableContentProvider());
        treeViewer.setLabelProvider(new SwitchableLabelProvider());
        treeViewer.addTreeListener(new TreeViewListener());

        addKeyListener();
    }

    private void createTreeViewer(Composite parent) {
        this.treeViewer = new TreeViewer(parent);
        configureTreeViewer();
    }

    private void configureTreeViewer() {
        ColumnViewerEditorActivationStrategy columnStrategy = new ColumnViewerEditorActivationStrategy(treeViewer);
        columnStrategy.setEnableEditorActivationWithKeyboard(true);
        TreeViewerEditor.create(treeViewer, columnStrategy, ColumnViewerEditor.DEFAULT);

        treeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(treeViewer.getTree()) });
        treeViewer.setColumnProperties(new String[] { "1" });
        treeViewer.setCellModifier(new ICellModifier() {

            @Override
            public void modify(Object element, String property, Object value) {
                if (isSupportedType(element)) {
                    IBookmarkModelComponent component = (IBookmarkModelComponent) ((TreeItem) element).getData();
                    if (value instanceof String) {
                        SetStringValueVisitor visitor = new SetStringValueVisitor((String) value);
                        component.accept(visitor);
                    }
                    return;
                }
                throw new IllegalArgumentException("Type of element is not supported - should not have been reached");
            }

            private boolean isSupportedType(Object element) {
                return (element instanceof TreeItem && ((TreeItem) element).getData() instanceof IBookmarkModelComponent);
            }

            @Override
            public Object getValue(Object element, String property) {

                if (element instanceof IBookmarkModelComponent) {
                    GetStringRepresentationVisitor getLabelVisitor = new GetStringRepresentationVisitor();
                    ((IBookmarkModelComponent) element).accept(getLabelVisitor);
                    return getLabelVisitor.getLable();
                }

                throw new IllegalArgumentException("Type of element is not supported - should not have been reached");
            }

            @Override
            public boolean canModify(Object element, String property) {

                if (element instanceof Category) {
                    return true;
                }
                return false;
            }
        });

        TreeViewerFocusCellManager focusCellManager = new TreeViewerFocusCellManager(treeViewer,
                new FocusCellOwnerDrawHighlighter(treeViewer));
        ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(treeViewer) {
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                return (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED)
                        || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }
        };

        TreeViewerEditor.create(treeViewer, focusCellManager, actSupport, ColumnViewerEditor.TABBING_VERTICAL);
    }

    public void doit() {
        IStructuredSelection selections = getSelections();
        if (selections.size() == 1) {
            treeViewer.editElement(selections.getFirstElement(), 0);
        }
    }

    private void addKeyListener() {
        treeViewer.getTree().addKeyListener(new TreeKeyListener());
    }

    public void setRepresentation(final IRepresentationMode mode) {
        currentMode = mode;
        treeViewer.refresh();
        updateExpansions(model);
    }

    public void setInput(final BookmarkModel model) {
        this.model = model;
        treeViewer.setInput(model);
        updateExpansions(model);
    }

    private void updateExpansions(final BookmarkModel model) {
        treeViewer.setExpandedElements(currentMode.getExpandedItems(model));
    }

    public void addDropSupport(final int operations, final Transfer[] transferTypes,
            final DropTargetListener dropListener) {
        treeViewer.addDropSupport(operations, transferTypes, dropListener);
    }

    public void addDragSupport(final int operations, final Transfer[] transferTypes,
            final DragSourceListener dragListener) {
        treeViewer.addDragSupport(operations, transferTypes, dragListener);
    }

    public IStructuredSelection getSelections() {

        return (IStructuredSelection) treeViewer.getSelection();
    }

    public void refresh() {
        treeViewer.refresh();
        updateExpansions(model);
    }

    private class TreeViewListener implements ITreeViewerListener {

        @Override
        public void treeCollapsed(final TreeExpansionEvent event) {
            final ITreeExpansionVisitor visitor = currentMode
                    .createTreeExpansionVisitor(ITreeExpansionVisitor.Action.COLLAPSE);
            acceptVisitor(event.getElement(), visitor);
        }

        @Override
        public void treeExpanded(final TreeExpansionEvent event) {
            final ITreeExpansionVisitor visitor = currentMode
                    .createTreeExpansionVisitor(ITreeExpansionVisitor.Action.EXPAND);
            acceptVisitor(event.getElement(), visitor);
        }

    }

    private class SwitchableContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            if (inputElement == null) {
                return new Object[0];
            }

            if (inputElement instanceof BookmarkModel) {
                final BookmarkModel model = (BookmarkModel) inputElement;
                final List<Category> categories = model.getCategories();
                return categories.toArray();
            } else {
                throw new IllegalStateException(String.format("Input element of TreeViewer is not of type '%s'.",
                        BookmarkModel.class.toString()));
            }
        }

        @Override
        public Object[] getChildren(final Object parentElement) {
            final IChildrenResolverVisitor visitor = currentMode.createChildrenResolverVisitor();
            acceptVisitor(parentElement, visitor);
            return visitor.getChildren().toArray();
        }

        @Override
        public Object getParent(final Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(final Object element) {
            final IChildrenResolverVisitor visitor = currentMode.createChildrenResolverVisitor();
            acceptVisitor(element, visitor);
            return visitor.hasChildren();
        }
    }

    private class SwitchableLabelProvider extends LabelProvider {
        @Override
        public String getText(final Object element) {
            final ILabelProviderVisitor visitor = currentMode.createLabelProviderVisitor();
            acceptVisitor(element, visitor);
            return visitor.getLabel();
        }

        @Override
        public Image getImage(final Object element) {
            final IImageProviderVisitor visitor = currentMode.createImageProviderVisitor();
            acceptVisitor(element, visitor);
            return visitor.getImage();
        }
    }

    private static void acceptVisitor(final Object element, final IModelVisitor visitor) {
        if (element instanceof IBookmarkModelComponent) {
            ((IBookmarkModelComponent) element).accept(visitor);
        } else {
            throw new RuntimeException("Should not be reachable. Visitor can only be used on IBookmarkModelComponents.");
        }
    }

    private class TreeKeyListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {

            if (isDeletion(e)) {
                processDeletion(e);
            }

            treeViewer.refresh();
        }

        private void processDeletion(KeyEvent e) {

            TreeItem[] selections = getSelections(e);

            for (TreeItem item : selections) {
                searchBookmarkDeleteSelection(item);
            }
        }

        private TreeItem[] getSelections(KeyEvent e) {
            Tree tree = (Tree) e.getSource();
            TreeItem[] selections = tree.getSelection();
            return selections;
        }

        private void searchBookmarkDeleteSelection(TreeItem item) {

            IBookmarkModelComponent selection = (IBookmarkModelComponent) item.getData();

            deletionForCategories(selection);

            deletionForIBookmarks(selection);
        }

        private void deletionForIBookmarks(IBookmarkModelComponent selection) {
            RemoveBookmarkModelComponentVisitor visitor = new RemoveBookmarkModelComponentVisitor(selection);

            for (Category category : model.getCategories()) {
                category.accept(visitor);
            }
        }

        private void deletionForCategories(IBookmarkModelComponent selection) {
            model.remove(selection);
        }

        private boolean isDeletion(KeyEvent e) {
            int BACKSPACE = 8;
            return (e.keyCode == SWT.DEL || e.keyCode == BACKSPACE);
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

    }

    public Tree getTree() {
        return treeViewer.getTree();
    }

    public TreeViewer getViewer() {
        return treeViewer;
    }

    private class GetStringRepresentationVisitor implements IModelVisitor {

        private String stringValue;

        public String getLable() {
            return stringValue;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
            stringValue = fileBookmark.getFile().toString();
        }

        @Override
        public void visit(Category category) {
            stringValue = category.getLabel();
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            stringValue = javaElementBookmark.getHandleId();
        }

    }

    private class SetStringValueVisitor implements IModelVisitor {
        private final String value;

        public SetStringValueVisitor(String value) {
            this.value = value;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {
            category.setLabel(value);
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
        }
    }

}
