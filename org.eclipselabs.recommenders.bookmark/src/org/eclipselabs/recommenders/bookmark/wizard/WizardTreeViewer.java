package org.eclipselabs.recommenders.bookmark.wizard;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.commands.BookmarkDeletionCommand;
import org.eclipselabs.recommenders.bookmark.commands.OpenBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.IChildrenResolverVisitor;
import org.eclipselabs.recommenders.bookmark.view.tree.IImageProviderVisitor;
import org.eclipselabs.recommenders.bookmark.view.tree.ILabelProviderVisitor;
import org.eclipselabs.recommenders.bookmark.view.tree.IRepresentationMode;
import org.eclipselabs.recommenders.bookmark.visitor.GetValueVisitor;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;

public class WizardTreeViewer {

    private IRepresentationMode currentMode;
    private TreeViewer treeViewer;
    private BookmarkModel model;
    private ISelectionChangedListener setSelectionChangedListener;
    private KeyListener treeKeyListener;

    public WizardTreeViewer(final Composite parent, final IRepresentationMode initialMode, final BookmarkModel model) {
        this.model = model;

        createTreeViewer(parent);
        currentMode = initialMode;
    }

    public void enabledActionsForViewPart(ViewPart part, BookmarkCommandInvoker commandInvoker) {
        addKeyListener(part, commandInvoker);
    }

    private void createTreeViewer(Composite parent) {
        this.treeViewer = new TreeViewer(parent);
        treeViewer.getTree().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        treeViewer.setContentProvider(new SwitchableContentProvider());
        treeViewer.setLabelProvider(new SwitchableLabelProvider());
    }

    public void overrideSelectionChangedListener(ISelectionChangedListener selection) {
        if (setSelectionChangedListener != null) {
            treeViewer.removeSelectionChangedListener(setSelectionChangedListener);
        }
        treeViewer.addSelectionChangedListener(selection);
    }

    public void renameCategory() {

        IBookmarkModelComponent component = (IBookmarkModelComponent) getSelections().getFirstElement();
        if (!isRenameValid(component)) {
            return;
        }
        String oldLabel = getOldCategoryLabel(component);

        InputDialog dialog = makeDialog(oldLabel);
        dialog.setBlockOnOpen(true);
        if (dialog.open() == Window.OK) {
            updateCategoryName(component, dialog);
            treeViewer.refresh();
        }
    }

    private void updateCategoryName(IBookmarkModelComponent component, InputDialog dialog) {
        String value = dialog.getValue();
        SetNewCategoryNameVisitor categoryVisitor = new SetNewCategoryNameVisitor(value);
        component.accept(categoryVisitor);
    }

    private String getOldCategoryLabel(IBookmarkModelComponent component) {
        GetValueVisitor valueVisitor = new GetValueVisitor();
        component.accept(valueVisitor);
        return (String) valueVisitor.getValue();
    }

    private boolean isRenameValid(IBookmarkModelComponent component) {
        if (getSelections().size() != 1) {
            return false;
        }

        IsCategoryVisitor isCategoryVisitor = new IsCategoryVisitor();
        component.accept(isCategoryVisitor);
        return isCategoryVisitor.isCategory();
    }

    private InputDialog makeDialog(String oldLabel) {
        return new InputDialog(treeViewer.getTree().getShell(), "Rename Category", "Enter a new category name:",
                oldLabel, new InputValidator());
    }

    private void addKeyListener(ViewPart part, BookmarkCommandInvoker commandInvoker) {
        treeKeyListener = new TreeKeyListener(part, commandInvoker);
        treeViewer.getTree().addKeyListener(treeKeyListener);
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
        if (model == null) {
            return;
        }
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

        private final ViewPart part;
        private final BookmarkCommandInvoker commandInvoker;

        public TreeKeyListener(ViewPart part, BookmarkCommandInvoker commandInvoker) {
            this.part = part;
            this.commandInvoker = commandInvoker;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (isDeletion(e)) {
                processDeletion(e);
            } else if (isRename(e)) {
                processRename(e);
            } else if (isEnter(e)) {
                processEnter(e);
            }

            treeViewer.refresh();
        }

        private void processEnter(KeyEvent e) {
            commandInvoker.invoke(new OpenBookmarkCommand(getSelections(), part.getSite().getWorkbenchWindow()
                    .getActivePage(), commandInvoker));
        }

        private boolean isEnter(KeyEvent e) {
            return e.keyCode == SWT.CR;
        }

        private void processRename(KeyEvent e) {
            renameCategory();
        }

        private boolean isRename(KeyEvent e) {
            return e.keyCode == SWT.F2;
        }

        private void processDeletion(KeyEvent e) {
            TreeItem[] selections = getTreeSelections(e);
            for (TreeItem item : selections) {
                searchBookmarkDeleteSelection(item);
            }
        }

        private TreeItem[] getTreeSelections(KeyEvent e) {
            Tree tree = (Tree) e.getSource();
            TreeItem[] selections = tree.getSelection();
            return selections;
        }

        private void searchBookmarkDeleteSelection(TreeItem item) {
            if (item.isDisposed()) {
                // happens if a parent node of the current one was delete before
                // than the current child is already
                // disposed
                return;
            }
            IBookmarkModelComponent component = (IBookmarkModelComponent) item.getData();
            commandInvoker.invoke(new BookmarkDeletionCommand(component));
        }

        private boolean isDeletion(KeyEvent e) {
            int BACKSPACE = 8;
            return (e.keyCode == SWT.DEL || e.keyCode == BACKSPACE);
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

    }

    private class InputValidator implements IInputValidator {

        @Override
        public String isValid(String newText) {
            if (newText.equals("")) {
                return "Blank is an invalid category name";
            }
            return null;
        }

    }

    private class SetNewCategoryNameVisitor implements IModelVisitor {

        private final String categoryName;

        public SetNewCategoryNameVisitor(String categoryName) {
            this.categoryName = categoryName;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {
            category.setLabel(categoryName);
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
        }

    }

    public void setTreeLayoutData(GridData data) {
        treeViewer.getTree().setLayoutData(data);
    }

    public void overrideKeyListener(KeyListener keyListener) {
        if (treeKeyListener != null) {
            treeViewer.getTree().removeKeyListener((KeyListener) treeKeyListener);
        }
        treeViewer.getTree().addKeyListener(keyListener);

    }

}
