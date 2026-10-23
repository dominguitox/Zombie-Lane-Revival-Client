package zlport.launcher;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.util.Optional;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** First-run language prompt: the launcher and the game share the chosen code. */
final class LanguageDialog {
   private LanguageDialog() {
   }

   static Optional<String> show(String initialCode) {
      LauncherLanguage.Language initial = LauncherLanguage.find(initialCode)
         .orElseGet(LauncherLanguage::systemDefault);
      if (GraphicsEnvironment.isHeadless()) {
         return Optional.of(initial.code());
      }
      LauncherText.setLanguage(initial.code());
      try {
         JComboBox<LauncherLanguage.Language> selector =
            new JComboBox<>(LauncherLanguage.all().toArray(LauncherLanguage.Language[]::new));
         selector.setSelectedItem(initial);

         JPanel panel = new JPanel();
         panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
         panel.add(new JLabel(LauncherText.get("language.prompt")));
         panel.add(javax.swing.Box.createVerticalStrut(8));
         panel.add(new JLabel(LauncherText.get("language.label")));
         panel.add(selector);
         panel.add(javax.swing.Box.createVerticalStrut(8));
         JLabel note = new JLabel(LauncherText.get("language.note"));
         note.setFont(note.getFont().deriveFont(java.awt.Font.PLAIN, 11.0F));
         panel.add(note);

         int choice = JOptionPane.showConfirmDialog(
            null,
            panel,
            LauncherText.get("language.title"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
         );
         if (choice != JOptionPane.OK_OPTION) {
            return Optional.empty();
         }
         Object selected = selector.getSelectedItem();
         LauncherLanguage.Language language =
            selected instanceof LauncherLanguage.Language chosen ? chosen : initial;
         return Optional.of(language.code());
      } catch (HeadlessException exception) {
         return Optional.of(initial.code());
      }
   }
}
