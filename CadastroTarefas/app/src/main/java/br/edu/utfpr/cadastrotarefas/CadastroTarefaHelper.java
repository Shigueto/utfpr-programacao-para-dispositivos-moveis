package br.edu.utfpr.cadastrotarefas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CadastroTarefaHelper extends SQLiteOpenHelper {

    private static final String NOME_BD = "cadastro_tarefas";
    private static final int VERSAO_BD = 3;
    public static final String NOME_TABELA = "tarefa";
    public static final String CAMPO_ID = "_id";
    public static final String CAMPO_DESCRICAO = "descricao";
    public static final String CAMPO_PRAZO = "prazo";
    public static final String[] CAMPOS ={CAMPO_ID, CAMPO_DESCRICAO, CAMPO_PRAZO};

    public CadastroTarefaHelper(Context context) {
        super(context, NOME_BD, null, VERSAO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + NOME_TABELA + "(" +
                CAMPO_ID + " INTEGER PRIMARY KEY, " +
                CAMPO_DESCRICAO + " TEXT, " +
                CAMPO_PRAZO + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + NOME_TABELA + " ADD " + CAMPO_PRAZO + " TEXT;");
        }
    }
}
