package org.acme.iam.manager.dto;

/**
 * Credential
 */
public class Credential {

	// TODO: This is probably a ENUM
	private String type;
    private String value;
    
    public Credential() {
        super();
    }

	public Credential(String type, String value) {
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
    
}