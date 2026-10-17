package com.umc.pediupatrao.config;

import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repo) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (repo.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                repo.save(admin);
                System.out.println("Usuário admin criado!");
            }

            if (repo.findByUsername("gerente").isEmpty()) {
                Usuario gerente = new Usuario();
                gerente.setUsername("gerente");
                gerente.setPassword(encoder.encode("gerente123"));
                gerente.setRole("GERENTE");
                repo.save(gerente);
                System.out.println("Usuário gerente criado!");
            }

            if (repo.findByUsername("atendente").isEmpty()) {
                Usuario atendente = new Usuario();
                atendente.setUsername("atendente");
                atendente.setPassword(encoder.encode("atendente123"));
                atendente.setRole("ATENDENTE");
                repo.save(atendente);
                System.out.println("Usuário atendente criado!");
            }
        };
    }
}