package cacean.sorteos;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper para respuestas de apuestas (ApiDonBetoNode REST).
 * Compatible con VentaActivity y EditarActivity.
 */
public class MyWrapper {
    public SoapObject soap;
    public String data;
    public Integer cant;
    public String code;
    public String message;

    private List<Apuesta> apuestas;

    /** Constructor legacy (SoapObject) */
    public MyWrapper(SoapObject soap, String data, Integer cant, String code, String message) {
        this.soap = soap;
        this.data = data;
        this.cant = cant;
        this.code = code;
        this.message = message;
        this.apuestas = null;
    }

    /** Constructor para respuestas REST (List<Apuesta>) */
    public MyWrapper(List<Apuesta> apuestas, String data, Integer cant, String code, String message) {
        this.soap = null;
        this.apuestas = apuestas != null ? apuestas : new ArrayList<>();
        this.data = data;
        this.cant = cant != null ? cant : this.apuestas.size();
        this.code = code;
        this.message = message;
    }

    /** Obtener lista de apuestas (REST) */
    public List<Apuesta> getApuestas() {
        return apuestas != null ? apuestas : new ArrayList<>();
    }
}
