package org.acme.iam.manager.dto;

/**
 * Credential
 */
public class Credential {

	// TODO: This is probably a ENUM
	private String type;
	private String value;
	private boolean temporary;
    
    public Credential() {
        super();
    }

	public Credential(String type, String value, boolean temporary) {
		this.type = type;
		this.value = value;
		this.temporary = temporary;
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

	public boolean isTemporary() {
		return temporary;
	}

	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
	}
    
}