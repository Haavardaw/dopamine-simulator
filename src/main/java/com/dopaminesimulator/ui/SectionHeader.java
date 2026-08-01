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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class SectionHeader extends JComponent
{
	private static final int HEIGHT = 20;

	private final String title;
	private final String trailing;

	public SectionHeader(String title)
	{
		this(title, null);
	}

	public SectionHeader(String title, String trailing)
	{
		this.title = title;
		this.trailing = trailing;
		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics.create();
		Skin.pixel(g);

		int width = getWidth();
		Skin.texture(g, 0, 0, width, HEIGHT, Skin.INSET);
		// a gold rule under the heading, the way the game underlines its own
		Skin.rule(g, 0, HEIGHT - 1, width, Skin.ORANGE.darker());

		int room = width - 12;
		if (trailing != null && !trailing.isEmpty())
		{
			g.setFont(Skin.small());
			FontMetrics metrics = g.getFontMetrics();
			Skin.right(g, trailing, width - 6, HEIGHT - 6, Skin.CREAM);
			room -= metrics.stringWidth(trailing) + 8;
		}

		g.setFont(Skin.body());
		Skin.text(g, Skin.elide(g.getFontMetrics(), title, Math.max(20, room)), 6, HEIGHT - 6,
			Skin.ORANGE);

		g.dispose();
	}
}
