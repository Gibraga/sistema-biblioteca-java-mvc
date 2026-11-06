package biblioteca.dao;

import biblioteca.model.Livro;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (acesso a dados) de Livro - persiste em dados/livros.csv.
 */
public class LivroDAO {

    private final Path arquivo = Paths.get("dados", "livros.csv");

    public List<Livro> listar() {
        List<Livro> lista = new ArrayList<>();
        for (String[] c : ArquivoCSV.ler(arquivo)) {
            lista.add(new Livro(Integer.parseInt(c[0]), c[1], c[2],
                    Integer.parseInt(c[3]), Boolean.parseBoolean(c[4])));
        }
        return lista;
    }

    public Livro buscarPorId(int id) {
        for (Livro l : listar()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    /** Insere (id = 0) ou atualiza um livro. */
    public void salvar(Livro livro) {
        List<Livro> lista = listar();
        if (livro.getId() == 0) {
            livro.setId(proximoId(lista));
            lista.add(livro);
        } else {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getId() == livro.getId()) {
                    lista.set(i, livro);
                }
            }
        }
        gravar(lista);
    }

    public void remover(int id) {
        List<Livro> lista = listar();
        lista.removeIf(l -> l.getId() == id);
        gravar(lista);
    }

    private int proximoId(List<Livro> lista) {
        int maior = 0;
        for (Livro l : lista) {
            maior = Math.max(maior, l.getId());
        }
        return maior + 1;
    }

    private void gravar(List<Livro> lista) {
        List<String[]> linhas = new ArrayList<>();
        for (Livro l : lista) {
            linhas.add(new String[] {
                String.valueOf(l.getId()),
                ArquivoCSV.limpar(l.getTitulo()),
                ArquivoCSV.limpar(l.getAutor()),
                String.valueOf(l.getAno()),
                String.valueOf(l.isDisponivel())
            });
        }
        ArquivoCSV.escrever(arquivo, linhas);
    }
}
