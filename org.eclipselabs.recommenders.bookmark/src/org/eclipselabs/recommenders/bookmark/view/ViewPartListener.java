package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipselabs.recommenders.bookmark.Activator;

public class ViewPartListener implements IPartListener2 {

    private final BookmarkView bookmarkView;

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
        GuiStateIO.writeGuiStateToMemento(Activator.getLocationForStoringGUIState(), bookmarkView);
        BookmarkIO.writeModel(Activator.getLocationForStoringBookmark(), Activator.getModel());
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
