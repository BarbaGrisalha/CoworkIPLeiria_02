package pt.ipleiria.estg.dei.coworkipleiria_02;

import java.io.Serializable;

public class Sala implements Serializable {  // ← OBRIGATÓRIO: implements Serializable

    private String id;
    private String nome;
    private int capacidade;
    private TipoSala tipo;
    private boolean disponivel;
    private Double precoPorHora;  // pode ser null

    // Construtor
    public Sala(String id, String nome, int capacidade, TipoSala tipo, boolean disponivel, Double precoPorHora) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.tipo = tipo;
        this.disponivel = disponivel;
        this.precoPorHora = precoPorHora;
    }

    // Getters (obrigatórios para o adapter e fragment)
    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public TipoSala getTipo() { return tipo; }
    public boolean isDisponivel() { return disponivel; }
    public Double getPrecoPorHora() { return precoPorHora; }

    // Enum dos tipos (ajusta conforme o teu projeto)
    public enum TipoSala {
        INDIVIDUAL,
        EQUIPE_PEQUENA,
        EQUIPE_MEDIA,
        REUNIAO
    }
}