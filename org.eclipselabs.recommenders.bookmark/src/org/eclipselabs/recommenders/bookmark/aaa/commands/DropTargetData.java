package org.eclipselabs.recommenders.bookmark.aaa.commands;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;

public class DropTargetData {
    
    private final Tree tree;
    private final Point point;
    private final IBookmarkModelComponent component;

    public DropTargetData(IBookmarkModelComponent iBookmarkModelComponent, Tree tree, Point point){
        this.component = iBookmarkModelComponent;
        this.tree = tree;
        this.point = point;
    }

    public Tree getTree() {
        return tree;
    }

    public Point getPoint() {
        return point;
    }

    public IBookmarkModelComponent getComponent() {
        return component;
    }

}
