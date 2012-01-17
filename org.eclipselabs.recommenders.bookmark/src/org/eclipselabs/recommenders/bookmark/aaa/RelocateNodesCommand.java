package org.eclipselabs.recommenders.bookmark.aaa;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;

import com.google.common.collect.Lists;

public class RelocateNodesCommand implements IBookmarkModelCommand {

    private final IBookmarkModelComponent target;
    private final IBookmarkModelComponent[] components;
    private final Point dropPoint;
    private boolean insertBeforeTarget = false;
    private final Tree tree;

    public RelocateNodesCommand(IBookmarkModelComponent target, IBookmarkModelComponent[] bookmarks,
            Point screenAbsolute, Tree tree) {
        this.target = target;
        this.components = bookmarks;
        this.dropPoint = screenAbsolute;
        this.tree = tree;

        insertBeforeTarget = insertBeforeDropTarget();

    }

    private boolean insertBeforeDropTarget() {

        Point controlRelativePoint = tree.toControl(dropPoint);
        TreeItem item = tree.getItem(controlRelativePoint);
        Rectangle bounds = item.getBounds();

        // divide target rectangle in upper an lower half
        Rectangle upperHalf = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height / 2);
        // Rectangle lowerHalf = new Rectangle(bounds.x, bounds.y +
        // (bounds.height / 2), bounds.width, bounds.height / 2);

        // boolean contains = upperHalf.contains(controlRelativePoint);
        // boolean contains2 = lowerHalf.contains(controlRelativePoint);

        // decide in which part drop occurred
        return upperHalf.contains(controlRelativePoint);
    }

    @Override
    public void execute(BookmarkModel model) {

        List<IBookmarkModelComponent> newOrder = Lists.newArrayList();
        List<IBookmarkModelComponent> allSilblings = getAllSilblings();
        LinkedList<IBookmarkModelComponent> unselectedSilblings = getUnselectedSilblings(allSilblings);

        int index = moveUnselectedNodesToNewOrderUntilTargetNode(unselectedSilblings, newOrder);

        if (insertBeforeTarget) {
            insertBeforeTarget(unselectedSilblings, newOrder, index);
        } else {
            insertAfterTarget(unselectedSilblings, newOrder, index);
        }

    }

    private void insertAfterTarget(LinkedList<IBookmarkModelComponent> unselectedSilblings,
            List<IBookmarkModelComponent> newOrder, int index) {

        newOrder.add(unselectedSilblings.get(index));

        for (IBookmarkModelComponent component : components) {
            newOrder.add(component);
        }

        for (int i = (index + 1); i < unselectedSilblings.size(); i++) {
            newOrder.add(unselectedSilblings.get(i));
        }

        setNewOrderForParent(newOrder);

    }

    private void setNewOrderForParent(List<IBookmarkModelComponent> newOrder) {
        IBookmarkModelComponent parent = target.getParent();
        for (IBookmarkModelComponent component : parent.getChildren()) {
            parent.remove(component);
        }
        for (IBookmarkModelComponent component : newOrder) {
            parent.add(component);
        }
    }

    private void insertBeforeTarget(LinkedList<IBookmarkModelComponent> unselectedSilblings,
            List<IBookmarkModelComponent> newOrder, int index) {
        for (IBookmarkModelComponent selected : components) {
            newOrder.add(selected);
        }

        for (int i = index; i < unselectedSilblings.size(); i++) {
            newOrder.add(unselectedSilblings.get(i));
        }

        setNewOrderForParent(newOrder);

    }

    private int moveUnselectedNodesToNewOrderUntilTargetNode(LinkedList<IBookmarkModelComponent> unselectedSilblings,
            List<IBookmarkModelComponent> newOrder) {

        int index = 0;
        for (; index < unselectedSilblings.size(); index++) {
            IBookmarkModelComponent current = unselectedSilblings.get(index);
            if (current == target) {
                break;
            }
            newOrder.add(current);
        }

        return index;
    }

    private LinkedList<IBookmarkModelComponent> getUnselectedSilblings(List<IBookmarkModelComponent> allSilblings) {
        LinkedList<IBookmarkModelComponent> unselectedSilblings = new LinkedList<IBookmarkModelComponent>(allSilblings);

        for (IBookmarkModelComponent component : components) {
            unselectedSilblings.remove(component);
        }
        return unselectedSilblings;
    }

    private List<IBookmarkModelComponent> getAllSilblings() {

        List<IBookmarkModelComponent> silblings = Lists.newArrayList();
        for (IBookmarkModelComponent component : target.getParent().getChildren()) {
            silblings.add(component);
        }

        return silblings;
    }

}
