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
package com.dopaminesimulator.incremental;

public final class Prestige
{
	public static final int STARS_PER_INSIGHT = 100;

	public static final int MIN_STARS = 2_500;

	/**
	 * Insight makes every upgrade level worth more, so it lifts the ceiling income
	 * settles at rather than multiplying what you already earn.
	 */
	public static final double GAIN_PER_INSIGHT = 0.04d;

	private Prestige()
	{
	}

	public static int insightFor(int totalStars)
	{
		return Math.max(0, totalStars / STARS_PER_INSIGHT);
	}

	public static boolean canPrestige(int totalStars)
	{
		return totalStars >= MIN_STARS;
	}

	public static int starsUntilPrestige(int totalStars)
	{
		return Math.max(0, MIN_STARS - totalStars);
	}

	public static double gainMultiplier(int insight)
	{
		return 1d + GAIN_PER_INSIGHT * Math.max(0, insight);
	}
}
