package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.util.ResourceAvailabilityValidator;

public class TreeLabelProvider extends LabelProvider {

	private AbstractUIPlugin plugin = Activator.getDefault();
	private ImageRegistry registry = plugin.getImageRegistry();

	private JavaElementLabelProvider jelp = new JavaElementLabelProvider(
			JavaElementLabelProvider.SHOW_RETURN_TYPE
					| JavaElementLabelProvider.SHOW_SMALL_ICONS
					| JavaElementLabelProvider.SHOW_DEFAULT);

	@Override
	public String getText(Object element) {
		if (element instanceof BMNode) {
			BMNode node = (BMNode) element;

			String text = jelp.getText(((BMNode) element).getValue());

			if (text.compareTo("") != 0)
				return text;

			return (String) node.getValue();

		}

		return "UNKNOWN TYPE";
	}

	@Override
	public Image getImage(Object element) {

		Image image = null;

		BMNode node = (BMNode) element;

		if (node.isBookmarkNode()) {
			image = registry.get(getImageKeyForType(element));
			return image;
		}

		Object value = node.getValue();
		image = jelp.getImage(value);
		
		
		if (!ResourceAvailabilityValidator.doesAssociatedProjectExists(value)) {
			image = registry
					.get(Activator.ICON_ASSOCIATED_RESOURCE_NOT_AVAILABLE);
			return image;
		}
		
		if (!ResourceAvailabilityValidator.isAssociatedProjectOpen(value)) {
			image = registry.get(Activator.ICON_ASSOCIATED_PROJECT_CLOSED);
			return image;
		}
		
		if (!ResourceAvailabilityValidator.doesReferecedObjectExists(value)) {
			image = registry
					.get(Activator.ICON_ASSOCIATED_RESOURCE_NOT_AVAILABLE);
			return image;
		}

		return image;

	}

	public String getImageKeyForType(Object element) {

		if (!(element instanceof BMNode))
			return Activator.ICON_DEFAULT;

		BMNode node = (BMNode) element;
		if (node.isBookmarkNode())
			return Activator.ICON_BOOKMARK;

		return Activator.ICON_DEFAULT;
	}
}
