package org.eclipselabs.recommenders.bookmark.wizard;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

public class TreeSelectionDependendButtonEnabler implements ISelectionChangedListener {

    private final RepresentationSwitchableTreeViewer treeViewer;
    private final Button button;

    public TreeSelectionDependendButtonEnabler(RepresentationSwitchableTreeViewer treeViewer, Button button) {
        this.treeViewer = treeViewer;
        this.button = button;
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        if (treeViewer.getSelections().size() == 0) {
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
        }
    }
}
