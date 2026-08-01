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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class PassHeader extends JComponent
{
	public static final int HEIGHT = 96;

	private static final int TITLE_H = 22;
	private static final int ROW_H = 16;

	private final int season;
	private final String theme;
	private final Color accent;
	private final int tier;
	private final int tiers;
	private final double into;
	private final double need;
	private final boolean premium;
	private final String remaining;

	public PassHeader(int season, String theme, Color accent, int tier, int tiers,
		double into, double need, boolean premium, String remaining)
	{
		this.season = season;
		this.theme = theme;
		this.accent = accent;
		this.tier = tier;
		this.tiers = tiers;
		this.into = into;
		this.need = need;
		this.premium = premium;
		this.remaining = remaining;
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
		Color accentText = Skin.temper(accent);

		Skin.plate(g, 0, 0, width, HEIGHT, Skin.PANEL);

		// title bar: the region name is the pass name, so it gets the full width
		Skin.well(g, 0, 0, width, TITLE_H, Skin.INSET);
		g.setFont(Skin.heading());
		Skin.centred(g, Skin.elide(g.getFontMetrics(), theme, width - 12), 0, width,
			TITLE_H - 6, accentText);

		int y = TITLE_H + 3;
		row(g, width, y, "Season", season + " - " + (premium ? "premium" : "free"),
			premium ? Skin.YELLOW : Skin.CREAM);
		row(g, width, y + ROW_H, "Tier", tier + " / " + tiers, Skin.CREAM);
		row(g, width, y + ROW_H * 2, "Ends in", remaining, Skin.CREAM);

		int barY = y + ROW_H * 3 + 2;
		g.setFont(Skin.small());
		Skin.bar(g, 4, barY, width - 8, 13, need <= 0d ? 1d : into / need, Skin.YELLOW,
			need <= 0d ? "Season complete" : (long) into + " / " + (long) need + " xp");

		g.dispose();
	}

	private void row(Graphics2D g, int width, int y, String label, String value, Color valueColour)
	{
		Skin.texture(g, 3, y, width - 6, ROW_H - 1, Skin.PANEL_LIT);
		g.setFont(Skin.small());
		FontMetrics metrics = g.getFontMetrics();
		int baseline = y + ROW_H - 6;
		Skin.text(g, label, 8, baseline, Skin.ORANGE);
		int room = width - 16 - metrics.stringWidth(label) - 8;
		Skin.right(g, Skin.elide(metrics, value, room), width - 8, baseline, valueColour);
	}
}
