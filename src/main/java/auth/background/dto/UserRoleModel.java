package auth.background.dto;

public class UserRoleModel {
	private String userRoleId;
	private String roleIDs;
	public String getRoleIDs() {
		return roleIDs;
	}
	public void setRoleIDs(String roleIDs) {
		this.roleIDs = roleIDs;
	}
	public String getUserRoleId() {
		return userRoleId;
	}
	public void setUserRoleId(String userRoleId) {
		this.userRoleId = userRoleId;
	}
}
