package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.node.BookmarkNode;
import org.eclipselabs.recommenders.bookmark.tree.node.ReferenceNode;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class TreeLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof ReferenceNode) {
			ReferenceNode node = (ReferenceNode) element;
			return node.getName() + "@" + node.getProjectName();
		} else if (element instanceof BookmarkNode) {
			BookmarkNode node = (BookmarkNode) element;
			return node.getText();
		}
		return "";
	}

	@Override
	public Image getImage(Object element) {

		Image image = null;
		AbstractUIPlugin plugin = Activator.getDefault();
		ImageRegistry registry = plugin.getImageRegistry();

		image = registry.get(getImageKeyForType(element));

		return image;
	}

	public String getImageKeyForType(Object element) {

		if (!(element instanceof TreeNode))
			return Activator.ICON_DEFAULT;

		if (element instanceof ReferenceNode) {
			ReferenceNode node = (ReferenceNode) element;
			String name = node.getName();
			int pos = name.lastIndexOf(".");
			if (pos != -1) {
				if (name.substring(pos + 1).compareTo("java") == 0)
					return Activator.ICON_JAVAFILE;
			}
		}

		if (element instanceof BookmarkNode)
			return Activator.ICON_BOOKMARK;

		return Activator.ICON_DEFAULT;
	}
}
