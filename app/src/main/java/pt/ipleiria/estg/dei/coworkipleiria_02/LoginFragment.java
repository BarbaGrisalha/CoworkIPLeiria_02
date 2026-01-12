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

        // Bypass temporário para criar usuário teste + login automático
        if (email.equals("admin@teste.pt") && senha.equals("123456")) {
            AppDatabase db = AppDatabase.getDatabase(requireContext());
            UserDao userDao = db.userDao();

            // Verifica se o admin já existe
            User existing = userDao.getByEmail("admin@teste.pt");
            if (existing == null) {
                // Cria o usuário admin de teste
                User admin = new User();
                admin.nome = "Admin Teste";
                admin.email = "admin@teste.pt";
                admin.password = "123456";  // ← sem hash por enquanto (pra simplificar)
                // Se tiver outros campos como id, etc., ajusta

                userDao.insert(admin);
                Toast.makeText(getContext(), "Usuário admin criado automaticamente!", Toast.LENGTH_SHORT).show();
            }

            // Agora faz o login normal com o admin
            User user = userDao.login("admin@teste.pt", "123456");
            if (user != null) {
                // Salva sessão
                SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("userId", user.id);
                editor.putString("userEmail", user.email);
                editor.putString("userNome", user.nome);
                editor.apply();

                Toast.makeText(getContext(), "Bem-vindo, " + user.nome + " (teste)!", Toast.LENGTH_SHORT).show();

                // Navega pra tela principal
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, new SalasFragment())
                        .commit();

                NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
                navigationView.setCheckedItem(R.id.nav_salas);
                return;
            }
        }

        // Login normal (para outros usuários que vierem a existir)
        User user = AppDatabase.getDatabase(requireContext()).userDao().login(email, senha);
        if (user != null) {
            // ... o código de login normal que já tens
        } else {
            Toast.makeText(getContext(), "Credenciais inválidas", Toast.LENGTH_SHORT).show();
        }
    }
}