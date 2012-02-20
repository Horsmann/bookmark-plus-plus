package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipselabs.recommenders.bookmark.action.SelfEnabling;

import com.google.common.collect.Lists;

public class SelectionChangedListener implements ISelectionChangedListener {

    private List<SelfEnabling> clients = Lists.newArrayList();

    public void register(SelfEnabling selfEnabling) {
        clients.add(selfEnabling);
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        for (SelfEnabling client : clients) {
            client.updateEnableStatus();
        }
    }

}