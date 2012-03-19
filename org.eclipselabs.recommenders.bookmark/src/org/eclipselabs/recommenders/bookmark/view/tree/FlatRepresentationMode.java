package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.List;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.tree.ITreeExpansionVisitor.Action;
import org.eclipselabs.recommenders.bookmark.visitor.IsResourceAvailableVisitor;

import com.google.common.collect.Lists;

public class FlatRepresentationMode implements IRepresentationMode {

    @Override
    public IChildrenResolverVisitor createChildrenResolverVisitor() {
        return new IChildrenResolverVisitor() {

            private List<IBookmark> childBookmarks;

            @Override
            public boolean hasChildren() {
                return !childBookmarks.isEmpty();
            }

            @Override
            public List<? extends IBookmark> getChildren() {
                return childBookmarks;
            }

            @Override
            public void visit(final FileBookmark fileBookmark) {
                if (childBookmarks == null) {
                    childBookmarks = Lists.newLinkedList();
                } else if (!fileBookmark.isInferredNode()) {
                    childBookmarks.add(fileBookmark);
                }
            }

            @Override
            public void visit(final Category category) {
                childBookmarks = Lists.newLinkedList();
                for (final IBookmark bookmark : category.getBookmarks()) {
                    bookmark.accept(this);
                }
            }

            @Override
            public void visit(final JavaElementBookmark javaElementBookmark) {
                if (childBookmarks == null) {
                    childBookmarks = Lists.newLinkedList();
                } else {
                    if (!javaElementBookmark.isInferredNode()) {
                        childBookmarks.add(javaElementBookmark);
                    }
                    visitChilds(javaElementBookmark);
                }
            }

            private void visitChilds(final JavaElementBookmark javaElementBookmark) {
                for (final JavaElementBookmark childElement : javaElementBookmark.getChildElements()) {
                    childElement.accept(this);
                }
            }

        };
    }

    @Override
    public ILabelProviderVisitor createLabelProviderVisitor() {
        return new ILabelProviderVisitor() {

            private String label;

            private final JavaElementLabelProvider jelp = new JavaElementLabelProvider(
                    JavaElementLabelProvider.SHOW_RETURN_TYPE | JavaElementLabelProvider.SHOW_SMALL_ICONS
                            | JavaElementLabelProvider.SHOW_DEFAULT);

            @Override
            public String getLabel() {
                return label;
            }

            @Override
            public void visit(final FileBookmark fileBookmark) {
                label = fileBookmark.getFile().getName();

            }

            @Override
            public void visit(final Category category) {
                label = category.getLabel();

            }

            @Override
            public void visit(final JavaElementBookmark javaElementBookmark) {
                final IJavaElement javaElement = javaElementBookmark.getJavaElement();
                String unitname = determineUnitname(javaElement);
                if (unitname.equals("")) {
                    label = jelp.getText(javaElement);
                } else {
                    label = unitname + "." + jelp.getText(javaElement);
                }
            }

            private String determineUnitname(IJavaElement element) {
                String compilationUnit = getCompilationUnitName(element);
                compilationUnit = chopOffFileEnding(compilationUnit);
                return compilationUnit;
            }

            public String getCompilationUnitName(IJavaElement element) {
                IJavaElement foundElement = seekCompilationUnitOrClassFile(element);

                if (foundElement == null || originalAndFoundElementAreTheSame(element, foundElement)) {
                    return "";
                }
                return foundElement.getElementName();
            }

            private boolean originalAndFoundElementAreTheSame(IJavaElement originalElement, IJavaElement foundElement) {
                return originalElement.getHandleIdentifier().equals(foundElement.getHandleIdentifier());
            }

            private IJavaElement seekCompilationUnitOrClassFile(IJavaElement element) {
                if (isCompilationUnitOrClassFile(element)) {
                    return element;
                }

                while ((element = element.getParent()) != null) {
                    if (isCompilationUnitOrClassFile(element)) {
                        return element;
                    }
                }

                return null;
            }

            private boolean isCompilationUnitOrClassFile(IJavaElement element) {
                return (element instanceof ICompilationUnit || element instanceof IClassFile);
            }

            private String chopOffFileEnding(String compilationUnit) {
                int pos = compilationUnit.lastIndexOf(".");
                if (pos == -1) {
                    return compilationUnit;
                }
                String name = compilationUnit.substring(0, pos);
                return name;
            }

        };
    }

    @Override
    public ITreeExpansionVisitor createTreeExpansionVisitor(final Action expansion) {
        return new ITreeExpansionVisitor() {

            @Override
            public void visit(final FileBookmark fileBookmark) {

            }

            @Override
            public void visit(final Category category) {
                category.setExpanded(expansion == Action.EXPAND);
            }

            @Override
            public void visit(final JavaElementBookmark javaElementBookmark) {

            }

        };
    }

    @Override
    public Object[] getExpandedItems(final BookmarkModel model) {
        final List<Object> expandedItems = Lists.newLinkedList();
        final IModelVisitor visitor = new IModelVisitor() {

            @Override
            public void visit(final Category category) {
                if (category.isExpanded()) {
                    expandedItems.add(category);
                }
                for (final IBookmark bookmark : category.getBookmarks()) {
                    bookmark.accept(this);
                }
            }

            @Override
            public void visit(final FileBookmark fileBookmark) {
            }

            @Override
            public void visit(final JavaElementBookmark javaElementBookmark) {
            }
        };
        for (final Category category : model.getCategories()) {
            visitor.visit(category);
        }
        return expandedItems.toArray();
    }

    @Override
    public IImageProviderVisitor createImageProviderVisitor() {
        return new IImageProviderVisitor() {
            private Image image;

            private final JavaElementLabelProvider jelp = new JavaElementLabelProvider(
                    JavaElementLabelProvider.SHOW_RETURN_TYPE | JavaElementLabelProvider.SHOW_SMALL_ICONS
                            | JavaElementLabelProvider.SHOW_DEFAULT);

            @Override
            public void visit(JavaElementBookmark javaElementBookmark) {
                final IJavaElement javaElement = JavaCore.create(javaElementBookmark.getHandleId());
                image = jelp.getImage(javaElement);
            }

            @Override
            public void visit(Category category) {
                final AbstractUIPlugin plugin = Activator.getDefault();
                final ImageRegistry registry = plugin.getImageRegistry();
                image = registry.get(Activator.ICON_CATEGORY);
            }

            @Override
            public void visit(FileBookmark fileBookmark) {
                image = jelp.getImage(fileBookmark.getFile());
                if (!fileBookmark.isInWorkspace()) {
                    updateIconIfFileNotAvailable(fileBookmark);
                }
            }

            private void updateIconIfFileNotAvailable(FileBookmark fileBookmark) {
                IsResourceAvailableVisitor availVisitor = new IsResourceAvailableVisitor();
                fileBookmark.accept(availVisitor);
                if (!availVisitor.isAvailable()) {
                    final AbstractUIPlugin plugin = Activator.getDefault();
                    final ImageRegistry registry = plugin.getImageRegistry();
                    image = registry.get(Activator.ICON_FILE_NOT_AVAILABLE);
                }
            }

            @Override
            public Image getImage() {
                return image;
            }

        };
    }

}
