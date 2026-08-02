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
import net.runelite.api.gameval.SpriteID;

@Getter
public enum CardSet
{
	QUESTS("Quests", "Every quest in the game"),
	SKILLS("Skills", "All twenty-three"),
	BOSSES("Bosses", "Including raids"),
	ITEMS("Items", "Gear and the things you handle daily"),
	MINIGAMES("Minigames", "Everything with its own reward shop"),
	SLAYER("Slayer", "Task monsters and slayer unlocks"),
	DIARIES("Diaries", "Achievement diaries by area"),

	// one set per skill, holding what that skill's in-game guide says you unlock,
	// plus Areas for the shortcuts and regions the guides list under several
	// skills at once
	AGILITY("Agility", "Courses and what agility opens up"),
	ATTACK("Attack", "What you can wield"),
	CONSTRUCTION("Construction", "Rooms and everything you can build in them"),
	COOKING("Cooking", "Everything you can cook or brew"),
	CRAFTING("Crafting", "Jewellery, leather, glass and pottery"),
	DEFENCE("Defence", "What you can wear"),
	FARMING("Farming", "Seeds, crops and trees"),
	FIREMAKING("Firemaking", "Logs and what you can light"),
	FISHING("Fishing", "Everything you can catch"),
	FLETCHING("Fletching", "Bows, bolts and darts"),
	HERBLORE("Herblore", "Herbs and potions"),
	HITPOINTS("Hitpoints", "What hitpoints alone gates"),
	HUNTER("Hunter", "Creatures and how to catch them"),
	MAGIC("Magic", "Spells and staves"),
	MINING("Mining", "Ores, rocks and pickaxes"),
	PRAYER("Prayer", "Prayers, bones and vestments"),
	RANGED("Ranged", "Bows, ammunition and what to wear"),
	RUNECRAFT("Runecraft", "Runes, altars and pouches"),
	SAILING("Sailing", "Ships, ports and crew"),
	SMITHING("Smithing", "Bars and everything you can hammer out"),
	STRENGTH("Strength", "What strength alone gates"),
	THIEVING("Thieving", "Stalls, chests and pockets"),
	WOODCUTTING("Woodcutting", "Trees and axes"),
	AREAS("Areas", "Shortcuts, regions and places you can reach");

	private final String displayName;
	private final String description;

	CardSet(String displayName, String description)
	{
		this.displayName = displayName;
		this.description = description;
	}

	/**
	 * Skill sets hold unlocks rather than collectibles, so a card in one is
	 * something you either have or have not: there is no owning Cooking 30 twice.
	 */
	public boolean isUnlockSet()
	{
		return ordinal() >= AGILITY.ordinal();
	}

	/**
	 * The game's own icon for the skill this set belongs to, or -1 for the sets
	 * that are not a skill. Drawn in the corner of the card so a Cooking card is
	 * recognisable as one at a glance.
	 */
	public int skillSpriteId()
	{
		switch (this)
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
			case HUNTER: return SpriteID.Staticons2.HUNTER;
			case FARMING: return SpriteID.Staticons2.FARMING;
			case CONSTRUCTION: return SpriteID.Staticons2.CONSTRUCTION;
			case SAILING: return SpriteID.Staticons2.SAILING;
			default: return -1;
		}
	}
}
