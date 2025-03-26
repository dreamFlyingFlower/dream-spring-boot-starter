package dream.flying.flower.autoconfigure.security.entity;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录用户信息
 * 
 * @author 飞花梦影
 * @date 2023-08-08 13:41:46
 * @git {@link https://gitee.com/dreamFlyingFlower}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	private String password;

	private String realName;

	private String avatar;

	private Integer gender;

	private String email;

	private String mobile;

	private Long orgId;

	private Integer status;

	private Integer superAdmin;

	/**
	 * 数据权限范围
	 * <p>
	 * null：表示全部数据权限
	 */
	private List<Long> dataScopeList;

	/**
	 * 帐户是否过期
	 */
	@Builder.Default
	private boolean isAccountNonExpired = true;

	/**
	 * 帐户是否被锁定
	 */
	@Builder.Default
	private boolean isAccountNonLocked = true;

	/**
	 * 密码是否过期
	 */
	@Builder.Default
	private boolean isCredentialsNonExpired = true;

	/**
	 * 帐户是否可用
	 */
	@Builder.Default
	private boolean isEnabled = true;

	/**
	 * 拥有权限集合
	 */
	private Set<String> authoritySet;

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authoritySet.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.isAccountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.isAccountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.isCredentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}
}