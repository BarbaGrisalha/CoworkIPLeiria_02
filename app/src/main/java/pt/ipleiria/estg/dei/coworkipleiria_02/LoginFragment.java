package pt.ipleiria.estg.dei.coworkipleiria_02;

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

        // Novo: Listener para o texto de cadastro
        TextView tvCadastrar = view.findViewById(R.id.tvCadastrar);  // Adicione isso no layout
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

//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        Log.d("LoginFragment", "onCreateView started");
//        View view = inflater.inflate(R.layout.fragment_login, container, false);
//        Log.d("LoginFragment", "inflate done");
//        return view;
//    }
    private void realizarLogin() {
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        // Validação básica
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(getContext(), "Preencha email e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        // Login normal usando o DAO existente
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        UserDao userDao = db.userDao();
        User user = userDao.login(email, senha);

        if (user != null) {
            // Salva sessão
            SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("userId", user.id);
            editor.putString("userEmail", user.email);
            editor.putString("userNome", user.nome);
            editor.apply();

            Toast.makeText(getContext(), "Bem-vindo, " + user.nome + "!", Toast.LENGTH_SHORT).show();

            // Atualiza o header do drawer com os dados do utilizador logado
            ((MainActivity) requireActivity()).atualizarHeaderUsuario();  // ← ESSA LINHA RESOLVE O BUG

            // Navega pra tela principal
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content_container, new SalasFragment())
                    .commit();

            NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_salas);
        } else {
            Toast.makeText(getContext(), "Credenciais inválidas", Toast.LENGTH_SHORT).show();
        }
    }
}