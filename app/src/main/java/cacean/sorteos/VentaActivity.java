package cacean.sorteos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.minipos.device.SDK;

import org.ksoap2.serialization.SoapObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class VentaActivity extends Activity implements OnClickListener {

    private Spinner lvw;
    private GridView gvw;
    private EditText numeroTxt;
    static Resources aux;
    private EditText lugar1;
    private EditText lugar2;
    private EditText lugar3;
    private EditText CopiaTicket;
    private String sorteoId;
    private String apuestaId;
    private TextView usuarioTxt;
    private TextView fechaTxt;
    private TextView strUltTicket;
    private TextView total;
    private TextView cantAp;
    private Button limpiar;
    private Button imprimir;
    private Button agregar;
    private Button cancelar;
    private Button salir;
    private Button copiar;
    private Context con;
    private String strNumero;
    private String strTicket;
    private String ErrorApuesta;
    private Boolean editApuesta;
    private String strLugar1;
    private String strLugar2;
    private String strLugar3;
    private List<String> strApuestas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta2);
        aux = getResources();
        usuarioTxt = findViewById(R.id.tvwUsuario);
        usuarioTxt.append(Usuario.nombre);
        fechaTxt = findViewById(R.id.tvwFecha);
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        fechaTxt.append(dateFormat.format(date));
        total = findViewById(R.id.txtTotal);
        strUltTicket = findViewById(R.id.txtUltTicket);
        cantAp = findViewById(R.id.txtCantAp);
        limpiar = findViewById(R.id.btnLimpiar);
        limpiar.setOnClickListener(this);
        agregar = findViewById(R.id.btnAgregar);
        agregar.setOnClickListener(this);
        imprimir = findViewById(R.id.btnImprimir);
        imprimir.setOnClickListener(this);

        copiar = findViewById(R.id.btnCopiar);
        copiar.setOnClickListener(this);

        cancelar = findViewById(R.id.btnCancelar);
        cancelar.setOnClickListener(this);
        salir = findViewById(R.id.btnSalir);
        salir.setOnClickListener(this);
        editApuesta = false;
        con = this;
        numeroTxt = (EditText)findViewById(R.id.txtNumero);
        lugar1 = (EditText)findViewById(R.id.txtLugar1);
        lugar2 = (EditText)findViewById(R.id.txtLugar2);
        lugar3 = (EditText)findViewById(R.id.txtLugar3);
        CopiaTicket = (EditText)findViewById(R.id.txtCopiar) ;

        imprimir.setEnabled(false);
        lvw = (Spinner)findViewById(R.id.lvwSorteos);
        gvw = (GridView)findViewById(R.id.gvApuestas);
        new DownloadTasklvw().execute("");
        new DownloadTaskLimpia().execute("");

        lvw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                sorteoId = ((SorteoLvw) arg0.getItemAtPosition(arg2)).getId();
                Log.e("Selected item : ", sorteoId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) { }
        });

        gvw.setOnItemClickListener((parent, view, position, id) -> {
            final int BLOCK = 5;

            if (strApuestas == null || strApuestas.isEmpty()) return;

            // Snapshot defensivo por si strApuestas cambia en otro hilo
            final ArrayList<String> data = new ArrayList<>(strApuestas);
            final int size = data.size();

            // Si por cualquier carrera aún no es múltiplo de 5, no hagas nada
            if (size < BLOCK || (size % BLOCK) != 0) return;

            // Ancla al inicio de bloque de 5
            final int base = (position / BLOCK) * BLOCK;

            // Verifica que haya 5 elementos desde 'base'
            if (base < 0 || base + BLOCK - 1 >= size) return;

            // Lee seguro
            apuestaId = safeGet(data, base);
            strNumero = safeGet(data, base + 1);
            strLugar1 = safeGet(data, base + 2);
            strLugar2 = safeGet(data, base + 3);
            strLugar3 = safeGet(data, base + 4);

            // Usa contexto del view para evitar problemas de contexto
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("Seleccione la operación para la apuesta: " + apuestaId + ".")
                    .setTitle("Atención!!")
                    .setCancelable(true)
                    .setNegativeButton("Editar", (d, which) -> {
                        editApuesta = true;
                        EditarApuesta(apuestaId, strNumero, strLugar1, strLugar2, strLugar3);
                    })
                    .setPositiveButton("Borrar", (d, which) -> {
                        new DownloadTaskDelete().execute("");
                    })
                    .show();
        });
    }

    // Helper para evitar NPE
    private static String safeGet(List<String> list, int index) {
        if (list == null || index < 0 || index >= list.size()) return "";
        String v = list.get(index);
        return v == null ? "" : v;
    }

    // Rellena a múltiplos de 5 (evita filas incompletas)
    private static void padToBlocksOf5(List<String> list) {
        if (list == null) return;
        int rem = list.size() % 5;
        if (rem != 0) {
            for (int i = 0; i < 5 - rem; i++) list.add("");
        }
    }

    public void onClick(View v){
        if(v.getId()==R.id.btnSalir){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            finish();
        }
        if(v.getId()==R.id.btnImprimir){
            imprimir.setEnabled(false);
            copiar.setEnabled(true);
            try {
                SDK.init(this);
            } catch (Throwable e1) {
                e1.printStackTrace();
                Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
            }
            new DownloadTaskFin().execute("");
        }
        if(v.getId()==R.id.btnLimpiar){
            LimpiaApuesta();
        }
        if(v.getId()==R.id.btnCopiar){
            imprimir.setEnabled(true);
            copiar.setEnabled(false);

            strTicket = CopiaTicket.getText().toString();
            new DownloadTaskCopiar().execute("");
        }
        if(v.getId()==R.id.btnAgregar){
            imprimir.setEnabled(true);
            strNumero = numeroTxt.getText().toString();
            if(editApuesta){
                new DownloadTaskModificar().execute("");
                //Limpiar el Número
                numeroTxt.setText("");
                //Asignarle el foco al Número
                numeroTxt.setFocusable(true);
                numeroTxt.requestFocus();

            }else{
                new DownloadTaskAgregar().execute("");
                numeroTxt.setText("");
                numeroTxt.setFocusable(true);
                numeroTxt.requestFocus();
            }
        }
        if(v.getId()==R.id.btnCancelar){
            copiar.setEnabled(true);
            new DownloadTaskLimpia().execute("");
        }
    }

    //Tarea en Background
    private class DownloadTasklvw extends AsyncTask<String, Void, LinkedList<SorteoLvw>>
    {
        protected LinkedList<SorteoLvw> doInBackground(String... args) {
            CargaDatosWS ws=new CargaDatosWS();
            return ws.getSorteoActivosNew(con,Usuario.user);
        }
        protected void onPostExecute(LinkedList<SorteoLvw> result) {
            ArrayAdapter<SorteoLvw> spinner_adapter = new ArrayAdapter<SorteoLvw>(VentaActivity.this,android.R.layout.simple_spinner_item,result);
            spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lvw.setAdapter(spinner_adapter);
        }
    }

    //Tarea en Background
    private class DownloadTaskAgregar extends AsyncTask<String, Void, MyWrapper>
    {
        @SuppressLint("WrongThread")
        protected MyWrapper doInBackground(String... args) {
            MyWrapper res = null;
            ErrorApuesta="";
            CargaDatosWS ws=new CargaDatosWS();
            res = ws.agregaApuesta(sorteoId, strNumero, lugar1.getText().toString(), lugar2.getText().toString(), lugar3.getText().toString(), Usuario.user, con);
            return res;
        }

        protected void onPostExecute(MyWrapper result) {
            if (result == null) {
                Toast.makeText(con, (ErrorApuesta == null || ErrorApuesta.isEmpty()) ? "Sin respuesta del servidor." : ErrorApuesta, Toast.LENGTH_LONG).show();
                return;
            }

            LinkedList<Apuesta> apuestas = new LinkedList<Apuesta>();
            ArrayAdapter<String> adaptador;
            strApuestas= new ArrayList<>();

            if (result.code.equals("00")) {
                if (result.cant > 0) {
                    for (int i = 0; i < result.cant; i++) {
                        SoapObject so = (SoapObject) result.soap.getProperty(i);
                        apuestas.add(new Apuesta(so.getProperty("IdApuesta").toString(), so.getProperty("Numero").toString(), so.getProperty("MontoPrimero").toString(), so.getProperty("MontoSegundo").toString(), so.getProperty("MontoTercero").toString()));
                        strApuestas.add(so.getProperty("IdApuesta").toString());
                        strApuestas.add(so.getProperty("Numero").toString());
                        strApuestas.add(so.getProperty("MontoPrimero").toString());
                        strApuestas.add(so.getProperty("MontoSegundo").toString());
                        strApuestas.add(so.getProperty("MontoTercero").toString());
                    }

                    padToBlocksOf5(strApuestas); // 👈 Asegura múltiplos de 5
                    adaptador = new ArrayAdapter<>(con, android.R.layout.simple_list_item_1, strApuestas);
                    gvw.setAdapter(adaptador);
                    gvw.setEnabled(!strApuestas.isEmpty());
                    gvw.invalidateViews();

                    total.setText(result.data);
                    cantAp.setText(result.cant.toString());
                    strNumero = "";
                } else {
                    Toast.makeText(con, result.message, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(con, result.message, Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void LimpiaApuesta()
    {
        //Limpiar los elementos superiores
        strNumero = "";
        numeroTxt.setText(strNumero);
        strLugar1 = "";
        lugar1.setText(strLugar1);
        strLugar2 = "";
        lugar2.setText(strLugar2);
        strLugar3 = "";
        lugar3.setText(strLugar3);
        //Asignarle el foco al Número
        numeroTxt.setFocusable(true);
        numeroTxt.requestFocus();
        CopiaTicket.setText("");
    }

    //Tarea en Background
    private class DownloadTaskModificar extends AsyncTask<String, Void, MyWrapper>
    {
        @SuppressLint("WrongThread")
        protected MyWrapper doInBackground(String... args) {
            MyWrapper res;
            CargaDatosWS ws=new CargaDatosWS();
            res = ws.modificarApuestaTicket(sorteoId,apuestaId, strNumero, lugar1.getText().toString(), lugar2.getText().toString(), lugar3.getText().toString(), Usuario.user, con);
            return res;
        }

        protected void onPostExecute(MyWrapper result) {
            if (result == null) {
                Toast.makeText(con, "Sin respuesta del servidor.", Toast.LENGTH_LONG).show();
                editApuesta = false;
                return;
            }

            LinkedList<Apuesta> apuestas = new LinkedList<Apuesta>();
            ArrayAdapter<String> adaptador;
            strApuestas= new ArrayList<>();

            if (result.code.equals("00")) {
                if (result.cant > 0) {
                    for (int i = 0; i < result.cant; i++) {
                        SoapObject so = (SoapObject) result.soap.getProperty(i);
                        apuestas.add(new Apuesta(so.getProperty("IdApuesta").toString(), so.getProperty("Numero").toString(), so.getProperty("MontoPrimero").toString(), so.getProperty("MontoSegundo").toString(), so.getProperty("MontoTercero").toString()));
                        strApuestas.add(so.getProperty("IdApuesta").toString());
                        strApuestas.add(so.getProperty("Numero").toString());
                        strApuestas.add(so.getProperty("MontoPrimero").toString());
                        strApuestas.add(so.getProperty("MontoSegundo").toString());
                        strApuestas.add(so.getProperty("MontoTercero").toString());
                    }

                    padToBlocksOf5(strApuestas); // 👈 Asegura múltiplos de 5
                    adaptador = new ArrayAdapter<>(con, android.R.layout.simple_list_item_1, strApuestas);
                    gvw.setAdapter(adaptador);
                    gvw.setEnabled(!strApuestas.isEmpty());
                    gvw.invalidateViews();

                    total.setText(result.data);
                    cantAp.setText(result.cant.toString());
                    strNumero = "";
                } else {
                    Toast.makeText(con, result.message, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(con, result.message, Toast.LENGTH_LONG).show();
            }
            editApuesta = false;
        }
    }

    private void EditarApuesta(String apuesta, String numero, String primero, String segundo, String tercero){
        numeroTxt.setText(numero);
        lugar1.setText(primero);
        lugar2.setText(segundo);
        lugar3.setText(tercero);
    }

    //Tarea en Background
    private class DownloadTaskFin extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... args) {
            CargaDatosWS ws=new CargaDatosWS();
            String strTicket;
            strTicket=ws.crearTicket(sorteoId.toString(),Usuario.user);
            return strTicket;
        }

        protected void onPostExecute(String result) {
            String strCode = "";
            if (result != null && result.length() > 2) {
                strCode = result.substring(0,2);
                result = result.substring(2);
            }
            if ("00".equals(strCode)) {
                strUltTicket.setText(result);
                gvw.setEnabled(false);           // 👈 deshabilita grid mientras queda vacío
                gvw.setAdapter(null);
                total.setText("0");
                cantAp.setText("0");
                LimpiaApuesta();
            } else {
                Toast.makeText(con, result == null ? "Sin respuesta del servidor." : result, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class DownloadTaskLimpia extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... args) {
            CargaDatosWS ws=new CargaDatosWS();
            String strRes = ws.limpiaTablaTemp(Usuario.user);
            return strRes;
        }

        protected void onPostExecute(String result) {
            gvw.setEnabled(false);               // 👈 deshabilita grid al limpiar
            gvw.setAdapter(null);
            total.setText("0");
            cantAp.setText("0");
        }
    }

    //Tarea en Background
    private class DownloadTaskDelete extends AsyncTask<String, Void, MyWrapper>
    {
        protected MyWrapper doInBackground(String... args) {
            CargaDatosWS ws=new CargaDatosWS();
            return ws.eliminaApuesta(apuestaId, Usuario.user,con);
        }

        protected void onPostExecute(MyWrapper result) {
            if (result == null) {
                Toast.makeText(con, "Sin respuesta del servidor.", Toast.LENGTH_LONG).show();
                return;
            }

            LinkedList<Apuesta> apuestas = new LinkedList<Apuesta>();
            ArrayAdapter<String> adaptador;
            strApuestas= new ArrayList<>();

            if (result.code.equals("00")) {
                if (result.cant > 0) {
                    for (int i = 0; i < result.cant; i++) {
                        SoapObject so = (SoapObject) result.soap.getProperty(i);
                        apuestas.add(new Apuesta(so.getProperty("IdApuesta").toString(), so.getProperty("Numero").toString(), so.getProperty("MontoPrimero").toString(), so.getProperty("MontoSegundo").toString(), so.getProperty("MontoTercero").toString()));
                        strApuestas.add(so.getProperty("IdApuesta").toString());
                        strApuestas.add(so.getProperty("Numero").toString());
                        strApuestas.add(so.getProperty("MontoPrimero").toString());
                        strApuestas.add(so.getProperty("MontoSegundo").toString());
                        strApuestas.add(so.getProperty("MontoTercero").toString());
                    }

                    padToBlocksOf5(strApuestas); // 👈 Asegura múltiplos de 5
                    adaptador = new ArrayAdapter<>(con, android.R.layout.simple_list_item_1, strApuestas);
                    gvw.setAdapter(adaptador);
                    gvw.setEnabled(!strApuestas.isEmpty());
                    gvw.invalidateViews();

                    total.setText(result.data);
                    cantAp.setText(result.cant.toString());
                } else {
                    gvw.setEnabled(false);       // 👈 si no hay datos, inhabilita
                    gvw.setAdapter(null);
                    total.setText("0");
                    cantAp.setText("0");
                }
            } else {
                Toast.makeText(con, result.message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class DownloadTaskCopiar extends AsyncTask<String, Void, MyWrapper>
    {
        @SuppressLint("WrongThread")
        protected MyWrapper doInBackground(String... args) {
            MyWrapper res;
            CargaDatosWS ws=new CargaDatosWS();
            res = ws.CopiaApuestas( strTicket,Usuario.user, con);
            return res;
        }

        protected void onPostExecute(MyWrapper result) {
            if (result == null) {
                Toast.makeText(con, "Sin respuesta del servidor.", Toast.LENGTH_LONG).show();
                return;
            }

            LinkedList<Apuesta> apuestas = new LinkedList<Apuesta>();
            ArrayAdapter<String> adaptador;
            strApuestas= new ArrayList<>();

            if (result.code.equals("00")) {
                if (result.cant > 0) {
                    for (int i = 0; i < result.cant; i++) {
                        SoapObject so = (SoapObject) result.soap.getProperty(i);
                        apuestas.add(new Apuesta(so.getProperty("ID").toString(), so.getProperty("NUMERO").toString(), so.getProperty("MONTO_APUESTA").toString(), so.getProperty("APUESTA_SEGUNDO").toString(), so.getProperty("APUESTA_TERCERO").toString()));

                        strApuestas.add(so.getProperty("ID").toString());
                        strApuestas.add(so.getProperty("NUMERO").toString());
                        strApuestas.add(so.getProperty("MONTO_APUESTA").toString());
                        strApuestas.add(so.getProperty("APUESTA_SEGUNDO").toString());
                        strApuestas.add(so.getProperty("APUESTA_TERCERO").toString());
                    }

                    padToBlocksOf5(strApuestas); // 👈 Asegura múltiplos de 5
                    adaptador = new ArrayAdapter<>(con, android.R.layout.simple_list_item_1, strApuestas);
                    gvw.setAdapter(adaptador);
                    gvw.setEnabled(!strApuestas.isEmpty());
                    gvw.invalidateViews();

                    total.setText(result.data);
                    cantAp.setText(result.cant.toString());
                    strNumero = "";
                } else {
                    Toast.makeText(con, result.message, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(con, result.message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
