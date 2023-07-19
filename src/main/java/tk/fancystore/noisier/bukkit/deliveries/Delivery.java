package tk.fancystore.noisier.bukkit.deliveries;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tk.fancystore.noisier.bukkit.Core;
import tk.fancystore.noisier.bukkit.player.Profile;
import tk.fancystore.noisier.plugin.config.NConfig;
import tk.fancystore.noisier.role.Role;
import tk.fancystore.noisier.utils.BukkitUtils;
import tk.fancystore.noisier.utils.StringUtils;
import tk.fancystore.noisier.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Delivery {

  private static final List<Delivery> DELIVERIES = new ArrayList<>();
  private final long id;
  private final int days;
  private final int slot;
  private final String permission;
  private final List<DeliveryReward> rewards;
  private final String icon;
  private final String message;

  public Delivery(int days, int slot, String permission, List<DeliveryReward> rewards, String icon, String message) {
    this.id = DELIVERIES.size();
    this.days = days;
    this.slot = slot;
    this.permission = permission;
    this.rewards = rewards;
    this.icon = icon;
    this.message = StringUtils.formatColors(message);
  }

  public static void setupDeliveries() {
    NConfig config = Core.getInstance().getConfig("deliveries");

    for (String key : config.getKeys("deliveries")) {
      int slot = config.getInt("deliveries." + key + ".slot");
      int days = config.getInt("deliveries." + key + ".days");
      String permission = config.getString("deliveries." + key + ".permission");
      String icon = config.getString("deliveries." + key + ".icon");
      String message = config.getString("deliveries." + key + ".message");
      List<DeliveryReward> rewards = new ArrayList<>();
      for (String reward : config.getStringList("deliveries." + key + ".rewards")) {
        rewards.add(new DeliveryReward(reward));
      }

      DELIVERIES.add(new Delivery(days, slot, permission, rewards, icon, message));
    }
  }

  public static Collection<Delivery> listDeliveries() {
    return DELIVERIES;
  }

  public long getId() {
    return this.id;
  }

  public long getDays() {
    return TimeUnit.DAYS.toMillis(this.days);
  }

  public int getSlot() {
    return this.slot;
  }

  public boolean hasPermission(Player player) {
    return this.permission.isEmpty() || player.hasPermission(this.permission);
  }

  public long getTimeUntil(Profile profile) {
    return this.permission.isEmpty() ?
        profile.getDataContainer("FancyCoreProfile", "created").getAsLong() + 86400000 : 0;
  }

  public List<DeliveryReward> listRewards() {
    return this.rewards;
  }

  public ItemStack getIcon(Profile profile) {
    Player player = profile.getPlayer();

    String desc = "";
    boolean permission = !this.hasPermission(player);
    boolean alreadyClaimed = profile.getDeliveriesContainer().alreadyClaimed(this.id);
    boolean requireTimeUntil = false;
    if (permission) {
      Role role = Role.getRoleByPermission(this.permission);
      desc = role == null ? "\n \n&cVocê não possui permissão." : "\n \n&7Exclusivo para " + role.getName() + "&7.";
    } else if (alreadyClaimed) {
      desc = "\n \n&7Você poderá coletar novamente em &f" + TimeUtils.getTimeUntil(profile.getDeliveriesContainer().getClaimTime(this.id)) + "&7.";
    } else {
      // só poderá coletar as primeiras entregas após 24 horas do primeiro login,
      // isso irá tornar as entregas mais únicas e irá fazer o player retornar ao servidor.
      long timeUntil = getTimeUntil(profile);

      requireTimeUntil = System.currentTimeMillis() < timeUntil;
      if (requireTimeUntil) {
        desc = "\n \n&cSua conta foi criada recentemente e portanto\n&cvocê só poderá coletar essa entrega em &f" + TimeUtils.getTimeUntil(timeUntil) + "&c.";
      } else {
        desc = "\n \n&eClique para coletar!";
      }
    }

    ItemStack item = BukkitUtils.deserializeItemStack(this.icon.replace("{color}", permission || alreadyClaimed || requireTimeUntil ? "&c" : "&a") + desc);
    if (!permission && alreadyClaimed) {
      if (item.getType() == Material.STORAGE_MINECART) {
        item.setType(Material.MINECART);
        item.setDurability((short) 0);
      } else if (item.getType() == Material.POTION) {
        item.setType(Material.GLASS_BOTTLE);
        item.setDurability((short) 0);
      }
    }

    return item;
  }

  public String getMessage() {
    return this.message;
  }
}
