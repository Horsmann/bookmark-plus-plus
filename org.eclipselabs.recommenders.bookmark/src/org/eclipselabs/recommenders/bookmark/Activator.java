package org.eclipselabs.recommenders.bookmark;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModelCloner;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.BookmarkIO;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipselabs.recommenders.bookmark"; //$NON-NLS-1$

    public static final String fileEnding = ".bm";

    // The shared instance
    private static Activator plugin;

    private static BookmarkView bookmarkView;

    public static final String ICON_INFERRED = "inferred";
    public static final String ICON_CATEGORY = "category";
    public static final String ICON_SINGLE_BOOKMARK = "singleBookmark";
    public static final String ICON_SINGLE_BOOKMARK_OVERLAY = "singleBookmarkOverlay";
    public static final String ICON_SHOW_IN_EDITOR = "openBookmarksInEditor";
    public static final String ICON_CLOSE_ALL_OPEN_EDITORS = "closeAllOpenEditors";
    public static final String ICON_OPEN_IN_SYSTEM_EXPLORER = "openInSystemExplorer";
    public static final String ICON_TOGGLE_VIEW = "toggleLevel";
    public static final String ICON_DELETE = "delete";
    public static final String ICON_RENAME_CATEGORY = "rename";
    public static final String ICON_SWITCH_FLAT_HIERARCHY = "toggleBetweenFlatAndTreeModus";
    public static final String ICON_SAVE = "save";
    public static final String ICON_FILE_NOT_AVAILABLE = "fileNotAvailable";

    public static final String AUTOSAVE_FILE = "bookmarkModel" + fileEnding;

    private static final String GUISTATE = "mementoGUIState";

    private static BookmarkModel model;

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        loadModel();
    }

    private void loadModel() {
        try {
            model = BookmarkIO.load(getLocationForStoringBookmark());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(BundleContext context) throws Exception {
        BookmarkIO.writeModel(getLocationForStoringBookmark(), model);
        plugin = null;
        super.stop(context);
    }

    public static Activator getDefault() {
        return plugin;
    }

    public static BookmarkModel getModel() {
        return model;
    }

    public static File getLocationForStoringBookmark() {
        IPath stateLocation = Activator.getDefault().getStateLocation();
        File stateFile = stateLocation.append(Activator.AUTOSAVE_FILE).toFile();
        return stateFile;
    }

    public static File getLocationForStoringGUIState() {
        IPath stateLocation = Activator.getDefault().getStateLocation();
        File stateFile = stateLocation.append(Activator.GUISTATE).toFile();
        return stateFile;
    }

    public static BookmarkModel getClonedModel() {
        return new BookmarkModelCloner(model).clone();
    }

    public static void setBookmarkView(BookmarkView bookmarkView) {
        Activator.bookmarkView = bookmarkView;
    }

    public static BookmarkCommandInvoker getCommandInvoker() {
        return Activator.bookmarkView;
    }

    public static void setNewModel(BookmarkModel newModel) {
        BookmarkIO.writeModel(getLocationForStoringBookmark(), newModel);
        if (bookmarkView != null) {
            bookmarkView.setNewModelForTreeViewer(newModel);
            bookmarkView.resetGui();
        } else {
            model.removeAll();
            for (Category cat : newModel.getCategories()) {
                model.add(cat);
            }
        }
    }

    public static IStructuredSelection getCurrentTreeSelection() {
        return bookmarkView.getSelections();
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        super.initializeImageRegistry(registry);
        Bundle bundle = Platform.getBundle(PLUGIN_ID);

        String[][] icons = { { ICON_CATEGORY, "icons/bkmrk_nav.gif" },
                { ICON_SINGLE_BOOKMARK, "icons/bookmark_obj.gif" },
                { ICON_SINGLE_BOOKMARK_OVERLAY, "icons/bookmark_obj_ovl.gif" },
                { ICON_SHOW_IN_EDITOR, "icons/read_obj.gif" }, { ICON_CLOSE_ALL_OPEN_EDITORS, "icons/clear_co.gif" },
                { ICON_OPEN_IN_SYSTEM_EXPLORER, "icons/opentype.gif" }, { ICON_TOGGLE_VIEW, "icons/synced.gif" },
                { ICON_DELETE, "icons/delete_obj.gif" }, { ICON_RENAME_CATEGORY, "icons/write_obj.gif" },
                { ICON_SWITCH_FLAT_HIERARCHY, "icons/tree_explorer.gif" }, { ICON_SAVE, "icons/save_edit.gif" },
                { ICON_FILE_NOT_AVAILABLE, "icons/errorwarning_tab.gif" } };

        registerIcons(bundle, registry, icons);
    }

    private void registerIcons(Bundle bundle, ImageRegistry registry, String[][] icons) {
        for (String[] icon : icons) {
            ImageDescriptor imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(icon[1]), null));
            registry.put(icon[0], imgDesc);
        }
    }

}
