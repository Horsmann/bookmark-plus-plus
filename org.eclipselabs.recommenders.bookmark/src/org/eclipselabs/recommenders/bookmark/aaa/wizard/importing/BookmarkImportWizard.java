/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipselabs.recommenders.bookmark.aaa.wizard.importing;

import java.io.File;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkIO;
import org.eclipselabs.recommenders.bookmark.aaa.commands.AddElementToModelCommand;
import org.eclipselabs.recommenders.bookmark.aaa.commands.DropTargetData;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.HierarchyValueVisitor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class BookmarkImportWizard extends Wizard implements IImportWizard {

    BookmarkImportWizardPage mainPage;

    public BookmarkImportWizard() {
        super();
    }

    public boolean performFinish() {
        IStructuredSelection selections = mainPage.getSelections();
        File file = mainPage.getFile();

        try {
            performImport(file, selections);
        } catch (JavaModelException e) {
            e.printStackTrace();
        }

        // try {
        // performImport(file, treeSelections);
        // Activator.getManager().saveModelState();
        // }
        // catch (JavaModelException e) {
        // e.printStackTrace();
        // }
        //
        // ViewManager manager = Activator.getManager();
        // if (manager.isViewFlattened()) {
        // BMNode head = manager.getModel().getModelHead();
        // manager.activateFlattenedModus(head);
        // }
        System.out.println("import finised");
        return true;
    }

    private void performImport(File file, IStructuredSelection treeSelections) throws JavaModelException {

        if (treeSelections.isEmpty()) {
            importAll(file);
        } else {
            importSelected(file, treeSelections);
        }
    }

    private void importSelected(File file, IStructuredSelection treeSelections) {
        // TODO Auto-generated method stub

    }

    private void importAll(File file) {
        BookmarkModel model = BookmarkIO.load(file);

        if (mainPage.consolidateBookmarksAsSingleCategory()) {
            String newCategoryName = mainPage.getCategoryName();

            List<Object> values = Lists.newArrayList();

            for (Category cat : model.getCategories()) {
                HierarchyValueVisitor valueVisitor = new HierarchyValueVisitor();
                cat.accept(valueVisitor);
                values.addAll(valueVisitor.getValues());
            }
            
            

            Optional<DropTargetData> target = Optional.absent();
            Optional<String> categoryName = Optional.of(newCategoryName);
            Activator.getCommandInvoker().invoke(
                    new AddElementToModelCommand(target, values.toArray(), categoryName, Activator
                            .getCommandInvoker()));

            return;
            // Activator.getModel().add(category);
        }

        for (Category category : model.getCategories()) {
            Activator.getModel().add(category);
        }
    }

    //
    // private void importSelected(File file,
    // List<IStructuredSelection> treeSelections)
    // throws JavaModelException
    // {
    // BMNode[] selectedNodes = WizardUtil.getSelectedNodes(treeSelections);
    //
    // BMNode root = WizardUtil.buildTreeConsistingOfSelection(selectedNodes);
    //
    // if (mainPage.consolidateBookmarksAsSingleBookmark()) {
    // root = uniteBookmarksWithNewName(root);
    // }
    //
    // ViewManager manager = Activator.getManager();
    // TreeModel model = manager.getModel();
    // for (BMNode bookmark : root.getChildren()) {
    // model.getModelRoot().addChild(bookmark);
    // }
    //
    // Object[] nodesToExpand = TreeUtil.getTreeBelowNode(root);
    //
    // TreeViewer viewer = manager.getActiveBookmarkView().getView();
    // for (Object o : nodesToExpand) {
    // TreeUtil.showNodeExpanded(viewer, (BMNode) o);
    // }
    // }
    //
    // private BMNode uniteBookmarksWithNewName(BMNode root)
    // throws JavaModelException
    // {
    // BMNode bookmark = TreeUtil.makeBookmarkNode();
    //
    // String newBookmarkName = mainPage.getNameOfNewBookmarkName();
    // bookmark.setValue(newBookmarkName);
    //
    // LinkedList<BMNode> leafs = TreeUtil.getLeafs(root);
    //
    // for (BMNode node : leafs) {
    //
    // TreePath path = new TreePath(new Object[] { node.getValue() });
    // TreeUtil.addNodesToExistingBookmark(bookmark, path);
    // }
    //
    // root.removeAllChildren();
    // root.addChild(bookmark);
    //
    // return root;
    // }
    //
    // private void importAll(File file)
    // throws JavaModelException
    // {
    // String[] data = BookmarkFileIO.readFromFile(file);
    //
    // if (data.length < 1) {
    // return;
    // }
    //
    // String serializedTree = data[0];
    // RestoredTree deserialized = TreeDeserializerFacade
    // .deserialize(serializedTree);
    //
    // BMNode root = deserialized.getRoot();
    //
    // if (mainPage.consolidateBookmarksAsSingleBookmark()) {
    // root = uniteBookmarksWithNewName(root);
    // }
    //
    // addBookmarksToCurrentModel(root);
    //
    // BMNode[] expanded = TreeUtil.getTreeBelowNode(root);
    // updateView(expanded);
    //
    // }
    //
    // private void addBookmarksToCurrentModel(BMNode root)
    // {
    // ViewManager manager = Activator.getManager();
    // TreeModel model = manager.getModel();
    // for (BMNode child : root.getChildren()) {
    // model.getModelRoot().addChild(child);
    // }
    // }
    //
    // private void updateView(BMNode[] expanded)
    // {
    // ViewManager manager = Activator.getManager();
    // BookmarkView bmView = manager.getActiveBookmarkView();
    //
    // if (manager.isViewFlattened()) {
    // updateFlatView(expanded);
    // }
    // else {
    //
    // for (BMNode expand : expanded) {
    // TreeUtil.showNodeExpanded(bmView.getView(), expand);
    // }
    // }
    //
    // bmView.updateControls();
    //
    // }
    //
    // private void updateFlatView(BMNode[] expanded)
    // {
    // ViewManager manager = Activator.getManager();
    // TreeModel model = manager.getModel();
    // BMNode head = model.getModelHead();
    // ExpandedStorage storage = manager.getExpandedStorage();
    //
    // manager.activateFlattenedModus(head);
    //
    // // there won't be any expanded nodes in the flat view, place the
    // // restored-expanded nodes in the storage for a later switch-back to the
    // // tree-representation
    // for (BMNode expand : expanded) {
    // storage.addNodeToExpandedStorage(expand);
    // }
    // }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("File Import Wizard");
        setNeedsProgressMonitor(true);
        mainPage = new BookmarkImportWizardPage("Import Bookmarks");
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

}
