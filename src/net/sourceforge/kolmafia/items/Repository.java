package net.sourceforge.kolmafia.items;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.sourceforge.kolmafia.AdventureResult;
import net.sourceforge.kolmafia.objectpool.ItemPool;

// Map from item ID to count.
public class Repository {
  public enum Type {
    INVENTORY,
    CLOSET,
    STORAGE,
    STASH,
    COLLECTION,
    UNLIMITED,
    FREEPULL,
    NOPULL,
    CAMPGROUND,
    CHATEAU
  }

  // Internal storage as map from item ID to count. We maintain the guarantee that there are no 0
  // values in here; i.e. if a key is in the map, the value is nonzero.
  private final HashMap<Integer, Integer> impl = new HashMap<>();
  private final Type type;

  public Repository(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public int size() {
    return this.impl.size();
  }

  public boolean contains(int itemId) {
    return this.impl.containsKey(itemId);
  }

  public boolean contains(AdventureResult ar) {
    return this.contains(ar.getItemId());
  }

  public int count(int itemId) {
    return itemId > 0 ? this.impl.getOrDefault(itemId, 0) : 0;
  }

  public int count(AdventureResult ar) {
    return this.count(ar.getItemId());
  }

  public Set<Map.Entry<Integer, Integer>> entrySet() {
    return this.impl.entrySet();
  }

  public Collection<AdventureResult> asAdventureResults() {
    return this.impl.entrySet().stream()
        .map(entry -> ItemPool.get(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  public void clear() {
    this.impl.clear();
  }

  public void add(int itemId, int count) {
    if (itemId > 0) {
      int newCount = this.impl.getOrDefault(itemId, 0) + count;
      if (newCount == 0) {
        this.impl.remove(itemId);
      } else {
        this.impl.put(itemId, newCount);
      }
    }
  }

  public void add(AdventureResult ar) {
    this.add(ar.getItemId(), ar.getCount());
  }

  public void addAll(Repository other) {
    for (var entry : other.impl.entrySet()) {
      this.add(entry.getKey(), entry.getValue());
    }
  }

  public void addAll(Collection<? extends AdventureResult> list) {
    list.forEach(this::add);
  }

  public void set(int itemId, int count) {
    if (count == 0) return;
    this.impl.put(itemId, count);
  }

  public void remove(int itemId) {
    this.impl.remove(itemId);
  }

  public void remove(AdventureResult ar) {
    this.remove(ar.getItemId());
  }
}
