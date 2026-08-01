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
package com.dopaminesimulator.systems;

import com.dopaminesimulator.core.DopamineEvent;
import com.dopaminesimulator.core.DopamineState;
import com.dopaminesimulator.core.DopamineSystem;
import com.dopaminesimulator.core.RewardQueue;
import com.dopaminesimulator.core.SkillWeights;
import com.dopaminesimulator.pass.BattlePass;

public class PassSystem implements DopamineSystem
{
	private static final double PER_KILL = 1.0d;
	private static final double XP_PER_POINT = 400d;
	private static final double TILES_PER_POINT = 25d;
	private static final double DAMAGE_PER_POINT = 20d;
	private static final double HEALED_PER_POINT = 20d;
	private static final double LOOT_PER_POINT = 5_000d;
	private static final double PER_LEVEL = 25d;
	private static final double IDLE_TRICKLE = 0.05d;

	private double earnedThisTick;

	@Override
	public String getName()
	{
		return "Battle Pass";
	}

	@Override
	public void handle(DopamineState state, DopamineEvent event, RewardQueue rewards)
	{
		switch (event.getType())
		{
			case TICK:
				earnedThisTick = 0d;
				if (state.isIdle())
				{
					grant(state, IDLE_TRICKLE);
				}
				break;
			case NPC_KILLED:
				grant(state, PER_KILL);
				break;
			case LEVEL_UP:
				grant(state, PER_LEVEL);
				break;
			case XP_GAINED:
				grant(state, SkillWeights.weightedXp(event.getKey(), event.getAmount())
					/ XP_PER_POINT);
				break;
			case DISTANCE_TRAVELLED:
				grant(state, event.getAmount() / TILES_PER_POINT);
				break;
			case DAMAGE_TAKEN:
				grant(state, event.getAmount() / DAMAGE_PER_POINT);
				break;
			case HEALTH_RESTORED:
				grant(state, event.getAmount() / HEALED_PER_POINT);
				break;
			case LOOT_RECEIVED:
				grant(state, event.getAmount() / LOOT_PER_POINT);
				break;
			default:
				break;
		}
	}

	private void grant(DopamineState state, double amount)
	{
		double room = BattlePass.MAX_XP_PER_TICK - earnedThisTick;
		double granted = Math.max(0d, Math.min(amount, room));
		if (granted <= 0d)
		{
			return;
		}
		earnedThisTick += granted;
		state.addPassXp(granted);
	}
}
