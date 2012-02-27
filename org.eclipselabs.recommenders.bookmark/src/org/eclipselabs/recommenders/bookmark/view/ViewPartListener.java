package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.XMLMemento;

public class ViewPartListener implements IPartListener2 {

    private final BookmarkView bookmarkView;
    IMemento memento = XMLMemento.createWriteRoot("root");

    public ViewPartListener(BookmarkView bookmarkView) {
        this.bookmarkView = bookmarkView;
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {

    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        if (memento != null) {
            bookmarkView.saveState(memento);
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {

    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
    }

}
