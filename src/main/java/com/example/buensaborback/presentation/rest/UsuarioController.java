package com.example.buensaborback.presentation.rest;

import com.example.buensaborback.bussines.service.IUsuarioService;
import com.example.buensaborback.domain.entities.Usuario;
import com.example.buensaborback.domain.entities.enums.Rol;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;
import java.util.Objects;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class UsuarioController {
    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@AuthenticationPrincipal Jwt jwt) {
        try {

            Usuario usuario = decodeToken(jwt);

            Usuario usuarioLogueado = usuarioService.login(usuario);

            return ResponseEntity.ok().body(usuarioLogueado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error en el proceso de login: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@AuthenticationPrincipal Jwt jwt) {
        try {
            Usuario usuario = decodeToken(jwt);
            Usuario usuarioRegistrado = usuarioService.register(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error en el proceso de registro: " + e.getMessage()));
        }
    }

    @GetMapping("/validar")
    public ResponseEntity<Boolean> validarExistenciaUsuario(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = decodeToken(jwt);
        boolean usuarioExistente = usuarioService.existsUsuarioByUsername(usuario.getUsername());
        System.out.println("VERIFICACION " + usuarioExistente);
        return ResponseEntity.ok(usuarioExistente);
    }

    @GetMapping("/cliente/{nombreUsuario}")
    public ResponseEntity<?> getClienteByNombreUsuario(@PathVariable String nombreUsuario) {
        try {
            return ResponseEntity.ok(usuarioService.getUsuarioByUsername(nombreUsuario).getCliente());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cliente no encontrado: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        try {
            usuarioService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al eliminar el usuario: " + e.getMessage()));
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(this.usuarioService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener los usuarios: " + e.getMessage()));
        }
    }

    private Usuario decodeToken(Jwt jwt){
        System.out.println(jwt);
        String auth0Id = jwt.getSubject();
        String username = jwt.getClaim("preferred_username");
        String email = jwt.getClaim("email");
        List<String> roles = jwt.getClaim("https://apiprueba/roles");

        // Assuming we take the first role if there are multiple
        String role = roles != null && !roles.isEmpty() ? roles.get(0) : "Cliente";

        // Map the role obtained from Auth0 to your Rol enum
        Rol userRole;
        switch (role) {
            case "Cliente":
                userRole = Rol.Cliente;
                break;
            case "Admin":
                userRole = Rol.Admin;
                break;
            default:
                userRole = Rol.Cliente; // Default to Cliente if role is unrecognized
                break;
        }

        Usuario usuario = new Usuario();
        usuario.setAuth0Id(auth0Id);
        System.out.println("EMAIL: "+ email);
        System.out.println("USERNAME: "+ username);
        if (username == null) {
            usuario.setUsername(email.split("@")[0]);
        } else {
            usuario.setUsername(username);
        }
        usuario.setEmail(email);
        usuario.setRol(userRole);
        System.out.println("USUARIO decodificado " + usuario);
        return usuario;
    }
}