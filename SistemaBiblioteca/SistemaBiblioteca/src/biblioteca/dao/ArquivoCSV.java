package biblioteca.dao;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitário de persistência: lê e grava arquivos CSV (separador ";").
 * Usado pelos DAOs para guardar os dados na pasta "dados".
 */
public final class ArquivoCSV {

    private static final String SEPARADOR = ";";

    private ArquivoCSV() {
    }

    public static List<String[]> ler(Path arquivo) {
        List<String[]> linhas = new ArrayList<>();
        if (!Files.exists(arquivo)) {
            return linhas;
        }
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                if (!linha.isBlank()) {
                    linhas.add(linha.split(SEPARADOR, -1));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler o arquivo " + arquivo, e);
        }
        return linhas;
    }

    public static void escrever(Path arquivo, List<String[]> linhas) {
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            List<String> texto = new ArrayList<>();
            for (String[] campos : linhas) {
                texto.add(String.join(SEPARADOR, campos));
            }
            Files.write(arquivo, texto, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao gravar o arquivo " + arquivo, e);
        }
    }

    /** Remove caracteres que quebrariam o formato CSV. */
    public static String limpar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace(";", ",").replace("\n", " ").replace("\r", " ");
    }
}
