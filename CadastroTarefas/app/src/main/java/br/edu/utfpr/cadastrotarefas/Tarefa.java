package br.edu.utfpr.cadastrotarefas;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tarefa {

    public static final String FORMATO_ARMAZENAMENTO = "yyyy-MM-dd";
    public static final String FORMATO_VISUALIZACAO = "dd/MM/yyyy";

    private Long id;
    private String descricao;
    private String prazo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPrazo() {
        return prazo;
    }

    public String getPrazoFormatado() {
        SimpleDateFormat sdfArmazenamento = new SimpleDateFormat(FORMATO_ARMAZENAMENTO);
        SimpleDateFormat sdfVisualizacao = new SimpleDateFormat(FORMATO_VISUALIZACAO);
        try {
            Date prazo = sdfArmazenamento.parse(this.prazo);
            return sdfVisualizacao.format(prazo);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }

    public void setPrazo(Date prazo) {
        this.prazo = new SimpleDateFormat(FORMATO_ARMAZENAMENTO).format(prazo);
    }

    @Override
    public String toString() {
        return id + " - " + descricao + " - " + getPrazoFormatado();
    }
}
