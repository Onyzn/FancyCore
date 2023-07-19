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
import tk.fancystore.noisier.bukkit.player.level.PrestigeSymbol;
import tk.fancystore.noisier.database.data.container.LevelContainer;
import tk.fancystore.noisier.utils.BukkitUtils;
import tk.fancystore.noisier.utils.enums.EnumSound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuPrestigeSymbols extends PagedPlayerMenu {

  private Map<ItemStack, PrestigeSymbol> symbols = new HashMap<>();

  public MenuPrestigeSymbols(Profile profile) {
    this(profile, 1);
  }

  public MenuPrestigeSymbols(Profile profile, int page) {
    super(profile.getPlayer(), "Níveis - Ícones de Prestígio", responsiveRows(0, PrestigeSymbol.listSymbols().size()));
    this.previousPage = responsiveSlot(-9);
    this.nextPage = responsiveSlot(-1);
    this.onlySlots(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);

    this.removeSlotsWith(BukkitUtils.deserializeItemStack("INK_SACK:1 : 1 : name>&cVoltar"), responsiveSlot(-5));

    List<ItemStack> items = new ArrayList<>();
    for (PrestigeSymbol symbol : PrestigeSymbol.listSymbols()) {
      ItemStack item = symbol.getIcon(profile);

      this.symbols.put(item, symbol);

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
              PrestigeSymbol symbol = this.symbols.get(item);
              if (symbol != null) {
                LevelContainer lc = profile.getLevelContainer();
                if (lc.getLevel().getIndex() >= symbol.getLevel()) {
                  if (lc.getSelectedSymbol().getId() == symbol.getId()) {
                    EnumSound.ITEM_PICKUP.play(this.player, 0.5F, 2.0F);
                  } else {
                    EnumSound.LEVEL_UP.play(this.player, 1.0F, 2.0F);
                    lc.setSelectedSymbol(symbol.getId());
                    new MenuPrestigeSymbols(profile, this.currentPage);
                  }
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
    symbols.clear();
    symbols = null;
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