package com.puc.sca.api.gateway;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.puc.sca.api.gateway.filter.AcessoSeguroFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class AcessoSeguroConfigurerAdapter extends WebSecurityConfigurerAdapter {

	private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(new AntPathRequestMatcher("/public/**"));

	private static final RequestMatcher PROTECTED_URLS = new NegatedRequestMatcher(PUBLIC_URLS);

	@Value("${jwt.secret.key}")
	private String secretKey;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.sessionManagement()
		    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    .and()
		    .exceptionHandling()
			.defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), PROTECTED_URLS)
			.and()
            .addFilterBefore(authFilter(), AnonymousAuthenticationFilter.class)
            .authorizeRequests()
			.requestMatchers(PROTECTED_URLS).authenticated()
			.and()
			.csrf()
			.disable()
			.formLogin()
			.disable()
			.httpBasic()
			.disable()
			.logout()
			.disable();
	}

	@Bean
	public AcessoSeguroFilter authFilter() throws Exception {
		final AcessoSeguroFilter filter = new AcessoSeguroFilter(PROTECTED_URLS);
		filter.setAuthenticationManager(authenticationManager());
		filter.setSecretKey(this.secretKey);
		return filter;
	}

	@Bean
	public AuthenticationEntryPoint forbiddenEntryPoint() {
		return new HttpStatusEntryPoint(FORBIDDEN);
	}

}
