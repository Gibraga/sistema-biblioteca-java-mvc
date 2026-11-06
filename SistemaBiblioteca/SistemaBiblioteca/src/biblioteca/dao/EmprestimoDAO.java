package biblioteca.dao;

import biblioteca.model.Emprestimo;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (acesso a dados) de Emprestimo - persiste em dados/emprestimos.csv.
 */
public class EmprestimoDAO {

    private final Path arquivo = Paths.get("dados", "emprestimos.csv");

    public List<Emprestimo> listar() {
        List<Emprestimo> lista = new ArrayList<>();
        for (String[] c : ArquivoCSV.ler(arquivo)) {
            LocalDate devolucao = c[5].isEmpty() ? null : LocalDate.parse(c[5]);
            lista.add(new Emprestimo(Integer.parseInt(c[0]), Integer.parseInt(c[1]),
                    Integer.parseInt(c[2]), LocalDate.parse(c[3]), LocalDate.parse(c[4]), devolucao));
        }
        return lista;
    }

    public Emprestimo buscarPorId(int id) {
        for (Emprestimo e : listar()) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    /** Insere (id = 0) ou atualiza um empréstimo. */
    public void salvar(Emprestimo emprestimo) {
        List<Emprestimo> lista = listar();
        if (emprestimo.getId() == 0) {
            emprestimo.setId(proximoId(lista));
            lista.add(emprestimo);
        } else {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getId() == emprestimo.getId()) {
                    lista.set(i, emprestimo);
                }
            }
        }
        gravar(lista);
    }

    private int proximoId(List<Emprestimo> lista) {
        int maior = 0;
        for (Emprestimo e : lista) {
            maior = Math.max(maior, e.getId());
        }
        return maior + 1;
    }

    private void gravar(List<Emprestimo> lista) {
        List<String[]> linhas = new ArrayList<>();
        for (Emprestimo e : lista) {
            linhas.add(new String[] {
                String.valueOf(e.getId()),
                String.valueOf(e.getLivroId()),
                String.valueOf(e.getUsuarioId()),
                e.getDataEmprestimo().toString(),
                e.getDataPrevista().toString(),
                e.getDataDevolucao() == null ? "" : e.getDataDevolucao().toString()
            });
        }
        ArquivoCSV.escrever(arquivo, linhas);
    }
}
