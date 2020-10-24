package br.edu.utfpr.multiplastelas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResumoActivity extends AppCompatActivity {
    private TextView txtNome;
    private TextView txtTelefone;
    private TextView txtEmail;
    private TextView txtSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo);

        txtNome = findViewById(R.id.txtNome);
        txtTelefone = findViewById(R.id.txtTelefone);
        txtEmail = findViewById(R.id.txtEmail);
        txtSite = findViewById(R.id.txtSite);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                txtNome.setText(bundle.getString("nome"));
                txtTelefone.setText(bundle.getString("telefone"));
                txtEmail.setText(bundle.getString("email"));
                txtSite.setText(bundle.getString("site"));
            }
        }
    }

    public void onLigarClick(View v) {
        String telefone = txtTelefone.getText().toString();
        Uri uri = Uri.parse("tel:" + telefone);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }

    public void onAcessarClick(View v) {
        String site = txtSite.getText().toString();
        Uri uri = Uri.parse(site);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void onEnviarEmailClick(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{txtEmail.getText().toString()});
        startActivity(Intent.createChooser(intent, getString(R.string.enviar_email)));
    }
}