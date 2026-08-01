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
package com.dopaminesimulator.ui;

import com.dopaminesimulator.feats.Feat;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;

public final class FeatBanner
{
	public static final int WIDTH = 300;
	public static final int HEIGHT = 64;

	private static final int MEDAL = 44;

	private FeatBanner()
	{
	}

	public static void draw(Graphics2D g, String title, String caption, Color rank,
		int tier, double shine)
	{
		Skin.pixel(g);
		Skin.plate(g, 0, 0, WIDTH, HEIGHT, Skin.PANEL);
		g.setColor(Skin.YELLOW);
		g.drawRect(1, 1, WIDTH - 3, HEIGHT - 3);

		drawMedal(g, Skin.temper(rank), tier);

		int textX = MEDAL + 16;
		int room = WIDTH - textX - 10;

		g.setFont(Skin.small());
		Skin.text(g, tier > 0 ? "Feat earned" : "Achievement", textX, 19, Skin.CREAM);

		g.setFont(Skin.heading());
		Skin.text(g, Skin.elide(g.getFontMetrics(), title, room), textX, 39, Skin.ORANGE);

		g.setFont(Skin.small());
		Skin.text(g, Skin.elide(g.getFontMetrics(), tier > 0
			? "Rank " + tier + " of " + Feat.RANKS + "   +"
				+ Math.round(Feat.BONUS_PER_TIER * 100d) + "% to everything"
			: caption, room), textX, 55, Skin.YELLOW);

		drawSweep(g, shine);
	}

	private static void drawMedal(Graphics2D g, Color rank, int tier)
	{
		int y = (HEIGHT - MEDAL) / 2;
		Skin.well(g, 10, y, MEDAL, MEDAL, Skin.INSET_DEEP);

		String label = tier > 0 ? String.valueOf(tier) : "*";
		g.setFont(Skin.heading());
		Skin.centred(g, label, 10, MEDAL, y + (MEDAL + g.getFontMetrics().getAscent()) / 2 - 3, rank);
	}

	/** A single bright pass across the plate: the whole flourish, and it is over quickly. */
	private static void drawSweep(Graphics2D g, double shine)
	{
		int x = (int) Math.round(-WIDTH * 0.4d + shine * WIDTH * 1.6d);
		Shape clip = g.getClip();
		g.clipRect(2, 2, WIDTH - 4, HEIGHT - 4);
		g.setPaint(new GradientPaint(x, 0, Skin.withAlpha(Color.WHITE, 0),
			x + 40, 0, Skin.withAlpha(Color.WHITE, 40)));
		g.fillRect(x, 2, 80, HEIGHT - 4);
		g.setClip(clip);
	}
}
