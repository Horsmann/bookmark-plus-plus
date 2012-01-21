package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.util.DirectoryUtil;
import org.eclipselabs.recommenders.bookmark.util.ResourceAvailabilityValidator;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class TreeLabelProvider
	extends LabelProvider
{

	private AbstractUIPlugin plugin = Activator.getDefault();
	private ImageRegistry registry = plugin.getImageRegistry();

	private JavaElementLabelProvider jelp = new JavaElementLabelProvider(
			JavaElementLabelProvider.SHOW_RETURN_TYPE
					| JavaElementLabelProvider.SHOW_SMALL_ICONS
					| JavaElementLabelProvider.SHOW_DEFAULT);

	private ViewManager manager;

	public TreeLabelProvider(ViewManager manager)
	{
		this.manager = manager;
	}

	@Override
	public String getText(Object object)
	{
		if (object instanceof BMNode) {
			BMNode node = (BMNode) object;

			String text = jelp.getText(node.getValue());

			text = updateTextIfViewIsFlattened(text, node);

			if (text.compareTo("") != 0) {
				return text;
			}

			return (String) node.getValue();

		}

		return "UNKNOWN TYPE";
	}

	private String updateTextIfViewIsFlattened(String text, BMNode viewNode)
	{

		BMNode node = getModelNode(viewNode);

		Object value = node.getValue();

		if (!showExtendInformationInName(value)) {
			return text;
		}

		String prefix = generatePrefix(value);
		String suffix = generateSuffix(value);

		String updatedText = prefix + text + suffix;
		updatedText = updatedText.trim();

		return updatedText;
	}

	private String generateSuffix(Object value)
	{
		String project = determineProject(value);
		String suffix = " - " + project;
		return suffix;
	}

	private String generatePrefix(Object value)
	{
		String compilationUnit = determineCompilationUnit(value);
		String prefix = "";

		if (!(value instanceof ICompilationUnit)
				&& compilationUnit.compareTo("") != 0) {
			prefix = compilationUnit + ".";
		}

		return prefix;
	}

	private String determineProject(Object value)
	{
		return DirectoryUtil.getProjectName(value);
	}

	private String determineCompilationUnit(Object value)
	{
		if (!(value instanceof IJavaElement)) {
			return "";
		}
		IJavaElement element = (IJavaElement) value;
		String compilationUnit = DirectoryUtil.getCompilationUnitName(element);

		compilationUnit = chopOffFileEnding(compilationUnit);

		return compilationUnit;
	}

	private String chopOffFileEnding(String compilationUnit)
	{
		int pos = compilationUnit.lastIndexOf(".");

		if (pos == -1) {
			return compilationUnit;
		}

		String name = compilationUnit.substring(0, pos);

		return name;
	}

	private BMNode getModelNode(BMNode viewNode)
	{
		if (viewNode.hasReference()) {
			return viewNode.getReference();
		}
		return viewNode;
	}

	protected boolean showExtendInformationInName(Object value)
	{
		return manager.isViewFlattened()
				&& ((value instanceof IJavaElement) || (value instanceof IFile));
	}

	@Override
	public Image getImage(Object element)
	{

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

	public String getImageKeyForType(Object element)
	{

		if (!(element instanceof BMNode)) {
			return Activator.ICON_DEFAULT;
		}

		BMNode node = (BMNode) element;
		if (node.isBookmarkNode()) {
			return Activator.ICON_CATEGORY;
		}

		return Activator.ICON_DEFAULT;
	}
}
