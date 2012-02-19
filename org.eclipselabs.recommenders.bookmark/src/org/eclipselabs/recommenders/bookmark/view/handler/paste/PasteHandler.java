package org.eclipselabs.recommenders.bookmark.view.handler.paste;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class PasteHandler extends AbstractHandler {

    private IPasteStrategy strategy;

    public PasteHandler(IPasteStrategy initialStrategy) {
        this.strategy = initialStrategy;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        strategy.execute(event);
        return null;
    }

    public void setNewStrategy(IPasteStrategy strategy) {
        this.strategy = strategy;
    }
}
