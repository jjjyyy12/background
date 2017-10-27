package auth.background.dto;


public class UserDto {
	private String Id;
     /// <summary>
     /// 用户名
     /// </summary>
 
	private String UserName;

     /// <summary>
     /// 密码
     /// </summary>
     private String Password;

     /// <summary>
     /// 用户姓名
     /// </summary>
     private String Name;

     /// <summary>
     /// 邮箱地址
     /// </summary>
     private String Email;

     /// <summary>
     /// 手机号
     /// </summary>
     private String MobileNumber;

     /// <summary>
     /// 备注
     /// </summary>
     private String Remarks;

     /// <summary>
     /// 创建人
     /// </summary>
     private String CreateUserId;
     /// <summary>
     /// 创建人
     /// </summary>
     private String CreateUserName;
     /// <summary>
     /// 创建时间
     /// </summary>
     private String CreateTime;

     /// <summary>
     /// 上次登录时间
     /// </summary>
     private String LastLoginTime;

     /// <summary>
     /// 登录次数
     /// </summary>
     private int LoginTimes;

     /// <summary>
     /// 部门ID
     /// </summary>
     private String DepartmentId;
     ///部门
     private String DepartmentName;
     /// <summary>
     /// 是否已删除
     /// </summary>
     private int IsDeleted;

     
     
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getMobileNumber() {
		return MobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		MobileNumber = mobileNumber;
	}

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
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

	public String getLastLoginTime() {
		return LastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		LastLoginTime = lastLoginTime;
	}

	public int getLoginTimes() {
		return LoginTimes;
	}

	public void setLoginTimes(int loginTimes) {
		LoginTimes = loginTimes;
	}

	public String getDepartmentId() {
		return DepartmentId;
	}

	public void setDepartmentId(String departmentId) {
		DepartmentId = departmentId;
	}

	public int getIsDeleted() {
		return IsDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		IsDeleted = isDeleted;
	}

	public String getDepartmentName() {
		return DepartmentName;
	}

	public void setDepartmentName(String departmentName) {
		DepartmentName = departmentName;
	}
}
