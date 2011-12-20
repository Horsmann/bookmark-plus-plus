package org.eclipselabs.recommenders.bookmark.view.categoryview;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;

public class ComboModifyListener implements ModifyListener
{

	private final Button button;

	public ComboModifyListener(Button saveBookmarkNameChanges)
	{
		this.button = saveBookmarkNameChanges;
	}

	@Override
	public void modifyText(ModifyEvent e)
	{
		button.setEnabled(true);
	}

}
