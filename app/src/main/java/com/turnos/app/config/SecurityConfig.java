package com.turnos.app.config;


import com.turnos.app.filter.JwtRequestFilter;

import io.jsonwebtoken.lang.Arrays;

import com.turnos.app.filter.ContentSecurityPolicyFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    private final JwtRequestFilter jwtRequestFilter;
    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private final UserDetailsService userDetailsService;

    private final ContentSecurityPolicyFilter cspFilter;



    public SecurityConfig(JwtRequestFilter jwtRequestFilter,@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService, ContentSecurityPolicyFilter cspFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailsService = userDetailsService;
        this.cspFilter = cspFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para uso de JWT
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("api/auth/**").anonymous() // Permitir rutas de autenticación para usuarios anónimos
                    .requestMatchers("/api/turno/sacar-turno").hasRole("USER") // Solo usuarios con el rol USER pueden sacar turnos
                    .requestMatchers(HttpMethod.POST, "/api/servicio/crear").hasRole("ADMIN") // Solo administradores pueden crear servicios
                    .requestMatchers(HttpMethod.DELETE, "/api/usuario/delete").hasRole("ADMIN") // Solo administradores pueden eliminar usuarios
                    .requestMatchers(HttpMethod.DELETE, "/api/servicio/agregar-profesional").hasRole("PROFESIONAL") // Solo profesionales pueden agregar servicios
                    .requestMatchers(HttpMethod.POST, "api/turno/crear-disponibles").hasRole("PROFESIONAL") // Solo profesionales pueden crear disponibilidad de turnos
                    .requestMatchers(HttpMethod.GET, "api/usuario/{id}").hasRole("USER") // Solo usuarios con rol USER pueden acceder a este endpoint
                    .requestMatchers(HttpMethod.GET, "api/profesional/all").anonymous() // Permitir la consulta de todos los profesionales a usuarios anónimos
                    .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class) // Añadimos el filtro JWT antes del UsernamePasswordAuthenticationFilter
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No se gestionan sesiones ya que usamos JWT
            .build();
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));  // Permitir el origen de tu frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));  
        configuration.setAllowCredentials(true); 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
/*@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS")
.allowCredentials(true);
    }
*/
     

}
