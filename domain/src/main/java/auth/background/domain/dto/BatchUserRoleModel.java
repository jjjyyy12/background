package auth.background.domain.dto;

public class BatchUserRoleModel {
	private String userIDs;
	private String roleIDs;
	public String getRoleIDs() {
		return roleIDs;
	}
	public void setRoleIDs(String roleIDs) {
		this.roleIDs = roleIDs;
	}
	public String getUserIDs() {
		return userIDs;
	}
	public void setUserIDs(String userIDs) {
		this.userIDs = userIDs;
	}
}
