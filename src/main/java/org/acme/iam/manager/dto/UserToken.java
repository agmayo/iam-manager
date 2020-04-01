package org.acme.iam.manager.dto;

/**
 * UserToken
 */
public class UserToken {

    private String accessToken;
    private int accessTokenExpiration;
    private int refreshTokenExpiration;
    private String refreshToken;

    public UserToken() {
        super();
    }
	public UserToken(String accessToken, int accessTokenExpiration, int refreshTokenExpiration, String refreshToken) {
		this.accessToken = accessToken;
		this.accessTokenExpiration = accessTokenExpiration;
		this.refreshTokenExpiration = refreshTokenExpiration;
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public int getAccessTokenExpiration() {
		return accessTokenExpiration;
	}

	public void setAccessTokenExpiration(int accessTokenExpiration) {
		this.accessTokenExpiration = accessTokenExpiration;
	}

	public int getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}

	public void setRefreshTokenExpiration(int refreshTokenExpiration) {
		this.refreshTokenExpiration = refreshTokenExpiration;
	}
    
}