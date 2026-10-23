package zlport.launcher;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Properties;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Consentimiento del informe de diagnóstico de la alpha. Se pregunta una sola
 * vez y queda guardado en {@code alpha.properties}; el jugador puede cambiarlo
 * desde el botón "Privacidad" del lanzador. Si acepta, al terminar cada sesión
 * se envía un zip técnico al servidor; si no, no se envía nada.
 */
final class AlphaConsent {
   static final String FILE_NAME = "alpha.properties";
   static final String PROPERTY = "zlport.alpha.consent";
   private static final String ASKED = "preguntado";
   private static final String GRANTED = "consentimiento";
   private static final String ASKED_AT = "preguntado_en";

   private AlphaConsent() {
   }

   static boolean enabled(Path dataRoot) {
      return load(dataRoot).granted;
   }

   /**
    * Devuelve el consentimiento guardado. Si todavía no se preguntó y hay
    * interfaz gráfica, muestra el diálogo una vez. En lanzamientos sin
    * lanzador (scripts, servidor dedicado) nunca abre ventanas.
    */
   static boolean askIfNeeded(Path dataRoot, boolean graphical) {
      String override = System.getProperty(PROPERTY);
      if (override != null) {
         boolean granted = Boolean.parseBoolean(override);
         save(dataRoot, granted);
         return granted;
      }
      State state = load(dataRoot);
      if (state.asked) {
         return state.granted;
      }
      if (!graphical || GraphicsEnvironment.isHeadless()) {
         return false;
      }
      return showDialog(dataRoot);
   }

   /** Abre el diálogo de privacidad; guarda siempre la respuesta. */
   static boolean showDialog(Path dataRoot) {
      JTextArea details = new JTextArea(LauncherText.get("alpha.details"));
      details.setEditable(false);
      details.setLineWrap(true);
      details.setWrapStyleWord(true);
      details.setOpaque(false);
      details.setBorder(null);
      details.setRows(17);
      details.setColumns(54);
      details.setCaretPosition(0);

      JPanel panel = new JPanel();
      panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
      panel.add(new JLabel(LauncherText.get("alpha.question")));
      panel.add(javax.swing.Box.createVerticalStrut(8));
      panel.add(new JScrollPane(details));

      Object[] options = { LauncherText.get("alpha.accept"), LauncherText.get("alpha.notNow") };
      int choice = JOptionPane.showOptionDialog(
         null,
         panel,
         LauncherText.get("alpha.title"),
         JOptionPane.YES_NO_OPTION,
         JOptionPane.QUESTION_MESSAGE,
         null,
         options,
         options[0]
      );
      boolean granted = choice == 0;
      save(dataRoot, granted);
      return granted;
   }

   private static State load(Path dataRoot) {
      Path file = dataRoot.resolve(FILE_NAME);
      if (!Files.isRegularFile(file)) {
         return new State(false, false);
      }
      Properties properties = new Properties();
      try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
         properties.load(reader);
      } catch (IOException | IllegalArgumentException exception) {
         return new State(false, false);
      }
      return new State(
         Boolean.parseBoolean(properties.getProperty(ASKED, "false")),
         Boolean.parseBoolean(properties.getProperty(GRANTED, "false"))
      );
   }

   private static void save(Path dataRoot, boolean granted) {
      Path file = dataRoot.resolve(FILE_NAME);
      String text = "# Informe de diagnóstico de la alpha de Zombie Lane.\n"
         + "# consentimiento=true: se envía un informe técnico al servidor al terminar cada sesión.\n"
         + "# consentimiento=false: no se envía nada; los registros se quedan solo en este equipo.\n"
         + ASKED + "=true\n"
         + GRANTED + "=" + granted + "\n"
         + ASKED_AT + "=" + Instant.now() + "\n";
      try {
         Files.createDirectories(dataRoot);
         Files.writeString(file, text, StandardCharsets.UTF_8);
      } catch (IOException exception) {
         System.getLogger("zlport.launcher").log(
            System.Logger.Level.WARNING,
            "No se pudo guardar el consentimiento del informe en " + file,
            exception
         );
      }
   }

   private record State(boolean asked, boolean granted) {
   }
}
