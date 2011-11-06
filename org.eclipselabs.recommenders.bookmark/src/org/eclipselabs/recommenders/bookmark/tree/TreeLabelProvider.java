package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.Activator;

public class TreeLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof ReferenceNode) {
			ReferenceNode node = (ReferenceNode) element;
			return node.getName() + "@" + node.getProjectName(); 
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

		if (!(element instanceof ReferenceNode))
			return "default";

		ReferenceNode node = (ReferenceNode) element;
		String name = node.getName();
		int pos = name.lastIndexOf(".");
		if (pos != -1) {
			if (name.substring(pos + 1).compareTo("java") == 0)
				return "javafile";
		}

		return "default";
	}
}
