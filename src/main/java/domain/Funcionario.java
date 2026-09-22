package domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Funcionario  extends  Pessoa{
 protected BigDecimal salario;
 protected  String funcao;

    public Funcionario(String nome, LocalDate dataNascimento, BigDecimal salario,String funcao) {
        super(nome, dataNascimento);
        setSalario(salario);
        setFuncao(funcao);
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        if (salario == null || salario.signum() < 0) {
            throw new IllegalArgumentException("Salário deve ser maior ou igual a zero.");
        }
        this.salario = salario;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        if (funcao == null || funcao.isBlank()) {
            throw new IllegalArgumentException("Função não pode ser vazia.");
        }
        this.funcao = funcao;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                '}';
    }

}
