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

import java.awt.Color;

@Getter
public enum Rarity
{
	COMMON("Common", new Color(0xA6, 0xB2, 0xC4), 0.6000d),
	UNCOMMON("Uncommon", new Color(0x4C, 0xAF, 0x50), 0.2700d),
	RARE("Rare", new Color(0x42, 0xA5, 0xF5), 0.1000d),
	EPIC("Epic", new Color(0xAB, 0x47, 0xBC), 0.0270d),
	LEGENDARY("Legendary", new Color(0xFF, 0xB3, 0x00), 0.0030d);

	private final String displayName;
	private final Color colour;
	private final double packWeight;

	Rarity(String displayName, Color colour, double packWeight)
	{
		this.displayName = displayName;
		this.colour = colour;
		this.packWeight = packWeight;
	}




	public boolean isPityWorthy()
	{
		return ordinal() >= RARE.ordinal();
	}

	public static final int MAX_STARS = 10;

	public int[] starThresholds()
	{
		switch (this)
		{
			case COMMON:
				return new int[]{1, 2, 5, 10, 20, 40, 80, 160, 320, 600};
			case UNCOMMON:
				return new int[]{1, 2, 4, 8, 16, 32, 64, 125, 240, 450};
			case RARE:
				return new int[]{1, 2, 4, 7, 13, 25, 48, 90, 170, 320};
			case EPIC:
				return new int[]{1, 2, 3, 6, 10, 18, 32, 58, 105, 190};
			default:
				return new int[]{1, 2, 3, 4, 7, 11, 17, 27, 42, 65};
		}
	}
	public int copiesForMaxStars()
	{
		return starThresholds()[MAX_STARS - 1];
	}
	public int starsFor(int copies)
	{
		int[] thresholds = starThresholds();
		int stars = 0;
		for (int threshold : thresholds)
		{
			if (copies >= threshold)
			{
				stars++;
			}
		}
		return stars;
	}
	public int copiesForNextStar(int copies)
	{
		int[] thresholds = starThresholds();
		for (int threshold : thresholds)
		{
			if (copies < threshold)
			{
				return threshold;
			}
		}
		return 0;
	}
	public Rarity next()
	{
		return this == LEGENDARY ? null : values()[ordinal() + 1];
	}

}
