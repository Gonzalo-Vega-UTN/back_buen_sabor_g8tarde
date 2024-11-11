package com.example.buensaborback.bussines.service.impl;

import com.example.buensaborback.bussines.service.IUsuarioService;
import com.example.buensaborback.domain.dto.Auth0User;
import com.example.buensaborback.domain.dto.ErrorDto;
import com.example.buensaborback.domain.entities.Usuario;
import com.example.buensaborback.domain.entities.enums.Rol;
import com.example.buensaborback.presentation.advice.exception.Auth0Exception;
import com.example.buensaborback.presentation.advice.exception.NotFoundException;
import com.example.buensaborback.presentation.advice.exception.UnauthorizeException;
import com.example.buensaborback.repositories.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final Auth0Service auth0Service;


    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, Auth0Service auth0Service) {
        this.usuarioRepository = usuarioRepository;
        this.auth0Service = auth0Service;
    }

    @Override
    public Usuario getUsuarioById(Long id) {
        return this.usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Usuario con ID %d no encontrado", id)));
    }

    @Override
    public void delete(Long id) {
        if (!existsUsuarioById(id)) {
            throw new NotFoundException(String.format("Usuario con ID %d no encontrado", id));
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario getUsuarioByUsername(String username) {
        return this.usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("Usuario con Username %s no encontrado", username)));
    }

    @Override
    public List<Usuario> getAll() {
        return this.usuarioRepository.findAll();
    }

    @Override
    public boolean existsUsuarioById(Long id) {
        return this.usuarioRepository.existsById(id);
    }

    @Override
    public boolean existsUsuarioByUsername(String username) {
        return this.usuarioRepository.findByUsername(username).isPresent();
    }

    @Override
    public Usuario login(Usuario usuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(usuario.getUsername());
        if (usuarioOptional.isPresent()) {
            Usuario usuarios = usuarioOptional.get();
            if (usuario.getAuth0Id().equals(usuario.getAuth0Id())) {
                return usuarios;
            } else {
                throw new UnauthorizeException("Credenciales incorrectas");
            }
        } else {
            return this.register(usuario);
        }
    }

    @Override
    public Usuario register(Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(usuario.getUsername());
        if (usuarioExistente.isPresent()) {
            Usuario usuarioActualizado = usuarioExistente.get();
            usuarioActualizado.setAuth0Id(usuario.getAuth0Id());
            usuarioActualizado.setEmail(usuario.getEmail());
            return usuarioRepository.save(usuarioActualizado);
        } else {
            return usuarioRepository.save(usuario);
        }
    }

    @Override
    public String encriptarClaveSHA256(String clave) {
        // Implementación para encriptar la contraseña
        return null;
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public List<Usuario> getUsuariosByRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Override
    public Usuario updateUsuarioRol(Long id, Rol newRol) {
        Usuario usuario = getUsuarioById(id);
        if (usuario == null) {
            throw new NotFoundException(String.format("Usuario con ID %d no encontrado", id));
        }

        usuario.setRol(newRol);
        return usuarioRepository.save(usuario);
    }

    public Usuario createUser(Auth0User body) {
        String token = auth0Service.getManagementApiToken();
        String usersDomain = String.valueOf(auth0Service.getDomain());
        if (usersDomain.endsWith("/")) {
            usersDomain = usersDomain.substring(0, usersDomain.length() - 1);
        }
        String url = String.format("%s%s", usersDomain , "/users");
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        headers.put("authorization", "Bearer " + token);
        try {
            Rol rol = body.getRol();
            body.setRol(null); //Rol no es parte de objeto Auth0
            body.setConnection("Username-Password-Authentication");
            HttpResponse<String> response = Unirest.post(url)
                    .body(body)
                    .headers(headers)
                    .asString();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            if(response.isSuccess()){
                body = objectMapper.readValue(response.getBody(), Auth0User.class);
                System.out.println("RESPONSE");
                System.out.println(body);
                Usuario usuario = Usuario.builder()
                        .email(body.getEmail())
                        .auth0Id(body.getUserId())
                        .username(body.getNickname())
                        .rol(rol).build();;
                return usuarioRepository.save(usuario);
            }else{
                ErrorDto error = objectMapper.readValue((response.getBody()), ErrorDto.class);
                String message = error.getMessage();
                throw new Auth0Exception(message);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}