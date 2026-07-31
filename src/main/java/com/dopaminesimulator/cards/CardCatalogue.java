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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.ItemID;

public final class CardCatalogue
{
	private static final List<Card> CARDS;
	private static final Map<String, Card> BY_ID;
	private static final Map<Rarity, List<Card>> BY_RARITY;
	private static final Map<CardSet, List<Card>> BY_SET;
	private static final Map<String, Integer> QUEST_ART = new HashMap<>();
	static
	{
		QUEST_ART.put("Dragon Slayer II", ItemID.MYTHICAL_CAPE);
		QUEST_ART.put("Monkey Madness II", ItemID.HEAVY_BALLISTA);
		QUEST_ART.put("Monkey Madness I", ItemID.DRAGON_SCIMITAR);
		QUEST_ART.put("Song of the Elves", ItemID.BLADE_OF_SAELDOR);
		QUEST_ART.put("Sins of the Father", ItemID.BLISTERWOOD_FLAIL);
		QUEST_ART.put("Legends' Quest", ItemID.CAPE_OF_LEGENDS);
		QUEST_ART.put("Lost City", ItemID.DRAGON_DAGGER);
		QUEST_ART.put("A Taste of Hope", ItemID.IVANDIS_FLAIL);
	}
	static
	{
		List<Card> cards = new ArrayList<>();
		addQuests(cards);
		addSkills(cards);
		addBosses(cards);
		addItems(cards);
		addMinigames(cards);
		addSlayer(cards);
		addDiaries(cards);
		CARDS = Collections.unmodifiableList(cards);
		Map<String, Card> byId = new HashMap<>();
		Map<Rarity, List<Card>> byRarity = new EnumMap<>(Rarity.class);
		Map<CardSet, List<Card>> bySet = new EnumMap<>(CardSet.class);
		for (Rarity r : Rarity.values())
		{
			byRarity.put(r, new ArrayList<>());
		}
		for (CardSet s : CardSet.values())
		{
			bySet.put(s, new ArrayList<>());
		}

		for (Card card : CARDS)
		{
			if (byId.put(card.getId(), card) != null)
			{
				throw new IllegalStateException("Duplicate card id: " + card.getId());
			}
			byRarity.get(card.getRarity()).add(card);
			bySet.get(card.getSet()).add(card);
		}
		BY_ID = Collections.unmodifiableMap(byId);
		BY_RARITY = Collections.unmodifiableMap(byRarity);
		BY_SET = Collections.unmodifiableMap(bySet);
	}

	private CardCatalogue()
	{
	}
	private static void addQuests(List<Card> out)
	{
		for (Quest quest : Quest.values())
		{
			String name = quest.getName();
			Rarity rarity = QuestDifficulty.rarityOf(quest);
			Integer art = QUEST_ART.get(name);
			out.add(art != null
				? Card.ofItem(CardSet.QUESTS, name, rarity, art)
				: Card.ofSprite(CardSet.QUESTS, name, rarity,
					SpriteID.AchievementDiaryIcons.BLUE_QUESTS));
		}
	}

	private static void addSkills(List<Card> out)
	{
		for (Skill skill : Skill.values())
		{
			int sprite = skillSprite(skill);
			if (sprite < 0)
			{
				continue;
			}
			out.add(Card.ofSprite(CardSet.SKILLS, title(skill.name()), skillRarity(skill), sprite));
		}
	}

	private static Rarity skillRarity(Skill skill)
	{
		switch (skill)
		{
			case RUNECRAFT:
				return Rarity.LEGENDARY;
			case AGILITY:
			case CONSTRUCTION:
				return Rarity.EPIC;
			case SLAYER:
			case HUNTER:
			case FARMING:
			case HERBLORE:
			case SAILING:
				return Rarity.RARE;
			case MINING:
			case FISHING:
			case THIEVING:
			case SMITHING:
			case CRAFTING:
			case FLETCHING:
				return Rarity.UNCOMMON;
			default:
				return Rarity.COMMON;
		}
	}
	private static int skillSprite(Skill skill)
	{
		switch (skill)
		{
			case ATTACK: return SpriteID.Staticons.ATTACK;
			case STRENGTH: return SpriteID.Staticons.STRENGTH;
			case DEFENCE: return SpriteID.Staticons.DEFENCE;
			case RANGED: return SpriteID.Staticons.RANGED;
			case PRAYER: return SpriteID.Staticons.PRAYER;
			case MAGIC: return SpriteID.Staticons.MAGIC;
			case HITPOINTS: return SpriteID.Staticons.HITPOINTS;
			case AGILITY: return SpriteID.Staticons.AGILITY;
			case HERBLORE: return SpriteID.Staticons.HERBLORE;
			case THIEVING: return SpriteID.Staticons.THIEVING;
			case CRAFTING: return SpriteID.Staticons.CRAFTING;
			case FLETCHING: return SpriteID.Staticons.FLETCHING;
			case MINING: return SpriteID.Staticons.MINING;
			case SMITHING: return SpriteID.Staticons.SMITHING;
			case FISHING: return SpriteID.Staticons.FISHING;
			case COOKING: return SpriteID.Staticons.COOKING;
			case FIREMAKING: return SpriteID.Staticons.FIREMAKING;
			case WOODCUTTING: return SpriteID.Staticons.WOODCUTTING;
			case RUNECRAFT: return SpriteID.Staticons2.RUNECRAFT;
			case SLAYER: return SpriteID.Staticons2.SLAYER;
			case FARMING: return SpriteID.Staticons2.FARMING;
			case HUNTER: return SpriteID.Staticons2.HUNTER;
			case CONSTRUCTION: return SpriteID.Staticons2.CONSTRUCTION;
			case SAILING: return SpriteID.Staticons2.SAILING;
			default: return -1;
		}
	}
	private static void addBosses(List<Card> out)
	{
		boss(out, "Obor", Rarity.COMMON, SpriteID.IconBoss25x25.OBOR);
		boss(out, "Bryophyta", Rarity.COMMON, SpriteID.IconBoss25x25.BRYOPHYTA);
		boss(out, "Giant Mole", Rarity.COMMON, SpriteID.IconBoss25x25.GIANT_MOLE);
		boss(out, "Tempoross", Rarity.COMMON, SpriteID.IconBoss25x25.TEMPOROSS);
		boss(out, "Wintertodt", Rarity.COMMON, SpriteID.IconBoss25x25.WINTERTODT);
		boss(out, "Barrows", Rarity.COMMON, SpriteID.IconBoss25x25.BARROWS_CHESTS);
		boss(out, "Sarachnis", Rarity.COMMON, SpriteID.IconBoss25x25.SARACHNIS);
		boss(out, "Hespori", Rarity.COMMON, SpriteID.IconBoss25x25.HESPORI);
		boss(out, "Scurrius", Rarity.COMMON, SpriteID.IconBoss25x25.SCURRIUS);
		boss(out, "Deranged Archaeologist", Rarity.COMMON, SpriteID.IconBoss25x25.DERANGED_ARCHAEOLOGIST);
		boss(out, "Chaos Fanatic", Rarity.COMMON, SpriteID.IconBoss25x25.CHAOS_FANATIC);
		boss(out, "Crazy Archaeologist", Rarity.COMMON, SpriteID.IconBoss25x25.CRAZY_ARCHAEOLOGIST);
		boss(out, "Kalphite Queen", Rarity.UNCOMMON, SpriteID.IconBoss25x25.KALPHITE_QUEEN);
		boss(out, "King Black Dragon", Rarity.UNCOMMON, SpriteID.IconBoss25x25.KING_BLACK_DRAGON);
		boss(out, "Skotizo", Rarity.UNCOMMON, SpriteID.IconBoss25x25.SKOTIZO);
		boss(out, "Zalcano", Rarity.UNCOMMON, SpriteID.IconBoss25x25.ZALCANO);
		boss(out, "Kraken", Rarity.UNCOMMON, SpriteID.IconBoss25x25.KRAKEN);
		boss(out, "Grotesque Guardians", Rarity.UNCOMMON, SpriteID.IconBoss25x25.GROTESQUE_GUARDIANS);
		boss(out, "Scorpia", Rarity.UNCOMMON, SpriteID.IconBoss25x25.SCORPIA);
		boss(out, "Chaos Elemental", Rarity.UNCOMMON, SpriteID.IconBoss25x25.CHAOS_ELEMENTAL);
		boss(out, "Dagannoth Rex", Rarity.UNCOMMON, SpriteID.IconBoss25x25.DAGANNOTH_REX);
		boss(out, "Dagannoth Prime", Rarity.UNCOMMON, SpriteID.IconBoss25x25.DAGANNOTH_PRIME);
		boss(out, "Dagannoth Supreme", Rarity.UNCOMMON, SpriteID.IconBoss25x25.DAGANNOTH_SUPREME);
		boss(out, "Mimic", Rarity.UNCOMMON, SpriteID.IconBoss25x25.MIMIC);
		boss(out, "Zulrah", Rarity.RARE, SpriteID.IconBoss25x25.ZULRAH);
		boss(out, "Vorkath", Rarity.RARE, SpriteID.IconBoss25x25.VORKATH);
		boss(out, "Abyssal Sire", Rarity.RARE, SpriteID.IconBoss25x25.ABYSSAL_SIRE);
		boss(out, "Thermonuclear Smoke Devil", Rarity.RARE, SpriteID.IconBoss25x25.THERMONUCLEAR_SMOKE_DEVIL);
		boss(out, "Cerberus", Rarity.RARE, SpriteID.IconBoss25x25.CERBERUS);
		boss(out, "General Graardor", Rarity.RARE, SpriteID.IconBoss25x25.GENERAL_GRAARDOR);
		boss(out, "K'ril Tsutsaroth", Rarity.RARE, SpriteID.IconBoss25x25.KRIL_TSUTSAROTH);
		boss(out, "Commander Zilyana", Rarity.RARE, SpriteID.IconBoss25x25.COMMANDER_ZILYANA);
		boss(out, "Kree'arra", Rarity.RARE, SpriteID.IconBoss25x25.KREEARRA);
		boss(out, "Alchemical Hydra", Rarity.RARE, SpriteID.IconBoss25x25.ALCHEMICAL_HYDRA);
		boss(out, "Callisto", Rarity.RARE, SpriteID.IconBoss25x25.ARTIO_CALLISTO);
		boss(out, "Vet'ion", Rarity.RARE, SpriteID.IconBoss25x25.CALVARION_VETION);
		boss(out, "Venenatis", Rarity.RARE, SpriteID.IconBoss25x25.SPINDEL_VENENATIS);
		boss(out, "The Gauntlet", Rarity.RARE, SpriteID.IconBoss25x25.THE_GAUNTLET);
		boss(out, "Corporeal Beast", Rarity.EPIC, SpriteID.IconBoss25x25.CORPOREAL_BEAST);
		boss(out, "The Nightmare", Rarity.EPIC, SpriteID.IconBoss25x25.NIGHTMARE);
		boss(out, "Phantom Muspah", Rarity.EPIC, SpriteID.IconBoss25x25.PHANTOM_MUSPAH);
		boss(out, "Nex", Rarity.EPIC, SpriteID.IconBoss25x25.NEX);
		boss(out, "Duke Sucellus", Rarity.EPIC, SpriteID.IconBoss25x25.DUKE_SUCELLUS);
		boss(out, "The Leviathan", Rarity.EPIC, SpriteID.IconBoss25x25.THE_LEVIATHAN);
		boss(out, "The Whisperer", Rarity.EPIC, SpriteID.IconBoss25x25.THE_WHISPERER);
		boss(out, "Vardorvis", Rarity.EPIC, SpriteID.IconBoss25x25.VARDORVIS);
		boss(out, "Araxxor", Rarity.EPIC, SpriteID.IconBoss25x25.ARAXXOR);
		boss(out, "The Corrupted Gauntlet", Rarity.EPIC, SpriteID.IconBoss25x25.THE_CORRUPTED_GAUNTLET);
		boss(out, "Chambers of Xeric", Rarity.LEGENDARY, SpriteID.IconBoss25x25.CHAMBERS_OF_XERIC);
		boss(out, "Theatre of Blood", Rarity.LEGENDARY, SpriteID.IconBoss25x25.THEATRE_OF_BLOOD);
		boss(out, "Tombs of Amascut", Rarity.LEGENDARY, SpriteID.IconBoss25x25.TOMBS_OF_AMASCUT);
		boss(out, "Yama", Rarity.LEGENDARY, SpriteID.IconBoss25x25.YAMA);
		boss(out, "Royal Titans", Rarity.LEGENDARY, SpriteID.IconBoss25x25.ROYAL_TITANS);
	}
	private static void addItems(List<Card> out)
	{
		item(out, "Logs", Rarity.COMMON, ItemID.LOGS);
		item(out, "Oak Logs", Rarity.COMMON, ItemID.OAK_LOGS);
		item(out, "Willow Logs", Rarity.COMMON, ItemID.WILLOW_LOGS);
		item(out, "Maple Logs", Rarity.COMMON, ItemID.MAPLE_LOGS);
		item(out, "Copper Ore", Rarity.COMMON, ItemID.COPPER_ORE);
		item(out, "Tin Ore", Rarity.COMMON, ItemID.TIN_ORE);
		item(out, "Iron Ore", Rarity.COMMON, ItemID.IRON_ORE);
		item(out, "Coal", Rarity.COMMON, ItemID.COAL);
		item(out, "Raw Trout", Rarity.COMMON, ItemID.RAW_TROUT);
		item(out, "Raw Salmon", Rarity.COMMON, ItemID.RAW_SALMON);
		item(out, "Bones", Rarity.COMMON, ItemID.BONES);
		item(out, "Cabbage", Rarity.COMMON, ItemID.CABBAGE);
		item(out, "Bread", Rarity.COMMON, ItemID.BREAD);
		item(out, "Tinderbox", Rarity.COMMON, ItemID.TINDERBOX);
		item(out, "Rope", Rarity.COMMON, ItemID.ROPE);
		item(out, "Hammer", Rarity.COMMON, ItemID.HAMMER);
		item(out, "Chisel", Rarity.COMMON, ItemID.CHISEL);
		item(out, "Knife", Rarity.COMMON, ItemID.KNIFE);
		item(out, "Spade", Rarity.COMMON, ItemID.SPADE);
		item(out, "Feather", Rarity.COMMON, ItemID.FEATHER);
		item(out, "Flax", Rarity.COMMON, ItemID.FLAX);
		item(out, "Grain", Rarity.COMMON, ItemID.GRAIN);
		item(out, "Bucket of Sand", Rarity.COMMON, ItemID.BUCKET_SAND);
		item(out, "Empty Vial", Rarity.COMMON, ItemID.VIAL_EMPTY);
		item(out, "Cake", Rarity.COMMON, ItemID.CAKE);
		item(out, "Bronze Dagger", Rarity.COMMON, ItemID.BRONZE_DAGGER);
		item(out, "Bronze Sword", Rarity.COMMON, ItemID.BRONZE_SWORD);
		item(out, "Guam Leaf", Rarity.COMMON, ItemID.GUAM_LEAF);
		item(out, "Big Bones", Rarity.COMMON, ItemID.BIG_BONES);
		item(out, "Yew Logs", Rarity.UNCOMMON, ItemID.YEW_LOGS);
		item(out, "Magic Logs", Rarity.UNCOMMON, ItemID.MAGIC_LOGS);
		item(out, "Redwood Logs", Rarity.UNCOMMON, ItemID.REDWOOD_LOGS);
		item(out, "Gold Ore", Rarity.UNCOMMON, ItemID.GOLD_ORE);
		item(out, "Mithril Ore", Rarity.UNCOMMON, ItemID.MITHRIL_ORE);
		item(out, "Adamantite Ore", Rarity.UNCOMMON, ItemID.ADAMANTITE_ORE);
		item(out, "Raw Lobster", Rarity.UNCOMMON, ItemID.RAW_LOBSTER);
		item(out, "Raw Swordfish", Rarity.UNCOMMON, ItemID.RAW_SWORDFISH);
		item(out, "Ranarr Weed", Rarity.UNCOMMON, ItemID.RANARR_WEED);
		item(out, "Dragon Bones", Rarity.UNCOMMON, ItemID.DRAGON_BONES);
		item(out, "Iron Scimitar", Rarity.UNCOMMON, ItemID.IRON_SCIMITAR);
		item(out, "Steel Scimitar", Rarity.UNCOMMON, ItemID.STEEL_SCIMITAR);
		item(out, "Mithril Scimitar", Rarity.UNCOMMON, ItemID.MITHRIL_SCIMITAR);
		item(out, "Adamant Scimitar", Rarity.UNCOMMON, ItemID.ADAMANT_SCIMITAR);
		item(out, "Runite Ore", Rarity.RARE, ItemID.RUNITE_ORE);
		item(out, "Raw Shark", Rarity.RARE, ItemID.RAW_SHARK);
		item(out, "Shark", Rarity.RARE, ItemID.SHARK);
		item(out, "Raw Anglerfish", Rarity.RARE, ItemID.RAW_ANGLERFISH);
		item(out, "Snapdragon", Rarity.RARE, ItemID.SNAPDRAGON);
		item(out, "Torstol", Rarity.RARE, ItemID.TORSTOL);
		item(out, "Amethyst", Rarity.RARE, ItemID.AMETHYST);
		item(out, "Rune Scimitar", Rarity.RARE, ItemID.RUNE_SCIMITAR);
		item(out, "Dragon Scimitar", Rarity.RARE, ItemID.DRAGON_SCIMITAR);
		item(out, "Granite Maul", Rarity.RARE, ItemID.GRANITE_MAUL);
		item(out, "Dragon Pickaxe", Rarity.RARE, ItemID.DRAGON_PICKAXE);
		item(out, "Occult Necklace", Rarity.RARE, ItemID.OCCULT_NECKLACE);
		item(out, "Crystal Bow", Rarity.RARE, ItemID.CRYSTAL_BOW);
		item(out, "Abyssal Whip", Rarity.EPIC, ItemID.ABYSSAL_WHIP);
		item(out, "Dragon Claws", Rarity.EPIC, ItemID.DRAGON_CLAWS);
		item(out, "Toxic Blowpipe", Rarity.EPIC, ItemID.TOXIC_BLOWPIPE);
		item(out, "Armadyl Chestplate", Rarity.EPIC, ItemID.ARMADYL_CHESTPLATE);
		item(out, "Bandos Chestplate", Rarity.EPIC, ItemID.BANDOS_CHESTPLATE);
		item(out, "Primordial Boots", Rarity.EPIC, ItemID.PRIMORDIAL_BOOTS);
		item(out, "Eternal Boots", Rarity.EPIC, ItemID.ETERNAL_BOOTS);
		item(out, "Pegasian Boots", Rarity.EPIC, ItemID.PEGASIAN_BOOTS);
		item(out, "Sanguinesti Staff", Rarity.EPIC, ItemID.SANGUINESTI_STAFF);
		item(out, "Kodai Wand", Rarity.EPIC, ItemID.KODAI_WAND);
		item(out, "Ancestral Hat", Rarity.EPIC, ItemID.ANCESTRAL_HAT);
		item(out, "Infernal Cape", Rarity.EPIC, ItemID.INFERNAL_CAPE);
		item(out, "Elder Maul", Rarity.LEGENDARY, ItemID.ELDER_MAUL);
		item(out, "Ghrazi Rapier", Rarity.LEGENDARY, ItemID.GHRAZI_RAPIER);
		item(out, "Inquisitor's Mace", Rarity.LEGENDARY, ItemID.INQUISITORS_MACE);
		item(out, "Voidwaker", Rarity.LEGENDARY, ItemID.VOIDWAKER);
		item(out, "Air Rune", Rarity.COMMON, ItemID.AIRRUNE);
		item(out, "Water Rune", Rarity.COMMON, ItemID.WATERRUNE);
		item(out, "Earth Rune", Rarity.COMMON, ItemID.EARTHRUNE);
		item(out, "Fire Rune", Rarity.COMMON, ItemID.FIRERUNE);
		item(out, "Mind Rune", Rarity.COMMON, ItemID.MINDRUNE);
		item(out, "Body Rune", Rarity.COMMON, ItemID.BODYRUNE);
		item(out, "Chaos Rune", Rarity.UNCOMMON, ItemID.CHAOSRUNE);
		item(out, "Cosmic Rune", Rarity.UNCOMMON, ItemID.COSMICRUNE);
		item(out, "Nature Rune", Rarity.UNCOMMON, ItemID.NATURERUNE);
		item(out, "Law Rune", Rarity.UNCOMMON, ItemID.LAWRUNE);
		item(out, "Death Rune", Rarity.UNCOMMON, ItemID.DEATHRUNE);
		item(out, "Mist Rune", Rarity.UNCOMMON, ItemID.MISTRUNE);
		item(out, "Dust Rune", Rarity.UNCOMMON, ItemID.DUSTRUNE);
		item(out, "Mud Rune", Rarity.RARE, ItemID.MUDRUNE);
		item(out, "Smoke Rune", Rarity.UNCOMMON, ItemID.SMOKERUNE);
		item(out, "Steam Rune", Rarity.UNCOMMON, ItemID.STEAMRUNE);
		item(out, "Lava Rune", Rarity.UNCOMMON, ItemID.LAVARUNE);
		item(out, "Blood Rune", Rarity.RARE, ItemID.BLOODRUNE);
		item(out, "Soul Rune", Rarity.RARE, ItemID.SOULRUNE);
		item(out, "Astral Rune", Rarity.RARE, ItemID.ASTRALRUNE);
		item(out, "Wrath Rune", Rarity.RARE, ItemID.WRATHRUNE);
		item(out, "Sunfire Rune", Rarity.EPIC, ItemID.SUNFIRERUNE);
		item(out, "Aether Rune", Rarity.EPIC, ItemID.AETHERRUNE);
		item(out, "Marrentill", Rarity.COMMON, ItemID.MARENTILL);
		item(out, "Tarromin", Rarity.COMMON, ItemID.TARROMIN);
		item(out, "Harralander", Rarity.UNCOMMON, ItemID.HARRALANDER);
		item(out, "Toadflax", Rarity.UNCOMMON, ItemID.TOADFLAX);
		item(out, "Irit Leaf", Rarity.UNCOMMON, ItemID.IRIT_LEAF);
		item(out, "Avantoe", Rarity.UNCOMMON, ItemID.AVANTOE);
		item(out, "Kwuarm", Rarity.UNCOMMON, ItemID.KWUARM);
		item(out, "Huasca", Rarity.RARE, ItemID.HUASCA);
		item(out, "Cadantine", Rarity.RARE, ItemID.CADANTINE);
		item(out, "Lantadyme", Rarity.RARE, ItemID.LANTADYME);
		item(out, "Dwarf Weed", Rarity.RARE, ItemID.DWARF_WEED);
		item(out, "Raw Shrimps", Rarity.COMMON, ItemID.RAW_SHRIMP);
		item(out, "Raw Anchovies", Rarity.COMMON, ItemID.RAW_ANCHOVIES);
		item(out, "Raw Sardine", Rarity.COMMON, ItemID.RAW_SARDINE);
		item(out, "Raw Herring", Rarity.COMMON, ItemID.RAW_HERRING);
		item(out, "Raw Mackerel", Rarity.COMMON, ItemID.RAW_MACKEREL);
		item(out, "Raw Cod", Rarity.COMMON, ItemID.RAW_COD);
		item(out, "Raw Pike", Rarity.COMMON, ItemID.RAW_PIKE);
		item(out, "Raw Tuna", Rarity.UNCOMMON, ItemID.RAW_TUNA);
		item(out, "Raw Bass", Rarity.UNCOMMON, ItemID.RAW_BASS);
		item(out, "Raw Monkfish", Rarity.UNCOMMON, ItemID.RAW_MONKFISH);
		item(out, "Raw Karambwan", Rarity.UNCOMMON, ItemID.TBWT_RAW_KARAMBWAN);
		item(out, "Raw Sea Turtle", Rarity.RARE, ItemID.RAW_SEATURTLE);
		item(out, "Raw Manta Ray", Rarity.RARE, ItemID.RAW_MANTARAY);
		item(out, "Raw Dark Crab", Rarity.RARE, ItemID.RAW_DARK_CRAB);
		item(out, "Uncut Onyx", Rarity.EPIC, ItemID.UNCUT_ONYX);
		item(out, "Zulrah's Scales", Rarity.COMMON, ItemID.SNAKEBOSS_SCALE);
		item(out, "Dexterous Prayer Scroll", Rarity.RARE, ItemID.RAIDS_PRAYERSCROLL);
		item(out, "Arcane Prayer Scroll", Rarity.RARE, ItemID.RAIDS_PRAYERSCROLL_AUGURY);
		item(out, "Twisted Buckler", Rarity.EPIC, ItemID.TWISTED_BUCKLER);
		item(out, "Dragon Hunter Crossbow", Rarity.EPIC, ItemID.DRAGONHUNTER_XBOW);
		item(out, "Dinh's Bulwark", Rarity.EPIC, ItemID.DINHS_BULWARK);
		item(out, "Ancestral Robe Top", Rarity.EPIC, ItemID.ANCESTRAL_ROBE_TOP);
		item(out, "Twisted Bow", Rarity.LEGENDARY, ItemID.TWISTED_BOW);
		item(out, "Justiciar Faceguard", Rarity.EPIC, ItemID.JUSTICIAR_FACEGUARD);
		item(out, "Justiciar Chestguard", Rarity.EPIC, ItemID.JUSTICIAR_CHESTGUARD);
		item(out, "Avernic Defender", Rarity.EPIC, ItemID.INFERNAL_DEFENDER);
		item(out, "Scythe of Vitur", Rarity.LEGENDARY, ItemID.SCYTHE_OF_VITUR);
		item(out, "Osmumten's Fang", Rarity.EPIC, ItemID.OSMUMTENS_FANG);
		item(out, "Lightbearer", Rarity.EPIC, ItemID.LIGHTBEARER);
		item(out, "Elidinis' Ward", Rarity.EPIC, ItemID.ELIDINIS_WARD);
		item(out, "Masori Mask", Rarity.EPIC, ItemID.MASORI_MASK);
		item(out, "Masori Chestplate", Rarity.EPIC, ItemID.MASORI_CHESTPLATE);
		item(out, "Tumeken's Shadow", Rarity.LEGENDARY, ItemID.TUMEKENS_SHADOW);
		item(out, "Armadyl Crossbow", Rarity.EPIC, ItemID.ACB);
		item(out, "Saradomin Sword", Rarity.RARE, ItemID.SARADOMIN_SWORD);
		item(out, "Zamorakian Spear", Rarity.RARE, ItemID.ZAMORAK_SPEAR);
		item(out, "Staff of the Dead", Rarity.EPIC, ItemID.SOTD);
		item(out, "Bandos Tassets", Rarity.EPIC, ItemID.BANDOS_SKIRT);
		item(out, "Armadyl Helmet", Rarity.EPIC, ItemID.ARMADYL_HELMET);
		item(out, "Torva Full Helm", Rarity.LEGENDARY, ItemID.TORVA_HELM);
		item(out, "Torva Platebody", Rarity.LEGENDARY, ItemID.TORVA_CHEST);
		item(out, "Zaryte Vambraces", Rarity.EPIC, ItemID.ZARYTE_VAMBRACES);
		item(out, "Virtus Mask", Rarity.EPIC, ItemID.VIRTUS_MASK);
		item(out, "Virtus Robe Top", Rarity.EPIC, ItemID.VIRTUS_TOP);
		item(out, "Nightmare Staff", Rarity.EPIC, ItemID.NIGHTMARE_STAFF);
		item(out, "Harmonised Orb", Rarity.LEGENDARY, ItemID.HARMONISED_ORB);
		item(out, "Volatile Orb", Rarity.EPIC, ItemID.VOLATILE_ORB);
		item(out, "Eldritch Orb", Rarity.EPIC, ItemID.ELDRITCH_ORB);
		item(out, "Ultor Vestige", Rarity.LEGENDARY, ItemID.ULTOR_VESTIGE);
		item(out, "Magus Vestige", Rarity.EPIC, ItemID.MAGUS_VESTIGE);
		item(out, "Venator Vestige", Rarity.EPIC, ItemID.VENATOR_VESTIGE);
		item(out, "Bellator Vestige", Rarity.EPIC, ItemID.BELLATOR_VESTIGE);
		item(out, "Awakener's Orb", Rarity.EPIC, ItemID.DT2_AWAKENERS_ORB);
		item(out, "Noxious Halberd", Rarity.LEGENDARY, ItemID.NOXIOUS_HALBERD);
		item(out, "Araxyte Fang", Rarity.EPIC, ItemID.ARAXYTE_FANG);
		item(out, "Amulet of Rancour", Rarity.EPIC, ItemID.AMULET_OF_RANCOUR);
		item(out, "Abyssal Dagger", Rarity.RARE, ItemID.ABYSSAL_DAGGER);
		item(out, "Abyssal Bludgeon", Rarity.EPIC, ItemID.ABYSSAL_BLUDGEON);
		item(out, "Kraken Tentacle", Rarity.RARE, ItemID.KRAKEN_TENTACLE);
		item(out, "Hydra's Claw", Rarity.EPIC, ItemID.HYDRA_CLAW);
		item(out, "Hydra Tail", Rarity.RARE, ItemID.HYDRA_TAIL);
		item(out, "Hydra Leather", Rarity.RARE, ItemID.HYDRA_LEATHER);
		item(out, "Sarachnis Cudgel", Rarity.RARE, ItemID.SARACHNIS_CUDGEL);
		item(out, "Primordial Crystal", Rarity.EPIC, ItemID.PRIMORDIAL_CRYSTAL);
		item(out, "Pegasian Crystal", Rarity.EPIC, ItemID.PEGASIAN_CRYSTAL);
		item(out, "Eternal Crystal", Rarity.EPIC, ItemID.ETERNAL_CRYSTAL);
		item(out, "Smouldering Stone", Rarity.RARE, ItemID.SMOULDERING_STONE);
		item(out, "Granite Hammer", Rarity.UNCOMMON, ItemID.GRANITE_HAMMER);
		item(out, "Elysian Sigil", Rarity.LEGENDARY, ItemID.ELYSIAN_SIGIL);
		item(out, "Serpentine Visage", Rarity.RARE, ItemID.SERPENTINE_VISAGE);
		item(out, "Draconic Visage", Rarity.EPIC, ItemID.DRAGONFIRE_VISAGE);
		item(out, "Tanzanite Fang", Rarity.RARE, ItemID.BLOWPIPE_FANG);
		item(out, "Magic Fang", Rarity.RARE, ItemID.MAGIC_FANG);
		item(out, "Dragon Harpoon", Rarity.RARE, ItemID.DRAGON_HARPOON);
		item(out, "Tome of Fire", Rarity.RARE, ItemID.TOME_OF_FIRE);
		item(out, "Tome of Water", Rarity.RARE, ItemID.TOME_OF_WATER);
		item(out, "Ring of the Gods", Rarity.RARE, ItemID.ROTG);
		item(out, "Treasonous Ring", Rarity.RARE, ItemID.SHARP_RING);
		item(out, "Tyrannical Ring", Rarity.RARE, ItemID.HEAVY_RING);
		item(out, "Smoke Battlestaff", Rarity.UNCOMMON, ItemID.SMOKE_BATTLESTAFF);
	}
	private static void addMinigames(List<Card> out)
	{
		plain(out, CardSet.MINIGAMES, "Barbarian Assault", Rarity.COMMON);
		plain(out, CardSet.MINIGAMES, "Pest Control", Rarity.COMMON);
		plain(out, CardSet.MINIGAMES, "Castle Wars", Rarity.COMMON);
		plain(out, CardSet.MINIGAMES, "Blast Furnace", Rarity.COMMON);
		plain(out, CardSet.MINIGAMES, "Tithe Farm", Rarity.COMMON);
		plain(out, CardSet.MINIGAMES, "Wintertodt", Rarity.COMMON);
		plain(out, CardSet.MINIGAMES, "Pyramid Plunder", Rarity.COMMON);
		plain(out, CardSet.MINIGAMES, "Brimhaven Agility Arena", Rarity.COMMON);
		plain(out, CardSet.MINIGAMES, "Rogues' Den", Rarity.UNCOMMON);
		plain(out, CardSet.MINIGAMES, "Trouble Brewing", Rarity.UNCOMMON);
		plain(out, CardSet.MINIGAMES, "Mage Training Arena", Rarity.UNCOMMON);
		plain(out, CardSet.MINIGAMES, "Gnome Restaurant", Rarity.UNCOMMON);
		plain(out, CardSet.MINIGAMES, "Volcanic Mine", Rarity.UNCOMMON);
		plain(out, CardSet.MINIGAMES, "Guardians of the Rift", Rarity.UNCOMMON);
		plain(out, CardSet.MINIGAMES, "Chambers of Xeric Challenge", Rarity.RARE);
		plain(out, CardSet.MINIGAMES, "Last Man Standing", Rarity.RARE);
		plain(out, CardSet.MINIGAMES, "Soul Wars", Rarity.RARE);
		plain(out, CardSet.MINIGAMES, "Nightmare Zone", Rarity.RARE);
		plain(out, CardSet.MINIGAMES, "Fight Caves", Rarity.EPIC);
		plain(out, CardSet.MINIGAMES, "Inferno", Rarity.LEGENDARY);
	}
	private static void addSlayer(List<Card> out)
	{
		slayer(out, "Crawling Hand", Rarity.COMMON, -1);
		slayer(out, "Banshee", Rarity.COMMON, ItemID.WITCHWOOD_ICON);
		slayer(out, "Rockslug", Rarity.COMMON, -1);
		slayer(out, "Cockatrice", Rarity.COMMON, -1);
		slayer(out, "Pyrefiend", Rarity.COMMON, -1);
		slayer(out, "Basilisk", Rarity.COMMON, -1);
		slayer(out, "Infernal Mage", Rarity.COMMON, -1);
		slayer(out, "Bloodveld", Rarity.COMMON, -1);
		slayer(out, "Jelly", Rarity.COMMON, -1);
		slayer(out, "Turoth", Rarity.COMMON, ItemID.LEAFBLADED_SWORD);
		slayer(out, "Aberrant Spectre", Rarity.UNCOMMON, -1);
		slayer(out, "Dust Devil", Rarity.UNCOMMON, -1);
		slayer(out, "Kurask", Rarity.UNCOMMON, ItemID.LEAFBLADED_SWORD);
		slayer(out, "Gargoyle", Rarity.UNCOMMON, ItemID.HAMMER);
		slayer(out, "Nechryael", Rarity.UNCOMMON, -1);
		slayer(out, "Cave Horror", Rarity.UNCOMMON, -1);
		slayer(out, "Skeletal Wyvern", Rarity.UNCOMMON, -1);
		slayer(out, "Dark Beast", Rarity.RARE, -1);
		slayer(out, "Abyssal Demon", Rarity.RARE, ItemID.ABYSSAL_WHIP);
		slayer(out, "Smoke Devil", Rarity.RARE, ItemID.OCCULT_NECKLACE);
		slayer(out, "Wyrm", Rarity.RARE, ItemID.BOOTS_OF_STONE);
		slayer(out, "Drake", Rarity.RARE, ItemID.BOOTS_OF_STONE);
		slayer(out, "Hydra", Rarity.EPIC, -1);
		slayer(out, "Brutal Black Dragon", Rarity.EPIC, ItemID.DRAGON_BONES);
		slayer(out, "Nechryarch", Rarity.LEGENDARY, -1);
	}
	private static void slayer(List<Card> out, String name, Rarity rarity, int itemId)
	{
		out.add(itemId > 0
			? Card.ofItem(CardSet.SLAYER, name, rarity, itemId)
			: Card.ofSprite(CardSet.SLAYER, name, rarity, SpriteID.Staticons2.SLAYER));
	}
	private static void addDiaries(List<Card> out)
	{
		String[] areas = {
			"Ardougne", "Desert", "Falador", "Fremennik", "Kandarin", "Karamja",
			"Kourend & Kebos", "Lumbridge & Draynor", "Morytania", "Varrock",
			"Western Provinces", "Wilderness"
		};
		Rarity[] tiers = {Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC};
		String[] names = {"Easy", "Medium", "Hard", "Elite"};

		String[] codes = {
			"Ardougne", "Desert", "Falador", "Fremennik", "Kandarin", "Karamja",
			"Kourend", "Lumbridge", "Morytania", "Varrock", "Western", "Wilderness"
		};

		for (int a = 0; a < areas.length; a++)
		{
			for (int i = 0; i < tiers.length; i++)
			{
				out.add(Card.ofSprite(CardSet.DIARIES, areas[a] + " " + names[i], tiers[i],
					SpriteID.AchievementDiaryIcons.GREEN_ACHIEVEMENT_DIARIES, codes[a]));
			}
		}

		out.add(Card.ofSprite(CardSet.DIARIES, "Achievement Diary Cape", Rarity.LEGENDARY,
			SpriteID.AchievementDiaryIcons.GREEN_ACHIEVEMENT_DIARIES, "Every"));
	}
	private static void boss(List<Card> out, String name, Rarity rarity, int spriteId)
	{
		out.add(Card.ofSprite(CardSet.BOSSES, name, rarity, spriteId));
	}
	private static void item(List<Card> out, String name, Rarity rarity, int itemId)
	{
		out.add(Card.ofItem(CardSet.ITEMS, name, rarity, itemId));
	}

	private static void plain(List<Card> out, CardSet set, String name, Rarity rarity)
	{
		out.add(Card.ofSprite(set, name, rarity, categorySprite(set)));
	}
	private static int categorySprite(CardSet set)
	{
		switch (set)
		{
			case DIARIES:
				return SpriteID.AchievementDiaryIcons.GREEN_ACHIEVEMENT_DIARIES;
			case MINIGAMES:
				return SpriteID.AchievementDiaryIcons.RED_MINIGAMES;
			case SLAYER:
				return SpriteID.Staticons2.SLAYER;
			default:
				return SpriteID.AchievementDiaryIcons.BLUE_QUESTS;
		}
	}
	private static String title(String enumName)
	{
		return enumName.charAt(0) + enumName.substring(1).toLowerCase();
	}

	public static List<Card> all()
	{
		return CARDS;
	}
	public static Card byId(String id)
	{
		return BY_ID.get(id);
	}
	public static List<Card> byRarity(Rarity rarity)
	{
		return Collections.unmodifiableList(BY_RARITY.get(rarity));
	}

	public static List<Card> bySet(CardSet set)
	{
		return Collections.unmodifiableList(BY_SET.get(set));
	}
	public static List<Card> bySetAndRarity(CardSet set, Rarity rarity)
	{
		List<Card> matching = new ArrayList<>();
		for (Card card : BY_SET.get(set))
		{
			if (card.getRarity() == rarity)
			{
				matching.add(card);
			}
		}
		return matching;
	}

	public static int size()
	{
		return CARDS.size();
	}
}
