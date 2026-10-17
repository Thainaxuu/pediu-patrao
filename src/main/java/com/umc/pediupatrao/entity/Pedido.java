package com.umc.pediupatrao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "pedidos")
public class Pedido {

    @Id
    private String id;

    // Dados do Cliente
    private String clienteId;
    private String nomeCliente;

    // Dados do Item / Pizza (mantendo compatibilidade com seu modelo)
    private String tipoPizza;
    private String sabor;
    private int quantidade;
    private Double precoUnitario = 0.0;

    // Suporte para lista de itens (RF02)
    private List<itemPedido> itens = new ArrayList<>();

    private Double valorTotal;
    private Double descontoPercentual;
    private Double valorFinal;
    private String status;
    private String usuarioEntrada;
    private LocalDateTime dataHoraEntrada;
    private String usuarioSaida;
    private LocalDateTime dataHoraSaida;
    private String justificativaCancelamento;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getTipoPizza() {
        return tipoPizza;
    }

    public void setTipoPizza(String tipoPizza) {
        this.tipoPizza = tipoPizza;
    }

    public String getSabor() {
        return sabor;
    }

    public void setSabor(String sabor) {
        this.sabor = sabor;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public List<itemPedido> getItens() {
        return itens;
    }

    public void setItens(List<itemPedido> itens) {
        this.itens = itens;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Double getDescontoPercentual() {
        return descontoPercentual;
    }

    public void setDescontoPercentual(Double descontoPercentual) {
        this.descontoPercentual = descontoPercentual;
    }

    public Double getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(Double valorFinal) {
        this.valorFinal = valorFinal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsuarioEntrada() {
        return usuarioEntrada;
    }

    public void setUsuarioEntrada(String usuarioEntrada) {
        this.usuarioEntrada = usuarioEntrada;
    }

    public LocalDateTime getDataHoraEntrada() {
        return dataHoraEntrada;
    }

    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) {
        this.dataHoraEntrada = dataHoraEntrada;
    }

    public String getUsuarioSaida() {
        return usuarioSaida;
    }

    public void setUsuarioSaida(String usuarioSaida) {
        this.usuarioSaida = usuarioSaida;
    }

    public LocalDateTime getDataHoraSaida() {
        return dataHoraSaida;
    }

    public void setDataHoraSaida(LocalDateTime dataHoraSaida) {
        this.dataHoraSaida = dataHoraSaida;
    }

    public String getJustificativaCancelamento() {
        return justificativaCancelamento;
    }

    public void setJustificativaCancelamento(String justificativaCancelamento) {
        this.justificativaCancelamento = justificativaCancelamento;
    }

    // Métodos de Conveniência (Aliases) para evitar erros de compilação no Service

    public void setDesconto(Double desconto) {
        this.descontoPercentual = desconto;
    }

    public Double getDesconto() {
        return this.descontoPercentual;
    }

    public void setUsuarioResponsavelSaida(String usuarioResponsavelSaida) {
        this.usuarioSaida = usuarioResponsavelSaida;
    }

    public String getUsuarioResponsavelSaida() {
        return this.usuarioSaida;
    }

    public void setJustificativa(String justificativa) {
        this.justificativaCancelamento = justificativa;
    }

    public String getJustificativa() {
        return this.justificativaCancelamento;
    }
}