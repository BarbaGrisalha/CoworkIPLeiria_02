package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class LoginFragment extends Fragment {

    private EditText etEmail, etSenha;
    private Button btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Inicializa views
        etEmail = view.findViewById(R.id.etEmail);
        etSenha = view.findViewById(R.id.etSenha);
        btnLogin = view.findViewById(R.id.btnLogin);

        // Botão de login
        btnLogin.setOnClickListener(v -> realizarLogin());

        return view;
    }

    private void realizarLogin() {
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        // Validação básica
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(getContext(), "Preencha email e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        // Consulta no Room
        User user = AppDatabase.getDatabase(requireContext()).userDao().login(email, senha);

        if (user != null) {
            // Login OK
            SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("userId", user.id);
            editor.putString("userEmail", user.email);
            editor.putString("userNome", user.nome);
            editor.apply();

            Toast.makeText(getContext(), "Bem-vindo, " + user.nome + "!", Toast.LENGTH_SHORT).show();

            // Navega para tela principal (ex: SalasFragment ou MainActivity)
            // Exemplo: substitui o fragment atual por um novo
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content_container, new SalasFragment())  // muda para o teu fragment principal
                    .commit();

            NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_salas);

        } else {
            Toast.makeText(getContext(), "Credenciais inválidas", Toast.LENGTH_SHORT).show();
        }
    }
}