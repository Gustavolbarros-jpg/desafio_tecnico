package main;

import domain.Funcionario;
import service.FuncionarioService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Principal {

    public static List<Funcionario> criarFuncionarios() {
        return List.of(
                new Funcionario("Maria", LocalDate.of(2000, 10, 18), new BigDecimal("2009.44"), "Operador"),
                new Funcionario("João", LocalDate.of(1990, 5, 12), new BigDecimal("2284.38"), "Operador"),
                new Funcionario("Caio", LocalDate.of(1961, 5, 2), new BigDecimal("9836.14"), "Coordenador"),
                new Funcionario("Miguel", LocalDate.of(1988, 10, 14), new BigDecimal("19119.88"), "Diretor"),
                new Funcionario("Alice", LocalDate.of(1995, 1, 5), new BigDecimal("2234.68"), "Recepcionista"),
                new Funcionario("Heitor", LocalDate.of(1999, 11, 19), new BigDecimal("1582.72"), "Operador"),
                new Funcionario("Arthur", LocalDate.of(1993, 3, 31), new BigDecimal("4071.84"), "Contador"),
                new Funcionario("Laura", LocalDate.of(1994, 7, 8), new BigDecimal("3017.45"), "Gerente"),
                new Funcionario("Heloísa", LocalDate.of(2003, 5, 24), new BigDecimal("1606.85"), "Eletricista"),
                new Funcionario("Helena", LocalDate.of(1996, 9, 2), new BigDecimal("2799.93"), "Gerente")
        );
    }

    public static void main(String[] args) {
        FuncionarioService service = new FuncionarioService();

        service.inserirFuncionarios(criarFuncionarios());
        System.out.println("3.1 - Funcionários inseridos: " + service.listarFuncionarios().size());

        System.out.println("3.2 - João removido: " + service.removerFuncionario("João"));

        System.out.println("\n3.3 - Todos os funcionários:");
        service.imprimirTodos();

        service.aumentoSalario();
        System.out.println("\n3.4 - Salários após aumento de 10%:");
        service.imprimirTodos();

        Map<String, List<Funcionario>> grupos = service.funcionariosPorFuncao();
        System.out.println("\n3.5 - Funcionários agrupados em " + grupos.size() + " funções.");

        System.out.println("\n3.6 - Funcionários por função:");
        service.imprimirAgrupadosPorFuncao(grupos);

        System.out.println("\n3.8 - Aniversariantes de outubro e dezembro:");
        service.imprimirAniversariantes(Set.of(10, 12));

        System.out.println("\n3.9 - Funcionário com maior idade:");
        service.imprimirFuncionarioMaisVelho();

        System.out.println("\n3.10 - Funcionários em ordem alfabética:");
        service.imprimirEmOrdemAlfabetica();

        System.out.println("\n3.11 - Total dos salários:");
        service.imprimirTotalSalarios();

        System.out.println("\n3.12 - Salários mínimos por funcionário:");
        service.imprimirSalariosMinimos();
    }
}
