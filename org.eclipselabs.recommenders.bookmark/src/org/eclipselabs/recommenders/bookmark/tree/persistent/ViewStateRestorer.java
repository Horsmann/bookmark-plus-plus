package org.eclipselabs.recommenders.bookmark.tree.persistent;

import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ViewStateRestorer
{
	private final ViewManager manager;

	public ViewStateRestorer(ViewManager manager) {
		this.manager = manager;
	}

	public void restoreViewState(String data){
		String[] split = data.split("\t");
		if (split.length == 0) {
			return;
		}

		Integer index = Integer.parseInt(split[0]);

		if (restoreInToggledState(split)) {
			restoreToggled(index);
		}

		if (restoreInFlattenedState(split)) {
			restoreFlattened(index);
		}
	}
	
	private void restoreFlattened(Integer index)
	{
		if (index > -1) {
			BMNode[] children = manager.getModel().getModelRoot().getChildren();

			manager.activateFlattenedModus(children[index]);
		}
		else {
			manager.activateFlattenedModus(manager.getModel().getModelRoot());
		}
	}

	private boolean restoreInFlattenedState(String[] split)
	{
		return (split.length >= 3 && split[2].compareTo(Persistent.FLAT) == 0);
	}

	private void restoreToggled(Integer index)
	{
		BMNode[] children = manager.getModel().getModelRoot().getChildren();
		manager.getModel().setHeadNode(children[index]);
		manager.activateToggledView();
		manager.getActiveBookmarkView().getView().setInput(children[index]);

	}

	private boolean restoreInToggledState(String[] split)
	{
		return (split.length >= 2 && split[1].compareTo(Persistent.TOGGLED) == 0);
	}
}
