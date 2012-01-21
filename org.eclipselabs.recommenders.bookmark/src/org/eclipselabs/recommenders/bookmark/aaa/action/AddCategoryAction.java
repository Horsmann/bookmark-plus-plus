package org.eclipselabs.recommenders.bookmark.aaa.action;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.aaa.commands.AddCategoryCommand;

public class AddCategoryAction extends Action{
    
    private final BookmarkCommandInvoker invoker;

    public AddCategoryAction(BookmarkCommandInvoker invoker) {
        this.invoker = invoker;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry()
                .getDescriptor(Activator.ICON_CATEGORY));
        this.setToolTipText("Add new category");
        this.setText("New Category");
    }

    @Override
    public void run() {
        invoker.invoke(new AddCategoryCommand());
    }
}
