package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etNome, etTelefone, etMorada;
    private Button btnSave;
    private String authToken;
    private static final String API_URL = "http://10.0.2.2:8080/cowork/api/web/customers/update"; // AJUSTA PARA O TEU ENDPOINT REAL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("EditProfile", "onCreate iniciado");

        try {
            setContentView(R.layout.activity_edit_profile);
            Log.d("EditProfile", "Layout inflado com sucesso");

            etNome     = findViewById(R.id.etNome);
            etTelefone = findViewById(R.id.etTelefone);
            etMorada   = findViewById(R.id.etMorada);
            btnSave    = findViewById(R.id.btnSave);

            if (etNome == null || etTelefone == null || etMorada == null || btnSave == null) {
                Log.e("EditProfile", "Algum view é null – verifica IDs no XML");
                Toast.makeText(this, "Erro interno: campos não encontrados", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            authToken = getSharedPreferences("user", MODE_PRIVATE)
                    .getString("auth_token", null);

            if (authToken == null || authToken.trim().isEmpty()) {
                Log.w("EditProfile", "Sem token válido → redireciona para login");
                Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_LONG).show();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, new LoginFragment())
                        .addToBackStack(null)
                        .commit();

                finish();
                return;
            }

            Log.d("EditProfile", "Token válido encontrado (início): " + authToken.substring(0, Math.min(10, authToken.length())) + "...");

            // Preenche os campos com dados salvos no login
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            etNome.setText(prefs.getString("nome", ""));
            // etTelefone.setText(prefs.getString("telefone", ""));  // descomenta se salvares
            // etMorada.setText(prefs.getString("morada", ""));      // descomenta se salvares

            btnSave.setOnClickListener(v -> updateProfile());

        } catch (Exception e) {
            Log.e("EditProfile", "Erro grave em onCreate", e);
            Toast.makeText(this, "Erro ao carregar perfil", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void updateProfile() {
        String nome     = etNome.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();
        String morada   = etMorada.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "O nome é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nome",     nome);
            jsonBody.put("telefone", telefone);
            jsonBody.put("morada",   morada);
        } catch (JSONException e) {
            Log.e("EditProfile", "Erro ao criar JSON", e);
            Toast.makeText(this, "Erro interno ao preparar dados", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                API_URL,
                jsonBody,
                response -> {
                    Log.d("EditProfile", "Sucesso: " + response.toString());
                    Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    String msg = "Erro ao atualizar perfil";
                    if (error.networkResponse != null) {
                        msg += " (código: " + error.networkResponse.statusCode + ")";
                        try {
                            String body = new String(error.networkResponse.data);
                            Log.e("EditProfile", "Resposta de erro: " + body);
                        } catch (Exception ignored) {}
                    }
                    Log.e("EditProfile", "Volley error", error);
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken.trim());  // trim() para evitar espaços
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                Log.d("EditProfile", "Enviando header Authorization: Bearer " + authToken.substring(0, 10) + "...");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}