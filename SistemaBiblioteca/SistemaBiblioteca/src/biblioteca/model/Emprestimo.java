package biblioteca.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * MODEL - representa o empréstimo de um livro a um usuário.
 * Contém as regras de prazo e multa por atraso.
 */
public class Emprestimo {

    /** Prazo padrão de devolução, em dias. */
    public static final int PRAZO_DIAS = 7;
    /** Valor da multa por dia de atraso, em reais. */
    public static final double MULTA_POR_DIA = 1.50;

    private int id;
    private int livroId;
    private int usuarioId;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevista;
    private LocalDate dataDevolucao; // null enquanto o livro não foi devolvido

    public Emprestimo(int id, int livroId, int usuarioId,
                      LocalDate dataEmprestimo, LocalDate dataPrevista, LocalDate dataDevolucao) {
        this.id = id;
        this.livroId = livroId;
        this.usuarioId = usuarioId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevista = dataPrevista;
        this.dataDevolucao = dataDevolucao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLivroId() { return livroId; }
    public int getUsuarioId() { return usuarioId; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public LocalDate getDataPrevista() { return dataPrevista; }

    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    /** O empréstimo está ativo enquanto não houver data de devolução. */
    public boolean isAtivo() {
        return dataDevolucao == null;
    }

    /** Dias de atraso até a data de referência (ou até a devolução, se já devolvido). */
    public long diasAtraso(LocalDate referencia) {
        LocalDate fim = isAtivo() ? referencia : dataDevolucao;
        return Math.max(0, ChronoUnit.DAYS.between(dataPrevista, fim));
    }

    /** Multa acumulada (ou final, se já devolvido). */
    public double calcularMulta(LocalDate referencia) {
        return diasAtraso(referencia) * MULTA_POR_DIA;
    }
}
