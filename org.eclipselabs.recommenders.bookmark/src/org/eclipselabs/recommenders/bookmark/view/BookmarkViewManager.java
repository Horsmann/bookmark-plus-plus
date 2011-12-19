package org.eclipselabs.recommenders.bookmark.view;

import java.util.HashMap;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.copyPaste.CopyHandler;
import org.eclipselabs.recommenders.bookmark.copyPaste.PasteHandler;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.FlatTreeNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.categoryview.CategoryView;
import org.eclipselabs.recommenders.bookmark.view.defaultView.DefaultView;

public class BookmarkViewManager
	extends ViewPart
	implements ViewManager
{

	private TreeModel model;

	/*
	 * This model will only be used during the flatten representation is used
	 * and will only referencing nodes of the 'main' model that shall be shown
	 * for the flatten view
	 */
	private TreeModel flatModel;

	private StackLayout stackLayout;
	private Composite container;

	private BookmarkView activeView;
	private DefaultView defaultView;
	private CategoryView toggledView;

	private boolean isFlattenedActive;
	private boolean isViewToggled;

	private HashMap<Object, String> expandedNodes = new HashMap<Object, String>();

	@Override
	public void createPartControl(Composite parent)
	{
		model = new TreeModel();
		flatModel = new TreeModel();
		// //
		container = new Composite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		container.setLayout(stackLayout);
		// //
		defaultView = new DefaultView(this, container, model, flatModel);
		toggledView = new CategoryView(this, container, model, flatModel);

		stackLayout.topControl = defaultView.getComposite();
		activeView = defaultView;
		defaultView.setUpToolbarForViewPart();

		addPartViewFeatures();
		restoreBookmarks();
		activeView.getView().refresh();
	}

	private void addPartViewFeatures()
	{
		IPartService service = (IPartService) getSite().getService(
				IPartService.class);
		service.addPartListener(new ViewPartListener(activeView));

		IHandlerService handlerServ = (IHandlerService) getSite().getService(
				IHandlerService.class);
		PasteHandler paste = new PasteHandler(activeView);
		handlerServ.activateHandler("org.eclipse.ui.edit.paste", paste);

		CopyHandler copy = new CopyHandler(this);
		handlerServ.activateHandler("org.eclipse.ui.edit.copy", copy);
	}

	@Override
	public BookmarkView getActiveBookmarkView()
	{
		return activeView;
	}

	public void activateToggledView()
	{
		isViewToggled = true;

		stackLayout.topControl = toggledView.getComposite();
		activeView = toggledView;
		toggledView.setUpToolbarForViewPart();

		container.layout(true, true);

		toggledView.updateControls();
	}

	public void activateDefaultView()
	{
		isViewToggled = false;

		stackLayout.topControl = defaultView.getComposite();
		activeView = defaultView;
		defaultView.setUpToolbarForViewPart();

		container.layout(true, true);

		toggledView.updateControls();
	}

	private void restoreBookmarks()
	{
		String[] lines = BookmarkFileIO.loadFromDefaultFile();
		if (lines != null && lines.length > 0) {
			RestoredTree restoredTree = TreeDeserializerFacade
					.deserialize(lines[0]);
			model.setModelRoot(restoredTree.getRoot());
			activeView.getView().setInput(model.getModelRoot());
			TreeDeserializerFacade.setExpandedNodesForView(
					activeView.getView(), restoredTree.getExpanded());
		}

		checkPreferencesForDeletionOfDeadReferences();

		addCurrentlyExpandedNodesToStorage();

	}

	private void checkPreferencesForDeletionOfDeadReferences()
	{
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		boolean removeDeadReferences = preferenceStore
				.getBoolean(org.eclipselabs.recommenders.bookmark.preferences.PreferenceConstants.REMOVE_DEAD_BOOKMARK_REFERENCES_VIEW_OPENING);

		if (removeDeadReferences) {
			final BMNode root = model.getModelRoot();

			TreeUtil.deleteNodesReferencingToDeadResourcesUnderNode(root, model);
			activeView.getView().refresh();
		}

	}

	@Override
	public void setFocus()
	{
		activeView.getView().getControl().setFocus();
	}

	public IViewSite getViewSite()
	{
		return (IViewSite) getSite();
	}

	@Override
	public ViewPart getViewPart()
	{
		return (ViewPart) this;
	}

	@Override
	public BookmarkView activateNextView()
	{

		if (activeView == defaultView) {
			activateToggledView();
			return activeView;
		}

		if (activeView == toggledView) {
			activateDefaultView();
			return activeView;
		}

		return null;
	}

	public void addCurrentlyExpandedNodesToStorage()
	{
		Object[] expanded = activeView.getView().getExpandedElements();
		for (Object o : expanded) {
			expandedNodes.put(o, "");
		}
		if (!model.isHeadEqualRoot()) {
			expandedNodes.put(model.getModelHead(), "");
		}
	}

	public void setStoredExpandedNodesForActiveView()
	{
		Object[] expanded = expandedNodes.keySet().toArray();
		activeView.getView().setExpandedElements(expanded);
	}

	@Override
	public void deleteExpandedNodeFromStorage(Object node)
	{
		expandedNodes.remove(node);
	}

	@Override
	public void reinitializeExpandedStorage()
	{
		expandedNodes = new HashMap<Object, String>();
	}

	@Override
	public void activateFlattenedView(BMNode node)
	{
		flatModel.getModelRoot().removeAllChildren();

		BMNode flattenedRoot = new TreeNode(null, false, true);

		if (setUpForDefaultView(node)) {
			buildFlatModelForDefaultView(flattenedRoot, node);
		}
		else {
			buildFlatModelForCategoryView(flattenedRoot, node);
		}

		flatModel.setModelRoot(flattenedRoot);
		activeView.getView().setInput(null);
		activeView.getView().setInput(flatModel.getModelRoot());

		isFlattenedActive = true;
	}

	private void buildFlatModelForCategoryView(BMNode flattenedRoot, BMNode node)
	{
		BMNode[] userAddedNodes = TreeUtil
				.getNonAutoGeneratedNodesUnderNode(node);

		for (BMNode userAdded : userAddedNodes) {

			BMNode newNode = new FlatTreeNode(userAdded);
			flattenedRoot.addChild(newNode);
		}
	}

	private void buildFlatModelForDefaultView(BMNode flattenedRoot, BMNode node)
	{
		for (BMNode child : node.getChildren()) {
			FlatTreeNode flatBookmark = new FlatTreeNode(child);
			addUserAddedNodesToFlatBookmark(flatBookmark, child);
			flattenedRoot.addChild(flatBookmark);
		}
	}

	private boolean setUpForDefaultView(BMNode node)
	{
		// if the childs are bookmarks "node" is the root, so we are preparing
		// for the default view
		return node.hasChildren() && node.getChildren()[0].isBookmarkNode();
	}

	private void addUserAddedNodesToFlatBookmark(FlatTreeNode targetNode,
			BMNode nodeWhichUserAddedNodesShallBeAddedToTargetNode)
	{
		BMNode[] userAddedNodes = TreeUtil
				.getNonAutoGeneratedNodesUnderNode(nodeWhichUserAddedNodesShallBeAddedToTargetNode);

		for (BMNode userAdded : userAddedNodes) {

			BMNode newNode = new FlatTreeNode(userAdded);
			targetNode.addChild(newNode);
		}

	}

	@Override
	public void deactivateFlattenedView()
	{
		activeView.getView().setInput(null);
		activeView.getView().setInput(model.getModelHead());
		setStoredExpandedNodesForActiveView();
		isFlattenedActive = false;
	}

	@Override
	public boolean isViewFlattened()
	{
		return isFlattenedActive;
	}

	@Override
	public boolean isViewToggled()
	{
		return isViewToggled;
	}
}