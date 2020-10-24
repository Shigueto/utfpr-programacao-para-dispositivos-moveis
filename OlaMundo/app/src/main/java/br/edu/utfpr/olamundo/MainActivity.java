package br.edu.utfpr.olamundo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

        Log.d("TESTE", "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TESTE", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TESTE", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TESTE", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TESTE", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TESTE", "onDestroy");
    }
}