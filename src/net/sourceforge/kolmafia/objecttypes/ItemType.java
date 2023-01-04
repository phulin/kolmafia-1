package net.sourceforge.kolmafia.objecttypes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Ways to use items.
 *
 * <p>A conflation of a few concepts. Most are "primary uses" as appear in items.txt
 *
 * <p>Primary uses determine how you can interact with an item. Some items are "usable" in addition
 * to their primary use (for example, the fortune cookie or glitch season reward).
 *
 * <p>ConsumptionType is also used for how the user /intends/ to interact with an item. This is what
 * the "Familiar 'uses'" are used for: when the user uses a command specifically for feeding a
 * familiar.
 *
 * <p>Most of this is handled in UseItemRequest.
 */
public enum ItemType {
  UNKNOWN("unknown"),
  // Cannot be "used" in any way by itself
  NONE("none"),

  // Consumables
  EAT("food"),
  DRINK("drink"),
  SPLEEN("spleen"),

  // Usables
  USE("usable"),
  USE_MULTIPLE("multiple"),
  USE_INFINITE("reusable"),
  USE_MESSAGE_DISPLAY("message"),

  // Familiar hatchlings
  FAMILIAR_HATCHLING("grow"),

  // Equipment
  HAT("hat"),
  WEAPON("weapon"),
  OFFHAND("offhand"),
  CONTAINER("container"),
  SHIRT("shirt"),
  PANTS("pants"),
  ACCESSORY("accessory"),
  FAMILIAR_EQUIPMENT("familiar"),

  // Customizable "equipment"
  STICKER("sticker"),
  CARD("card"),
  FOLDER("folder"),
  BOOTSKIN("bootskin"),
  BOOTSPUR("bootspur"),
  SIXGUN("sixgun"),

  // Special "uses"
  FOOD_HELPER("food helper"),
  DRINK_HELPER("drink helper"),
  ZAP("zap"),
  EL_VIBRATO_SPHERE("sphere"),
  PASTA_GUARDIAN("guardian"),
  POKEPILL("pokepill"),

  // Potions
  POTION("potion"),
  AVATAR_POTION("avatar"),

  // Familiar "uses"
  ROBORTENDER("robortender"),
  STOCKING_MIMIC("stocking mimic"),
  SLIMELING("slimeling"),
  SPIRIT_HOBO("spirit hobo"),
  GLUTTONOUS_GHOST("gluttonous ghost");

  public final String description;
  private static final Map<String, ItemType> consumptionTypeByDescription = new HashMap<>();

  ItemType(String description) {
    this.description = description;
  }

  public static ItemType byDescription(String description) {
    var lookup = consumptionTypeByDescription.get(description);
    if (lookup != null) return lookup;
    var search =
        Arrays.stream(ItemType.values()).filter(x -> x.description.equals(description)).findAny();
    search.ifPresent(x -> consumptionTypeByDescription.put(description, x));
    return search.orElse(null);
  }

  /**
   * Returns true if this item type relates to equipment
   *
   * @return True if the type relates to equipment
   */
  public boolean isEquipmentType(final boolean includeFamiliarEquipment) {
    return switch (this) {
      case ACCESSORY, CONTAINER, HAT, SHIRT, PANTS, WEAPON, OFFHAND -> true;
      default -> includeFamiliarEquipment && this == FAMILIAR_EQUIPMENT;
    };
  }
}
