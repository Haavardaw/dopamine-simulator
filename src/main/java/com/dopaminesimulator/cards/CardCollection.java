/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.dopaminesimulator.cards;

import lombok.Getter;

import com.dopaminesimulator.core.DopamineState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class CardCollection
{
	public static final double BONUS_PER_COLLECTION = 0.07d;

	/** What one trip round again is permanently worth. */
	public static final double BONUS_PER_ASCENSION = 0.05d;

	public static final int[] TIER_STARS = {1, 3, 6, 10};

	public static final String[] TIER_NAMES = {"Bronze", "Silver", "Gold", "Diamond"};
	private static final List<CardCollection> ALL = new ArrayList<>();
	private static final Map<String, List<CardCollection>> BY_CARD = new LinkedHashMap<>();
	private final String name;
	private final CardSet set;
	private final String description;
	private final List<Card> cards;
	private CardCollection(String name, CardSet set, String description, List<Cards> members)
	{
		this.name = name;
		this.set = set;
		this.description = description;
		List<Card> resolved = new ArrayList<>(members.size());
		for (Cards member : members)
		{
			if (member.getSet() != set)
			{
				throw new IllegalStateException(name + " lists " + member
					+ ", which is in " + member.getSet() + " rather than " + set);
			}
			resolved.add(member.getCard());
		}
		this.cards = Collections.unmodifiableList(resolved);
	}

	static
	{
		// the diary sets group themselves: one collection per area, holding whichever
		// tiers exist for it
		for (Cards entry : Cards.values())
		{
			if (entry.getSet() != CardSet.DIARIES)
			{
				continue;
			}
			String area = areaOf(entry.getDisplayName());
			if (area == null || contains(area + " Diaries"))
			{
				continue;
			}
			List<Cards> tiers = new ArrayList<>();
			for (Cards sibling : Cards.values())
			{
				if (sibling.getSet() == CardSet.DIARIES
					&& area.equals(areaOf(sibling.getDisplayName())))
				{
					tiers.add(sibling);
				}
			}
			define(area + " Diaries", CardSet.DIARIES, "All four difficulty tiers.", tiers);
		}
		define("The Elf Saga", CardSet.QUESTS, "The elf quest series.", Cards.PLAGUE_CITY,
			Cards.BIOHAZARD, Cards.UNDERGROUND_PASS, Cards.REGICIDE, Cards.ROVING_ELVES,
			Cards.MOURNING_S_END_PART_I, Cards.MOURNING_S_END_PART_II, Cards.SONG_OF_THE_ELVES);
		define("The Myreque", CardSet.QUESTS, "The Myreque quest series.",
			Cards.IN_SEARCH_OF_THE_MYREQUE, Cards.IN_AID_OF_THE_MYREQUE, Cards.DARKNESS_OF_HALLOWVALE,
			Cards.A_TASTE_OF_HOPE, Cards.SINS_OF_THE_FATHER, Cards.THE_BLOOD_MOON_RISES);
		define("The Mahjarrat", CardSet.QUESTS, "Quests involving the Mahjarrat.",
			Cards.DESERT_TREASURE_I, Cards.DESERT_TREASURE_II_THE_FALLEN_EMPIRE, Cards.THE_CURSE_OF_ARRAV,
			Cards.DEFENDER_OF_VARROCK, Cards.WHILE_GUTHIX_SLEEPS, Cards.SECRETS_OF_THE_NORTH);
		define("Gnome Business", CardSet.QUESTS, "The gnome quest series.", Cards.TREE_GNOME_VILLAGE,
			Cards.THE_GRAND_TREE, Cards.THE_EYES_OF_GLOUPHRIE, Cards.THE_PATH_OF_GLOUPHRIE,
			Cards.MONKEY_MADNESS_I, Cards.MONKEY_MADNESS_II);
		define("Fremennik Trials", CardSet.QUESTS, "The Fremennik quest series.",
			Cards.THE_FREMENNIK_TRIALS, Cards.THE_FREMENNIK_ISLES, Cards.THE_FREMENNIK_EXILES);

		define("Dragon Slayers", CardSet.QUESTS, "Both Dragon Slayer quests.", Cards.DRAGON_SLAYER_I,
			Cards.DRAGON_SLAYER_II);
		define("Pirate Tales", CardSet.QUESTS, "The pirate quest series.", Cards.PIRATE_S_TREASURE,
			Cards.RUM_DEAL, Cards.CABIN_FEVER, Cards.THE_GREAT_BRAIN_ROBBERY);
		define("Great Kourend", CardSet.QUESTS, "The Great Kourend quest series.",
			Cards.CLIENT_OF_KOUREND, Cards.THE_QUEEN_OF_THIEVES, Cards.THE_DEPTHS_OF_DESPAIR,
			Cards.TALE_OF_THE_RIGHTEOUS, Cards.THE_FORSAKEN_TOWER, Cards.THE_ASCENT_OF_ARCEUUS,
			Cards.A_KINGDOM_DIVIDED);
		define("Varlamore", CardSet.QUESTS, "The Varlamore quest series.", Cards.CHILDREN_OF_THE_SUN,
			Cards.TWILIGHT_S_PROMISE, Cards.PERILOUS_MOONS, Cards.THE_FINAL_DAWN);

		define("Recipe for Disaster", CardSet.QUESTS, "Recipe for Disaster and its ten subquests.",
			Cards.RECIPE_FOR_DISASTER, Cards.RECIPE_FOR_DISASTER_ANOTHER_COOK_S_QUEST,
			Cards.RECIPE_FOR_DISASTER_MOUNTAIN_DWARF, Cards.RECIPE_FOR_DISASTER_WARTFACE_BENTNOZE,
			Cards.RECIPE_FOR_DISASTER_PIRATE_PETE, Cards.RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE,
			Cards.RECIPE_FOR_DISASTER_EVIL_DAVE, Cards.RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE,
			Cards.RECIPE_FOR_DISASTER_SIR_AMIK_VARZE, Cards.RECIPE_FOR_DISASTER_KING_AWOWOGEI,
			Cards.RECIPE_FOR_DISASTER_CULINAROMANCER);
		define("Combat Skills", CardSet.SKILLS, "The seven combat skills.", Cards.ATTACK,
			Cards.STRENGTH, Cards.DEFENCE, Cards.HITPOINTS, Cards.RANGED, Cards.MAGIC, Cards.PRAYER);
		define("Gathering Skills", CardSet.SKILLS, "The five gathering skills.", Cards.MINING,
			Cards.FISHING, Cards.WOODCUTTING, Cards.FARMING, Cards.HUNTER);
		define("Artisan Skills", CardSet.SKILLS, "The eight artisan skills.", Cards.SMITHING,
			Cards.CRAFTING, Cards.FLETCHING, Cards.COOKING, Cards.FIREMAKING, Cards.HERBLORE,
			Cards.CONSTRUCTION, Cards.RUNECRAFT);
		define("Support Skills", CardSet.SKILLS, "The three support skills.", Cards.AGILITY,
			Cards.THIEVING, Cards.SLAYER);
		define("God Wars Dungeon", CardSet.BOSSES, "The four generals and Nex.",
			Cards.GENERAL_GRAARDOR, Cards.K_RIL_TSUTSAROTH, Cards.COMMANDER_ZILYANA, Cards.KREE_ARRA,
			Cards.NEX);
		define("Dagannoth Kings", CardSet.BOSSES, "All three Dagannoth Kings.", Cards.DAGANNOTH_REX,
			Cards.DAGANNOTH_PRIME, Cards.DAGANNOTH_SUPREME);
		define("Wilderness Bosses", CardSet.BOSSES, "Bosses found in the Wilderness.", Cards.CALLISTO,
			Cards.VET_ION, Cards.VENENATIS, Cards.CHAOS_ELEMENTAL, Cards.CHAOS_FANATIC,
			Cards.CRAZY_ARCHAEOLOGIST, Cards.SCORPIA, Cards.KING_BLACK_DRAGON);
		define("The Desert Awakening", CardSet.BOSSES, "The four Desert Treasure II bosses.",
			Cards.DUKE_SUCELLUS, Cards.THE_LEVIATHAN, Cards.THE_WHISPERER, Cards.VARDORVIS);
		define("Raids", CardSet.BOSSES, "All three raids.", Cards.CHAMBERS_OF_XERIC,
			Cards.CHAMBERS_OF_XERIC_CHALLENGE_MODE, Cards.THEATRE_OF_BLOOD, Cards.TOMBS_OF_AMASCUT);
		define("Slayer Bosses", CardSet.BOSSES, "Bosses that appear as Slayer tasks.",
			Cards.ABYSSAL_SIRE, Cards.CERBERUS, Cards.KRAKEN, Cards.THERMONUCLEAR_SMOKE_DEVIL,
			Cards.ALCHEMICAL_HYDRA, Cards.GROTESQUE_GUARDIANS);
		define("Skilling Bosses", CardSet.BOSSES, "Bosses trained as skilling activities.",
			Cards.TEMPOROSS, Cards.WINTERTODT, Cards.ZALCANO, Cards.HESPORI);
		define("Low-Level Slayer", CardSet.SLAYER, "Slayer tasks up to level 52.", Cards.CRAWLING_HAND,
			Cards.BANSHEE, Cards.ROCKSLUG, Cards.COCKATRICE, Cards.PYREFIEND, Cards.BASILISK,
			Cards.INFERNAL_MAGE, Cards.BLOODVELD, Cards.JELLY);
		define("Mid-Level Slayer", CardSet.SLAYER, "Slayer tasks from level 55 to 80.", Cards.TUROTH,
			Cards.ABERRANT_SPECTRE, Cards.DUST_DEVIL, Cards.KURASK, Cards.GARGOYLE, Cards.NECHRYAEL,
			Cards.CAVE_HORROR, Cards.SKELETAL_WYVERN, Cards.WYRM);

		define("High-Level Slayer", CardSet.SLAYER, "Slayer tasks from level 77 upwards.",
			Cards.DARK_BEAST, Cards.ABYSSAL_DEMON, Cards.SMOKE_DEVIL, Cards.DRAKE, Cards.HYDRA,
			Cards.BRUTAL_BLACK_DRAGON, Cards.ARAXYTE, Cards.CAVE_KRAKEN, Cards.ANCIENT_WYVERN);

		define("The Scimitar Ladder", CardSet.ITEMS, "The scimitar tier list.", Cards.IRON_SCIMITAR,
			Cards.STEEL_SCIMITAR, Cards.MITHRIL_SCIMITAR, Cards.ADAMANT_SCIMITAR, Cards.RUNE_SCIMITAR,
			Cards.DRAGON_SCIMITAR);
		define("Every Log", CardSet.ITEMS, "Every type of logs.", Cards.LOGS, Cards.OAK_LOGS,
			Cards.WILLOW_LOGS, Cards.MAPLE_LOGS, Cards.YEW_LOGS, Cards.MAGIC_LOGS, Cards.REDWOOD_LOGS);
		define("Every Ore", CardSet.ITEMS, "Every type of ore.", Cards.COPPER_ORE, Cards.TIN_ORE,
			Cards.IRON_ORE, Cards.COAL, Cards.GOLD_ORE, Cards.MITHRIL_ORE, Cards.ADAMANTITE_ORE,
			Cards.RUNITE_ORE);
		define("Chambers of Xeric", CardSet.ITEMS, "Uniques from the Chambers of Xeric.",
			Cards.KODAI_WAND, Cards.ANCESTRAL_HAT, Cards.ANCESTRAL_ROBE_TOP, Cards.ELDER_MAUL,
			Cards.DRAGON_CLAWS, Cards.TWISTED_BUCKLER, Cards.DRAGON_HUNTER_CROSSBOW, Cards.DINH_S_BULWARK,
			Cards.DEXTEROUS_PRAYER_SCROLL, Cards.ARCANE_PRAYER_SCROLL, Cards.TWISTED_BOW);

		define("Theatre of Blood", CardSet.ITEMS, "Uniques from the Theatre of Blood.",
			Cards.GHRAZI_RAPIER, Cards.SANGUINESTI_STAFF, Cards.JUSTICIAR_FACEGUARD,
			Cards.JUSTICIAR_CHESTGUARD, Cards.AVERNIC_DEFENDER, Cards.SCYTHE_OF_VITUR);

		define("Tombs of Amascut", CardSet.ITEMS, "Uniques from the Tombs of Amascut.",
			Cards.OSMUMTEN_S_FANG, Cards.LIGHTBEARER, Cards.ELIDINIS_WARD, Cards.MASORI_MASK,
			Cards.MASORI_BODY, Cards.TUMEKEN_S_SHADOW);

		define("God Wars Uniques", CardSet.ITEMS, "Drops from the four God Wars generals.",
			Cards.ARMADYL_CHESTPLATE, Cards.ARMADYL_HELMET, Cards.ARMADYL_CROSSBOW,
			Cards.BANDOS_CHESTPLATE, Cards.BANDOS_TASSETS, Cards.SARADOMIN_SWORD, Cards.ZAMORAKIAN_SPEAR,
			Cards.STAFF_OF_THE_DEAD);

		define("The Nex Drop Table", CardSet.ITEMS, "Drops from Nex.", Cards.TORVA_FULL_HELM,
			Cards.TORVA_PLATEBODY, Cards.ZARYTE_VAMBRACES);

		define("The Nightmare", CardSet.ITEMS, "Drops from the Nightmare.", Cards.INQUISITOR_S_MACE,
			Cards.NIGHTMARE_STAFF, Cards.HARMONISED_ORB, Cards.VOLATILE_ORB, Cards.ELDRITCH_ORB);

		define("Desert Treasure II Drops", CardSet.ITEMS, "Drops from the Forgotten Four.",
			Cards.ULTOR_VESTIGE, Cards.MAGUS_VESTIGE, Cards.VENATOR_VESTIGE, Cards.BELLATOR_VESTIGE,
			Cards.AWAKENER_S_ORB, Cards.VIRTUS_MASK, Cards.VIRTUS_ROBE_TOP);

		define("Araxxor", CardSet.ITEMS, "Drops from Araxxor.", Cards.NOXIOUS_HALBERD,
			Cards.ARAXYTE_FANG, Cards.AMULET_OF_RANCOUR);

		define("Slayer Boss Drops", CardSet.ITEMS, "Uniques from bosses that appear as Slayer tasks.",
			Cards.ABYSSAL_WHIP, Cards.ABYSSAL_DAGGER, Cards.ABYSSAL_BLUDGEON, Cards.KRAKEN_TENTACLE,
			Cards.HYDRA_S_CLAW, Cards.HYDRA_TAIL, Cards.HYDRA_LEATHER, Cards.OCCULT_NECKLACE,
			Cards.SMOKE_BATTLESTAFF, Cards.GRANITE_HAMMER);

		define("Cerberus", CardSet.ITEMS,
			"The three crystals, the boots they upgrade, and the smouldering stone.",
			Cards.PRIMORDIAL_CRYSTAL, Cards.PEGASIAN_CRYSTAL, Cards.ETERNAL_CRYSTAL,
			Cards.PRIMORDIAL_BOOTS, Cards.PEGASIAN_BOOTS, Cards.ETERNAL_BOOTS, Cards.SMOULDERING_STONE);

		define("Wilderness Boss Rings", CardSet.ITEMS, "The three rings from the Wilderness bosses.",
			Cards.RING_OF_THE_GODS, Cards.TREASONOUS_RING, Cards.TYRANNICAL_RING);

		define("Zulrah", CardSet.ITEMS, "Zulrah's drops and the blowpipe they build.",
			Cards.TANZANITE_FANG, Cards.MAGIC_FANG, Cards.SERPENTINE_VISAGE, Cards.UNCUT_ONYX,
			Cards.ZULRAH_S_SCALES, Cards.TOXIC_BLOWPIPE);

		define("Dragon Equipment", CardSet.ITEMS, "Dragon gear.", Cards.DRAGON_SCIMITAR,
			Cards.DRAGON_PICKAXE, Cards.DRAGON_HARPOON, Cards.DRAGON_CLAWS, Cards.DRAGON_HUNTER_CROSSBOW);

		define("Skilling Boss Drops", CardSet.ITEMS, "Uniques from Tempoross, Wintertodt and Hespori.",
			Cards.DRAGON_HARPOON, Cards.TOME_OF_FIRE, Cards.TOME_OF_WATER);
		define("The Rune Pouch", CardSet.ITEMS, "Every rune.", Cards.AIR_RUNE, Cards.WATER_RUNE,
			Cards.EARTH_RUNE, Cards.FIRE_RUNE, Cards.MIND_RUNE, Cards.BODY_RUNE, Cards.CHAOS_RUNE,
			Cards.COSMIC_RUNE, Cards.NATURE_RUNE, Cards.LAW_RUNE, Cards.DEATH_RUNE, Cards.BLOOD_RUNE,
			Cards.SOUL_RUNE, Cards.ASTRAL_RUNE, Cards.WRATH_RUNE, Cards.MIST_RUNE, Cards.DUST_RUNE,
			Cards.MUD_RUNE, Cards.SMOKE_RUNE, Cards.STEAM_RUNE, Cards.LAVA_RUNE, Cards.SUNFIRE_RUNE,
			Cards.AETHER_RUNE);

		define("Tools", CardSet.ITEMS, "Basic skilling tools.", Cards.TINDERBOX, Cards.ROPE,
			Cards.HAMMER, Cards.CHISEL, Cards.KNIFE, Cards.SPADE);
		define("The Herb Patch", CardSet.ITEMS, "Every herb, cleaned.", Cards.GUAM_LEAF,
			Cards.MARRENTILL, Cards.TARROMIN, Cards.HARRALANDER, Cards.RANARR_WEED, Cards.TOADFLAX,
			Cards.IRIT_LEAF, Cards.AVANTOE, Cards.HUASCA, Cards.KWUARM, Cards.SNAPDRAGON, Cards.CADANTINE,
			Cards.LANTADYME, Cards.DWARF_WEED, Cards.TORSTOL);
		define("Every Catch", CardSet.ITEMS, "Raw fish across the levels.", Cards.RAW_SHRIMPS,
			Cards.RAW_ANCHOVIES, Cards.RAW_SARDINE, Cards.RAW_HERRING, Cards.RAW_MACKEREL,
			Cards.RAW_TROUT, Cards.RAW_COD, Cards.RAW_PIKE, Cards.RAW_SALMON, Cards.RAW_TUNA,
			Cards.RAW_LOBSTER, Cards.RAW_BASS, Cards.RAW_SWORDFISH, Cards.RAW_MONKFISH,
			Cards.RAW_KARAMBWAN, Cards.RAW_SHARK, Cards.RAW_SEA_TURTLE, Cards.RAW_MANTA_RAY,
			Cards.RAW_ANGLERFISH, Cards.RAW_DARK_CRAB);
		define("Combat Minigames", CardSet.MINIGAMES, "Combat minigames.", Cards.BARBARIAN_ASSAULT,
			Cards.PEST_CONTROL, Cards.CASTLE_WARS, Cards.LAST_MAN_STANDING, Cards.SOUL_WARS,
			Cards.NIGHTMARE_ZONE);
		define("Skilling Minigames", CardSet.MINIGAMES, "Skilling minigames.", Cards.BLAST_FURNACE,
			Cards.TITHE_FARM, Cards.PYRAMID_PLUNDER, Cards.BRIMHAVEN_AGILITY_ARENA, Cards.ROGUES_DEN,
			Cards.TROUBLE_BREWING, Cards.MAGE_TRAINING_ARENA, Cards.GNOME_RESTAURANT, Cards.VOLCANIC_MINE,
			Cards.GUARDIANS_OF_THE_RIFT);
		define("The Caves", CardSet.MINIGAMES, "The Fight Caves and the Inferno.", Cards.FIGHT_CAVES,
			Cards.INFERNO);
	}
	private static void define(String name, CardSet set, String description, Cards... members)
	{
		define(name, set, description, java.util.Arrays.asList(members));
	}

	private static void define(String name, CardSet set, String description, List<Cards> members)
	{
		CardCollection collection = new CardCollection(name, set, description, members);
		ALL.add(collection);
		for (Card card : collection.cards)
		{
			BY_CARD.computeIfAbsent(card.getId(), k -> new ArrayList<>()).add(collection);
		}
	}
	private static String areaOf(String diaryName)
	{
		int split = diaryName.lastIndexOf(' ');
		if (split <= 0)
		{
			return null;
		}
		String tier = diaryName.substring(split + 1);
		boolean isTier = "Easy".equals(tier) || "Medium".equals(tier)
			|| "Hard".equals(tier) || "Elite".equals(tier);

		return isTier ? diaryName.substring(0, split) : null;
	}
	private static boolean contains(String name)
	{
		for (CardCollection collection : ALL)
		{
			if (collection.name.equals(name))
			{
				return true;
			}
		}
		return false;
	}

	public static List<CardCollection> all()
	{
		return Collections.unmodifiableList(ALL);
	}

	public static List<CardCollection> inSet(CardSet set)
	{
		List<CardCollection> found = new ArrayList<>();
		for (CardCollection collection : ALL)
		{
			if (collection.set == set)
			{
				found.add(collection);
			}
		}
		return found;
	}

	public static List<CardCollection> forCard(Card card)
	{
		return BY_CARD.getOrDefault(card.getId(), Collections.emptyList());
	}

	public static double multiplierFor(DopamineState state, CardSet set)
	{
		double ascended = 0d;
		for (CardCollection collection : inSet(set))
		{
			ascended += collection.bonusFromAscension(state);
		}
		return Math.pow(1d + BONUS_PER_COLLECTION, tiersIn(state, set)) * (1d + ascended);
	}

	/**
	 * What ascending has permanently bought this collection. Kept additive so
	 * that ten ascensions is ten times one rather than something unbounded.
	 */
	public double bonusFromAscension(DopamineState state)
	{
		return state.getAscension(name) * BONUS_PER_ASCENSION;
	}
	public static int completedIn(DopamineState state, CardSet set)
	{
		int completed = 0;
		for (CardCollection collection : inSet(set))
		{
			if (collection.isComplete(state))
			{
				completed++;
			}
		}
		return completed;
	}

	public static int tiersIn(DopamineState state, CardSet set)
	{
		int tiers = 0;
		for (CardCollection collection : inSet(set))
		{
			tiers += collection.tierIn(state);
		}
		return tiers;
	}

	public static int maxTiersIn(CardSet set)
	{
		return inSet(set).size() * TIER_STARS.length;
	}

	public int size()
	{
		return cards.size();
	}
	public int ownedIn(DopamineState state)
	{
		int owned = 0;
		for (Card card : cards)
		{
			if (state.owns(card.getId()))
			{
				owned++;
			}
		}
		return owned;
	}

	/**
	 * Every card in the collection at the top of its star track.
	 *
	 * <p>The gate for taking it round again. Stricter than {@link #isComplete},
	 * which only wants one star on each.
	 */
	public boolean isMaxed(DopamineState state)
	{
		if (cards.isEmpty())
		{
			return false;
		}
		for (Card card : cards)
		{
			if (state.getStars(card.getId()) < Rarity.MAX_STARS)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * What it costs to take this collection round again, in dust.
	 *
	 * <p>Priced off what the collection is worth rather than a flat figure, so a
	 * collection of legendaries costs more to reset than one of commons, and each
	 * further ascension costs half again as much as the last.
	 */
	public long ascensionCost(DopamineState state)
	{
		long base = 0L;
		for (Card card : cards)
		{
			base += Dust.costToMax(card.getRarity());
		}
		return Math.round(base * 0.25d * Math.pow(1.5d, state.getAscension(name)));
	}

	public boolean isComplete(DopamineState state)
	{
		return tierIn(state) > 0;
	}

	public int tierIn(DopamineState state)
	{
		if (cards.isEmpty())
		{
			return 0;
		}

		int lowest = Integer.MAX_VALUE;
		for (Card card : cards)
		{
			if (!state.owns(card.getId()))
			{
				return 0;
			}
			lowest = Math.min(lowest, state.getStars(card.getId()));
		}

		int tier = 0;
		for (int threshold : TIER_STARS)
		{
			if (lowest >= threshold)
			{
				tier++;
			}
		}
		return tier;
	}

	public String tierNameIn(DopamineState state)
	{
		int tier = tierIn(state);
		return tier <= 0 ? "Incomplete" : TIER_NAMES[Math.min(tier, TIER_NAMES.length) - 1];
	}

	public int starsForNextTier(DopamineState state)
	{
		int tier = tierIn(state);
		return tier >= TIER_STARS.length ? 0 : TIER_STARS[tier];
	}


	@Override
	public String toString()
	{
		return name;
	}
}
