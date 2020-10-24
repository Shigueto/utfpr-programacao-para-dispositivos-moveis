package br.edu.utfpr.listas;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private AutoCompleteTextView autoComplete;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SPINNER
        spinner = findViewById(R.id.spinner);

        final String[] estados = {"Paraná", "Santa Catarina", "Rio Grande do Sul"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, estados);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String item = estados[position];
//                String item = (String) parent.getSelectedItem();
                String item = (String) parent.getItemAtPosition(position);
                Log.d("TESTE", "Item selecionado: " + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // AUTOCOMPLETE
        autoComplete = findViewById(R.id.autoComplete);

        ArrayAdapter<CharSequence> adapterAuto =
                ArrayAdapter.createFromResource(this, R.array.paises,
                        android.R.layout.simple_list_item_1);

        autoComplete.setAdapter(adapterAuto);
        autoComplete.setThreshold(1);

        // LISTVIEW
        listView = findViewById(R.id.listView);

//        ArrayAdapter<CharSequence> adapterList =
//                ArrayAdapter.createFromResource(this, R.array.paises, R.layout.elemento_lista);
//
//        listView.setAdapter(adapterList);

        List<Pessoa> pessoas = new ArrayList<>();
        pessoas.add(new Pessoa("Ana", "ana@email.com"));
        pessoas.add(new Pessoa("João", "joao@email.com"));
        pessoas.add(new Pessoa("José", "jose@email.com"));
        pessoas.add(new Pessoa("Maria", "maria@email.com"));

        PessoaAdapter adapterList = new PessoaAdapter(this, pessoas);

        listView.setAdapter(adapterList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                Log.d("TESTE", "Item selecionado no ListView: " + item);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                Log.d("TESTE", "Item selecionado no ListView com clique longo: " + item);
                return true;
            }
        });
    }

    public void mostrarItemSelecionado(View v) {
        String item = (String) spinner.getSelectedItem();
        Log.d("TESTE", "Item selecionado: " + item);
    }
}