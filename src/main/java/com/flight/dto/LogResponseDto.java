/**
 * 
 */
package com.flight.dto;

/**
 * @author Vincent
 *
 */
public class LogResponseDto {
	String jwt;

	/**
	 * @return the jwt
	 */
	public String getJwt() {
		return jwt;
	}

	/**
	 * @param jwt the jwt to set
	 */
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	/**
	 * 
	 */
	public LogResponseDto() {
		super();
	}

	/**
	 * @param jwt
	 */
	public LogResponseDto(String jwt) {
		super();
		this.jwt = jwt;
	}
}
