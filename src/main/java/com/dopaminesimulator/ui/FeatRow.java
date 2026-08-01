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
	public static final int HEIGHT = 46;

	private static final int MEDAL = 28;
	private static final int PAD = 5;

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

		Skin.plate(g, 0, 0, width, HEIGHT - 1, tier > 0 ? Skin.PANEL_LIT : Skin.PANEL);
		if (mastered)
		{
			g.setColor(Skin.YELLOW);
			g.drawRect(1, 1, width - 3, HEIGHT - 4);
		}

		drawMedal(g, rank);

		int textX = PAD + MEDAL + 6;
		int textWidth = Math.max(20, width - textX - PAD);

		String ranks = tier + " / " + maxTier;
		g.setFont(Skin.small());
		FontMetrics small = g.getFontMetrics();
		int ranksWidth = small.stringWidth(ranks) + 6;

		g.setFont(Skin.body());
		Skin.text(g, Skin.elide(g.getFontMetrics(), name, textWidth - ranksWidth), textX, 16,
			tier > 0 ? Skin.ORANGE : Skin.CREAM);

		g.setFont(Skin.small());
		Skin.right(g, ranks, width - PAD, 15, tier > 0 ? rank : Skin.DIM);
		Skin.text(g, Skin.elide(small, progressText, textWidth), textX, 29, Skin.CREAM);

		if (!mastered)
		{
			Skin.bar(g, textX, HEIGHT - 14, textWidth, 8, fraction, Skin.YELLOW);
		}

		g.dispose();
	}

	private void drawMedal(Graphics2D g, Color rank)
	{
		int y = (HEIGHT - 1 - MEDAL) / 2;
		Skin.well(g, PAD, y, MEDAL, MEDAL, Skin.INSET_DEEP);

		String label = tier > 0 ? String.valueOf(tier) : "-";
		g.setFont(Skin.body());
		FontMetrics metrics = g.getFontMetrics();
		Skin.text(g, label, PAD + (MEDAL - metrics.stringWidth(label)) / 2,
			y + (MEDAL + metrics.getAscent()) / 2 - 2, tier > 0 ? rank : Skin.DIM);
	}
}
