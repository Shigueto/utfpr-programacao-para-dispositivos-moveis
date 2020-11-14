package br.edu.utfpr.cadastrotarefas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    private SQLiteDatabase database;
    private CadastroTarefaHelper helper;

    public TarefaDAO(Context context) {
        helper = new CadastroTarefaHelper(context);
    }

    public void open() {
        database = helper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public boolean salvar(Tarefa tarefa) {
        ContentValues values = new ContentValues();
        values.put(CadastroTarefaHelper.CAMPO_DESCRICAO, tarefa.getDescricao());

        if (tarefa.getId() == null) {
            return database.insert(CadastroTarefaHelper.NOME_TABELA, null, values) >= 0;
        } else {
            return database.update(CadastroTarefaHelper.NOME_TABELA, values,
                    CadastroTarefaHelper.CAMPO_ID+"="+tarefa.getId(), null) > 0;
        }
    }

    public boolean excluir(Long id) {
        return database.delete(CadastroTarefaHelper.NOME_TABELA,
                CadastroTarefaHelper.CAMPO_ID+"="+id, null) > 0;
    }

    public Tarefa carregar(Long id) {
        Cursor cursor = database.query(CadastroTarefaHelper.NOME_TABELA, CadastroTarefaHelper.CAMPOS,
                CadastroTarefaHelper.CAMPO_ID+"="+id, null, null, null, null);
        Tarefa tarefa = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            tarefa = new Tarefa();
            tarefa.setId(cursor.getLong(0));
            tarefa.setDescricao(cursor.getString(1));
        }
        cursor.close();
        return tarefa;
    }

    public List<Tarefa> listar(String ordenacao, boolean usarOrdemDecrescente, String filtro) {
        String where = null;
        if (filtro != null && !filtro.trim().isEmpty()) {
            where = "UPPER (" + CadastroTarefaHelper.CAMPO_DESCRICAO + ") LIKE '" + filtro.toUpperCase() + "%'";
        }
        String orderBy = null;
        if (ordenacao != null && !ordenacao.trim().isEmpty()) {
            orderBy = ordenacao;
            if (usarOrdemDecrescente) {
                orderBy += " DESC";
            }
        }

        Cursor cursor = database.query(CadastroTarefaHelper.NOME_TABELA, CadastroTarefaHelper.CAMPOS,
                where, null, null, null, orderBy);

        List<Tarefa> tarefas = new ArrayList<>();

        while (cursor.moveToNext()) {
            Tarefa tarefa = new Tarefa();
            tarefa.setId(cursor.getLong(0));
            tarefa.setDescricao(cursor.getString(1));
            tarefas.add(tarefa);
        }

        cursor.close();

        return tarefas;
    }

}
