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

import com.dopaminesimulator.pass.PassReward;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.JComponent;

/**
 * A rung on the season track, laid out like a reward row in the game's own
 * interfaces: a sunken tier well on the left, the free reward above the premium one.
 */
public class PassTierRow extends JComponent
{
	public static final int HEIGHT = 38;

	private static final int WELL = 22;
	private static final int WELL_X = 5;

	private final int tier;
	private final boolean milestone;
	private final boolean reached;
	private final Color accent;
	private final PassReward free;
	private final PassReward premium;
	private final boolean freeClaimed;
	private final boolean premiumClaimed;
	private final boolean premiumOwned;
	private final BufferedImage freeIcon;
	private final BufferedImage premiumIcon;

	public PassTierRow(int tier, boolean milestone, boolean reached, boolean first, boolean last,
		Color accent, PassReward free, PassReward premium, boolean freeClaimed,
		boolean premiumClaimed, boolean premiumOwned, BufferedImage freeIcon,
		BufferedImage premiumIcon, Consumer<Boolean> onClaim)
	{
		this.tier = tier;
		this.milestone = milestone;
		this.reached = reached;
		this.accent = accent;
		this.free = free;
		this.premium = premium;
		this.freeClaimed = freeClaimed;
		this.premiumClaimed = premiumClaimed;
		this.premiumOwned = premiumOwned;
		this.freeIcon = freeIcon;
		this.premiumIcon = premiumIcon;

		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
		setMinimumSize(new Dimension(0, HEIGHT));
		setOpaque(false);

		if (onClaim != null)
		{
			addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					boolean lower = e.getY() > HEIGHT / 2;
					if (!reached)
					{
						return;
					}
					if (lower && premiumOwned && !premiumClaimed)
					{
						onClaim.accept(true);
					}
					else if (!lower && !freeClaimed)
					{
						onClaim.accept(false);
					}
				}
			});
		}
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics.create();
		Skin.pixel(g);

		int width = getWidth();
		Skin.row(g, 0, 0, width, HEIGHT, reached ? Skin.PANEL_LIT : Skin.PANEL,
			milestone && reached ? Skin.YELLOW : reached ? Skin.temper(accent) : null);

		drawTierNumber(g);

		int textX = WELL_X + WELL + 4;
		drawReward(g, free, freeIcon, textX, 15, freeClaimed, false);
		drawReward(g, premium, premiumIcon, textX, 30, premiumClaimed, true);

		g.dispose();
	}

	private void drawTierNumber(Graphics2D g)
	{
		g.setFont(milestone ? Skin.heading() : Skin.body());
		FontMetrics metrics = g.getFontMetrics();
		String label = String.valueOf(tier);
		Skin.text(g, label, WELL_X + (WELL - metrics.stringWidth(label)) / 2, 24,
			reached ? (milestone ? Skin.YELLOW : Skin.CREAM) : Skin.DIM);
	}

	private void drawReward(Graphics2D g, PassReward reward, BufferedImage icon, int x, int baseline,
		boolean claimed, boolean isPremium)
	{
		int size = 13;
		int iconY = baseline - size + 2;
		boolean available = reached && !claimed && (!isPremium || premiumOwned);
		boolean faded = claimed || !reached || (isPremium && !premiumOwned);

		if (icon != null)
		{
			Composite before = g.getComposite();
			if (faded)
			{
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
			}
			g.drawImage(icon, x, iconY, size, size, null);
			g.setComposite(before);
		}
		else
		{
			g.setColor(faded ? Skin.DIM : Skin.temper(reward.colour()));
			g.fillRect(x + 4, iconY + 4, 6, 6);
		}

		g.setFont(Skin.small());
		FontMetrics metrics = g.getFontMetrics();
		String text = reward.describe();
		if (isPremium && !premiumOwned)
		{
			text = "Premium: " + text;
		}

		String status = claimed ? "done" : available ? "claim" : null;
		int statusWidth = status == null ? 4 : metrics.stringWidth(status) + 8;
		int textX = x + size + 5;
		Skin.text(g, Skin.elide(metrics, text, getWidth() - textX - statusWidth - 6), textX,
			baseline, claimed ? Skin.DIM : available ? Skin.CREAM : Skin.DIM);

		if (status != null)
		{
			Skin.right(g, status, getWidth() - 6, baseline, claimed ? Skin.GREEN : Skin.YELLOW);
		}
	}
}
