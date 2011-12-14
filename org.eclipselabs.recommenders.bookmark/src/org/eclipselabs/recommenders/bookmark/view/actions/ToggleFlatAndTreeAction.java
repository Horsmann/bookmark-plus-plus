package org.eclipselabs.recommenders.bookmark.view.actions;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;

public class ToggleFlatAndTreeAction extends Action
{
	public ToggleFlatAndTreeAction(){
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_TOGGLE_FLAT_HIERARCHY));
		this.setToolTipText("Toggles between tree and a flattened representation");
		this.setText("Toggle flat/tree");
	}
	
	@Override
	public void run() {
		System.err.println("click");
	}

}
