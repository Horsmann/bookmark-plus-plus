package org.eclipselabs.recommenders.bookmark.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.Activator;

public class BookmarkPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public BookmarkPreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("Bookmark++ Preferences");
    }

    public void createFieldEditors() {
        addField(new BooleanFieldEditor(
                PreferenceConstants.CLOSE_ALL_OPEN_EDITOR_WINDOWS_IF_BOOKMARK_CATEGORY_IS_OPENED,
                "&Close all open editor windows when a bookmark category is opened", getFieldEditorParent()));
    }

    public void init(IWorkbench workbench) {
    }

}