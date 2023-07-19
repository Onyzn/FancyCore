package tk.fancystore.noisier.bukkit.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.fancystore.noisier.bukkit.Core;

public class CoreCommand extends Commands {

  public CoreCommand() {
    super("fancycore", "fc");
  }

  @Override
  public void perform(CommandSender sender, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;

      if (!player.hasPermission("fancycore.admin")) {
        player.sendMessage("§6FancyCore §bv" + Core.getInstance().getDescription().getVersion() + " §7Criado por §6Noisier§7.");
        return;
      }

      if (args.length == 0) {
        player.sendMessage(" \n§6/fc update §f- §7Atualizar o FancyCore.\n ");
        return;
      }

      String action = args[0];
      if (action.equalsIgnoreCase("update")) {
        player.sendMessage("§aO plugin já se encontra em sua última versão.");
      } else {
        player.sendMessage(" \n§6/fc update §f- §7Atualizar o FancyCore.\n ");
      }
    } else {
      sender.sendMessage("§cApenas jogadores podem utilizar este comando.");
    }
  }
}
