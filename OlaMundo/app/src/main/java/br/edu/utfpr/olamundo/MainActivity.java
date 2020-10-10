package br.edu.utfpr.olamundo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView txtNome = new TextView(this);
        txtNome.setText("Nome: ");
        EditText edtNome = new EditText(this);
        Button btnMensagem = new Button(this);
        btnMensagem.setText("Visualizar mensagem");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(txtNome);
        layout.addView(edtNome);
        layout.addView(btnMensagem);

        setContentView(layout);
    }
}