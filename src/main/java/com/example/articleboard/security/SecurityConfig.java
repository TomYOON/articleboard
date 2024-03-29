package com.example.articleboard.security;

import com.example.articleboard.env.UriConfig;
import com.example.articleboard.security.filter.CustomAuthenticationFilter;
import com.example.articleboard.security.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(), jwtUtils);
        customAuthenticationFilter.setFilterProcessesUrl(UriConfig.LOGIN);

        http.logout()
            .logoutUrl(UriConfig.LOGOUT)
            .deleteCookies(jwtUtils.ACCESS_TOKEN_KEY, jwtUtils.REFRESH_TOKEN_KEY);

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers(UriConfig.LOGIN + "/**", UriConfig.Member.BASE + "/**").permitAll();

        //article
        http.authorizeRequests().antMatchers(UriConfig.Article.BASE + "/**").permitAll()
                                .antMatchers(GET, UriConfig.Article.BASE + "/**").permitAll()
                                .antMatchers(POST, UriConfig.Article.BASE + "/**").authenticated()
                                .antMatchers(PUT, UriConfig.Article.BASE + "/**").authenticated();

//        http.authorizeRequests().antMatchers(UriConfig.Member.BASE + "/**").hasAnyAuthority(Role.ADMIN.toString());

        http.authorizeRequests().antMatchers(DELETE, UriConfig.Comment.BASE + "/**").authenticated();
//        http.authorizeRequests().anyRequest().authenticated();

        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws  Exception {
        return super.authenticationManagerBean();
    }
}
