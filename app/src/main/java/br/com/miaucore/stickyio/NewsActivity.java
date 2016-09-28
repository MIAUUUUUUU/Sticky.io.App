package br.com.miaucore.stickyio;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    public ListView lvNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        lvNews = (ListView) findViewById(R.id.lvNews);
        FetchNewsTask task = new FetchNewsTask();
        task.execute();
    }

    private class FetchNewsTask extends AsyncTask<String, Void, String> {

        protected ProgressDialog progresso;

        @Override
        protected void onPreExecute() {
            progresso = ProgressDialog.show(NewsActivity.this, "Aguarde..", "Buscando dados no servidor!");
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL("http://miaucore.azurewebsites.net/api/News");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getResponseCode() == 200) {

                    BufferedReader stream = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()) );

                    String linha = "";
                    StringBuilder builder = new StringBuilder();

                    while( (linha = stream.readLine()) != null ) {
                        builder.append(linha);
                    }

                    connection.disconnect();
                    return builder.toString();

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                progresso.dismiss();
            }

            return null;

        }


        protected void onPostExecute(String s) {
            progresso.dismiss();
            if (s != null) {

                try {
                    JSONArray arrjson = new JSONArray(s);

                    JSONObject jasao;
                    List<News> lista = new ArrayList<News>();
                    for(int i=0; i < arrjson.length(); i++) {
                        jasao = arrjson.getJSONObject(i);
                        lista.add(new News(jasao.getString("title"), jasao.getString("content")));
                    }

                    ArrayAdapter<News> adapter = new ArrayAdapter<News>(NewsActivity.this, android.R.layout.simple_list_item_1, lista);

                    lvNews.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
    }
}
