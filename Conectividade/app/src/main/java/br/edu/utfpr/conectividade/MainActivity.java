package br.edu.utfpr.conectividade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtUf;
    private EditText edtLocalidade;
    private EditText edtLogradouro;
    private ListView listaCeps;
    private ArrayAdapter<String> adapter;
//    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUf = findViewById(R.id.edtUf);
        edtLocalidade = findViewById(R.id.edtLocalidade);
        edtLogradouro = findViewById(R.id.edtLogradouro);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new ArrayList<String>());
        listaCeps = findViewById(R.id.listaCeps);
        listaCeps.setAdapter(adapter);
    }

    public void onBuscarClick(View v) {
        if (edtUf.getText().toString().trim().isEmpty()) {
            edtUf.requestFocus();
            Toast.makeText(this, R.string.uf_obrigatoria, Toast.LENGTH_LONG).show();
        } else if (edtLocalidade.getText().toString().trim().isEmpty()) {
            edtLocalidade.requestFocus();
            Toast.makeText(this, R.string.localidade_obrigatoria, Toast.LENGTH_LONG).show();
        } else if (edtLogradouro.getText().toString().trim().isEmpty()) {
            edtLogradouro.requestFocus();
            Toast.makeText(this, R.string.logradouro_obrigatorio, Toast.LENGTH_LONG).show();
        } else {
            final String uf = edtUf.getText().toString();
            final String localidade = edtLocalidade.getText().toString();
            final String logradouro = edtLogradouro.getText().toString();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    buscarListaCeps(uf, localidade, logradouro);
                }
            });
            t.start();
        }
    }

    private void buscarListaCeps(String uf, String localidade, String logradouro) {
        try {
            String retornoJson = efetuarRequisicao(uf, localidade, logradouro);

            // converter para JSON
            JSONArray array = new JSONArray(retornoJson);
            final List<String> ceps = new ArrayList<>();
            for (int i=0; i<array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String cep = obj.getString("cep");
                String log = obj.getString("logradouro");
                String bairro = obj.getString("bairro");
                String linha = "CEP: " + cep + "\nLogradouro: " + log +
                        "\nBairro: " + bairro;
                ceps.add(linha);
            }

            // atualizar interface
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    adapter.clear();
//                    if (ceps.size() > 0) {
//                        adapter.addAll(ceps);
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    if (ceps.size() > 0) {
                        adapter.addAll(ceps);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MainActivity.this, getString(R.string.buscar_ceps_erro),
//                            Toast.LENGTH_LONG).show();
//                    adapter.clear();
//                    adapter.notifyDataSetChanged();
//                }
//            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, getString(R.string.buscar_ceps_erro),
                            Toast.LENGTH_LONG).show();
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private String efetuarRequisicao(String uf, String localidade, String logradouro) throws IOException {
        HttpURLConnection conexao = null;
        String retornoJson = null;
        try {
            URL url = new URL("https://viacep.com.br/ws/" + uf + "/" + localidade +
                    "/" + logradouro + "/json/");
            conexao = (HttpURLConnection) url.openConnection();
            InputStream is = conexao.getInputStream();
            retornoJson = lerStream(is);
        } finally {
            if (conexao != null) {
                conexao.disconnect();
            }
        }
        return retornoJson;
    }

    private String lerStream(InputStream is) throws IOException {
        String retornoJson = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            String linha = null;
            while ((linha = reader.readLine()) != null) {
                builder.append(linha);
                builder.append("\n");
            }
            retornoJson = builder.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return retornoJson;
    }

    public void onAbrirPaginaDownloadClick(View v) {
        startActivity(new Intent(this, DownloadActivity.class));
    }
}