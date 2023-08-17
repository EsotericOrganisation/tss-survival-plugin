package net.slqmy.tss_survival.listener;

import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BlockListener implements Listener {

  private final TSSSurvivalPlugin plugin;

  private final List<Material> placeableBlocks = Arrays.asList(Material.AZURE_BLUET, Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.CRIMSON_ROOTS, Material.WITHER_ROSE, Material.CACTUS, Material.SUGAR_CANE, Material.BAMBOO, Material.SPORE_BLOSSOM, Material.LILY_OF_THE_VALLEY, Material.CORNFLOWER, Material.CHORUS_PLANT, Material.LILAC, Material.SUNFLOWER, Material.TALL_GRASS, Material.LARGE_FERN, Material.VINE, Material.TWISTING_VINES, Material.WEEPING_VINES, Material.NETHER_SPROUTS, Material.WARPED_ROOTS, Material.FROGSPAWN, Material.HANGING_ROOTS, Material.GLOW_LICHEN, Material.CHORUS_FLOWER, Material.OAK_BUTTON, Material.OAK_PRESSURE_PLATE, Material.POLISHED_BLACKSTONE_BUTTON, Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.SHULKER_BOX, Material.WHITE_BED, Material.CANDLE, Material.WHITE_BANNER, Material.MOSS_CARPET, Material.POINTED_DRIPSTONE, Material.SNOW, Material.OAK_SAPLING, Material.AZALEA, Material.FLOWERING_AZALEA, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS, Material.GRASS, Material.FERN, Material.DEAD_BUSH, Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.DEAD_BUBBLE_CORAL_FAN, Material.DEAD_BRAIN_CORAL_FAN, Material.DEAD_TUBE_CORAL_FAN, Material.HORN_CORAL_FAN, Material.FIRE_CORAL_FAN, Material.BUBBLE_CORAL_FAN, Material.SPONGE, Material.WET_SPONGE, Material.SCULK_VEIN, Material.COBWEB, Material.TORCH, Material.LANTERN, Material.LADDER, Material.CONDUIT, Material.LODESTONE, Material.SCAFFOLDING, Material.ARMOR_STAND, Material.FLOWER_POT, Material.ITEM_FRAME, Material.GLOW_ITEM_FRAME, Material.PAINTING, Material.OAK_SIGN, Material.PLAYER_HEAD, Material.DRAGON_EGG, Material.REDSTONE, Material.LEVER, Material.STRING, Material.GLOW_BERRIES, Material.SWEET_BERRIES, Material.NETHER_WART, Material.BEETROOT_SEEDS, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Material.COCOA_BEANS, Material.WHEAT_SEEDS, Material.TURTLE_EGG, Material.KELP, Material.SEA_PICKLE, Material.SEAGRASS, Material.LILY_PAD, Material.BUBBLE_CORAL, Material.BRAIN_CORAL, Material.TUBE_CORAL, Material.BRAIN_CORAL_FAN, Material.TUBE_CORAL_FAN, Material.DEAD_TUBE_CORAL, Material.DEAD_HORN_CORAL, Material.DEAD_FIRE_CORAL, Material.DEAD_BUBBLE_CORAL, Material.DEAD_BRAIN_CORAL, Material.HORN_CORAL, Material.FIRE_CORAL, Material.DEAD_HORN_CORAL_FAN, Material.DEAD_FIRE_CORAL_FAN, Material.PUFFERFISH_BUCKET, Material.WATER_BUCKET, Material.OAK_CHEST_BOAT, Material.SALMON_BUCKET, Material.COD_BUCKET, Material.TROPICAL_FISH_BUCKET, Material.AXOLOTL_BUCKET, Material.TADPOLE_BUCKET, Material.LAVA_BUCKET, Material.POWDER_SNOW_BUCKET, Material.FLINT_AND_STEEL, Material.FIRE_CHARGE, Material.BONE_MEAL, Material.OAK_BOAT, Material.CAKE, Material.COMPARATOR, Material.MINECART, Material.REPEATER, Material.TRIPWIRE_HOOK, Material.ROSE_BUSH, Material.PEONY, Material.BIG_DRIPLEAF, Material.SMALL_DRIPLEAF, Material.RAIL, Material.OXEYE_DAISY, Material.PUFFERFISH_BUCKET, Material.WATER_BUCKET, Material.OAK_CHEST_BOAT, Material.SALMON_BUCKET, Material.COD_BUCKET, Material.TROPICAL_FISH_BUCKET, Material.AXOLOTL_BUCKET, Material.TADPOLE_BUCKET, Material.LAVA_BUCKET, Material.POWDER_SNOW_BUCKET, Material.FLINT_AND_STEEL, Material.FIRE_CHARGE, Material.BONE_MEAL, Material.OAK_BOAT, Material.CAKE, Material.COMPARATOR, Material.MINECART, Material.REPEATER, Material.TRIPWIRE_HOOK, Material.ROSE_BUSH, Material.PEONY, Material.BIG_DRIPLEAF, Material.SMALL_DRIPLEAF, Material.RAIL, Material.OXEYE_DAISY, Material.WATER, Material.LAVA, Material.POWDER_SNOW, Material.FIRE, Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL, Material.ZOMBIE_HEAD, Material.CREEPER_HEAD, Material.PIGLIN_HEAD, Material.OAK_SAPLING, Material.SPRUCE_SAPLING, Material.BIRCH_SAPLING, Material.JUNGLE_SAPLING, Material.ACACIA_SAPLING, Material.CHERRY_SAPLING, Material.DARK_OAK_SAPLING, Material.BEDROCK, Material.SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.BLACK_SHULKER_BOX, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.RAIL, Material.ACTIVATOR_RAIL, Material.OAK_SIGN, Material.SPRUCE_SIGN, Material.BIRCH_SIGN, Material.JUNGLE_SIGN, Material.ACACIA_SIGN, Material.CHERRY_SIGN, Material.DARK_OAK_SIGN, Material.MANGROVE_SIGN, Material.BAMBOO_SIGN, Material.CRIMSON_SIGN, Material.WARPED_SIGN, Material.OAK_HANGING_SIGN, Material.SPRUCE_HANGING_SIGN, Material.BIRCH_HANGING_SIGN, Material.JUNGLE_HANGING_SIGN, Material.ACACIA_HANGING_SIGN, Material.CHERRY_HANGING_SIGN, Material.DARK_OAK_HANGING_SIGN, Material.MANGROVE_HANGING_SIGN, Material.BAMBOO_HANGING_SIGN, Material.CRIMSON_HANGING_SIGN, Material.WARPED_HANGING_SIGN, Material.WHITE_BED, Material.ORANGE_BED, Material.MAGENTA_BED, Material.LIGHT_BLUE_BED, Material.YELLOW_BED, Material.LIME_BED, Material.PINK_BED, Material.GRAY_BED, Material.LIGHT_GRAY_BED, Material.CYAN_BED, Material.PURPLE_BED, Material.BLUE_BED, Material.BROWN_BED, Material.GREEN_BED, Material.RED_BED, Material.BLACK_BED, Material.WHITE_BANNER, Material.ORANGE_BANNER, Material.MAGENTA_BANNER, Material.LIGHT_BLUE_BANNER, Material.YELLOW_BANNER, Material.LIME_BANNER, Material.PINK_BANNER, Material.GRAY_BANNER, Material.LIGHT_GRAY_BANNER, Material.CYAN_BANNER, Material.PURPLE_BANNER, Material.BLUE_BANNER, Material.BROWN_BANNER, Material.GREEN_BANNER, Material.RED_BANNER, Material.BLACK_BANNER, Material.FLOWER_BANNER_PATTERN, Material.CREEPER_BANNER_PATTERN, Material.SKULL_BANNER_PATTERN, Material.MOJANG_BANNER_PATTERN, Material.GLOBE_BANNER_PATTERN, Material.PIGLIN_BANNER_PATTERN, Material.CANDLE, Material.WHITE_CANDLE, Material.ORANGE_CANDLE, Material.MAGENTA_CANDLE, Material.LIGHT_BLUE_CANDLE, Material.YELLOW_CANDLE, Material.LIME_CANDLE, Material.PINK_CANDLE, Material.GRAY_CANDLE, Material.LIGHT_GRAY_CANDLE, Material.CYAN_CANDLE, Material.PURPLE_CANDLE, Material.BLUE_CANDLE, Material.BROWN_CANDLE, Material.GREEN_CANDLE, Material.RED_CANDLE, Material.BLACK_CANDLE, Material.OAK_WALL_SIGN, Material.SPRUCE_WALL_SIGN, Material.BIRCH_WALL_SIGN, Material.ACACIA_WALL_SIGN, Material.CHERRY_WALL_SIGN, Material.JUNGLE_WALL_SIGN, Material.DARK_OAK_WALL_SIGN, Material.MANGROVE_WALL_SIGN, Material.BAMBOO_WALL_SIGN, Material.OAK_WALL_HANGING_SIGN, Material.SPRUCE_WALL_HANGING_SIGN, Material.BIRCH_WALL_HANGING_SIGN, Material.ACACIA_WALL_HANGING_SIGN, Material.CHERRY_WALL_HANGING_SIGN, Material.JUNGLE_WALL_HANGING_SIGN, Material.DARK_OAK_WALL_HANGING_SIGN, Material.MANGROVE_WALL_HANGING_SIGN, Material.CRIMSON_WALL_HANGING_SIGN, Material.WARPED_WALL_HANGING_SIGN, Material.BAMBOO_WALL_HANGING_SIGN, Material.POTTED_OAK_SAPLING, Material.POTTED_SPRUCE_SAPLING, Material.POTTED_BIRCH_SAPLING, Material.POTTED_JUNGLE_SAPLING, Material.POTTED_ACACIA_SAPLING, Material.POTTED_CHERRY_SAPLING, Material.POTTED_DARK_OAK_SAPLING, Material.WHITE_WALL_BANNER, Material.ORANGE_WALL_BANNER, Material.MAGENTA_WALL_BANNER, Material.LIGHT_BLUE_WALL_BANNER, Material.YELLOW_WALL_BANNER, Material.LIME_WALL_BANNER, Material.PINK_WALL_BANNER, Material.GRAY_WALL_BANNER, Material.LIGHT_GRAY_WALL_BANNER, Material.CYAN_WALL_BANNER, Material.PURPLE_WALL_BANNER, Material.BLUE_WALL_BANNER, Material.BROWN_WALL_BANNER, Material.GREEN_WALL_BANNER, Material.RED_WALL_BANNER, Material.BLACK_WALL_BANNER, Material.BAMBOO_SAPLING, Material.CRIMSON_WALL_SIGN, Material.WARPED_WALL_SIGN, Material.CANDLE_CAKE, Material.WHITE_CANDLE_CAKE, Material.ORANGE_CANDLE_CAKE, Material.MAGENTA_CANDLE_CAKE, Material.LIGHT_BLUE_CANDLE_CAKE, Material.YELLOW_CANDLE_CAKE, Material.LIME_CANDLE_CAKE, Material.PINK_CANDLE_CAKE, Material.GRAY_CANDLE_CAKE, Material.LIGHT_GRAY_CANDLE_CAKE, Material.CYAN_CANDLE_CAKE, Material.PURPLE_CANDLE_CAKE, Material.BLUE_CANDLE_CAKE, Material.BROWN_CANDLE_CAKE, Material.GREEN_CANDLE_CAKE, Material.RED_CANDLE_CAKE, Material.BLACK_CANDLE_CAKE, Material.CRAFTING_TABLE);

  private final LinkedList<Material> breakableBlocks = new LinkedList<>(Arrays.asList(Material.NETHER_GOLD_ORE, Material.NETHER_QUARTZ_ORE, Material.ANCIENT_DEBRIS, Material.DEEPSLATE_DIAMOND_ORE, Material.RAW_IRON_BLOCK, Material.RAW_COPPER_BLOCK, Material.RAW_GOLD_BLOCK, Material.GLOWSTONE, Material.SMALL_AMETHYST_BUD, Material.MEDIUM_AMETHYST_BUD, Material.LARGE_AMETHYST_BUD, Material.AMETHYST_CLUSTER, Material.OAK_LEAVES, Material.MUSHROOM_STEM, Material.MANGROVE_ROOTS, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.NETHER_WART_BLOCK, Material.WARPED_WART_BLOCK, Material.SHROOMLIGHT, Material.MELON, Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.JACK_O_LANTERN, Material.HAY_BLOCK, Material.BEE_NEST, Material.HONEYCOMB_BLOCK, Material.OAK_LOG, Material.OAK_WOOD, Material.STRIPPED_OAK_LOG, Material.STRIPPED_OAK_WOOD, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.CHAIN, Material.GOLD_BLOCK, Material.NETHERITE_BLOCK, Material.DIAMOND_BLOCK, Material.LAPIS_BLOCK, Material.EMERALD_BLOCK, Material.AMETHYST_BLOCK, Material.WHITE_WOOL, Material.WHITE_CARPET, Material.CALCITE, Material.OBSIDIAN, Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE, Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE, Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_REDSTONE_ORE, Material.SCULK_SENSOR, Material.SCULK_SHRIEKER, Material.SLIME_BLOCK, Material.HONEY_BLOCK, Material.BEDROCK, Material.DIAMOND_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.LAPIS_ORE, Material.DEEPSLATE_EMERALD_ORE, Material.EMERALD_ORE, Material.INFESTED_CRACKED_STONE_BRICKS, Material.INFESTED_MOSSY_STONE_BRICKS, Material.INFESTED_STONE_BRICKS, Material.INFESTED_COBBLESTONE, Material.INFESTED_STONE, Material.CHEST, Material.BARREL, Material.SPAWNER, Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.CHERRY_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.STRIPPED_OAK_LOG, Material.STRIPPED_SPRUCE_LOG, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_CHERRY_LOG, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_MANGROVE_LOG, Material.STRIPPED_OAK_WOOD, Material.STRIPPED_SPRUCE_WOOD, Material.STRIPPED_BIRCH_WOOD, Material.STRIPPED_JUNGLE_WOOD, Material.STRIPPED_ACACIA_WOOD, Material.STRIPPED_CHERRY_WOOD, Material.STRIPPED_DARK_OAK_WOOD, Material.STRIPPED_MANGROVE_WOOD, Material.OAK_WOOD, Material.SPRUCE_WOOD, Material.BIRCH_WOOD, Material.JUNGLE_WOOD, Material.ACACIA_WOOD, Material.CHERRY_WOOD, Material.DARK_OAK_WOOD, Material.MANGROVE_WOOD, Material.OAK_LEAVES, Material.SPRUCE_LEAVES, Material.BIRCH_LEAVES, Material.JUNGLE_LEAVES, Material.ACACIA_LEAVES, Material.CHERRY_LEAVES, Material.DARK_OAK_LEAVES, Material.MANGROVE_LEAVES, Material.AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES, Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.MAGENTA_WOOL, Material.LIGHT_BLUE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL, Material.PINK_WOOL, Material.GRAY_WOOL, Material.LIGHT_GRAY_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL, Material.BROWN_WOOL, Material.GREEN_WOOL, Material.RED_WOOL, Material.BLACK_WOOL, Material.MOSS_CARPET, Material.WHITE_CARPET, Material.ORANGE_CARPET, Material.MAGENTA_CARPET, Material.LIGHT_BLUE_CARPET, Material.YELLOW_CARPET, Material.LIME_CARPET, Material.PINK_CARPET, Material.GRAY_CARPET, Material.LIGHT_GRAY_CARPET, Material.CYAN_CARPET, Material.PURPLE_CARPET, Material.BLUE_CARPET, Material.BROWN_CARPET, Material.GREEN_CARPET, Material.RED_CARPET, Material.BLACK_CARPET, Material.WOODEN_SWORD, Material.WOODEN_SHOVEL, Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.STONE, Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.DEEPSLATE, Material.COCOA_BEANS, Material.GRAVEL, Material.SAND, Material.TALL_SEAGRASS, Material.COCOA, Material.COCOA_BEANS, Material.RED_SAND));

  public BlockListener(TSSSurvivalPlugin plugin) {
	this.plugin = plugin;
	breakableBlocks.addAll(placeableBlocks);
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onBlockPlace(@NotNull BlockPlaceEvent event) {
	Block placedBlock = event.getBlockPlaced();

	Chunk chunk = event.getBlock().getChunk();
	PersistentDataContainer container = chunk.getPersistentDataContainer();

	String chunkOwnerUuid = container.get(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING);
	if (chunkOwnerUuid != null) {
	  return;
	}

	if (placeableBlocks.contains(placedBlock.getType())) {
	  return;
	}

	plugin.getCore().getMessageManager().sendMessage(event.getPlayer(), Message.CANT_PLACE_IN_WILDERNESS);
	event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onBlockBreak(@NotNull BlockBreakEvent event) {
	Block placedBlock = event.getBlock();

	Chunk chunk = event.getBlock().getChunk();
	PersistentDataContainer container = chunk.getPersistentDataContainer();

	String chunkOwnerUuid = container.get(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING);
	if (chunkOwnerUuid != null) {
	  return;
	}

	if (breakableBlocks.contains(placedBlock.getType())) {
	  return;
	}

	plugin.getCore().getMessageManager().sendMessage(event.getPlayer(), Message.CANT_BREAK_IN_WILDERNESS);
	event.setCancelled(true);
  }
}
