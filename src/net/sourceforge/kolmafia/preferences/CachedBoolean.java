package net.sourceforge.kolmafia.preferences;

public class CachedBoolean extends CachedPreference<Boolean> {
  public CachedBoolean(String property) {
    super(property);
  }

  @Override
  public void update() {
    this.value = Preferences.getBoolean(this.property);
  }
}
