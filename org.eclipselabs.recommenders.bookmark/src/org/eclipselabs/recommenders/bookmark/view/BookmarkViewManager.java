package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.subview.CategoryView;
import org.eclipselabs.recommenders.bookmark.view.subview.DefaultView;

public class BookmarkViewManager extends ViewPart implements ViewManager {

	private TreeModel model;

	private StackLayout stackLayout = null;
	private Composite container = null;

	private BookmarkView activeView = null;
	private DefaultView defaultView = null;
	private CategoryView toggledView = null;

	@Override
	public void createPartControl(Composite parent) {
		model = new TreeModel();
		// //
		container = new Composite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		container.setLayout(stackLayout);
		// //
		defaultView = new DefaultView(this, container, model);
		toggledView = new CategoryView(this, container, model);

		stackLayout.topControl = defaultView.composite;
		activeView = defaultView;
		defaultView.setUpToolbarForViewPart();

		addPartViewFeatures();
		restoreBookmarks();
		activeView.getView().refresh();
	}

	private void addPartViewFeatures() {
		IPartService service = (IPartService) getSite().getService(
				IPartService.class);
		service.addPartListener(new ViewPartListener(getActiveViewer(), model));
	}

	@Override
	public TreeViewer getActiveViewer() {
		return activeView.getView();
	}

	public void activateToggledView() {
		stackLayout.topControl = toggledView.composite;
		activeView = toggledView;
		toggledView.setUpToolbarForViewPart();
		toggledView.refreshCategories();
		container.layout(true, true);
	}

	public void activateDefaultView() {
		stackLayout.topControl = defaultView.composite;
		activeView = defaultView;
		defaultView.setUpToolbarForViewPart();
		container.layout(true, true);
	}

	private void restoreBookmarks() {
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

	}

	private void checkPreferencesForDeletionOfDeadReferences() {
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		boolean removeDeadReferences = preferenceStore
				.getBoolean(org.eclipselabs.recommenders.bookmark.preferences.PreferenceConstants.REMOVE_DEAD_BOOKMARK_REFERENCES);

		if (removeDeadReferences) {
			final TreeNode root = model.getModelRoot();

			TreeUtil.deleteNodesReferencingToDeadResourcesUnderNode(root, model);
			activeView.getView().refresh();
		}

	}

	@Override
	public void setFocus() {
		activeView.getView().getControl().setFocus();
	}

	public IViewSite getViewSite() {
		return (IViewSite) getSite();
	}

	@Override
	public ViewPart getViewPart() {
		return (ViewPart) this;
	}

	@Override
	public void activateNextView() {

		if (activeView == defaultView) {
			activateToggledView();
			return;
		}

		if (activeView == toggledView) {
			activateDefaultView();
			return;
		}

	}

}