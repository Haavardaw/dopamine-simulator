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

import com.dopaminesimulator.core.EventType;
import com.dopaminesimulator.incremental.Prestige;
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
	SUFFERING("Suffering", "Damage taken", 5_000_000d,
		new Color(0xAB, 0x47, 0xBC), 1.0d, 1_000d);

	/**
	 * Output and cost both climb geometrically, cost slightly faster.
	 *
	 * <p>Additive gain against exponential cost was tried first, on the reasoning
	 * that it would flatten income the way xp per hour flattens. It flattened
	 * something else instead: the payback on an upgrade is cost over gain, which
	 * with those two shapes is 1.19 x 1.18^level and therefore unbounded. Payback
	 * passed a day of play by level twenty and two million hours by level eighty
	 * seven, so upgrades were irrational to buy after about the fifth one and a
	 * simulated five hundred hour run stalled at level nineteen by hour twenty
	 * five.
	 *
	 * <p>With both geometric, payback becomes (cost/gain)^level, so the gap
	 * between these two numbers is the whole design. At 1.11 against 1.18 an
	 * upgrade pays back in about four hours early and about a day by the time
	 * cost finally outruns income near level thirty, which is where prestige
	 * takes over.
	 */
	public static final double UPGRADE_GAIN_GROWTH = 1.11d;

	public static final double UPGRADE_COST_GROWTH = 1.18d;

	/**
	 * A click pays this many seconds of your current income, rather than a flat
	 * sum that the rest of the economy leaves behind.
	 *
	 * <p>Four seconds makes a single click a visible number rather than a
	 * rounding error, which two was: thirty clicks in an hour came to under two
	 * percent of it. The runaway that a larger figure used to cause is held by
	 * the hourly allowance in ClickState rather than by keeping this small, so
	 * the click can feel worth pressing without ever beating real play.
	 */
	public static final double CLICK_SECONDS = 4d;

	/**
	 * Clicking earns a share of everything else, so there is no level to sell for
	 * it. It stays a source for attribution and for the income breakdown.
	 */
	public boolean isUpgradeable()
	{
		return this != CLICK;
	}

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

	public double hourlyGainAt(int level)
	{
		return baseHourlyIncome()
			* (multiplierForLevel(level + 1) - multiplierForLevel(level));
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
		double total = 0d;
		for (int i = 0; i < count; i++)
		{
			total += upgradeCost(currentLevel + i);
		}
		return total;
	}
	public static double multiplierForLevel(int level)
	{
		return multiplierForLevel(level, 0);
	}

	/**
	 * Insight scales the output rather than the growth rate. Folding it into the
	 * growth would let a prestiged player push gain past cost growth, at which
	 * point payback falls with every level and upgrades never stop being free.
	 */
	public static double multiplierForLevel(int level, int insight)
	{
		return Math.pow(UPGRADE_GAIN_GROWTH, Math.max(0, level))
			* Prestige.gainMultiplier(insight);
	}
	public double pointsFor(double units, int upgradeLevel, int insight)
	{
		return units * basePointsPerUnit * multiplierForLevel(upgradeLevel, insight);
	}
	public static PointSource forEvent(EventType type)
	{
		switch (type)
		{
			case CLICK:
				return CLICK;
			case XP_GAINED:
			case LEVEL_UP:
				return EXPERIENCE;
			case NPC_KILLED:
				return COMBAT;
			case LOOT_RECEIVED:
				return WEALTH;
			case DISTANCE_TRAVELLED:
				return TRAVEL;
			case HEALTH_RESTORED:
				return RECOVERY;
			case DAMAGE_TAKEN:
				return SUFFERING;
			case TICK:
				return IDLING;
			default:
				return null;
		}
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
