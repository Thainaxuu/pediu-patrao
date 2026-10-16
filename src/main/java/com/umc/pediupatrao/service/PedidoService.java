package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.repository.PedidoRepository;
import com.umc.pediupatrao.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(String id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o ID: " + id));
    }

    public Pedido criarPedido(Pedido pedido, String usuarioAutenticado) {
        double totalCalculado = 0.0;

        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            for (ItemPedido item : pedido.getItens()) {
                if (item.getProdutoId() != null) {
                    Produto p = produtoRepository.findById(item.getProdutoId()).orElse(null);
                    if (p != null) {
                        item.setProduto(p);
                        item.setPrecoUnitario(p.getPreco());
                        totalCalculado += p.getPreco() * item.getQuantidade();
                    }
                }
            }
        }

        pedido.setValorTotal(totalCalculado);
        pedido.setStatus("RECEBIDO");
        pedido.setDataHoraEntrada(LocalDateTime.now());
        pedido.setUsuarioResponsavel(usuarioAutenticado);

        return pedidoRepository.save(pedido);
    }

    public Pedido atualizarStatus(String pedidoId, String novoStatus, String usuarioAutenticado) {
        Pedido pedido = buscarPorId(pedidoId);
        String statusAtual = pedido.getStatus();

        if ("CANCELADO".equals(statusAtual) || "FINALIZADO".equals(statusAtual)) {
            throw new IllegalStateException("O pedido já está " + statusAtual + " e não pode ter o status alterado.");
        }

        boolean transicaoValida = false;

        if ("RECEBIDO".equals(statusAtual) && "EM PREPARAÇÃO".equals(novoStatus)) {
            transicaoValida = true;
        } else if ("EM PREPARAÇÃO".equals(statusAtual) && "PRONTO".equals(novoStatus)) {
            transicaoValida = true;
        } else if ("PRONTO".equals(statusAtual)
                && ("SAIU PARA ENTREGA".equals(novoStatus) || "RETIRADO".equals(novoStatus))) {
            transicaoValida = true;
            pedido.setDataHoraSaida(LocalDateTime.now());
            pedido.setUsuarioResponsavelSaida(usuarioAutenticado);
        } else if (("SAIU PARA ENTREGA".equals(statusAtual) || "RETIRADO".equals(statusAtual))
                && "FINALIZADO".equals(novoStatus)) {
            transicaoValida = true;
        }

        if (!transicaoValida) {
            throw new IllegalArgumentException("Transição inválida de '" + statusAtual + "' para '" + novoStatus + "'.");
        }

        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    public Pedido aplicarDesconto(String pedidoId, Double percentualDesconto, String usuarioGerente) {
        Pedido pedido = buscarPorId(pedidoId);

        if ("SAIU PARA ENTREGA".equals(pedido.getStatus())
                || "RETIRADO".equals(pedido.getStatus())
                || "FINALIZADO".equals(pedido.getStatus())
                || "CANCELADO".equals(pedido.getStatus())) {
            throw new IllegalStateException("Desconto não permitido após a saída ou finalização do pedido.");
        }

        if (percentualDesconto == null || percentualDesconto < 0 || percentualDesconto > 20.0) {
            throw new IllegalArgumentException("O desconto deve ser entre 0% e 20%.");
        }

        pedido.setDesconto(percentualDesconto);
        double valorComDesconto = pedido.getValorTotal() * (1 - (percentualDesconto / 100.0));
        pedido.setValorTotal(valorComDesconto);

        return pedidoRepository.save(pedido);
    }

    public Pedido cancelarPedido(String pedidoId, String justificativa, String usuarioGerente) {
        Pedido pedido = buscarPorId(pedidoId);

        if ("SAIU PARA ENTREGA".equals(pedido.getStatus())
                || "RETIRADO".equals(pedido.getStatus())
                || "FINALIZADO".equals(pedido.getStatus())) {
            throw new IllegalStateException("Não é possível cancelar um pedido que já saiu ou foi finalizado.");
        }

        if (justificativa == null || justificativa.trim().isEmpty()) {
            throw new IllegalArgumentException("A justificativa de cancelamento é obrigatória.");
        }

        pedido.setStatus("CANCELADO");
        pedido.setJustificativaCancelamento(justificativa);

        return pedidoRepository.save(pedido);
    }
}