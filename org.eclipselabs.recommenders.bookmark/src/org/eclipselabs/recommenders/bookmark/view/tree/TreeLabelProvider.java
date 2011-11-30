package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

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
			TreeNode node = (TreeNode) element;
			image = jelp.getImage(node.getValue());

			if (!isAssociatedProjectOpen(node))
				image = registry.get(Activator.ICON_ASSOCIATED_PROJECT_CLOSED);
			
			if (!doesAssociatedProjectExists(node))
				image = registry.get(Activator.ICON_ASSOCIATED_PROJECT_NOT_AVAILABLE);

			if (image == null || node.isBookmarkNode())
				image = registry.get(getImageKeyForType(element));
		}

		return image;

	}
	
	private boolean doesAssociatedProjectExists(TreeNode node) {
		Object value = node.getValue();
		if (value instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) value;
			IJavaProject project = element.getJavaProject();
			boolean doesExist = project.exists();
			return doesExist;
		}

		return false;
	}

	private boolean isAssociatedProjectOpen(TreeNode node) {
		Object value = node.getValue();
		if (value instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) value;
			IProject project = element.getJavaProject().getProject();
			boolean isOpen = project.isOpen();
			return isOpen;
		}

		return false;
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
