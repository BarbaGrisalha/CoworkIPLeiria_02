package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import pt.ipleiria.estg.dei.coworkipleiria_02.model.User;

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

        // Hamburger icone
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);
        }

        // Verifica se já tá logado
        if (isUserLoggedIn()) {
            //Se logado vai pra sala
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, new SalasFragment())
                        .commit();
                navigationView.setCheckedItem(R.id.nav_salas);
            }
        } else {
            // Não logado volta pra LoginFragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content_container, new LoginFragment())
                    .commit();
        }
        atualizarHeaderUsuario();
    }


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
        } else if (id == R.id.nav_edit_profile) {
            startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
        } else if (id == R.id.nav_logout) {
            // Logout: limpa sessão
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Toast.makeText(this, "Sessão encerrada. Até breve!", Toast.LENGTH_SHORT).show();
            carregarFragment(new LoginFragment());

            // Recarrega o navigationView para marcar o item correto
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_salas);
        }

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

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);  // chave correta do login
        String nome = prefs.getString("nome", "Não logado");
        String email = prefs.getString("email", "");  // se salvar email no login

        if (userId != -1) {
            tvNome.setText(nome);
            tvEmail.setText(email);
        } else {
            tvNome.setText("Não logado");
            tvEmail.setText("");
        }
    }
}