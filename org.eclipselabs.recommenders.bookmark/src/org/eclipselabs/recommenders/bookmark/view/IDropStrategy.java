package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.swt.dnd.DropTargetEvent;

public interface IDropStrategy {

    public void updateOnDragEvent(DropTargetEvent event);

    public void performDrop(DropTargetEvent event);
}