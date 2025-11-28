package pe.edu.upc.backend.servicesimplTF;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upc.backend.dtosTF.ADMINPadreDTO;
import pe.edu.upc.backend.dtosTF.DTOUser;
import pe.edu.upc.backend.entitiesTF.Padre;
import pe.edu.upc.backend.entitiesTF.User;
import pe.edu.upc.backend.repositoriesTF.PadreRepository;
import pe.edu.upc.backend.repositoriesTF.UserRepository;
import pe.edu.upc.backend.servicesTF.ADMINPadreService;
import pe.edu.upc.backend.servicesTF.UserService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ADMINPadreServiceImpl implements ADMINPadreService {

    @Autowired
    private PadreRepository padreRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Conversión Entity <-> DTO (COMPLETADO) ---

    private Padre convertToEntity(ADMINPadreDTO dto) {
        Padre entity = new Padre();
        // Nota: padreId solo se usa en Update
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setTipoDocumento(dto.getTipoDocumento()); // AGREGADO: mapear tipoDocumento
        entity.setDni(dto.getDni());
        entity.setEmail(dto.getEmail());
        entity.setTelefono(dto.getTelefono());
        entity.setTipoParentesco(dto.getTipoParentesco());
        entity.setClaveVisible(dto.getClaveVisible()); // AGREGADO: mapear claveVisible
        return entity;
    }

    private ADMINPadreDTO convertToDTO(Padre entity) {
        ADMINPadreDTO dto = new ADMINPadreDTO();
        dto.setPadreId(entity.getPadreId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setTipoDocumento(entity.getTipoDocumento()); // AGREGADO: mapear tipoDocumento
        dto.setDni(entity.getDni());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setTipoParentesco(entity.getTipoParentesco());
        dto.setClaveVisible(entity.getClaveVisible()); // AGREGADO: mapear claveVisible
        dto.setFechaRegistro(entity.getFechaRegistro());
        
        // AGREGADO: mapear estado del usuario
        User usuario = userRepository.findByUsername(entity.getEmail());
        dto.setUsuarioActivo(usuario != null ? usuario.isEnabled() : true);
        
        return dto;
    }

    // --- Implementación de CRUD ---

    @Override
    public ADMINPadreDTO crearPadre(ADMINPadreDTO ADMINPadreDTO) {
        Padre padre = convertToEntity(ADMINPadreDTO);
        padre.setFechaRegistro(new Date()); // Se añade la fecha de registro
        
        // Crear usuario de login para el padre
        crearUsuarioLogin(padre);
        
        Padre nuevoPadre = padreRepository.save(padre);
        return convertToDTO(nuevoPadre);
    }

    @Override
    public ADMINPadreDTO obtenerPadrePorId(Long id) {
        Padre padre = padreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + id));
        return convertToDTO(padre);
    }

    @Override
    public List<ADMINPadreDTO> obtenerTodosLosPadres() {
        return padreRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ADMINPadreDTO> buscarPadresPorNombre(String termino) {
        // Asumiendo que el repositorio tiene un metodo que acepta dos parámetros para OR.
        return padreRepository.findPorNombreOApellidoJPQL(termino, termino).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ADMINPadreDTO actualizarPadre(Long id, ADMINPadreDTO ADMINPadreDTO) {
        // 1. Buscar la entidad existente o lanzar excepción si no se encuentra
        Padre padreExistente = padreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + id));

        if (ADMINPadreDTO.getNombre() != null && !ADMINPadreDTO.getNombre().isBlank()) {

            padreExistente.setNombre(ADMINPadreDTO.getNombre());
        }

        // **Apellido:** Verificar no null/vacio/blanco
        if (ADMINPadreDTO.getApellido() != null && !ADMINPadreDTO.getApellido().isBlank()) {
            padreExistente.setApellido(ADMINPadreDTO.getApellido());
        }

        // **DNI:** Verificar no null/vacio/blanco
        if (ADMINPadreDTO.getDni() != null && !ADMINPadreDTO.getDni().isBlank()) {
            padreExistente.setDni(ADMINPadreDTO.getDni());
        }

        // **Email:** Verificar no null/vacio/blanco
        if (ADMINPadreDTO.getEmail() != null && !ADMINPadreDTO.getEmail().isBlank()) {
            padreExistente.setEmail(ADMINPadreDTO.getEmail());
        }

        // **Teléfono:** Verificar no null/vacio/blanco
        if (ADMINPadreDTO.getTelefono() != null && !ADMINPadreDTO.getTelefono().isBlank()) {
            padreExistente.setTelefono(ADMINPadreDTO.getTelefono());
        }

        // **Tipo Documento:** Verificar no null/vacio/blanco
        if (ADMINPadreDTO.getTipoDocumento() != null && !ADMINPadreDTO.getTipoDocumento().isBlank()) {
            padreExistente.setTipoDocumento(ADMINPadreDTO.getTipoDocumento());
        }

        // **Clave Visible:** Verificar no null/vacio/blanco
        if (ADMINPadreDTO.getClaveVisible() != null && !ADMINPadreDTO.getClaveVisible().isBlank()) {
            padreExistente.setClaveVisible(ADMINPadreDTO.getClaveVisible());
            // Actualizar también la contraseña del usuario
            actualizarUsuarioLogin(padreExistente);
        }

        // **Tipo Parentesco:** Solo verificar no null (asumiendo que es un enum u objeto que no tiene 'isBlank')
        // Si fuera un String, se usaría la verificación completa como en los demás campos.
        if (ADMINPadreDTO.getTipoParentesco() != null) {
            padreExistente.setTipoParentesco(ADMINPadreDTO.getTipoParentesco());
        }

        // 3. Guardar la entidad actualizada
        Padre actualizado = padreRepository.save(padreExistente);

        // 4. Convertir a DTO y retornar
        return convertToDTO(actualizado);
    }

    @Override
    public void eliminarPadre(Long id) {
        // SOFT DELETE: Desactivar usuario en lugar de eliminar físicamente
        try {
            // 1. Buscar la entidad Padre por ID
            Padre padre = padreRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + id));
            
            // 2. Buscar el usuario asociado por email
            User usuario = userRepository.findByUsername(padre.getEmail());
            
            if (usuario != null) {
                // 3. Desactivar el usuario (Soft Delete)
                usuario.setEnabled(false);
                userRepository.save(usuario);
                System.out.println("✅ Usuario desactivado para padre: " + padre.getEmail());
            } else {
                System.out.println("⚠️ No se encontró usuario asociado para el padre: " + padre.getEmail());
            }
            
            System.out.println("✅ Padre desactivado exitosamente (ID: " + id + ")");
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error al desactivar padre: " + e.getMessage());
            throw new RuntimeException("Error al desactivar el padre", e);
        }
    }

    // Nuevo método para toggle de estado
    public ADMINPadreDTO toggleEstadoPadre(Long id) {
        try {
            // 1. Buscar la entidad Padre por ID
            Padre padre = padreRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Padre no encontrado con ID: " + id));
            
            // 2. Buscar el usuario asociado por email
            User usuario = userRepository.findByUsername(padre.getEmail());
            
            if (usuario != null) {
                // 3. Toggle del estado del usuario
                boolean nuevoEstado = !usuario.isEnabled();
                usuario.setEnabled(nuevoEstado);
                userRepository.save(usuario);
                
                String accion = nuevoEstado ? "activado" : "desactivado";
                System.out.println("✅ Usuario " + accion + " para padre: " + padre.getEmail());
                
                // 4. Retornar el DTO actualizado con el nuevo estado
                ADMINPadreDTO dto = convertToDTO(padre);
                dto.setUsuarioActivo(nuevoEstado);
                return dto;
            } else {
                System.out.println("⚠️ No se encontró usuario asociado para el padre: " + padre.getEmail());
                // Si no hay usuario, asumir que está activo por defecto
                ADMINPadreDTO dto = convertToDTO(padre);
                dto.setUsuarioActivo(true);
                return dto;
            }
            
        } catch (EntityNotFoundException e) {
            System.err.println("❌ Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error al cambiar estado del padre: " + e.getMessage());
            throw new RuntimeException("Error al cambiar estado del padre", e);
        }
    }

    // --- Métodos auxiliares para gestión de usuarios ---

    private void crearUsuarioLogin(Padre padre) {
        try {
            // Verificar si ya existe un usuario con ese email
            User usuarioExistente = userRepository.findByUsername(padre.getEmail());
            
            if (usuarioExistente == null) {
                // Crear nuevo usuario
                DTOUser nuevoUsuario = new DTOUser();
                nuevoUsuario.setUsername(padre.getEmail());
                nuevoUsuario.setPassword(padre.getClaveVisible()); // Se encriptará en el servicio
                nuevoUsuario.setAuthorities("ROLE_PADRE");
                
                userService.add(nuevoUsuario);
                System.out.println("✅ Usuario creado para padre: " + padre.getEmail());
            } else {
                System.out.println("ℹ️ Usuario ya existe para email: " + padre.getEmail());
            }
        } catch (Exception e) {
            System.err.println("❌ Error al crear usuario para padre: " + e.getMessage());
            // No lanzar excepción para no interrumpir la creación del padre
        }
    }

    private void actualizarUsuarioLogin(Padre padre) {
        try {
            // Buscar usuario existente
            User usuarioExistente = userRepository.findByUsername(padre.getEmail());
            
            if (usuarioExistente != null) {
                // Actualizar contraseña
                usuarioExistente.setPassword(passwordEncoder.encode(padre.getClaveVisible()));
                userRepository.save(usuarioExistente);
                System.out.println("✅ Contraseña actualizada para usuario: " + padre.getEmail());
            } else {
                // Si no existe, crear uno nuevo
                crearUsuarioLogin(padre);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar usuario para padre: " + e.getMessage());
        }
    }

}
