package net.sourceforge.kolmafia.objecttypes;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;
import net.sourceforge.kolmafia.Modifiers;
import net.sourceforge.kolmafia.moods.RestoreItem;
import net.sourceforge.kolmafia.objectpool.ItemPool;
import net.sourceforge.kolmafia.objectpool.SkillPool;
import net.sourceforge.kolmafia.persistence.Consumable;
import net.sourceforge.kolmafia.persistence.ItemDatabase;
import net.sourceforge.kolmafia.persistence.SkillDatabase;
import net.sourceforge.kolmafia.utilities.StringUtilities;

public class Item {
  private final int id;
  private final String name;
  private final String plural;
  private final String descriptionId;
  private final String image;
  private final ItemType itemType;
  private final EnumSet<ItemDatabase.Attribute> attributes;

  private final String lookup;

  private RestoreItem restore = null;
  private Consumable consumable = null;

  public Item(
      int id,
      String name,
      String plural,
      String descriptionId,
      String image,
      ItemType itemType,
      EnumSet<ItemDatabase.Attribute> attributes) {
    this.id = id;
    this.name = name;
    this.plural = plural;
    this.descriptionId = descriptionId;
    this.image = image;
    this.itemType = itemType;
    this.attributes = attributes;

    this.lookup = Modifiers.getLookupName("Item", id);
  }

  // Only to be used at initialization time.
  public void setRestore(RestoreItem restore) {
    this.restore = restore;
  }

  // Only to be used at initialization time.
  public void setConsumable(Consumable consumable) {
    this.consumable = consumable;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPlural() {
    return plural;
  }

  public String getDescriptionId() {
    return descriptionId;
  }

  public String getImage() {
    return image;
  }

  public ItemType getItemType() {
    return itemType;
  }

  public Consumable getConsumable() {
    return consumable;
  }

  public RestoreItem getRestore() {
    return restore;
  }

  public boolean isQuest() {
    return attributes.contains(ItemDatabase.Attribute.QUEST);
  }

  public boolean isGift() {
    return attributes.contains(ItemDatabase.Attribute.GIFT);
  }

  public boolean isTradeable() {
    return attributes.contains(ItemDatabase.Attribute.TRADEABLE);
  }

  public boolean isDiscardable() {
    return attributes.contains(ItemDatabase.Attribute.DISCARDABLE);
  }

  public boolean isCombatUsable() {
    return attributes.contains(ItemDatabase.Attribute.COMBAT);
  }

  public boolean isCombatReusable() {
    return attributes.contains(ItemDatabase.Attribute.COMBAT_REUSABLE);
  }

  public boolean isUsable() {
    return attributes.contains(ItemDatabase.Attribute.USABLE);
  }

  public boolean isReusable() {
    return attributes.contains(ItemDatabase.Attribute.REUSABLE);
  }

  public boolean isMultiUsable() {
    return attributes.contains(ItemDatabase.Attribute.MULTIPLE);
  }

  public String getCandyType() {
    return Stream.of(
            ItemDatabase.Attribute.CANDY0,
            ItemDatabase.Attribute.CANDY1,
            ItemDatabase.Attribute.CANDY2)
        .filter(attributes::contains)
        .findAny()
        .map(Enum::name)
        .orElse(null);
  }

  public boolean isChocolate() {
    return attributes.contains(ItemDatabase.Attribute.CHOCOLATE);
  }

  public EnumSet<ItemDatabase.Attribute> getAttributes() {
    return attributes;
  }

  public boolean isPotion() {
    return List.of(ItemType.POTION, ItemType.AVATAR_POTION).contains(itemType);
  }

  public boolean isEquipment() {
    return isEquipment(true);
  }

  public boolean isEquipment(boolean includeFamiliarEquipment) {
    return itemType.isEquipmentType(includeFamiliarEquipment);
  }

  public boolean isFood() {
    return itemType == ItemType.EAT;
  }

  public boolean isBooze() {
    return itemType == ItemType.DRINK;
  }

  public boolean isHat() {
    return itemType == ItemType.HAT;
  }

  public boolean isWeapon() {
    return itemType == ItemType.WEAPON;
  }

  public boolean isOffHand() {
    return itemType == ItemType.OFFHAND;
  }

  public boolean isShirt() {
    return itemType == ItemType.SHIRT;
  }

  public boolean isPants() {
    return itemType == ItemType.PANTS;
  }

  public boolean isAccessory() {
    return itemType == ItemType.ACCESSORY;
  }

  public boolean isFamiliarEquipment() {
    return itemType == ItemType.FAMILIAR_EQUIPMENT;
  }

  public int getGelatinousNoobSkillId() {
    if (!isEquipment(false)
        && isDiscardable()
        && (isTradeable()
            || isGift()
            || id == ItemPool.CLOD_OF_DIRT
            || id == ItemPool.DIRTY_BOTTLECAP
            || id == ItemPool.DISCARDED_BUTTON)) {
      int intDescId = StringUtilities.parseInt(descriptionId);
      return switch (id) {
          // Override Robortender items
        case ItemPool.NOVELTY_HOT_SAUCE -> SkillPool.FROWN_MUSCLES;
        case ItemPool.COCKTAIL_MUSHROOM -> SkillPool.RETRACTABLE_TOES;
        case ItemPool.GRANOLA_LIQUEUR -> SkillPool.INK_GLAND;
        case ItemPool.GREGNADIGNE -> SkillPool.BENDABLE_KNEES;
        case ItemPool.BABY_OIL_SHOOTER -> SkillPool.POWERFUL_VOCAL_CHORDS;
        case ItemPool.LIMEPATCH -> SkillPool.ANGER_GLANDS;
        default -> (intDescId % 125) + 23001;
      };
    }
  }

  /**
   * Returns the Skill granted by using this Item.
   *
   * @return The Skill granted, or null if no skill
   */
  public Integer getGrantedSkillId() {
    String skillName = Modifiers.getStringModifier("Item", id, "Skill");
    return skillName.isEmpty() ? null : SkillDatabase.getSkillId(skillName);
  }

  /**
   * Returns the Recipe granted by using this Item.
   *
   * @return The Recipe learned, or null if no recipe
   */
  public Item getRecipe() {
    String recipeName = Modifiers.getStringModifier("Item", id, "Recipe");
    return recipeName.isEmpty()
        ? null
        : ItemDatabase.getItemObject(ItemDatabase.getExactItemId(recipeName));
  }
}
