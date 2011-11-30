package org.eclipselabs.recommenders.bookmark.view.actions;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.program.Program;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.util.ResourceAvailabilityValidator;

public class OpenFileInSystemExplorerAction extends Action {

	private TreeViewer viewer = null;

	public OpenFileInSystemExplorerAction(TreeViewer viewer) {
		this.viewer = viewer;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_OPEN_IN_SYSTEM_EXPLORER));
		this.setToolTipText("Opens a window in the OS file explorer to the selected objects");
		this.setText("Show in Explorer");
	}

	@Override
	public void run() {
		List<IStructuredSelection> selectedList = TreeUtil
				.getTreeSelections(viewer);
		for (int i = 0; i < selectedList.size(); i++) {
			TreeNode node = (TreeNode) selectedList.get(i);
			Object value = node.getValue();

			if (!ResourceAvailabilityValidator.isResourceAvailable(value)) {
				return;
			}
			
			File file = getOpenableFileHandle(value);

			if (file != null) {
				Program.launch(file.getAbsolutePath());
			}
		}
	}

	private File getOpenableFileHandle(Object value) {
		File file = null;

		if (value instanceof IFile) {
			IFile iRef = (IFile) value;
			File targetFile = iRef.getRawLocation().toFile();
			file = targetFile.getParentFile();
		}

		if (value instanceof IJavaElement) {
			IJavaElement javElement = (IJavaElement) value;
			File targetFile = null;

			IResource resource = tryToObtainTheCompilationUnit(javElement);

			if (resource == null)
				return null;

			targetFile = resource.getRawLocation().toFile();

			file = targetFile.getParentFile();
		}
		return file;
	}

	private IResource tryToObtainTheCompilationUnit(IJavaElement element) {
		IResource resource = null;
		try {
			resource = element.getCorrespondingResource();

			if (resource != null)
				return resource;

			IJavaElement compUnit = element;

			while (hasParent(compUnit)
					&& notInstanceOfICompilationUnit(compUnit)) {
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

	// private IJavaElement getElementWithOpenableFileHandle(IJavaElement
	// javValue) {
	//
	// IJavaElement compUnit = javValue;
	//
	// while (compUnit.getParent() != null
	// && !(compUnit instanceof ICompilationUnit))
	// compUnit = compUnit.getParent();
	//
	// return compUnit;
	// }

}
