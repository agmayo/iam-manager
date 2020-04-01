package org.acme.iam.manager.dto;

import javax.json.bind.annotation.JsonbProperty;

/**
 * TokenData POJO Plain Old Java Object.
 */
public class TokenData {
    @JsonbProperty("access_token")
    private String accessToken;
    @JsonbProperty("expires_in")
    private int expiresIn;
    @JsonbProperty("refresh_token")
	private String refreshToken;
	@JsonbProperty("refresh_expires_in")
    private int refreshExpiration;
    // TODO: probably an enum.
    @JsonbProperty("token_type")
    private String tokenType;
    // TODO: say what?
    @JsonbProperty("not-before-policy")
    private int notBeforePolicy;
    @JsonbProperty("session_state")
    private String sessionState;
    private String scope;

    public TokenData() {
        super();
    }

	public TokenData(String accessToken, int expiresIn, String refreshToken, String tokenType, int notBeforePolicy,
			String sessionState, String scope, int refreshExpiration) {
		this.accessToken = accessToken;
		this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;
		this.tokenType = tokenType;
		this.notBeforePolicy = notBeforePolicy;
		this.sessionState = sessionState;
		this.scope = scope;
		this.refreshExpiration= refreshExpiration;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public int getNotBeforePolicy() {
		return notBeforePolicy;
	}

	public void setNotBeforePolicy(int notBeforePolicy) {
		this.notBeforePolicy = notBeforePolicy;
	}

	public String getSessionState() {
		return sessionState;
	}

	public void setSessionState(String sessionState) {
		this.sessionState = sessionState;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public int getRefreshExpiration() {
		return refreshExpiration;
	}

	public void setRefreshExpiration(int refreshExpiration) {
		this.refreshExpiration = refreshExpiration;
	}

    
}