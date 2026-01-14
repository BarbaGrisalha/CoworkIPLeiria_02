package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private static final String PREFS_NAME = "session";
    private static final String KEY_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Hamburger icon
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);
        }

        // Verifica se já está logado
        if (isUserLoggedIn()) {
            // Já logado → carrega tela principal (salas)
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, new SalasFragment())
                        .commit();
                navigationView.setCheckedItem(R.id.nav_salas);
            }
        } else {
            // Não logado → mostra LoginFragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content_container, new LoginFragment())
                    .commit();
        }
        atualizarHeaderUsuario();
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d("MainActivity", "onCreate started");
//        setContentView(R.layout.activity_main);
//        Log.d("MainActivity", "setContentView done");
//        // ... resto
//        if (!isUserLoggedIn()) {
//            Log.d("MainActivity", "Loading LoginFragment");
//            getSupportFragmentManager().beginTransaction().replace(R.id.main_content_container, new LoginFragment()).commit();
//            Log.d("MainActivity", "Fragment committed");
//        }
//    }
    // Verifica se tem userId salvo (sessão ativa)
    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int userId = prefs.getInt(KEY_USER_ID, -1);
        return userId != -1;
    }

    private void carregarFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content_container, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_salas) {
            carregarFragment(new SalasFragment());
        } else if (id == R.id.nav_minhas_reservas) {
            carregarFragment(new MinhasReservasFragment());
        } else if (id == R.id.nav_faturas) {
            carregarFragment(new EmitirFaturasFragment());
        } else if (id == R.id.nav_logout) {
            // Logout: limpa a sessão
            SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
            prefs.edit().clear().apply();  // Remove userId, email, nome, etc.

            // Mostra toast de confirmação
            Toast.makeText(this, "Sessão encerrada. Até breve!", Toast.LENGTH_SHORT).show();

            // Carrega o LoginFragment
            carregarFragment(new LoginFragment());

            // Opcional: desmarca itens do menu ou atualiza header do drawer
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_salas); // ou nenhum

        }
        // ... outros itens

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void atualizarHeaderUsuario() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView tvNome = headerView.findViewById(R.id.tv_header_nome);
        TextView tvEmail = headerView.findViewById(R.id.tv_header_email);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId != -1) {
            // Carrega o utilizador do BD
            UserDao userDao = AppDatabase.getDatabase(this).userDao();
            User user = userDao.getUserById(userId);  // tens este método no DAO?

            if (user != null) {
                tvNome.setText(user.getNome());
                tvEmail.setText(user.getEmail());
            } else {
                tvNome.setText("Utilizador não encontrado");
                tvEmail.setText("");
            }
        } else {
            tvNome.setText("Não logado");
            tvEmail.setText("");
        }
    }
}