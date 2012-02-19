package org.eclipselabs.recommenders.bookmark.view.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class BookmarkCurrentPositionHandler extends AbstractHandler{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        System.err.println("Executing BookmarkCurrentPositionHandler");
        return null;
    }

}
