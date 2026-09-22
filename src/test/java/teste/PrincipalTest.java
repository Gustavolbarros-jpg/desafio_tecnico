package teste;

import main.Principal;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrincipalTest {
    @Test
    void mainExecutaTodosOsRequisitosNaSequencia() {
        PrintStream anterior = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream capturada = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capturada);
            Principal.main(new String[0]);
        } finally {
            System.setOut(anterior);
        }

        String texto = buffer.toString(StandardCharsets.UTF_8);
        String[] secoes = {"3.1 -", "3.2 -", "3.3 -", "3.4 -", "3.5 -",
                "3.6 -", "3.8 -", "3.9 -", "3.10 -", "3.11 -", "3.12 -"};
        int posicaoAnterior = -1;
        for (String secao : secoes) {
            int posicao = texto.indexOf(secao);
            assertTrue(posicao > posicaoAnterior, "Seção ausente ou fora de ordem: " + secao);
            posicaoAnterior = posicao;
        }
        assertTrue(texto.contains("3.1 - Funcionários inseridos: 10"));
        assertTrue(texto.contains("3.2 - João removido: true"));
        assertTrue(texto.contains("50.906,82"));
        assertFalse(texto.contains("Nome: João |"));
    }
}
