package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ATENDENTE', 'GERENTE')")
    public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedido,
                                              Authentication authentication) {
        String usuarioResponsavel = authentication.getName();
        Pedido novoPedido = pedidoService.criarPedido(pedido, usuarioResponsavel);
        return ResponseEntity.ok(novoPedido);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ATENDENTE', 'GERENTE')")
    public ResponseEntity<Pedido> atualizarStatus(@PathVariable String id,
                                                  @RequestParam String status,
                                                  Authentication authentication) {
        String usuarioResponsavel = authentication.getName();
        Pedido pedidoAtualizado = pedidoService.atualizarStatus(id, status, usuarioResponsavel);
        return ResponseEntity.ok(pedidoAtualizado);
    }

    @PostMapping("/{id}/desconto")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Pedido> aplicarDesconto(@PathVariable String id,
                                                  @RequestParam Double percentualDesconto,
                                                  Authentication authentication) {
        String usuarioGerente = authentication.getName();
        Pedido pedidoComDesconto = pedidoService.aplicarDesconto(id, percentualDesconto, usuarioGerente);
        return ResponseEntity.ok(pedidoComDesconto);
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Pedido> cancelarPedido(@PathVariable String id,
                                                 @RequestBody Map<String, String> payload,
                                                 Authentication authentication) {
        String justificativa = payload.get("justificativa");
        String usuarioGerente = authentication.getName();
        Pedido pedidoCancelado = pedidoService.cancelarPedido(id, justificativa, usuarioGerente);
        return ResponseEntity.ok(pedidoCancelado);
    }
}