package br.com.miaucore.stickyio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void SignIn(View v) {
        EditText edtUsername = (EditText) findViewById(R.id.edtUsername);
        EditText edtPassword = (EditText) findViewById(R.id.edtPassword);

        AuthenticateTask task = new AuthenticateTask();
        task.execute(edtUsername.getText().toString(), edtPassword.getText().toString());
    }

    public class AuthenticateTask extends AsyncTask<String, Void, Integer> {

        protected ProgressDialog progresso;


        @Override
        protected void onPreExecute() {
            progresso = ProgressDialog.show(LoginActivity.this, "Aguarde..", "Verificando autenticação!");
        }

        @Override
        protected Integer doInBackground(String... strings) {

            try {
                URL url = new URL("http://miaucore.azurewebsites.net/api/Login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                JSONStringer json = new JSONStringer();
                json.object();
                json.key("Login").value(strings[0]);
                json.key("Password").value(strings[1]);
                json.endObject();

                OutputStreamWriter stream = new OutputStreamWriter(connection.getOutputStream());
                stream.write(json.toString());
                stream.close();

                return connection.getResponseCode();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                progresso.dismiss();
            }

            return null;

        }


        protected void onPostExecute(Integer code) {
            progresso.dismiss();
            if (code == 200) {
                Intent i = new Intent(LoginActivity.this, NewsActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(LoginActivity.this, "Falha ao realizar a autenticação, tente novamente mais tarde!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
