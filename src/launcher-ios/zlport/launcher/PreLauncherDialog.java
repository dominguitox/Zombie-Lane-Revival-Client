package zlport.launcher;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Pre-game graphical launcher: one screen with the mode (local / server), the
 * local-only profile picker, connection credentials and game options plus a
 * collapsible advanced section. It does not start the game: it returns a
 * {@link Selection} for {@code IosMain} to configure the launch. The logic is
 * separated from Swing (see {@link Fields}, {@link #validateFields} and
 * {@link #optionValues}) so it can be tested without a window.
 *
 * <p>Server-mode credentials are sent to the selected server for registration
 * or login; the password is never stored locally.</p>
 */
public final class PreLauncherDialog {
   private static final Logger LOG = System.getLogger("zlport.launcher.selector");
   private static final int DEFAULT_WINDOW_WIDTH = 1280;
   private static final int DEFAULT_WINDOW_HEIGHT = 720;
   private static final int MIN_WINDOW_WIDTH = 640;
   private static final int MIN_WINDOW_HEIGHT = 360;
   private static final int MAX_WINDOW_DIMENSION = 16384;
   private static final int MAX_PORT_FIELD_LENGTH = 5;
    private static final int MAX_WINDOW_FIELD_LENGTH = 5;
    private static final int MAX_SPAWN_FIELD_LENGTH = 4;
    private static final int MAX_PASSWORD_LENGTH = 128;

   /** Dialog outcome: chosen profile (null when the picker is not in use) and confirmed connection. */
   public record Selection(
      Path profileDir,
      LocalProfiles.ProfileInfo profile,
      PreLauncherState.Mode mode,
      ServerConnection server,
      String user,
      String accountId,
      boolean rememberUser,
      String devMissions
   ) {
   }

    /** Raw field values, without Swing, so validation can run outside the dialog. */
    record Fields(
       boolean serverMode,
       String host,
       String port,
       String user,
       String password,
       String width,
       String height,
       boolean maximized,
       boolean fullscreen,
       boolean pauseFocus,
       boolean audio,
       boolean glDebug,
       String level,
       String spawn,
       String energyRecharge,
       boolean cashCheat
    ) {
       static final Fields EMPTY =
          new Fields(false, "", "", "", "", "", "", false, false, true, true, false, "INFO", "", "", false);
    }

   private final Path dataRoot;
   private final PreLauncherState seed;
   private final boolean pickProfile;
   private final JDialog dialog =
      new JDialog((java.awt.Frame)null, "Zombie Lane 1.4.4", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
   private Optional<Selection> result = Optional.empty();

   private JComboBox<LocalProfiles.ProfileInfo> profileBox;
   private javax.swing.border.TitledBorder profileTitle;
   private javax.swing.JComponent profilePanel;
   private String profileTitleText;
   private JRadioButton localMode;
   private JRadioButton serverMode;
   private JPanel serverPanel;
   /** Minimum readable identity length for the server account user name. */
   static final int MIN_USER_LENGTH = 3;
   /** Server password minimum; mirrors the authentication endpoint. */
   static final int MIN_PASSWORD_LENGTH = 4;
   private JTextField hostField;
   private JTextField portField;
   private JTextField userField;
   private JPasswordField passwordField;
   private JCheckBox registerAccount;
   private JCheckBox rememberUser;
   private JLabel probeResult;
   private JTextField widthField;
   private JTextField heightField;
   private JCheckBox maximizedBox;
   private JCheckBox fullscreenBox;
    private JCheckBox pauseFocusBox;
    private JCheckBox audioBox;
    private JCheckBox glDebugBox;
    private JComboBox<String> levelBox;
    private JTextField spawnField;
    private JCheckBox infiniteEnergyBox;
    private JCheckBox cashCheatBox;
    private Settings currentSettings = Settings.defaults();

   private PreLauncherDialog(Path dataRoot, PreLauncherState seed, boolean pickProfile) {
      this.dataRoot = dataRoot;
      this.seed = seed;
      this.pickProfile = pickProfile;
   }

   /**
    * Opens the dialog and blocks until Play or Cancel. Returns empty when the
    * user cancels or the environment has no display.
    */
   public static Optional<Selection> show(
      Path dataRoot, PreLauncherState seed, boolean pickProfile, List<LocalProfiles.ProfileInfo> profiles
   ) throws IOException {
      try {
         PreLauncherDialog dialog = new PreLauncherDialog(dataRoot, seed, pickProfile);
         return dialog.open(profiles);
      } catch (HeadlessException exception) {
         LOG.log(Level.WARNING, "sin pantalla para el lanzador gráfico; el juego sigue sin diálogo", exception);
         return Optional.empty();
      }
   }

   private Optional<Selection> open(List<LocalProfiles.ProfileInfo> profiles) throws IOException {
      JPanel content = new JPanel(new GridBagLayout());
      content.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
      int row = 0;

      row = this.buildConnectionSection(content, row);
      if (this.pickProfile) {
         row = this.buildProfileSection(content, row, profiles);
      }
      row = this.buildGameSection(content, row);
      row = this.buildAdvancedSection(content, row);
      this.reapplyOptions();
      this.updateProfileVisibility();

      JPanel buttons = new JPanel();
      JButton play = new JButton(LauncherText.get("button.play"));
      play.addActionListener(action -> this.play());
      JButton cancel = new JButton(LauncherText.get("button.cancel"));
      cancel.addActionListener(action -> this.cancel());
      JButton privacy = new JButton(LauncherText.get("button.privacy"));
      privacy.addActionListener(action -> AlphaConsent.showDialog(this.dataRoot));
      this.dialog.getRootPane().setDefaultButton(play);
      buttons.add(play);
      buttons.add(cancel);
      buttons.add(privacy);
      content.add(buttons, sectionConstraints(row));

      this.dialog.setContentPane(content);
      this.dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      this.dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
         .put(KeyStroke.getKeyStroke("ESCAPE"), "cancelar");
      this.dialog.getRootPane().getActionMap().put(
         "cancelar",
         new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
               PreLauncherDialog.this.cancel();
            }
         }
      );
      this.dialog.pack();
      this.dialog.setMinimumSize(this.dialog.getSize());
      this.dialog.setLocationRelativeTo(null);
      this.dialog.setVisible(true);
      return this.result;
   }

   private void cancel() {
      this.result = Optional.empty();
      this.dialog.dispose();
   }

   private int buildProfileSection(JPanel content, int row, List<LocalProfiles.ProfileInfo> profiles) {
      JPanel section = new JPanel(new GridBagLayout());
      this.profileTitleText = LauncherText.get("section.profile");
      javax.swing.border.TitledBorder titled =
         BorderFactory.createTitledBorder(this.profileTitleText);
      section.setBorder(titled);
      this.profileTitle = titled;
      this.profilePanel = section;

      GridBagConstraints c = newSectionConstraints();
      c.weightx = 1.0;
      c.fill = GridBagConstraints.HORIZONTAL;
      this.profileBox = new JComboBox<>(profiles.toArray(LocalProfiles.ProfileInfo[]::new));
      if (!profiles.isEmpty()) {
         this.profileBox.setSelectedIndex(0);
      }
      String last = this.seed.lastProfile();
      if (!last.isEmpty()) {
         for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).id().equals(last)) {
               this.profileBox.setSelectedIndex(i);
            }
         }
      }
      section.add(this.profileBox, c);
      this.profileBox.addItemListener(event -> {
         if (event.getStateChange() == ItemEvent.SELECTED) {
            this.reapplyOptions();
         }
      });
      this.profilePanel.setName("localProfiles");

      c = newSectionConstraints();
      c.gridx = 1;
      JButton create = new JButton(LauncherText.get("button.createProfile"));
      create.addActionListener(action -> this.createProfile());
      section.add(create, c);

      content.add(section, sectionConstraints(row++));
      return row;
   }

   private int buildConnectionSection(JPanel content, int row) {
      JPanel section = new JPanel(new GridBagLayout());
      section.setBorder(BorderFactory.createTitledBorder(LauncherText.get("section.connection")));

      GridBagConstraints c = newSectionConstraints();
      this.localMode = new JRadioButton(LauncherText.get("mode.local"), this.seed.mode() == PreLauncherState.Mode.LOCAL);
      this.serverMode = new JRadioButton(LauncherText.get("mode.server"), this.seed.mode() == PreLauncherState.Mode.SERVER);
      javax.swing.ButtonGroup modes = new javax.swing.ButtonGroup();
      modes.add(this.localMode);
      modes.add(this.serverMode);
      java.awt.event.ActionListener switchMode = action -> this.updateServerEnabled();
      this.localMode.addActionListener(switchMode);
      this.serverMode.addActionListener(switchMode);
      section.add(this.localMode, c);
      c = nextCell(c);
      section.add(this.serverMode, c);
      this.localMode.addActionListener(action -> this.updateProfileVisibility());
      this.serverMode.addActionListener(action -> this.updateProfileVisibility());

      c = newSectionConstraints();
      c.gridy = 1;
      c.gridwidth = GridBagConstraints.REMAINDER;
      c.weightx = 1.0;
      c.fill = GridBagConstraints.HORIZONTAL;
      this.serverPanel = this.buildServerPanel();
      section.add(this.serverPanel, c);

      content.add(section, sectionConstraints(row++));
      this.updateServerEnabled();
      return row;
   }

   private JPanel buildServerPanel() {
      // Label/field pairs on their own rows: one row per pair, so every
      // control is visible and editable even in a narrow window.
      JPanel panel = new JPanel(new GridBagLayout());
      GridBagConstraints c = newSectionConstraints();

      panel.add(new JLabel(LauncherText.get("label.server")), c);
      c = this.fieldCell(c);
      this.hostField = new JTextField(this.seed.host());
      this.limitLength(this.hostField, ServerConnection.MAX_HOST_LENGTH);
      panel.add(this.hostField, c);

      c = this.nextRow(c);
      panel.add(new JLabel(LauncherText.get("label.port")), c);
      c = this.fieldCell(c);
      this.portField = new JTextField(String.valueOf(this.seed.port()));
      this.integerField(this.portField, MAX_PORT_FIELD_LENGTH);
      panel.add(this.portField, c);

      c = this.nextRow(c);
      panel.add(new JLabel(LauncherText.get("label.user")), c);
      c = this.fieldCell(c);
      this.userField = new JTextField(this.seed.rememberUser() ? this.seed.user() : "");
      this.userField.setName("serverUsername");
      this.limitLength(this.userField, ServerConnection.MAX_USER_LENGTH);
      panel.add(this.userField, c);

      c = this.nextRow(c);
      panel.add(new JLabel(LauncherText.get("label.password")), c);
      c = this.fieldCell(c);
      this.passwordField = new JPasswordField();
      this.passwordField.setName("serverPassword");
      this.limitLength(this.passwordField, MAX_PASSWORD_LENGTH);
      panel.add(this.passwordField, c);
      c = nextCell(c);
      this.registerAccount = new JCheckBox(LauncherText.get("check.register"));
      panel.add(this.registerAccount, c);

      c = this.nextRow(c);
      this.rememberUser = new JCheckBox(LauncherText.get("check.rememberUser"));
      this.rememberUser.setSelected(this.seed.rememberUser());
      panel.add(this.rememberUser, c);

      c = this.nextRow(c);
      JButton probeButton = new JButton(LauncherText.get("button.probe"));
      probeButton.addActionListener(action -> this.probeConnection());
      panel.add(probeButton, c);
      c = nextCell(c);
      c.weightx = 1.0;
      c.fill = GridBagConstraints.HORIZONTAL;
      this.probeResult = new JLabel(" ");
      panel.add(this.probeResult, c);

      c = this.nextRow(c);
      c.gridwidth = GridBagConstraints.REMAINDER;
      c.weightx = 1.0;
      c.fill = GridBagConstraints.HORIZONTAL;
      JLabel warning = new JLabel(LauncherText.get("warning.account"));
      warning.setFont(warning.getFont().deriveFont(Font.PLAIN, 11.0F));
      panel.add(warning, c);
      return panel;
   }

   /** Field cell: the column after a label, stretching with the window. */
   private GridBagConstraints fieldCell(GridBagConstraints label) {
      GridBagConstraints c = nextCell(label);
      c.weightx = 1.0;
      c.fill = GridBagConstraints.HORIZONTAL;
      return c;
   }

   /** Label cell at the start of the next row. */
   private GridBagConstraints nextRow(GridBagConstraints previous) {
      GridBagConstraints c = newSectionConstraints();
      c.gridy = previous.gridy + 1;
      return c;
   }

   private int buildGameSection(JPanel content, int row) {
      JPanel section = new JPanel(new GridBagLayout());
      section.setBorder(BorderFactory.createTitledBorder(LauncherText.get("section.game")));

      GridBagConstraints c = newSectionConstraints();
      this.fullscreenBox = new JCheckBox(LauncherText.get("check.fullscreen"));
      section.add(this.fullscreenBox, c);
      this.maximizedBox = new JCheckBox(LauncherText.get("check.maximized"));
      section.add(this.maximizedBox, nextCell(c));

      c = this.nextRow(c);
      section.add(new JLabel(LauncherText.get("label.windowSize")), c);
      c = this.fieldCell(c);
      this.widthField = new JTextField(4);
      this.heightField = new JTextField(4);
      this.integerField(this.widthField, MAX_WINDOW_FIELD_LENGTH);
      this.integerField(this.heightField, MAX_WINDOW_FIELD_LENGTH);
      javax.swing.JPanel size = new javax.swing.JPanel();
      size.add(this.widthField);
      size.add(new JLabel("x"));
      size.add(this.heightField);
      section.add(size, c);

      c = this.nextRow(c);
      this.pauseFocusBox = new JCheckBox(LauncherText.get("check.pauseFocus"));
      section.add(this.pauseFocusBox, c);
      this.audioBox = new JCheckBox(LauncherText.get("check.audio"));
      section.add(this.audioBox, nextCell(c));

      c = this.nextRow(c);
      section.add(new JLabel(LauncherText.get("label.spawn")), c);
      c = this.fieldCell(c);
      this.spawnField = new JTextField(4);
      this.decimalField(this.spawnField, MAX_SPAWN_FIELD_LENGTH);
      section.add(this.spawnField, c);

      c = this.nextRow(c);
      this.infiniteEnergyBox = new JCheckBox(LauncherText.get("check.energyInfinite"));
      this.cashCheatBox = new JCheckBox(LauncherText.get("check.cashCheat"));
      section.add(this.infiniteEnergyBox, c);
      section.add(this.cashCheatBox, nextCell(c));

      content.add(section, sectionConstraints(row++));
      return row;
   }

   private int buildAdvancedSection(JPanel content, int row) {
      JCheckBox advancedToggle = new JCheckBox(LauncherText.get("check.advanced"));
      JPanel advanced = new JPanel(new GridBagLayout());
      advanced.setBorder(BorderFactory.createTitledBorder(LauncherText.get("section.advanced")));
      advanced.setVisible(false);
      advancedToggle.addItemListener(event -> {
         advanced.setVisible(event.getStateChange() == ItemEvent.SELECTED);
         this.dialog.pack();
      });

      GridBagConstraints c = newSectionConstraints();
      advanced.add(new JLabel(LauncherText.get("label.logLevel")), c);
      c = this.fieldCell(c);
      this.levelBox = new JComboBox<>(new String[] { "SEVERE", "WARNING", "INFO", "CONFIG", "FINE", "FINER", "FINEST", "ALL", "OFF" });
      advanced.add(this.levelBox, c);

      c = this.nextRow(c);
      this.glDebugBox = new JCheckBox(LauncherText.get("check.glDebug"));
      advanced.add(this.glDebugBox, c);

      // Dev editions only: the src-release body adds nothing, so the player
      // binary has no dev missions field at all.
      c = newSectionConstraints();
      c.gridy = 1;
      zlport.dev.LauncherDevOptions.addSharedMissionsField(advanced, c, this.seed.devMissions());

      content.add(advancedToggle, sectionConstraints(row++));
      content.add(advanced, sectionConstraints(row++));
      return row;
   }

   /** Applies the selected profile's options, or the defaults when no profile exists. */
   private void reapplyOptions() {
      Settings settings = Settings.defaults();
      if (this.profileBox != null) {
         Object item = this.profileBox.getSelectedItem();
         if (item instanceof LocalProfiles.ProfileInfo profile) {
            try {
               settings = Settings.load(profile.directory());
            } catch (RuntimeException exception) {
               LOG.log(Level.WARNING, "opciones del perfil por defecto (no se pudieron leer)", exception);
            }
         }
      }
      this.currentSettings = settings;
      this.applySettings(settings);
   }

    private void applySettings(Settings settings) {
       this.widthField.setText(String.valueOf(settings.width() != null ? settings.width() : DEFAULT_WINDOW_WIDTH));
       this.heightField.setText(String.valueOf(settings.height() != null ? settings.height() : DEFAULT_WINDOW_HEIGHT));
       this.maximizedBox.setSelected(settings.maximized());
       this.fullscreenBox.setSelected(settings.fullscreen());
       this.pauseFocusBox.setSelected(settings.pauseOnFocusLoss());
       this.audioBox.setSelected(settings.audio());
       this.glDebugBox.setSelected(settings.glDebug());
       this.levelBox.setSelectedItem(settings.logLevel());
       this.spawnField.setText(String.valueOf(settings.spawnMultiplier()));
       this.infiniteEnergyBox.setSelected(settings.energyRechargeSeconds() == 1);
       this.cashCheatBox.setSelected(settings.cashCheat());
    }

   private void createProfile() {
      String name = JOptionPane.showInputDialog(
         this.dialog, LauncherText.get("profile.namePrompt"), LauncherText.get("profile.createTitle"), JOptionPane.PLAIN_MESSAGE
      );
      if (name == null) {
         return;
      }
      if (name.strip().isEmpty()) {
         JOptionPane.showMessageDialog(
            this.dialog, LauncherText.get("profile.nameRequired"), "Zombie Lane", JOptionPane.INFORMATION_MESSAGE
         );
         return;
      }
      try {
         Optional<LocalProfiles.ProfileInfo> created = LocalProfiles.createNew(this.dataRoot, name);
         if (created.isEmpty()) {
            return;
         }
         LocalProfiles.ProfileInfo createdProfile = created.orElseThrow();
         List<LocalProfiles.ProfileInfo> profiles = LocalProfiles.profiles(this.dataRoot);
         this.profileBox.removeAllItems();
         for (LocalProfiles.ProfileInfo profile : profiles) {
            this.profileBox.addItem(profile);
         }
         boolean found = false;
         for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).id().equals(createdProfile.id())) {
               this.profileBox.setSelectedIndex(i);
               found = true;
            }
         }
         if (!found) {
            this.reapplyOptions();
         }
      } catch (IOException | RuntimeException exception) {
         JOptionPane.showMessageDialog(
            this.dialog, LauncherText.format("profile.createFailed", exception.getMessage()), "Zombie Lane", JOptionPane.ERROR_MESSAGE
         );
      }
   }

   private void updateServerEnabled() {
      boolean enabled = this.serverMode != null && this.serverMode.isSelected();
      this.setServerEnabled(enabled);
      this.updateProfileVisibility();
   }

   private void updateProfileVisibility() {
      if (this.profilePanel == null || this.localMode == null) {
         return;
      }
      boolean visible = this.pickProfile && this.localMode.isSelected();
      this.profilePanel.setVisible(visible);
      if (this.profileTitle != null) {
         this.profileTitle.setTitle(this.profileTitleText);
      }
      java.awt.Container parent = this.profilePanel.getParent();
      if (parent != null) {
         parent.revalidate();
         parent.repaint();
      }
   }

    private void setServerEnabled(boolean enabled) {
       if (this.serverPanel == null) {
          return;
       }
       this.hostField.setEnabled(enabled);
       this.portField.setEnabled(enabled);
       this.userField.setEnabled(enabled);
       this.passwordField.setEnabled(enabled);
       this.rememberUser.setEnabled(enabled);
       // The cheats are offline-only: disabled while the server mode is picked.
       this.setCheatBoxesEnabled(!enabled);
    }

    private void setCheatBoxesEnabled(boolean local) {
       if (this.infiniteEnergyBox != null) {
          this.infiniteEnergyBox.setEnabled(local);
       }
       if (this.cashCheatBox != null) {
          this.cashCheatBox.setEnabled(local);
       }
    }

   private void limitLength(JTextField field, int maxLength) {
      AbstractDocument document = (AbstractDocument) field.getDocument();
      document.setDocumentFilter(new FieldFilter(maxLength, null));
   }

   private void integerField(JTextField field, int maxLength) {
      AbstractDocument document = (AbstractDocument) field.getDocument();
      document.setDocumentFilter(new FieldFilter(maxLength, "\\d*"));
   }

   private void decimalField(JTextField field, int maxLength) {
      AbstractDocument document = (AbstractDocument) field.getDocument();
      document.setDocumentFilter(new FieldFilter(maxLength, "\\d*(?:[.,]\\d*)?"));
   }

   private static final class FieldFilter extends DocumentFilter {
      private final int maxLength;
      private final String pattern;

      private FieldFilter(int maxLength, String pattern) {
         this.maxLength = maxLength;
         this.pattern = pattern;
      }

      @Override
      public void insertString(FilterBypass filter, int offset, String text, AttributeSet attributes) throws BadLocationException {
         this.replace(filter, offset, 0, text, attributes);
      }

      @Override
      public void replace(FilterBypass filter, int offset, int length, String text, AttributeSet attributes) throws BadLocationException {
         String current = filter.getDocument().getText(0, filter.getDocument().getLength());
         String insertion = text == null ? "" : text;
         String candidate = current.substring(0, offset) + insertion + current.substring(offset + length);
         if (candidate.length() <= this.maxLength && (this.pattern == null || candidate.matches(this.pattern))) {
            super.replace(filter, offset, length, text, attributes);
         }
      }
   }

    private Fields collectFields() {
       return new Fields(
          this.serverMode.isSelected(),
          this.hostField.getText(),
          this.portField.getText(),
          this.userField.getText(),
          new String(this.passwordField.getPassword()).replace("\0", " "),
          this.widthField.getText(),
          this.heightField.getText(),
          this.maximizedBox.isSelected(),
          this.fullscreenBox.isSelected(),
          this.pauseFocusBox.isSelected(),
          this.audioBox.isSelected(),
          this.glDebugBox.isSelected(),
          String.valueOf(this.levelBox.getSelectedItem()),
          this.spawnField.getText(),
          this.energyRechargeValue(),
          this.cashCheatBox.isSelected()
       );
    }

    /**
     * The energia.recarga value the launcher persists: the infinite-energy
     * checkbox becomes 1; otherwise the profile keeps its current seconds,
     * except a leftover 1 (from a previous checked box), which goes back to
     * the default.
     */
    private String energyRechargeValue() {
       if (this.infiniteEnergyBox.isSelected()) {
          return "1";
       }
       int seconds = this.currentSettings.energyRechargeSeconds();
       return String.valueOf(seconds == 1 ? Settings.ENERGY_RECHARGE_DEFAULT : seconds);
    }

    /** Keys of zombie-lane.properties the launcher writes, with the field values. */
    static Map<String, String> optionValues(Fields f) {
       java.util.Map<String, String> values = new java.util.LinkedHashMap<>();
       values.put(Settings.WIDTH, f.width().strip());
       values.put(Settings.HEIGHT, f.height().strip());
       values.put(Settings.MAXIMIZED, String.valueOf(f.maximized()));
       values.put(Settings.FULLSCREEN, String.valueOf(f.fullscreen()));
       values.put(Settings.PAUSE_ON_FOCUS_LOSS, String.valueOf(f.pauseFocus()));
       values.put(Settings.AUDIO, String.valueOf(f.audio()));
       values.put(Settings.GL_DEBUG, String.valueOf(f.glDebug()));
       values.put(Settings.LOG_LEVEL, f.level().strip().toUpperCase(Locale.ROOT));
       values.put(Settings.SPAWN_MULTIPLIER, f.spawn().strip().replace(',', '.'));
       // Offline helpers only: server sessions leave energia.recarga and the
       // cash cheat untouched (roda.passo and vizinho.intervalo stay only in
       // the file, for manual tuning).
       if (!f.serverMode()) {
          values.put(Settings.ENERGY_RECHARGE, f.energyRecharge().strip());
          values.put(Settings.CASH_CHEAT, String.valueOf(f.cashCheat()));
       }
       return values;
    }

   /**
    * Validates the fields without Swing or network. Returns the problem list;
    * empty means everything is fine. Network validation is separate: only the
    * "Probar conexión" button talks for the first time to the typed server.
    */
   static List<String> validateFields(Fields f) {
      List<String> errors = new ArrayList<>();
      if (f.serverMode()) {
         String hostError = ServerConnection.validateHost(f.host());
         if (hostError != null) {
            errors.add(hostError);
         }
         String portError = portError(f.port());
         if (portError != null) {
            errors.add(portError);
         }
         if (hostError == null && portError == null) {
            try {
               ServerConnection.of(f.host(), f.port());
            } catch (IllegalArgumentException exception) {
               errors.add(exception.getMessage());
            }
         }
         // In server mode the typed user IS the account identity: required,
         // with a readable minimum length.
          if (f.user().strip().isEmpty()) {
             errors.add(LauncherText.get("error.userRequired"));
          }
          String userError = ServerConnection.validateUser(f.user());
          if (userError != null) {
             errors.add(userError);
          }
          if (userError == null && !f.user().strip().isEmpty() && f.user().strip().length() < MIN_USER_LENGTH) {
             errors.add(LauncherText.format("error.userMin", MIN_USER_LENGTH));
          }
          if (f.password().length() < MIN_PASSWORD_LENGTH || f.password().length() > MAX_PASSWORD_LENGTH) {
             errors.add(LauncherText.format("error.passwordLength", MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
          }
      }

      Integer width = dimension(f.width(), MIN_WINDOW_WIDTH);
      Integer height = dimension(f.height(), MIN_WINDOW_HEIGHT);
      if (width == null) {
         errors.add(LauncherText.format("error.width", f.width(), MIN_WINDOW_WIDTH, MAX_WINDOW_DIMENSION));
      }
      if (height == null) {
         errors.add(LauncherText.format("error.height", f.height(), MIN_WINDOW_HEIGHT, MAX_WINDOW_DIMENSION));
      }

      String levelError = levelError(f.level());
      if (levelError != null) {
         errors.add(levelError);
      }
      String spawnError = spawnError(f.spawn());
      if (spawnError != null) {
         errors.add(spawnError);
      }

      return errors;
    }

    private static Integer boundedInteger(String text, int min, int max) {
       String value = text == null ? "" : text.strip();
       if (!value.matches("\\d{1,10}")) {
          return null;
       }
       try {
          int parsed = Integer.parseInt(value);
          return parsed >= min && parsed <= max ? parsed : null;
       } catch (NumberFormatException ignored) {
          return null;
       }
    }

    private static String levelError(String text) {
      String value = text == null ? "" : text.strip().toUpperCase(Locale.ROOT);
      try {
         java.util.logging.Level.parse(value);
         return null;
      } catch (IllegalArgumentException ignored) {
         return LauncherText.format("error.level", text);
      }
   }

    private static String spawnError(String text) {
      String error = LauncherText.format(
         "error.spawn", text, Settings.MIN_SPAWN_MULTIPLIER, Settings.MAX_SPAWN_MULTIPLIER
      );
      String value = text == null ? "" : text.strip().replace(',', '.');
      try {
         float multiplier = Float.parseFloat(value);
         if (!Float.isNaN(multiplier) && !Float.isInfinite(multiplier) && multiplier >= Settings.MIN_SPAWN_MULTIPLIER && multiplier <= Settings.MAX_SPAWN_MULTIPLIER) {
            return null;
         }
      } catch (NumberFormatException ignored) {
      }
      return error;
   }

    private static String portError(String portText) {
      String error = LauncherText.format("error.port", portText, ServerConnection.MIN_PORT, ServerConnection.MAX_PORT);
      return boundedInteger(portText, ServerConnection.MIN_PORT, ServerConnection.MAX_PORT) == null ? error : null;
   }

   private static Integer dimension(String text, int min) {
      return boundedInteger(text, min, MAX_WINDOW_DIMENSION);
   }

   private void play() {
      Fields f = this.collectFields();
      List<String> errors = validateFields(f);
      if (!errors.isEmpty()) {
         JOptionPane.showMessageDialog(
            this.dialog, String.join("\n", errors), LauncherText.get("error.invalidOptionsTitle"), JOptionPane.ERROR_MESSAGE
         );
         return;
      }

      PreLauncherState.Mode mode = this.serverMode.isSelected() ? PreLauncherState.Mode.SERVER : PreLauncherState.Mode.LOCAL;

      LocalProfiles.ProfileInfo profile = null;
      if (this.pickProfile && mode == PreLauncherState.Mode.LOCAL) {
         Object item = this.profileBox.getSelectedItem();
          if (!(item instanceof LocalProfiles.ProfileInfo chosen)) {
             JOptionPane.showMessageDialog(
                this.dialog, LauncherText.get("profile.noProfiles"), "Zombie Lane", JOptionPane.WARNING_MESSAGE
             );
             return;
          }
         profile = chosen;
      }
      ServerConnection server = null;
      if (mode == PreLauncherState.Mode.SERVER) {
         try {
            server = ServerConnection.of(f.host(), f.port());
         } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this.dialog, exception.getMessage(), "Zombie Lane", JOptionPane.ERROR_MESSAGE);
            return;
         }
         // Confirm that the selected server responds before sending credentials.
         String unavailable = serverGateMessage(server);
          if (unavailable != null) {
             JOptionPane.showMessageDialog(
                this.dialog, unavailable, LauncherText.get("error.serverUnavailableTitle"), JOptionPane.ERROR_MESSAGE
             );
             return;
          }
      }
      String authenticatedAccountId = null;
      if (mode == PreLauncherState.Mode.SERVER && server != null) {
         char[] enteredPassword = this.passwordField.getPassword();
         PendingCredentials.stage(enteredPassword);
         java.util.Arrays.fill(enteredPassword, '\0');
         char[] loginPassword = PendingCredentials.take();
         ServerConnection.AuthResult auth = server.authenticate(f.user().strip(), loginPassword, this.registerAccount.isSelected());
          if (!auth.ok()) {
             JOptionPane.showMessageDialog(
                this.dialog, auth.detail(), LauncherText.get("error.loginTitle"), JOptionPane.ERROR_MESSAGE
             );
             return;
          }
         authenticatedAccountId = auth.accountId();
      }
      // The typed user names the server account; it is only persisted when
      // "Recordar usuario" is checked (never the password, see
      // PendingCredentials).
      String user = f.user().strip();
      String host = server == null ? ServerConnection.normalizeHost(f.host()) : server.host();
      int port = server == null ? this.seed.port() : server.port();
      String devMissions = zlport.dev.LauncherDevOptions.sharedMissionsDirectory();

      int width = Integer.parseInt(f.width().strip());
      int height = Integer.parseInt(f.height().strip());
      Launcher.WindowState window =
         new Launcher.WindowState(this.currentSettings.x(), this.currentSettings.y(), width, height, f.maximized(), f.fullscreen());

      try {
          if (profile != null) {
             Settings.saveLauncherOptions(profile.directory(), window, optionValues(f));
             PreLauncherState.of(
                mode, host, port, user, this.rememberUser.isSelected(), profile.id(), devMissions, LauncherText.currentCode()
             ).save(this.dataRoot);
             this.result = Optional.of(new Selection(profile.directory(), profile, mode, server, user, authenticatedAccountId, this.rememberUser.isSelected(), devMissions));
          } else {
             Settings.saveLauncherOptions(this.dataRoot, window, optionValues(f));
             PreLauncherState.of(
                mode, host, port, user, this.rememberUser.isSelected(), this.seed.lastProfile(), devMissions, LauncherText.currentCode()
             ).save(this.dataRoot);
             this.result = Optional.of(new Selection(this.dataRoot, null, mode, server, user, authenticatedAccountId, this.rememberUser.isSelected(), devMissions));
          }
      } catch (IOException | RuntimeException exception) {
         JOptionPane.showMessageDialog(
            this.dialog, LauncherText.format("error.saveOptions", exception.getMessage()), "Zombie Lane", JOptionPane.ERROR_MESSAGE
         );
         return;
      }

      this.dialog.dispose();
   }

   /**
    * Refusal gate used before any credential is sent and by scripted SERVER
    * launches: null when the server answers /health, otherwise the message the
    * player sees and the launch is refused.
    */
   static String serverGateMessage(ServerConnection server) {
      ServerConnection.Probe probe = server.probe();
      if (probe.ok()) {
         return null;
      }
      return LauncherText.format("error.serverGate", server.host(), server.port(), probe.detail());
   }

   private void probeConnection() {
      String hostError = ServerConnection.validateHost(this.hostField.getText());
      if (hostError != null) {
         this.probeResult.setText(hostError);
         return;
      }
      String portError = portError(this.portField.getText());
      if (portError != null) {
         this.probeResult.setText(portError);
         return;
      }

      ServerConnection server;
      try {
         server = ServerConnection.of(this.hostField.getText(), this.portField.getText());
      } catch (IllegalArgumentException exception) {
         this.probeResult.setText(exception.getMessage());
         return;
      }

      this.probeResult.setText(LauncherText.format("probe.checking", server.healthUrl()));
      Thread thread = new Thread(() -> {
         ServerConnection.Probe probe = server.probe();
         SwingUtilities.invokeLater(() -> {
            if (probe.ok()) {
               this.probeResult.setText(LauncherText.format("probe.ok", server.healthUrl()));
            } else if (probe.detail().startsWith("HTTP ") || probe.detail().startsWith("port ")) {
               this.probeResult.setText(probe.detail());
            } else {
               this.probeResult.setText(LauncherText.format("probe.noResponse", probe.detail()));
            }
         });
      }, "zl-lanzador-comprueba");
      thread.setDaemon(true);
      thread.start();
   }

   private static GridBagConstraints nextCell(GridBagConstraints c) {
      GridBagConstraints copy = (GridBagConstraints)c.clone();
      copy.gridx++;
      copy.gridy = c.gridy;
      return copy;
   }

   private static GridBagConstraints sectionConstraints(int row) {
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = row;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1.0;
      c.insets = new Insets(4, 0, 4, 0);
      return c;
   }

   private static GridBagConstraints newSectionConstraints() {
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 0;
      c.insets = new Insets(2, 4, 2, 4);
      c.anchor = GridBagConstraints.WEST;
      c.fill = GridBagConstraints.NONE;
      return c;
   }
}
