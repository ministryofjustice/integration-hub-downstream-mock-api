package uk.gov.justice.digital.hmpps.templatepackagename.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableMethodSecurity
class SecurityConfiguration(
  @param:Value("\${DOWNSTREAM_MOCK_BASIC_AUTH_USERNAME:orchestration-client}") private val username: String,
  @param:Value("\${DOWNSTREAM_MOCK_BASIC_AUTH_PASSWORD:orchestration-secret}") private val password: String,
) {
  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
    .csrf { it.disable() }
    .formLogin { it.disable() }
    .httpBasic(withDefaults())
    .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
    .authorizeHttpRequests {
      it.requestMatchers("/health/**", "/info", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs", "/v3/api-docs/**").permitAll()
      it.anyRequest().authenticated()
    }
    .build()

  @Bean
  fun userDetailsService(): UserDetailsService = InMemoryUserDetailsManager(
    User.withUsername(username)
      .password("{noop}$password")
      .roles("ORCHESTRATION_CLIENT")
      .build(),
  )
}
