package pt.ipleiria.estg.dei.coworkipleiria_02;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private EditText etEmail, etSenha;
    private Button btnLogin;
    private RequestQueue requestQueue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        requestQueue = Volley.newRequestQueue(getContext());

        // Inicializa views
        etEmail = view.findViewById(R.id.etEmail);
        etSenha = view.findViewById(R.id.etSenha);
        btnLogin = view.findViewById(R.id.btnLogin);

        // Botão de login
        btnLogin.setOnClickListener(v -> realizarLogin());


        TextView tvCadastrar = view.findViewById(R.id.tvCadastrar);
        tvCadastrar.setOnClickListener(v -> {
            // Navega para CadastroFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content_container, new CadastroFragment())
                    .addToBackStack(null)  // Permite voltar com back button
                    .commit();
        });

        return view;
    }

    String url = "http://10.0.2.2:8080/cowork/api/web/auth/login";

    // ... imports e onCreateView iguais ...

    private void realizarLogin() {
        String username = etEmail.getText().toString().trim();
        String password = etSenha.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Preencha username e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao preparar dados", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Log.d("LOGIN_OK", response.toString());

                    boolean success = response.optBoolean("success", false);

                    if (success) {
                        JSONObject user = response.optJSONObject("user");
                        String token = response.optString("token", null);

                        // Declara prefs e editor AQUI, dentro do success
                        SharedPreferences prefs = getContext().getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();  // ← aqui está o editor

                        if (user != null) {
                            int userId = user.optInt("id", -1);
                            String nome = user.optString("nome", "Usuário");
                            int customerId = user.optInt("customer_id", -1);

                            editor.putInt("user_id", userId);
                            editor.putInt("customer_id", customerId);
                            editor.putString("nome", nome);

                            if (token != null && !token.isEmpty()) {
                                editor.putString("auth_token", token);
                                Log.d("LOGIN_TOKEN", "Token salvo: " + token);
                            } else {
                                Log.w("LOGIN_TOKEN", "Nenhum token retornado pelo backend");
                            }

                            editor.apply();  // aplica as alterações

                            Log.d("USER_ID_SALVO", "User ID: " + userId + " | Customer ID: " + customerId);
                            Toast.makeText(getContext(), "Bem-vindo, " + nome + "!", Toast.LENGTH_SHORT).show();

                            // Navega para SalasFragment
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_content_container, new SalasFragment())
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            Toast.makeText(getContext(), "Dados do usuário não encontrados", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Login falhou", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LOGIN_ERRO", error.toString());
                    if (error.networkResponse != null) {
                        Log.e("STATUS", String.valueOf(error.networkResponse.statusCode));
                        Log.e("BODY", new String(error.networkResponse.data));
                    }
                    Toast.makeText(getContext(), "Falha na conexão ou credenciais inválidas", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }
}