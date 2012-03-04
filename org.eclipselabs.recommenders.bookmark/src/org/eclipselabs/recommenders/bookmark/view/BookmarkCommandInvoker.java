package org.eclipselabs.recommenders.bookmark.view;

import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;

public interface BookmarkCommandInvoker {

    void invoke(IBookmarkModelCommand command);

}
