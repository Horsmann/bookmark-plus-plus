package org.eclipselabs.recommenders.bookmark.aaa.commands;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

import com.google.common.collect.Lists;

public class RelocateNodesCommand implements IBookmarkModelCommand {

    private final IBookmarkModelComponent target;
    private final IBookmarkModelComponent[] components;
    private final Point dropPoint;
    private boolean insertBeforeTarget = false;
    private final Tree tree;
    private BookmarkModel model;

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

        this.model = model;

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

        setNewOrder(newOrder);

    }

    private void setNewOrder(List<IBookmarkModelComponent> newOrder) {

        if (target instanceof Category) {
            setNewOrderForCategories(newOrder);
            return;
        }

        IBookmarkModelComponent parent = target.getParent();
        RemoveChildrenVisitor removeVisitor = new RemoveChildrenVisitor();
        parent.accept(removeVisitor);

        AddListAsChildrenVisitor addVisitor = new AddListAsChildrenVisitor(newOrder);
        parent.accept(addVisitor);
    }

    private void setNewOrderForCategories(List<IBookmarkModelComponent> newOrder) {

        model.removeAll();

        for (IBookmarkModelComponent component : newOrder) {
            model.add((Category) component);
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

        setNewOrder(newOrder);

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

        if (target instanceof Category) {
            return new LinkedList<IBookmarkModelComponent>(model.getCategories());
        }

        GetSilblingsVisitor visitor = new GetSilblingsVisitor();
        IBookmarkModelComponent parent = target.getParent();
        parent.accept(visitor);
        return visitor.getSilblings();
    }

    private class RemoveChildrenVisitor implements IModelVisitor {

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {
            IBookmark[] bookmarks = category.getBookmarks().toArray(new IBookmark[0]);
            for (IBookmark bookmark : bookmarks) {
                category.remove(bookmark);
            }
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            JavaElementBookmark[] childElements = javaElementBookmark.getChildElements().toArray(
                    new JavaElementBookmark[0]);
            for (JavaElementBookmark child : childElements) {
                javaElementBookmark.remove(child);
            }
        }

    }

    private class AddListAsChildrenVisitor implements IModelVisitor {

        private final List<IBookmarkModelComponent> newOrder;

        public AddListAsChildrenVisitor(List<IBookmarkModelComponent> newOrder) {
            this.newOrder = newOrder;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {
            for (IBookmarkModelComponent component : newOrder) {
                if (component instanceof IBookmark) {
                    category.add((IBookmark) component);
                }
            }
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            for (IBookmarkModelComponent component : newOrder) {
                if (component instanceof JavaElementBookmark) {
                    javaElementBookmark.addChildElement((JavaElementBookmark) component);
                }
            }
        }

    }

    private class GetSilblingsVisitor implements IModelVisitor {

        private List<IBookmarkModelComponent> silblings = Lists.newArrayList();

        public List<IBookmarkModelComponent> getSilblings() {
            return silblings;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {
            silblings.addAll(category.getBookmarks());
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            silblings.addAll(javaElementBookmark.getChildElements());
        }

    }
}
