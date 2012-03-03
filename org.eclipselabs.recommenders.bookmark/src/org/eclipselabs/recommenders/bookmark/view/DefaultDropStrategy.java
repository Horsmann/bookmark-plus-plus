package org.eclipselabs.recommenders.bookmark.view;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.commands.AddBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.ChangeBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.ReorderBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.ExternalFile;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class DefaultDropStrategy implements IDropStrategy {

    protected final BookmarkCommandInvoker commandInvoker;

    public DefaultDropStrategy(BookmarkCommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
    }

    @Override
    public void performDrop(DropTargetEvent event) {

        setDropOperation(event);
        final Optional<IBookmarkModelComponent> dropTarget = getDropTarget(event);
        boolean insertDropBeforeTarget = determineInsertLocation(event);

        if (event.data instanceof TreeSelection) {
            processTreeSelection(dropTarget, (TreeSelection) event.data, insertDropBeforeTarget);
        } else if (isWorkbenchInternalFileDrop(event)) {
            IFile[] ifiles = createWorkbenchInternalIFiles((String[]) event.data);
            List<TreePath> paths = Lists.newArrayList();
            for (IFile file : ifiles) {
                paths.add(new TreePath(new Object[] { file }));
            }
            TreeSelection sel = new TreeSelection(paths.toArray(new TreePath[0]));
            processTreeSelection(dropTarget, sel, insertDropBeforeTarget);
            return;
        } else if (isExternalFileDrop(event)) {
            IFile[] ifiles = createWorkbenchExternalIFiles((String[]) event.data);
            List<TreePath> paths = Lists.newArrayList();
            for (IFile file : ifiles) {
                paths.add(new TreePath(new Object[] { new ExternalFile(file) }));
            }
            TreeSelection sel = new TreeSelection(paths.toArray(new TreePath[0]));
            processTreeSelection(dropTarget, sel, insertDropBeforeTarget);
        } else {
            ISelection selections = LocalSelectionTransfer.getTransfer().getSelection();
            if (selections instanceof IStructuredSelection) {
                processStructuredSelection(dropTarget, (IStructuredSelection) selections, isCopyOperation(event),
                        insertDropBeforeTarget);
            }
        }
    }

    private IFile[] createWorkbenchExternalIFiles(String[] data) {
        List<IFile> files = Lists.newArrayList();
        for (String abs : data) {
            IPath path = new Path(abs);
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            if (!isDirectory(file)) {
                files.add(file);
            }
        }
        return files.toArray(new IFile[0]);
    }

    private boolean isDirectory(IFile ifile) {
        File file = ifile.getFullPath().toFile();
        return file.isDirectory();
    }

    private boolean isExternalFileDrop(DropTargetEvent event) {
        return isFileTransferSupported(event.dataTypes) && event.data instanceof String[];
    }

    private boolean isFileTransferSupported(TransferData[] dataTypes) {
        for (TransferData data : dataTypes) {
            if (FileTransfer.getInstance().isSupportedType(data)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWorkbenchInternalFileDrop(DropTargetEvent event) {
        return isResourceTransferSupported(event.dataTypes) && event.data instanceof String[];
    }

    private IFile[] createWorkbenchInternalIFiles(String[] data) {
        List<IFile> files = Lists.newArrayList();
        for (String s : data) {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IPath location = Path.fromOSString((String) s);
            IFile file = workspace.getRoot().getFileForLocation(location);
            files.add(file);
        }

        return files.toArray(new IFile[0]);
    }

    private boolean isResourceTransferSupported(TransferData[] dataTypes) {
        for (TransferData data : dataTypes) {
            if (ResourceTransfer.getInstance().isSupportedType(data)) {
                return true;
            }
        }
        return false;
    }

    protected void setDropOperation(DropTargetEvent event) {
        if ((event.operations & DND.DROP_MOVE) != 0) {
            event.detail = DND.DROP_MOVE;
        } else {
            event.detail = DND.DROP_COPY;
        }
    }

    protected boolean isCopyOperation(DropTargetEvent event) {
        return !((event.operations & DND.DROP_MOVE) != 0);
    }

    protected void processStructuredSelection(Optional<IBookmarkModelComponent> dropTarget,
            IStructuredSelection selections, boolean keepSource, boolean insertDropBeforeTarget) {
        IBookmarkModelComponent[] components = getModelComponentsFromSelection(selections);
        if (dropTarget.isPresent() && areBookmarksSortedByHand(dropTarget.get(), components)) {
            processReorderingofNodes(dropTarget.get(), components, insertDropBeforeTarget);
        } else {
            processDroppedElementOriginatedFromInsideTheView(dropTarget, components, keepSource, insertDropBeforeTarget);
        }
    }

    protected boolean determineInsertLocation(DropTargetEvent event) {
        Tree tree = getTreeControl(event);
        Point dropPoint = new Point(event.x, event.y);

        Point controlRelativePoint = tree.toControl(dropPoint);
        TreeItem item = tree.getItem(controlRelativePoint);
        if (item == null) {
            return false;
        }
        Rectangle bounds = item.getBounds();
        Rectangle upperHalf = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height / 2);
        return upperHalf.contains(controlRelativePoint);

    }

    private Tree getTreeControl(DropTargetEvent event) {
        DropTarget dropTargetSource = (DropTarget) event.getSource();
        Tree tree = (Tree) dropTargetSource.getControl();
        return tree;
    }

    private IBookmarkModelComponent[] getModelComponentsFromSelection(IStructuredSelection selections) {
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();

        List<IBookmarkModelComponent> items = Lists.newArrayList();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (hasEncapsulatedModelComponent(object)) {
                TreeItem item = (TreeItem) object;
                items.add((IBookmarkModelComponent) item.getData());
            }
        }
        return items.toArray(new IBookmarkModelComponent[0]);

    }

    private boolean hasEncapsulatedModelComponent(Object object) {

        if (object instanceof TreeItem) {
            if (((TreeItem) object).getData() instanceof IBookmarkModelComponent) {
                return true;
            }
        } else {
            return false;
        }

        return false;
    }

    private void processReorderingofNodes(IBookmarkModelComponent target, IBookmarkModelComponent[] components,
            boolean insertDropBeforeTarget) {
        commandInvoker.invoke(new ReorderBookmarksCommand(target, components, insertDropBeforeTarget));

    }

    private boolean areBookmarksSortedByHand(IBookmarkModelComponent target, IBookmarkModelComponent[] components) {
        for (IBookmarkModelComponent bookmark : components) {
            if (!haveSameParent(target, bookmark)) {
                return false;
            }
        }

        return true;
    }

    private boolean haveSameParent(IBookmarkModelComponent target, IBookmarkModelComponent bookmark) {

        return target.getParent() == bookmark.getParent();
    }

    protected Optional<IBookmarkModelComponent> getDropTarget(final DropTargetEvent event) {
        if (event.item == null) {
            return Optional.absent();
        } else {
            return Optional.of((IBookmarkModelComponent) event.item.getData());
        }
    }

    protected void processTreeSelection(final Optional<IBookmarkModelComponent> dropTarget,
            final TreeSelection treeSelection, boolean insertDropBeforeTarget) {
        final TreePath[] treePath = treeSelection.getPaths();

        Object[] elements = new Object[treePath.length];
        for (int i = 0; i < treePath.length; i++) {
            elements[i] = treePath[i].getLastSegment();
        }
        processDroppedElementOriginatedFromOutsideTheView(dropTarget, elements, insertDropBeforeTarget);
    }

    protected void processDroppedElementOriginatedFromOutsideTheView(
            final Optional<IBookmarkModelComponent> dropTarget, final Object[] elements, boolean isDropBeforeTarget) {
        Optional<String> categoryName = Optional.absent();
        commandInvoker.invoke(new AddBookmarksCommand(elements, commandInvoker, isDropBeforeTarget, dropTarget,
                categoryName));
    }

    protected void processDroppedElementOriginatedFromInsideTheView(Optional<IBookmarkModelComponent> dropTarget,
            IBookmarkModelComponent[] components, boolean isCopyOperation, boolean insertDropBeforeTarget) {

        if (!causeRecursion(components, dropTarget)) {
            Optional<String> categoryName = Optional.absent();
            commandInvoker.invoke(new ChangeBookmarksCommand(components, isCopyOperation, commandInvoker,
                    insertDropBeforeTarget, dropTarget, categoryName));
        }
    }

    protected boolean causeRecursion(IBookmarkModelComponent[] components, Optional<IBookmarkModelComponent> dropTarget) {

        if (!dropTarget.isPresent()) {
            return false;
        }

        for (IBookmarkModelComponent bookmark : components) {
            RecursionPreventerVisitor visitor = new RecursionPreventerVisitor(dropTarget.get());
            bookmark.accept(visitor);
            if (visitor.recursionFound) {
                return true;
            }
        }

        return false;
    }

    private class RecursionPreventerVisitor implements IModelVisitor {

        boolean recursionFound = false;
        private final IBookmarkModelComponent target;

        public RecursionPreventerVisitor(IBookmarkModelComponent target) {
            this.target = target;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {

        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            if (javaElementBookmark == target) {
                recursionFound = true;
            }
            for (IBookmark child : javaElementBookmark.getChildElements()) {
                child.accept(this);
            }

        }

    }

    @Override
    public void performDropEnter(DropTargetEvent event) {
        // will accept text but prefer to have files dropped
        for (int i = 0; i < event.dataTypes.length; i++) {
            if (FileTransfer.getInstance().isSupportedType(event.dataTypes[i])) {
                event.currentDataType = event.dataTypes[i];
                break;
            }
        }
    }

}
