package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CadastroFragment extends Fragment {

    private EditText etNome, etEmail, etSenha, etConfirmarSenha;
    private Button btnCadastrar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro, container, false);

        etNome = view.findViewById(R.id.etNome);
        etEmail = view.findViewById(R.id.etEmail);
        etSenha = view.findViewById(R.id.etSenha);
        etConfirmarSenha = view.findViewById(R.id.etConfirmarSenha);
        btnCadastrar = view.findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(v -> realizarCadastro());

        return view;
    }

    private void realizarCadastro() {
        String nome = etNome.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();
        String confirmarSenha = etConfirmarSenha.getText().toString().trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            Toast.makeText(getContext(), "Senhas não coincidem", Toast.LENGTH_SHORT).show();
            return;
        }

        // JSON para POST no backend
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nome", nome);
            jsonBody.put("email", email);
            jsonBody.put("password", senha);
            jsonBody.put("password_confirmation", confirmarSenha);
            // Adicione outros campos se o backend exigir (ex.: username, nif, etc.)
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Erro ao preparar dados", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8080/cowork/api/web/auth/register";  // Ajuste o endpoint de cadastro real

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Log.d("CADASTRO_OK", response.toString());
                    boolean success = response.optBoolean("success", false);

                    if (success) {
                        Toast.makeText(getContext(), "Cadastro realizado com sucesso! Faça login.", Toast.LENGTH_LONG).show();

                        // Navega para login
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_content_container, new LoginFragment())
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), response.optString("message", "Erro no cadastro"), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("CADASTRO_ERRO", error.toString());
                    String msg = "Falha no cadastro";
                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        Log.e("CADASTRO_BODY", body);
                        msg += " - " + error.networkResponse.statusCode + ": " + body;
                    }
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
}