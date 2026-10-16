package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final AuditoriaService auditoriaService;

    public PedidoService(PedidoRepository pedidoRepository, AuditoriaService auditoriaService) {
        this.pedidoRepository = pedidoRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(String id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Transactional
    public Pedido criarPedido(Pedido pedido, String usuarioLogado) {
        boolean temItens = pedido.getItens() != null && !pedido.getItens().isEmpty();
        boolean temItemUnico = pedido.getPrecoUnitario() != null
                && pedido.getPrecoUnitario() > 0
                && pedido.getQuantidade() > 0;

        if (!temItens && !temItemUnico) {
            throw new IllegalArgumentException("O pedido deve ter ao menos um item.");
        }

        pedido.setStatus("RECEBIDO");

        pedido.setUsuarioEntrada(usuarioLogado);
        pedido.setDataHoraEntrada(LocalDateTime.now());

        recalcularValores(pedido);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        auditoriaService.registrar(
                "INCLUSÃO DE PEDIDO",
                "Pedido",
                pedidoSalvo.getId(),
                "status / valorFinal",
                null,
                "RECEBIDO (R$ " + pedidoSalvo.getValorFinal() + ")",
                null);

        return pedidoSalvo;
    }

    @Transactional
    public Pedido atualizarStatus(String pedidoId, String novoStatus, String usuarioLogado) {
        if (novoStatus == null || novoStatus.isBlank()) {
            throw new IllegalArgumentException("O novo status é obrigatório.");
        }

        if ("CANCELADO".equals(novoStatus)) {
            throw new IllegalStateException("Para cancelar um pedido, utilize o cancelamento com justificativa.");
        }

        Pedido pedido = buscarPorId(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado.");
        }

        if ("CANCELADO".equals(pedido.getStatus())) {
            throw new IllegalStateException("Não é possível alterar o status de um pedido cancelado.");
        }

        String statusAnterior = pedido.getStatus();

        if ("SAIU_PARA_ENTREGA".equals(novoStatus) || "RETIRADO".equals(novoStatus)) {
            pedido.setUsuarioSaida(usuarioLogado);
            pedido.setDataHoraSaida(LocalDateTime.now());
        }

        pedido.setStatus(novoStatus);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        auditoriaService.registrar(
                "MUDANÇA DE STATUS DE PEDIDO",
                "Pedido",
                pedidoId,
                "status",
                statusAnterior,
                novoStatus,
                null);

        return pedidoSalvo;
    }

    @Transactional
    public Pedido aplicarDesconto(String pedidoId, Double percentual, String usuarioLogado) {
        if (percentual == null || percentual.isNaN() || percentual < 0.0 || percentual > 100.0) {
            throw new IllegalArgumentException("O percentual de desconto deve estar entre 0 e 100.");
        }

        Pedido pedido = buscarPorId(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado.");
        }

        if ("CANCELADO".equals(pedido.getStatus())) {
            throw new IllegalStateException("Não é possível aplicar desconto em um pedido cancelado.");
        }

        Double descontoAnterior = pedido.getDescontoPercentual() != null ? pedido.getDescontoPercentual() : 0.0;

        pedido.setDescontoPercentual(percentual);
        recalcularValores(pedido);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        auditoriaService.registrar(
                "APLICAÇÃO DE DESCONTO",
                "Pedido",
                pedidoId,
                "descontoPercentual / valorFinal",
                descontoAnterior + "%",
                percentual + "% (R$ " + pedidoSalvo.getValorFinal() + ")",
                null);

        return pedidoSalvo;
    }

    @Transactional
    public Pedido cancelarPedido(String pedidoId, String justificativa, String usuarioLogado) {
        if (justificativa == null || justificativa.isBlank()) {
            throw new IllegalArgumentException("A justificativa é obrigatória para cancelar um pedido.");
        }

        Pedido pedido = buscarPorId(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado.");
        }

        if ("CANCELADO".equals(pedido.getStatus())) {
            throw new IllegalStateException("O pedido já está cancelado.");
        }

        String statusAnterior = pedido.getStatus();

        pedido.setStatus("CANCELADO");
        pedido.setJustificativaCancelamento(justificativa.trim());
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        auditoriaService.registrar(
                "CANCELAMENTO DE PEDIDO",
                "Pedido",
                pedidoId,
                "status",
                statusAnterior,
                "CANCELADO",
                justificativa.trim());

        return pedidoSalvo;
    }

    private void recalcularValores(Pedido pedido) {
        double total = 0.0;

        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            for (ItemPedido item : pedido.getItens()) {
                double subtotal = (item.getPrecoUnitario() != null && item.getQuantidade() != null) ?
                        item.getPrecoUnitario() * item.getQuantidade() : 0.0;
                item.setSubtotal(subtotal);
                total += subtotal;
            }
        } else if (pedido.getPrecoUnitario() != null && pedido.getQuantidade() > 0) {
            total = pedido.getPrecoUnitario() * pedido.getQuantidade();
        }

        pedido.setValorTotal(total);

        double desc = pedido.getDescontoPercentual() != null ? pedido.getDescontoPercentual() : 0.0;
        double valorComDesconto = total - (total * (desc / 100.0));
        pedido.setValorFinal(valorComDesconto);
    }
}