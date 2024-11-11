package com.example.buensaborback.presentation.rest;

import com.example.buensaborback.bussines.service.IEmpleadoService;
import com.example.buensaborback.bussines.service.impl.EmpleadoServiceImpl;
import com.example.buensaborback.domain.dto.RedirectDto;
import com.example.buensaborback.domain.entities.Empleado;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin("*")
public class EmpleadoController {

    private final IEmpleadoService empleadoService;

    public EmpleadoController(EmpleadoServiceImpl empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping ("/formulario/{empleado}")
    public ResponseEntity<RedirectDto> redirectToExternalUrl(@PathVariable("empleado") String email) {
        try{
            return ResponseEntity.ok(RedirectDto.builder().urlRedirect("/formulario-empleado").build());
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody Empleado empleado){
        return ResponseEntity.ok(empleadoService.create(empleado));
    }

    @PostMapping("/uploads")
    public ResponseEntity<?> uploadImages(@RequestParam(value = "id") Long idArticulo,
                                          @RequestParam(value = "uploads") MultipartFile[] files) {
        return ResponseEntity.ok(empleadoService.uploadImages(files, idArticulo));

    }


}
