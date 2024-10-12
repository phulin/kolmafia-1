package net.sourceforge.kolmafia.swingui.panel;

import static net.sourceforge.kolmafia.preferences.Preferences.getString;
import static net.sourceforge.kolmafia.preferences.Preferences.setString;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.Collections;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.sourceforge.kolmafia.StaticEntity;
import net.sourceforge.kolmafia.swingui.CommandDisplayFrame;
import net.sourceforge.kolmafia.swingui.listener.DefaultComponentFocusTraversalPolicy;
import net.sourceforge.kolmafia.swingui.listener.HyperlinkAdapter;
import net.sourceforge.kolmafia.swingui.listener.ThreadedListener;
import net.sourceforge.kolmafia.swingui.widget.AutoHighlightTextField;
import net.sourceforge.kolmafia.swingui.widget.RequestPane;
import net.sourceforge.kolmafia.utilities.RollingLinkedList;
import netscape.javascript.JSObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CommandDisplayPanel extends JPanel implements FocusListener {
  private final RollingLinkedList<String> commandHistory = new RollingLinkedList<>(20);
  private final AutoHighlightTextField entryField;
  private int elementId = 0;

  // js for (let i = 0; i < 1000; i++) { print(i); wait(1); }
  private static final int PADDING = 5;
  private static final String STYLE =
      "* { box-sizing: border-box; }"
          + "html { padding: 0px; margin: 0px; }"
          + "body { font-family: sans-serif; padding: 0px; margin: 0px; }"
          + "#container { height: 100vh; width: 100vw; overflow-x: clip; overflow-y: scroll; padding: "
          + PADDING
          + "px; }";
  private static final String INITIAL_CONTENT =
      "<!doctype html><html><head><style>"
          + STYLE
          + "</style>"
          + "</head><body><div id=\"container\"></div></div></body></html>";

  // If 1100 or more lines in scrollback, truncate to 1000.
  private static final int TRUNCATE_THRESHOLD = 1100;
  private static final int TRUNCATE_TO = 1000;

  public WebView webView = null;
  private Node body = null;
  private Node container = null;
  private final JFXPanel panel;

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

    JButton entryButton = new JButton("exec");
    entryButton.addActionListener(new CommandEntryListener());

    entryPanel.add(this.entryField, BorderLayout.CENTER);
    entryPanel.add(entryButton, BorderLayout.EAST);

    this.setLayout(new BorderLayout(1, 1));

    JFXPanel jfxPanel = new JFXPanel();
    jfxPanel.setSize(400, 300);
    jfxPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    this.panel = jfxPanel;

    Platform.runLater(
        () -> {
          this.webView = new WebView();
          WebEngine engine = this.webView.getEngine();
          engine.loadContent(CommandDisplayPanel.INITIAL_CONTENT, "text/html");
          engine
              .getLoadWorker()
              .stateProperty()
              .addListener(
                  (ObservableValue<? extends Worker.State> observable,
                      Worker.State oldValue,
                      Worker.State newValue) -> {
                    if (newValue != Worker.State.SUCCEEDED) {
                      return;
                    }

                    this.body =
                        this.webView.getEngine().getDocument().getElementsByTagName("body").item(0);
                    this.container =
                        this.webView.getEngine().getDocument().getElementById("container");

                    this.webView
                        .getEngine()
                        .executeScript(
                            """
                        (function() {
                          window.scrolled = true;
                          const container = document.getElementById("container");
                          container.addEventListener("scroll", (event) => {
                            if (window.ignoreScrollEvents) {
                              window.ignoreScrollEvents = false;
                              return;
                            }
                            const distanceFromBottom = container.scrollHeight - (container.scrollTop + window.innerHeight + 2 * %d)
                            window.scrolled = distanceFromBottom <= 20;
                          });
                          new MutationObserver((muts) => {
                            muts.forEach((mut) => {
                              if (mut.type === "childList" && mut.addedNodes && mut.addedNodes.length > 0 && window.scrolled) {
                                window.ignoreScrollEvents = true;
                                container.scrollTo(0, container.scrollHeight);
                              }
                            });
                          }).observe(
                            container,
                            { childList: true }
                          );
                        })();
                        """
                                .formatted(PADDING));
                  });

          Scene scene = new Scene(this.webView);
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

  private void truncate() {
    //    NodeList children = this.container.getChildNodes();
    //    if (children.getLength() >= TRUNCATE_THRESHOLD) {
    //      for (int i = children.getLength() - TRUNCATE_TO - 1; i >= 0; i--) {
    //        Node child = children.item(i);
    //        this.container.removeChild(child);
    //      }
    //    }
    if (this.container.getChildNodes().getLength() >= TRUNCATE_THRESHOLD) {
      CommandDisplayFrame.panel
          .webView
          .getEngine()
          .executeScript(
              """
        (function() {
          const container = document.getElementById("container");
          for (let i = container.childNodes.length - %d - 1; i >= 0; i--) {
            const child = container.childNodes[i];
            this.container.removeChild(child);
          }
        })()"""
                  .formatted(TRUNCATE_TO));
    }
  }

  public void appendSessionContent(String content) {
    if (this.body == null) return;
    Platform.runLater(
        () -> {
          Element newDiv = this.container.getOwnerDocument().createElement("div");
          newDiv.setAttribute("id", "element" + this.elementId++);
          ((JSObject) newDiv).setMember("innerHTML", content);
          this.container.appendChild(newDiv);
          truncate();

          SwingUtilities.invokeLater(this.panel::invalidate);
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
