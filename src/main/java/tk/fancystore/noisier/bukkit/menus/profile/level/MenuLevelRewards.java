package tk.fancystore.noisier.bukkit.menus.profile.level;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import tk.fancystore.noisier.bukkit.Core;
import tk.fancystore.noisier.bukkit.libraries.menu.PagedPlayerMenu;
import tk.fancystore.noisier.bukkit.menus.profile.MenuLevels;
import tk.fancystore.noisier.bukkit.player.Profile;
import tk.fancystore.noisier.bukkit.player.level.NetworkLevel;
import tk.fancystore.noisier.database.data.container.LevelContainer;
import tk.fancystore.noisier.utils.BukkitUtils;
import tk.fancystore.noisier.utils.enums.EnumSound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuLevelRewards extends PagedPlayerMenu {

  private Map<ItemStack, NetworkLevel> levels = new HashMap<>();

  public MenuLevelRewards(Profile profile) {
    this(profile, 1);
  }

  public MenuLevelRewards(Profile profile, int page) {
    super(profile.getPlayer(), "Níveis - Recompensas", responsiveRows(0, NetworkLevel.listLevels().size()));
    this.previousPage = responsiveSlot(-9);
    this.nextPage = responsiveSlot(-1);
    this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);

    this.removeSlotsWith(BukkitUtils.deserializeItemStack("INK_SACK:1 : 1 : name>&cVoltar"), responsiveSlot(-5));

    List<ItemStack> items = new ArrayList<>();
    for (NetworkLevel level : NetworkLevel.listLevels()) {
      ItemStack item = level.getIcon(profile);

      this.levels.put(item, level);

      items.add(item);
    }

    this.setItems(items);
    items.clear();

    this.register(Core.getInstance());
    this.open(page);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(this.getCurrentInventory())) {
      evt.setCancelled(true);

      if (evt.getWhoClicked().equals(this.player)) {
        Profile profile = Profile.getProfile(this.player.getName());
        if (profile == null) {
          this.player.closeInventory();
          return;
        }

        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(this.getCurrentInventory())) {
          ItemStack item = evt.getCurrentItem();

          if (item != null && item.getType() != Material.AIR) {
            if (evt.getSlot() == this.previousPage) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              this.openPrevious();
            } else if (evt.getSlot() == this.nextPage) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              this.openNext();
            } else if (evt.getSlot() == responsiveSlot(-5)) {
              EnumSound.CLICK.play(this.player, 0.5F, 2.0F);
              new MenuLevels(profile);
            } else {
              NetworkLevel level = this.levels.get(item);
              if (level != null) {
                LevelContainer lc = profile.getLevelContainer();
                if (lc.getLevel().getIndex() >= level.getIndex() && !lc.isClaimed(level.getIndex())) {
                  EnumSound.LEVEL_UP.play(this.player, 1.0F, 2.0F);

                  lc.claimReward(level.getIndex());
                  NetworkLevel.getByLevel(level.getIndex()).getRewards().forEach(playerLevelReward ->
                      playerLevelReward.dispatch(profile));

                  new MenuLevelRewards(profile, this.currentPage);
                } else {
                  EnumSound.ENDERMAN_TELEPORT.play(this.player, 0.5F, 1.0F);
                }
              }
            }
          }
        }
      }
    }
  }

  public void cancel() {
    levels.clear();
    levels = null;
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
    if (evt.getPlayer().equals(this.player) && evt.getInventory().equals(this.getCurrentInventory())) {
      this.cancel();
    }
  }
}