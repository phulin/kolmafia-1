package net.sourceforge.kolmafia.preferences;

import net.sourceforge.kolmafia.listener.Listener;
import net.sourceforge.kolmafia.listener.PreferenceListenerRegistry;

/**
 * These classes are a way to handle properties that are accessed many times and rarely changed, to
 * avoid making many lookups into the Preferences maps.
 */
public abstract class CachedPreference<T> implements Listener {
  protected String property;
  protected T value;

  public CachedPreference(String property) {
    this.property = property;
    this.update();
    PreferenceListenerRegistry.registerPreferenceListener(property, this);
  }

  @Override
  public abstract void update();

  public T get() {
    return this.value;
  }
}
