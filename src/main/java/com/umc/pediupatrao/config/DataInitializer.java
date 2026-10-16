package com.umc.pediupatrao.config;

import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            usuarioRepository.save(admin);
        }

        if (usuarioRepository.findByUsername("gerente").isEmpty()) {
            Usuario gerente = new Usuario();
            gerente.setUsername("gerente");
            gerente.setPassword(passwordEncoder.encode("gerente123"));
            gerente.setRole("GERENTE");
            usuarioRepository.save(gerente);
        }

        if (usuarioRepository.findByUsername("atendente").isEmpty()) {
            Usuario atendente = new Usuario();
            atendente.setUsername("atendente");
            atendente.setPassword(passwordEncoder.encode("atendente123"));
            atendente.setRole("ATENDENTE");
            usuarioRepository.save(atendente);
        }
    }
}