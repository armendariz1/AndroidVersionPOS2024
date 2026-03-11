# Migración de SOAP a ApiDonBetoNode (REST)

Este documento describe los ajustes realizados en la aplicación POS2024 para migrar de los servicios SOAP (Service1.svc y WSPuntosBeto.asmx) a la API REST **ApiDonBetoNode**, manteniendo las entradas y salidas de forma transparente para la aplicación.

## Resumen

La aplicación ahora utiliza **ApiDonBetoNode** como backend en lugar de los servicios web SOAP. Los cambios se realizaron de forma que la lógica de negocio y la interfaz de usuario no requieran modificaciones; solo la capa de comunicación con el servidor fue actualizada.

## Archivos modificados

### 1. `CargaDatosWS.java`
- **Ubicación:** `app/src/main/java/cacean/sorteos/CargaDatosWS.java`
- **Cambios:** Todas las llamadas SOAP fueron reemplazadas por llamadas REST usando `ApiClient`.
- **Mapeo de endpoints:**
  - Service1.svc → `/api/validarUsuario`, `/api/editarTicket`, `/api/agregarApuesta`, `/api/modificarApuesta`, `/api/eliminarApuesta`, `/api/copiarApuestas`, etc.
  - WSPuntosBeto.asmx → `/api/getPoints`, `/api/getSorteos`, `/api/getSucursal`, `/api/getApuestas`, `/api/getReporteVentas`, `/api/getPermisoReporte`
- Las firmas de los métodos públicos se mantienen igual; solo cambia la implementación interna.

### 2. `ApiClient.java` (nuevo)
- **Ubicación:** `app/src/main/java/cacean/sorteos/ApiClient.java`
- **Descripción:** Cliente HTTP para llamadas REST con métodos `post()` y `json()`.
- Usa `HttpURLConnection` y `org.json` para parsear respuestas JSON.
- La URL base se configura en `ApiClient.BASE_URL`.

### 3. `MyWrapper.java`
- **Ubicación:** `app/src/main/java/cacean/sorteos/MyWrapper.java`
- **Cambios:** Nuevo constructor `MyWrapper(List<Apuesta>, data, cant, code, message)` para respuestas REST.
- Nuevo método `getApuestas()` que devuelve `List<Apuesta>`.
- Se mantiene el constructor con `SoapObject` por compatibilidad con código legacy.

### 4. `VentaActivity.java`
- **Ubicación:** `app/src/main/java/cacean/sorteos/VentaActivity.java`
- **Cambios:** Sustituido el uso de `result.soap.getProperty(i)` por `result.getApuestas()` en:
  - `DownloadTaskAgregar`
  - `DownloadTaskModificar`
  - `DownloadTaskDelete`
  - `DownloadTaskCopiar`
- Eliminado el import de `SoapObject`.

### 5. `EditarActivity.java`
- **Ubicación:** `app/src/main/java/cacean/sorteos/EditarActivity.java`
- **Cambios:** Sustituido el uso de `result.soap.getProperty(i)` por `result.getApuestas()` en:
  - `DownloadTaskedtAgregar`
  - `DownloadTaskedtModificar`
  - `DownloadTaskedtInicia`
  - `DownloadTaskedtDelete`
- Eliminado el import de `SoapObject`.

### 6. `strings.xml`
- **Ubicación:** `app/src/main/res/values/strings.xml`
- **Cambios:** Añadido el recurso `api_base_url` con valor `http://10.0.2.2:3000/api`.

### 7. `Main.java`
- **Ubicación:** `app/src/main/java/cacean/sorteos/Main.java`
- **Cambios:** En `onCreate()` se asigna `ApiClient.BASE_URL = getString(R.string.api_base_url);`

## Configuración

### URL base de la API

La URL base se define en `strings.xml`:

```xml
<string name="api_base_url">http://10.0.2.2:3000/api</string>
```

- **Emulador Android:**  
  Usar `http://10.0.2.2:3000/api` (10.0.2.2 es el host del PC desde el emulador).

- **Dispositivo real:**  
  Cambiar por la IP del equipo donde corre ApiDonBetoNode, por ejemplo:  
  `http://192.168.1.100:3000/api`

### Requisitos

1. **ApiDonBetoNode** debe estar en ejecución en el puerto indicado (por defecto 3000).
2. El dispositivo o emulador debe estar en la misma red que el servidor (en dispositivos reales).
3. Si usas firewall, el puerto debe estar permitido.

## Compatibilidad

- Las entradas y salidas de los métodos de `CargaDatosWS` se mantienen igual.
- El resto de la aplicación (menús, pantallas, validaciones) no requiere cambios.
- La migración es transparente para el usuario final.

## Dependencias

- Se mantiene `org.json` para parseo JSON.
- Se eliminó la dependencia de `ksoap2` en los archivos que usan exclusivamente REST (VentaActivity, EditarActivity). Si otros módulos aún usan SOAP, la dependencia puede seguir en el proyecto.
