package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.service.PedidoService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/{id}/desconto")
    @PreAuthorize("hasRole('GERENTE')")
    public String aplicarDesconto(@PathVariable String id,
                                  @RequestParam("percentual") Double percentual,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        try {
            pedidoService.aplicarDesconto(id, percentual, principal.getName());
            redirectAttributes.addFlashAttribute("sucesso", "Desconto de " + percentual + "% aplicado!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao aplicar desconto: " + e.getMessage());
        }
        return "redirect:/pedidos";
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('GERENTE')")
    public String cancelarPedido(@PathVariable String id,
                                 @RequestParam("justificativa") String justificativa,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            pedidoService.cancelarPedido(id, justificativa, principal.getName());
            redirectAttributes.addFlashAttribute("sucesso", "Pedido cancelado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cancelar pedido: " + e.getMessage());
        }
        return "redirect:/pedidos";
    }
}