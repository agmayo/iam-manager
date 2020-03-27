package org.acme.iam.manager.dto;

import java.util.List;

/**
 * User
 */
public class User {

    private String username;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private List<Credential> credentials;
    
    public User() {
        super();
    }

	public User(String username, String firstName, String lastName, Boolean enabled, List<Credential> credentials) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;
		this.credentials = credentials;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<Credential> getCredentials() {
		return credentials;
	}

	public void setCredentials(List<Credential> credentials) {
		this.credentials = credentials;
	}
    
}