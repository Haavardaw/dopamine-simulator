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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 * A button cut from the same stone as the panels: raised at rest, sunken while
 * held, with the label in interface orange.
 */
public class StoneButton extends JButton
{
	private Color accent = Skin.ORANGE;

	public StoneButton(String text)
	{
		super(text);
		setFont(Skin.body());
		setFocusPainted(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		setOpaque(false);
		setForeground(Skin.ORANGE);
	}

	public StoneButton withAccent(Color colour)
	{
		this.accent = colour;
		setForeground(colour);
		return this;
	}

	@Override
	public void setBorder(Border border)
	{
		// the stone edge is painted, so a second bordered edge would double up
	}

	@Override
	public Insets getInsets()
	{
		return new Insets(4, 8, 4, 8);
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics.create();
		Skin.pixel(g);

		boolean held = getModel().isArmed() && getModel().isPressed();
		boolean hovered = getModel().isRollover();

		if (!isEnabled())
		{
			Skin.well(g, 0, 0, getWidth(), getHeight(), Skin.INSET_DEEP);
		}
		else if (held)
		{
			Skin.well(g, 0, 0, getWidth(), getHeight(), Skin.INSET);
		}
		else
		{
			Skin.plate(g, 0, 0, getWidth(), getHeight(), hovered ? Skin.PANEL_LIT : Skin.PANEL);
		}
		g.dispose();

		Color wanted = isEnabled() ? accent : Skin.DIM;
		if (!wanted.equals(getForeground()))
		{
			setForeground(wanted);
		}
		super.paintComponent(graphics);
	}
}
