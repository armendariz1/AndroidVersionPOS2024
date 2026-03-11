package cacean.sorteos;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.AsyncTask;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.minipos.device.SDK;


public class Main extends AppCompatActivity {
    private TextView user;
    private TextView pwd;
    private Button btnCancel;
    private Button btnLog;
    private String res;
    private ProgressDialog pd;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            SDK.init(this);
        } catch (Throwable e1) {
            e1.printStackTrace();
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }


        context=this;
        ApiClient.BASE_URL = getString(R.string.api_base_url);
        user=(TextView)findViewById(R.id.txtUser);
        pwd=(TextView)findViewById(R.id.txtPwd);
        btnCancel=(Button)findViewById(R.id.btnCancelar);
        btnLog=(Button)findViewById(R.id.btnLogin);
        //Se establece listener para nuestros botones
        btnCancel.setOnClickListener(listenerCanc);
        btnLog.setOnClickListener(listenerLogin);
        Usuario.nombre = "";
        Usuario.user = "";
        Usuario.paterno = "";
        Usuario.perfil = "";
    }

    private View.OnClickListener listenerLogin = new View.OnClickListener()
    {
        public void onClick(View arg0) {
            //	Usamos un AsyncTask, para poder mostrar una ventana de por favor espere, mientras se consulta el servicio web
            new DownloadTask2().execute("");
            pd = ProgressDialog.show(context, "Por favor espere","Validando usuario", true, false);

        }
    };

    private View.OnClickListener listenerCanc = new View.OnClickListener()
    {
        public void onClick(View arg0) {
            //	Cerramos la aplicacion
            Intent intent = new Intent(Intent.ACTION_MAIN);
            finish();
        }
    };

    //Tarea en Background
    private class DownloadTask2 extends AsyncTask<String, Void, Object>
    {

        @SuppressLint("WrongThread")
        protected Integer doInBackground(String... args) {
            CargaDatosWS ws=new CargaDatosWS();
            //Se invoca nuestro metodo
            res=ws.getUsuario(user.getText().toString(), pwd.getText().toString());

            if (res.substring(1,1).equals("B"))
            {
                return 1;
            }
            else {
                return 0;
            }

        }

        protected void onPostExecute(Object result) {
            //Se elimina la pantalla de por favor espere.
            pd.dismiss();
            //Se muestra mensaje con la respuesta del servicio web
            Toast.makeText(context,res,Toast.LENGTH_LONG).show();
            super.onPostExecute(result);
            if (Usuario.perfil.length()>0){
                    startActivity(new Intent(context,MenuSorteoActivity.class));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
