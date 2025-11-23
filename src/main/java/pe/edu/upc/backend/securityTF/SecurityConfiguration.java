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


    private static final String[] AUTH_WHITELIST ={

            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",

            // -- Login y Registro
            "/api/auth/login", // Tu nueva ruta de login
            "/api/auth/register" // Ruta de registro, si la tuvieras
    };

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

        // 3. Reglas de Autorización
        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll()

                        // Rutas protegidas
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/psicologos/**").hasAnyAuthority("ROLE_PSICOLOGO", "ROLE_ADMIN")
                        .requestMatchers("/api/psicologos/**").hasAuthority("ROLE_PSICOLOGO")
                        .requestMatchers("/api/padres/**").hasAuthority("ROLE_PADRE")

                        // Final: Cualquier otra ruta debe estar autenticada
                        .anyRequest().authenticated()
        );

        http.sessionManagement(
                (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }
}
