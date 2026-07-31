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
package com.dopaminesimulator.points;

import lombok.Getter;

import java.awt.Color;

@Getter
public enum PointSource
{
	CLICK("Clicking", "Click the Play tab's coins for money!", 0d,
		new Color(0xFF, 0xB3, 0x00), 1.0d, 1_000d),
	EXPERIENCE("Experience", "XP in any skill", 50d,
		new Color(0x42, 0xA5, 0xF5), 0.0167d, 60_000d),
	COMBAT("Combat", "Things killed", 500d,
		new Color(0xE5, 0x53, 0x53), 5.5d, 180d),

	IDLING("Bank Standing", "Ticks where nothing else earned you anything", 2_500d,
		new Color(0x8D, 0x6E, 0x63), 0.167d, 6_000d),
	RECOVERY("Eating", "Hitpoints restored", 5_000d,
		new Color(0xEC, 0x40, 0x7A), 3.3d, 300d),
	TRAVEL("Travel", "Distance covered", 50_000d,
		new Color(0x66, 0xBB, 0x6A), 0.4d, 2_500d),
	WEALTH("Wealth", "Value of loot", 500_000d,
		new Color(0xFF, 0xD5, 0x4F), 0.3d, 3_300d),
	PRESENCE("Presence", "Levelling up", 5_000_000d,
		new Color(0xAB, 0x47, 0xBC), 0.167d, 6_000d);

	public static final double UPGRADE_MULTIPLIER = 1.35d;

	public static final double UPGRADE_COST_GROWTH = UPGRADE_MULTIPLIER;

	public static final double TARGET_HOURLY_INCOME = 1_000d;

	public static final double UPGRADE_COST_HOURS = 0.5d;
	private final String displayName;
	private final String description;
	private final double unlockAtLifetimePoints;
	private final Color colour;
	private final double basePointsPerUnit;
	private final double typicalUnitsPerHour;
	PointSource(String displayName, String description, double unlockAtLifetimePoints,
				Color colour, double basePointsPerUnit, double typicalUnitsPerHour)
	{
		this.displayName = displayName;
		this.description = description;
		this.unlockAtLifetimePoints = unlockAtLifetimePoints;
		this.colour = colour;
		this.basePointsPerUnit = basePointsPerUnit;
		this.typicalUnitsPerHour = typicalUnitsPerHour;
	}
	public double baseHourlyIncome()
	{
		return basePointsPerUnit * typicalUnitsPerHour;
	}


	public boolean isUnlockedAt(double lifetimePoints)
	{
		return lifetimePoints >= unlockAtLifetimePoints;
	}

	public double upgradeCost(int currentLevel)
	{
		return baseHourlyIncome() * UPGRADE_COST_HOURS
			* Math.pow(UPGRADE_COST_GROWTH, currentLevel);
	}
	public double upgradeCostForMany(int currentLevel, int count)
	{
		if (count <= 0)
		{
			return 0d;
		}
		double first = upgradeCost(currentLevel);
		return first * (Math.pow(UPGRADE_COST_GROWTH, count) - 1d) / (UPGRADE_COST_GROWTH - 1d);
	}
	public static double multiplierForLevel(int level)
	{
		return Math.pow(UPGRADE_MULTIPLIER, Math.max(0, level));
	}
	public double pointsFor(double units, int upgradeLevel)
	{
		return units * basePointsPerUnit * multiplierForLevel(upgradeLevel);
	}
	public static PointSource byId(String id)
	{
		for (PointSource source : values())
		{
			if (source.name().equals(id))
			{
				return source;
			}
		}
		return null;
	}
}
