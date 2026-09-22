package teste;

import domain.Funcionario;
import domain.Pessoa;
import domain.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FuncionarioService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioTest {
    private FuncionarioService service;

    @BeforeEach
    void preparar() {
        service = new FuncionarioService();
        service.inserirFuncionarios(Principal.criarFuncionarios());
    }

    @Test
    void requisito1e2_pessoaEFuncionario() {
        Funcionario maria = service.listarFuncionarios().getFirst();
        assertInstanceOf(Pessoa.class, maria);
        assertEquals("Maria", maria.getNome());
        assertEquals(LocalDate.of(2000, 10, 18), maria.getDataNascimento());
        assertEquals(new BigDecimal("2009.44"), maria.getSalario());
        assertEquals("Operador", maria.getFuncao());
    }

    @Test
    void requisito31_inserirNaOrdemDaTabela() {
        List<Funcionario> funcionarios = service.listarFuncionarios();
        assertEquals(10, funcionarios.size());
        assertEquals(
                List.of("Maria", "João", "Caio", "Miguel", "Alice", "Heitor",
                        "Arthur", "Laura", "Heloísa", "Helena"),
                funcionarios.stream().map(Funcionario::getNome).toList()
        );
        assertEquals(new BigDecimal("19119.88"), funcionarios.get(3).getSalario());
        assertEquals("Diretor", funcionarios.get(3).getFuncao());
        assertEquals(LocalDate.of(2003, 5, 24), funcionarios.get(8).getDataNascimento());
    }

    @Test
    void requisito32_removerJoao() {
        assertTrue(service.removerFuncionario("João"));
        assertEquals(9, service.listarFuncionarios().size());
        assertTrue(service.listarFuncionarios().stream()
                .noneMatch(funcionario -> funcionario.getNome().equals("João")));
        assertFalse(service.removerFuncionario("João"));
    }

    @Test
    void requisito33_imprimirDataESalarioFormatados() {
        String texto = saida(service::imprimirTodos);
        assertTrue(texto.contains("Nome: Maria | Nascimento: 18/10/2000"));
        assertTrue(texto.contains("2.009,44"));
        assertTrue(texto.contains("Função: Operador"));
        assertTrue(texto.contains("19.119,88"));
    }

    @Test
    void requisito34_aumentarDezPorCento() {
        service.aumentoSalario();
        List<Funcionario> funcionarios = service.listarFuncionarios();
        assertEquals(new BigDecimal("2210.38"), funcionarios.getFirst().getSalario());
        assertEquals(new BigDecimal("2512.82"), funcionarios.get(1).getSalario());
        assertEquals(new BigDecimal("21031.87"), funcionarios.get(3).getSalario());
    }

    @Test
    void requisito35_agruparEmMapPorFuncao() {
        service.removerFuncionario("João");
        Map<String, List<Funcionario>> grupos = service.funcionariosPorFuncao();
        assertEquals(7, grupos.size());
        assertEquals(List.of("Operador", "Coordenador", "Diretor", "Recepcionista",
                "Contador", "Gerente", "Eletricista"), List.copyOf(grupos.keySet()));
        assertEquals(List.of("Maria", "Heitor"), grupos.get("Operador").stream()
                .map(Funcionario::getNome).toList());
        assertEquals(List.of("Laura", "Helena"), grupos.get("Gerente").stream()
                .map(Funcionario::getNome).toList());
    }

    @Test
    void requisito36_imprimirAgrupadosPorFuncao() {
        service.removerFuncionario("João");
        String texto = saida(() -> service.imprimirAgrupadosPorFuncao(service.funcionariosPorFuncao()));
        assertEquals(7, texto.lines().filter(linha -> linha.startsWith("Função: ")).count());
        assertTrue(texto.indexOf("Função: Operador") < texto.indexOf("Função: Gerente"));
        assertTrue(texto.contains("Nome: Maria"));
        assertTrue(texto.contains("Nome: Heitor"));
    }

    @Test
    void requisito38_aniversariantesDeOutubroEDezembro() {
        String texto = saida(() -> service.imprimirAniversariantes(Set.of(10, 12)));
        assertTrue(texto.contains("Nome: Maria"));
        assertTrue(texto.contains("Nome: Miguel"));
        assertFalse(texto.contains("Nome: João"));
        assertEquals(2, texto.lines().filter(linha -> linha.startsWith("Nome: ")).count());
    }

    @Test
    void requisito39_funcionarioMaisVelho() {
        int idade = Period.between(LocalDate.of(1961, 5, 2), LocalDate.now()).getYears();
        assertEquals("Nome: Caio | Idade: " + idade + " anos",
                saida(service::imprimirFuncionarioMaisVelho).trim());
    }

    @Test
    void requisito310_ordenarAlfabeticamente() {
        service.removerFuncionario("João");
        List<String> nomes = saida(service::imprimirEmOrdemAlfabetica).lines()
                .map(linha -> linha.substring("Nome: ".length(), linha.indexOf(" |")))
                .collect(Collectors.toList());
        assertEquals(List.of("Alice", "Arthur", "Caio", "Heitor", "Helena",
                "Heloísa", "Laura", "Maria", "Miguel"), nomes);
    }

    @Test
    void requisito311_totalDosSalarios() {
        service.removerFuncionario("João");
        service.aumentoSalario();
        assertEquals(new BigDecimal("50906.82"), service.calcularTotalSalarios());
        assertTrue(saida(service::imprimirTotalSalarios).contains("50.906,82"));
    }

    @Test
    void requisito312_salariosMinimos() {
        service.removerFuncionario("João");
        service.aumentoSalario();
        String texto = saida(service::imprimirSalariosMinimos);
        assertTrue(texto.contains("Maria: 1,82 salários mínimos"));
        assertEquals(9, texto.lines().filter(linha -> linha.contains("salários mínimos")).count());
        assertFalse(texto.contains("João:"));
    }

    @Test
    void nomeVazioLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
                new Funcionario("  ", LocalDate.of(2000, 1, 1),
                        new BigDecimal("1000.00"), "Operador"));
        assertThrows(IllegalArgumentException.class, () ->
                service.listarFuncionarios().getFirst().setNome(null));
    }

    @Test
    void dataNulaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
                new Funcionario("Ana", null, new BigDecimal("1000.00"), "Operador"));
    }

    @Test
    void salarioInvalidoLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
                new Funcionario("Ana", LocalDate.of(2000, 1, 1),
                        new BigDecimal("-1.00"), "Operador"));
        assertThrows(IllegalArgumentException.class, () ->
                service.listarFuncionarios().getFirst().setSalario(null));
    }

    @Test
    void funcaoVaziaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
                new Funcionario("Ana", LocalDate.of(2000, 1, 1),
                        new BigDecimal("1000.00"), ""));
        assertThrows(IllegalArgumentException.class, () ->
                service.listarFuncionarios().getFirst().setFuncao(" "));
    }

    private static String saida(Runnable acao) {
        PrintStream anterior = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream capturada = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capturada);
            acao.run();
        } finally {
            System.setOut(anterior);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
