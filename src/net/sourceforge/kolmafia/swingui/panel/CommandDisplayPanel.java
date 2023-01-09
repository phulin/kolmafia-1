package net.sourceforge.kolmafia.swingui.panel;

import static net.sourceforge.kolmafia.preferences.Preferences.getString;
import static net.sourceforge.kolmafia.preferences.Preferences.setString;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.sourceforge.kolmafia.StaticEntity;
import net.sourceforge.kolmafia.swingui.CommandDisplayFrame;
import net.sourceforge.kolmafia.swingui.listener.DefaultComponentFocusTraversalPolicy;
import net.sourceforge.kolmafia.swingui.listener.HyperlinkAdapter;
import net.sourceforge.kolmafia.swingui.listener.ThreadedListener;
import net.sourceforge.kolmafia.swingui.widget.AutoHighlightTextField;
import net.sourceforge.kolmafia.swingui.widget.RequestPane;
import net.sourceforge.kolmafia.utilities.RollingLinkedList;
import netscape.javascript.JSObject;

public class CommandDisplayPanel extends JPanel implements FocusListener {
  private final RollingLinkedList<String> commandHistory = new RollingLinkedList<>(20);
  private final AutoHighlightTextField entryField;
  private final JButton entryButton;

  private static final String STYLE = "body { font-family: sans-serif; padding: 1px; }";
  private static final String INITIAL_CONTENT =
      "<html><head><style>" + STYLE + "</style></head><body></body></html>";
  private WebView webView = null;
  private JSObject body = null;

  private final String preference;
  private final String DELIMITER = "#";

  private int commandIndex = 0;

  public CommandDisplayPanel(String preference) {
    this.preference = preference;
    loadHistoryFromPreference();
    RequestPane outputDisplay = new RequestPane();
    outputDisplay.addHyperlinkListener(new HyperlinkAdapter());

    JPanel entryPanel = new JPanel(new BorderLayout());
    this.entryField = new AutoHighlightTextField();
    this.entryField.addKeyListener(new CommandEntryListener());

    this.entryButton = new JButton("exec");
    this.entryButton.addActionListener(new CommandEntryListener());

    entryPanel.add(this.entryField, BorderLayout.CENTER);
    entryPanel.add(this.entryButton, BorderLayout.EAST);

    this.setLayout(new BorderLayout(1, 1));

    JFXPanel jfxPanel = new JFXPanel();
    jfxPanel.setSize(400, 300);

    Platform.runLater(
        () -> {
          this.webView = new WebView();
          WebEngine engine = this.webView.getEngine();
          engine.loadContent(CommandDisplayPanel.INITIAL_CONTENT, "text/html");
          webView
              .getEngine()
              .getLoadWorker()
              .stateProperty()
              .addListener(
                  (ObservableValue<? extends Worker.State> observable,
                      Worker.State oldValue,
                      Worker.State newValue) -> {
                    if (newValue != Worker.State.SUCCEEDED) {
                      return;
                    }

                    this.body = (JSObject) this.webView.getEngine().executeScript("document.body");
                  });

          Group root = new Group();
          Scene scene = new Scene(root);
          root.getChildren().add(this.webView);
          jfxPanel.setScene(scene);
        });

    this.add(jfxPanel, BorderLayout.CENTER);

    this.add(entryPanel, BorderLayout.SOUTH);

    this.setFocusCycleRoot(true);
    this.setFocusTraversalPolicy(new DefaultComponentFocusTraversalPolicy(this.entryField));

    this.addFocusListener(this);
  }

  private void loadHistoryFromPreference() {
    String pref = getString(preference);
    String[] commands = pref.split(DELIMITER);
    if (!commands[0].trim().isEmpty()) {
      Collections.addAll(commandHistory, commands);
      this.commandIndex = commandHistory.size();
    }
  }

  private void updatePreference() {
    String newPref = String.join(DELIMITER, commandHistory);
    setString(preference, newPref);
  }

  @Override
  public void focusGained(FocusEvent e) {
    this.entryField.requestFocus();
  }

  @Override
  public void focusLost(FocusEvent e) {}

  public void appendSessionContent(String content) {
    if (this.body == null) return;
    Platform.runLater(
        () -> {
          String newInnerHTML;
          newInnerHTML = this.body.getMember("innerHTML") + content;
          this.body.setMember("innerHTML", newInnerHTML);
        });
  }

  private class CommandEntryListener extends ThreadedListener {
    @Override
    protected boolean isValidKeyCode(int keyCode) {
      return keyCode == KeyEvent.VK_UP
          || keyCode == KeyEvent.VK_DOWN
          || keyCode == KeyEvent.VK_ENTER;
    }

    @Override
    protected void execute() {
      if (this.isAction()) {
        this.submitCommand();
        return;
      }

      int keyCode = getKeyCode();

      if (keyCode == KeyEvent.VK_UP) {
        if (CommandDisplayPanel.this.commandIndex <= 0) {
          return;
        }

        CommandDisplayPanel.this.entryField.setText(
            CommandDisplayPanel.this.commandHistory.get(--CommandDisplayPanel.this.commandIndex));
      } else if (keyCode == KeyEvent.VK_DOWN) {
        if (CommandDisplayPanel.this.commandIndex + 1
            >= CommandDisplayPanel.this.commandHistory.size()) {
          return;
        }

        CommandDisplayPanel.this.entryField.setText(
            CommandDisplayPanel.this.commandHistory.get(++CommandDisplayPanel.this.commandIndex));
      } else if (keyCode == KeyEvent.VK_ENTER) {
        this.submitCommand();
      }
    }

    /**
     * Handler for keyReleased events which will end up running this object's execute() method.
     *
     * <p>This implementation is identical to ThreadedListener.keyReleased (which this overrides),
     * except it runs on the Event Dispatch Thread. This is vital for command entry since
     * dispatching a parallel thread will create a race condition.
     *
     * @see
     *     net.sourceforge.kolmafia.swingui.listener.ThreadedListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(final KeyEvent e) {
      if (e.isConsumed()) {
        return;
      }

      if (!this.isValidKeyCode(e.getKeyCode())) {
        return;
      }

      this.keyEvent = e;
      try {
        this.run();
      } catch (Exception e1) {
        StaticEntity.printStackTrace(e1);
      }

      e.consume();
    }

    private void submitCommand() {
      String command = CommandDisplayPanel.this.entryField.getText().trim();
      CommandDisplayPanel.this.entryField.setText("");

      if (!command.isEmpty()
          && (CommandDisplayPanel.this.commandHistory.isEmpty()
              || !CommandDisplayPanel.this.commandHistory.getLast().equals(command))) {
        CommandDisplayPanel.this.commandHistory.add(command);
        updatePreference();
      }

      CommandDisplayPanel.this.commandIndex = CommandDisplayPanel.this.commandHistory.size();
      CommandDisplayFrame.executeCommand(command);
    }
  }
}
