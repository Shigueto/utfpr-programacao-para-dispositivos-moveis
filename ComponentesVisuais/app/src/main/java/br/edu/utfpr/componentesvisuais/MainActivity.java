package br.edu.utfpr.componentesvisuais;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView txtProgresso;
    private SeekBar sbProgresso;
    private DatePicker data;
    private TimePicker hora;
    private ProgressBar progresso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtProgresso = findViewById(R.id.txtProgresso);
        sbProgresso = findViewById(R.id.sbProgresso);
        sbProgresso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtProgresso.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        data = findViewById(R.id.data);
        hora = findViewById(R.id.hora);

        progresso = findViewById(R.id.progresso);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=1; i <= progresso.getMax(); i++) {
                    progresso.setProgress(i);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void mostrarDataHora(View v) {
        int dia = data.getDayOfMonth();
        int mes = data.getMonth() + 1;
        int ano = data.getYear();

        String dataFormatada = dia + "/" + mes + "/" + ano;

        int horas = hora.getCurrentHour();
        int minutos = hora.getCurrentMinute();

        String horaFormatada = horas + ":" + minutos;

        Toast.makeText(this,
                dataFormatada + " " + horaFormatada,
                Toast.LENGTH_LONG).show();
    }

}