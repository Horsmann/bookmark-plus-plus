package org.eclipselabs.recommenders.bookmark.aaa.wizard.importing;

import org.eclipselabs.recommenders.bookmark.aaa.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.aaa.commands.AddElementToModelCommand;
import org.eclipselabs.recommenders.bookmark.aaa.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.HierarchyValueVisitor;

import com.google.common.base.Optional;

public class BookmarkModelCloner implements BookmarkCommandInvoker {

    private final BookmarkModel master;
    private BookmarkModel cloned;

    public BookmarkModelCloner(final BookmarkModel master) {
        this.master = master;
        cloned = new BookmarkModel();
    }

    public BookmarkModel clone() {

        for (Category cat : master.getCategories()) {
            HierarchyValueVisitor valueVisitor = new HierarchyValueVisitor();
            cat.accept(valueVisitor);

            Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
            Optional<String> label = Optional.of(cat.getLabel());
            invoke(new AddElementToModelCommand(dropTarget, valueVisitor.getValues().toArray(), label, this, false));
        }

        return cloned;
    }

    @Override
    public void invoke(IBookmarkModelCommand command) {
        command.execute(cloned);
    }

}
