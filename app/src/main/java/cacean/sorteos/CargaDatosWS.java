package cacean.sorteos;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import java.util.LinkedList;


public class CargaDatosWS {
    public CargaDatosWS(TicketActivity ticketActivity) {
    }

    public CargaDatosWS() {

    }

    public String getUsuario(String usuario, String pw)
    {
        String res=null;
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/validarUsuario";
        final String METHOD = "validarUsuario";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("usuario", usuario); // Paso parametros al WS
            request.addProperty("pw",pw); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            String code = response.getPropertyAsString("Codigo").toString();
            String msg = response.getPropertyAsString("Mensaje").toString();

            String[] strAux = msg.split(",");

            Usuario.user = usuario;

            if (code.equals("00"))
            {
                Usuario.nombre = strAux[0];
                Usuario.paterno = strAux[1];
                Usuario.perfil = strAux[2];
                res = "Bienvenido "+Usuario.nombre;
            }
            else
            {
                res="Error " + msg;
                Usuario.perfil = "";
            }

        }
        catch(Exception e)
        {
            //e.printStackTrace();
            res=e.toString();
        }
        return res;
    }

    public String validaEditar(String ticket)
    {
        String res=null;
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/verificaEditarTicket";
        final String METHOD = "verificaEditarTicket";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("idTicket", ticket); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            String code = response.getPropertyAsString("Codigo").toString();
            String msg = response.getPropertyAsString("Mensaje").toString();

            if (code.equals("00"))
            {
                res = code + msg;
            }
            else
            {
                res=msg;
            }

        }
        catch(Exception e)
        {
            //e.printStackTrace();
            res="Error: " + e.toString();
        }
        return res;
    }

    public String validaTicket(String ticket)
    {
        SoapObject response= new SoapObject();
        String res = "";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/verificaTicket";
        final String METHOD = "verificaTicket";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("parametro", ticket); // Paso parametros al WS
            //request.addProperty("usuario",user); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            String code = response.getPropertyAsString("Codigo").toString();
            String msg = response.getPropertyAsString("Mensaje").toString();

            if(code == "00"){
                res = "OK" + msg;
            }
            else{
                res = msg;
            }
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            res="Error: " + e.getMessage();
        }
        return res;
    }

    public String marcaTicketPagado(String ticket,String usuario)
    {
        SoapObject response= new SoapObject();
        String res = "";
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/marcaTicketPagado";
        final String METHOD = "marcaTicketPagado";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("ticket", ticket); // Paso parametros al WS
            request.addProperty("usuario", usuario); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            String code = response.getPropertyAsString("Codigo").toString();
            String msg = response.getPropertyAsString("Mensaje").toString();

            if(code == "00"){
                res = "OK" + msg;
            }
            else{
                res = msg;
            }
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            res="Error: " + e.getMessage();
        }
        return res;
    }

    public String crearTicket(String sorteo,String user)
    {
        String res=null;
        String puntos,sucursal;
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/crearTicket";
        final String METHOD = "crearTicket";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("idSorteo", sorteo); // Paso parametros al WS
            request.addProperty("usuario",user); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            String code = response.getPropertyAsString("Codigo").toString();
            String msg = response.getPropertyAsString("Mensaje").toString();
            //CAVL-INI-MAY.14.2015-
            String ticket = response.getPropertyAsString("NumeroTicket").toString();
            String strcopies =  response.getPropertyAsString("Impresiones").toString();
            //msg esta agregandole saltos de línea al inicio, la siguiente línea
            //es para quitarlos.
            msg = msg.trim();
            //Centrar el texto
            msg = "      " + msg;
            //CAVL-FIN-

            puntos = getPuntos(ticket).trim();

            if (!puntos.equals("NO"))
            {
                msg = msg + "\r\n       Puntos Don Beto";
                msg = msg + "\r\n        Vale " + puntos.trim() + " puntos";
            }

            sucursal = NombreSuc(user);

            msg = msg + "\r\nVendido en sucursal:" + sucursal;




            if (code.equals("00"))
            {
                //res="OK, Usr: " + Usuario.user + ", Nombre: " + Usuario.nombre + ", Paterno: " + Usuario.paterno + ", Perfil: " + Usuario.perfil;
                //CAVL-INI-MAY.14.2015-
                Imprimir imprime = new Imprimir();
//                for (int i = 0; i < copies; i++)
//                {
//                    res = imprime.PrintStr(msg, "VENTA", ticket);
//                }
                //if (strcopies.equals("1"))
               // {
                    res = imprime.PrintStr(msg, "VENTA", ticket,Usuario.user);
               // }else
               // {
               //     res = imprime.PrintDblStr(msg, "VENTA", ticket);
               // }
                //CAVL-FIN-
                res = code + ticket;
            }
            else
            {
                res= msg;
            }

        }
        catch(Exception e)
        {
            //e.printStackTrace();
            res=e.toString();
        }
        return res;
    }

    public MyWrapper editarTicket(String ticket,String user, Context cont)
    {
        SoapObject response = new SoapObject();
        String code = null;
        String msg = null;
        String strTotal = null;
        SoapObject strApuestas = null;
        Integer cant = null;
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/editarTicket";
        final String METHOD = "editarTicket";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("idTicket", ticket); // Paso parametros al WS
            request.addProperty("usuario",user); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            code = response.getPropertyAsString("Codigo").toString();
            msg = response.getPropertyAsString("Mensaje").toString();

            if (code.equals("00")) {
                strTotal = response.getPropertyAsString("TotalTemp");
                strApuestas = (SoapObject) (response.getProperty("ApuestasTemp"));
                cant = strApuestas.getPropertyCount();
            }
        }
        catch(Exception e)
        {
            Log.e(CargaDatosWS.class.getSimpleName(),"",e);
        }
        return new MyWrapper(strApuestas,strTotal,cant,code,msg);
    }

    public MyWrapper eliminaApuesta(String apuesta,String user,Context cont)
    {
        SoapObject response = new SoapObject();
        String code = null;
        String msg = null;
        String strTotal = null;
        SoapObject strApuestas = null;
        Integer cant = null;
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/eliminaApuesta";
        final String METHOD = "eliminaApuesta";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("idApuesta", apuesta); // Paso parametros al WS
            request.addProperty("usuario",user); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            code = response.getPropertyAsString("Codigo");
            msg = response.getPropertyAsString("Mensaje");
            strTotal = response.getPropertyAsString("TotalTemp");
            strApuestas = (SoapObject)(response.getProperty("ApuestasTemp"));
            cant = strApuestas.getPropertyCount();

        }
        catch(Exception e)
        {
            Log.e(CargaDatosWS.class.getSimpleName(),"",e);
        }
        return new MyWrapper(strApuestas,strTotal,cant,code,msg);
    }

    public String eliminaTicket(String ticket,String user)
    {
        String res=null;
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/eliminaTicket";
        final String METHOD = "eliminaTicket";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("idTicket", ticket); // Paso parametros al WS
            request.addProperty("usuario",user); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            String code = response.getPropertyAsString("Codigo").toString();
            String msg = response.getPropertyAsString("Mensaje").toString();

            if (code.equals("00"))
            {
                //res="OK, Usr: " + Usuario.user + ", Nombre: " + Usuario.nombre + ", Paterno: " + Usuario.paterno + ", Perfil: " + Usuario.perfil;
                res = "Respuesta correcta "+msg;
            }
            else
            {
                res="Error " + msg;
            }

        }
        catch(Exception e)
        {
            //e.printStackTrace();
            res=e.toString();
        }
        return res;
    }

    public MyWrapper agregaApuesta(String sorteo, String numero,String monto1,String monto2, String monto3, String user, Context cont)
    {
        SoapObject response = new SoapObject();
        String code = null;
        String msg = null;
        String strTotal = null;
        SoapObject strApuestas = null;
        Integer cant = null;
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/insertaApuesta";
        final String METHOD = "insertaApuesta";

        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("idSorteo",sorteo);// Paso parametros al WS
            request.addProperty("numero", numero);
            request.addProperty("montoPrimero", monto1);
            request.addProperty("montoSegundo",monto2);
            request.addProperty("montoTercero",monto3);
            request.addProperty("usuario",user); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            code = response.getPropertyAsString("Codigo");
            msg = response.getPropertyAsString("Mensaje");
            if (code.equals("00")) {
                strTotal = response.getPropertyAsString("TotalTemp");
                strApuestas = (SoapObject) (response.getProperty("ApuestasTemp"));
                cant = strApuestas.getPropertyCount();
            }
            else
            {
                Toast.makeText(cont, msg, Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            Log.e(CargaDatosWS.class.getSimpleName(),"",e);
        }
        return new MyWrapper(strApuestas,strTotal,cant,code,msg);
    }

    public String limpiaTablaTemp(String user)
    {
        String res=null;
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/limpiaTablaTemp";
        final String METHOD = "limpiaTablaTemp";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("usuario",user); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            res = response.getPropertyAsString("Codigo").toString();
            //String msg = response.getPropertyAsString("Mensaje").toString();

        }
        catch(Exception e)
        {
            Log.e(CargaDatosWS.class.getSimpleName(),"",e);
            //e.printStackTrace();
            res=e.toString();
        }
        return res;
    }

    public MyWrapper modificarApuestaTicket(String sorteo,String apuesta, String numero, String monto1, String monto2, String monto3, String user,Context cont)
    {
        SoapObject response = new SoapObject();
        String code = null;
        String msg = null;
        String strTotal = null;
        SoapObject strApuestas = null;
        Integer cant = null;
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/modificarApuestaTicket";
        final String METHOD = "modificarApuestaTicket";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("idSorteo",sorteo); // Paso parametros al WS
            request.addProperty("idApuesta", apuesta);
            request.addProperty("numero",numero);
            request.addProperty("montoPrimero",monto1);
            request.addProperty("montoSegundo",monto2);
            request.addProperty("montoTercero",monto3);
            request.addProperty("usuario",user); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            code = response.getPropertyAsString("Codigo");
            msg = response.getPropertyAsString("Mensaje");
            strTotal = response.getPropertyAsString("TotalTemp");
            strApuestas = (SoapObject)(response.getProperty("ApuestasTemp"));
            cant = strApuestas.getPropertyCount();

        }
        catch(Exception e)
        {
            Log.e(CargaDatosWS.class.getSimpleName(),"",e);
        }
        return new MyWrapper(strApuestas,strTotal,cant,code,msg);
    }

    public String terminaEditarTicket(String ticket,String user)
    {
        String res=null;
        final String NAMESPACE = "http://tempuri.org/";
        final String URL = "http://cacean.com/Service1.svc";
        final String SOAPACTION = "http://tempuri.org/SorteoWS/terminaEditarTicket";
        final String METHOD = "terminaEditarTicket";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("idTicket", ticket); // Paso parametros al WS
            request.addProperty("usuario",user); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            String code = response.getPropertyAsString("Codigo").toString();
            String msg = response.getPropertyAsString("Mensaje").toString();

            //CAVL-INI-MAY.14.2015-
            String strcopies =  response.getPropertyAsString("Impresiones").toString();
            //msg esta agregandole saltos de línea al inicio, la siguiente línea
            //es para quitarlos.
            msg = msg.trim();
            //Centrar el texto
            msg = "      " + msg;
            //CAVL-FIN-

            if (code.equals("00"))
            {
                Imprimir imprime = new Imprimir();
                //if (strcopies.equals("1"))
               // {
                    res = imprime.PrintStr(msg, "EDITAR", ticket,Usuario.user);
                //}else
               // {
               //     res = imprime.PrintDblStr(msg, "EDITAR", ticket);
               //}
                res = code;
            }
            else
            {
                res="Error " + msg;
            }

        }
        catch(Exception e)
        {
            //e.printStackTrace();
            res=e.toString();
        }
        return res;
    }

    public String getPuntos(String idticket)
    {
        String res;
        final String NAMESPACE = "http://cacean.org/";
        final String URL = "http://getpoints.cacean.com/WSPuntosBeto.asmx"; //godaddy
        final String SOAPACTION = "http://cacean.org/GetPoints";
        final String METHOD = "GetPoints";

        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);
            //Add the parameters
            request.addProperty("idticket", idticket); // Paso parametros al WS
            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);
            //Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            //Obtener resultados de salida
            String msg = response.getValue().toString();
            res = msg;

        }
        catch(Exception e)
        {
            res="Err imprimir puntos";
        }
        return res;
    }

    public LinkedList<SorteoLvw> getSorteoActivosNew(Context cont,String user)
    {
        LinkedList<SorteoLvw> sorteos = new LinkedList<SorteoLvw>();
        String res=null;

        final String NAMESPACE = "http://cacean.org/";
        final String URL = "http://getpoints.cacean.com/WSPuntosBeto.asmx"; //godaddy
        final String SOAPACTION = "http://cacean.org/GetSorteos";
        final String METHOD = "GetSorteos";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            request.addProperty("idusuario", user); // Paso parametros al WS
            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();

            String code = response.getPropertyAsString("Codigo").toString();
            String msg = response.getPropertyAsString("Mensaje").toString();
            SoapObject strSorteos = (SoapObject)(response.getProperty("Sorteos"));
            Integer cant = strSorteos.getPropertyCount();

            if (code.equals("00"))
            {
                if (cant > 0)
                {
                    for (int i = 0; i < cant; i++) {

                        SoapObject so = new SoapObject();
                        so = (SoapObject)strSorteos.getProperty(i);

                        sorteos.add(new SorteoLvw(so.getProperty("IdSorteo").toString(),so.getProperty("FechaSorteo").toString() + " " + so.getProperty("NombreSorteo").toString()));

                    }
                }

            }
            else
            {
                res="Error " + msg;
            }
            int[] id_views = new int[]{android.R.id.text1,android.R.id.text2};

        }
        catch(Exception e)
        {
            Log.e(CargaDatosWS.class.getSimpleName(),"",e);
            res=e.toString();
        }
        return sorteos;
    }

    public String NombreSuc(String user)
    {
        String res;
        final String NAMESPACE = "http://cacean.org/";
        final String URL = "http://getpoints.cacean.com/WSPuntosBeto.asmx"; //godaddy
        final String SOAPACTION = "http://cacean.org/GetSucursal";
        final String METHOD = "GetSucursal";


        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("idusuario",user); // Paso parametros al WS

            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();
            res = response.getPropertyAsString("NombreSucursal").toString();

        }
        catch(Exception e)
        {
            res="N/A";
        }
        return res;
    }

    public MyWrapper CopiaApuestas(String ticket, String user, Context cont)
    {
        SoapObject response = new SoapObject();
        String code = null;
        String msg = null;
        String strTotal = null;
        SoapObject strApuestas = null;
        Integer cant = null;


        final String NAMESPACE = "http://cacean.org/";
        final String URL = "http://getpoints.cacean.com/WSPuntosBeto.asmx"; //godaddy
        final String SOAPACTION = "http://cacean.org/GetApuestas";
        final String METHOD = "GetApuestas";

        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);

            //Add the parameters
            request.addProperty("id_ticket",ticket);// Paso parametros al WS
            request.addProperty("user", user);


            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            response = (SoapObject) envelope.getResponse();

            //Obtener resultados de salida
            code = response.getPropertyAsString("Codigo");
            msg = response.getPropertyAsString("Mensaje");
            if (code.equals("00")) {
                strTotal = response.getPropertyAsString("data");
                strApuestas = (SoapObject) (response.getProperty("Apuestas"));
                cant = strApuestas.getPropertyCount();
            }
            else
            {
                Toast.makeText(cont, msg, Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            Log.e(CargaDatosWS.class.getSimpleName(),"",e);
        }
        return new MyWrapper(strApuestas,strTotal,cant,code,msg);
    }

    public ReporteVenta getReporteventas(String idUsuario)
    {
        ReporteVenta res = new ReporteVenta();
        final String NAMESPACE = "http://cacean.org/";
        final String URL = "http://getpoints.cacean.com/WSPuntosBeto.asmx"; //godaddy
        final String SOAPACTION = "http://cacean.org/GetReporteVentas";
        final String METHOD = "GetReporteVentas";

        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);
            //Add the parameters
            request.addProperty("idUsuario", idUsuario); // Paso parametros al WS
            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();
            //Obtener resultados de salida

            res.reporteText="";

            res.codigo = response.getPropertyAsString("codigo");

            if (res.codigo.equals("00")) {

                res.idSucursal = response.getPropertyAsString("idSucursal");
                res.nombreSucursal = response.getPropertyAsString("nombreSucursal");
                res.idSorteo = response.getPropertyAsString("idSorteo");
                res.nombreSorteo = response.getPropertyAsString("nombreSorteo");
                res.ventaHoy = response.getPropertyAsString("ventaHoy");

                res.nombreSorteo1 = response.getPropertyAsString("nombreSorteo1");
                res.fechaSorteo1 = response.getPropertyAsString("fechaSorteo1");
                res.ventaSorteo1 = response.getPropertyAsString("ventaSorteo1");

                res.nombreSorteo2 = response.getPropertyAsString("nombreSorteo2");
                res.fechaSorteo2 = response.getPropertyAsString("fechaSorteo2");
                res.ventaSorteo2 = response.getPropertyAsString("ventaSorteo2");

                res.nombreSorteo3 = response.getPropertyAsString("nombreSorteo3");
                res.fechaSorteo3 = response.getPropertyAsString("fechaSorteo3");
                res.ventaSorteo3 = response.getPropertyAsString("ventaSorteo3");

                res.nombreSorteo4 = response.getPropertyAsString("nombreSorteo4");
                res.fechaSorteo4 = response.getPropertyAsString("fechaSorteo4");
                res.ventaSorteo4 = response.getPropertyAsString("ventaSorteo4");

                res.nombreSorteo5 = response.getPropertyAsString("nombreSorteo5");
                res.fechaSorteo5 = response.getPropertyAsString("fechaSorteo5");
                res.ventaSorteo5 = response.getPropertyAsString("ventaSorteo5");

                res.reporteText = response.getPropertyAsString("reporteText");

                res.msg = "Reporte generado correctamente";


            } else {
                res.msg = "Error al consultar las ventas";
            }

        }
        catch(Exception e)
        {
            Log.e("error", e.toString());
            res.msg="Error al consultar las ventas";
        }
        return res;
    }

    public String getPermisoReporte(String idUsuario)
    {
        String res;
        final String NAMESPACE = "http://cacean.org/";
        final String URL = "http://getpoints.cacean.com/WSPuntosBeto.asmx"; //godaddy
        final String SOAPACTION = "http://cacean.org/GetPermisoReporte";
        final String METHOD = "GetPermisoReporte";

        try
        {

            // Model the request
            SoapObject request = new SoapObject(NAMESPACE, METHOD);
            //Add the parameters
            request.addProperty("idUsuario", idUsuario); // Paso parametros al WS
            //Model the envelope
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            //Model the transport
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            //Call the Web Service
            androidHttpTransport.call(SOAPACTION, envelope);

            //Get the response
            SoapObject response = (SoapObject) envelope.getResponse();
            //Obtener resultados de salida

            res = response.getPropertyAsString("permiso");

        }
        catch(Exception e)
        {
            Log.e("error", e.toString());
            res="Error al consultar las ventas";
        }
        return res;
    }

}
