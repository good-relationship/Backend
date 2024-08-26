package capstone.relation.api.auth.jwt;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import capstone.relation.global.user.UserInfo;
import capstone.relation.user.domain.Role;

public class SecurityUser implements UserDetails, UserInfo {
	private final Long userId;
	private final Role role;

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "SecurityUser{" + "memberId=" + userId + ", role=" + role + '}';
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getKey());
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(authority);
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return String.valueOf(userId);
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	public SecurityUser(Long userId, Role role) {
		this.userId = userId;
		this.role = role;
	}

	public static SecurityUser of(Long memberId, String role) {
		return new SecurityUser(memberId, Role.valueOf(role));
	}
}
