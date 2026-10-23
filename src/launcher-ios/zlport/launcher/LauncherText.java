package zlport.launcher;

import java.util.HashMap;
import java.util.Map;

/** Small embedded catalogue for the launcher UI; no external properties are needed at runtime. */
public final class LauncherText {
   private static final String DEFAULT_LANGUAGE = LauncherLanguage.systemDefault().code();
   private static String currentLanguage = DEFAULT_LANGUAGE;

   private static final Map<String, String> ENGLISH = map(
      "button.play", "Play",
      "button.cancel", "Cancel",
      "button.privacy", "Privacy...",
      "button.createProfile", "Create new profile...",
      "button.probe", "Test connection",
      "button.start", "Start",
      "button.create", "Create",
      "button.acceptSend", "Accept and send",
      "button.notNow", "Not now",
      "button.continue", "Continue",
      "section.connection", "Connection",
      "section.profile", "Local profile",
      "section.game", "Game options",
      "section.advanced", "Advanced",
      "mode.local", "Local (offline)",
      "mode.server", "Server",
      "label.server", "Server:",
      "label.port", "Port:",
      "label.user", "User:",
      "label.password", "Password:",
      "check.register", "Create new account",
      "check.rememberUser", "Remember user",
      "warning.account", "<html>Create an account or sign in. The password is stored on the server as a PBKDF2 hash, never on this computer.<br>HTTP does not encrypt the connection: use it only on this machine or a trusted local network.</html>",
      "check.fullscreen", "Fullscreen",
      "check.maximized", "Maximized",
      "label.windowSize", "Window size (px):",
      "check.pauseFocus", "Pause when focus is lost",
      "check.audio", "Sound",
      "label.zoom", "Mouse wheel zoom step (1.01–1.5):",
      "label.recharge", "Energy: seconds per point (1–3600):",
      "label.spawn", "Zombies: multiplier (1.0–5.0):",
      "label.neighbor", "Neighbors: minutes (1–1440):",
      "check.advanced", "Advanced options",
      "label.logLevel", "Log level (diagnostic):",
      "check.glDebug", "GL debug (checks glGetError on every call)",
      "profile.namePrompt", "Profile name:",
      "profile.createTitle", "Create new profile",
      "profile.nameRequired", "Enter a profile name.",
      "profile.createFailed", "Could not create the profile: {0}",
      "profile.noProfiles", "No profiles: create one to start.",
      "profile.localLabel", "Local profile:",
      "profile.legacyWarning", "An older save was found outside players; it will not be copied or modified.",
      "profile.pickerTitle", "Zombie Lane - Select profile",
      "profile.existingName", "Existing profile",
      "profile.noProfilesShort", "No local profiles. Create one to start.",
      "error.invalidOptionsTitle", "Zombie Lane: invalid options",
      "error.serverUnavailableTitle", "Zombie Lane: server unavailable",
      "error.loginTitle", "Could not sign in",
      "error.saveOptions", "Could not save options: {0}",
      "error.saveLanguage", "Could not save the language preference: {0}",
      "error.serverGate", "Could not connect to server {0}:{1} ({2}).\nYou cannot play online without a server connection.",
      "error.hostRequired", "Enter the server host or IP.",
      "error.hostTooLong", "The host cannot exceed {0} characters.",
      "error.hostPortField", "Enter the port in its own field; IPv6 is not allowed in this field.",
      "error.hostPaths", "The host cannot contain paths or separators; enter only the IP or name.",
      "error.hostSpaces", "The host cannot contain spaces.",
      "error.userTooLong", "The user cannot exceed {0} characters.",
      "error.userChars", "The user only allows letters, numbers, spaces and . _ -",
      "error.userRequired", "Enter your server account user.",
      "error.userMin", "The user must be at least {0} characters.",
      "error.passwordLength", "Password must be between {0} and {1} characters.",
      "error.zoom", "Mouse wheel zoom step '{0}' is invalid: from {1} to {2}",
      "error.level", "Log level '{0}' is invalid: SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL or OFF",
      "error.recharge", "Energy (seconds per point) '{0}' is invalid: from 1 to {1}",
      "error.spawn", "Zombie multiplier '{0}' is invalid: from {1} to {2}",
      "error.neighbor", "Neighbor minutes '{0}' are invalid: from 1 to {1}",
      "error.port", "Port '{0}' is invalid: must be an integer from {1} to {2}",
      "error.width", "Window width '{0}' is invalid: integer from {1} to {2}",
      "error.height", "Window height '{0}' is invalid: integer from {1} to {2}",
      "error.endpointFormat", "Enter a HOST:PORT address, for example 192.168.1.10:6767",
      "error.endpointFormatGiven", "Enter a HOST:PORT address, for example 192.168.1.10:6767, not '{0}'",
      "error.authInvalid", "Invalid authentication response.",
      "probe.checking", "Checking {0} ...",
      "probe.ok", "OK: server responds at {0}",
      "probe.noResponse", "No response from server ({0})",
      "language.title", "Language",
      "language.prompt", "Choose the language for the launcher and the game.",
      "language.label", "Language:",
      "language.note", "You can change it later in the game settings.",
      "alpha.title", "Zombie Lane alpha: diagnostic reports",
      "alpha.question", "Send diagnostic reports to the alpha server?",
      "alpha.details", "What is sent:\n"
         + "  - Technical session log (errors, warnings and timings).\n"
         + "  - Game actions recorded by the game (no passwords).\n"
         + "  - Version, operating system, Java, memory and machine data.\n"
         + "  - Your alpha user name and account identifier.\n\n"
         + "What is NOT sent:\n"
         + "  - Passwords or the saved game.\n\n"
         + "The alpha server keeps reports for 30 days. The organizer may keep\n"
         + "copies of this alpha for longer.\n\n"
         + "You can change this choice at any time with the Privacy button\n"
         + "in the launcher.",
      "alpha.accept", "Accept and send",
      "alpha.notNow", "Not now"
   );

   private static final Map<String, String> SPANISH = map(
      "button.play", "Jugar",
      "button.cancel", "Cancelar",
      "button.privacy", "Privacidad...",
      "button.createProfile", "Crear perfil nuevo...",
      "button.probe", "Probar conexión",
      "button.start", "Iniciar",
      "button.create", "Crear",
      "button.acceptSend", "Aceptar y enviar",
      "button.notNow", "Ahora no",
      "button.continue", "Continuar",
      "section.connection", "Conexión",
      "section.profile", "Perfil local",
      "section.game", "Opciones del juego",
      "section.advanced", "Avanzado",
      "mode.local", "Local (sin red)",
      "mode.server", "Servidor",
      "label.server", "Servidor:",
      "label.port", "Puerto:",
      "label.user", "Usuario:",
      "label.password", "Contraseña:",
      "check.register", "Crear cuenta nueva",
      "check.rememberUser", "Recordar usuario",
      "warning.account", "<html>Crea una cuenta o inicia sesión. La contraseña se guarda en el servidor como hash PBKDF2, nunca en este equipo.<br>HTTP no cifra la conexión: utiliza solo en esta máquina o una red local de confianza.</html>",
      "check.fullscreen", "Pantalla completa",
      "check.maximized", "Maximizada",
      "label.windowSize", "Tamaño de la ventana (px):",
      "check.pauseFocus", "Pausar al perder el foco",
      "check.audio", "Sonido",
      "label.zoom", "Zoom por paso de rueda (1.01–1.5):",
      "label.recharge", "Energía: segundos por punto (1–3600):",
      "label.spawn", "Zombis: multiplicador (1.0–5.0):",
      "label.neighbor", "Vecinos: minutos (1–1440):",
      "check.advanced", "Opciones avanzadas",
      "label.logLevel", "Nivel del log (diagnóstico):",
      "check.glDebug", "Depuración GL (comprueba glGetError en cada llamada)",
      "profile.namePrompt", "Nombre del perfil:",
      "profile.createTitle", "Crear perfil nuevo",
      "profile.nameRequired", "Escribe un nombre para el perfil.",
      "profile.createFailed", "No se pudo crear el perfil: {0}",
      "profile.noProfiles", "No hay perfiles: crea uno nuevo para empezar.",
      "profile.localLabel", "Perfil local:",
      "profile.legacyWarning", "Se detectó un guardado anterior fuera de players; no se copiará ni modificará.",
      "profile.pickerTitle", "Zombie Lane - Seleccionar perfil",
      "profile.existingName", "Perfil existente",
      "profile.noProfilesShort", "No hay perfiles locales. Crea uno nuevo para empezar.",
      "error.invalidOptionsTitle", "Zombie Lane: opciones inválidas",
      "error.serverUnavailableTitle", "Zombie Lane: servidor no disponible",
      "error.loginTitle", "No se pudo iniciar sesión",
      "error.saveOptions", "No se pudieron guardar las opciones: {0}",
      "error.saveLanguage", "No se pudo guardar la preferencia de idioma: {0}",
      "error.serverGate", "No se pudo conectar con el servidor {0}:{1} ({2}).\nSin conexión con el servidor no se puede jugar en línea.",
      "error.hostRequired", "Escribe el host o la IP del servidor.",
      "error.hostTooLong", "El host no puede pasar de {0} caracteres.",
      "error.hostPortField", "Escribe el puerto en su campo; IPv6 no está permitido en este campo.",
      "error.hostPaths", "El host no puede contener rutas ni separadores; escribe solo la IP o el nombre.",
      "error.hostSpaces", "El host no puede contener espacios.",
      "error.userTooLong", "El usuario no puede pasar de {0} caracteres.",
      "error.userChars", "El usuario solo admite letras, números, espacios y . _ -",
      "error.userRequired", "Escribe el usuario de tu cuenta del servidor.",
      "error.userMin", "El usuario debe tener al menos {0} caracteres.",
      "error.passwordLength", "La contraseña debe tener entre {0} y {1} caracteres.",
      "error.zoom", "El zoom por paso de rueda '{0}' no es válido: de {1} a {2}",
      "error.level", "El nivel del log '{0}' no es válido: SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL u OFF",
      "error.recharge", "La energía (segundos por punto) '{0}' no es válida: de 1 a {1}",
      "error.spawn", "El multiplicador de zombis '{0}' no es válido: de {1} a {2}",
      "error.neighbor", "Los minutos de vecinos '{0}' no son válidos: de 1 a {1}",
      "error.port", "El puerto '{0}' no es válido: debe ser un entero de {1} a {2}",
      "error.width", "El ancho de la ventana '{0}' no es válido: entero de {1} a {2}",
      "error.height", "El alto de la ventana '{0}' no es válido: entero de {1} a {2}",
      "error.endpointFormat", "Escribe una dirección HOST:PUERTO, por ejemplo 192.168.1.10:6767",
      "error.endpointFormatGiven", "Escribe una dirección HOST:PUERTO, por ejemplo 192.168.1.10:6767, no '{0}'",
      "error.authInvalid", "Respuesta de autenticación inválida.",
      "probe.checking", "Comprobando {0} ...",
      "probe.ok", "OK: el servidor responde en {0}",
      "probe.noResponse", "Sin respuesta del servidor ({0})",
      "language.title", "Idioma",
      "language.prompt", "Elige el idioma del lanzador y del juego.",
      "language.label", "Idioma:",
      "language.note", "Puedes cambiarlo más tarde en las opciones del juego.",
      "alpha.title", "Zombie Lane alpha: informes de diagnóstico",
      "alpha.question", "¿Enviar informes de diagnóstico al servidor de la alpha?",
      "alpha.details", "Qué se envía:\n"
         + "  - Registro técnico de la sesión (errores, avisos y tiempos).\n"
         + "  - Acciones de juego registradas por el juego (sin contraseñas).\n"
         + "  - Versión, sistema operativo, Java, memoria y datos del equipo.\n"
         + "  - Tu nombre de usuario de la alpha y el identificador de cuenta.\n\n"
         + "Qué NO se envía:\n"
         + "  - Contraseñas ni la partida guardada.\n\n"
         + "El servidor de la alpha conserva los informes 30 días. El organizador\n"
         + "puede conservar copias de esta alpha durante más tiempo.\n\n"
         + "Puedes cambiar esta elección cuando quieras con el botón Privacidad\n"
         + "del lanzador.",
      "alpha.accept", "Aceptar y enviar",
      "alpha.notNow", "Ahora no"
   );

   private static final Map<String, String> FRENCH = map(
      "button.play", "Jouer",
      "button.cancel", "Annuler",
      "button.privacy", "Confidentialité...",
      "button.createProfile", "Créer un profil...",
      "button.probe", "Tester la connexion",
      "button.start", "Démarrer",
      "button.create", "Créer",
      "button.acceptSend", "Accepter et envoyer",
      "button.notNow", "Pas maintenant",
      "button.continue", "Continuer",
      "section.connection", "Connexion",
      "section.profile", "Profil local",
      "section.game", "Options du jeu",
      "section.advanced", "Avancé",
      "mode.local", "Local (hors ligne)",
      "mode.server", "Serveur",
      "label.server", "Serveur :",
      "label.port", "Port :",
      "label.user", "Utilisateur :",
      "label.password", "Mot de passe :",
      "check.register", "Créer un compte",
      "check.rememberUser", "Se souvenir de l'utilisateur",
      "warning.account", "<html>Créez un compte ou connectez-vous. Le mot de passe est stocké sur le serveur sous forme de hachage PBKDF2, jamais sur cet ordinateur.<br>HTTP ne chiffre pas la connexion : utilisez-la uniquement sur cette machine ou sur un réseau local de confiance.</html>",
      "check.fullscreen", "Plein écran",
      "check.maximized", "Agrandie",
      "label.windowSize", "Taille de la fenêtre (px) :",
      "check.pauseFocus", "Mettre en pause si la fenêtre perd le focus",
      "check.audio", "Son",
      "label.zoom", "Zoom par cran de molette (1.01–1.5) :",
      "label.recharge", "Énergie : secondes par point (1–3600) :",
      "label.spawn", "Zombies : multiplicateur (1.0–5.0) :",
      "label.neighbor", "Voisins : minutes (1–1440) :",
      "check.advanced", "Options avancées",
      "label.logLevel", "Niveau de journal (diagnostic) :",
      "check.glDebug", "Débogage GL (vérifie glGetError à chaque appel)",
      "profile.namePrompt", "Nom du profil :",
      "profile.createTitle", "Créer un profil",
      "profile.nameRequired", "Saisissez un nom de profil.",
      "profile.createFailed", "Impossible de créer le profil : {0}",
      "profile.noProfiles", "Aucun profil : créez-en un pour commencer.",
      "profile.localLabel", "Profil local :",
      "profile.legacyWarning", "Une ancienne sauvegarde a été trouvée hors de players ; elle ne sera ni copiée ni modifiée.",
      "profile.pickerTitle", "Zombie Lane - Sélectionner un profil",
      "profile.existingName", "Profil existant",
      "profile.noProfilesShort", "Aucun profil local. Créez-en un pour commencer.",
      "error.invalidOptionsTitle", "Zombie Lane : options invalides",
      "error.serverUnavailableTitle", "Zombie Lane : serveur indisponible",
      "error.loginTitle", "Connexion impossible",
      "error.saveOptions", "Impossible d’enregistrer les options : {0}",
      "error.saveLanguage", "Impossible d’enregistrer la préférence de langue : {0}",
      "error.serverGate", "Impossible de se connecter au serveur {0}:{1} ({2}).\nSans connexion au serveur, impossible de jouer en ligne.",
      "error.hostRequired", "Saisissez l’hôte ou l’IP du serveur.",
      "error.hostTooLong", "L’hôte ne peut pas dépasser {0} caractères.",
      "error.hostPortField", "Saisissez le port dans son champ ; IPv6 n’est pas autorisé dans ce champ.",
      "error.hostPaths", "L’hôte ne peut pas contenir de chemins ni de séparateurs ; saisissez uniquement l’IP ou le nom.",
      "error.hostSpaces", "L’hôte ne peut pas contenir d’espaces.",
      "error.userTooLong", "L’utilisateur ne peut pas dépasser {0} caractères.",
      "error.userChars", "L’utilisateur n’accepte que les lettres, les chiffres, les espaces et . _ -",
      "error.userRequired", "Saisissez l’utilisateur de votre compte serveur.",
      "error.userMin", "L’utilisateur doit comporter au moins {0} caractères.",
      "error.passwordLength", "Le mot de passe doit comporter entre {0} et {1} caractères.",
      "error.zoom", "Le zoom par cran de molette '{0}' n’est pas valide : de {1} à {2}",
      "error.level", "Le niveau de journal '{0}' n’est pas valide : SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL ou OFF",
      "error.recharge", "L’énergie (secondes par point) '{0}' n’est pas valide : de 1 à {1}",
      "error.spawn", "Le multiplicateur de zombies '{0}' n’est pas valide : de {1} à {2}",
      "error.neighbor", "Les minutes de voisins '{0}' ne sont pas valides : de 1 à {1}",
      "error.port", "Le port '{0}' n’est pas valide : doit être un entier de {1} à {2}",
      "error.width", "La largeur de la fenêtre '{0}' n’est pas valide : entier de {1} à {2}",
      "error.height", "La hauteur de la fenêtre '{0}' n’est pas valide : entier de {1} à {2}",
      "error.endpointFormat", "Saisissez une adresse HOST:PORT, par exemple 192.168.1.10:6767",
      "error.endpointFormatGiven", "Saisissez une adresse HOST:PORT, par exemple 192.168.1.10:6767, et non '{0}'",
      "error.authInvalid", "Réponse d’authentification invalide.",
      "probe.checking", "Vérification de {0} ...",
      "probe.ok", "OK : le serveur répond à {0}",
      "probe.noResponse", "Aucune réponse du serveur ({0})",
      "language.title", "Langue",
      "language.prompt", "Choisissez la langue du lanceur et du jeu.",
      "language.label", "Langue :",
      "language.note", "Vous pourrez la modifier plus tard dans les options du jeu.",
      "alpha.title", "Zombie Lane alpha : rapports de diagnostic",
      "alpha.question", "Envoyer les rapports de diagnostic au serveur de l’alpha ?",
      "alpha.details", "Ce qui est envoyé :\n"
         + "  - Journal technique de la session (erreurs, avertissements et durées).\n"
         + "  - Actions de jeu enregistrées par le jeu (sans mots de passe).\n"
         + "  - Version, système d’exploitation, Java, mémoire et données de la machine.\n"
         + "  - Votre nom d’utilisateur alpha et l’identifiant de compte.\n\n"
         + "Ce qui n’est PAS envoyé :\n"
         + "  - Les mots de passe ni la sauvegarde.\n\n"
         + "Le serveur de l’alpha conserve les rapports 30 jours. L’organisateur\n"
         + "peut conserver des copies de cette alpha plus longtemps.\n\n"
         + "Vous pouvez modifier ce choix à tout moment avec le bouton Confidentialité\n"
         + "du lanceur.",
      "alpha.accept", "Accepter et envoyer",
      "alpha.notNow", "Pas maintenant"
   );

   private static final Map<String, String> ITALIAN = map(
      "button.play", "Gioca",
      "button.cancel", "Annulla",
      "button.privacy", "Privacy...",
      "button.createProfile", "Crea nuovo profilo...",
      "button.probe", "Prova connessione",
      "button.start", "Avvia",
      "button.create", "Crea",
      "button.acceptSend", "Accetta e invia",
      "button.notNow", "Non ora",
      "button.continue", "Continua",
      "section.connection", "Connessione",
      "section.profile", "Profilo locale",
      "section.game", "Opzioni di gioco",
      "section.advanced", "Avanzate",
      "mode.local", "Locale (offline)",
      "mode.server", "Server",
      "label.server", "Server:",
      "label.port", "Porta:",
      "label.user", "Utente:",
      "label.password", "Password:",
      "check.register", "Crea nuovo account",
      "check.rememberUser", "Ricorda utente",
      "warning.account", "<html>Crea un account o accedi. La password viene salvata sul server come hash PBKDF2, mai su questo computer.<br>HTTP non cifra la connessione: usala solo su questa macchina o su una rete locale affidabile.</html>",
      "check.fullscreen", "Schermo intero",
      "check.maximized", "Massimizzata",
      "label.windowSize", "Dimensione finestra (px):",
      "check.pauseFocus", "Pausa quando perdi il focus",
      "check.audio", "Audio",
      "label.zoom", "Zoom per scatto rotella (1.01–1.5):",
      "label.recharge", "Energia: secondi per punto (1–3600):",
      "label.spawn", "Zombi: moltiplicatore (1.0–5.0):",
      "label.neighbor", "Vicini: minuti (1–1440):",
      "check.advanced", "Opzioni avanzate",
      "label.logLevel", "Livello log (diagnostica):",
      "check.glDebug", "Debug GL (controlla glGetError a ogni chiamata)",
      "profile.namePrompt", "Nome profilo:",
      "profile.createTitle", "Crea nuovo profilo",
      "profile.nameRequired", "Scrivi un nome per il profilo.",
      "profile.createFailed", "Impossibile creare il profilo: {0}",
      "profile.noProfiles", "Nessun profilo: creane uno per iniziare.",
      "profile.localLabel", "Profilo locale:",
      "profile.legacyWarning", "È stato trovato un salvataggio precedente fuori da players; non verrà copiato né modificato.",
      "profile.pickerTitle", "Zombie Lane - Seleziona profilo",
      "profile.existingName", "Profilo esistente",
      "profile.noProfilesShort", "Nessun profilo locale. Creane uno per iniziare.",
      "error.invalidOptionsTitle", "Zombie Lane: opzioni non valide",
      "error.serverUnavailableTitle", "Zombie Lane: server non disponibile",
      "error.loginTitle", "Accesso non riuscito",
      "error.saveOptions", "Impossibile salvare le opzioni: {0}",
      "error.saveLanguage", "Impossibile salvare la preferenza di lingua: {0}",
      "error.serverGate", "Impossibile connettersi al server {0}:{1} ({2}).\nSenza connessione al server non puoi giocare online.",
      "error.hostRequired", "Scrivi l'host o l'IP del server.",
      "error.hostTooLong", "L'host non può superare {0} caratteri.",
      "error.hostPortField", "Scrivi la porta nel suo campo; IPv6 non è consentito in questo campo.",
      "error.hostPaths", "L'host non può contenere percorsi o separatori; scrivi solo l'IP o il nome.",
      "error.hostSpaces", "L'host non può contenere spazi.",
      "error.userTooLong", "L'utente non può superare {0} caratteri.",
      "error.userChars", "L'utente accetta solo lettere, numeri, spazi e . _ -",
      "error.userRequired", "Scrivi l'utente del tuo account server.",
      "error.userMin", "L'utente deve avere almeno {0} caratteri.",
      "error.passwordLength", "La password deve avere tra {0} e {1} caratteri.",
      "error.zoom", "Lo zoom per scatto rotella '{0}' non è valido: da {1} a {2}",
      "error.level", "Il livello log '{0}' non è valido: SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL o OFF",
      "error.recharge", "L'energia (secondi per punto) '{0}' non è valida: da 1 a {1}",
      "error.spawn", "Il moltiplicatore zombi '{0}' non è valido: da {1} a {2}",
      "error.neighbor", "I minuti dei vicini '{0}' non sono validi: da 1 a {1}",
      "error.port", "La porta '{0}' non è valida: deve essere un intero da {1} a {2}",
      "error.width", "La larghezza della finestra '{0}' non è valida: intero da {1} a {2}",
      "error.height", "L'altezza della finestra '{0}' non è valida: intero da {1} a {2}",
      "error.endpointFormat", "Scrivi un indirizzo HOST:PORT, ad esempio 192.168.1.10:6767",
      "error.endpointFormatGiven", "Scrivi un indirizzo HOST:PORT, ad esempio 192.168.1.10:6767, non '{0}'",
      "error.authInvalid", "Risposta di autenticazione non valida.",
      "probe.checking", "Verifica di {0} ...",
      "probe.ok", "OK: il server risponde su {0}",
      "probe.noResponse", "Nessuna risposta dal server ({0})",
      "language.title", "Lingua",
      "language.prompt", "Scegli la lingua del launcher e del gioco.",
      "language.label", "Lingua:",
      "language.note", "Puoi cambiarla più tardi nelle opzioni del gioco.",
      "alpha.title", "Zombie Lane alpha: rapporti di diagnostica",
      "alpha.question", "Inviare i rapporti di diagnostica al server dell'alpha?",
      "alpha.details", "Cosa viene inviato:\n"
         + "  - Log tecnico della sessione (errori, avvisi e tempi).\n"
         + "  - Azioni di gioco registrate dal gioco (senza password).\n"
         + "  - Versione, sistema operativo, Java, memoria e dati della macchina.\n"
         + "  - Il tuo nome utente alpha e l'identificativo dell'account.\n\n"
         + "Cosa NON viene inviato:\n"
         + "  - Password o partita salvata.\n\n"
         + "Il server dell'alpha conserva i rapporti per 30 giorni. L'organizzatore\n"
         + "può conservare copie di questa alpha più a lungo.\n\n"
         + "Puoi cambiare questa scelta in qualsiasi momento con il pulsante Privacy\n"
         + "del launcher.",
      "alpha.accept", "Accetta e invia",
      "alpha.notNow", "Non ora"
   );

   private static final Map<String, String> GERMAN = map(
      "button.play", "Spielen",
      "button.cancel", "Abbrechen",
      "button.privacy", "Datenschutz...",
      "button.createProfile", "Neues Profil erstellen...",
      "button.probe", "Verbindung testen",
      "button.start", "Starten",
      "button.create", "Erstellen",
      "button.acceptSend", "Akzeptieren und senden",
      "button.notNow", "Jetzt nicht",
      "button.continue", "Weiter",
      "section.connection", "Verbindung",
      "section.profile", "Lokales Profil",
      "section.game", "Spieloptionen",
      "section.advanced", "Erweitert",
      "mode.local", "Lokal (offline)",
      "mode.server", "Server",
      "label.server", "Server:",
      "label.port", "Port:",
      "label.user", "Benutzer:",
      "label.password", "Passwort:",
      "check.register", "Neues Konto erstellen",
      "check.rememberUser", "Benutzer merken",
      "warning.account", "<html>Erstelle ein Konto oder melde dich an. Das Passwort wird auf dem Server als PBKDF2-Hash gespeichert, niemals auf diesem Computer.<br>HTTP verschlüsselt die Verbindung nicht: Nur auf diesem Rechner oder in einem vertrauenswürdigen lokalen Netzwerk verwenden.</html>",
      "check.fullscreen", "Vollbild",
      "check.maximized", "Maximiert",
      "label.windowSize", "Fenstergröße (px):",
      "check.pauseFocus", "Bei Fokusverlust pausieren",
      "check.audio", "Ton",
      "label.zoom", "Zoom pro Mausradschritt (1.01–1.5):",
      "label.recharge", "Energie: Sekunden pro Punkt (1–3600):",
      "label.spawn", "Zombies: Multiplikator (1.0–5.0):",
      "label.neighbor", "Nachbarn: Minuten (1–1440):",
      "check.advanced", "Erweiterte Optionen",
      "label.logLevel", "Log-Level (Diagnose):",
      "check.glDebug", "GL-Debug (prüft glGetError bei jedem Aufruf)",
      "profile.namePrompt", "Profilname:",
      "profile.createTitle", "Neues Profil erstellen",
      "profile.nameRequired", "Gib einen Profilnamen ein.",
      "profile.createFailed", "Profil konnte nicht erstellt werden: {0}",
      "profile.noProfiles", "Keine Profile: Erstelle eins, um zu starten.",
      "profile.localLabel", "Lokales Profil:",
      "profile.legacyWarning", "Eine ältere Speicherung außerhalb von players wurde gefunden; sie wird weder kopiert noch verändert.",
      "profile.pickerTitle", "Zombie Lane - Profil auswählen",
      "profile.existingName", "Bestehendes Profil",
      "profile.noProfilesShort", "Keine lokalen Profile. Erstelle eins, um zu starten.",
      "error.invalidOptionsTitle", "Zombie Lane: ungültige Optionen",
      "error.serverUnavailableTitle", "Zombie Lane: Server nicht verfügbar",
      "error.loginTitle", "Anmeldung fehlgeschlagen",
      "error.saveOptions", "Optionen konnten nicht gespeichert werden: {0}",
      "error.saveLanguage", "Spracheinstellung konnte nicht gespeichert werden: {0}",
      "error.serverGate", "Verbindung zum Server {0}:{1} ({2}) fehlgeschlagen.\nOhne Serververbindung kann nicht online gespielt werden.",
      "error.hostRequired", "Gib den Host oder die IP des Servers ein.",
      "error.hostTooLong", "Der Host darf höchstens {0} Zeichen lang sein.",
      "error.hostPortField", "Gib den Port in seinem Feld ein; IPv6 ist in diesem Feld nicht erlaubt.",
      "error.hostPaths", "Der Host darf keine Pfade oder Trennzeichen enthalten; gib nur die IP oder den Namen ein.",
      "error.hostSpaces", "Der Host darf keine Leerzeichen enthalten.",
      "error.userTooLong", "Der Benutzer darf höchstens {0} Zeichen lang sein.",
      "error.userChars", "Der Benutzer erlaubt nur Buchstaben, Zahlen, Leerzeichen und . _ -",
      "error.userRequired", "Gib den Benutzer deines Serverkontos ein.",
      "error.userMin", "Der Benutzer muss mindestens {0} Zeichen lang sein.",
      "error.passwordLength", "Das Passwort muss zwischen {0} und {1} Zeichen lang sein.",
      "error.zoom", "Der Zoom pro Mausradschritt '{0}' ist ungültig: von {1} bis {2}",
      "error.level", "Der Log-Level '{0}' ist ungültig: SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL oder OFF",
      "error.recharge", "Die Energie (Sekunden pro Punkt) '{0}' ist ungültig: von 1 bis {1}",
      "error.spawn", "Der Zombie-Multiplikator '{0}' ist ungültig: von {1} bis {2}",
      "error.neighbor", "Die Nachbarn-Minuten '{0}' sind ungültig: von 1 bis {1}",
      "error.port", "Der Port '{0}' ist ungültig: muss eine Ganzzahl von {1} bis {2} sein",
      "error.width", "Die Fensterbreite '{0}' ist ungültig: Ganzzahl von {1} bis {2}",
      "error.height", "Die Fensterhöhe '{0}' ist ungültig: Ganzzahl von {1} bis {2}",
      "error.endpointFormat", "Gib eine HOST:PORT-Adresse ein, zum Beispiel 192.168.1.10:6767",
      "error.endpointFormatGiven", "Gib eine HOST:PORT-Adresse ein, zum Beispiel 192.168.1.10:6767, nicht '{0}'",
      "error.authInvalid", "Ungültige Authentifizierungsantwort.",
      "probe.checking", "Prüfe {0} ...",
      "probe.ok", "OK: Server antwortet unter {0}",
      "probe.noResponse", "Keine Antwort vom Server ({0})",
      "language.title", "Sprache",
      "language.prompt", "Wähle die Sprache für Launcher und Spiel.",
      "language.label", "Sprache:",
      "language.note", "Du kannst sie später in den Spieloptionen ändern.",
      "alpha.title", "Zombie Lane Alpha: Diagnoseberichte",
      "alpha.question", "Diagnoseberichte an den Alpha-Server senden?",
      "alpha.details", "Was gesendet wird:\n"
         + "  - Technisches Sitzungsprotokoll (Fehler, Warnungen und Zeiten).\n"
         + "  - Vom Spiel aufgezeichnete Aktionen (ohne Passwörter).\n"
         + "  - Version, Betriebssystem, Java, Speicher und Gerätedaten.\n"
         + "  - Dein Alpha-Benutzername und die Konto-ID.\n\n"
         + "Was NICHT gesendet wird:\n"
         + "  - Passwörter oder der Spielstand.\n\n"
         + "Der Alpha-Server speichert Berichte 30 Tage. Der Organisator kann\n"
         + "Kopien dieser Alpha länger aufbewahren.\n\n"
         + "Du kannst diese Auswahl jederzeit über die Schaltfläche Datenschutz\n"
         + "im Launcher ändern.",
      "alpha.accept", "Akzeptieren und senden",
      "alpha.notNow", "Jetzt nicht"
   );

   private LauncherText() {
   }

   public static void setLanguage(String code) {
      currentLanguage = LauncherLanguage.normalize(code).orElse(DEFAULT_LANGUAGE);
   }

   public static String currentCode() {
      return currentLanguage;
   }

   public static String get(String key) {
      String value = table(currentLanguage).get(key);
      if (value == null) {
         value = ENGLISH.get(key);
      }
      return value == null ? key : value;
   }

   public static String format(String key, Object... arguments) {
      String text = get(key);
      for (int i = 0; i < arguments.length; i++) {
         text = text.replace("{" + i + "}", String.valueOf(arguments[i]));
      }
      return text;
   }

   private static Map<String, String> table(String code) {
      return switch (code) {
         case "es" -> SPANISH;
         case "fr" -> FRENCH;
         case "it" -> ITALIAN;
         case "de" -> GERMAN;
         default -> ENGLISH;
      };
   }

   private static Map<String, String> map(String... pairs) {
      if (pairs.length % 2 != 0) {
         throw new IllegalArgumentException("LauncherText entries must be key/value pairs");
      }
      Map<String, String> values = new HashMap<>();
      for (int i = 0; i < pairs.length; i += 2) {
         values.put(pairs[i], pairs[i + 1]);
      }
      return Map.copyOf(values);
   }
}
