package org.eclipselabs.recommenders.bookmark.view.categoryview.combo;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.view.categoryview.CategoryView;

public class ComboModifyListener
	implements ModifyListener
{

	private final Button button;
	private final CategoryView categoryView;

	public ComboModifyListener(Button saveBookmarkNameChanges,
			CategoryView categoryView)
	{
		this.button = saveBookmarkNameChanges;
		this.categoryView = categoryView;
	}

	@Override
	public void modifyText(ModifyEvent e)
	{
		Combo combo = (Combo) e.getSource();
		String text = combo.getText();

		BMNode head = categoryView.getModel().getModelHead();
		String headName = (String) head.getValue();

		if (areDifferent(text, headName)) {
			button.setEnabled(true);
		}
		else {
			button.setEnabled(false);
		}
	}

	private boolean areDifferent(String text, String headName)
	{
		return text.compareTo(headName) != 0;
	}

}
