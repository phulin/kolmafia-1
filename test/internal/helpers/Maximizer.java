package internal.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.sourceforge.kolmafia.AdventureResult;
import net.sourceforge.kolmafia.ModifierType;
import net.sourceforge.kolmafia.Modifiers;
import net.sourceforge.kolmafia.maximizer.Boost;
import net.sourceforge.kolmafia.swingui.MaximizerFrame;

public class Maximizer {

  public static boolean maximize(String maximizerString) {
    return net.sourceforge.kolmafia.maximizer.Maximizer.maximize(maximizerString, 0, 0, true);
  }

  public static void maximizeCreatable(String maximizerString) {
    MaximizerFrame.expressionSelect.setSelectedItem(maximizerString);
    net.sourceforge.kolmafia.maximizer.Maximizer.maximize(1, 0, 0, false, 0);
  }

  public static double modFor(String modifier) {
    return Modifiers.getNumericModifier(ModifierType.GENERATED, "_spec", modifier);
  }

  public static List<Boost> getBoosts() {
    return net.sourceforge.kolmafia.maximizer.Maximizer.boosts;
  }

  public static Optional<AdventureResult> getSlot(int slot) {
    var boost =
        getBoosts().stream().filter(Boost::isEquipment).filter(b -> b.getSlot() == slot).findAny();
    return boost.map(Boost::getItem);
  }

  public static void recommendedSlotIs(int slot, String item) {
    Optional<AdventureResult> equipment = getSlot(slot);
    assertTrue(equipment.isPresent(), "Expected " + item + " to be recommended, but it was not");
    assertEquals(AdventureResult.tallyItem(item), equipment.get());
  }

  public static void recommendedSlotIsUnchanged(int slot) {
    Optional<AdventureResult> equipment = getSlot(slot);
    assertTrue(
        equipment.isEmpty(),
        () ->
            "Expected empty slot "
                + slot
                + ", but it was "
                + equipment.map(AdventureResult::toString).orElse(""));
  }

  public static void recommends(String item) {
    Optional<Boost> found =
        getBoosts().stream()
            .filter(Boost::isEquipment)
            .filter(b -> item.equals(b.getItem().getName()))
            .findAny();
    assertTrue(found.isPresent(), "Expected " + item + " to be recommended, but it was not");
  }

  public static void recommends(int itemId) {
    Optional<Boost> found =
        getBoosts().stream()
            .filter(Boost::isEquipment)
            .filter(b -> (itemId == b.getItem().getItemId()))
            .findAny();
    assertTrue(found.isPresent(), "Expected " + itemId + " to be recommended, but it was not");
  }

  public static boolean someBoostIs(Predicate<Boost> predicate) {
    return getBoosts().stream().anyMatch(predicate);
  }

  public static boolean commandStartsWith(Boost boost, String prefix) {
    return boost.getCmd().startsWith(prefix);
  }
}
