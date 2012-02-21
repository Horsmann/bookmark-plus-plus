package org.eclipselabs.recommenders.bookmark.wizard;

import java.util.LinkedList;
import java.util.List;

import org.eclipselabs.recommenders.bookmark.commands.ChangeElementCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class ImportSelectedBookmarksCommand implements IBookmarkModelCommand {

    private final IBookmarkModelComponent[] components;
    private final BookmarkCommandInvoker invoker;
    private final boolean isCopyOperation;
    private final boolean insertDropBeforeTarget;
    private final Optional<IBookmarkModelComponent> dropTarget;

    public ImportSelectedBookmarksCommand(IBookmarkModelComponent[] components, BookmarkCommandInvoker invoker,
            boolean isCopyOperation, boolean insertDropBeforeTarget, Optional<IBookmarkModelComponent> dropTarget) {
        this.components = components;
        this.invoker = invoker;
        this.dropTarget = dropTarget;
        this.isCopyOperation = isCopyOperation;
        this.insertDropBeforeTarget = insertDropBeforeTarget;

    }

    @Override
    public void execute(BookmarkModel model) {
        LinkedList<List<IBookmarkModelComponent>> splitByCategory = splitByCategory(components);

        Optional<String> categoryName = Optional.absent();

        for (List<IBookmarkModelComponent> list : splitByCategory) {
            if (!list.isEmpty()) {
                IBookmarkModelComponent category = getCategory(list.get(0));
                IsCategoryVisitor visitor = new IsCategoryVisitor();
                category.accept(visitor);
                if (visitor.isCategory()) {
                    categoryName = Optional.of(((Category) category).getLabel());
                }
            }

            invoker.invoke(new ChangeElementCommand(list.toArray(new IBookmarkModelComponent[0]),
                    isCopyOperation, invoker, insertDropBeforeTarget, dropTarget, categoryName));
            categoryName = Optional.absent();
        }
    }

    private LinkedList<List<IBookmarkModelComponent>> splitByCategory(IBookmarkModelComponent[] components) {

        LinkedList<IBookmarkModelComponent> pool = new LinkedList<IBookmarkModelComponent>();
        for (IBookmarkModelComponent component : components) {
            pool.add(component);
        }

        LinkedList<List<IBookmarkModelComponent>> split = new LinkedList<List<IBookmarkModelComponent>>();
        while (!pool.isEmpty()) {
            IBookmarkModelComponent poll = pool.poll();
            IBookmarkModelComponent cat = getCategory(poll);
            List<IBookmarkModelComponent> catSet = Lists.newArrayList();
            catSet.add(poll);
            List<IBookmarkModelComponent> belongToOtherCat = Lists.newArrayList();
            while (!pool.isEmpty()) {
                IBookmarkModelComponent remain = pool.poll();
                if (cat == getCategory(remain)) {
                    catSet.add(remain);
                } else {
                    belongToOtherCat.add(remain);
                }
            }
            split.add(catSet);
            pool.addAll(belongToOtherCat);
        }

        return split;
    }

    private IBookmarkModelComponent getCategory(IBookmarkModelComponent poll) {
        while (poll.hasParent()) {
            poll = poll.getParent();
        }

        return poll;
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    }

}
