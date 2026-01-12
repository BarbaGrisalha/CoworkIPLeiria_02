package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

        AppDatabase db = AppDatabase.getDatabase(requireContext());
        UserDao userDao = db.userDao();

        if (userDao.getByEmail(email) != null) {
            Toast.makeText(getContext(), "Email já cadastrado", Toast.LENGTH_SHORT).show();
            return;
        }

        User novoUser = new User(email, senha, nome);
        userDao.insert(novoUser);

        Toast.makeText(getContext(), "Cadastro realizado com sucesso! Faça login.", Toast.LENGTH_SHORT).show();

        // Volta para LoginFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content_container, new LoginFragment())
                .commit();
    }
}