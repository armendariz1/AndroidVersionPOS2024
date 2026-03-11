package cacean.sorteos;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Cliente para ApiDonBetoNode (REST).
 * Reemplaza las llamadas SOAP a Service1.svc y WSPuntosBeto.asmx.
 * Entradas y salidas idénticas para transparencia con la aplicación POS2024.
 */
public class CargaDatosWS {

    private static final String KEY_USUARIO = "usuario";
    private static final String KEY_ID_TICKET = "idTicket";
    private static final String KEY_ID_SORTEO = "idSorteo";
    private static final String KEY_CODIGO = "Codigo";
    private static final String KEY_MENSAJE = "Mensaje";
    private static final String MSG_ERROR_CONEXION = "Error de conexión";
    private static final String PREFIX_ERROR = "Error ";
    private static final String MSG_ERROR_SIN_RESPUESTA = "Error: Sin respuesta";
    private static final String PREFIX_ERROR_COLON = "Error: ";
    private static final String MSG_ERROR_CONSULTA_VENTAS = "Error al consultar las ventas";
    private static final String MSG_SIN_RESPUESTA = "Sin respuesta";

    public CargaDatosWS(TicketActivity ticketActivity) {
    }

    public CargaDatosWS() {
    }

    public String getUsuario(String usuario, String pw) {
        String res = null;
        try {
            JSONObject body = ApiClient.json(KEY_USUARIO, usuario, "pw", pw);
            JSONObject response = ApiClient.post("/validarUsuario", body);
            if (response == null) {
                return MSG_ERROR_CONEXION;
            }
            String code = response.optString(KEY_CODIGO, "");
            String msg = response.optString(KEY_MENSAJE, "");
            String[] strAux = msg.split(",");

            Usuario.user = usuario;

            if ("00".equals(code)) {
                Usuario.nombre = strAux.length > 0 ? strAux[0] : "";
                Usuario.paterno = strAux.length > 1 ? strAux[1] : "";
                Usuario.perfil = strAux.length > 2 ? strAux[2] : "";
                res = "Bienvenido " + Usuario.nombre;
            } else {
                res = PREFIX_ERROR + msg;
                Usuario.perfil = "";
            }
        } catch (Exception e) {
            res = e.toString();
        }
        return res;
    }

    public String validaEditar(String ticket) {
        String res = null;
        try {
            JSONObject body = ApiClient.json(KEY_ID_TICKET, ticket);
            JSONObject response = ApiClient.post("/verificaEditarTicket", body);
            if (response == null) {
                return MSG_ERROR_SIN_RESPUESTA;
            }
            String code = response.optString(KEY_CODIGO, "");
            String msg = response.optString(KEY_MENSAJE, "");

            if ("00".equals(code)) {
                res = code + msg;
            } else {
                res = msg;
            }
        } catch (Exception e) {
            res = PREFIX_ERROR_COLON + e.toString();
        }
        return res;
    }

    public String validaTicket(String ticket) {
        String res = "";
        try {
            JSONObject body = ApiClient.json("parametro", ticket);
            JSONObject response = ApiClient.post("/verificaTicket", body);
            if (response == null) {
                return MSG_ERROR_SIN_RESPUESTA;
            }
            String code = response.optString(KEY_CODIGO, "");
            String msg = response.optString(KEY_MENSAJE, "");

            if ("00".equals(code)) {
                res = "OK" + msg;
            } else {
                res = msg;
            }
        } catch (Exception e) {
            res = PREFIX_ERROR_COLON + (e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return res;
    }

    public String marcaTicketPagado(String ticket, String usuario) {
        String res = "";
        try {
            JSONObject body = ApiClient.json("ticket", ticket, KEY_USUARIO, usuario);
            JSONObject response = ApiClient.post("/marcaTicketPagado", body);
            if (response == null) {
                return MSG_ERROR_SIN_RESPUESTA;
            }
            String code = response.optString(KEY_CODIGO, "");
            String msg = response.optString(KEY_MENSAJE, "");

            if ("00".equals(code)) {
                res = "OK" + msg;
            } else {
                res = msg;
            }
        } catch (Exception e) {
            res = PREFIX_ERROR_COLON + (e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return res;
    }

    public String crearTicket(String sorteo, String user) {
        String res = null;
        String puntos;
        String sucursal;
        try {
            JSONObject body = ApiClient.json(KEY_ID_SORTEO, sorteo, KEY_USUARIO, user);
            JSONObject response = ApiClient.post("/crearTicket", body);
            if (response == null) {
                return MSG_ERROR_CONEXION;
            }
            String code = response.optString(KEY_CODIGO, "");
            String msg = response.optString(KEY_MENSAJE, "");
            String ticket = response.optString("NumeroTicket", "");
            msg = msg.trim();
            msg = "      " + msg;

            puntos = getPuntos(ticket).trim();

            if (!"NO".equals(puntos) && puntos != null && !puntos.isEmpty()) {
                msg = msg + "\r\n       Puntos Don Beto";
                msg = msg + "\r\n        Vale " + puntos.trim() + " puntos";
            }

            sucursal = NombreSuc(user);
            msg = msg + "\r\nVendido en sucursal:" + sucursal;

            if ("00".equals(code)) {
                Imprimir imprime = new Imprimir();
                res = imprime.PrintStr(msg, "VENTA", ticket, Usuario.user);
                res = code + ticket;
            } else {
                res = msg;
            }
        } catch (Exception e) {
            res = e.toString();
        }
        return res;
    }

    public MyWrapper editarTicket(String ticket, String user, Context cont) {
        String code = null;
        String msg = null;
        String strTotal = null;
        List<Apuesta> apuestas = new ArrayList<>();
        Integer cant = 0;

        try {
            JSONObject body = ApiClient.json(KEY_ID_TICKET, ticket, KEY_USUARIO, user);
            JSONObject response = ApiClient.post("/editarTicket", body);
            if (response == null) {
                return new MyWrapper(apuestas, "", 0, "02", MSG_SIN_RESPUESTA);
            }
            code = response.optString(KEY_CODIGO, "");
            msg = response.optString(KEY_MENSAJE, "");

            if ("00".equals(code)) {
                strTotal = response.optString("TotalTemp", "");
                JSONArray arr = response.optJSONArray("ApuestasTemp");
                if (arr != null) {
                    apuestas = parseApuestasTemp(arr);
                    cant = apuestas.size();
                }
            }
        } catch (Exception e) {
            Log.e(CargaDatosWS.class.getSimpleName(), "", e);
        }
        return new MyWrapper(apuestas, strTotal, cant, code, msg);
    }

    public MyWrapper eliminaApuesta(String apuesta, String user, Context cont) {
        String code = null;
        String msg = null;
        String strTotal = null;
        List<Apuesta> apuestas = new ArrayList<>();
        Integer cant = 0;

        try {
            JSONObject body = ApiClient.json("idApuesta", apuesta, KEY_USUARIO, user);
            JSONObject response = ApiClient.post("/eliminaApuesta", body);
            if (response == null) {
                return new MyWrapper(apuestas, "", 0, "02", MSG_SIN_RESPUESTA);
            }
            code = response.optString(KEY_CODIGO, "");
            msg = response.optString(KEY_MENSAJE, "");
            strTotal = response.optString("TotalTemp", "");
            JSONArray arr = response.optJSONArray("ApuestasTemp");
            if (arr != null) {
                apuestas = parseApuestasTemp(arr);
                cant = apuestas.size();
            }
        } catch (Exception e) {
            Log.e(CargaDatosWS.class.getSimpleName(), "", e);
        }
        return new MyWrapper(apuestas, strTotal, cant, code, msg);
    }

    public String eliminaTicket(String ticket, String user) {
        String res = null;
        try {
            JSONObject body = ApiClient.json(KEY_ID_TICKET, ticket, KEY_USUARIO, user);
            JSONObject response = ApiClient.post("/eliminaTicket", body);
            if (response == null) {
                return MSG_ERROR_CONEXION;
            }
            String code = response.optString(KEY_CODIGO, "");
            String msg = response.optString(KEY_MENSAJE, "");

            if ("00".equals(code)) {
                res = "Respuesta correcta " + msg;
            } else {
                res = PREFIX_ERROR + msg;
            }
        } catch (Exception e) {
            res = e.toString();
        }
        return res;
    }

    public MyWrapper agregaApuesta(String sorteo, String numero, String monto1, String monto2, String monto3, String user, Context cont) {
        String code = null;
        String msg = null;
        String strTotal = null;
        List<Apuesta> apuestas = new ArrayList<>();
        Integer cant = 0;

        try {
            JSONObject body = ApiClient.json(
                    KEY_ID_SORTEO, sorteo,
                    "numero", numero,
                    "montoPrimero", monto1,
                    "montoSegundo", monto2,
                    "montoTercero", monto3,
                    KEY_USUARIO, user
            );
            JSONObject response = ApiClient.post("/insertaApuesta", body);
            if (response == null) {
                return new MyWrapper(apuestas, "", 0, "02", MSG_SIN_RESPUESTA);
            }
            code = response.optString(KEY_CODIGO, "");
            msg = response.optString(KEY_MENSAJE, "");

            if ("00".equals(code)) {
                strTotal = response.optString("TotalTemp", "");
                JSONArray arr = response.optJSONArray("ApuestasTemp");
                if (arr != null) {
                    apuestas = parseApuestasTemp(arr);
                    cant = apuestas.size();
                }
            } else {
                Toast.makeText(cont, msg, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(CargaDatosWS.class.getSimpleName(), "", e);
        }
        return new MyWrapper(apuestas, strTotal, cant, code, msg);
    }

    public String limpiaTablaTemp(String user) {
        String res = null;
        try {
            JSONObject body = ApiClient.json(KEY_USUARIO, user);
            JSONObject response = ApiClient.post("/limpiaTablaTemp", body);
            if (response == null) {
                return MSG_ERROR_CONEXION;
            }
            res = response.optString(KEY_CODIGO, "");
        } catch (Exception e) {
            Log.e(CargaDatosWS.class.getSimpleName(), "", e);
            res = e.toString();
        }
        return res;
    }

    public MyWrapper modificarApuestaTicket(String sorteo, String apuesta, String numero, String monto1, String monto2, String monto3, String user, Context cont) {
        String code = null;
        String msg = null;
        String strTotal = null;
        List<Apuesta> apuestas = new ArrayList<>();
        Integer cant = 0;

        try {
            JSONObject body = ApiClient.json(
                    KEY_ID_SORTEO, sorteo,
                    "idApuesta", apuesta,
                    "numero", numero,
                    "montoPrimero", monto1,
                    "montoSegundo", monto2,
                    "montoTercero", monto3,
                    KEY_USUARIO, user
            );
            JSONObject response = ApiClient.post("/modificarApuestaTicket", body);
            if (response == null) {
                return new MyWrapper(apuestas, "", 0, "02", MSG_SIN_RESPUESTA);
            }
            code = response.optString(KEY_CODIGO, "");
            msg = response.optString(KEY_MENSAJE, "");
            strTotal = response.optString("TotalTemp", "");
            JSONArray arr = response.optJSONArray("ApuestasTemp");
            if (arr != null) {
                apuestas = parseApuestasTemp(arr);
                cant = apuestas.size();
            }
        } catch (Exception e) {
            Log.e(CargaDatosWS.class.getSimpleName(), "", e);
        }
        return new MyWrapper(apuestas, strTotal, cant, code, msg);
    }

    public String terminaEditarTicket(String ticket, String user) {
        String res = null;
        try {
            JSONObject body = ApiClient.json(KEY_ID_TICKET, ticket, KEY_USUARIO, user);
            JSONObject response = ApiClient.post("/terminaEditarTicket", body);
            if (response == null) {
                return MSG_ERROR_CONEXION;
            }
            String code = response.optString(KEY_CODIGO, "");
            String msg = response.optString(KEY_MENSAJE, "");
            msg = msg.trim();
            msg = "      " + msg;

            if ("00".equals(code)) {
                Imprimir imprime = new Imprimir();
                res = imprime.PrintStr(msg, "EDITAR", ticket, Usuario.user);
                res = code;
            } else {
                res = PREFIX_ERROR + msg;
            }
        } catch (Exception e) {
            res = e.toString();
        }
        return res;
    }

    public String getPuntos(String idticket) {
        String res;
        try {
            JSONObject body = ApiClient.json("idticket", idticket);
            JSONObject response = ApiClient.post("/getPoints", body);
            if (response == null) {
                return "NO";
            }
            String code = response.optString(KEY_CODIGO, "");
            if ("00".equals(code)) {
                res = response.optString("Resultado", "");
            } else {
                res = "NO";
            }
        } catch (Exception e) {
            res = "NO";
        }
        return res;
    }

    public LinkedList<SorteoLvw> getSorteoActivosNew(Context cont, String user) {
        LinkedList<SorteoLvw> sorteos = new LinkedList<>();
        try {
            JSONObject body = ApiClient.json("idusuario", user);
            JSONObject response = ApiClient.post("/getSorteos", body);
            if (response == null) {
                return sorteos;
            }
            String code = response.optString(KEY_CODIGO, "");
            JSONArray arr = response.optJSONArray("Sorteos");

            if ("00".equals(code) && arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject so = arr.getJSONObject(i);
                    String id = so.optString("IdSorteo", "");
                    String fecha = so.optString("FechaSorteo", "");
                    String nombre = so.optString("NombreSorteo", "");
                    sorteos.add(new SorteoLvw(id, fecha + " " + nombre));
                }
            }
        } catch (Exception e) {
            Log.e(CargaDatosWS.class.getSimpleName(), "", e);
        }
        return sorteos;
    }

    public String NombreSuc(String user) {
        String res;
        try {
            JSONObject body = ApiClient.json("idusuario", user);
            JSONObject response = ApiClient.post("/getSucursal", body);
            if (response == null) {
                return "N/A";
            }
            res = response.optString("NombreSucursal", "N/A");
        } catch (Exception e) {
            res = "N/A";
        }
        return res;
    }

    public MyWrapper CopiaApuestas(String ticket, String user, Context cont) {
        String code = null;
        String msg = null;
        String strTotal = null;
        List<Apuesta> apuestas = new ArrayList<>();
        Integer cant = 0;

        try {
            JSONObject body = ApiClient.json("id_ticket", ticket, "user", user);
            JSONObject response = ApiClient.post("/getApuestas", body);
            if (response == null) {
                return new MyWrapper(apuestas, "", 0, "02", MSG_SIN_RESPUESTA);
            }
            code = response.optString(KEY_CODIGO, "");
            msg = response.optString(KEY_MENSAJE, "");

            if ("00".equals(code)) {
                strTotal = response.optString("Data", "");
                JSONArray arr = response.optJSONArray("Apuestas");
                if (arr != null) {
                    apuestas = parseApuestasGetApuestas(arr);
                    cant = apuestas.size();
                }
            } else {
                Toast.makeText(cont, msg, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(CargaDatosWS.class.getSimpleName(), "", e);
        }
        return new MyWrapper(apuestas, strTotal, cant, code, msg);
    }

    public ReporteVenta getReporteventas(String idUsuario) {
        ReporteVenta res = new ReporteVenta();
        try {
            JSONObject body = ApiClient.json("idUsuario", idUsuario);
            JSONObject response = ApiClient.post("/getReporteVentas", body);
            if (response == null) {
                res.msg = MSG_ERROR_CONSULTA_VENTAS;
                return res;
            }

            res.reporteText = "";
            res.codigo = response.optString(KEY_CODIGO, "02");

            if ("00".equals(res.codigo)) {
                res.idSucursal = response.optString("IdSucursal", "");
                res.nombreSucursal = response.optString("NombreSucursal", "");
                res.idSorteo = response.optString("IdSorteo", "");
                res.nombreSorteo = response.optString("NombreSorteo", "");
                res.ventaHoy = response.optString("VentaHoy", "");

                res.nombreSorteo1 = response.optString("NombreSorteo1", "");
                res.fechaSorteo1 = response.optString("FechaSorteo1", "");
                res.ventaSorteo1 = response.optString("VentaSorteo1", "");

                res.nombreSorteo2 = response.optString("NombreSorteo2", "");
                res.fechaSorteo2 = response.optString("FechaSorteo2", "");
                res.ventaSorteo2 = response.optString("VentaSorteo2", "");

                res.nombreSorteo3 = response.optString("NombreSorteo3", "");
                res.fechaSorteo3 = response.optString("FechaSorteo3", "");
                res.ventaSorteo3 = response.optString("VentaSorteo3", "");

                res.nombreSorteo4 = response.optString("NombreSorteo4", "");
                res.fechaSorteo4 = response.optString("FechaSorteo4", "");
                res.ventaSorteo4 = response.optString("VentaSorteo4", "");

                res.nombreSorteo5 = response.optString("NombreSorteo5", "");
                res.fechaSorteo5 = response.optString("FechaSorteo5", "");
                res.ventaSorteo5 = response.optString("VentaSorteo5", "");

                res.reporteText = response.optString("ReporteText", "");
                res.msg = "Reporte generado correctamente";
            } else {
                res.msg = MSG_ERROR_CONSULTA_VENTAS;
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
            res.msg = MSG_ERROR_CONSULTA_VENTAS;
        }
        return res;
    }

    public String getPermisoReporte(String idUsuario) {
        String res;
        try {
            JSONObject body = ApiClient.json("idUsuario", idUsuario);
            JSONObject response = ApiClient.post("/getPermisoReporte", body);
            if (response == null) {
                return MSG_ERROR_CONSULTA_VENTAS;
            }
            res = response.optString("Permiso", "");
        } catch (Exception e) {
            Log.e("error", e.toString());
            res = MSG_ERROR_CONSULTA_VENTAS;
        }
        return res;
    }

    private List<Apuesta> parseApuestasTemp(JSONArray arr) {
        List<Apuesta> list = new ArrayList<>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new Apuesta(
                        o.optString("IdApuesta", ""),
                        o.optString("Numero", ""),
                        o.optString("MontoPrimero", ""),
                        o.optString("MontoSegundo", ""),
                        o.optString("MontoTercero", "")
                ));
            }
        } catch (Exception e) {
            Log.e(CargaDatosWS.class.getSimpleName(), "parseApuestasTemp", e);
        }
        return list;
    }

    private List<Apuesta> parseApuestasGetApuestas(JSONArray arr) {
        List<Apuesta> list = new ArrayList<>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new Apuesta(
                        o.optString("ID", ""),
                        o.optString("NUMERO", ""),
                        o.optString("MONTO_APUESTA", ""),
                        o.optString("APUESTA_SEGUNDO", ""),
                        o.optString("APUESTA_TERCERO", "")
                ));
            }
        } catch (Exception e) {
            Log.e(CargaDatosWS.class.getSimpleName(), "parseApuestasGetApuestas", e);
        }
        return list;
    }
}
