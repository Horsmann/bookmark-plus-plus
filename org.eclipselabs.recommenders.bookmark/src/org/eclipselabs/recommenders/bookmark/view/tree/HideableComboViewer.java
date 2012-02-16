package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;

public class HideableComboViewer extends Composite {

    private ComboViewer combo;
    private Button saveButton;
    private final Composite parent;
    private final BookmarkModel model;

    public HideableComboViewer(Composite parent, int style, BookmarkModel model) {
        super(parent, style);
        this.parent = parent;
        this.model = model;
        setLayout();
        addCategoryIcon();
        addComboViewer();
        addSaveButton();
        addComboViewerListener();
        hide();
    }

    private void setLayout() {
        setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
    }

    private void addComboViewerListener() {
        combo.getCombo().addModifyListener(new ComboModifyListener(saveButton));
        combo.getCombo().addSelectionListener(new ComboSelectionListener());
    }

    private void addSaveButton() {
        saveButton = new Button(this, SWT.NONE);
        saveButton.setImage(Activator.getDefault().getImageRegistry().get(Activator.ICON_SAVE));
        saveButton.addListener(SWT.MouseDown, new ComboSaveButtonListener());
        GridData gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        saveButton.setLayoutData(gridData);
        saveButton.setEnabled(false);
    }

    private void addComboViewer() {
        combo = new ComboViewer(this, SWT.NONE);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        combo.getCombo().setLayoutData(gridData);
        combo.setContentProvider(new ComboContentProvider());
    }

    private void addCategoryIcon() {
        Label label = new Label(this, SWT.NONE);
        label.setImage(Activator.getDefault().getImageRegistry().get(Activator.ICON_CATEGORY));
        GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        label.setLayoutData(gridData);
    }

    public int getIndex() {
        return combo.getCombo().getSelectionIndex();
    }

    public void hide() {
        setVisible(false);
        setLayoutData(new GridData(0, 0));
        parent.layout(true, true);
    }

    public void show() {
        setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        setVisible(true);
        parent.layout(true, true);
    }

    private class ComboContentProvider implements IStructuredContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public Object[] getElements(Object inputElement) {

            return new Object[] { ((Category) inputElement).getLabel() };
        }

    }

    private class ComboSaveButtonListener implements Listener {

        @Override
        public void handleEvent(Event event) {
            System.out.println("SaveButton Implementierung fehlt noch");
        }

    }

    public class ComboModifyListener implements ModifyListener {

        private final Button button;

        public ComboModifyListener(Button saveBookmarkNameChanges) {
            this.button = saveBookmarkNameChanges;
        }

        @Override
        public void modifyText(ModifyEvent e) {
            Combo combo = (Combo) e.getSource();
            int index = combo.getSelectionIndex();
            if (index < 0) {
                return;
            }
            String text = combo.getText();
            String label = model.getCategories().get(index).getLabel();

            System.out.println("Modify-Listener noch nicht implementiert");

            if (areDifferent(text, label) && textIsNotBlank(text)) {
                button.setEnabled(true);
            } else {
                button.setEnabled(false);
            }
        }

        private boolean textIsNotBlank(String text) {
            return text.trim().compareTo("") != 0;
        }

        private boolean areDifferent(String text, String headName) {
            return text.compareTo(headName) != 0;
        }

    }

    private class ComboSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            System.out.println("ComboSelectionListener noch nicht implementiert");
            // ExpandedStorage storage = manager.getExpandedStorage();
            // TreeModel model = manager.getModel();
            //
            // storage.removeCurrentlyVisibleNodes();
            // storage.addCurrentlyExpandedNodes();
            //
            // int index = combo.getSelectionIndex();
            // BMNode[] bookmarks = model.getModelRoot().getChildren();
            //
            // model.setHeadNode(bookmarks[index]);
            // TreeViewer viewer = manager.getActiveBookmarkView().getView();
            //
            // viewer.setInput(null);
            // viewer.setInput(model.getModelHead());
            // storage.expandStoredNodesForActiveView();
            //
            // if (manager.isViewFlattened()) {
            // manager.activateFlattenedModus(bookmarks[index]);
            // }
            //
            // saveBookmarkNameChanges.setEnabled(false);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    public void selectIndex(int index) {
        combo.getCombo().select(index);
    }

    public void setNewSelections(List<Category> categories) {
        combo.getCombo().removeAll();
        for (Category category : categories) {
            combo.add(category.getLabel());
        }
    }
}
