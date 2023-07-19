package tk.fancystore.noisier.bukkit.cmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.fancystore.noisier.bukkit.Core;
import tk.fancystore.noisier.bukkit.game.FakeGame;
import tk.fancystore.noisier.bukkit.game.Game;
import tk.fancystore.noisier.bukkit.player.Profile;

public class LobbyCommand extends Commands {

  public LobbyCommand() {
    super("lobby");
  }

  @Override
  public void perform(CommandSender sender, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("§cApenas jogadores podem utilizar este comando.");
      return;
    }

    Player player = (Player) sender;
    Profile profile = Profile.getProfile(player.getName());
    Game<?> game = profile.getGame();
    if (game != null && !(game instanceof FakeGame)) {
      player.sendMessage("§aConectando...");
      game.leave(profile, null);
      return;
    }

    Core.sendServer(profile, "lobby");
  }
}
