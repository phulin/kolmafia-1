package net.sourceforge.kolmafia.textui.command;

import javafx.application.Platform;
import net.sourceforge.kolmafia.KoLmafia;
import net.sourceforge.kolmafia.KoLmafiaCLI.ParameterHandling;
import net.sourceforge.kolmafia.swingui.CommandDisplayFrame;
import net.sourceforge.kolmafia.textui.RuntimeLibrary;
import net.sourceforge.kolmafia.textui.javascript.JavascriptRuntime;
import net.sourceforge.kolmafia.textui.parsetree.CompositeValue;
import net.sourceforge.kolmafia.textui.parsetree.Value;

public class JavaScriptCommand extends AbstractCommand {
  public JavaScriptCommand() {
    this.flags = ParameterHandling.FULL_LINE;
    this.usage = " <statement> - test a line of JavaScript code without having to edit a script.";
  }

  @Override
  public void run(final String cmd, String parameters) {
    if (cmd.endsWith("q")) {
      Platform.runLater(
          () -> {
            Object result = CommandDisplayFrame.panel.webView.getEngine().executeScript(parameters);
            KoLmafia.updateDisplay("Returned: " + result);
          });
      return;
    }

    JavascriptRuntime runtime = new JavascriptRuntime(parameters);
    Value returnValue = runtime.execute("main", new String[] {});

    if (cmd.endsWith("q")) {
      return;
    }

    KoLmafia.updateDisplay("Returned: " + returnValue);

    returnValue = Value.asProxy(returnValue);
    if (returnValue instanceof CompositeValue) {
      RuntimeLibrary.dump((CompositeValue) returnValue);
    }
  }
}
