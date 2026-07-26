package br.com.bruno.spring_boot_essentials.controller;

import br.com.bruno.spring_boot_essentials.config.TokenService;
import br.com.bruno.spring_boot_essentials.dto.AuthenticationDto;
import br.com.bruno.spring_boot_essentials.dto.LoginResponseDto;
import br.com.bruno.spring_boot_essentials.dto.RegisterDto;
import br.com.bruno.spring_boot_essentials.exception.ElemetoDuplicadoException;
import br.com.bruno.spring_boot_essentials.model.UsuarioEntity;
import br.com.bruno.spring_boot_essentials.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.gerarToken((UsuarioEntity) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDto data) {
        if (this.repository.findByEmail(data.email()) != null) {
            throw new ElemetoDuplicadoException("Email já cadastrado.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());
        UsuarioEntity newUser = new UsuarioEntity(data.nome(), data.email(), encryptedPassword);
        this.repository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
