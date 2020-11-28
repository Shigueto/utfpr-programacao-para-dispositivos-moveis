package br.edu.utfpr.cadastrotarefas;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class ServicoMensagens extends FirebaseMessagingService {

    public static final String MENSAGEM_RECEBIDA = "mensagem_recebida";
    public static final String CHAVE_PARAMETRO_1 = "parametro1";
    public static final String CHAVE_PARAMETRO_2 = "parametro2";

    @Override
    public void onNewToken(@NonNull String token) {
        SharedPreferences prefs = getSharedPreferences(MainActivity.NOME_ARQUIVO, Context.MODE_PRIVATE);
        prefs.edit().putString(MainActivity.CHAVE_TOKEN, token).apply();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // se é mensagem de notificação e caiu neste método, está em primeiro plano
        if (remoteMessage.getNotification() != null) {
            mostrarNotificacao(remoteMessage.getNotification(), remoteMessage.getData());
        } else if (remoteMessage.getData().size() > 0) {
            Intent intent = new Intent(MENSAGEM_RECEBIDA);
            if (remoteMessage.getData().containsKey(CHAVE_PARAMETRO_1)) {
                intent.putExtra(CHAVE_PARAMETRO_1, remoteMessage.getData().get(CHAVE_PARAMETRO_1));
            }
            if (remoteMessage.getData().containsKey(CHAVE_PARAMETRO_2)) {
                intent.putExtra(CHAVE_PARAMETRO_2, remoteMessage.getData().get(CHAVE_PARAMETRO_2));
            }
            sendBroadcast(intent);
        }
    }

    private void mostrarNotificacao(RemoteMessage.Notification notification, Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (data.containsKey(CHAVE_PARAMETRO_1)) {
            intent.putExtra(CHAVE_PARAMETRO_1, data.get(CHAVE_PARAMETRO_1));
        }
        if (data.containsKey(CHAVE_PARAMETRO_2)) {
            intent.putExtra(CHAVE_PARAMETRO_2, data.get(CHAVE_PARAMETRO_2));
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String id = getString(R.string.id_canal);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.titulo_notificacao))
                .setContentText(getString(R.string.conteudo_notificacao))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.conteudo_notificacao)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }
}
