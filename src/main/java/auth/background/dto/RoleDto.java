package auth.background.dto;

public class RoleDto {
	private String Id;
	private String Code;
	private String Name;
	private String CreateUserId;
	private String CreateUserName;
	private String CreateTime;
	private String Remarks;
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getCreateUserId() {
		return CreateUserId;
	}
	public void setCreateUserId(String createUserId) {
		CreateUserId = createUserId;
	}
	public String getCreateUserName() {
		return CreateUserName;
	}
	public void setCreateUserName(String createUserName) {
		CreateUserName = createUserName;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getRemarks() {
		return Remarks;
	}
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}
}
