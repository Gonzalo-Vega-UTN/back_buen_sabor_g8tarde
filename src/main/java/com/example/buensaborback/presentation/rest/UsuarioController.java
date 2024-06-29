package com.example.buensaborback.presentation.rest;

import com.example.buensaborback.bussines.service.IUsuarioService;
import com.example.buensaborback.domain.entities.Usuario;
import com.example.buensaborback.domain.entities.enums.Rol;
import com.example.buensaborback.presentation.advice.exception.UnauthorizeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;
import java.util.Objects;

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
            String auth0Id = jwt.getSubject();
            String username = jwt.getClaim("preferred_username");
            String email = jwt.getClaim("email");



            Usuario usuario = new Usuario();
            usuario.setAuth0Id(auth0Id);
            usuario.setUsername(username);
            usuario.setEmail(email);



            Usuario usuarioLogueado = usuarioService.login(username, auth0Id);

            // Si el usuario no existía y fue creado, o si existía y fue actualizado
            if (!Objects.equals(usuarioLogueado.getEmail(), email) ){
                usuarioLogueado.setEmail(email);

                usuarioLogueado = usuarioService.register(usuarioLogueado);
            }

            return ResponseEntity.ok().body(usuarioLogueado);
        } catch (UnauthorizeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error en el proceso de login: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioRegistrado = usuarioService.register(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error en el proceso de registro: " + e.getMessage()));
        }
    }

    @GetMapping("/validar/{nombreUsuario}")
    public ResponseEntity<Boolean> validarExistenciaUsuario(@PathVariable String nombreUsuario) {
        boolean usuarioExistente = usuarioService.existsUsuarioByUsername(nombreUsuario);
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
}