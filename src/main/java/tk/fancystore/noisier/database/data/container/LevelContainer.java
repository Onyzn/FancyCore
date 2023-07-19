package tk.fancystore.noisier.database.data.container;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import tk.fancystore.noisier.bukkit.player.level.NetworkLevel;
import tk.fancystore.noisier.bukkit.player.level.PrestigeSymbol;
import tk.fancystore.noisier.bukkit.titles.Title;
import tk.fancystore.noisier.database.data.DataContainer;
import tk.fancystore.noisier.database.data.interfaces.AbstractContainer;

@SuppressWarnings("unchecked")
public class LevelContainer extends AbstractContainer {

  public LevelContainer(DataContainer dataContainer) {
    super(dataContainer);
  }

  public void setLevel(int level) {
    JSONObject info = this.dataContainer.getAsJsonObject();
    info.put("level", level);
    this.dataContainer.set(info.toString());
    info.clear();
  }

  public NetworkLevel getLevel() {
    JSONObject info = this.dataContainer.getAsJsonObject();
    int level = Integer.parseInt(info.get("level").toString());
    this.dataContainer.set(info.toString());
    info.clear();

    return NetworkLevel.getByLevel(level);
  }

  public void setExp(int exp) {
    JSONObject info = this.dataContainer.getAsJsonObject();
    info.put("exp", exp);
    this.dataContainer.set(info.toString());
    info.clear();
  }

  public int getExp() {
    JSONObject info = this.dataContainer.getAsJsonObject();
    int exp = Integer.parseInt(info.get("exp").toString());
    this.dataContainer.set(info.toString());
    info.clear();

    return exp;
  }

  public void claimReward(long level) {
    JSONObject info = this.dataContainer.getAsJsonObject();
    JSONArray claimed = (JSONArray) info.get("claimed");
    claimed.add(level);
    this.dataContainer.set(info.toString());
    claimed.clear();
    info.clear();
  }

  public boolean isClaimed(long level) {
    JSONObject info = this.dataContainer.getAsJsonObject();
    JSONArray claimed = (JSONArray) info.get("claimed");
    boolean isClaimed = claimed.contains(level);
    this.dataContainer.set(info.toString());
    claimed.clear();
    info.clear();

    return isClaimed;
  }

  public PrestigeSymbol getSelectedSymbol() {
    JSONObject info = this.dataContainer.getAsJsonObject();
    long id = (long) info.get("symbol");
    this.dataContainer.set(info.toString());
    info.clear();
    return PrestigeSymbol.getById(id);
  }

  public void setSelectedSymbol(long id) {
    JSONObject info = this.dataContainer.getAsJsonObject();
    info.put("symbol", id);
    this.dataContainer.set(info.toString());
    info.clear();
  }
}
