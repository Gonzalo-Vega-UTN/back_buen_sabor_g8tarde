package com.example.buensaborback.presentation.rest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.buensaborback.bussines.service.IUsuarioService;
import com.example.buensaborback.domain.dto.Auth0User;
import com.example.buensaborback.domain.entities.Usuario;
import com.example.buensaborback.domain.entities.enums.Rol;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class UsuarioController {
    private final IUsuarioService usuarioService;
    @Value("${auth0.api.client.secret}")
    private String secret;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@AuthenticationPrincipal Jwt jwt) {
        try {
            Usuario usuario = decodeToken(jwt);
            System.out.println("REGISTER TOKEN");
            System.out.println(usuario);
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
            System.out.println("REGISTER TOKEN");
            System.out.println(usuario);
            Usuario usuarioRegistrado = usuarioService.register(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error en el proceso de registro: " + e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(
            @RequestBody Auth0User body) {
        return ResponseEntity.ok(usuarioService.createUser(body));
    }

    @GetMapping("/validar")
    public ResponseEntity<Boolean> validarExistenciaUsuario(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = decodeToken(jwt);
        boolean usuarioExistente = usuarioService.existsUsuarioByUsername(usuario.getUsername());
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

    @GetMapping("/usuarios")
    public ResponseEntity<?> getAllUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.getAllUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los usuarios: " + e.getMessage()));
        }
    }

    @GetMapping("/usuarios/rol/{rol}")
    public ResponseEntity<?> getUsuariosByRol(@PathVariable Rol rol) {
        try {
            List<Usuario> usuarios = usuarioService.getUsuariosByRol(rol);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los usuarios por rol: " + e.getMessage()));
        }
    }

    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<?> updateUsuarioRol(@PathVariable Long id, @RequestBody String newRolString) {
        try {
            Rol newRol = Rol.valueOf(newRolString);
            Usuario updatedUsuario = usuarioService.updateUsuarioRol(id, newRol);
            return ResponseEntity.ok(updatedUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Rol inválido: " + e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el rol del usuario: " + e.getMessage()));
        }
    }

    private Usuario decodeToken(Jwt jwt){
        String auth0Id = jwt.getSubject();
        String username = jwt.getClaim("preferred_username");
        String email = jwt.getClaim("email");
        List<String> roles = jwt.getClaim("https://apiprueba/roles");

        String role = roles != null && !roles.isEmpty() ? roles.get(0) : "Cliente";

        Rol userRole;
        switch (role) {
            case "Cliente":
                userRole = Rol.Cliente;
                break;
            case "Admin":
                userRole = Rol.Admin;
                break;
            default:
                userRole = Rol.Cliente;
                break;
        }

        Usuario usuario = new Usuario();
        usuario.setAuth0Id(auth0Id);
        if (username == null) {
            usuario.setUsername(email.split("@")[0]);
        } else {
            usuario.setUsername(username);
        }
        usuario.setEmail(email);
        usuario.setRol(userRole);
        return usuario;
    }
}