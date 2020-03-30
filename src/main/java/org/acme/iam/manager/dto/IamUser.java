package org.acme.iam.manager.dto;

import java.util.List;

/**
 * IamUser
 */
public class IamUser {

    private String id;
    private String createdTimestamp;
    private String username;
    private boolean enabled;
    private boolean totp;
    private boolean emailVerified;
    private String firstName;
    private String lastName;
    private String email;
    private List<?> disableableCredentialTypes;
    private List<?> requiredActions;
    private int notBefore;
    private IamAccess access;

    public IamUser() {
        super();
    }

	public IamUser(String id, String createdTimestamp, String username, boolean enabled, boolean totp,
			boolean emailVerified, String firstName, String lastName, String email, List<?> disableableCredentialTypes,
			List<?> requiredActions, int notBefore, IamAccess access) {
		this.id = id;
		this.createdTimestamp = createdTimestamp;
		this.username = username;
		this.enabled = enabled;
		this.totp = totp;
		this.emailVerified = emailVerified;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.disableableCredentialTypes = disableableCredentialTypes;
		this.requiredActions = requiredActions;
		this.notBefore = notBefore;
		this.access = access;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(String createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isTotp() {
		return totp;
	}

	public void setTotp(boolean totp) {
		this.totp = totp;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<?> getDisableableCredentialTypes() {
		return disableableCredentialTypes;
	}

	public void setDisableableCredentialTypes(List<?> disableableCredentialTypes) {
		this.disableableCredentialTypes = disableableCredentialTypes;
	}

	public List<?> getRequiredActions() {
		return requiredActions;
	}

	public void setRequiredActions(List<?> requiredActions) {
		this.requiredActions = requiredActions;
	}

	public int getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(int notBefore) {
		this.notBefore = notBefore;
	}

	public IamAccess getAccess() {
		return access;
	}

	public void setAccess(IamAccess access) {
		this.access = access;
	}
    
    

    


}