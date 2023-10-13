package com.cbw.security.jwt.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
 

/* 권한테이블 */

@Entity
@Table(name ="authority")
@Getter
@NoArgsConstructor
public class Authority {
	
	@Id
	@Column(name ="authority_name", length = 127)
	private String authorityName;
	
	@Builder
	public Authority(String authorityName) {
		this.authorityName= authorityName;
	}
	

}
