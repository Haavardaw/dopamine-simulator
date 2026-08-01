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
		QUEST_ART.put("Animal Magnetism", ItemID.AVAS_ASSEMBLER);
		QUEST_ART.put("Another Slice of H.A.M.", ItemID.ANCIENT_GOBLIN_MACE);
		QUEST_ART.put("The Ascent of Arceuus", ItemID.ARCEUUS_ESSENCE_BLOCK_DARK);
		QUEST_ART.put("Alfred Grimhand's Barcrawl", ItemID.BARCRAWL_CARD);
		QUEST_ART.put("Bear Your Soul", ItemID.ARCEUUS_SOULBEARER);
		QUEST_ART.put("Below Ice Mountain", ItemID.BIM_STEAK_SANDWICH);
		QUEST_ART.put("Between a Rock...", ItemID.DWARF_GOLDROCK_HELMET);
		QUEST_ART.put("Big Chompy Bird Hunting", ItemID.OGRE_BOW);
		QUEST_ART.put("Biohazard", ItemID.DISTILLATOR);
		QUEST_ART.put("Black Knights' Fortress", ItemID.IRON_CHAINBODY);
		QUEST_ART.put("Bone Voyage", ItemID.FOSSIL_CALCITE);
		QUEST_ART.put("Cabin Fever", ItemID.FEVER_PIRACY_BOOK);
		QUEST_ART.put("Client of Kourend", ItemID.VEOS_KHAREDSTS_MEMOIRS);
		QUEST_ART.put("Clock Tower", ItemID.BLACKCOG);
		QUEST_ART.put("Cold War", ItemID.PENGUIN_MASK);
		QUEST_ART.put("Contact!", ItemID.CONTACT_KERIS);
		QUEST_ART.put("Cook's Assistant", ItemID.CAKE);
		QUEST_ART.put("The Corsair Curse", ItemID.PIRATE_BANDANA_BROWN);
		QUEST_ART.put("Creature of Fenkenstrain", ItemID.RING_OF_CHAROS);
		QUEST_ART.put("Curse of the Empty Lord", ItemID.SECRET_GHOST_TOP);
		QUEST_ART.put("Daddy's Home", ItemID.POH_SAW);
		QUEST_ART.put("Darkness of Hallowvale", ItemID.SILVER_SICKLE_ENCHANTED);
		QUEST_ART.put("Death Plateau", ItemID.DEATH_CLIMBINGBOOTS);
		QUEST_ART.put("Death to the Dorgeshuun", ItemID.DTTD_BONE_CROSSBOW);
		QUEST_ART.put("Demon Slayer", ItemID.SILVERLIGHT);
		QUEST_ART.put("The Depths of Despair", ItemID.HOSIDIUSQUEST_ACCORD);
		QUEST_ART.put("Desert Treasure I", ItemID.TRAIL_ANCIENT_STAFF);
		QUEST_ART.put("Devious Minds", ItemID.DEVIOUS_BOWSWORD);
		QUEST_ART.put("The Dig Site", ItemID.SPECIMEN_BRUSH);
		QUEST_ART.put("Doric's Quest", ItemID.COPPER_ORE);
		QUEST_ART.put("Dragon Slayer I", ItemID.ANTIDRAGONBREATHSHIELD);
		QUEST_ART.put("Dragon Slayer II", ItemID.MYTHICAL_CAPE);
		QUEST_ART.put("Dream Mentor", ItemID.DREAM_VIAL_FULL);
		QUEST_ART.put("Druidic Ritual", ItemID.GUAM_LEAF);
		QUEST_ART.put("Dwarf Cannon", ItemID.MCANNONTOOLKIT);
		QUEST_ART.put("Eadgar's Ruse", ItemID.EADGAR_GOUTWEED_HERB);
		QUEST_ART.put("Eagles' Peak", ItemID.HUNTING_FERRET);
		QUEST_ART.put("Elemental Workshop I", ItemID.ELEMENTAL_SHIELD);
		QUEST_ART.put("Elemental Workshop II", ItemID.ELEM_MIND_HELM);
		QUEST_ART.put("Enakhra's Lament", ItemID.CAMULET);
		QUEST_ART.put("The Enchanted Key", ItemID.MAKINGHISTORY_KEY);
		QUEST_ART.put("Enlightened Journey", ItemID.ZEP_TEST_BALLOON);
		QUEST_ART.put("Enter the Abyss", ItemID.RCU_POUCH_SMALL);
		QUEST_ART.put("Ernest the Chicken", ItemID.RAW_CHICKEN);
		QUEST_ART.put("The Eyes of Glouphrie", ItemID.EYEGLO_BLUE_CIRCLE);
		QUEST_ART.put("Fairytale I - Growing Pains", ItemID.FAIRY_ENCHANTED_SECATEURS);
		QUEST_ART.put("Fairytale II - Cure a Queen", ItemID.FAIRYTALE2_NUFFS_CERTIFICATE);
		QUEST_ART.put("Family Crest", ItemID.FAMILY_CREST);
		QUEST_ART.put("Family Pest", ItemID.GAUNTLETS_OF_CHAOS);
		QUEST_ART.put("The Feud", ItemID.BLACKJACK_OAK);
		QUEST_ART.put("Fight Arena", ItemID.KHAZARD_PLATEMAIL);
		QUEST_ART.put("Fishing Contest", ItemID.HEMENSTER_FISHING_TROPHY);
		QUEST_ART.put("Forgettable Tale...", ItemID.DWARVEN_STOUT);
		QUEST_ART.put("The Forsaken Tower", ItemID.LOVAQUEST_HAMMER);
		QUEST_ART.put("The Fremennik Exiles", ItemID.NEITIZNOT_FACEGUARD);
		QUEST_ART.put("The Fremennik Isles", ItemID.FRIS_KINGLY_HELM);
		QUEST_ART.put("The Fremennik Trials", ItemID.VIKING_ENCHANTED_STRUNG_LYRE);
		QUEST_ART.put("Garden of Tranquillity", ItemID.RING_OF_CHAROS_UNLOCKED);
		QUEST_ART.put("The General's Shadow", ItemID.SHADOW_MAJ_SHADOW_SWORD);
		QUEST_ART.put("Gertrude's Cat", ItemID.LAZYCATOBJECT_BROWN);
		QUEST_ART.put("Getting Ahead", ItemID.GA_FURHEAD);
		QUEST_ART.put("Ghosts Ahoy", ItemID.ECTOPHIAL);
		QUEST_ART.put("The Giant Dwarf", ItemID.POH_UNFRAMED_PAINTING_GIANTDWARF);
		QUEST_ART.put("Goblin Diplomacy", ItemID.GOBLIN_ARMOUR);
		QUEST_ART.put("The Golem", ItemID.GOLEM_STATUETTE);
		QUEST_ART.put("The Grand Tree", ItemID.GRANDTREE_DACONIAROCK);
		QUEST_ART.put("The Great Brain Robbery", ItemID.BRAIN_ANCHOR);
		QUEST_ART.put("Grim Tales", ItemID.GRIM_WEAR_HELMET);
		QUEST_ART.put("The Hand in the Sand", ItemID.HANDSAND_SANDYHAND);
		QUEST_ART.put("Haunted Mine", ItemID.CRYSTALSHARD_NECKLACE);
		QUEST_ART.put("Hazeel Cult", ItemID.CARNILLEAN_ARMOUR);
		QUEST_ART.put("Heroes' Quest", ItemID.DRAGON_BATTLEAXE);
		QUEST_ART.put("Holy Grail", ItemID.HOLY_GRAIL);
		QUEST_ART.put("Horror from the Deep", ItemID.HORROR_CASKET);
		QUEST_ART.put("Icthlarin's Little Helper", ItemID.ICS_LITTLE_AMULET_OF_CATSPEAK);
		QUEST_ART.put("Imp Catcher", ItemID.AMULET_OF_ACCURACY);
		QUEST_ART.put("In Aid of the Myreque", ItemID.BURGH_ROD_COMMAND_FINAL_10);
		QUEST_ART.put("In Search of Knowledge", ItemID.ARCEUUS_LIBRARY_REWARD);
		QUEST_ART.put("In Search of the Myreque", ItemID.SILVER_SICKLE);
		QUEST_ART.put("Jungle Potion", ItemID.SNAKE_WEED);
		QUEST_ART.put("A Kingdom Divided", ItemID.AKD_ROSES_DUMMY);
		QUEST_ART.put("King's Ransom", ItemID.KR_CLUE_ARMOUR);
		QUEST_ART.put("The Knight's Sword", ItemID.FALADIAN_SWORD);
		QUEST_ART.put("Lair of Tarn Razorlor", ItemID.LOTR_CRYSTALSHARD_NECKLACE_UPGRADE);
		QUEST_ART.put("Legends' Quest", ItemID.CAPE_OF_LEGENDS);
		QUEST_ART.put("Lost City", ItemID.DRAMEN_STAFF);
		QUEST_ART.put("The Lost Tribe", ItemID.LOST_TRIBE_BROOCH);
		QUEST_ART.put("Lunar Diplomacy", ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF);
		QUEST_ART.put("Mage Arena I", ItemID.SARADOMIN_CAPE);
		QUEST_ART.put("Mage Arena II", ItemID.MA2_SARADOMIN_CAPE);
		QUEST_ART.put("Making Friends with My Arm", ItemID.MY2ARM_COFFIN);
		QUEST_ART.put("Making History", ItemID.MAKINGHISTORY_KEY);
		QUEST_ART.put("Merlin's Crystal", ItemID.EXCALIBUR);
		QUEST_ART.put("Misthalin Mystery", ItemID.MACRO_QUIZ_MYSTERY_BOX);
		QUEST_ART.put("Monkey Madness I", ItemID.DRAGON_SCIMITAR);
		QUEST_ART.put("Monkey Madness II", ItemID.HEAVY_BALLISTA);
		QUEST_ART.put("Monk's Friend", ItemID.MONKROBETOP);
		QUEST_ART.put("Mountain Daughter", ItemID.MDAUGHTER_BEAR_HELMET);
		QUEST_ART.put("Mourning's End Part I", ItemID.MOURNING_MOURNER_TOP);
		QUEST_ART.put("Mourning's End Part II", ItemID.DEATH_TALISMAN);
		QUEST_ART.put("Murder Mystery", ItemID.MURDERNECKLACE);
		QUEST_ART.put("My Arm's Big Adventure", ItemID.MYARM_HARDYTUBERS);
		QUEST_ART.put("Nature Spirit", ItemID.SILVER_SICKLE_BLESSED);
		QUEST_ART.put("A Night at the Theatre", ItemID.VERZIK_SPECIAL_WEAPON);
		QUEST_ART.put("Observatory Quest", ItemID.TELESCOPE_DUMMY);
		QUEST_ART.put("Olaf's Quest", ItemID.OLAF2_GATE_KEY_1);
		QUEST_ART.put("One Small Favour", ItemID.FAVOUR_KEY_RING);
		QUEST_ART.put("Pirate's Treasure", ItemID.CASKET);
		QUEST_ART.put("Plague City", ItemID.GASMASK);
		QUEST_ART.put("A Porcine of Interest", ItemID.PORCINE_SOURHOG_TROPHY);
		QUEST_ART.put("Priest in Peril", ItemID.DAGGER_WOLFBANE);
		QUEST_ART.put("Prince Ali Rescue", ItemID.SKINPASTE);
		QUEST_ART.put("The Queen of Thieves", ItemID.VEOS_MEMOIRS_PISC_PAGE);
		QUEST_ART.put("Rag and Bone Man I", ItemID.RAG_POLISHED_GOBLIN_BONE);
		QUEST_ART.put("Rag and Bone Man II", ItemID.RAG_BONESACK);
		QUEST_ART.put("Ratcatchers", ItemID.SNAKE_FLUTE);
		QUEST_ART.put("Recipe for Disaster", ItemID.HUNDRED_GAUNTLETS_LEVEL_10);
		QUEST_ART.put("Recruitment Drive", ItemID.BASIC_TK_HELM);
		QUEST_ART.put("Regicide", ItemID.DRAGON_HALBERD);
		QUEST_ART.put("The Restless Ghost", ItemID.AMULET_OF_GHOSTSPEAK);
		QUEST_ART.put("Romeo & Juliet", ItemID.CADAVA);
		QUEST_ART.put("Roving Elves", ItemID.CRYSTAL_BOW);
		QUEST_ART.put("Royal Trouble", ItemID.ROYAL_BOX);
		QUEST_ART.put("Rum Deal", ItemID.DEAL_WRENCH_BLESSED);
		QUEST_ART.put("Rune Mysteries", ItemID.AIR_TALISMAN);
		QUEST_ART.put("Scorpion Catcher", ItemID.SCORPIONCAGEFULL);
		QUEST_ART.put("Sea Slug", ItemID.SLUG2_SEASLUG_YOUNG);
		QUEST_ART.put("Shades of Mort'ton", ItemID.FLAMTAER_HAMMER);
		QUEST_ART.put("Shadow of the Storm", ItemID.DARKLIGHT);
		QUEST_ART.put("Sheep Herder", ItemID.PLAGUE_JACKET);
		QUEST_ART.put("Sheep Shearer", ItemID.BALL_OF_WOOL);
		QUEST_ART.put("Shield of Arrav", ItemID.THE_SHIELD_OF_ARRAV);
		QUEST_ART.put("Shilo Village", ItemID.MOSOL_WAMPUM_BELT);
		QUEST_ART.put("Sins of the Father", ItemID.BLISTERWOOD_FLAIL);
		QUEST_ART.put("Skippy and the Mogres", ItemID.SLAYERGUIDE_MOGRE);
		QUEST_ART.put("The Slug Menace", ItemID.BASIC_TK_RANK2_HELM);
		QUEST_ART.put("Song of the Elves", ItemID.BLADE_OF_SAELDOR);
		QUEST_ART.put("A Soul's Bane", ItemID.SOULBANE_ZAROS_SPEAR);
		QUEST_ART.put("Spirits of the Elid", ItemID.ELID_STATUETTE);
		QUEST_ART.put("Swan Song", ItemID.MONKFISH);
		QUEST_ART.put("Tai Bwo Wannai Trio", ItemID.TBWT_RAW_KARAMBWAN);
		QUEST_ART.put("A Tail of Two Cats", ItemID.TWOCATS_AMULETOFCATSPEAK);
		QUEST_ART.put("Tale of the Righteous", ItemID.SHAYZIENQUEST_CRYSTAL_DUMMY);
		QUEST_ART.put("A Taste of Hope", ItemID.IVANDIS_FLAIL);
		QUEST_ART.put("Tears of Guthix", ItemID.TOG_BOWL);
		QUEST_ART.put("Temple of Ikov", ItemID.IKOV_STAFFOFARMARDYL);
		QUEST_ART.put("Throne of Miscellania", ItemID.MISC_TREATY);
		QUEST_ART.put("The Tourist Trap", ItemID.DESERT_SHIRT);
		QUEST_ART.put("Tower of Life", ItemID.TOL_PLAYER_CONSTRUCTION_HARDHAT);
		QUEST_ART.put("Tree Gnome Village", ItemID.GNOME_AMULET);
		QUEST_ART.put("Tribal Totem", ItemID.TRIBAL_TOTEM);
		QUEST_ART.put("Troll Romance", ItemID.TROLLROMANCE_TOBOGGON);
		QUEST_ART.put("Troll Stronghold", ItemID.TROLL_KEY_PRISON);
		QUEST_ART.put("Underground Pass", ItemID.IBANSTAFF);
		QUEST_ART.put("Vampyre Slayer", ItemID.STAKE);
		QUEST_ART.put("Wanted!", ItemID.WANTED_SOLUS_TROPHY);
		QUEST_ART.put("Watchtower", ItemID.OGRERELIC);
		QUEST_ART.put("Waterfall Quest", ItemID.GLARIALS_AMULET_WATERFALL_QUEST);
		QUEST_ART.put("What Lies Below", ItemID.SUROK_RING);
		QUEST_ART.put("Witch's House", ItemID.BALL);
		QUEST_ART.put("Witch's Potion", ItemID.EYE_OF_NEWT);
		QUEST_ART.put("X Marks the Spot", ItemID.SPADE);
		QUEST_ART.put("Zogre Flesh Eaters", ItemID.ZOGRE_BOW);
		QUEST_ART.put("The Frozen Door", ItemID.NEX_FROZEN_KEY);
		QUEST_ART.put("Land of the Goblins", ItemID.LOTG_3DOSEGOBLIN);
		QUEST_ART.put("Hopespear's Will", ItemID.LOTG_BONE_HIGHPRIEST5);
		QUEST_ART.put("Temple of the Eye", ItemID.TOTE_AMULET);
		QUEST_ART.put("Beneath Cursed Sands", ItemID.KERIS_PARTISAN);
		QUEST_ART.put("Sleeping Giants", ItemID.GIANTS_FOUNDRY_COLOSSAL_BLADE);
		QUEST_ART.put("The Garden of Death", ItemID.TGOD_TABLET_1);
		QUEST_ART.put("Into the Tombs", ItemID.OSMUMTENS_FANG);
		QUEST_ART.put("Recipe for Disaster - Another Cook's Quest", ItemID.EGG);
		QUEST_ART.put("Recipe for Disaster - Mountain Dwarf", ItemID.ROCKCAKE);
		QUEST_ART.put("Recipe for Disaster - Wartface & Bentnoze", ItemID._100GOBLIN_COMPROMISE_MUSH);
		QUEST_ART.put("Recipe for Disaster - Pirate Pete", ItemID.HUNDRED_PIRATE_FISHCAKE);
		QUEST_ART.put("Recipe for Disaster - Lumbridge Guide", ItemID._100GUIDE_GUIDECAKE);
		QUEST_ART.put("Recipe for Disaster - Evil Dave", ItemID.HUNDRED_DAVE_STEW);
		QUEST_ART.put("Recipe for Disaster - Skrach Uglogwee", ItemID._100_JUBBLY_MEAT_COOKED);
		QUEST_ART.put("Recipe for Disaster - Sir Amik Varze", ItemID.CHICKENQUEST_EVIL_CHICKEN_EGG);
		QUEST_ART.put("Recipe for Disaster - King Awowogei", ItemID.HUNDRED_ILM_COOKED_STUFFED_SNAKE);
		QUEST_ART.put("Recipe for Disaster - Culinaromancer", ItemID.HUNDRED_GAUNTLETS_LEVEL_10);
		QUEST_ART.put("Secrets of the North", ItemID.VENATOR_BOW);
		QUEST_ART.put("Desert Treasure II - The Fallen Empire", ItemID.DT2_ELDER_HORN_DUMMY);
		QUEST_ART.put("His Faithful Servants", ItemID.BARROWS_BOOK_HISTORY);
		QUEST_ART.put("The Path of Glouphrie", ItemID.CRYSTAL_CHIME);
		QUEST_ART.put("Children of the Sun", ItemID.VARLAMORE_JAGUAR_FUR);
		QUEST_ART.put("Barbarian Training", ItemID.BRUT_FISHING_ROD);
		QUEST_ART.put("Defender of Varrock", ItemID.DOV_SHIELD_OF_ARRAV);
		QUEST_ART.put("While Guthix Sleeps", ItemID.WGS_STONE_OF_JAS_DUMMY_ITEM);
		QUEST_ART.put("Twilight's Promise", ItemID.VMQ2_CREST);
		QUEST_ART.put("At First Light", ItemID.GB_MOSS_ESSENCE);
		QUEST_ART.put("Perilous Moons", ItemID.PMOON_ICOSAHEDRON);
		QUEST_ART.put("The Ribbiting Tale of a Lily Pad Labour Dispute", ItemID.ORANGE);
		QUEST_ART.put("The Heart of Darkness", ItemID.VMQ3_CULTIST_ROBE_TOP);
		QUEST_ART.put("Death on the Isle", ItemID.DOTI_CASEFILE);
		QUEST_ART.put("Meat and Greet", ItemID.COOKED_MEAT_UNDEAD);
		QUEST_ART.put("Ethically Acquired Antiquities", ItemID.VM_MUSEUM_MAP);
		QUEST_ART.put("The Curse of Arrav", ItemID.COA_CANOPIC_JAR_COMPLETE);
		QUEST_ART.put("The Final Dawn", ItemID.ARKAN_BLADE);
		QUEST_ART.put("Shadows of Custodia", ItemID.CUSTODIAN_ANTLER_GUARD);
		QUEST_ART.put("Scrambled!", ItemID.SCRAMBLED_EGG);
		QUEST_ART.put("Vale Totems", ItemID.ENT_TOTEMS_LOOT);
		QUEST_ART.put("Pandemonium", ItemID.SAILING_CHARTING_SPYGLASS);
		QUEST_ART.put("Prying Times", ItemID.PRYING_TIMES_CARGO_CRATE);
		QUEST_ART.put("Current Affairs", ItemID.DUMMY_CHARTING_DUCK);
		QUEST_ART.put("Troubled Tortugans", ItemID.TORTUGAN_SHIELD);
		QUEST_ART.put("The Red Reef", ItemID.TRR_RED_CORAL);
		QUEST_ART.put("Learning the Ropes", ItemID.BURNT_SHRIMP);
		QUEST_ART.put("The Ides of Milk", ItemID.COWQUEST_MILK_SAMPLE_1);
		QUEST_ART.put("The Blood Moon Rises", ItemID.HALLOWED_FLAIL);
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
		out.add(Card.ofItem(CardSet.BOSSES, "Chambers of Xeric: Challenge Mode", Rarity.LEGENDARY,
			ItemID.TWISTED_BOW));
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
		minigame(out, "Barbarian Assault", Rarity.COMMON, ItemID.BARBASSAULT_PENANCE_FIGHTER_TORSO);
		minigame(out, "Pest Control", Rarity.COMMON, ItemID.PEST_VOID_KNIGHT_TOP);
		minigame(out, "Castle Wars", Rarity.COMMON, ItemID.CASTLEWARS_CLOAK_SARADOMIN);
		minigame(out, "Blast Furnace", Rarity.COMMON, ItemID.GAUNTLETS_OF_GOLDSMITHING);
		minigame(out, "Tithe Farm", Rarity.COMMON, ItemID.TITHE_REWARD_TORSO_MALE);
		minigame(out, "Pyramid Plunder", Rarity.COMMON, ItemID.PHARAOHS_SCEPTRE);
		minigame(out, "Brimhaven Agility Arena", Rarity.COMMON, ItemID.AGILITYARENA_TICKET);
		minigame(out, "Rogues' Den", Rarity.UNCOMMON, ItemID.ROGUESDEN_BODY);
		minigame(out, "Trouble Brewing", Rarity.UNCOMMON, ItemID.ASGARNIAN_ALE);
		minigame(out, "Mage Training Arena", Rarity.UNCOMMON, ItemID.MAGICTRAINING_INFINITYHAT);
		minigame(out, "Gnome Restaurant", Rarity.UNCOMMON, ItemID.BALL_GNOMEBALL_GAME);
		minigame(out, "Volcanic Mine", Rarity.UNCOMMON, ItemID.FOSSIL_VOLCANIC_ASH);
		minigame(out, "Guardians of the Rift", Rarity.UNCOMMON, ItemID.ABYSSAL_LANTERN);
		minigame(out, "Last Man Standing", Rarity.RARE, ItemID.BLIGHTED_KARAMBWAN);
		minigame(out, "Soul Wars", Rarity.RARE, ItemID.SOUL_CAPE_RED);
		minigame(out, "Nightmare Zone", Rarity.RARE, ItemID.IMBUED_HEART);
		minigame(out, "Fight Caves", Rarity.EPIC, ItemID.TZHAAR_CAPE_FIRE);
		minigame(out, "Inferno", Rarity.LEGENDARY, ItemID.INFERNAL_CAPE);
	}
	private static void addSlayer(List<Card> out)
	{
		slayer(out, "Crawling Hand", Rarity.COMMON, ItemID.SLAYERGUIDE_CRAWLINGHAND);
		slayer(out, "Banshee", Rarity.COMMON, ItemID.SLAYERGUIDE_BANSHEE);
		slayer(out, "Rockslug", Rarity.COMMON, ItemID.SLAYERGUIDE_ROCKSLUG);
		slayer(out, "Cockatrice", Rarity.COMMON, ItemID.SLAYERGUIDE_COCKATRICE);
		slayer(out, "Pyrefiend", Rarity.COMMON, ItemID.SLAYERGUIDE_PYRFIEND);
		slayer(out, "Basilisk", Rarity.COMMON, ItemID.SLAYERGUIDE_BASILISK);
		slayer(out, "Infernal Mage", Rarity.COMMON, ItemID.SLAYERGUIDE_INFERNALMAGE);
		slayer(out, "Bloodveld", Rarity.COMMON, ItemID.SLAYERGUIDE_BLOODVELD);
		slayer(out, "Jelly", Rarity.COMMON, ItemID.SLAYERGUIDE_JELLY);
		slayer(out, "Turoth", Rarity.COMMON, ItemID.SLAYERGUIDE_TUROTH);
		slayer(out, "Aberrant Spectre", Rarity.UNCOMMON, ItemID.SLAYERGUIDE_ABERRANTSPECTER);
		slayer(out, "Dust Devil", Rarity.UNCOMMON, ItemID.SLAYERGUIDE_DUSTDEVIL);
		slayer(out, "Kurask", Rarity.UNCOMMON, ItemID.SLAYERGUIDE_KURASK);
		slayer(out, "Gargoyle", Rarity.UNCOMMON, ItemID.SLAYERGUIDE_GARGOYLE);
		slayer(out, "Nechryael", Rarity.UNCOMMON, ItemID.SLAYERGUIDE_NECHRYAEL);
		slayer(out, "Cave Horror", Rarity.UNCOMMON, ItemID.SLAYERGUIDE_HARMLESS_CAVE_HORROR);
		slayer(out, "Skeletal Wyvern", Rarity.UNCOMMON, ItemID.SLAYERGUIDE_SKELETALWYVERN);
		slayer(out, "Dark Beast", Rarity.RARE, ItemID.SLAYERGUIDE_DARK_BEAST);
		slayer(out, "Abyssal Demon", Rarity.RARE, ItemID.SLAYERGUIDE_ABYSSALDEMON);
		slayer(out, "Smoke Devil", Rarity.RARE, ItemID.OCCULT_NECKLACE);
		slayer(out, "Wyrm", Rarity.RARE, ItemID.SLAYERGUIDE_WYRM);
		slayer(out, "Drake", Rarity.RARE, ItemID.SLAYERGUIDE_DRAKE);
		slayer(out, "Hydra", Rarity.EPIC, ItemID.SLAYERGUIDE_HYDRA);
		slayer(out, "Brutal Black Dragon", Rarity.EPIC, ItemID.DRAGONMASK_BLACK);
		slayer(out, "Nechryarch", Rarity.LEGENDARY, ItemID.MALEDICTION_WARD);
	}
	private static void minigame(List<Card> out, String name, Rarity rarity, int itemId)
	{
		out.add(itemId > 0
			? Card.ofItem(CardSet.MINIGAMES, name, rarity, itemId)
			: Card.ofSprite(CardSet.MINIGAMES, name, rarity,
				SpriteID.AchievementDiaryIcons.RED_MINIGAMES));
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

		int[][] rewards = {
			{ItemID.ARDY_CAPE_EASY, ItemID.ARDY_CAPE_MEDIUM, ItemID.ARDY_CAPE_HARD, ItemID.ARDY_CAPE_ELITE},
			{ItemID.DESERT_AMULET_EASY, ItemID.DESERT_AMULET_MEDIUM, ItemID.DESERT_AMULET_HARD, ItemID.DESERT_AMULET_ELITE},
			{ItemID.FALADOR_SHIELD_EASY, ItemID.FALADOR_SHIELD_MEDIUM, ItemID.FALADOR_SHIELD_HARD, ItemID.FALADOR_SHIELD_ELITE},
			{ItemID.FREMENNIK_BOOTS_EASY, ItemID.FREMENNIK_BOOTS_MEDIUM, ItemID.FREMENNIK_BOOTS_HARD, ItemID.FREMENNIK_BOOTS_ELITE},
			{ItemID.SEERS_HEADBAND_EASY, ItemID.SEERS_HEADBAND_MEDIUM, ItemID.SEERS_HEADBAND_HARD, ItemID.SEERS_HEADBAND_ELITE},
			{ItemID.ATJUN_GLOVES_EASY, ItemID.ATJUN_GLOVES_MED, ItemID.ATJUN_GLOVES_HARD, ItemID.ATJUN_GLOVES_ELITE},
			{ItemID.ZEAH_BLESSING_EASY, ItemID.ZEAH_BLESSING_MEDIUM, ItemID.ZEAH_BLESSING_HARD, ItemID.ZEAH_BLESSING_ELITE},
			{ItemID.LUMBRIDGE_RING_EASY, ItemID.LUMBRIDGE_RING_MEDIUM, ItemID.LUMBRIDGE_RING_HARD, ItemID.LUMBRIDGE_RING_ELITE},
			{ItemID.MORYTANIA_LEGS_EASY, ItemID.MORYTANIA_LEGS_MEDIUM, ItemID.MORYTANIA_LEGS_HARD, ItemID.MORYTANIA_LEGS_ELITE},
			{ItemID.VARROCK_ARMOUR_EASY, ItemID.VARROCK_ARMOUR_MEDIUM, ItemID.VARROCK_ARMOUR_HARD, ItemID.VARROCK_ARMOUR_ELITE},
			{ItemID.WESTERN_BANNER_EASY, ItemID.WESTERN_BANNER_MEDIUM, ItemID.WESTERN_BANNER_HARD, ItemID.WESTERN_BANNER_ELITE},
			{ItemID.WILDERNESS_SWORD_EASY, ItemID.WILDERNESS_SWORD_MEDIUM, ItemID.WILDERNESS_SWORD_HARD, ItemID.WILDERNESS_SWORD_ELITE}
		};

		for (int a = 0; a < areas.length; a++)
		{
			for (int i = 0; i < tiers.length; i++)
			{
				out.add(Card.ofItem(CardSet.DIARIES, areas[a] + " " + names[i], tiers[i], rewards[a][i]));
			}
		}

		out.add(Card.ofItem(CardSet.DIARIES, "Achievement Diary Cape", Rarity.LEGENDARY,
			ItemID.SKILLCAPE_AD_TRIMMED));
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
