package biblioteca.controller;

import biblioteca.dao.EmprestimoDAO;
import biblioteca.dao.LivroDAO;
import biblioteca.dao.UsuarioDAO;
import biblioteca.model.Emprestimo;
import biblioteca.model.Livro;
import biblioteca.model.Usuario;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CONTROLLER de Emprestimo - concentra as regras principais do sistema:
 * disponibilidade do livro, limite por usuário, prazo e multa por atraso.
 */
public class EmprestimoController {

    /** Máximo de empréstimos ativos por usuário. */
    public static final int LIMITE_POR_USUARIO = 3;

    private final EmprestimoDAO dao = new EmprestimoDAO();
    private final LivroDAO livroDAO = new LivroDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Emprestimo emprestar(int livroId, int usuarioId) {
        Livro livro = livroDAO.buscarPorId(livroId);
        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        if (livro == null || usuario == null) {
            throw new IllegalArgumentException("Selecione um livro e um usuário.");
        }
        if (!livro.isDisponivel()) {
            throw new IllegalStateException("Este livro já está emprestado.");
        }
        long ativos = 0;
        for (Emprestimo e : dao.listar()) {
            if (e.getUsuarioId() == usuarioId && e.isAtivo()) {
                ativos++;
            }
        }
        if (ativos >= LIMITE_POR_USUARIO) {
            throw new IllegalStateException("O usuário já atingiu o limite de "
                    + LIMITE_POR_USUARIO + " empréstimos ativos.");
        }

        LocalDate hoje = LocalDate.now();
        Emprestimo emprestimo = new Emprestimo(0, livroId, usuarioId, hoje,
                hoje.plusDays(Emprestimo.PRAZO_DIAS), null);
        dao.salvar(emprestimo);

        livro.setDisponivel(false);
        livroDAO.salvar(livro);
        return emprestimo;
    }

    /** Registra a devolução e retorna o valor da multa (0 se não houver atraso). */
    public double devolver(int emprestimoId) {
        Emprestimo emprestimo = dao.buscarPorId(emprestimoId);
        if (emprestimo == null) {
            throw new IllegalArgumentException("Selecione um empréstimo.");
        }
        if (!emprestimo.isAtivo()) {
            throw new IllegalStateException("Este empréstimo já foi devolvido.");
        }
        emprestimo.setDataDevolucao(LocalDate.now());
        dao.salvar(emprestimo);

        Livro livro = livroDAO.buscarPorId(emprestimo.getLivroId());
        if (livro != null) {
            livro.setDisponivel(true);
            livroDAO.salvar(livro);
        }
        return emprestimo.calcularMulta(LocalDate.now());
    }

    public List<Emprestimo> listar(boolean somenteAtivos) {
        List<Emprestimo> resultado = new ArrayList<>();
        for (Emprestimo e : dao.listar()) {
            if (!somenteAtivos || e.isAtivo()) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    public String tituloDoLivro(int livroId) {
        Livro livro = livroDAO.buscarPorId(livroId);
        return livro == null ? "(removido)" : livro.getTitulo();
    }

    public String nomeDoUsuario(int usuarioId) {
        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        return usuario == null ? "(removido)" : usuario.getNome();
    }
}
