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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class FeatRow extends JComponent
{
	public static final int HEIGHT = 38;

	private static final int RANK_W = 22;

	private final String name;
	private final String progressText;
	private final int tier;
	private final int maxTier;
	private final double fraction;
	private final boolean mastered;

	public FeatRow(Feat feat, int tier, String progressText, double fraction)
	{
		this.name = feat.getDisplayName();
		this.progressText = progressText;
		this.tier = tier;
		this.maxTier = feat.maxTier();
		this.fraction = Math.max(0d, Math.min(1d, fraction));
		this.mastered = tier >= feat.maxTier();
		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
		setMinimumSize(new Dimension(0, HEIGHT));
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics.create();
		Skin.pixel(g);

		int width = getWidth();
		Color rank = Skin.temper(Feat.tierColour(tier));

		Skin.row(g, 0, 0, width, HEIGHT, Skin.PANEL, tier > 0 ? rank : null);

		// the rank reads as a number in its own column, not as a medal in a well
		g.setFont(Skin.heading());
		FontMetrics heading = g.getFontMetrics();
		String label = tier > 0 ? String.valueOf(tier) : "-";
		Skin.text(g, label, 7 + (RANK_W - heading.stringWidth(label)) / 2, 24,
			tier > 0 ? rank : Skin.DIM);

		int textX = 7 + RANK_W + 6;

		g.setFont(Skin.small());
		FontMetrics small = g.getFontMetrics();
		String ranks = tier + "/" + maxTier;
		Skin.right(g, ranks, width - 6, 15, mastered ? Skin.YELLOW : Skin.DIM);

		g.setFont(Skin.body());
		Skin.text(g, Skin.elide(g.getFontMetrics(), name,
			width - textX - small.stringWidth(ranks) - 14), textX, 16,
			mastered ? Skin.YELLOW : tier > 0 ? Skin.ORANGE : Skin.CREAM);

		g.setFont(Skin.small());
		Skin.text(g, Skin.elide(small, progressText, width - textX - 8), textX, 29,
			mastered ? Skin.GREEN : Skin.CREAM);

		if (!mastered)
		{
			g.setColor(rank);
			g.fillRect(textX, HEIGHT - 4, (int) Math.round((width - textX - 6) * fraction), 2);
		}

		g.dispose();
	}
}
