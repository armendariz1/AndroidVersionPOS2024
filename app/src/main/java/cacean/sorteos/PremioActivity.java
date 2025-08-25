package cacean.sorteos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PremioActivity extends Activity implements View.OnClickListener {

    private Button scanBtn;
    private Button verifBtn;
    private TextView contentTxt;

    private ExecutorService executor; // para llamadas en background

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_premio);

        scanBtn  = findViewById(R.id.btnScan);
        verifBtn = findViewById(R.id.btnVerif);
        contentTxt = findViewById(R.id.txtTicket);

        scanBtn.setOnClickListener(this);
        verifBtn.setOnClickListener(this);

        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdownNow();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnScan) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
            return;
        }

        if (id == R.id.btnVerif) {
            String ticket = contentTxt.getText() != null ? contentTxt.getText().toString().trim() : "";
            if (ticket.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "Debe teclear o escanear el ticket", Toast.LENGTH_SHORT).show();
                return;
            }
            verificarTicket(ticket);
        }
    }

    private void verificarTicket(String ticket) {
        // Dialog de carga (no Material)
        AlertDialog loading = createLoadingDialog("Por favor espere", "Verificando ticket");
        loading.show();

        executor.execute(() -> {
            String res;
            try {
                CargaDatosWS ws = new CargaDatosWS();
                res = ws.validaTicket(ticket);
            } catch (Exception e) {
                res = "ERROR: " + e.getMessage();
            }

            final String resultado = res;
            runOnUiThread(() -> {
                if (!isFinishing()) loading.dismiss();
                procesarResultadoYMostrarDialog(ticket, resultado);
            });
        });
    }

    private void procesarResultadoYMostrarDialog(String ticket, String res) {
        boolean ganador = false;
        String titulo = "Suerte para la próxima!";
        String mensaje = res != null ? res : "";

        if (mensaje.length() > 2 && mensaje.startsWith("OK")) {
            mensaje = mensaje.substring(2);
        }

        // Chequeo seguro del segmento "GANADOR" en posiciones [7,14)
        if (mensaje.length() >= 14) {
            String gg = mensaje.substring(7, 14).toUpperCase(Locale.ROOT);
            if ("GANADOR".equals(gg)) {
                ganador = true;
                titulo = "¡Felicidades!";
                mensaje = mensaje + "\n\n¿Deseas marcar como PAGADO el boleto?";
            }
            if ("PAGADO ".equals(gg)) {
                titulo = "¡TICKET YA PAGADO!";
            }
        }

        AlertDialog.Builder b = new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setCancelable(false);

        if (ganador) {
            b.setPositiveButton("Sí", (d, w) -> {
                d.dismiss();
                marcarTicketPagado(ticket);
            });
            b.setNegativeButton("No", (d, w) -> {
                d.dismiss();
                Toast.makeText(this, "NO se pagó el ticket", Toast.LENGTH_SHORT).show();
            });
        } else {
            b.setPositiveButton("OK", (d, w) -> d.dismiss());
        }

        b.show();
    }

    private void marcarTicketPagado(String ticket) {
        AlertDialog loading = createLoadingDialog("Procesando", "Marcando ticket como pagado…");
        loading.show();

        executor.execute(() -> {
            String respuesta;
            try {
                CargaDatosWS ws = new CargaDatosWS();
                respuesta = ws.marcaTicketPagado(ticket, Usuario.user); // <-- si tienes ws.marcaticketpagado(ticket), cámbialo aquí
            } catch (Exception e) {
                respuesta = "ERROR: " + e.getMessage();
            }

            final String finalRespuesta = respuesta;
            runOnUiThread(() -> {
                if (!isFinishing()) loading.dismiss();
                new AlertDialog.Builder(this)
                        .setTitle("Resultado")
                        .setMessage(finalRespuesta)
                        .setPositiveButton("OK", (dd, ww) -> dd.dismiss())
                        .setCancelable(false)
                        .show();
            });
        });
    }

    private AlertDialog createLoadingDialog(CharSequence title, CharSequence message) {
        View spinner = getLayoutInflater().inflate(R.layout.dialog_progress_indeterminate, null, false);
        return new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setView(spinner)   // vista con ProgressBar indeterminado
                .setCancelable(false)
                .create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            // Guardamos solo el contenido (sin prefijos) para que el servicio reciba el ticket limpio
            contentTxt.setText(scanContent != null ? scanContent : "");
        } else {
            Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT).show();
        }
    }
}
