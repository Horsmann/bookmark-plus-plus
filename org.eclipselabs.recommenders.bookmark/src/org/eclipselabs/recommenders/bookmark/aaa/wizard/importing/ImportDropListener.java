package org.eclipselabs.recommenders.bookmark.aaa.wizard.importing;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkTreeDropListener;
import org.eclipselabs.recommenders.bookmark.aaa.commands.ChangeElementInModleCommand;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.IsCategoryVisitor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class ImportDropListener extends BookmarkTreeDropListener {

    public ImportDropListener(BookmarkCommandInvoker commandInvoker) {
        super(commandInvoker);
    }

    @Override
    public void drop(final DropTargetEvent event) {
        final Optional<IBookmarkModelComponent> dropTarget = getDropTarget(event);

        boolean insertDropBeforeTarget = determineInsertLocation(event);

        ISelection selections = LocalSelectionTransfer.getTransfer().getSelection();
        if (selections instanceof IStructuredSelection) {
            boolean isCopyOperation = true;
            processStructuredSelection(dropTarget, (IStructuredSelection) selections, isCopyOperation,
                    insertDropBeforeTarget);
        }

    }

    protected void processDroppedElementOriginatedFromInsideTheView(Optional<IBookmarkModelComponent> dropTarget,
            IBookmarkModelComponent[] components, boolean isCopyOperation, boolean insertDropBeforeTarget) {

        if (!causeRecursion(components, dropTarget)) {

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

                commandInvoker.invoke(new ChangeElementInModleCommand(list.toArray(new IBookmarkModelComponent[0]),
                        isCopyOperation, commandInvoker, insertDropBeforeTarget, dropTarget, categoryName));
                categoryName = Optional.absent();
            }

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
}
