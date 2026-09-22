package service;

import domain.Funcionario;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FuncionarioService {
    private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final BigDecimal AUMENTO = new BigDecimal("0.10");
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1212.00");

    private final List<Funcionario> funcionarios = new ArrayList<>();

    public void inserirFuncionarios(List<Funcionario> novos) {
        funcionarios.addAll(Objects.requireNonNull(novos));
    }

    public boolean removerFuncionario(String nome) {
        return funcionarios.removeIf(funcionario -> funcionario.getNome().equalsIgnoreCase(nome));
    }

    public List<Funcionario> listarFuncionarios() {
        return List.copyOf(funcionarios);
    }

    public void printFuncionarios(List<Funcionario> lista) {
        lista.forEach(funcionario -> System.out.printf(
                "Nome: %s | Nascimento: %s | Salário: %s | Função: %s%n",
                funcionario.getNome(),
                formatarData(funcionario.getDataNascimento()),
                formatarMoeda(funcionario.getSalario()),
                funcionario.getFuncao()
        ));
    }

    public void imprimirTodos() {
        printFuncionarios(funcionarios);
    }

    public void imprimirFiltrados(Predicate<Funcionario> criterio) {
        printFuncionarios(funcionarios.stream().filter(criterio).toList());
    }

    public void imprimirAniversariantes(Set<Integer> meses) {
        imprimirFiltrados(funcionario -> meses.contains(funcionario.getDataNascimento().getMonthValue()));
    }

    public void aumentoSalario() {
        funcionarios.forEach(funcionario -> {
            BigDecimal novoSalario = funcionario.getSalario()
                    .multiply(BigDecimal.ONE.add(AUMENTO))
                    .setScale(2, RoundingMode.HALF_UP);
            funcionario.setSalario(novoSalario);
        });
    }

    public Map<String, List<Funcionario>> funcionariosPorFuncao() {
        return funcionarios.stream().collect(Collectors.groupingBy(
                Funcionario::getFuncao,
                LinkedHashMap::new,
                Collectors.toList()
        ));
    }

    public void imprimirAgrupadosPorFuncao() {
        imprimirAgrupadosPorFuncao(funcionariosPorFuncao());
    }

    public void imprimirAgrupadosPorFuncao(Map<String, List<Funcionario>> grupos) {
        grupos.forEach((funcao, lista) -> {
            System.out.println("Função: " + funcao);
            printFuncionarios(lista);
        });
    }

    public void imprimirFuncionarioMaisVelho() {
        funcionarios.stream()
                .min(Comparator.comparing(Funcionario::getDataNascimento))
                .ifPresent(funcionario -> {
                    int idade = Period.between(funcionario.getDataNascimento(), LocalDate.now()).getYears();
                    System.out.printf("Nome: %s | Idade: %d anos%n", funcionario.getNome(), idade);
                });
    }

    public void imprimirEmOrdemAlfabetica() {
        List<Funcionario> ordenados = funcionarios.stream()
                .sorted(Comparator.comparing(Funcionario::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
        printFuncionarios(ordenados);
    }

    public BigDecimal calcularTotalSalarios() {
        return funcionarios.stream()
                .map(Funcionario::getSalario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void imprimirTotalSalarios() {
        System.out.println("Total dos salários: " + formatarMoeda(calcularTotalSalarios()));
    }

    public void imprimirSalariosMinimos() {
        NumberFormat formatoQuantidade = NumberFormat.getNumberInstance(LOCALE_BR);
        formatoQuantidade.setMinimumFractionDigits(2);
        formatoQuantidade.setMaximumFractionDigits(2);

        funcionarios.forEach(funcionario -> {
            BigDecimal quantidade = funcionario.getSalario()
                    .divide(SALARIO_MINIMO, 2, RoundingMode.HALF_UP);
            System.out.printf("%s: %s salários mínimos%n",
                    funcionario.getNome(),
                    formatoQuantidade.format(quantidade));
        });
    }

    private String formatarMoeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor);
    }

    private String formatarData(LocalDate data) {
        return data.format(FORMATO_DATA);
    }
}
