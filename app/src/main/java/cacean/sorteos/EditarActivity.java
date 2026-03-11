package cacean.sorteos;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cacean.sorteos.adapter.SorteoAdapter;

public class EditarActivity extends Activity implements OnClickListener {

    private Spinner edtlvw;
    private GridView edtgvw;
    private String strTicket;
    private EditText edtnumeroTxt;
    static Resources aux;
    private EditText edtlugar1;
    private EditText edtlugar2;
    private EditText edtlugar3;
    private String edtsorteoId;
    private String edtapuestaId;
    private TextView edtusuarioTxt;
    private TextView edtfechaTxt;
    private TextView edttotal;
    private TextView edtcantap;
    private Button edtlimpiar;
    private Button edtimprimir;
    private Button edtagregar;
    private Button edtcancelar;
    private Button edtsalir;
    private String edtres;
    private ProgressDialog edtpd;
    private Context edtcon;
    private String edtstrNumero;
    private Boolean edtblnNumero;
    private Boolean edteditApuesta;
    private String edtstrLugar1;
    private Boolean edtblnLugar1;
    private String edtstrLugar2;
    private Boolean edtblnLugar2;
    private String edtstrLugar3;
    private Boolean edtblnLugar3;
    private List<String> edtstrApuestas;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar2);
        Bundle bundle = getIntent().getExtras();
        strTicket =  bundle.getString("ticketEdit");
        edtusuarioTxt = (TextView)findViewById(R.id.tvwUsuario);
        edtusuarioTxt.append(Usuario.nombre);
        aux = getResources();
        edtfechaTxt = (TextView)findViewById(R.id.tvwFecha);
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        edtfechaTxt.append(dateFormat.format(date));
        edttotal = (TextView)findViewById(R.id.txtTotal);
        edtcantap = (TextView)findViewById(R.id.txtCantAp);
        edtlimpiar = (Button)findViewById(R.id.btnLimpiar);
        edtlimpiar.setOnClickListener(this);
        edtagregar = (Button)findViewById(R.id.btnAgregar);
        edtagregar.setOnClickListener(this);
        edtimprimir = (Button)findViewById(R.id.btnImprimir);
        edtimprimir.setOnClickListener(this);
        edtcancelar = (Button)findViewById(R.id.btnCancelar);
        edtcancelar.setOnClickListener(this);
        edtsalir = (Button)findViewById(R.id.btnSalir);
        edtsalir.setOnClickListener(this);
        edteditApuesta = false;
        edtcon = this;
        edtnumeroTxt = (EditText)findViewById(R.id.txtNumero);
        edtlugar1 = (EditText)findViewById(R.id.txtLugar1);
        edtlugar2 = (EditText)findViewById(R.id.txtLugar2);
        edtlugar3 = (EditText)findViewById(R.id.txtLugar3);
        edtlvw = (Spinner)findViewById(R.id.lvwSorteos);
        edtgvw = (GridView)findViewById(R.id.gvApuestas);
        new DownloadTaskedtlvw().execute("");
        new DownloadTaskedtLimpia().execute("");

        edtlvw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                edtsorteoId = ((SorteoLvw) arg0.getItemAtPosition(arg2)).getId();

                Log.e("Selected item : ", edtsorteoId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        edtgvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                edtapuestaId = edtstrApuestas.get(position -(position % 5));
                edtstrNumero = edtstrApuestas.get(position - (position % 5) + 1);
                edtstrLugar1 = edtstrApuestas.get(position - (position % 5) + 2);
                edtstrLugar2 = edtstrApuestas.get(position - (position % 5) + 3);
                edtstrLugar3 = edtstrApuestas.get(position - (position % 5) + 4);

                AlertDialog.Builder builder = new AlertDialog.Builder(edtcon);
                builder.setMessage("Seleccione la operación para la apuesta: " + edtapuestaId + ".")
                        .setTitle("Atención!!")
                        .setCancelable(true)
                        .setNegativeButton("Editar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Ejecutar editar Apuesta(apuestaId);
                                        edteditApuesta = true;
                                        EditarApuesta(edtapuestaId, edtstrNumero, edtstrLugar1, edtstrLugar2, edtstrLugar3);
                                    }
                                })
                        .setPositiveButton("Borrar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new DownloadTaskedtDelete().execute(""); // metodo que se debe implementar
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                //Toast.makeText(getApplicationContext(), "Has selecccionado: "  + position + ";" + edtstrApuestas.get(position -(position % 5)) + ";" + edtstrApuestas.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        //Cargar datos del ticket a editar
        new DownloadTaskedtInicia().execute("");
    }


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void onClick(View v){
        //respond to clicks
        if(v.getId()==R.id.btnSalir){
            //Salir
            Intent intent = new Intent(Intent.ACTION_MAIN);
            finish();
        }
        if(v.getId()==R.id.btnImprimir){
            edtimprimir.setEnabled(false);
            new DownloadTaskedtFin().execute("");
        }
        if(v.getId()==R.id.btnLimpiar){
            //Limpiar los elementos superiores
            LimpiaApuesta();
        }
        if(v.getId()==R.id.btnAgregar){
            edtimprimir.setEnabled(true);
            edtstrNumero = edtnumeroTxt.getText().toString();
            if(edteditApuesta){
               new DownloadTaskedtModificar().execute("");

            }else{
                new DownloadTaskedtAgregar().execute("");
                //Limpiar el Número
                edtnumeroTxt.setText("");
                //Asignarle el foco al Número
                edtnumeroTxt.setFocusable(true);
                edtnumeroTxt.requestFocus();
            }
        }
        if(v.getId()==R.id.btnCancelar){
            new DownloadTaskedtLimpia().execute("");
        }
    }

    //Tarea en Background
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class DownloadTaskedtlvw extends AsyncTask<String, Void, LinkedList<SorteoLvw>>
    {

        protected LinkedList<SorteoLvw> doInBackground(String... args) {
            CargaDatosWS ws=new CargaDatosWS();
            //Se invoca nuestro metodo
            return ws.getSorteoActivosNew(edtcon,Usuario.user);
//            return 1;
        }

        protected void onPostExecute(LinkedList<SorteoLvw> result) {
            //Creamos el adaptador
            ArrayAdapter<SorteoLvw> spinner_adapter = new ArrayAdapter<SorteoLvw>(EditarActivity.this,android.R.layout.simple_spinner_item,result);
            //Añadimos el layout para el menú y se lo damos al spinner
            spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            edtlvw.setAdapter(spinner_adapter);

        }
    }

    private void LimpiaApuesta()
    {
        edtstrNumero = "";
        edtnumeroTxt.setText(edtstrNumero);
        edtstrLugar1 = "";
        edtlugar1.setText(edtstrLugar1);
        edtstrLugar2 = "";
        edtlugar2.setText(edtstrLugar2);
        edtstrLugar3 = "";
        edtlugar3.setText(edtstrLugar3);
        //Asignarle el foco al Número
        edtnumeroTxt.setFocusable(true);
        edtnumeroTxt.requestFocus();
    }
    //Tarea en Background
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class DownloadTaskedtAgregar extends AsyncTask<String, Void, MyWrapper>
    {

        @SuppressLint("WrongThread")
        protected MyWrapper doInBackground(String... args) {
            MyWrapper res = null;
            String strTotal = null;
            String msg = null;
            CargaDatosWS ws=new CargaDatosWS();
            //Se invoca nuestro metodo
            res = ws.agregaApuesta(edtsorteoId, edtstrNumero, edtlugar1.getText().toString(), edtlugar2.getText().toString(), edtlugar3.getText().toString(), Usuario.user, edtcon);

            return res;
        }

        protected void onPostExecute(MyWrapper result) {

            //No hacer nada
            LinkedList<Apuesta> apuestas = new LinkedList<Apuesta>();
            ArrayAdapter<String> adaptador;
            edtstrApuestas= new ArrayList<>();

            if (result.code.equals("00")) {
                List<Apuesta> lista = result.getApuestas();
                if (!lista.isEmpty()) {
                    for (Apuesta a : lista) {
                        apuestas.add(a);
                        edtstrApuestas.add(a.getIdApuesta());
                        edtstrApuestas.add(a.getNumero());
                        edtstrApuestas.add(a.getMontoPrimero());
                        edtstrApuestas.add(a.getMontoSegundo());
                        edtstrApuestas.add(a.getMontoTercero());
                    }
                    adaptador = new ArrayAdapter<String>(edtcon,android.R.layout.simple_list_item_1,edtstrApuestas);

                    edtgvw.setAdapter(adaptador);

                    edttotal.setText(result.data);
                    edtcantap.setText(String.valueOf(result.cant));

                    edtstrNumero = "";
                }
                else
                {
                    Toast.makeText(edtcon, result.message, Toast.LENGTH_LONG).show();
                }
            }else
            {
                Toast.makeText(edtcon, result.message, Toast.LENGTH_LONG).show();
            }
        }
    }

    //Tarea en Background
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class DownloadTaskedtModificar extends AsyncTask<String, Void, MyWrapper>
    {

        @SuppressLint("WrongThread")
        protected MyWrapper doInBackground(String... args) {
            MyWrapper res = null;
            String strTotal = null;
            String msg = null;
            CargaDatosWS ws=new CargaDatosWS();
            //Se invoca nuestro metodo
            res = ws.modificarApuestaTicket(edtsorteoId,edtapuestaId, edtstrNumero, edtlugar1.getText().toString(), edtlugar2.getText().toString(), edtlugar3.getText().toString(), Usuario.user, edtcon);

            return res;
        }

        protected void onPostExecute(MyWrapper result) {

            //No hacer nada
            LinkedList<Apuesta> apuestas = new LinkedList<Apuesta>();
            ArrayAdapter<String> adaptador;
            edtstrApuestas= new ArrayList<>();

            if (result.code.equals("00")) {
                List<Apuesta> lista = result.getApuestas();
                if (!lista.isEmpty()) {
                    for (Apuesta a : lista) {
                        apuestas.add(a);
                        edtstrApuestas.add(a.getIdApuesta());
                        edtstrApuestas.add(a.getNumero());
                        edtstrApuestas.add(a.getMontoPrimero());
                        edtstrApuestas.add(a.getMontoSegundo());
                        edtstrApuestas.add(a.getMontoTercero());
                    }
                    adaptador = new ArrayAdapter<String>(edtcon,android.R.layout.simple_list_item_1,edtstrApuestas);

                    edtgvw.setAdapter(adaptador);

                    edttotal.setText(result.data);
                    edtcantap.setText(String.valueOf(result.cant));

                    edtstrNumero = "";

                    LimpiaApuesta();
                }
                else
                {
                    Toast.makeText(edtcon, result.message, Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(edtcon, result.message, Toast.LENGTH_LONG).show();
            }
            edteditApuesta = false;
        }
    }

    private void EditarApuesta(String apuesta, String numero, String primero, String segundo, String tercero){
        edtnumeroTxt.setText(numero);
        edtlugar1.setText(primero);
        edtlugar2.setText(segundo);
        edtlugar3.setText(tercero);
    }

    //Tarea en Background
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class DownloadTaskedtFin extends AsyncTask<String, Void, String>
    {

        protected String doInBackground(String... args) {
            CargaDatosWS ws=new CargaDatosWS();
            //String strTicket = "";
            String strRes = "";

            //Se invoca nuestro metodo
            strRes=ws.terminaEditarTicket(strTicket,Usuario.user);
//            if (strRes.equals("00"))
//            {
//                strRes = "";
//                strRes=ws.getTicket(strTicket, "EDITAR");
//            }
            return strRes;
        }

        protected void onPostExecute(String result) {
            //No hacer nada
            if(result.equals("00")){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                finish();
            }else{
                Toast.makeText(edtcon, result, Toast.LENGTH_LONG).show();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class DownloadTaskedtLimpia extends AsyncTask<String, Void, String>
    {

        protected String doInBackground(String... args) {
            CargaDatosWS ws=new CargaDatosWS();
            String strRes = "";

            //Se invoca nuestro metodo
            strRes=ws.limpiaTablaTemp(Usuario.user);
            return strRes;
        }

        protected void onPostExecute(String result) {
            //Limpiar spinner
            edtgvw.setAdapter(null);
            edttotal.setText("0");
            edtcantap.setText("0");

        }
    }

    //Tarea en Background
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class DownloadTaskedtInicia extends AsyncTask<String, Void, MyWrapper>
    {

        protected MyWrapper doInBackground(String... args) {
            MyWrapper res = null;
            String strTotal = null;
            String msg = null;
            CargaDatosWS ws=new CargaDatosWS();
            //Se invoca nuestro metodo
            res = ws.editarTicket(strTicket, Usuario.user, edtcon);

            return res;
        }

        protected void onPostExecute(MyWrapper result) {

            //No hacer nada
            LinkedList<Apuesta> apuestas = new LinkedList<Apuesta>();
            ArrayAdapter<String> adaptador;
            edtstrApuestas= new ArrayList<>();

            if (result.code.equals("00")) {
                List<Apuesta> lista = result.getApuestas();
                if (!lista.isEmpty()) {
                    for (Apuesta a : lista) {
                        apuestas.add(a);
                        edtstrApuestas.add(a.getIdApuesta());
                        edtstrApuestas.add(a.getNumero());
                        edtstrApuestas.add(a.getMontoPrimero());
                        edtstrApuestas.add(a.getMontoSegundo());
                        edtstrApuestas.add(a.getMontoTercero());
                    }
                    adaptador = new ArrayAdapter<String>(edtcon,android.R.layout.simple_list_item_1,edtstrApuestas);

                    edtgvw.setAdapter(adaptador);

                    edttotal.setText(result.data);
                    edtcantap.setText(String.valueOf(result.cant));
                }
                else
                {
                    Toast.makeText(edtcon, result.message, Toast.LENGTH_LONG).show();
                }
            }else
            {
                Toast.makeText(edtcon, result.message, Toast.LENGTH_LONG).show();
            }
        }
    }

    //Tarea en Background
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class DownloadTaskedtDelete extends AsyncTask<String, Void, MyWrapper>
    {

        protected MyWrapper doInBackground(String... args) {
            CargaDatosWS ws=new CargaDatosWS();
            //Se invoca nuestro metodo
            return ws.eliminaApuesta(edtapuestaId, Usuario.user,edtcon);
//            return 1;
        }

        protected void onPostExecute(MyWrapper result) {
            LinkedList<Apuesta> apuestas = new LinkedList<Apuesta>();
            ArrayAdapter<String> adaptador;
            edtstrApuestas= new ArrayList<>();

            if (result.code.equals("00")) {
                List<Apuesta> lista = result.getApuestas();
                edtcantap.setText(String.valueOf(result.cant));
                if (!lista.isEmpty()) {
                    for (Apuesta a : lista) {
                        apuestas.add(a);
                        edtstrApuestas.add(a.getIdApuesta());
                        edtstrApuestas.add(a.getNumero());
                        edtstrApuestas.add(a.getMontoPrimero());
                        edtstrApuestas.add(a.getMontoSegundo());
                        edtstrApuestas.add(a.getMontoTercero());
                    }
                    adaptador = new ArrayAdapter<String>(edtcon,android.R.layout.simple_list_item_1,edtstrApuestas);

                    edtgvw.setAdapter(adaptador);

                    edttotal.setText(result.data);
                }
                else
                {
                    edtgvw.setAdapter(null);
                    edttotal.setText("0");
                    edtcantap.setText("0");
                }
            }

        }
    }
}
