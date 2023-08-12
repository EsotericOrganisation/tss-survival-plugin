package net.slqmy.tss_survival;

public enum MiningSkillExpReward {

  STONE(1),
  DEEPSLATE(2),
  COAL_ORE(4),
  IRON_ORE(20),
  COPPER_ORE(12),
  GOLD_ORE(50),
  REDSTONE_ORE(35),
  EMERALD_ORE(200),
  LAPIS_LAZULI_ORE(35),
  DIAMOND_ORE(350),
  NETHER_GOLD_ORE(4),
  NETHER_QUARTZ_ORE(40),
  ANCIENT_DEBRIS(700),
  OBSIDIAN(40),
  SMALL_AMETHYST_BUD(10),
  MEDIUM_AMETHYST_BUD(20),
  LARGE_AMETHYST_BUD(40),
  AMETHYST_CLUSTER(80);

  private final int expReward;

  MiningSkillExpReward(int expReward) {
    this.expReward = expReward;
  }

  public int getExpReward() {
    return expReward;
  }
}
