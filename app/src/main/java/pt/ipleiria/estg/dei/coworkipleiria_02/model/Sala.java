package pt.ipleiria.estg.dei.coworkipleiria_02.model;

import java.io.Serializable;

public class Sala implements Serializable {
    private int id;
    private String nome_sala;
    private int capacidade;
    private String descricao;
    private int pricing_plan_id;
    private String status;

    public Sala() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomeSala() { return nome_sala; }
    public void setNomeSala(String nome_sala) { this.nome_sala = nome_sala; }

    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getPricingPlanId() { return pricing_plan_id; }
    public void setPricingPlanId(int pricing_plan_id) { this.pricing_plan_id = pricing_plan_id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}