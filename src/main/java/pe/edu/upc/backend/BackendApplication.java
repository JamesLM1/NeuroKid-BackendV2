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
            PsicologoRepository psicologoRepository
    ){
        return args->{

            System.out.println("🚀 ========================================");
            System.out.println("🚀 INICIANDO DATA SEEDING - NEUROKID");
            System.out.println("🚀 ========================================");

            // ===================================================
            // 1. CREACIÓN DE AUTORIDADES/ROLES
            // ===================================================
            if (authorityService.findAll().isEmpty()) {
                System.out.println("📋 Creando roles...");
            Authority adminAuth = authorityService.add(new Authority(null,"ROLE_ADMIN",null));
            Authority psicologoAuth = authorityService.add(new Authority(null,"ROLE_PSICOLOGO",null));
            Authority padreAuth = authorityService.add(new Authority(null,"ROLE_PADRE",null));
                System.out.println("✅ Roles creados: ADMIN, PSICOLOGO, PADRE");
            } else {
                System.out.println("ℹ️  Roles ya existen, saltando...");
            }

            // ===================================================
            // 2. CREACIÓN DE USUARIOS BÁSICOS
            // ===================================================
            if (userService.findAll().size() < 3) {
                System.out.println("👤 Creando usuarios básicos...");

                // Administrador
                userService.add(new DTOUser(null,"admin.neuro@gmail.com","admin123","ROLE_ADMIN"));
                System.out.println("  ✅ Admin: admin.neuro@gmail.com / admin123");

                // Psicóloga
                userService.add(new DTOUser(null,"ana.psico@gmail.com","psico123","ROLE_PSICOLOGO"));
                System.out.println("  ✅ Psicóloga: ana.psico@gmail.com / psico123");

                // Padre
                userService.add(new DTOUser(null,"maria.padre@gmail.com","padre123","ROLE_PADRE"));
                System.out.println("  ✅ Padre: maria.padre@gmail.com / padre123");

            } else {
                System.out.println("ℹ️  Usuarios ya existen, saltando...");
            }

            // ===================================================
            // 3. CREACIÓN DE PERFILES BASE (PADRE Y PSICÓLOGO)
            // ===================================================
            
            // 3.1 Crear Perfil de Padre (para maria.padre@gmail.com)
            if (padreRepository.count() == 0) {
                System.out.println("👨‍👩‍👧 Creando perfil de Padre...");
                Padre padre1 = new Padre();
                padre1.setNombre("María");
                padre1.setApellido("González");
                padre1.setTipoDocumento("DNI");
                padre1.setDni("12345678");
                padre1.setEmail("maria.padre@gmail.com");
                padre1.setTelefono("987654321");
                padre1.setTipoParentesco("Madre");
                padre1.setFechaRegistro(new Date());
                padre1 = padreRepository.save(padre1);
                System.out.println("  ✅ Padre creado: María González (ID: " + padre1.getPadreId() + ")");
            } else {
                System.out.println("ℹ️  Padre ya existe");
            }

            // 3.2 Crear Perfil de Psicólogo (para ana.psico@gmail.com)
            if (psicologoRepository.count() == 0) {
                System.out.println("🧠 Creando perfil de Psicóloga...");
                Psicologo psicologo1 = new Psicologo();
                psicologo1.setNombre("Ana");
                psicologo1.setApellido("Torres");
                psicologo1.setDni("87654321");
                psicologo1.setEspecialidad("Neuropsicología Infantil");
                psicologo1.setEmail("ana.psico@gmail.com");
                psicologo1.setTelefono("912345678");
                psicologo1.setFechaRegistro(new Date());
                psicologo1 = psicologoRepository.save(psicologo1);
                System.out.println("  ✅ Psicóloga creada: Ana Torres - " + psicologo1.getEspecialidad() + " (ID: " + psicologo1.getPsicologoId() + ")");
            } else {
                System.out.println("ℹ️  Psicóloga ya existe");
            }


            System.out.println("🎉 ========================================");
            System.out.println("🎉 DATA SEEDING BÁSICO COMPLETADO");
            System.out.println("🎉 ========================================");
            System.out.println("");
            System.out.println("📊 RESUMEN DE DATOS CREADOS:");
            System.out.println("   - Roles: ADMIN, PSICOLOGO, PADRE");
            System.out.println("   - Usuarios: " + userService.findAll().size());
            System.out.println("   - Padres: " + padreRepository.count());
            System.out.println("   - Psicólogos: " + psicologoRepository.count());
            System.out.println("");
            System.out.println("🔐 CREDENCIALES DE ACCESO:");
            System.out.println("   👨‍💼 Admin: admin.neuro@gmail.com / admin123");
            System.out.println("   🧠 Psicóloga: ana.psico@gmail.com / psico123");
            System.out.println("   👨‍👩‍👧 Padre: maria.padre@gmail.com / padre123");
            System.out.println("");
            System.out.println("ℹ️  NOTA: Sistema limpio - Bandejas vacías listas para pruebas funcionales");
            System.out.println("========================================");
        };
    }
}
