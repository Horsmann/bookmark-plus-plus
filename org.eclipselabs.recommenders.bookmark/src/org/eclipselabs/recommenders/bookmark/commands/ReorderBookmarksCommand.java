package org.eclipselabs.recommenders.bookmark.commands;

import java.util.LinkedList;
import java.util.List;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.visitor.GetValueVisitor;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;

import com.google.common.collect.Lists;

public class ReorderBookmarksCommand implements IBookmarkModelCommand {

    private final IBookmarkModelComponent target;
    private final IBookmarkModelComponent[] components;
    private BookmarkModel model;
    private final boolean isDropBeforeTarget;

    public ReorderBookmarksCommand(IBookmarkModelComponent target, IBookmarkModelComponent[] bookmarks,
            boolean isDropBeforeTarget) {
        this.target = target;
        this.components = bookmarks;
        this.isDropBeforeTarget = isDropBeforeTarget;
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    }

    @Override
    public void execute(BookmarkModel model) {
        this.model = model;
        List<IBookmarkModelComponent> newOrder = Lists.newArrayList();
        List<IBookmarkModelComponent> allSilblings = getAllSilblings();
        LinkedList<IBookmarkModelComponent> unselectedSilblings = getUnselectedSilblings(allSilblings);
        if (unselectedSilblings.isEmpty()) {
            return;
        }
        int index = moveUnselectedNodesToNewOrderUntilTargetNode(unselectedSilblings, newOrder);
        if (isDropBeforeTarget) {
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
            IsCategoryVisitor visitor = new IsCategoryVisitor();
            component.accept(visitor);
            if (visitor.isCategory()) {
                model.add((Category) component);
            }
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

        GetValueVisitor targetVisitor = new GetValueVisitor();
        target.accept(targetVisitor);
        for (IBookmarkModelComponent component : components) {
            GetValueVisitor visitor = new GetValueVisitor();
            component.accept(visitor);
            if (isValueOfDroppedAndTargetElementEqual(targetVisitor.getValue(), visitor.getValue())) {
                return new LinkedList<IBookmarkModelComponent>();
            }
            unselectedSilblings.remove(component);
        }
        return unselectedSilblings;
    }

    private boolean isValueOfDroppedAndTargetElementEqual(Object value, Object value2) {
        return value.equals(value2);
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
