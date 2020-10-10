package br.edu.utfpr.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
                Toast.makeText(MainActivity.this,
                        "Esse botão serve para somar dois valores",
                        Toast.LENGTH_LONG).show();
                return true; // eventos subsequentes não serão executados
            }
        });
    }

    public void onSomarClick(View v) {
        try {
            // Entrada de dados
            Double valor1 = Double.parseDouble(edtValor1.getText().toString());
            Double valor2 = Double.parseDouble(edtValor2.getText().toString());
            // Processamento
            Double soma = valor1 + valor2;
            // Saída
            txtResultado.setText(String.valueOf(soma));
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Toast toast = Toast.makeText(this,
                    "Informe valores válidos",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void onLimparClick(View v) {
        edtValor1.getText().clear();
        edtValor2.getText().clear();
        txtResultado.setText("0.0");
        edtValor1.requestFocus();
    }
}