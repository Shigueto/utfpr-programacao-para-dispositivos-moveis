package br.edu.utfpr.conectividade;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadActivity extends AppCompatActivity {

    private ProgressBar progresso;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        progresso = findViewById(R.id.progresso);
        imageView = findViewById(R.id.imageView);
    }

    public void onIniciarDownloadClick(View v) {
        String url = "https://picsum.photos/400/600";
        new BaixarImagemTask().execute(url);
    }

    private class BaixarImagemTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setVisibility(View.GONE);
            progresso.setVisibility(View.VISIBLE);
            progresso.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progresso.setProgress(values[0]);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
//            URL url = null;
//            try {
//                url = new URL(strings[0]);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                return null;
//            }
//            Bitmap bitmap = null;
//            HttpURLConnection conexao = null;
//            try {
//                conexao = (HttpURLConnection) url.openConnection();
//                InputStream is = conexao.getInputStream();
//                bitmap = BitmapFactory.decodeStream(is);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            } finally {
//                if (conexao != null) {
//                    conexao.disconnect();
//                }
//            }
//            return bitmap;
            Bitmap bitmap = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            HttpURLConnection conexao = null;
            InputStream inputStreamArquivo = null;
            try {
                URL url = new URL(strings[0]);
                conexao = (HttpURLConnection) url.openConnection();
                conexao.connect();

                int tamanhoArquivo = conexao.getContentLength();

                inputStream = conexao.getInputStream();
                String caminhoArquivo = getFilesDir() + "/tmp.png";
                outputStream = new FileOutputStream(caminhoArquivo);

                byte[] data = new byte[4096];
                long total = 0;
                int contagem;
                while ((contagem = inputStream.read(data)) != -1) {
                    total += contagem;
                    publishProgress((int) (total * 100 / tamanhoArquivo));
                    outputStream.write(data, 0, contagem);
                }

                // abrir arquivo
                inputStreamArquivo = new FileInputStream(caminhoArquivo);
                bitmap = BitmapFactory.decodeStream(inputStreamArquivo);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                      outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStreamArquivo != null) {
                    try {
                        inputStreamArquivo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (conexao != null) {
                    conexao.disconnect();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            progresso.setVisibility(View.GONE);
            if (bitmap != null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}