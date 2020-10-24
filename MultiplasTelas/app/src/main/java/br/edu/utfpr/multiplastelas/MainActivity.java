package br.edu.utfpr.multiplastelas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public final static int PESQUISAR_PAIS_REQUEST_CODE = 1;

    private EditText edtNome;
    private EditText edtTelefone;
    private EditText edtEmail;
    private EditText edtSite;
    private EditText edtPais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNome = findViewById(R.id.edtNome);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtEmail = findViewById(R.id.edtEmail);
        edtSite = findViewById(R.id.edtSite);
        edtPais = findViewById(R.id.edtPais);
    }

    public void onSobreClick(View v) {
        Intent intent = new Intent(this, SobreActivity.class);
        startActivity(intent);
    }

    public void onContinuarClick(View v) {
        String nome = edtNome.getText().toString();
        String telefone = edtTelefone.getText().toString();
        String email = edtEmail.getText().toString();
        String site = edtSite.getText().toString();

        if (nome.trim().isEmpty()) {
            edtNome.requestFocus();
            Toast.makeText(this, R.string.nome_obrigatorio, Toast.LENGTH_LONG).show();
        } else if (telefone.trim().isEmpty()) {
            edtTelefone.requestFocus();
            Toast.makeText(this, R.string.telefone_obrigatorio, Toast.LENGTH_LONG).show();
        } else if (email.trim().isEmpty()) {
            edtEmail.requestFocus();
            Toast.makeText(this, R.string.email_obrigatorio, Toast.LENGTH_LONG).show();
        } else if (site.trim().isEmpty()) {
            edtSite.requestFocus();
            Toast.makeText(this, R.string.site_obrigatorio, Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, ResumoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("nome", nome);
            bundle.putString("telefone", telefone);
            bundle.putString("email", email);
            bundle.putString("site", site);
            intent.putExtras(bundle);

            startActivity(intent);
        }
    }

    public void onPesquisarPaisClick(View v) {
        Intent intent = new Intent(this, PesquisarPaisActivity.class);
        startActivityForResult(intent, PESQUISAR_PAIS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PESQUISAR_PAIS_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null && bundle.containsKey("pais")) {
                String pais = bundle.getString("pais");
                edtPais.setText(pais);
            }
        }
    }
}