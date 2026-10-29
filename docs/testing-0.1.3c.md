# Seguimiento de prueba — Alpha 0.1.3c

**Corte:** 2026-10-27. Guía para continuar la prueba mañana. La versión
publicada/oficial sigue siendo **0.1.2**; **0.1.3c** es una build de desarrollo,
no un release público.

## Estado

- El tester confirmó que el launcher ya envía informes después de configurar
  su clave. Es una confirmación del usuario; desde este entorno no se consultó
  la base ni los logs privados del hosting para verificarla independientemente.
- El usuario vio cuatro filas insertadas en MySQL; todavía no se ha identificado
  si pertenecen a `testers` o a `reports`. La consulta y la explicación de ambas
  tablas están abajo.
- El usuario confirmó posteriormente que el visor web administrativo funciona
  completamente en su hosting; no se han consultado sus datos privados desde
  este entorno. La auditoría de acceso y retención es independiente de esa
  confirmación funcional.
- El paquete compilado está en `%TEMP%\opencode\Zombie-Lane-Alpha-Local-Test-0.1.3c`.
  Se generó con `-SkipZip`: no se creó un ZIP del paquete. Incluye la versión
  `0.1.3c`, no incluye el servidor y puede reutilizarse para continuar el test.
- Desde este entorno, `GET /health.php` respondió HTTP 200 y cuerpo `ok`.
  `GET /reports/index.php` respondió 405, esperado porque ese endpoint solo
  recibe informes mediante POST.
- Sigue siendo recomendable comprobar el registro recibido y hacer una prueba
  sintética controlada antes de ampliar el test a más testers.

## Cambios incluidos en 0.1.3c

- **LOCAL/OFFLINE:** **Probar conexiones** hace un único GET a
  `https://reports.jorgelio.cl/health.php`. No contacta el servidor del juego,
  la web pública ni el estado público.
- **SERVER:** el botón mantiene cuatro GET: salud del servidor del juego,
  página pública, snapshot de estado y salud del receptor. No envía usuario,
  contraseña ni informe.
- El cliente usa las rutas PHP directas `health.php` y `reports/index.php`; no
  depende de reglas de reescritura `.htaccess`.
- Con consentimiento alpha, el cliente envía el ZIP de una sesión a
  `POST https://reports.jorgelio.cl/reports/index.php`, tanto en LOCAL como en
  SERVER. El límite es 1 MiB. Si falta la clave o falla el POST, conserva el
  ZIP en `logs/informes/pendientes`; al iniciar reintenta hasta tres pendientes.
  Solo mueve el ZIP a `logs/informes/enviados` tras HTTP 200. Los ZIP locales
  antiguos se purgan a los 30 días.
- El identificador de versión enviado es `0.1.3c`; la fuente del receptor
  permite esa versión. Si el servidor responde con rechazo de versión, hay que
  confirmar que se desplegó el `private/app.php` actualizado.

## Qué significan las filas y cómo revisar los informes

Primero identifica en qué tabla están las cuatro filas:

- `testers`: son claves de testers. `id` es el ID interno, `token_sha256` es
  solo el hash del token y `enabled=1` lo habilita (`0` lo desactiva). No son
  informes ni contienen su contenido. No compartas tokens ni hashes.
- `reports`: son informes recibidos. Cada fila describe un ZIP; contiene el ID
  del tester, un nombre de archivo aleatorio, tamaño, hash del ZIP, fecha de
  recepción y fecha de caducidad. Reintentar el mismo informe no debería crear
  una fila/archivo duplicado.

En phpMyAdmin, la siguiente consulta muestra metadatos recientes de los ZIPs;
no muestra el contenido ni el token:

```sql
SELECT id, tester_id, filename, bytes, HEX(sha256) AS zip_sha256,
       received_at, expires_at
FROM reports
ORDER BY received_at DESC;
```

El contenido está en un archivo ZIP bajo el directorio privado configurado por
el receptor (fuera de `public_html`/document root). `filename` permite localizar
el archivo dentro de ese directorio. Para revisarlo:

1. Usa el File Manager de DirectAdmin solo si permite entrar a ese directorio
   privado, o accede como propietario por SFTP/SSH. La ruta configurada está en
   el `config.php` privado del receptor; no publiques ese archivo ni sus
   credenciales.
2. Descarga una **copia** del ZIP usando el nombre de la fila. No lo muevas a
   una carpeta pública ni crees una URL para descargarlo. El nombre guardado
   puede no tener extensión; si hace falta, añade `.zip` solo a la copia local.
3. Ábrelo con 7-Zip. `informe.txt` y `sesion.json` resumen la sesión. También
   puede contener logs técnicos (`zombie-lane.log` y su rotación), acciones de
   juego (`acciones.jsonl`), opciones (`configuracion.txt`), configuración del
   launcher, datos del sistema/Java y `fallo.txt` si hubo un error.

El informe no incluye contraseñas ni la partida guardada, pero sí puede contener
el usuario/cuenta, la dirección del servidor, actividad de juego, opciones y
datos técnicos del equipo. Trátalo como información privada: no pegues el ZIP,
sus archivos, nombres de usuario ni hashes en el chat. El visor administrativo
añadido después permite leer estos datos con autenticación; su instalación está
en `report-receiver/VIEWER.md` del repositorio Revival. No hay descarga pública.

## Pruebas ejecutadas

- Build Windows `0.1.3c`: compiló 96 clases y pasó el smoke de ayuda del
  launcher; paquete ensamblado sin ZIP.
- `SessionReportSmoke` y `ConnectivityChecksSmoke`: pasaron con receptor local
  de prueba. El smoke de conectividad verifica que LOCAL solo contacte
  `/health.php`; también se corrigió el fixture para diferenciarlo del `/health`
  del juego.
- `LocalGameServerSmoke`: 205 comprobaciones pasaron.
- `ServerViewerSmoke`: pasó.
- `php -l` en los scripts del receptor y `php tests/zip.php`: pasaron.
- `git diff --check`: sin errores de whitespace en los cambios revisados.
- Hosting comprobado desde este entorno: health 200 `ok`; GET al endpoint POST
  405, como se esperaba. El usuario confirmó luego que el envío ya funciona.

Las pruebas automáticas usan loopback y no reemplazan una prueba completa del
hosting ni verifican por sí solas las filas/archivos de producción.

## Para continuar mañana

1. Reutiliza el paquete existente y no lo sobrescribas. Si hay que reconstruir,
   usa una carpeta de salida nueva.
2. Comprueba localmente que `report-token.txt` está en la carpeta de datos
   efectiva del launcher; por defecto es `%APPDATA%\ZombieLane-1.4.4`. No
   muestres el contenido del archivo.
3. Para terminar de revisar las cuatro filas, confirma si son de `testers` o de
   `reports`. Si son `reports`, consulta metadatos y, solo si necesitas leer el
   contenido, inspecciona una copia del ZIP desde el almacenamiento privado.
4. Con consentimiento, termina una sesión de prueba y busca en la consola el
   mensaje de informe recibido. Comprueba localmente que el ZIP dejó
   `pendientes` y está en `enviados`; en MySQL confirma el registro y hora.
5. Si algo falla, guarda el texto del error quitando tokens, hashes, cuentas,
   nombres de usuario y cualquier contenido personal. No reenvíes manualmente
   el mismo ZIP ni cambies la clave mientras el envío siga funcionando.

## Archivos de referencia

- Visor web administrativo implementado en el repositorio Revival y confirmado
  funcional en el hosting por el usuario:
  `report-receiver/VIEWER.md` documenta instalación, autenticación y pruebas;
  `public/admin/index.php` muestra testers e informes y `private/viewer.php`
  lee los diagnósticos del ZIP. Usa credenciales administrativas propias.

- Cliente: `src/launcher-ios/zlport/launcher/SessionReport.java`,
  `ConnectivityChecks.java`, `PreLauncherDialog.java` y `LauncherText.java`.
- Pruebas: `tests/SessionReportSmoke.java` y
  `tests/ConnectivityChecksSmoke.java`.
- Contrato del hosting: [report-receiver.md](report-receiver.md).
- Notas de versión: [release-0.1.3.md](release-0.1.3.md).
- Receptor privado Revival: `report-receiver/private/app.php`,
  `report-receiver/migrations/001_initial.sql` y
  `report-receiver/tests/synthetic.php`.
