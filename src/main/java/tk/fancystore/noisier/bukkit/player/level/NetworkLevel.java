package tk.fancystore.noisier.bukkit.player.level;

import com.google.common.collect.ImmutableList;
import org.bukkit.inventory.ItemStack;
import tk.fancystore.noisier.Manager;
import tk.fancystore.noisier.bukkit.Core;
import tk.fancystore.noisier.bukkit.player.Profile;
import tk.fancystore.noisier.plugin.config.NConfig;
import tk.fancystore.noisier.plugin.logger.CoreLogger;
import tk.fancystore.noisier.utils.BukkitUtils;
import tk.fancystore.noisier.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class NetworkLevel {

  private final int index;
  private final int exp;
  private final String description;
  private final List<NetworkLevelReward> rewards;

  public NetworkLevel(int exp, String description, List<NetworkLevelReward> rewards) {
    this.index = levels.size() + 1;
    this.exp = exp;
    this.description = description;
    this.rewards = rewards;
  }

  public int getIndex() {
    return index;
  }

  public int getExp() {
    return exp;
  }

  public String getDescription() {
    return description;
  }

  public List<NetworkLevelReward> getRewards() {
    return rewards;
  }

  public int getExperienceUntil(int current) {
    NetworkLevel level = this.getNext();

    if (level == null) {
      return 0;
    }

    return level.getExp() - current;
  }

  public ItemStack getIcon(Profile profile) {
    return BukkitUtils.deserializeItemStack(profile.getLevelContainer().getLevel().getIndex() >= index ? (profile.getLevelContainer().isClaimed(index) ?
        "MINECART : 1 : name>&cRecompensa do Nível " + index + " : lore>" + description + "\n \n&cVocê já coletou esta recompensa!"  :
        "STORAGE_MINECART : 1 : name>&aRecompensa do Nível " + index + " : lore>" + description + "\n \n&eClique para coletar esta recmpensa!") :
        "STORAGE_MINECART : 1 : name>&cRecompensa do Nível " + index + " : lore>" + description + "\n \n&cVocê precisa atingir o nível " + index + " para coletar!"
    );
  }

  public NetworkLevel getNext() {
    return levels.stream().filter(level -> level.getIndex() == this.index + 1).findFirst().orElse(null);
  }

  public String getProgressBar(Profile profile) {
    return getProgressBar(profile, "▇", "§a", "§7");
  }

  public String getProgressBar(Profile profile, String symbol, String completedColor, String remainColor) {
    int exp = 0;

    NetworkLevel next = this.getNext();
    if (next != null) {
      exp = next.getExp();
    }

    return Utils.makeProgressBar(profile.getLevelContainer().getExp(), exp, 10, symbol, completedColor, next == null ? completedColor : remainColor);
  }

  public static final CoreLogger LOGGER = Manager.getCorePlugin().getCoreLogger().getModule("Level");
  private static final List<NetworkLevel> levels = new ArrayList<>();

  public static void setupLevels() {
    PrestigeSymbol.setupPrestigeSymbols();

    NConfig config = Core.getInstance().getConfig("levels");
    for (String key : config.getSection("levels").getKeys()) {
      int exp = config.getInt("levels." + key + ".exp");
      String description = config.getString("levels." + key + ".description");
      List<NetworkLevelReward> rewards = new ArrayList<>();
      for (String reward : config.getStringList("levels." + key + ".rewards")) {
        rewards.add(new NetworkLevelReward(reward));
      }

      levels.add(new NetworkLevel(exp, description, rewards));
    }
  }

  public static NetworkLevel getByLevel(int level) {
    return levels.get(level - 1);
  }

  public static List<NetworkLevel> listLevels() {
    return ImmutableList.copyOf(levels);
  }
}
