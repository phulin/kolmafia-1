package net.sourceforge.kolmafia.websocket;

import com.alibaba.fastjson2.annotation.JSONField;
import com.alibaba.fastjson2.annotation.JSONType;
import net.sourceforge.kolmafia.KoLCharacter;

@JSONType
public record CharacterSheetMessage(
    String username,
    int userId,
    int currentLevel,
    String ascensionClass,
    long currentHP,
    long maximumHP,
    long baseMaxHP,
    long currentMP,
    long maximumMP,
    long baseMaxMP,
    int[] adjustedStats,
    long[] totalSubpoints,
    int[] baseStats)
    implements WebsocketMessage {
  public CharacterSheetMessage() {
    this(
        KoLCharacter.getUserName(),
        KoLCharacter.getUserId(),
        KoLCharacter.getLevel(),
        KoLCharacter.getAscensionClassName(),
        KoLCharacter.getCurrentHP(),
        KoLCharacter.getMaximumHP(),
        KoLCharacter.getBaseMaxHP(),
        KoLCharacter.getCurrentMP(),
        KoLCharacter.getMaximumMP(),
        KoLCharacter.getBaseMaxMP(),
        new int[] {
          KoLCharacter.getAdjustedMuscle(),
          KoLCharacter.getAdjustedMysticality(),
          KoLCharacter.getAdjustedMoxie()
        },
        new long[] {
          KoLCharacter.getTotalMuscle(),
          KoLCharacter.getTotalMysticality(),
          KoLCharacter.getTotalMoxie()
        },
        new int[] {
          KoLCharacter.getBaseMuscle(),
          KoLCharacter.getBaseMysticality(),
          KoLCharacter.getBaseMoxie()
        });
  }

  @JSONField(name = "type")
  @Override
  public String getType() {
    return "charsheet";
  }
}
