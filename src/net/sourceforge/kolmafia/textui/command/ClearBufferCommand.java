package net.sourceforge.kolmafia.textui.command;

import net.sourceforge.kolmafia.swingui.CommandDisplayFrame;

public class ClearBufferCommand extends AbstractCommand {
  public ClearBufferCommand() {
    this.usage = " - clear CLI window.";
  }

  @Override
  public void run(final String cmd, final String parameters) {
    CommandDisplayFrame.panel
        .webView
        .getEngine()
        .executeScript("document.getElementById(\"container\").textContent = ''");
    throw new UnsupportedOperationException("Not implemented.");
  }
}
