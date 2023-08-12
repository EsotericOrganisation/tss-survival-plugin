package net.slqmy.tss_survival;

public enum SkillExpReward {

  STONE(1),
  COAL_ORE(2),
  IRON_ORE(5),
  COPPER_ORE(3),
  GOLD_ORE(10),
  REDSTONE_ORE(8),
  EMERALD_ORE(20),
  LAPIS_LAZULI_ORE(15),
  DIAMOND_ORE(35),
  NETHER_GOLD_ORE(6),
  NETHER_QUARTZ_ORE(8);

  private final int expReward;

  SkillExpReward(int expReward) {
	this.expReward = expReward;
  }

  public int getExpReward() {
	return expReward;
  }
}
