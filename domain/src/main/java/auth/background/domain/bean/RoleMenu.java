package auth.background.domain.bean;

public class RoleMenu extends RoleMenuKey {
    private String menuid1;
    private Menu menu;
    private Role role;
    public String getMenuid1() {
        return menuid1;
    }

    public void setMenuid1(String menuid1) {
        this.menuid1 = menuid1 == null ? null : menuid1.trim();
    }

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}