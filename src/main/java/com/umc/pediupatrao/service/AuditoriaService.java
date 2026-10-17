package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Auditoria;
import com.umc.pediupatrao.repository.AuditoriaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    public List<Auditoria> listarTodas() {
        return auditoriaRepository.findAllByOrderByDataHoraDesc();
    }

    public void registrar(String tipoOperacao, String entidade, String entidadeId,
                          String campoAlterado, String valorAnterior, String valorNovo,
                          String justificativa) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = (auth != null && auth.isAuthenticated()) ? auth.getName() : "SISTEMA";

        String perfil = "DESCONHECIDO";
        if (auth != null && auth.getAuthorities() != null) {
            perfil = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("DESCONHECIDO")
                    .replace("ROLE_", "");
        }

        Auditoria registro = new Auditoria(usuario, perfil, tipoOperacao, entidade,
                entidadeId, campoAlterado, valorAnterior, valorNovo, justificativa);

        auditoriaRepository.save(registro);
    }
}