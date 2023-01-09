package net.sourceforge.kolmafia.textui.command;

public class ClearBufferCommand extends AbstractCommand {
  public ClearBufferCommand() {
    this.usage = " - clear CLI window.";
  }

  @Override
  public void run(final String cmd, final String parameters) {
    throw new UnsupportedOperationException("Not implemented.");
    //    CommandDisplayFrame.sessionOutputView.getEngine().getDocument().getChildNodes();
  }
}
