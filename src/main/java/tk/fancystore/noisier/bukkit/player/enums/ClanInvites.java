package tk.fancystore.noisier.bukkit.player.enums;

public enum ClanInvites {
  ATIVADO,
  DESATIVADO;

  private static final ClanInvites[] VALUES = values();

  public static ClanInvites getByOrdinal(long ordinal) {
    if (ordinal < 2 && ordinal > -1) {
      return VALUES[(int) ordinal];
    }

    return null;
  }

  public String getInkSack() {
    if (this == ATIVADO) {
      return "10";
    }

    return "8";
  }

  public String getName() {
    if (this == ATIVADO) {
      return "§aAtivado";
    }

    return "§cDesativado";
  }

  public ClanInvites next() {
    if (this == DESATIVADO) {
      return ATIVADO;
    }

    return DESATIVADO;
  }
}