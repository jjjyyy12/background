package auth.background.dto;

public class DepartmentDto {
	private String Id;
	private String ParentId;
	private String Code;
	private String Name;
	private String Contactnumber;
	private String Manager;
	private String CreateUserId;
	private String CreateUserName;
	private String CreateTime;
	private String Remarks;
	private int IsDeleted;
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
	 
 
	public String getRemarks() {
		return Remarks;
	}
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}
	public String getParentid() {
		return ParentId;
	}
	public void setParentid(String parentid) {
		this.ParentId = parentid;
	}
	public String getContactnumber() {
		return Contactnumber;
	}
	public void setContactnumber(String contactnumber) {
		Contactnumber = contactnumber;
	}
	public String getManager() {
		return Manager;
	}
	public void setManager(String manager) {
		Manager = manager;
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
	public int getIsDeleted() {
		return IsDeleted;
	}
	public void setIsDeleted(int isDeleted) {
		IsDeleted = isDeleted;
	}
	 
	 
}
