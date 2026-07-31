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
package com.dopaminesimulator.packs;

import com.dopaminesimulator.cards.Rarity;
import lombok.Getter;

import java.awt.Color;

@Getter
public enum PackTier
{
	SCRAP("Scrap Pack", 400d, 3, 1.0d, null, false, 0d,
		new Color(0x9E, 0x9E, 0x9E),
		"3 cards. No rarity floor."),
	STANDARD("Standard Pack", 4_000d, 5, 1.0d, null, false, 2_000d,
		new Color(0x64, 0xB5, 0xF6),
		"5 cards. No rarity floor."),
	GILDED("Gilded Pack", 45_000d, 5, 2.5d, Rarity.UNCOMMON, false, 25_000d,
		new Color(0x66, 0xBB, 0x6A),
		"5 cards. Uncommon floor, x2.5 rare odds."),
	CURATED("Curated Pack", 250_000d, 5, 2.0d, Rarity.UNCOMMON, true, 150_000d,
		new Color(0x26, 0xC6, 0xDA),
		"5 cards from one chosen set. Uncommon floor, x2 rare odds."),
	PRISMATIC("Prismatic Pack", 1_200_000d, 7, 6.0d, Rarity.RARE, false, 700_000d,
		new Color(0xAB, 0x47, 0xBC),
		"7 cards. Rare floor, x6 rare odds."),
	ASCENDANT("Ascendant Pack", 25_000_000d, 10, 15.0d, Rarity.EPIC, false, 15_000_000d,
		new Color(0xFF, 0xB3, 0x00),
		"10 cards. Epic floor, x15 rare odds."),
	MYTHIC("Mythic Pack", 600_000_000d, 12, 40.0d, Rarity.EPIC, false, 400_000_000d,
		new Color(0xFF, 0x70, 0x43),
		"12 cards. Epic floor, x40 rare odds.");
	private final String displayName;
	private final double cost;
	private final int cardCount;
	private final double luck;
	private final Rarity floor;
	private final boolean targetsSet;
	private final double unlockAtLifetimePoints;
	private final Color colour;
	private final String description;
	PackTier(String displayName, double cost, int cardCount, double luck, Rarity floor,
			 boolean targetsSet, double unlockAtLifetimePoints, Color colour, String description)
	{
		this.displayName = displayName;
		this.cost = cost;
		this.cardCount = cardCount;
		this.luck = luck;
		this.floor = floor;
		this.targetsSet = targetsSet;
		this.unlockAtLifetimePoints = unlockAtLifetimePoints;
		this.colour = colour;
		this.description = description;
	}

    public boolean isUnlockedAt(double lifetimePoints)
	{
		return lifetimePoints >= unlockAtLifetimePoints;
	}
	public double getCostPerCard()
	{
		return cost / cardCount;
	}
}
