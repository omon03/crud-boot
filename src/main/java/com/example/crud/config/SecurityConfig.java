package com.example.crud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.crud.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "com.example.crud")
@ToString
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final int STRENGTH = 31;

    @Autowired
    private UserServiceImpl userDetailsService;
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(STRENGTH);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
            .loginPage("/login")  // стандартная страница с формой логина от спринга
            .successHandler(loginSuccessHandler)  // логика обработки при логине
            .loginProcessingUrl("/login")  // action с формы логина
            // параметры логина и пароля с формы логина
            .usernameParameter("j_username")
            .passwordParameter("j_password")
            .permitAll();  // доступ к форме логина всем

        http.logout()
            .permitAll()  // разрешаем логаут всем
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // URL логаута
            .logoutSuccessUrl("/")  // URL при удачном логауте
            .and().csrf().disable();  // выключаем кросс доменную секьюрность (на этапе обучения не важна)

        http
            .authorizeRequests()  // делаем страницу регистрации недоступной для авторизированных пользователей
            .antMatchers("/login").anonymous()  //страница аутентификации доступна всем
            // защищенные URL
            .antMatchers("/hello-admin").access("hasAnyRole('ADMIN')")
            .antMatchers("/admin/**").access("hasAnyRole('ADMIN')")
            .antMatchers("/hello-user").access("hasAnyRole('USER')")
            .antMatchers("/user/**").authenticated();
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        // конфигурация для прохождения аутентификации
        // тут для аутентификации нам нужно чтобы юзердитейлс информацию передавал
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
