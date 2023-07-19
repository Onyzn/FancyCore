package tk.fancystore.noisier.bukkit.menus.profile;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.fancystore.noisier.bukkit.Core;
import tk.fancystore.noisier.bukkit.libraries.menu.PlayerMenu;
import tk.fancystore.noisier.bukkit.menus.MenuProfile;
import tk.fancystore.noisier.bukkit.menus.profile.level.MenuLevelRewards;
import tk.fancystore.noisier.bukkit.menus.profile.level.MenuPrestigeSymbols;
import tk.fancystore.noisier.bukkit.player.Profile;
import tk.fancystore.noisier.bukkit.player.level.NetworkLevel;
import tk.fancystore.noisier.bukkit.player.level.PrestigeSymbol;
import tk.fancystore.noisier.cosmetics.cosmetics.CosmeticType;
import tk.fancystore.noisier.database.data.container.LevelContainer;
import tk.fancystore.noisier.utils.BukkitUtils;
import tk.fancystore.noisier.utils.enums.EnumSound;

import java.util.stream.Collectors;

public class MenuLevels extends PlayerMenu {

  public MenuLevels(Profile profile) {
    super(profile.getPlayer(), "Níveis", 4);

    LevelContainer lc = profile.getLevelContainer();
    int index =  lc.getLevel().getIndex();

    long rewards = NetworkLevel.listLevels().stream().filter(level -> index >= level.getIndex() && !lc.isClaimed(level.getIndex())).count();
    this.setItem(12, BukkitUtils.deserializeItemStack((rewards == 0 ? "MINECART" : "STORAGE_MINECART") +
        " : 1 : name>&aRecompensas : lore>&7O sistema de níveis do servidor permite que\n&7você ganhe &6Experiência&7 ao jogar minigames\n&7e receba recompensas por cada novo nível\n&7alcançado. Neste, você terá acesso a \n&7todas as recompensas e poderá coletar suas \n&7recompensas.\n \n" + (rewards == 0 ? "§cVocê não possui recompensas para coletar" : "§eVocê possui §b" + rewards + " recompensa" + (rewards == 1 ? "" : "s") + "§e para coletar!")));

    int max = PrestigeSymbol.listSymbols().size();
    long have = PrestigeSymbol.listSymbols().stream().filter(ps -> index >= ps.getLevel()).count();
    long percentage = (have * 100) / max;
    String name = lc.getSelectedSymbol().getName();
    String color = have == max ? "&a" : have > (max / 2) ? "&7" : "&c";
    this.setItem(14, BukkitUtils.deserializeItemStack(
        "SKULL_ITEM:3 : 1 : name>&aÍcones de Prestígio : lore>&7Receba ícones por conquistar certos níveis, \n&7esses ícones irão enfeitar seu nível no \n&7chat, dando um visual mais customizado para \n&7seu nome. Neste menu você poderá selecionar \n&7seus ícones.\n \n&fDesbloqueados: " + color + have + "/" + max + " &8(" + percentage + "%)\n&fSelecionado atualmente:\n&a" + name + "\n \n&eClique para abrir! : skinvalue>eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdiNzQ3YjM3OGE0MWEwYTZlZGM4NmMwMDBmMDQwYzY5OTRhODMzMjUxMTk2YzlkNTJjMmEyMzBmOTUxNjBjYyJ9fX0="));

    this.setItem(31, BukkitUtils.deserializeItemStack("INK_SACK:1 : 1 : name>&cVoltar"));

    this.register(Core.getInstance());
    this.open();
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(this.getInventory())) {
      evt.setCancelled(true);

      if (evt.getWhoClicked().equals(this.player)) {
        Profile profile = Profile.getProfile(this.player.getName());
        if (profile == null) {
          this.player.closeInventory();
          return;
        }

        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(this.getInventory())) {
          ItemStack item = evt.getCurrentItem();

          if (item != null && item.getType() != Material.AIR) {
            if (evt.getSlot() == 31) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              new MenuProfile(profile);
            } else if (evt.getSlot() == 12) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              new MenuLevelRewards(profile);
            } else if (evt.getSlot() == 14) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              new MenuPrestigeSymbols(profile);
            }
          }
        }
      }
    }
  }

  public void cancel() {
    HandlerList.unregisterAll(this);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    if (evt.getPlayer().equals(this.player)) {
      this.cancel();
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent evt) {
    if (evt.getPlayer().equals(this.player) && evt.getInventory().equals(this.getInventory())) {
      this.cancel();
    }
  }
}