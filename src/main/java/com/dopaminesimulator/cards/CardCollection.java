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
	public static final double BONUS_PER_COLLECTION = 0.10d;
	private static final List<CardCollection> ALL = new ArrayList<>();
	private static final Map<String, List<CardCollection>> BY_CARD = new LinkedHashMap<>();
	private final String name;
	private final CardSet set;
	private final String description;
	private final List<Card> cards;
	private final List<String> unresolved;
	private CardCollection(String name, CardSet set, String description, List<String> memberNames)
	{
		this.name = name;
		this.set = set;
		this.description = description;
		List<Card> resolved = new ArrayList<>();
		List<String> missing = new ArrayList<>();
		for (String member : memberNames)
		{
			Card card = findIn(set, member);
			if (card == null)
			{
				missing.add(member);
			}
			else
			{
				resolved.add(card);
			}
		}
		this.cards = Collections.unmodifiableList(resolved);
		this.unresolved = Collections.unmodifiableList(missing);
	}

	static
	{
		for (Card card : CardCatalogue.bySet(CardSet.DIARIES))
		{
			String area = areaOf(card.getName());
			if (area == null)
			{
				continue;
			}
			List<String> tiers = new ArrayList<>();
			for (Card sibling : CardCatalogue.bySet(CardSet.DIARIES))
			{
				if (area.equals(areaOf(sibling.getName())))
				{
					tiers.add(sibling.getName());
				}
			}
			if (!contains(area + " Diaries"))
			{
				define(area + " Diaries", CardSet.DIARIES,
					"All four difficulty tiers.", tiers);
			}
		}
		define("The Elf Saga", CardSet.QUESTS,
			"The elf quest series.",
			"Plague City", "Biohazard", "Underground Pass", "Regicide", "Roving Elves",
			"Mourning's End Part I", "Mourning's End Part II", "Song of the Elves");
		define("The Myreque", CardSet.QUESTS,
			"The Myreque quest series.",
			"In Search of the Myreque", "In Aid of the Myreque", "Darkness of Hallowvale",
			"A Taste of Hope", "Sins of the Father", "The Blood Moon Rises");
		define("The Mahjarrat", CardSet.QUESTS,
			"Quests involving the Mahjarrat.",
			"Desert Treasure I", "Desert Treasure II - The Fallen Empire", "The Curse of Arrav",
			"Defender of Varrock", "While Guthix Sleeps", "Secrets of the North");
		define("Gnome Business", CardSet.QUESTS,
			"The gnome quest series.",
			"Tree Gnome Village", "The Grand Tree", "The Eyes of Glouphrie",
			"The Path of Glouphrie", "Monkey Madness I", "Monkey Madness II");
		define("Fremennik Trials", CardSet.QUESTS,
			"The Fremennik quest series.",
			"The Fremennik Trials", "The Fremennik Isles", "The Fremennik Exiles");

		define("Dragon Slayers", CardSet.QUESTS,
			"Both Dragon Slayer quests.",
			"Dragon Slayer I", "Dragon Slayer II");
		define("Pirate Tales", CardSet.QUESTS,
			"The pirate quest series.",
			"Pirate's Treasure", "Rum Deal", "Cabin Fever", "The Great Brain Robbery");
		define("Great Kourend", CardSet.QUESTS,
			"The Great Kourend quest series.",
			"Client of Kourend", "The Queen of Thieves", "The Depths of Despair",
			"Tale of the Righteous", "The Forsaken Tower", "The Ascent of Arceuus",
			"A Kingdom Divided");
		define("Varlamore", CardSet.QUESTS,
			"The Varlamore quest series.",
			"Children of the Sun", "Twilight's Promise", "Perilous Moons", "The Final Dawn");

		define("Recipe for Disaster", CardSet.QUESTS,
			"Recipe for Disaster and its ten subquests.",
			"Recipe for Disaster", "Recipe for Disaster - Another Cook's Quest",
			"Recipe for Disaster - Mountain Dwarf", "Recipe for Disaster - Wartface & Bentnoze",
			"Recipe for Disaster - Pirate Pete", "Recipe for Disaster - Lumbridge Guide",
			"Recipe for Disaster - Evil Dave", "Recipe for Disaster - Skrach Uglogwee",
			"Recipe for Disaster - Sir Amik Varze", "Recipe for Disaster - King Awowogei",
			"Recipe for Disaster - Culinaromancer");
		define("Combat Skills", CardSet.SKILLS,
			"The seven combat skills.",
			"Attack", "Strength", "Defence", "Hitpoints", "Ranged", "Magic", "Prayer");
		define("Gathering Skills", CardSet.SKILLS,
			"The five gathering skills.",
			"Mining", "Fishing", "Woodcutting", "Farming", "Hunter");
		define("Artisan Skills", CardSet.SKILLS,
			"The eight artisan skills.",
			"Smithing", "Crafting", "Fletching", "Cooking", "Firemaking", "Herblore",
			"Construction", "Runecraft");
		define("Support Skills", CardSet.SKILLS,
			"The three support skills.",
			"Agility", "Thieving", "Slayer");
		define("God Wars Dungeon", CardSet.BOSSES,
			"The four generals and Nex.",
			"General Graardor", "K'ril Tsutsaroth", "Commander Zilyana", "Kree'arra", "Nex");
		define("Dagannoth Kings", CardSet.BOSSES,
			"All three Dagannoth Kings.",
			"Dagannoth Rex", "Dagannoth Prime", "Dagannoth Supreme");
		define("Wilderness Bosses", CardSet.BOSSES,
			"Bosses found in the Wilderness.",
			"Callisto", "Vet'ion", "Venenatis", "Chaos Elemental", "Chaos Fanatic",
			"Crazy Archaeologist", "Scorpia", "King Black Dragon");
		define("The Desert Awakening", CardSet.BOSSES,
			"The four Desert Treasure II bosses.",
			"Duke Sucellus", "The Leviathan", "The Whisperer", "Vardorvis");
		define("Raids", CardSet.BOSSES,
			"All three raids.",
			"Chambers of Xeric", "Theatre of Blood", "Tombs of Amascut");
		define("Slayer Bosses", CardSet.BOSSES,
			"Bosses that appear as Slayer tasks.",
			"Abyssal Sire", "Cerberus", "Kraken", "Thermonuclear Smoke Devil",
			"Alchemical Hydra", "Grotesque Guardians");
		define("Skilling Bosses", CardSet.BOSSES,
			"Bosses trained as skilling activities.",
			"Tempoross", "Wintertodt", "Zalcano", "Hespori");
		define("Low-Level Slayer", CardSet.SLAYER,
			"Slayer tasks up to level 52.",
			"Crawling Hand", "Banshee", "Rockslug", "Cockatrice", "Pyrefiend", "Basilisk",
			"Infernal Mage", "Bloodveld", "Jelly");
		define("Mid-Level Slayer", CardSet.SLAYER,
			"Slayer tasks from level 55 to 80.",
			"Turoth", "Aberrant Spectre", "Dust Devil", "Kurask", "Gargoyle", "Nechryael",
			"Cave Horror", "Skeletal Wyvern", "Wyrm");

		define("High-Level Slayer", CardSet.SLAYER,
			"Slayer tasks from level 77 upwards.",
			"Dark Beast", "Abyssal Demon", "Smoke Devil", "Drake", "Hydra",
			"Brutal Black Dragon", "Nechryarch");

		define("The Scimitar Ladder", CardSet.ITEMS,
			"The scimitar tier list.",
			"Iron Scimitar", "Steel Scimitar", "Mithril Scimitar", "Adamant Scimitar",
			"Rune Scimitar", "Dragon Scimitar");
		define("Every Log", CardSet.ITEMS,
			"Every type of logs.",
			"Logs", "Oak Logs", "Willow Logs", "Maple Logs", "Yew Logs", "Magic Logs",
			"Redwood Logs");
		define("Every Ore", CardSet.ITEMS,
			"Every type of ore.",
			"Copper Ore", "Tin Ore", "Iron Ore", "Coal", "Gold Ore", "Mithril Ore",
			"Adamantite Ore", "Runite Ore");
		define("Chambers of Xeric", CardSet.ITEMS,
			"Uniques from the Chambers of Xeric.",
			"Kodai Wand", "Ancestral Hat", "Ancestral Robe Top", "Elder Maul", "Dragon Claws",
			"Twisted Buckler", "Dragon Hunter Crossbow", "Dinh's Bulwark",
			"Dexterous Prayer Scroll", "Arcane Prayer Scroll", "Twisted Bow");

		define("Theatre of Blood", CardSet.ITEMS,
			"Uniques from the Theatre of Blood.",
			"Ghrazi Rapier", "Sanguinesti Staff", "Justiciar Faceguard", "Justiciar Chestguard",
			"Avernic Defender", "Scythe of Vitur");

		define("Tombs of Amascut", CardSet.ITEMS,
			"Uniques from the Tombs of Amascut.",
			"Osmumten's Fang", "Lightbearer", "Elidinis' Ward", "Masori Mask",
			"Masori Chestplate", "Tumeken's Shadow");

		define("God Wars Uniques", CardSet.ITEMS,
			"Drops from the four God Wars generals.",
			"Armadyl Chestplate", "Armadyl Helmet", "Armadyl Crossbow", "Bandos Chestplate",
			"Bandos Tassets", "Saradomin Sword", "Zamorakian Spear", "Staff of the Dead");

		define("The Nex Drop Table", CardSet.ITEMS,
			"Drops from Nex.",
			"Torva Full Helm", "Torva Platebody", "Zaryte Vambraces");

		define("The Nightmare", CardSet.ITEMS,
			"Drops from the Nightmare.",
			"Inquisitor's Mace", "Nightmare Staff", "Harmonised Orb", "Volatile Orb",
			"Eldritch Orb");

		define("Desert Treasure II Drops", CardSet.ITEMS,
			"Drops from the Forgotten Four.",
			"Ultor Vestige", "Magus Vestige", "Venator Vestige", "Bellator Vestige",
			"Awakener's Orb", "Virtus Mask", "Virtus Robe Top");

		define("Araxxor", CardSet.ITEMS,
			"Drops from Araxxor.",
			"Noxious Halberd", "Araxyte Fang", "Amulet of Rancour");

		define("Slayer Boss Drops", CardSet.ITEMS,
			"Uniques from bosses that appear as Slayer tasks.",
			"Abyssal Whip", "Abyssal Dagger", "Abyssal Bludgeon", "Kraken Tentacle",
			"Hydra's Claw", "Hydra Tail", "Hydra Leather", "Occult Necklace",
			"Smoke Battlestaff", "Granite Hammer");

		define("Cerberus", CardSet.ITEMS,
			"The three crystals, the boots they upgrade, and the smouldering stone.",
			"Primordial Crystal", "Pegasian Crystal", "Eternal Crystal", "Primordial Boots",
			"Pegasian Boots", "Eternal Boots", "Smouldering Stone");

		define("Wilderness Boss Rings", CardSet.ITEMS,
			"The three rings from the Wilderness bosses.",
			"Ring of the Gods", "Treasonous Ring", "Tyrannical Ring");

		define("Zulrah", CardSet.ITEMS,
			"Zulrah's drops and the blowpipe they build.",
			"Tanzanite Fang", "Magic Fang", "Serpentine Visage", "Uncut Onyx",
			"Zulrah's Scales", "Toxic Blowpipe");

		define("Dragon Equipment", CardSet.ITEMS,
			"Dragon gear.",
			"Dragon Scimitar", "Dragon Pickaxe", "Dragon Harpoon", "Dragon Claws",
			"Dragon Hunter Crossbow");

		define("Skilling Boss Drops", CardSet.ITEMS,
			"Uniques from Tempoross, Wintertodt and Hespori.",
			"Dragon Harpoon", "Tome of Fire", "Tome of Water");
		define("The Rune Pouch", CardSet.ITEMS,
			"Every rune.",
			"Air Rune", "Water Rune", "Earth Rune", "Fire Rune", "Mind Rune", "Body Rune",
			"Chaos Rune", "Cosmic Rune", "Nature Rune", "Law Rune", "Death Rune", "Blood Rune",
			"Soul Rune", "Astral Rune", "Wrath Rune", "Mist Rune", "Dust Rune", "Mud Rune",
			"Smoke Rune", "Steam Rune", "Lava Rune", "Sunfire Rune", "Aether Rune");

		define("Tools", CardSet.ITEMS,
			"Basic skilling tools.",
			"Tinderbox", "Rope", "Hammer", "Chisel", "Knife", "Spade");
		define("The Herb Patch", CardSet.ITEMS,
			"Every herb, cleaned.",
			"Guam Leaf", "Marrentill", "Tarromin", "Harralander", "Ranarr Weed", "Toadflax",
			"Irit Leaf", "Avantoe", "Huasca", "Kwuarm", "Snapdragon", "Cadantine", "Lantadyme",
			"Dwarf Weed", "Torstol");
		define("Every Catch", CardSet.ITEMS,
			"Raw fish across the levels.",
			"Raw Shrimps", "Raw Anchovies", "Raw Sardine", "Raw Herring", "Raw Mackerel",
			"Raw Trout", "Raw Cod", "Raw Pike", "Raw Salmon", "Raw Tuna", "Raw Lobster",
			"Raw Bass", "Raw Swordfish", "Raw Monkfish", "Raw Karambwan", "Raw Shark",
			"Raw Sea Turtle", "Raw Manta Ray", "Raw Anglerfish", "Raw Dark Crab");
		define("Combat Minigames", CardSet.MINIGAMES,
			"Combat minigames.",
			"Barbarian Assault", "Pest Control", "Castle Wars", "Last Man Standing", "Soul Wars",
			"Nightmare Zone");
		define("Skilling Minigames", CardSet.MINIGAMES,
			"Skilling minigames.",
			"Blast Furnace", "Tithe Farm", "Wintertodt", "Pyramid Plunder",
			"Brimhaven Agility Arena", "Rogues' Den", "Trouble Brewing", "Mage Training Arena",
			"Gnome Restaurant", "Volcanic Mine", "Guardians of the Rift");
		define("The Caves", CardSet.MINIGAMES,
			"The Fight Caves and the Inferno.",
			"Fight Caves", "Inferno");
	}
	private static void define(String name, CardSet set, String description, String... members)
	{
		define(name, set, description, java.util.Arrays.asList(members));
	}
	private static void define(String name, CardSet set, String description, List<String> members)
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

	private static Card findIn(CardSet set, String name)
	{
		for (Card card : CardCatalogue.bySet(set))
		{
			if (card.getName().equalsIgnoreCase(name))
			{
				return card;
			}
		}
		return null;
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
		return Math.pow(1d + BONUS_PER_COLLECTION, completedIn(state, set));
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

	public boolean isComplete(DopamineState state)
	{
		return !cards.isEmpty() && ownedIn(state) == cards.size();
	}


	@Override
	public String toString()
	{
		return name;
	}
}
