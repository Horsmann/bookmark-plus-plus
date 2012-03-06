package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.AddCategoryCommand;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.combo.HideableComboViewer;

public class AddCategoryAction extends Action{
    
    private final BookmarkCommandInvoker commandInvoker;
    private final HideableComboViewer hideableComboViewer;

    public AddCategoryAction(BookmarkCommandInvoker commandInvoker, HideableComboViewer hideableComboViewer) {
        this.commandInvoker = commandInvoker;
        this.hideableComboViewer = hideableComboViewer;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry()
                .getDescriptor(Activator.ICON_CATEGORY));
        this.setToolTipText("Add new category");
        this.setText("New Category");
    }

    @Override
    public void run() {
        commandInvoker.invoke(new AddCategoryCommand(hideableComboViewer));
    }
}
