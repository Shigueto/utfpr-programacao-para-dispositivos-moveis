package br.edu.utfpr.calculadora;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText edtValor1;
    EditText edtValor2;
    TextView txtResultado;
    Button btnSomar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtValor1 = findViewById(R.id.edtValor1);
        edtValor2 = findViewById(R.id.edtValor2);
        txtResultado = findViewById(R.id.txtResultado);
        btnSomar = findViewById(R.id.btnSomar);

        btnSomar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, R.string.sobre_somar,
                        Toast.LENGTH_LONG).show();
                return true; // eventos subsequentes não serão executados
            }
        });
    }

    public void onSomarClick(View v) {
        try {
            Locale locale = Locale.getDefault();
            Log.d("TESTE", "Linguagem: " + locale.getLanguage());
            Log.d("TESTE", "País: " + locale.getCountry());
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            // Entrada de dados
            Double valor1 = nf.parse(edtValor1.getText().toString()).doubleValue();
            Double valor2 = nf.parse(edtValor2.getText().toString()).doubleValue();
            // Processamento
            Double soma = valor1 + valor2;
            // Saída
            txtResultado.setText(nf.format(soma));
        } catch (ParseException ex) {
            ex.printStackTrace();
            Toast toast = Toast.makeText(this, R.string.valores_invalidos,
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void onLimparClick(View v) {
        edtValor1.getText().clear();
        edtValor2.getText().clear();
        txtResultado.setText(R.string.zeros);
        edtValor1.requestFocus();
    }
}