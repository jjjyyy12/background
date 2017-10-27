package auth.background.dto;

public class ResetPasswordModel {
	private String resetPasswordId;
	private String OldPassword;
	private String NewPassword;
	private String NewPassword2;
	public String getResetPasswordId() {
		return resetPasswordId;
	}
	public void setResetPasswordId(String resetPasswordId) {
		this.resetPasswordId = resetPasswordId;
	}
	public String getOldPassword() {
		return OldPassword;
	}
	public void setOldPassword(String oldPassword) {
		OldPassword = oldPassword;
	}
	public String getNewPassword() {
		return NewPassword;
	}
	public void setNewPassword(String newPassword) {
		NewPassword = newPassword;
	}
	public String getNewPassword2() {
		return NewPassword2;
	}
	public void setNewPassword2(String newPassword2) {
		NewPassword2 = newPassword2;
	}
	
}
