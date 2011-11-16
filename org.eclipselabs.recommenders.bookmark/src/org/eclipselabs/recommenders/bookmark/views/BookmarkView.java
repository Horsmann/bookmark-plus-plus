package org.eclipselabs.recommenders.bookmark.views;

import java.util.Collections;
import java.util.List;

import javax.management.modelmbean.ModelMBean;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.SWTNodeEditListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.tree.TreeDragListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeDropListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeLabelProvider;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

import com.google.gson.Gson;

import org.eclipse.ui.ide.IDE;

public class BookmarkView extends ViewPart {

	private TreeViewer viewer;
	private TreeModel model;
	private Action showInEditor, saveBookmarks, loadBookmarks;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		model = new TreeModel();

		addDragDropSupportToView(viewer, model);

		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(model.getModelRoot());

		// viewer.setContentProvider(new
		// StandardJavaElementContentProvider(true));
		// viewer.setLabelProvider(new JavaElementLabelProvider());
		// viewer.setInput(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));

		viewer.getTree().addListener(SWT.MouseDoubleClick,
				new SWTNodeEditListener(viewer));

		createActions();
		setUpToolbar();

	}

	private void createActions() {

		createOpenBookmarkAction();
		createSaveBookmarkAction();
		createLoadBookmarkAction();

	}

	private void createSaveBookmarkAction() {
		saveBookmarks = new Action("Save Bookmarks") {
			public void run() {
				saveBookmarks();
			}
		};
		saveBookmarks.setImageDescriptor(Activator.getDefault()
				.getImageRegistry()
				.getDescriptor(Activator.ICON_SAVE_BOOKMARKS));
	}

	private void createLoadBookmarkAction() {
		loadBookmarks = new Action("Load Bookmarks") {
			public void run() {
				loadBookmarks();
			}
		}; //
		loadBookmarks.setImageDescriptor(Activator.getDefault()
				.getImageRegistry()
				.getDescriptor(Activator.ICON_LOAD_BOOKMARKS));
	}

	private void loadBookmarks() {

		String test = "";

		IJavaElement element = JavaCore.create(test);
		
		System.err.println(element.getElementName() + " "
				+ element.getElementType() + " " + element.getJavaProject());

		IEditorPart part;
		try {
			part = JavaUI.openInEditor(element);
			JavaUI.revealInEditor(part, element);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	

	}

	private void saveBookmarks() {

		Gson gson = new Gson();
		
		//Nur flache Hierarchien
		
		TreeNode root = model.getModelRoot();
		
		String treeStructure="";
		
		//Replizieren der Baumstruktur und Zuweisung als Object-Value die ID
		
		String treeStructureGson = gson.toJson(treeStructure);
		
//		List<IStructuredSelection> selectedList = getTreeSelections();
//		for (int i = 0; i < selectedList.size(); i++) {
//			TreeNode node = (TreeNode) selectedList.get(i);
//
//			IJavaElement nodeValue = (IJavaElement) node.getValue();
//
//			String valueId = nodeValue.getHandleIdentifier();
//			
//			gson.toJson(valueId);
//		}
//
//		System.err.println("save");
	}

	private void createOpenBookmarkAction() {
		showInEditor = new Action("Open Bookmarks") {
			public void run() {
				try {
					openBookmarks();
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		showInEditor
				.setImageDescriptor(Activator.getDefault().getImageRegistry()
						.getDescriptor(Activator.ICON_SHOW_IN_EDITOR));
	}

	private void setUpToolbar() {

		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(showInEditor);
		mgr.add(saveBookmarks);
		mgr.add(loadBookmarks);
	}

	private void openBookmarks() throws PartInitException, JavaModelException {

		List<IStructuredSelection> selectedList = getTreeSelections();
		for (int i = 0; i < selectedList.size(); i++) {
			TreeNode node = (TreeNode) selectedList.get(i);
			System.err.println("Open: " + node.getValue());

			// Speichern der Objektkopie beim droppen und dann ein ggf.
			// reduziertes return?
			// Platform.getAdapterManager().
			// IEditorPart part = JavaUI.openInEditor((IJavaElement) node
			// .getValue());

			// JavaUI.revealInEditor(part, (IJavaElement) node.getValue());

			IFile file = (IFile) Platform.getAdapterManager()
					.getAdapter(((IJavaElement) node.getValue()).getResource(),
							IFile.class);
			// IEditorPart part = EditorUtility.openInEditor(file);

			IDE.openEditor(this.getViewSite().getWorkbenchWindow()
					.getActivePage(), file);

		}

		System.err.println("****END*****");
	}

	@SuppressWarnings("unchecked")
	private List<IStructuredSelection> getTreeSelections() {
		ISelection selection = viewer.getSelection();
		if (selection == null)
			return Collections.emptyList();
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
			return ((IStructuredSelection) selection).toList();

		return Collections.emptyList();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public IViewSite getViewSite() {
		return (IViewSite) getSite();
	}

	public void addDragDropSupportToView(TreeViewer viewer, TreeModel model) {
		int operations = DND.DROP_LINK;
		Transfer[] transferTypes = new Transfer[] {
				ResourceTransfer.getInstance(),
				LocalSelectionTransfer.getTransfer() };

		viewer.addDropSupport(operations, transferTypes, new TreeDropListener(
				viewer, model));
		viewer.addDragSupport(operations, transferTypes, new TreeDragListener(
				viewer));
	}

}