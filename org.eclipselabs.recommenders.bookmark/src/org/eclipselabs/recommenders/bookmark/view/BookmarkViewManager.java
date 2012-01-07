package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.copyCutPaste.CopyHandler;
import org.eclipselabs.recommenders.bookmark.copyCutPaste.CutHandler;
import org.eclipselabs.recommenders.bookmark.copyCutPaste.PasteHandler;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.FlatTreeNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.ViewStateRestorer;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
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

	private ExpandedStorage expandedStorage;

	@Override
	public void createPartControl(Composite parent)
	{
		model = new TreeModel();
		flatModel = new TreeModel();
		expandedStorage = new ExpandedStorage(this);
		// //
		container = new Composite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		container.setLayout(stackLayout);
		// //
		defaultView = new DefaultView(this, container);
		toggledView = new CategoryView(this, container);

		stackLayout.topControl = defaultView.getComposite();
		activeView = defaultView;
		defaultView.setUpToolbarForViewPart();

		addPartViewFeatures();
		restoreSavedState();

		activeView.updateControls();

		Activator.setManager(this);
	}

	private void addPartViewFeatures()
	{
		IPartService service = (IPartService) getSite().getService(
				IPartService.class);
		service.addPartListener(new ViewPartListener(this));

		IHandlerService handlerServ = (IHandlerService) getSite().getService(
				IHandlerService.class);
		PasteHandler paste = new PasteHandler(this);
		handlerServ.activateHandler("org.eclipse.ui.edit.paste", paste);

		CopyHandler copy = new CopyHandler(this);
		handlerServ.activateHandler("org.eclipse.ui.edit.copy", copy);

		CutHandler cut = new CutHandler(this);
		handlerServ.activateHandler("org.eclipse.ui.edit.cut", cut);
	}

	@Override
	public BookmarkView getActiveBookmarkView()
	{
		return activeView;
	}

	@Override
	public void activateToggledView()
	{
		isViewToggled = true;

		stackLayout.topControl = toggledView.getComposite();
		activeView = toggledView;
		toggledView.setUpToolbarForViewPart();

		container.layout(true, true);

		toggledView.updateControls();
	}

	@Override
	public void activateDefaultView()
	{
		isViewToggled = false;

		stackLayout.topControl = defaultView.getComposite();
		activeView = defaultView;
		defaultView.setUpToolbarForViewPart();

		container.layout(true, true);

		toggledView.updateControls();
	}

	private void restoreSavedState()
	{
		String[] lines = BookmarkFileIO.loadFromDefaultFile();

		if (lines == null) {
			return;
		}

		if (lines.length >= 1) {
			loadBookmarkData(lines[0]);
		}

		if (lines.length >= 2) {
			new ViewStateRestorer(this).restoreViewState(lines[1]);
		}

	}

	private void loadBookmarkData(String data)
	{
		RestoredTree restoredTree = TreeDeserializerFacade.deserialize(data);
		model.setModelRoot(restoredTree.getRoot());
		activeView.getView().setInput(model.getModelRoot());
		TreeDeserializerFacade.setExpandedNodesForView(activeView.getView(),
				restoredTree.getExpanded());
		checkPreferencesForDeletionOfDeadReferences();

		expandedStorage.addCurrentlyExpandedNodes();
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

	@Override
	public void activateFlattenedModus(BMNode node)
	{
		flatModel.getModelRoot().removeAllChildren();

		BMNode flattenedRoot = new TreeNode(null, false, false);

		boolean setUpForDefault = setUpForDefaultView(node);
		if (setUpForDefault) {
			expandedStorage.addCurrentlyExpandedNodes();

			buildFlatModelForDefaultView(flattenedRoot, node);
		}
		else {
			buildFlatModelForCategoryView(flattenedRoot, node);
		}

		TreeViewer view = activeView.getView();

		flatModel.setModelRoot(flattenedRoot);
		view.setInput(null);
		view.setInput(flatModel.getModelRoot());

		// we have to set the data in the view before we can expand
		if (setUpForDefault) {
			expandBookmarks(flattenedRoot, view);
		}

		isFlattenedActive = true;
	}

	@Override
	public void deactivateFlattenedModus()
	{
		activeView.getView().setInput(null);
		activeView.getView().setInput(model.getModelHead());
		expandedStorage.expandStoredNodesForActiveView();
		isFlattenedActive = false;
	}

	private void expandBookmarks(BMNode flattenedRoot, TreeViewer view)
	{
		for (BMNode child : flattenedRoot.getChildren()) {
			TreeUtil.showNodeExpanded(view, child);
		}
	}

	private void buildFlatModelForCategoryView(BMNode flattenedRoot, BMNode node)
	{
		BMNode[] userAddedNodes = TreeUtil
				.getNodesThatShallBeVisibleInFlatModusUnderNode(node);

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
				.getNodesThatShallBeVisibleInFlatModusUnderNode(nodeWhichUserAddedNodesShallBeAddedToTargetNode);

		for (BMNode userAdded : userAddedNodes) {

			BMNode newNode = new FlatTreeNode(userAdded);
			targetNode.addChild(newNode);
		}

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

	@Override
	public TreeModel getModel()
	{
		return model;
	}

	@Override
	public TreeModel getFlatModel()
	{
		return flatModel;
	}

	@Override
	public ExpandedStorage getExpandedStorage()
	{
		return expandedStorage;
	}

	@Override
	public void saveModelState()
	{
		TreeSerializerFacade.serializeToDefaultLocation(this);
	}
}