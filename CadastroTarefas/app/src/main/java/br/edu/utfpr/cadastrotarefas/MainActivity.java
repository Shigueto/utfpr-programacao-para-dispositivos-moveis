package br.edu.utfpr.cadastrotarefas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int FILTRO_REQUEST_CODE = 1;

    private EditText edtCodigo;
    private EditText edtDescricao;
    private ListView listaTarefas;
    private ArrayAdapter<Tarefa> adapter;
    private TarefaDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtCodigo = findViewById(R.id.edtCodigo);
        edtDescricao = findViewById(R.id.edtDescricao);
        listaTarefas = findViewById(R.id.listaTarefas);

        dao = new TarefaDAO(this);
        dao.open();

        List<Tarefa> tarefas = obterTarefas();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tarefas);

        listaTarefas.setAdapter(adapter);
        listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tarefa tarefa = (Tarefa) parent.getItemAtPosition(position);
                carregarTarefa(tarefa.getId());
            }
        });
    }

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }

    private List<Tarefa> obterTarefas() {
        SharedPreferences prefs = getSharedPreferences(FiltroActivity.NOME_ARQUIVO_PREFS,
                Context.MODE_PRIVATE);
        String ordenacao = prefs.getString(FiltroActivity.PREF_ORDENACAO, CadastroTarefaHelper.CAMPO_ID);
        boolean usarOrdemDecrescente = prefs.getBoolean(FiltroActivity.PREF_USAR_ORDEM_DECRESCENTE,
                false);
        String filtro = prefs.getString(FiltroActivity.PREF_FILTRO, "");

        return dao.listar(ordenacao, usarOrdemDecrescente, filtro);
    }

    private void carregarTarefa(long id) {
        Tarefa tarefa = dao.carregar(id);
        edtCodigo.setText(String.valueOf(tarefa.getId()));
        edtDescricao.setText(tarefa.getDescricao());
    }

    private void atualizarLista() {
        List<Tarefa> tarefas = obterTarefas();
        adapter.clear();
        adapter.addAll(tarefas);
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
            Tarefa tarefa = new Tarefa();
            tarefa.setDescricao(descricao);
            String codigo = edtCodigo.getText().toString();
            if (!codigo.isEmpty()) {
                tarefa.setId(Long.parseLong(codigo));
            }
            if (dao.salvar(tarefa)) {
                limparCampos();
                atualizarLista();
            } else {
                Toast.makeText(this, R.string.salvar_tarefa_erro, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onExcluirClick(View v) {
        String codigo = edtCodigo.getText().toString();
        if (codigo.isEmpty()) {
            Toast.makeText(this, R.string.informe_codigo, Toast.LENGTH_LONG).show();
        } else {
            if (dao.excluir(Long.parseLong(codigo))) {
                limparCampos();
                atualizarLista();
            } else {
                Toast.makeText(this, R.string.excluir_tarefa_erro, Toast.LENGTH_LONG).show();
            }
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