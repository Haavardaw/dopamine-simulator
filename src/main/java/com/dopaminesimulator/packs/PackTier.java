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
	// a pile of copies of one card reads as noise, so grants stay small and
	// bigger packs hand out more cards instead

	SCRAP("Scrap Pack", 1_000d, 1, 1, 1.0d, null, Rarity.COMMON, false, 0d,
		new Color(0x9E, 0x9E, 0x9E),
		"1 card. Common only."),
	STANDARD("Standard Pack", 2_400d, 2, 1, 1.25d, null, Rarity.UNCOMMON, false, 2_000d,
		new Color(0x64, 0xB5, 0xF6),
		"2 cards. Common or Uncommon, in bulk."),
	GILDED("Gilded Pack", 15_000d, 3, 2, 1.0d, Rarity.UNCOMMON, Rarity.RARE, false, 20_000d,
		new Color(0x66, 0xBB, 0x6A),
		"3 cards. Uncommon or Rare."),
	CURATED("Curated Pack", 70_000d, 3, 11, 1.0d, Rarity.UNCOMMON, Rarity.RARE, true,
		100_000d, new Color(0x26, 0xC6, 0xDA),
		"3 cards from one chosen set. Uncommon or Rare."),
	PRISMATIC("Prismatic Pack", 350_000d, 4, 19, 1.5d, Rarity.RARE, Rarity.EPIC,
		false, 500_000d, new Color(0xAB, 0x47, 0xBC),
		"4 cards. Rare floor, about three in ten Epic."),
	ASCENDANT("Ascendant Pack", 2_000_000d, 5, 30, 1.6d, Rarity.EPIC,
		Rarity.LEGENDARY, false, 3_000_000d, new Color(0xFF, 0xB3, 0x00),
		"5 cards. Epic floor, about one in seven Legendary."),
	MYTHIC("Mythic Pack", 6_250_000d, 3, 28, 1.0d, Rarity.LEGENDARY, Rarity.LEGENDARY,
		false, 15_000_000d, new Color(0xFF, 0x70, 0x43),
		"3 cards, always Legendary. The cheapest Legendary copies in the game.");
	public static final int MAX_COPIES = 10;

	private final String displayName;
	private final double cost;
	private final int cardCount;
	private final int bulkCopies;
	private final double luck;
	private final Rarity floor;
	private final Rarity ceiling;
	private final boolean targetsSet;
	private final double unlockAtLifetimePoints;
	private final Color colour;
	private final String description;
	PackTier(String displayName, double cost, int cardCount, int bulkCopies, double luck,
			 Rarity floor, Rarity ceiling, boolean targetsSet, double unlockAtLifetimePoints,
			 Color colour, String description)
	{
		this.displayName = displayName;
		this.cost = cost;
		this.cardCount = cardCount;
		this.bulkCopies = bulkCopies;
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

	public Rarity lowestRarity()
	{
		return floor == null ? Rarity.COMMON : floor;
	}

	public int copiesFor(Rarity rarity)
	{
		double scale = rarity.copiesForMaxStars()
			/ (double) Rarity.COMMON.copiesForMaxStars();
		return Math.max(1, Math.min(MAX_COPIES, (int) Math.round(bulkCopies * scale)));
	}

	public double getCostPerCopy()
	{
		return cost / (cardCount * (double) copiesFor(lowestRarity()));
	}
}
