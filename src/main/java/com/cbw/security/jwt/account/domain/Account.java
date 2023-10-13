package com.cbw.security.jwt.account.domain;

import java.util.Set;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 사용자계정 테이블 */

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor
public class Account {
	
	@Id
	@Column(name = "account_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "username", length = 127)
	private String username;
	
	@Column(name = "password", length = 255)
	private String password;
	
	@Column(name = "token_weight")
	private Long tokenWeight;   // 리프레시 토큰 가중치, 리프레시 토큰안에 기입된 가중치가 tokenWeight 보다 작을경우 해당 토큰은 유효하지 않음
	
	@Column(name = "nickname", length = 127)
	private String nickname;
	
	@Column(name = "activated")
	private boolean activated;
	
	@Column(name = "refresh_token", length = 255)
	private String refreshToken;
	
	@ManyToMany
	@JoinTable(   // JoinTable은 테이블과 테이블 사이에 별도의 조인 테이블을 만들어 양 테이블간의 연관관계를 설정 하는 방법
			name = "account_authority",
			joinColumns = {@JoinColumn(name = "account_id" , referencedColumnName = "account_id")},  //referencedColumnName 기본은 pk컬럼을 참조한다, pk컬럼이 아닌 컬럼을 참조할때 해당참조컬럼을 지정한다.
			inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
	private Set<Authority> authorities;
	
	@Builder
	public Account(String username, String password, String nickname, Set<Authority> authorities, boolean activated) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.authorities = authorities;
		this.activated = activated;
		this.tokenWeight = 1L;  // 초기 가중치는 1
	}
	
	public void increaseTokenWeight() {
		this.tokenWeight++;
	}
	
	public void updateRefreshToken(String refreshToken,Long tokenWeight) {
		this.refreshToken = refreshToken;
		this.tokenWeight = tokenWeight;
	}
}
