package org.eclipselabs.recommenders.bookmark.view.categoryview.combo;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ComboModifyListener
	implements ModifyListener
{

	private final Button button;
	private final ViewManager manager;

	public ComboModifyListener(Button saveBookmarkNameChanges,
			ViewManager manager)
	{
		this.button = saveBookmarkNameChanges;
		this.manager = manager;
	}

	@Override
	public void modifyText(ModifyEvent e)
	{
		Combo combo = (Combo) e.getSource();
		String text = combo.getText();

		BMNode head = manager.getModel().getModelHead();
		String headName = (String) head.getValue();

		if (areDifferent(text, headName) && textIsNotBlank(text)) {
			button.setEnabled(true);
		}
		else {
			button.setEnabled(false);
		}
	}

	private boolean textIsNotBlank(String text)
	{
		return text.trim().compareTo("") != 0;
	}

	private boolean areDifferent(String text, String headName)
	{
		return text.compareTo(headName) != 0;
	}

}
