package br.edu.utfpr.cadastrotarefas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CadastroTarefaHelper extends SQLiteOpenHelper {

    private static final String NOME_BD = "cadastro_tarefas";
    private static final int VERSAO_BD = 1;
    public static final String NOME_TABELA = "tarefa";
    public static final String CAMPO_ID = "_id";
    public static final String CAMPO_DESCRICAO = "descricao";
    public static final String[] CAMPOS ={CAMPO_ID, CAMPO_DESCRICAO};

    public CadastroTarefaHelper(Context context) {
        super(context, NOME_BD, null, VERSAO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + NOME_TABELA + "(" +
                CAMPO_ID + " INTEGER PRIMARY KEY, " +
                CAMPO_DESCRICAO + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
