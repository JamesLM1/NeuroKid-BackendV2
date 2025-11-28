package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.ADMINPsicologoDTO;
import pe.edu.upc.backend.dtosTF.DTOUser;
import pe.edu.upc.backend.entitiesTF.Psicologo;
import pe.edu.upc.backend.entitiesTF.User;
import pe.edu.upc.backend.repositoriesTF.PsicologoRepository;
import pe.edu.upc.backend.repositoriesTF.UserRepository;
import pe.edu.upc.backend.servicesTF.ADMINPsicologoService;
import pe.edu.upc.backend.servicesTF.UserService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ADMINPsicologoServiceImpl implements ADMINPsicologoService {

    @Autowired
    private PsicologoRepository psicologoRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Conversión Entity <-> DTO (COMPLETADO) ---

    private Psicologo convertToEntity(ADMINPsicologoDTO dto) {
        Psicologo entity = new Psicologo();
        // Mapeo manual de campos del DTO a la Entidad
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setTipoDocumento(dto.getTipoDocumento()); // AGREGADO: mapear tipoDocumento
        entity.setDni(dto.getDni());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setEspecialidad(dto.getEspecialidad());
        entity.setClaveVisible(dto.getClaveVisible()); // AGREGADO: mapear claveVisible
        // El ID no se mapea en la creación
        return entity;
    }

    private ADMINPsicologoDTO convertToDTO(Psicologo entity) {
        ADMINPsicologoDTO dto = new ADMINPsicologoDTO();
        // Mapeo manual de campos de la Entidad al DTO
        dto.setPsicologoId(entity.getPsicologoId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setTipoDocumento(entity.getTipoDocumento()); // AGREGADO: mapear tipoDocumento
        dto.setDni(entity.getDni());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setEspecialidad(entity.getEspecialidad());
        dto.setClaveVisible(entity.getClaveVisible()); // AGREGADO: mapear claveVisible
        dto.setFechaRegistro(entity.getFechaRegistro()); // Asumido
        
        // AGREGADO: mapear estado del usuario
        User usuario = userRepository.findByUsername(entity.getEmail());
        dto.setUsuarioActivo(usuario != null ? usuario.isEnabled() : true);
        
        return dto;
    }

    // --- Implementación de CRUD ---

    @Override
    public ADMINPsicologoDTO crearPsicologo(ADMINPsicologoDTO ADMINPsicologoDTO) {
        Psicologo psicologo = convertToEntity(ADMINPsicologoDTO);
        psicologo.setFechaRegistro(new Date()); // Se añade la fecha de registro
        
        // Crear usuario de login para el psicólogo
        crearUsuarioLogin(psicologo);
        
        Psicologo nuevoPsicologo = psicologoRepository.save(psicologo);
        return convertToDTO(nuevoPsicologo);
    }

    @Override
    public ADMINPsicologoDTO obtenerPsicologoPorId(Long id) {
        Psicologo psicologo = psicologoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con ID: " + id));
        return convertToDTO(psicologo);
    }

    @Override
    public List<ADMINPsicologoDTO> obtenerTodosLosPsicologos() {
        return psicologoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ADMINPsicologoDTO> buscarPsicologosPorEspecialidad(String especialidad) {
        return psicologoRepository.findByEspecialidad(especialidad).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ADMINPsicologoDTO actualizarPsicologo(Long id, ADMINPsicologoDTO ADMINPsicologoDTO) {
        // 1. Buscar la entidad existente o lanzar excepción si no se encuentra
        Psicologo psicologoExistente = psicologoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con ID: " + id));

        // 2. Aplicar las verificaciones condicionales (if) para actualizar solo los campos
        //    que no son nulos y no están en blanco en el DTO.

        // Nombre
        if (ADMINPsicologoDTO.getNombre() != null && !ADMINPsicologoDTO.getNombre().isBlank()) {
            psicologoExistente.setNombre(ADMINPsicologoDTO.getNombre());
        }

        // Apellido
        if (ADMINPsicologoDTO.getApellido() != null && !ADMINPsicologoDTO.getApellido().isBlank()) {
            psicologoExistente.setApellido(ADMINPsicologoDTO.getApellido());
        }

        // DNI
        if (ADMINPsicologoDTO.getDni() != null && !ADMINPsicologoDTO.getDni().isBlank()) {
            psicologoExistente.setDni(ADMINPsicologoDTO.getDni());
        }

        // Email
        if (ADMINPsicologoDTO.getEmail() != null && !ADMINPsicologoDTO.getEmail().isBlank()) {
            psicologoExistente.setEmail(ADMINPsicologoDTO.getEmail());
        }

        // Teléfono
        if (ADMINPsicologoDTO.getTelefono() != null && !ADMINPsicologoDTO.getTelefono().isBlank()) {
            psicologoExistente.setTelefono(ADMINPsicologoDTO.getTelefono());
        }

        // **Tipo Documento:** Verificar no null/vacio/blanco
        if (ADMINPsicologoDTO.getTipoDocumento() != null && !ADMINPsicologoDTO.getTipoDocumento().isBlank()) {
            psicologoExistente.setTipoDocumento(ADMINPsicologoDTO.getTipoDocumento());
        }

        // **Clave Visible:** Verificar no null/vacio/blanco
        if (ADMINPsicologoDTO.getClaveVisible() != null && !ADMINPsicologoDTO.getClaveVisible().isBlank()) {
            psicologoExistente.setClaveVisible(ADMINPsicologoDTO.getClaveVisible());
            // Actualizar también la contraseña del usuario
            actualizarUsuarioLogin(psicologoExistente);
        }

        // Especialidad
        // Usamos solo la verificación de 'null' si Especialidad es un objeto/enum.
        // Si fuera un String simple, se recomienda usar también !.isBlank().
        if (ADMINPsicologoDTO.getEspecialidad() != null && !ADMINPsicologoDTO.getEspecialidad().isBlank()) {
            psicologoExistente.setEspecialidad(ADMINPsicologoDTO.getEspecialidad());
        }

        // 3. Guardar la entidad actualizada
        Psicologo actualizado = psicologoRepository.save(psicologoExistente);

        // 4. Convertir a DTO y retornar
        return convertToDTO(actualizado);
    }

    @Override
    public void eliminarPsicologo(Long id) {
        // SOFT DELETE: Desactivar usuario en lugar de eliminar físicamente
        try {
            // 1. Buscar la entidad Psicólogo por ID
            Psicologo psicologo = psicologoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con ID: " + id));
            
            // 2. Buscar el usuario asociado por email
            User usuario = userRepository.findByUsername(psicologo.getEmail());
            
            if (usuario != null) {
                // 3. Desactivar el usuario (Soft Delete)
                usuario.setEnabled(false);
                userRepository.save(usuario);
                System.out.println("✅ Usuario desactivado para psicólogo: " + psicologo.getEmail());
            } else {
                System.out.println("⚠️ No se encontró usuario asociado para el psicólogo: " + psicologo.getEmail());
            }
            
            System.out.println("✅ Psicólogo desactivado exitosamente (ID: " + id + ")");
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error al desactivar psicólogo: " + e.getMessage());
            throw new RuntimeException("Error al desactivar el psicólogo", e);
        }
    }

    // Nuevo método para toggle de estado
    public ADMINPsicologoDTO toggleEstadoPsicologo(Long id) {
        try {
            // 1. Buscar la entidad Psicólogo por ID
            Psicologo psicologo = psicologoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Psicólogo no encontrado con ID: " + id));
            
            // 2. Buscar el usuario asociado por email
            User usuario = userRepository.findByUsername(psicologo.getEmail());
            
            if (usuario != null) {
                // 3. Toggle del estado del usuario
                boolean nuevoEstado = !usuario.isEnabled();
                usuario.setEnabled(nuevoEstado);
                userRepository.save(usuario);
                
                String accion = nuevoEstado ? "activado" : "desactivado";
                System.out.println("✅ Usuario " + accion + " para psicólogo: " + psicologo.getEmail());
                
                // 4. Retornar el DTO actualizado con el nuevo estado
                ADMINPsicologoDTO dto = convertToDTO(psicologo);
                dto.setUsuarioActivo(nuevoEstado);
                return dto;
            } else {
                System.out.println("⚠️ No se encontró usuario asociado para el psicólogo: " + psicologo.getEmail());
                // Si no hay usuario, asumir que está activo por defecto
                ADMINPsicologoDTO dto = convertToDTO(psicologo);
                dto.setUsuarioActivo(true);
                return dto;
            }
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error al cambiar estado del psicólogo: " + e.getMessage());
            throw new RuntimeException("Error al cambiar estado del psicólogo", e);
        }
    }

    // --- Métodos auxiliares para gestión de usuarios ---

    private void crearUsuarioLogin(Psicologo psicologo) {
        try {
            // Verificar si ya existe un usuario con ese email
            User usuarioExistente = userRepository.findByUsername(psicologo.getEmail());
            
            if (usuarioExistente == null) {
                // Crear nuevo usuario
                DTOUser nuevoUsuario = new DTOUser();
                nuevoUsuario.setUsername(psicologo.getEmail());
                nuevoUsuario.setPassword(psicologo.getClaveVisible()); // Se encriptará en el servicio
                nuevoUsuario.setAuthorities("ROLE_PSICOLOGO");
                
                userService.add(nuevoUsuario);
                System.out.println("✅ Usuario creado para psicólogo: " + psicologo.getEmail());
            } else {
                System.out.println("ℹ️ Usuario ya existe para email: " + psicologo.getEmail());
            }
        } catch (Exception e) {
            System.err.println("❌ Error al crear usuario para psicólogo: " + e.getMessage());
            // No lanzar excepción para no interrumpir la creación del psicólogo
        }
    }

    private void actualizarUsuarioLogin(Psicologo psicologo) {
        try {
            // Buscar usuario existente
            User usuarioExistente = userRepository.findByUsername(psicologo.getEmail());
            
            if (usuarioExistente != null) {
                // Actualizar contraseña
                usuarioExistente.setPassword(passwordEncoder.encode(psicologo.getClaveVisible()));
                userRepository.save(usuarioExistente);
                System.out.println("✅ Contraseña actualizada para usuario: " + psicologo.getEmail());
            } else {
                // Si no existe, crear uno nuevo
                crearUsuarioLogin(psicologo);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar usuario para psicólogo: " + e.getMessage());
        }
    }

}
