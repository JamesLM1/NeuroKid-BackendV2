package pe.edu.upc.backend.securityTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    // No se necesita AUTH_WHITELIST, se maneja directamente en las reglas

    @Autowired
    JwtRequestFilter jwtRequestFilter; // Filtro que procesa el JWT

    // Codificador de Contraseñas
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager (necesario para el proceso de autenticación en /login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. Añadir el filtro JWT
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // 2. Configuración CORS y CSRF
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);

        // 3. Reglas de Autorización - CONFIGURACIÓN REFORZADA PARA DELETE Y PATCH
        http.authorizeHttpRequests(auth -> auth
            // 0. REGLAS EXPLÍCITAS PARA MÉTODOS ESPECÍFICOS (Cinturón y Tirantes)
            .requestMatchers(HttpMethod.DELETE, "/api/admin/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/api/admin/**").hasRole("ADMIN")
            
            // 1. Públicos
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api/auth/**", "/api/public/**").permitAll()
            
            // 2. Roles Maestros (Acceso total a sus áreas)
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/padres/**").hasAnyRole("PADRE", "ADMIN")
            .requestMatchers("/api/psicologos/**").hasAnyRole("PSICOLOGO", "ADMIN")
            
            // 3. Endpoints Específicos (sin usar ** en el medio)
            .requestMatchers("/api/asignaciones/**").authenticated()
            .requestMatchers("/api/recursos/**").authenticated()
            
            // 4. Resto
            .anyRequest().authenticated()
        );

        // 4. Configuración de sesión (Stateless para JWT)
        http.sessionManagement(
                (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }
}
