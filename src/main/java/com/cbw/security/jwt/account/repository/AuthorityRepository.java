package com.cbw.security.jwt.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cbw.security.jwt.account.domain.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
