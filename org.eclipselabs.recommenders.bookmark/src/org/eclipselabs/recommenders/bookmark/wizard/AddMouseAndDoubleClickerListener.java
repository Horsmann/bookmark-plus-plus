package org.eclipselabs.recommenders.bookmark.wizard;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class AddMouseAndDoubleClickerListener implements MouseListener, IDoubleClickListener {

    private final RepresentationSwitchableTreeViewer leftTreeViewer;
    private final RepresentationSwitchableTreeViewer rightTreeViewer;
    private final BookmarkCommandInvoker invoker;

    public AddMouseAndDoubleClickerListener(RepresentationSwitchableTreeViewer leftTreeViewer,
            RepresentationSwitchableTreeViewer rightTreeViewer, BookmarkCommandInvoker invoker) {
        this.leftTreeViewer = leftTreeViewer;
        this.rightTreeViewer = rightTreeViewer;
        this.invoker = invoker;

    }

    @Override
    public void doubleClick(DoubleClickEvent event) {
        addSelections();
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
    }

    @Override
    public void mouseDown(MouseEvent e) {
        addSelections();
    }

    @Override
    public void mouseUp(MouseEvent e) {
    }

    private void addSelections() {
        IBookmarkModelComponent[] components = getSelectedBookmarkComponents();

        if (rightTreeViewer.getSelections().size() == 0) {
            Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
            invoker.invoke(new ImportSelectedBookmarksCommand(components, invoker, true, false, dropTarget));
        } else {
            Optional<IBookmarkModelComponent> dropTarget = Optional.of((IBookmarkModelComponent) rightTreeViewer
                    .getSelections().getFirstElement());
            invoker.invoke(new ImportSelectedBookmarksCommand(components, invoker, true, false, dropTarget));
        }
    }

    private IBookmarkModelComponent[] getSelectedBookmarkComponents() {
        IStructuredSelection selections = leftTreeViewer.getSelections();
        List<IBookmarkModelComponent> components = Lists.newArrayList();
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();
        while (iterator.hasNext()) {
            components.add((IBookmarkModelComponent) iterator.next());
        }
        return components.toArray(new IBookmarkModelComponent[0]);
    }

}
