package tk.fancystore.noisier.bukkit.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.WatchdogThread;
import tk.fancystore.noisier.Manager;
import tk.fancystore.noisier.bukkit.Core;
import tk.fancystore.noisier.bukkit.fake.FakeManager;
import tk.fancystore.noisier.bukkit.menus.profile.MenuSocial;
import tk.fancystore.noisier.bukkit.nms.NMS;
import tk.fancystore.noisier.bukkit.player.Profile;
import tk.fancystore.noisier.bukkit.player.enums.ChatMention;
import tk.fancystore.noisier.bukkit.player.enums.ChatMessages;
import tk.fancystore.noisier.bukkit.player.enums.PrivateMessages;
import tk.fancystore.noisier.bukkit.player.enums.ProtectionLobby;
import tk.fancystore.noisier.bukkit.player.hotbar.HotbarButton;
import tk.fancystore.noisier.bukkit.titles.TitleManager;
import tk.fancystore.noisier.database.data.container.LevelContainer;
import tk.fancystore.noisier.database.data.container.PreferencesContainer;
import tk.fancystore.noisier.database.exception.ProfileLoadException;
import tk.fancystore.noisier.plugin.logger.CoreLogger;
import tk.fancystore.noisier.reflection.Accessors;
import tk.fancystore.noisier.reflection.acessors.FieldAccessor;
import tk.fancystore.noisier.role.Role;
import tk.fancystore.noisier.utils.StringUtils;
import tk.fancystore.noisier.utils.Utils;
import tk.fancystore.noisier.utils.enums.EnumSound;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Listeners implements Listener {

  public static final CoreLogger LOGGER = Core.getInstance().getCoreLogger().getModule("Listeners");

  public static final Map<String, Long> ANTI_SPAM = new HashMap<>();
  public static final Map<String, Long> DELAY_PLAYERS = new HashMap<>();
  private static final Map<String, Long> PROTECTION_LOBBY = new HashMap<>();
  public static final Map<String, String> SETTING_SOCIAL = new HashMap<>();

  public static final FieldAccessor<Map> COMMAND_MAP = Accessors.getField(SimpleCommandMap.class, "knownCommands", Map.class);
  private static final SimpleCommandMap SIMPLE_COMMAND_MAP = (SimpleCommandMap) Accessors.getMethod(Bukkit.getServer().getClass(), "getCommandMap").invoke(Bukkit.getServer());
  private static final FieldAccessor<WatchdogThread> RESTART_WATCHDOG = Accessors.getField(WatchdogThread.class, "instance", WatchdogThread.class);
  private static final FieldAccessor<Boolean> RESTART_WATCHDOG_STOPPING = Accessors.getField(WatchdogThread.class, "stopping", boolean.class);

  public static void setupListeners() {
    Bukkit.getPluginManager().registerEvents(new Listeners(), Core.getInstance());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent evt) {
    if (evt.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
      try {
        Profile.createOrLoadProfile(evt.getName());
      } catch (ProfileLoadException ex) {
        LOGGER.log(Level.SEVERE, "Nao foi possível carregar os dados do perfil \"" + evt.getName() + "\": ", ex);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerLoginMonitor(PlayerLoginEvent evt) {
    Profile profile = Profile.getProfile(evt.getPlayer().getName());
    if (profile == null) {
      evt.disallow(PlayerLoginEvent.Result.KICK_OTHER,
          "§cAparentemente o servidor não conseguiu carregar seu Perfil.\n \n§cIsso ocorre normalmente quando o servidor ainda está despreparado para receber logins, aguarde um pouco e tente novamente.");
      return;
    }

    profile.setPlayer(evt.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent evt) {
    Player player = evt.getPlayer();
    //Profile.getProfile(player.getName()).save("FancyCoreProfile");


    /*if (player.hasPermission("fancycore.admin")) {
      if (FancyUpdater.UPDATER != null && FancyUpdater.UPDATER.canDownload) {
        TextComponent component = new TextComponent("");
        for (BaseComponent components : TextComponent.fromLegacyText("\n §6§lATUALIZAÇÃO \n \n §7Foi encontrado um update novo do §6FancyCore §f(" +
            FancyUpdater.getVersion(2) + ")§7, para atualizar basta clicar ")) {
          component.addExtra(components);
        }
        TextComponent click = new TextComponent("AQUI");
        click.setColor(ChatColor.GREEN);
        click.setBold(true);
        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fc atualizar"));
        click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para atualizar o FancyCore.")));
        component.addExtra(click);
        for (BaseComponent components : TextComponent.fromLegacyText("§7.\n ")) {
          component.addExtra(components);
        }

        player.spigot().sendMessage(component);
        EnumSound.LEVEL_UP.play(player, 1.0F, 1.0F);
      }
    }*/
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent evt) {
    Profile profile = Profile.unloadProfile(evt.getPlayer().getName());
    if (profile != null) {
      if (profile.getGame() != null) {
        profile.getGame().leave(profile, profile.getGame());
      }
      TitleManager.leaveServer(profile);
      if (!((CraftServer) Bukkit.getServer()).getHandle().getServer().isRunning() || RESTART_WATCHDOG_STOPPING.get(RESTART_WATCHDOG.get(null))) {
        // server stopped - save SYNC
        profile.saveSync();
        Core.getInstance().getLogger().info("O perfil " + profile.getName() + " foi salvo!");
      } else {
        // server running - save ASYNC
        profile.save();
      }
      profile.destroy();
    }

    FakeManager.fakeNames.remove(evt.getPlayer().getName());
    FakeManager.fakeRoles.remove(evt.getPlayer().getName());
    FakeManager.fakeSkins.remove(evt.getPlayer().getName());
    ANTI_SPAM.remove(evt.getPlayer().getName());
    DELAY_PLAYERS.remove(evt.getPlayer().getName());
    PROTECTION_LOBBY.remove(evt.getPlayer().getName().toLowerCase());
    SETTING_SOCIAL.remove(evt.getPlayer().getName());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onAsyncPlayerChat(AsyncPlayerChatEvent evt) {
    if (evt.isCancelled()) {
      return;
    }

    evt.setCancelled(true);

    Player player = evt.getPlayer();

    if (evt.getMessage().equals("event:cancel_social_setting")) {
      if (SETTING_SOCIAL.containsKey(player.getName())) {
        SETTING_SOCIAL.remove(player.getName());
        player.sendMessage("§cA operação foi cancelada.");
        return;
      }
      return;
    }

    if (SETTING_SOCIAL.containsKey(player.getName())) {
      String id = SETTING_SOCIAL.remove(player.getName());

      String message = evt.getMessage();

      if (message.length() > 32) {
        player.sendMessage("§cMuito grande, informe apenas o seu nome de usuário!");
        return;
      }

      if (id.equals("dc") && !Utils.isValidDiscord(message)) {
        player.sendMessage("§cNome de usuário inválido, tente seguir o padrão §fnome#0000§c!");
        return;
      }

      player.sendMessage("§aRede social setada com sucesso!");
      Profile.getProfile(player.getName()).getSocialContainer().set(id, message);
      return;
    }

    String format = String.format(evt.getFormat(), player.getName(), evt.getMessage());
    String current = Manager.getCurrent(player.getName());
    Role role = Role.getPlayerRole(player);
    LevelContainer lc = Profile.getProfile(player.getName()).getLevelContainer();

    TextComponent component = StringUtils.toTextComponent(format + "</actions>hover:show_text>" + StringUtils.getLastColor(role.getPrefix()) + current + "\n§fNível: §b" + lc.getSelectedSymbol().getSymbol() + lc.getLevel().getIndex() + "\n§fGrupo: " + role.getName() + "\n \n§eClique para enviar uma mensagem privada.</and>click:suggest_command>/tell " + current + " ");

    evt.getRecipients().forEach(players -> {
      if (players != null) {
        String name = players.getName();
        PreferencesContainer pc = Profile.getProfile(name).getPreferencesContainer();

        if (pc.getChatMessages() == ChatMessages.ATIVADO) {
          if (players != player && pc.getChatMention() == ChatMention.ATIVADO && format.contains(name)) {
            NMS.sendActionBar(players, Role.getColored(player.getName()) + " §emencionou você no chat!");
            EnumSound.ORB_PICKUP.play(players, 1F, 1F);

            players.spigot().sendMessage(StringUtils.toTextComponent(format.replaceFirst(name, "§e" + name + StringUtils.getAllColors((format).split(name)[0]))  + "</actions>hover:show_text>" + StringUtils.getLastColor(role.getPrefix()) + current + "\n§fGrupo: " + role.getName() + "\n \n§eClique para enviar uma mensagem privada.</and>click:suggest_command>/tell " + current + " "));
            return;
          }

          players.spigot().sendMessage(component);
        }
      }
    });
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent evt) {
    if (evt.isCancelled()) {
      return;
    }

    Player player = evt.getPlayer();
    Profile profile = Profile.getProfile(player.getName());

    if (profile != null) {
      String[] args = evt.getMessage().replace("/", "").split(" ");

      if (args.length > 0) {
        String command = args[0];
        if (COMMAND_MAP.get(SIMPLE_COMMAND_MAP).containsKey("lobby") && command.equals("lobby") && profile.getPreferencesContainer()
            .getProtectionLobby() == ProtectionLobby.ATIVADO) {
          long last = PROTECTION_LOBBY.getOrDefault(player.getName().toLowerCase(), 0L);
          if (last > System.currentTimeMillis()) {
            PROTECTION_LOBBY.remove(player.getName().toLowerCase());
            return;
          }

          evt.setCancelled(true);
          PROTECTION_LOBBY.put(player.getName().toLowerCase(), System.currentTimeMillis() + 3000);
          player.sendMessage("§aVocê tem certeza? Utilize /lobby novamente para voltar ao lobby.");
        } else if (COMMAND_MAP.get(SIMPLE_COMMAND_MAP).containsKey("tell") && args.length > 1 && command.equals("tell") && !args[1].equalsIgnoreCase(player.getName())) {
          profile = Profile.getProfile(args[1]);
          if (profile != null && profile.getPreferencesContainer().getPrivateMessages() != PrivateMessages.TODOS) {
            evt.setCancelled(true);
            player.sendMessage("§cEste usuário desativou as mensagens privadas.");
          }
        }
      }
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent evt) {
    Player player = evt.getPlayer();
    Profile profile = Profile.getProfile(player.getName());

    if (profile != null && profile.getHotbar() != null) {
      ItemStack item = player.getItemInHand();
      if (evt.getAction().name().contains("CLICK") && item != null && item.hasItemMeta()) {
        HotbarButton button = profile.getHotbar().compareButton(player, item);
        if (button != null) {
          evt.setCancelled(true);
          button.getAction().execute(profile);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent evt) {
    if (evt.getRightClicked() instanceof ArmorStand) {
      if (evt.getPlayer().getGameMode() == GameMode.ADVENTURE) {
        evt.setCancelled(true);
      }
    } else if ((evt.getRightClicked() instanceof Player) && evt.getPlayer().isSneaking()) {
      Profile target = Profile.getProfile(evt.getRightClicked().getName());

      if (target != null && target.getGame() == null) {
        new MenuSocial(Profile.getProfile(evt.getPlayer().getName()), target);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getWhoClicked() instanceof Player) {
      Player player = (Player) evt.getWhoClicked();
      Profile profile = Profile.getProfile(player.getName());

      if (profile != null && profile.getHotbar() != null) {
        ItemStack item = evt.getCurrentItem();
        if (item != null && item.getType() != Material.AIR) {
          if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(player.getInventory()) && item.hasItemMeta()) {
            HotbarButton button = profile.getHotbar().compareButton(player, item);
            if (button != null) {
              evt.setCancelled(true);
              button.getAction().execute(profile);
            }
          }
        }
      }
    }
  }
}
