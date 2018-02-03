package auth.background.dto;

public class MenuDto {
	private String Id;
	private String ParentId;
	private String Code;
	private String Name;
	private String SerialNumber;
	private String Url;
	private int Type;
	private String Icon;
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
	 
	public String getUrl() {
		return Url;
	}
	public void setUrl(String url) {
		Url = url;
	}
	public int getCreateTime() {
		return Type;
	}
	public void setCreateTime(int type) {
		Type = type;
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
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	public String getIcon() {
		return Icon;
	}
	public void setIcon(String icon) {
		Icon = icon;
	}
}
