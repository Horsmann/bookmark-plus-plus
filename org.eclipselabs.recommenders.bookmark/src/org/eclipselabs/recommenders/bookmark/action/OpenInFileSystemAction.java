package org.eclipselabs.recommenders.bookmark.action;

import java.io.File;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.program.Program;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.IsResourceAvailableVisitor;

import com.google.common.base.Optional;

public class OpenInFileSystemAction extends Action implements SelfEnabling {

    private final RepresentationSwitchableTreeViewer treeViewer;

    public OpenInFileSystemAction(RepresentationSwitchableTreeViewer treeViewer) {
        this.treeViewer = treeViewer;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry()
                .getDescriptor(Activator.ICON_OPEN_IN_SYSTEM_EXPLORER));
        this.setToolTipText("Opens a window in the OS file explorer to the selected objects");
        this.setText("Show in Explorer");
        this.setEnabled(false);
    }

    @Override
    public void run() {
        IStructuredSelection selections = treeViewer.getSelections();
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (isValid(next)) {
                IBookmarkModelComponent component = (IBookmarkModelComponent) next;
                boolean isAvailable = isAvailable(component);
                Optional<File> file = getFileSystemLocation(component);
                if (isAvailable && file.isPresent()) {
                    Program.launch(file.get().getAbsolutePath());
                }
            }
        }
    }

    private Optional<File> getFileSystemLocation(IBookmarkModelComponent component) {
        GetFileinFileSystemVisitor visitor = new GetFileinFileSystemVisitor();
        component.accept(visitor);
        return visitor.getFile();
    }

    private boolean isAvailable(IBookmarkModelComponent component) {
        IsResourceAvailableVisitor available = new IsResourceAvailableVisitor();
        component.accept(available);
        return available.isAvailable();
    }

    private boolean isValid(Object next) {
        return next instanceof IBookmarkModelComponent;
    }

    @Override
    public void updateEnableStatus() {
        IStructuredSelection selections = treeViewer.getSelections();
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (isValid(next)) {
                IBookmarkModelComponent component = (IBookmarkModelComponent) next;
                Optional<File> file = getFileSystemLocation(component);
                if (file.isPresent()) {
                    setEnabled(true);
                    return;
                }
            }
        }
        setEnabled(false);
    }

    private class GetFileinFileSystemVisitor implements IModelVisitor {

        private Optional<File> file = Optional.absent();

        public Optional<File> getFile() {
            return file;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
            if (fileBookmark.isInWorkspace()) {
                file = getParentOfInternalFile(fileBookmark.getFile());
            } else {
                file = getParentOfExternalFile(fileBookmark.getFile());
            }
        }

        private Optional<File> getParentOfExternalFile(IFile iFile) {
            File bookmarkedFile = iFile.getFullPath().toFile();
            File parent = bookmarkedFile.getParentFile();
            if (parent != null) {
                return Optional.of(parent);
            }
            return Optional.absent();
        }

        private Optional<File> getParentOfInternalFile(IFile iFile) {
            IPath location = iFile.getRawLocation();
            if (location == null) {
                return Optional.absent();
            }
            File bookmarkedFile = location.toFile();
            File parent = bookmarkedFile.getParentFile();
            if (parent != null) {
                return Optional.of(parent);
            }
            return Optional.absent();
        }

        @Override
        public void visit(Category category) {
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {

            IsResourceAvailableVisitor availVisitor = new IsResourceAvailableVisitor();
            javaElementBookmark.accept(availVisitor);
            if (!availVisitor.isAvailable()) {
                return;
            }
            IResource resource = getResource(javaElementBookmark.getJavaElement());
            file = Optional.of(resource.getParent().getRawLocation().toFile());
        }

        private IResource getResource(IJavaElement element) {
            IResource resource = null;
            try {
                resource = element.getCorrespondingResource();
                if (resource != null) {
                    return resource;
                }
                IJavaElement compUnit = element;
                while (hasParent(compUnit) && notInstanceOfICompilationUnit(compUnit)) {
                    compUnit = compUnit.getParent();
                }
                resource = compUnit.getCorrespondingResource();
            } catch (JavaModelException e) {
                e.printStackTrace();
            }

            return resource;
        }

        private boolean notInstanceOfICompilationUnit(IJavaElement ele) {
            return !(ele instanceof ICompilationUnit);
        }

        private boolean hasParent(IJavaElement ele) {
            return ele.getParent() != null;
        }

    }
}
