package net.sourceforge.kolmafia.preferences;

public class CachedInteger extends CachedPreference<Integer> {
  public CachedInteger(String property) {
    super(property);
  }

  @Override
  public void update() {
    this.value = Preferences.getInteger(this.property);
  }
}
