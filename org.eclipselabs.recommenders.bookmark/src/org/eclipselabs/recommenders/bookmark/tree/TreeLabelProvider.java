package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class TreeLabelProvider extends LabelProvider {

	JavaElementLabelProvider jelp = new JavaElementLabelProvider(
			JavaElementLabelProvider.SHOW_RETURN_TYPE
					| JavaElementLabelProvider.SHOW_SMALL_ICONS
					| JavaElementLabelProvider.SHOW_DEFAULT);

	@Override
	public String getText(Object element) {
		if (element instanceof TreeNode) {
			TreeNode node = (TreeNode) element;

			String text = jelp.getText(((TreeNode) element).getValue());

			if (text.compareTo("") != 0)
				return text;

			if (node.isBookmarkNode())
				return (String) node.getValue();

		}

		return "UNKNOWN TYPE";
	}

	@Override
	public Image getImage(Object element) {

		Image image = null;
		AbstractUIPlugin plugin = Activator.getDefault();
		ImageRegistry registry = plugin.getImageRegistry();

		if (element instanceof TreeNode) {
			image = jelp.getImage(((TreeNode) element).getValue());
			if (image == null)
				image = registry.get(getImageKeyForType(element));
		}

		return image;

	}

	public String getImageKeyForType(Object element) {

		if (!(element instanceof TreeNode))
			return Activator.ICON_DEFAULT;

		TreeNode node = (TreeNode) element;
		if (node.isBookmarkNode())
			return Activator.ICON_BOOKMARK;

		return Activator.ICON_DEFAULT;
	}
}
