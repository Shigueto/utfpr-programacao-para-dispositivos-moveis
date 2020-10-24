package br.edu.utfpr.listas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PessoaAdapter extends BaseAdapter {

    private final Context context;
    private final List<Pessoa> pessoas;

    public PessoaAdapter(Context context, List<Pessoa> pessoas) {
        this.context = context;
        this.pessoas = pessoas;
    }

    @Override
    public int getCount() {
        return pessoas.size();
    }

    @Override
    public Pessoa getItem(int position) {
        return pessoas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View container = inflater.inflate(R.layout.elemento_lista, null);
        TextView nome = container.findViewById(R.id.nome);
        TextView email = container.findViewById(R.id.email);
        Pessoa pessoa = getItem(position);
        nome.setText(pessoa.getNome());
        email.setText(pessoa.getEmail());
        return container;
    }
}
