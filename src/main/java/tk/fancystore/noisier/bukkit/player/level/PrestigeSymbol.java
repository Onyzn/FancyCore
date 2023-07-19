package tk.fancystore.noisier.bukkit.player.level;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tk.fancystore.noisier.bukkit.Core;
import tk.fancystore.noisier.bukkit.player.Profile;
import tk.fancystore.noisier.database.data.container.LevelContainer;
import tk.fancystore.noisier.plugin.config.NConfig;
import tk.fancystore.noisier.role.Role;
import tk.fancystore.noisier.utils.BukkitUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrestigeSymbol {

  private final long id;
  private final String name;
  private final int level;
  private final String symbol;
  private final ItemStack icon;

  public PrestigeSymbol(long id, String name, int level, String symbol, ItemStack icon) {
    this.id = id;
    this.name = name;
    this.level = level;
    this.symbol = symbol;
    this.icon = icon;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getLevel() {
    return level;
  }

  public String getSymbol() {
    return symbol;
  }

  public ItemStack getIcon(Profile profile) {
    ItemStack cloned = icon.clone();
    ItemMeta meta = cloned.getItemMeta();
    meta.addItemFlags(ItemFlag.values());

    LevelContainer lc = profile.getLevelContainer();
    boolean has = lc.getLevel().getIndex() >= this.level;
    boolean selected = lc.getSelectedSymbol().getId() == this.id;
    meta.setDisplayName((has ? selected ? "§6" : "§e" : "§c") + meta.getDisplayName());
    List<String> list = new ArrayList<>();
    list.addAll(meta.getLore());
    list.addAll(Arrays.asList("", "§fPré visualização:", "§6[" + this.symbol + "] " + Role.getPrefixed(profile.getName()),
        "", has ? selected ? "§6Selecionado!" : "§eClique para selecionar!" : "§cVocê precisa atingir o nível " + this.level + "!"));
    meta.setLore(list);
    cloned.setItemMeta(meta);
    return cloned;
  }

  private static final List<PrestigeSymbol> SYMBOLS = new ArrayList<>();

  public static void setupPrestigeSymbols() {
    NConfig config =  Core.getInstance().getConfig("symbols");
    for (String key : config.getKeys()) {
      long id = config.getLong(key + ".id");
      String name = config.getString(key + ".name");
      int level = config.getInt(key + ".level");
      String symbol = config.getString(key + ".symbol");
      ItemStack icon = BukkitUtils.deserializeItemStack(config.getString(key + ".icon")
          .replace("{name}", name)
          .replace("{symbol}", symbol)
      );

      SYMBOLS.add(new PrestigeSymbol(id, name, level, symbol, icon));
    }
  }

  public static PrestigeSymbol getById(long id) {
    return SYMBOLS.stream().filter(symbol -> symbol.getId() == id).findFirst().orElse(null);
  }

  public static List<PrestigeSymbol> listSymbols() {
    return SYMBOLS;
  }
}
