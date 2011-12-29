package org.eclipselabs.recommenders.bookmark.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class BookmarkPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{

	public BookmarkPreferencePage()
	{
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Bookmark++ Preferences");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		// addField(new DirectoryFieldEditor(PreferenceConstants.P_PATH,
		// "&Directory preference:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				PreferenceConstants.REMOVE_DEAD_BOOKMARK_REFERENCES_VIEW_OPENING,
				"&automatically remove bookmarks that are referencing to non available projects when the view is opened",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor(
				PreferenceConstants.REMOVE_DEAD_BOOKMARK_REFERENCES_REFRESH,
				"&Refreshing removes dead links to non available projects",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor(
				PreferenceConstants.CLOSE_ALL_OPEN_EDITOR_WINDOWS_IF_BOOKMARK_CATEGORY_IS_OPENED,
				"&Close all open editor windows when a bookmark category is opened",
				getFieldEditorParent()));

		// addField(new RadioGroupFieldEditor(
		// PreferenceConstants.P_CHOICE,
		// "An example of a multiple-choice preference",
		// 1,
		// new String[][] { { "&Choice 1", "choice1" }, {
		// "C&hoice 2", "choice2" }
		// }, getFieldEditorParent()));
		// addField(
		// new StringFieldEditor(PreferenceConstants.P_STRING,
		// "A &text preference:", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

}