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
	SCRAP("Scrap Pack", 400d, 3, 1, 1.0d, null, Rarity.RARE, false, 0d,
		new Color(0x9E, 0x9E, 0x9E),
		"3 cards, 1 copy each. Rare at best."),
	STANDARD("Standard Pack", 2_400d, 5, 3, 1.0d, null, Rarity.EPIC, false, 2_000d,
		new Color(0x64, 0xB5, 0xF6),
		"5 cards, 3 copies each. Epic at best."),
	GILDED("Gilded Pack", 15_000d, 5, 12, 2.5d, Rarity.UNCOMMON, Rarity.EPIC, false, 20_000d,
		new Color(0x66, 0xBB, 0x6A),
		"5 cards, 12 copies each. Uncommon to Epic, x2.5 rare odds."),
	CURATED("Curated Pack", 70_000d, 5, 40, 2.0d, Rarity.UNCOMMON, Rarity.EPIC, true,
		100_000d, new Color(0x26, 0xC6, 0xDA),
		"5 cards, 40 copies each, from one chosen set. Uncommon to Epic."),
	PRISMATIC("Prismatic Pack", 350_000d, 7, 120, 6.0d, Rarity.RARE, Rarity.LEGENDARY,
		false, 500_000d, new Color(0xAB, 0x47, 0xBC),
		"7 cards, 120 copies each. Rare floor and the first shot at Legendary."),
	ASCENDANT("Ascendant Pack", 2_000_000d, 10, 400, 15.0d, Rarity.EPIC,
		Rarity.LEGENDARY, false, 3_000_000d, new Color(0xFF, 0xB3, 0x00),
		"10 cards, 400 copies each. Epic floor, roughly one card in ten Legendary."),
	MYTHIC("Mythic Pack", 8_000_000d, 12, 200, 40.0d, Rarity.LEGENDARY, Rarity.LEGENDARY,
		false, 15_000_000d, new Color(0xFF, 0x70, 0x43),
		"12 cards, 200 copies each. Legendary only.");
	private final String displayName;
	private final double cost;
	private final int cardCount;
	private final int copiesPerCard;
	private final double luck;
	private final Rarity floor;
	private final Rarity ceiling;
	private final boolean targetsSet;
	private final double unlockAtLifetimePoints;
	private final Color colour;
	private final String description;
	PackTier(String displayName, double cost, int cardCount, int copiesPerCard, double luck,
			 Rarity floor, Rarity ceiling, boolean targetsSet, double unlockAtLifetimePoints,
			 Color colour, String description)
	{
		this.displayName = displayName;
		this.cost = cost;
		this.cardCount = cardCount;
		this.copiesPerCard = copiesPerCard;
		this.luck = luck;
		this.floor = floor;
		this.ceiling = ceiling;
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

	public double getCostPerCopy()
	{
		return cost / (cardCount * (double) copiesPerCard);
	}
}
