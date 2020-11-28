package br.edu.utfpr.cadastrotarefas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final int FILTRO_REQUEST_CODE = 1;
    public static final String NOME_ARQUIVO = "preferences";
    public static final String CHAVE_TOKEN = "token";

    private ListView listaTarefas;
    private ArrayAdapter<Tarefa> adapter;
    private TarefaDAO dao;
    private Tarefa tarefaSelecionada;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaTarefas = findViewById(R.id.listaTarefas);

        dao = new TarefaDAO(this);
        dao.open();

        List<Tarefa> tarefas = obterTarefas();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tarefas);

        listaTarefas.setAdapter(adapter);
        listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tarefaSelecionada = (Tarefa) parent.getItemAtPosition(position);
                PopupMenu menu = new PopupMenu(MainActivity.this, view);
                menu.setOnMenuItemClickListener(MainActivity.this);
                menu.inflate(R.menu.menu_popup);
                menu.show();
            }
        });

        registerForContextMenu(listaTarefas);

        criarCanalNotificacao();

        obterTokenFirebase();

        mostrarParametros(getIntent());
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mostrarParametros(intent);
            }
        };
    }

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ServicoMensagens.MENSAGEM_RECEBIDA));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
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

    private void atualizarLista() {
        List<Tarefa> tarefas = obterTarefas();
        adapter.clear();
        adapter.addAll(tarefas);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTRO_REQUEST_CODE && resultCode == Activity.RESULT_OK)  {
            atualizarLista();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                abrirForm(null);
                return true;
            case R.id.filtro:
                Intent intent = new Intent(this, FiltroActivity.class);
                startActivityForResult(intent, FILTRO_REQUEST_CODE);
                return true;
            case R.id.sobre:
                mostrarInfoSobre();
                return true;
            case R.id.mostrar_notificacao:
                mostrarNotificacao();
                return true;
            case R.id.ocultar_notificacao:
                ocultarNotificacao();
                return true;
            case R.id.mostrar_token:
                mostrarToken();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void mostrarInfoSobre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sobre)
                .setMessage(R.string.sobre_descricao)
                .setPositiveButton(R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void abrirForm(Tarefa tarefa) {
        FormDialog dialog = new FormDialog(tarefa, new SaveCallback() {
            @Override
            public void save(Tarefa tarefa) {
                if (dao.salvar(tarefa)) {
                    atualizarLista();
                } else {
                    Toast.makeText(MainActivity.this, R.string.salvar_tarefa_erro, Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show(getSupportFragmentManager(), "TAG");
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editar:
                abrirForm(tarefaSelecionada);
                return true;
            case R.id.excluir:
                excluir(tarefaSelecionada.getId());
                return true;
            default:
                return false;
        }
    }

    private void excluir(final Long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.atencao)
                .setMessage(R.string.confirmar_excluir)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dao.excluir(id)) {
                            atualizarLista();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.excluir_tarefa_erro, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contexto, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Tarefa tarefa = adapter.getItem(menuInfo.position);
        switch (item.getItemId()) {
            case R.id.detalhes:
                mostrarDetalhes(tarefa);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void mostrarDetalhes(Tarefa tarefa) {
        LayoutInflater inflater = getLayoutInflater();
        View container = inflater.inflate(R.layout.dialog_detalhes_fragment, null);

        TextView txtCodigo = container.findViewById(R.id.txtCodigo);
        TextView txtDescricao = container.findViewById(R.id.txtDescricao);
        TextView txtPrazo = container.findViewById(R.id.txtPrazo);

        txtCodigo.setText(getString(R.string.codigo_detalhe, String.valueOf(tarefa.getId())));
        txtDescricao.setText(getString(R.string.descricao_detalhe, tarefa.getDescricao()));
        txtPrazo.setText(getString(R.string.prazo_detalhe, tarefa.getPrazoFormatado()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.detalhes_tarefa)
                .setView(container)
                .setPositiveButton(R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void criarCanalNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = getString(R.string.id_canal);
            String nome = getString(R.string.nome_canal);
            String descricao = getString(R.string.descricao_canal);
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(id, nome, importancia);
            channel.setDescription(descricao);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void mostrarNotificacao() {
        Intent intent = new Intent(this, FiltroActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(FILTRO_REQUEST_CODE,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String id = getString(R.string.id_canal);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.titulo_notificacao))
                .setContentText(getString(R.string.conteudo_notificacao))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.conteudo_notificacao)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }

    private void ocultarNotificacao() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(0);
    }

    private void obterTokenFirebase() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("TAG", "Não foi possível obter o token");
                    return;
                }
                String token = task.getResult();
                SharedPreferences prefs = getSharedPreferences(NOME_ARQUIVO, Context.MODE_PRIVATE);
                prefs.edit().putString(CHAVE_TOKEN, token).apply();
            }
        });
    }

    private void mostrarToken() {
        SharedPreferences prefs = getSharedPreferences(NOME_ARQUIVO, Context.MODE_PRIVATE);
        final String token = prefs.getString(CHAVE_TOKEN, getString(R.string.nao_gerado));

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.token_firebase)
                .setMessage(token)
                .setNeutralButton(R.string.copiar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData data = ClipData.newPlainText(getString(R.string.token_firebase), token);
                        clipboard.setPrimaryClip(data);

                        Toast.makeText(MainActivity.this, R.string.token_copiado, Toast.LENGTH_LONG).show();
                    }
                })
                .setPositiveButton(R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarParametros(Intent intent) {
        StringBuilder builder = new StringBuilder();
        if (intent != null) {
            String parametro1 = intent.getStringExtra(ServicoMensagens.CHAVE_PARAMETRO_1);
            if (parametro1 != null) {
                builder.append(parametro1);
                builder.append("\n");
            }
            String parametro2 = intent.getStringExtra(ServicoMensagens.CHAVE_PARAMETRO_2);
            if (parametro2 != null) {
                builder.append(parametro2);
                builder.append("\n");
            }
        }
        String mensagem = builder.toString();
        if (!mensagem.isEmpty()) {
            Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
        }
    }

    public interface SaveCallback {
        void save(Tarefa tarefa);
    }
    
    public static class FormDialog extends DialogFragment implements DialogInterface.OnClickListener {
        
        private SaveCallback saveCallback;
        private Tarefa tarefa;
        private EditText edtCodigo;
        private EditText edtDescricao;
        private EditText edtPrazo;
        
        public FormDialog(Tarefa tarefa, SaveCallback saveCallback) {
            this.tarefa = tarefa;
            this.saveCallback = saveCallback;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View container = inflater.inflate(R.layout.dialog_form_fragment, null);
            
            edtCodigo = container.findViewById(R.id.edtCodigo);
            edtDescricao = container.findViewById(R.id.edtDescricao);
            edtPrazo = container.findViewById(R.id.edtPrazo);
            
            if (tarefa != null) {
                edtCodigo.setText(String.valueOf(tarefa.getId()));
                edtDescricao.setText(tarefa.getDescricao());
                edtPrazo.setText(tarefa.getPrazoFormatado());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(tarefa == null ? R.string.nova_tarefa : R.string.alterar_tarefa)
                    .setView(container)
                    .setPositiveButton(R.string.salvar, this)
                    .setNegativeButton(R.string.cancelar, null);
            return builder.create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String descricao = edtDescricao.getText().toString();
            if (descricao.trim().isEmpty()) {
                Toast.makeText(getActivity(), R.string.informe_descricao, Toast.LENGTH_LONG).show();
                edtDescricao.requestFocus();
            } else {
                Tarefa tarefa = new Tarefa();
                tarefa.setDescricao(descricao);
                try {
                    Date prazo = new SimpleDateFormat(Tarefa.FORMATO_VISUALIZACAO)
                            .parse(edtPrazo.getText().toString());
                    tarefa.setPrazo(prazo);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String codigo = edtCodigo.getText().toString();
                if (!codigo.isEmpty()) {
                    tarefa.setId(Long.parseLong(codigo));
                }
                if (saveCallback != null) {
                    saveCallback.save(tarefa);
                }
                dialog.dismiss();
            }
        }
    }
}