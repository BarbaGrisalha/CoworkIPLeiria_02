package pt.ipleiria.estg.dei.coworkipleiria_02;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {
    private EditText edtUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        edtUrl = findViewById(R.id.edt_url);
        edtUrl.setText(ApiConfig.getBaseUrl(this));

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> {
            String url = edtUrl.getText().toString().trim();
            if (!url.endsWith("/")) url += "/";
            ApiConfig.setBaseUrl(this, url);
            Toast.makeText(this, "URL salva: " + url, Toast.LENGTH_SHORT).show();
            finish();  // Volta para tela anterior
        });
    }
}