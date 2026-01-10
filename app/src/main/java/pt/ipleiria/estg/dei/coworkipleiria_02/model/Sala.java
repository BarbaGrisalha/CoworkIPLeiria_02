package pt.ipleiria.estg.dei.coworkipleiria_02.model;

    public class Sala {

        private String id;
        private String nome;
        private int capacidade;
        private TipoSala tipo;
        private boolean disponivel;
        private Double precoPorHora;

        public Sala(String id, String nome, int capacidade, TipoSala tipo, boolean disponivel, Double precoPorHora) {
            this.id = id;
            this.nome = nome;
            this.capacidade = capacidade;
            this.tipo = tipo;
            this.disponivel = disponivel;
            this.precoPorHora = precoPorHora;
        }

        // Getters
        public String getId() { return id; }
        public String getNome() { return nome; }
        public int getCapacidade() { return capacidade; }
        public TipoSala getTipo() { return tipo; }
        public boolean isDisponivel() { return disponivel; }
        public Double getPrecoPorHora() { return precoPorHora; }

        public enum TipoSala {
            INDIVIDUAL,
            EQUIPE_PEQUENA,
            EQUIPE_MEDIA,
            REUNIAO
        }
}


