package pe.edu.upc.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pe.edu.upc.backend.dtosTF.DTOUser;
import pe.edu.upc.backend.entitiesTF.*;
import pe.edu.upc.backend.repositoriesTF.*;
import pe.edu.upc.backend.servicesTF.AuthorityService;
import pe.edu.upc.backend.servicesTF.UserService;

import java.util.Date;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner startConfiguration(
            UserService userService,
            AuthorityService authorityService,
            PadreRepository padreRepository,
            PsicologoRepository psicologoRepository,
            MenorRepository menorRepository,
            AsignacionRepository asignacionRepository,
            DisponibilidadHorariaRepository disponibilidadRepository,
            CitaRepository citaRepository,
            RecursoEducativoRepository recursoEducativoRepository,
            FavoritoRepository favoritoRepository
    ){
        return args->{

            // 1. CREACIÓN DE AUTORIDADES/ROLES
            Authority adminAuth = authorityService.add(new Authority(null,"ROLE_ADMIN",null));
            Authority psicologoAuth = authorityService.add(new Authority(null,"ROLE_PSICOLOGO",null));
            Authority padreAuth = authorityService.add(new Authority(null,"ROLE_PADRE",null));

            // 2. CREACIÓN DE USUARIOS DE PRUEBA

            // Administrador
            userService.add(new DTOUser(null,"admin.neuro","admin123","ROLE_ADMIN"));

            // Psicólogo
            userService.add(new DTOUser(null,"ana.psico","psico123","ROLE_PSICOLOGO"));

            // Padre/Apoderado
            userService.add(new DTOUser(null,"maria.padre","padre123","ROLE_PADRE"));

            // Usuario con múltiples roles (ejemplo)
            userService.add(new DTOUser(null,"super.user","super123","ROLE_ADMIN;ROLE_PSICOLOGO"));




        };
    }
}
