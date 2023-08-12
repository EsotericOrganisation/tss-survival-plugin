package net.slqmy.tss_survival;

public enum FarmingSkillExpReward {

  WHEAT(1),
  POTATO(5),
  CARROT(5),
  BEETROOT(10),
  MELON(20),
  PUMPKIN(20),
  PITCHER_PLANT(50),
  TORCHFLOWER(35);

  private final int expReward;

  FarmingSkillExpReward(int expReward) {
  	this.expReward = expReward;
  }

  public int getExpReward() {
	return expReward;
  }
}
