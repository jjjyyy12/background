package auth.background.bean;

public class RoleMenu extends RoleMenuKey {
    private String menuid1;

    public String getMenuid1() {
        return menuid1;
    }

    public void setMenuid1(String menuid1) {
        this.menuid1 = menuid1 == null ? null : menuid1.trim();
    }
}