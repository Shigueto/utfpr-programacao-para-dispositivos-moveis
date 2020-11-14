package br.edu.utfpr.cadastrotarefas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String NOME_BD = "cadastro_tarefas";
    private static final String NOME_TABELA = "tarefa";
    public static final String CAMPO_ID = "_id";
    public static final String CAMPO_DESCRICAO = "descricao";
    private static final String[] CAMPOS ={CAMPO_ID, CAMPO_DESCRICAO};
    private static final int FILTRO_REQUEST_CODE = 1;

    private EditText edtCodigo;
    private EditText edtDescricao;
    private ListView listaTarefas;
    private SQLiteDatabase database;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtCodigo = findViewById(R.id.edtCodigo);
        edtDescricao = findViewById(R.id.edtDescricao);
        listaTarefas = findViewById(R.id.listaTarefas);

        // criar banco de dados, caso não exista
        database = openOrCreateDatabase(NOME_BD, Context.MODE_PRIVATE, null);
        // criar tabela "tarefa", caso não exista
        database.execSQL("CREATE TABLE IF NOT EXISTS " + NOME_TABELA + "(" +
                CAMPO_ID + " INTEGER PRIMARY KEY, " +
                CAMPO_DESCRICAO + " TEXT);");

        int[] elementos = {android.R.id.text1, android.R.id.text2};
        Cursor cursor = obterTarefas();
        adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                cursor, CAMPOS, elementos);

        listaTarefas.setAdapter(adapter);
        listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                carregarTarefa(id);
            }
        });
    }

    private Cursor obterTarefas() {
        SharedPreferences prefs = getSharedPreferences(FiltroActivity.NOME_ARQUIVO_PREFS,
                Context.MODE_PRIVATE);
        String ordenacao = prefs.getString(FiltroActivity.PREF_ORDENACAO, CAMPO_ID);
        boolean usarOrdemDecrescente = prefs.getBoolean(FiltroActivity.PREF_USAR_ORDEM_DECRESCENTE,
                false);
        String filtro = prefs.getString(FiltroActivity.PREF_FILTRO, "");

        String where = null;
        if (!filtro.trim().isEmpty()) {
            where = "UPPER (" + CAMPO_DESCRICAO + ") LIKE '" + filtro.toUpperCase() + "%'";
        }
        String orderBy = ordenacao;
        if (usarOrdemDecrescente) {
            orderBy += " DESC";
        }

        return database.query(NOME_TABELA, CAMPOS, where,
                null, null, null, orderBy);
    }

    private void carregarTarefa(long id) {
        Cursor c = database.query(NOME_TABELA, CAMPOS, CAMPO_ID+"="+id,
                null, null, null, null);
//        Cursor c = database.query(NOME_TABELA, CAMPOS, CAMPO_ID+"=?",
//                new String[]{String.valueOf(id)}, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            edtCodigo.setText(c.getString(0));
            edtDescricao.setText(c.getString(1));
        } else {
            Toast.makeText(this, R.string.registro_nao_encontrado,
                    Toast.LENGTH_LONG).show();
        }
        c.close();
    }

    private void atualizarLista() {
        Cursor cursor = obterTarefas();
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    private void limparCampos() {
        edtCodigo.setText("");
        edtDescricao.setText("");
        edtDescricao.requestFocus();
    }

    public void onSalvarClick(View v) {
        String descricao = edtDescricao.getText().toString();
        if (descricao.trim().isEmpty()) {
            Toast.makeText(this, R.string.informe_descricao,
                    Toast.LENGTH_LONG).show();
            edtDescricao.requestFocus();
        } else {
            ContentValues values = new ContentValues();
            values.put(CAMPO_DESCRICAO, descricao);
            String codigo = edtCodigo.getText().toString();
            // inserir
            if (codigo.trim().isEmpty()) {
                database.insert(NOME_TABELA, null, values);
            }
            // editar
            else {
                database.update(NOME_TABELA, values,
                        CAMPO_ID+"="+codigo, null);
//                database.update(NOME_TABELA, values,
//                        CAMPO_ID+"=?", new String[]{codigo});
            }
            limparCampos();
            atualizarLista();
        }
    }

    public void onExcluirClick(View v) {
        String codigo = edtCodigo.getText().toString();
        if (codigo.isEmpty()) {
            Toast.makeText(this, R.string.informe_codigo, Toast.LENGTH_LONG).show();
        } else {
            database.delete(NOME_TABELA, CAMPO_ID+"="+codigo, null);
//            database.delete(NOME_TABELA, CAMPO_ID+"=?", new String[]{codigo});
            limparCampos();
            atualizarLista();
        }
    }

    public void onLimparClick(View v) {
        limparCampos();
    }

    public void onFiltroClick(View v) {
        Intent intent = new Intent(this, FiltroActivity.class);
        startActivityForResult(intent, FILTRO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTRO_REQUEST_CODE && resultCode == Activity.RESULT_OK)  {
            atualizarLista();
        }
    }
}