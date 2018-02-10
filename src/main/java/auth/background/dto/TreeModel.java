package auth.background.dto;

public class TreeModel {
	private String Id ;

	private String Text ;

	private String Parent ;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		Text = text;
	}

	public String getParent() {
		return Parent;
	}

	public void setParent(String parent) {
		Parent = parent;
	}
}
