package br.edu.utfpr.multiplastelas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PesquisarPaisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar_pais);

        ListView listaPaises = findViewById(R.id.listaPaises);

        String[] paises = {"Argentina", "Brasil", "Paraguai", "Uruguai"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, paises);

        listaPaises.setAdapter(adapter);

        listaPaises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pais = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra("pais", pais);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}