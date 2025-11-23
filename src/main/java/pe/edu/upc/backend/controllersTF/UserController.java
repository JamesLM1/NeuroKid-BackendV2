package pe.edu.upc.backend.controllersTF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.backend.dtosTF.DTOToken;
import pe.edu.upc.backend.dtosTF.DTOUser;
import pe.edu.upc.backend.entitiesTF.User;
import pe.edu.upc.backend.securityTF.JwtUtilService;
import pe.edu.upc.backend.securityTF.UserSecurity;
import pe.edu.upc.backend.servicesTF.UserService;

import java.util.stream.Collectors;

@CrossOrigin("*") // Permite peticiones desde cualquier origen (Frontend)
@RestController
@RequestMapping("/api/auth") // Usamos /api/auth como prefijo para Login/Register
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager; // Componente clave de Spring Security

    @Autowired
    JwtUtilService jwtUtilService; // Componente para generar el token


    // ===========================================
    // 1. REGISTRO DE USUARIO (Acceso Público)
    // URL: POST /api/auth/register
    // ===========================================

    @PostMapping("/register")
    public ResponseEntity<DTOUser> register(@RequestBody DTOUser user){
        // La lógica de codificación de contraseña y asignación de roles ocurre en UserServiceImpl
        user = userService.add(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


    // ===========================================
    // 2. LOGIN Y GENERACIÓN DE JWT (Acceso Público)
    // URL: POST /api/auth/login
    // ===========================================

    @PostMapping("/login")
    public ResponseEntity<DTOToken> login(@RequestBody User user){

        // 1. AUTENTICACIÓN: Intenta autenticar al usuario usando el AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        // 2. CARGA DE DETALLES: Carga el objeto UserSecurity desde la DB (vía UserDetailsService)
        UserSecurity userSecurity = (UserSecurity) userDetailsService.loadUserByUsername(user.getUsername());

        // 3. GENERACIÓN DE JWT: Crea el token
        String jwt = jwtUtilService.generateToken(userSecurity);

        // 4. EXTRACCIÓN DE DATOS PARA LA RESPUESTA
        Long id = userSecurity.getUser().getId();
        String authorities = userSecurity.getUser().getAuthorities().stream()
                .map(authority -> authority.getName())
                .collect(Collectors.joining(";", "", "")); // Formato del profesor

        // 5. RESPUESTA: Devuelve el token, ID y roles
        return new ResponseEntity<>(new DTOToken(jwt, id, authorities), HttpStatus.OK);
    }
}
