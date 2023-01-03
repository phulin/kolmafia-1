package net.sourceforge.kolmafia.objecttypes;

import jdk.jshell.spi.ExecutionControl;
import net.sourceforge.kolmafia.Modifiers;
import net.sourceforge.kolmafia.moods.RestoreItem;
import net.sourceforge.kolmafia.persistence.Consumable;
import net.sourceforge.kolmafia.persistence.ItemDatabase;

import java.util.EnumSet;

public class Item {
  private final int id;
  private final String name;
  private final String plural;
  private final String image;
  private final String notes;
  private final EnumSet<ItemDatabase.Attribute> attributes;
  private final int gelatinousNoobSkillId;
  private final int grantedSkillId;

  private final String lookup;

  private RestoreItem restore = null;
  private Consumable consumable = null;


  public Item(int id, String name, String plural, String image, String notes,
              EnumSet<ItemDatabase.Attribute> attributes, int gelatinousNoobSkillId, int grantedSkillId) {
    this.id = id;
    this.name = name;
    this.plural = plural;
    this.image = image;
    this.notes = notes;
    this.attributes = attributes;
    this.gelatinousNoobSkillId = gelatinousNoobSkillId;
    this.grantedSkillId = grantedSkillId;

    this.lookup = Modifiers.getLookupName("Item", id);
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

  public String getImage() {
    return image;
  }

  public Consumable getConsumable() {
    return consumable;
  }

  public Range getHpRestored() {
    throw new UnsupportedOperationException();
  }

  public Range getMpRestored() {
    throw new UnsupportedOperationException();
  }

  public String getNotes() {
    return notes;
  }

  public boolean isQuest() {
    return quest;
  }

  public boolean isGift() {
    return gift;
  }

  public boolean isTradeable() {
    return tradeable;
  }

  public boolean isDiscardable() {
    return discardable;
  }

  public boolean isCombatUsable() {
    return combatUsable;
  }

  public boolean isCombatReusable() {
    return combatReusable;
  }

  public boolean isUsable() {
    return usable;
  }

  public boolean isReusable() {
    return reusable;
  }

  public boolean isMultiUsable() {
    return multiUsable;
  }

  public EnumSet<ItemDatabase.Attribute> getAttributes() {
    return attributes;
  }

  public String getCandyType() {
    return candyType;
  }

  public boolean isChocolate() {
    return chocolate;
  }

  public boolean isPotion() {
    return potion;
  }

  public int getGelatinousNoobSkillId() {
    return gelatinousNoobSkillId;
  }

  public int getGrantedSkillId() {
    return grantedSkillId;
  }

  public Item getRecipe() {
    return recipe;
  }
}
