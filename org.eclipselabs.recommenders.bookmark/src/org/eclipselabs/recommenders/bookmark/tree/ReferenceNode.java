package org.eclipselabs.recommenders.bookmark.tree;

public class ReferenceNode extends TreeNode {
	
	private String project;
	private String name;


	public ReferenceNode(String text) {
		super(text);
		this.project = extractProject(text);
		this.name = extractName(text);
	}
	
	public String getProjectName() {
		return project;
	}
	
	private String extractProject(String text) {
		int posFirstSlash = text.indexOf("/");
		int posSecondSlash = 0;
		if (posFirstSlash > -1)
			posSecondSlash = text.indexOf("/", posFirstSlash + 1);
		else
			return text;

		if (posSecondSlash > -1) {
			return text.substring(posFirstSlash + 1, posSecondSlash
					- posFirstSlash);
		}
		return text;
	}
	
	private String extractName(String text) {
		int posLastSlash = text.lastIndexOf("/");
		int posDot = text.lastIndexOf(".");

		if (posDot == -1 || posLastSlash == -1)
			return text;

		String name = text.substring(posLastSlash + 1);

		return name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return getName();
	}
	

}
