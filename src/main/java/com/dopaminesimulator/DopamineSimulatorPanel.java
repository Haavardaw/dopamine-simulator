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
package com.dopaminesimulator;

import com.dopaminesimulator.cards.Card;
import com.dopaminesimulator.cards.CardAffinity;
import com.dopaminesimulator.cards.CardCatalogue;
import com.dopaminesimulator.cards.CardCollection;
import com.dopaminesimulator.cards.CardSet;
import com.dopaminesimulator.cards.CollectionBonus;
import com.dopaminesimulator.cards.Rarity;
import com.dopaminesimulator.core.DopamineState;
import com.dopaminesimulator.core.IncomeTracker;
import com.dopaminesimulator.core.Reward;
import com.dopaminesimulator.feats.Achievement;
import com.dopaminesimulator.feats.Feat;
import com.dopaminesimulator.feats.Feats;
import com.dopaminesimulator.incremental.BigNumbers;
import com.dopaminesimulator.incremental.Milestones;
import com.dopaminesimulator.packs.PackTier;
import com.dopaminesimulator.pass.BattlePass;
import com.dopaminesimulator.pass.PassReward;
import com.dopaminesimulator.systems.BannerService;
import com.dopaminesimulator.systems.PassService;
import com.dopaminesimulator.points.ClickState;
import com.dopaminesimulator.points.PointSource;
import com.dopaminesimulator.ui.CardComponent;
import com.dopaminesimulator.ui.ClickButton;
import com.dopaminesimulator.ui.FeatRow;
import com.dopaminesimulator.ui.PointsHeader;
import com.dopaminesimulator.ui.ScrollableContent;
import com.dopaminesimulator.ui.SectionHeader;
import com.dopaminesimulator.ui.WrappedLabel;
import com.dopaminesimulator.ui.ShopRow;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;

public class DopamineSimulatorPanel extends PluginPanel
{
		private static final org.slf4j.Logger LOGGER =
		org.slf4j.LoggerFactory.getLogger(DopamineSimulatorPanel.class);
	private static final int CASCADE_INTERVAL_MS = 110;
	private static final int CARD_MIN_WIDTH = 38;
	private static final int CARD_MAX_WIDTH = 58;
	private static final int CARD_GAP = 3;
	private static final int CARD_MIN_COLUMNS = 3;
	private static final int CARD_MAX_COLUMNS = 10;
	private static final Color GOLD = new Color(0xFF, 0xB3, 0x00);
	private enum Tab
	{
		PLAY, SHOP, CARDS, FEATS
	}
	private final DopamineSimulatorPlugin plugin;
	private final DopamineSimulatorConfig config;
	private final AtomicBoolean refreshQueued = new AtomicBoolean();

	private Timer cascadeTimer;
	private ClickButton clickButton;
	private final Timer surgeTimer;
	private final JPanel playContent = new JPanel();
	private final JPanel shopContent = new JPanel();
	private final JPanel cardsContent = new JPanel();
	private final JPanel featsContent = new JPanel();
	private final JScrollPane scrollPane;
	private Tab selectedTab = Tab.PLAY;
	private Card selectedCard;
	private CardSet selectedSet = CardSet.QUESTS;
	private int buyQuantity = 1;
	private boolean collectionsExpanded;
	private String cardSearch = "";
	private boolean showingAchievements;
	private int shopView;
	private final JTextField searchField = new JTextField();
	DopamineSimulatorPanel(DopamineSimulatorPlugin plugin, DopamineSimulatorConfig config)
	{
		super(false);
		this.plugin = plugin;
		this.config = config;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		for (JPanel tabPanel : new JPanel[]{playContent, shopContent, cardsContent, featsContent})
		{
			tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));
			tabPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		}
		JPanel display = new JPanel(new BorderLayout());
		display.setBackground(ColorScheme.DARK_GRAY_COLOR);
		MaterialTabGroup tabGroup = new MaterialTabGroup(display);
		tabGroup.setLayout(new GridLayout(1, 4, 1, 0));
		tabGroup.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
		MaterialTab playTab = tab("Play", tabGroup, playContent);
		MaterialTab shopTab = tab("Shop", tabGroup, shopContent);
		MaterialTab cardsTab = tab("Cards", tabGroup, cardsContent);
		MaterialTab featsTab = tab("Feats", tabGroup, featsContent);
		playTab.setOnSelectEvent(() -> selectTab(Tab.PLAY));
		shopTab.setOnSelectEvent(() -> selectTab(Tab.SHOP));
		cardsTab.setOnSelectEvent(() -> selectTab(Tab.CARDS));
		featsTab.setOnSelectEvent(() -> selectTab(Tab.FEATS));

		tabGroup.addTab(playTab);
		tabGroup.addTab(shopTab);
		tabGroup.addTab(cardsTab);
		tabGroup.addTab(featsTab);
		ScrollableContent wrapper = new ScrollableContent();
		wrapper.setLayout(new BorderLayout());
		wrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		wrapper.add(display, BorderLayout.NORTH);
		scrollPane = new JScrollPane(wrapper,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);

		add(tabGroup, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		scrollPane.getViewport().addComponentListener(new ComponentAdapter()
		{
			private int lastColumns = -1;
			@Override
			public void componentResized(ComponentEvent e)
			{
				int columns = cardColumns();
				if (columns != lastColumns)
				{
					lastColumns = columns;
					if (selectedTab == Tab.CARDS)
					{
						rebuild();
					}
				}
			}
		});
		surgeTimer = new Timer(500, e ->
		{
			if (selectedTab == Tab.CARDS || !plugin.isPlayable())
			{
				return;
			}
			rebuild();
		});
		initSearchField();
		surgeTimer.start();
		tabGroup.select(playTab);
		rebuild();
	}
	private static MaterialTab tab(String name, MaterialTabGroup group, JComponent content)
	{
		MaterialTab created = new MaterialTab(name, group, content)
		{
			@Override
			public Insets getInsets()
			{
				Insets insets = super.getInsets();
				return new Insets(insets.top, 1, insets.bottom, 1);
			}
		};
		created.setFont(FontManager.getRunescapeSmallFont());
		created.setHorizontalAlignment(SwingConstants.CENTER);
		return created;
	}

	private boolean selectTab(Tab tab)
	{
		selectedTab = tab;
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
		rebuild();
		return true;
	}
	public void refresh()
	{
		if (selectedTab == Tab.CARDS)
		{
			SwingUtilities.invokeLater(() ->
			{
				if (config.autoReveal())
				{
					startCascade();
				}
			});
			return;
		}
		if (!refreshQueued.compareAndSet(false, true))
		{
			return;
		}
		SwingUtilities.invokeLater(() ->
		{
			refreshQueued.set(false);
			rebuild();
		});
	}
	private void rebuild()
	{
		int scrollPosition = scrollPane.getVerticalScrollBar().getValue();
		JPanel target = contentFor(selectedTab);
		target.removeAll();

		if (!plugin.isPlayable())
		{
			buildLockedTab();
		}
		else
		{
			DopamineState state = plugin.getEngine().getState();

			switch (selectedTab)
			{
				case PLAY:
					buildPlayTab(state);
					break;
				case SHOP:
					buildShopTab(state);
					break;
				case CARDS:
					buildCardsTab(state);
					break;
				case FEATS:
					buildFeatsTab(state);
					break;
			}
			if (config.autoReveal())
			{
				startCascade();
			}
		}
		target.revalidate();
		target.repaint();
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPosition));
	}

	private void buildLockedTab()
	{
		JPanel target = contentFor(selectedTab);

		JLabel title = new JLabel("Logged out");
		title.setFont(FontManager.getRunescapeBoldFont());
		title.setForeground(GOLD);
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		target.add(title);
		target.add(Box.createVerticalStrut(6));

		target.add(hint("Progress is saved per account, so nothing can be earned or spent from "
			+ "the login screen. Log in to carry on."));
	}

	private JPanel contentFor(Tab tab)
	{
		switch (tab)
		{
			case SHOP:
				return shopContent;
			case CARDS:
				return cardsContent;
			case FEATS:
				return featsContent;
			default:
				return playContent;
		}
	}

	private void buildPlayTab(DopamineState state)
	{
		IncomeTracker income = plugin.getIncomeTracker();
		ClickState clicks = plugin.getClickState();
		long now = System.currentTimeMillis();
		boolean surging = clicks != null && clicks.isSurging(now);
		double perHour = income.totalPerHour(state.getTick());
		playContent.add(pointsLine(state));
		playContent.add(Box.createVerticalStrut(8));
		playContent.add(buildClickButton(state, surging));
		playContent.add(Box.createVerticalStrut(8));
		JPanel waiting = revealQueueStrip();
		if (waiting != null)
		{
			playContent.add(waiting);
			playContent.add(Box.createVerticalStrut(8));
		}

		playContent.add(sectionLabel("Sources",
			BigNumbers.format(perHour) + "/hr"));
		playContent.add(hint("Each one pays more every time you upgrade it."));
		playContent.add(Box.createVerticalStrut(4));
		playContent.add(buildQuantitySelector());
		playContent.add(Box.createVerticalStrut(5));

		for (PointSource source : PointSource.values())
		{
			if (state.isSourceUnlocked(source))
			{
				playContent.add(upgradeRow(state, source, income));
				playContent.add(Box.createVerticalStrut(4));
			}
		}

		JPanel nextUnlock = nextUnlockRow(state);
		if (nextUnlock != null)
		{
			playContent.add(Box.createVerticalStrut(4));
			playContent.add(nextUnlock);
		}

		playContent.add(Box.createVerticalStrut(8));
		playContent.add(milestoneLine(state));
		playContent.add(featLine(state));
	}

	private ClickButton buildClickButton(DopamineState state, boolean surging)
	{
		if (clickButton == null)
		{
			clickButton = new ClickButton(this::pointsPerClick, plugin::click);
			clickButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		}

		clickButton.setIcon(plugin.getGameIcons()
			.forClick(state.getSourceUpgradeLevel(PointSource.CLICK)));
		clickButton.setSurging(surging);
		return clickButton;
	}
	private double pointsPerClick()
	{
		if (plugin.getEngine() == null)
		{
			return 0d;
		}
		DopamineState state = plugin.getEngine().getState();
		double surge = plugin.getClickState() != null
			&& plugin.getClickState().isSurging(System.currentTimeMillis())
			? ClickState.SURGE_MULTIPLIER : 1d;
		return PointSource.CLICK.pointsFor(1d, state.getSourceUpgradeLevel(PointSource.CLICK))
			* Milestones.globalMultiplier(state.getLifetimePoints()) * surge;
	}
	private JPanel nextUnlockRow(DopamineState state)
	{
		String name = null;
		String detail = null;
		double target = 0d;

		PointSource source = state.nextLockedSource();
		if (source != null)
		{
			name = source.getDisplayName();
			detail = source.getDescription();
			target = source.getUnlockAtLifetimePoints();
		}

		for (PackTier tier : PackTier.values())
		{
			if (state.isPackUnlocked(tier))
			{
				continue;
			}
			double at = tier.getUnlockAtLifetimePoints();
			if (target <= 0d || at < target)
			{
				name = tier.getDisplayName();
				detail = tier.getDescription();
				target = at;
			}
			break;
		}

		if (name == null)
		{
			return null;
		}

		double lifetime = state.getLifetimePoints();
		JPanel row = new JPanel();
		row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 2, 0, 0, GOLD),
			BorderFactory.createEmptyBorder(5, 6, 5, 6)));

		JLabel title = new JLabel("Next unlock: " + name);
		title.setFont(FontManager.getRunescapeSmallFont());
		title.setForeground(GOLD);
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.add(title);

		row.add(hint(detail, availableWidth() - 20));
		row.add(Box.createVerticalStrut(3));

		JProgressBar bar = new JProgressBar(0, 1000);
		bar.setValue((int) Math.min(1000, lifetime / target * 1000));
		bar.setStringPainted(true);
		bar.setString(BigNumbers.format(lifetime) + " / " + BigNumbers.format(target)
			+ " lifetime");
		bar.setFont(FontManager.getRunescapeSmallFont());
		bar.setForeground(GOLD);
		bar.setBackground(ColorScheme.DARK_GRAY_COLOR);
		bar.setAlignmentX(Component.LEFT_ALIGNMENT);
		bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));
		row.add(bar);

		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
		return row;
	}

	private ShopRow upgradeRow(DopamineState state, PointSource source, IncomeTracker income)
	{
		int level = state.getSourceUpgradeLevel(source);
		double cost = source.upgradeCostForMany(level, buyQuantity);
		boolean affordable = state.getPoints() >= cost;
		double rate = income.perHour(source, state.getTick());

		String effect = (rate > 0 ? BigNumbers.format(rate) + "/hr  •  " : "")
			+ "x" + String.format("%.2f", PointSource.multiplierForLevel(level))
			+ "  →  x" + String.format("%.2f", PointSource.multiplierForLevel(level + buyQuantity));

		ShopRow row = new ShopRow(
			source.getDisplayName(),
			effect,
			cost,
			source.getColour(),
			String.valueOf(level),
			affordable,
			state.getPoints() / cost,
			r -> plugin.buySourceUpgrade(source, buyQuantity));
		row.setIcon(plugin.getGameIcons().forSource(source));
		CardSet set = CollectionBonus.setFor(source);
		double fromCards = CollectionBonus.multiplierFor(state, source);
		row.setToolTipText(source.getDescription()
			+ "  \u2022  level " + level
			+ ", each adding "
			+ Math.round(PointSource.UPGRADE_GAIN * 100d) + "%"
			+ "  \u2022  " + multiplierText(fromCards) + " from " + set.getDisplayName() + " cards"
			+ "  \u2022  " + multiplierText(PointSource.multiplierForLevel(level) * fromCards)
			+ " total");
		return sized(row);
	}
	private JPanel shopToggle()
	{
		JPanel row = new JPanel(new GridLayout(1, 3, 4, 0));
		row.setBackground(ColorScheme.DARK_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
		row.add(toggleButton("Packs", shopView == 0, () -> shopView = 0));
		row.add(toggleButton("Pass", shopView == 1, () -> shopView = 1));
		row.add(toggleButton("Banner", shopView == 2, () -> shopView = 2));
		return row;
	}

	private void buildBannerTab(DopamineState state)
	{
		BannerService banner = plugin.getBannerService();
		Card featured = banner.featured(state);
		int pity = state.getBannerPity();

		JLabel header = new JLabel("Featured: " + featured.getName());
		header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		header.setForeground(featured.getRarity().getColour());
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		shopContent.add(header);
		shopContent.add(Box.createVerticalStrut(4));

		CardComponent art = new CardComponent(featured, state.getStars(featured.getId()),
			state.owns(featured.getId()), 72, plugin.getCardArtService(),
			state.isShiny(featured.getId()));
		art.setAlignmentX(Component.LEFT_ALIGNMENT);
		shopContent.add(art);
		shopContent.add(Box.createVerticalStrut(6));

		JProgressBar bar = new JProgressBar(0, BannerService.HARD_PITY);
		bar.setValue(pity);
		bar.setStringPainted(true);
		bar.setString(pity + " / " + BannerService.HARD_PITY + " to guaranteed");
		bar.setFont(FontManager.getRunescapeSmallFont());
		bar.setForeground(GOLD);
		bar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		bar.setAlignmentX(Component.LEFT_ALIGNMENT);
		bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		shopContent.add(bar);
		shopContent.add(Box.createVerticalStrut(5));
		shopContent.add(hint(String.format("%.1f%%", banner.rateAt(pity) * 100d)
			+ " this pull. Every pull also opens a Prismatic Pack. "
			+ BigNumbers.format(state.getBannerPulls()) + " pulls so far."));
		shopContent.add(Box.createVerticalStrut(8));

		for (int count : new int[]{1, 10})
		{
			double cost = BannerService.PULL_COST * count;
			shopContent.add(sized(new ShopRow(
				count == 1 ? "Pull" : "Pull x" + count,
				count + " pull" + (count == 1 ? "" : "s") + " on " + featured.getName(),
				cost,
				featured.getRarity().getColour(),
				String.valueOf(count),
				state.getPoints() >= cost,
				state.getPoints() / cost,
				r -> plugin.pullBanner(selectedSet, count))));
			shopContent.add(Box.createVerticalStrut(3));
		}
	}

	private void buildPassTab(DopamineState state)
	{
		int season = state.getPassSeason();
		int tier = BattlePass.tierAt(state.getPassXp(), season);
		PassService pass = plugin.getPassService();

		JLabel header = new JLabel("Season " + season + "   Tier " + tier + "/" + BattlePass.TIERS);
		header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		header.setForeground(GOLD);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		shopContent.add(header);
		shopContent.add(Box.createVerticalStrut(3));

		double into = BattlePass.xpIntoTier(state.getPassXp(), season);
		double need = tier >= BattlePass.TIERS ? 0d : BattlePass.xpForTier(tier + 1, season);
		JProgressBar bar = new JProgressBar(0, 1000);
		bar.setValue(need <= 0d ? 1000 : (int) Math.round(into / need * 1000d));
		bar.setStringPainted(true);
		bar.setString(need <= 0d
			? "Season complete"
			: (long) into + " / " + (long) need + " pass xp");
		bar.setFont(FontManager.getRunescapeSmallFont());
		bar.setForeground(GOLD);
		bar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		bar.setAlignmentX(Component.LEFT_ALIGNMENT);
		bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		shopContent.add(bar);
		shopContent.add(Box.createVerticalStrut(5));
		shopContent.add(hint("Pass xp comes from playing, capped per tick. It cannot be bought."));
		shopContent.add(Box.createVerticalStrut(8));

		int pending = pass.unclaimed(state).size();
		if (pending > 0)
		{
			JButton claim = new JButton("Claim " + pending + " reward" + (pending == 1 ? "" : "s"));
			claim.setFont(FontManager.getRunescapeSmallFont());
			claim.setForeground(GOLD);
			claim.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			claim.setFocusPainted(false);
			claim.setAlignmentX(Component.LEFT_ALIGNMENT);
			claim.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
			claim.addActionListener(e -> plugin.claimAllPassTiers(selectedSet));
			shopContent.add(claim);
			shopContent.add(Box.createVerticalStrut(6));
		}

		if (!state.isPassPremium())
		{
			double cost = BattlePass.premiumCost(season);
			shopContent.add(sized(new ShopRow(
				"Unlock Premium Track",
				"Doubles every tier this season",
				cost,
				GOLD,
				"+",
				state.getPoints() >= cost,
				state.getPoints() / cost,
				r -> plugin.buyPassPremium())));
			shopContent.add(Box.createVerticalStrut(6));
		}

		if (pass.canStartNextSeason(state))
		{
			JButton next = new JButton("Start season " + (season + 1));
			next.setFont(FontManager.getRunescapeSmallFont());
			next.setForeground(GOLD);
			next.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
			next.setFocusPainted(false);
			next.setAlignmentX(Component.LEFT_ALIGNMENT);
			next.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
			next.addActionListener(e -> plugin.startNextPassSeason());
			shopContent.add(next);
			shopContent.add(Box.createVerticalStrut(6));
		}

		for (int t = 1; t <= BattlePass.TIERS; t++)
		{
			shopContent.add(passRow(state, t, tier));
			shopContent.add(Box.createVerticalStrut(2));
		}
	}

	private JPanel passRow(DopamineState state, int tier, int reached)
	{
		int season = state.getPassSeason();
		boolean unlocked = reached >= tier;
		boolean milestone = BattlePass.isMilestone(tier);

		JPanel row = new JPanel(new BorderLayout(6, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
		row.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, milestone ? 3 : 2, 0, 0,
				unlocked ? GOLD : Color.DARK_GRAY),
			BorderFactory.createEmptyBorder(3, 6, 3, 6)));

		JLabel label = new JLabel("Tier " + tier);
		label.setFont(FontManager.getRunescapeSmallFont());
		label.setForeground(unlocked ? GOLD : Color.GRAY);
		label.setPreferredSize(new Dimension(46, 14));
		row.add(label, BorderLayout.WEST);

		JPanel rewards = new JPanel(new GridLayout(2, 1, 0, 1));
		rewards.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		rewards.add(passRewardLabel(state, tier, false, unlocked));
		rewards.add(passRewardLabel(state, tier, true, unlocked));
		row.add(rewards, BorderLayout.CENTER);
		row.setToolTipText("Tier " + tier + " of season " + season);
		return row;
	}

	private JLabel passRewardLabel(DopamineState state, int tier, boolean premium, boolean unlocked)
	{
		PassReward reward = premium
			? BattlePass.premiumReward(tier, state.getPassSeason())
			: BattlePass.freeReward(tier, state.getPassSeason());
		boolean claimed = state.isPassTierClaimed(tier, premium);
		boolean locked = premium && !state.isPassPremium();

		String prefix = premium ? "★ " : "";
		JLabel label = new JLabel(prefix + reward.describe() + (claimed ? "  (claimed)" : ""));
		label.setFont(FontManager.getRunescapeSmallFont());

		if (claimed)
		{
			label.setForeground(Color.DARK_GRAY);
		}
		else if (locked)
		{
			label.setForeground(Color.GRAY);
		}
		else if (unlocked)
		{
			label.setForeground(reward.colour());
		}
		else
		{
			label.setForeground(Color.GRAY);
		}

		if (unlocked && !claimed && !locked)
		{
			label.setToolTipText("Click to claim");
			label.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
			label.addMouseListener(new java.awt.event.MouseAdapter()
			{
				@Override
				public void mousePressed(java.awt.event.MouseEvent e)
				{
					plugin.claimPassTier(tier, premium, selectedSet);
				}
			});
		}
		return label;
	}

	private void buildShopTab(DopamineState state)
	{
		shopContent.add(pointsLine(state));
		shopContent.add(Box.createVerticalStrut(6));
		shopContent.add(shopToggle());
		shopContent.add(Box.createVerticalStrut(8));

		if (shopView == 1)
		{
			buildPassTab(state);
			return;
		}
		if (shopView == 2)
		{
			buildBannerTab(state);
			return;
		}

		shopContent.add(sectionLabel("Packs"));
		shopContent.add(hint("Bigger packs hold more cards and roll better odds. "
			+ "Curated draws only from the set chosen on the Cards tab."));
		shopContent.add(Box.createVerticalStrut(4));
		shopContent.add(buildQuantitySelector());
		shopContent.add(Box.createVerticalStrut(5));

		for (PackTier tier : PackTier.values())
		{
			shopContent.add(packRow(state, tier));
			shopContent.add(Box.createVerticalStrut(4));
		}
		shopContent.add(Box.createVerticalStrut(8));
		shopContent.add(hint("Rare guaranteed within "
			+ plugin.getPackService().packsUntilPity(state) + " packs."));
	}
	private ShopRow packRow(DopamineState state, PackTier tier)
	{
		boolean unlocked = state.isPackUnlocked(tier);
		double unitCost = plugin.getPackService().costOf(state, tier);
		double cost = unitCost * buyQuantity;
		boolean affordable = unlocked && state.getPoints() >= cost;
		if (!unlocked)
		{
			return sized(new ShopRow("???", "Reach "
				+ BigNumbers.format(tier.getUnlockAtLifetimePoints()) + " lifetime points",
				tier.getUnlockAtLifetimePoints(), Color.DARK_GRAY, "?",
				false, state.getLifetimePoints() / tier.getUnlockAtLifetimePoints(), null));
		}
		int totalCards = tier.getCardCount() * buyQuantity;

		StringBuilder effect = new StringBuilder(totalCards + " cards");
		if (tier.getFloor() != null)
		{
			effect.append("  •  ").append(tier.getFloor().getDisplayName()).append("+");
		}
		if (tier.getLuck() > 1d)
		{
			effect.append("  •  +").append(Math.round((tier.getLuck() - 1d) * 100d))
				.append("% top odds");
		}
		String name = tier.isTargetsSet()
			? tier.getDisplayName() + " (" + selectedSet.getDisplayName() + ")"
			: tier.getDisplayName();
		ShopRow row = new ShopRow(
			buyQuantity > 1 ? name + " x" + buyQuantity : name,
			effect.toString(),
			cost,
			tier.getColour(),
			String.valueOf(totalCards),
			affordable,
			state.getPoints() / cost,
			r -> plugin.buyPacks(tier, selectedSet, buyQuantity));
		row.setIcon(plugin.getGameIcons().forPack(tier));
		row.setToolTipText(tier.getDescription()
			+ "  \u2022  " + BigNumbers.format(tier.getCostPerCopy()) + " per copy"
			+ (buyQuantity > 1
				? "  \u2022  " + BigNumbers.format(unitCost) + " each, "
					+ BigNumbers.format(cost) + " for " + buyQuantity
				: "")
			+ "  \u2022  " + (tier.isTargetsSet()
				? "draws only from " + selectedSet.getDisplayName()
				: "draws from every set"));
		return sized(row);
	}
	private void buildCardsTab(DopamineState state)
	{
		int stars = state.getTotalStars();
		int maxStars = CardCatalogue.size() * Rarity.MAX_STARS;
		double complete = maxStars == 0 ? 0d : stars * 100d / maxStars;

		JLabel header = new JLabel(String.format("%.1f%% complete", complete));
		header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		header.setForeground(GOLD);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		cardsContent.add(header);
		cardsContent.add(Box.createVerticalStrut(3));

		JProgressBar overall = new JProgressBar(0, 1000);
		overall.setValue((int) Math.round(complete * 10d));
		overall.setStringPainted(true);
		overall.setString(state.getUniqueCardsOwned() + "/" + CardCatalogue.size() + " owned");
		overall.setFont(FontManager.getRunescapeSmallFont());
		overall.setForeground(GOLD);
		overall.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		overall.setAlignmentX(Component.LEFT_ALIGNMENT);
		overall.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		cardsContent.add(overall);
		cardsContent.add(Box.createVerticalStrut(5));
		cardsContent.add(hint(BigNumbers.format(stars) + " of " + BigNumbers.format(maxStars)
			+ " stars" + variantSummary(state)));
		cardsContent.add(Box.createVerticalStrut(8));
		cardsContent.add(buildSetSelector(state));
		cardsContent.add(Box.createVerticalStrut(4));
		cardsContent.add(buildSearchBox());
		cardsContent.add(Box.createVerticalStrut(8));
		buildSelectedSet(state);
	}

	private JPanel buildSearchBox()
	{
		JPanel row = new JPanel(new BorderLayout(4, 0));
		row.setBackground(ColorScheme.DARK_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.add(searchField, BorderLayout.CENTER);

		if (!cardSearch.isEmpty())
		{
			JButton clear = new JButton("x");
			clear.setFont(FontManager.getRunescapeSmallFont());
			clear.setFocusPainted(false);
			clear.setMargin(new Insets(0, 4, 0, 4));
			clear.addActionListener(e ->
			{
				searchField.setText("");
				searchField.requestFocusInWindow();
			});
			row.add(clear, BorderLayout.EAST);
		}

		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
		return row;
	}

	private void initSearchField()
	{
		searchField.setFont(FontManager.getRunescapeSmallFont());
		searchField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		searchField.setForeground(Color.LIGHT_GRAY);
		searchField.setCaretColor(Color.LIGHT_GRAY);
		searchField.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		searchField.setToolTipText("Filter this set by card name");
		searchField.getDocument().addDocumentListener(new DocumentListener()
		{
			private void changed()
			{
				String text = searchField.getText().trim();
				if (text.equals(cardSearch))
				{
					return;
				}
				cardSearch = text;
				selectedCard = null;
				SwingUtilities.invokeLater(() ->
				{
					rebuild();
					searchField.requestFocusInWindow();
				});
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				changed();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				changed();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				changed();
			}
		});
	}

	private List<Card> visibleCards()
	{
		List<Card> all = CardCatalogue.bySet(selectedSet);
		if (cardSearch.isEmpty())
		{
			return all;
		}

		String needle = cardSearch.toLowerCase();
		List<Card> matches = new ArrayList<>();
		for (Card card : all)
		{
			if (card.getName().toLowerCase().contains(needle))
			{
				matches.add(card);
			}
		}
		return matches;
	}

	private static String variantSummary(DopamineState state)
	{
		StringBuilder text = new StringBuilder();
		if (state.getShinyCount() > 0)
		{
			text.append("  •  ").append(state.getShinyCount()).append(" shiny");
		}
		if (state.getGildedCount() > 0)
		{
			text.append("  •  ").append(state.getGildedCount()).append(" gilded");
		}
		return text.toString();
	}

	private JPanel buildSetSelector(DopamineState state)
	{
		JPanel row = new JPanel(new BorderLayout(4, 0));
		row.setBackground(ColorScheme.DARK_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);

		JComboBox<CardSet> picker = new JComboBox<>(CardSet.values());
		picker.setSelectedItem(selectedSet);
		picker.setFont(FontManager.getRunescapeSmallFont());
		picker.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		picker.setForeground(Color.LIGHT_GRAY);
		picker.setFocusable(false);
		picker.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
				boolean selected, boolean focused)
			{
				super.getListCellRendererComponent(list, value, index, selected, focused);
				CardSet set = (CardSet) value;
				setFont(FontManager.getRunescapeSmallFont());
				setText(set.getDisplayName() + "   " + ownedIn(state, set) + "/"
					+ CardCatalogue.bySet(set).size());
				return this;
			}
		});
		picker.addActionListener(e ->
		{
			CardSet chosen = (CardSet) picker.getSelectedItem();
			if (chosen != null && chosen != selectedSet)
			{
				selectedSet = chosen;
				selectedCard = null;
				rebuild();
			}
		});

		row.add(picker, BorderLayout.CENTER);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
		return row;
	}

	private static String multiplierText(double multiplier)
	{
		return "x" + String.format(multiplier >= 10d ? "%.0f" : "%.2f", multiplier);
	}
	private void buildSelectedSet(DopamineState state)
	{
		List<Card> cards = visibleCards();
		int owned = ownedIn(state, selectedSet);
		int columns = cardColumns();
		int cardWidth = cardWidthFor(columns);
		cardsContent.add(sectionLabel(selectedSet.getDisplayName(),
			cardSearch.isEmpty()
				? owned + "/" + cards.size() + " cards"
				: cards.size() + " matching"));
		PointSource powers = CollectionBonus.sourceFor(selectedSet);
		JLabel effect = new JLabel(powers.getDisplayName() + " "
			+ multiplierText(CollectionBonus.multiplierFor(state, powers)));
		effect.setFont(FontManager.getRunescapeSmallFont());
		effect.setForeground(GOLD);
		effect.setAlignmentX(Component.LEFT_ALIGNMENT);
		effect.setIconTextGap(4);
		BufferedImage sourceIcon = plugin.getGameIcons().forSource(powers);
		if (sourceIcon != null)
		{
			effect.setIcon(new ImageIcon(sourceIcon));
		}
		cardsContent.add(effect);
		cardsContent.add(Box.createVerticalStrut(6));
		if (cardSearch.isEmpty())
		{
			buildCollections(state);
		}

		if (cards.isEmpty())
		{
			cardsContent.add(hint("No cards in " + selectedSet.getDisplayName()
				+ " match \"" + cardSearch + "\"."));
			return;
		}
		for (int start = 0; start < cards.size(); start += columns)
		{
			int end = Math.min(start + columns, cards.size());
			List<Card> row = cards.subList(start, end);
			cardsContent.add(cardRow(state, row, columns, cardWidth));
			cardsContent.add(Box.createVerticalStrut(4));

			if (selectedCard != null && row.contains(selectedCard))
			{
				cardsContent.add(cardDetail(state, selectedCard));
				cardsContent.add(Box.createVerticalStrut(4));
			}
		}
	}
	private void buildCollections(DopamineState state)
	{
		List<CardCollection> collections = CardCollection.inSet(selectedSet);
		if (collections.isEmpty())
		{
			return;
		}
		int done = CardCollection.tiersIn(state, selectedSet);

		JButton toggle = new JButton((collectionsExpanded ? "▾" : "▸")
			+ " Collections    " + done + "/" + CardCollection.maxTiersIn(selectedSet)
			+ "    each x1." + String.format("%02d",
				Math.round(CardCollection.BONUS_PER_COLLECTION * 100)));
		toggle.setFont(FontManager.getRunescapeSmallFont());
		toggle.setForeground(done > 0 ? GOLD : Color.LIGHT_GRAY);
		toggle.setFocusPainted(false);
		toggle.setHorizontalAlignment(SwingConstants.LEFT);
		toggle.setMargin(new Insets(3, 5, 3, 5));
		toggle.setAlignmentX(Component.LEFT_ALIGNMENT);
		toggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		toggle.addActionListener(e ->
		{
			collectionsExpanded = !collectionsExpanded;
			rebuild();
		});
		cardsContent.add(toggle);
		if (!collectionsExpanded)
		{
			cardsContent.add(Box.createVerticalStrut(6));
			return;
		}
		cardsContent.add(Box.createVerticalStrut(3));
		List<CardCollection> ordered = new ArrayList<>(collections);
		ordered.sort(Comparator.comparingDouble(
			(CardCollection c) -> -(c.ownedIn(state) / (double) Math.max(1, c.size()))));
		for (CardCollection collection : ordered)
		{
			cardsContent.add(collectionRow(state, collection));
			cardsContent.add(Box.createVerticalStrut(3));
		}
		cardsContent.add(Box.createVerticalStrut(6));
	}

	private JPanel collectionRow(DopamineState state, CardCollection collection)
	{
		int owned = collection.ownedIn(state);
		int tier = collection.tierIn(state);
		boolean complete = tier > 0;
		JPanel row = new JPanel(new BorderLayout(6, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 2, 0, 0, complete ? GOLD : Color.DARK_GRAY),
			BorderFactory.createEmptyBorder(4, 6, 4, 6)));
		JLabel name = new JLabel(collection.getName());
		name.setFont(FontManager.getRunescapeSmallFont());
		name.setForeground(complete ? GOLD : Color.LIGHT_GRAY);
		row.add(name, BorderLayout.WEST);
		JLabel progress = new JLabel(complete
			? collection.tierNameIn(state) + "  "
				+ multiplierText(Math.pow(1d + CardCollection.BONUS_PER_COLLECTION, tier))
			: owned + "/" + collection.size());
		progress.setFont(FontManager.getRunescapeSmallFont());
		progress.setForeground(complete ? GOLD : Color.GRAY);
		row.add(progress, BorderLayout.EAST);
		StringBuilder members = new StringBuilder(collection.getDescription());
		members.append("  \u2022  ").append(owned).append('/').append(collection.size())
			.append(" owned");
		for (Card card : collection.getCards())
		{
			if (!state.owns(card.getId()))
			{
				members.append("  \u2022  need ").append(card.getName());
			}
		}
		row.setToolTipText(members.toString());
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
		return row;
	}

	private int cardColumns()
	{
		int available = availableWidth();
		int fits = (available + CARD_GAP) / (CARD_MIN_WIDTH + CARD_GAP);
		return Math.max(CARD_MIN_COLUMNS, Math.min(CARD_MAX_COLUMNS, fits));
	}

	private int cardWidthFor(int columns)
	{
		int available = availableWidth() - (columns - 1) * CARD_GAP;
		return Math.max(CARD_MIN_WIDTH, Math.min(CARD_MAX_WIDTH, available / columns));
	}
	private JPanel cardRow(DopamineState state, List<Card> row, int columns, int cardWidth)
	{
		JPanel grid = new JPanel(new GridLayout(1, columns, CARD_GAP, 0))
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
			}
		};
		grid.setBackground(ColorScheme.DARK_GRAY_COLOR);
		grid.setAlignmentX(Component.LEFT_ALIGNMENT);
		for (Card card : row)
		{
			int copies = state.getCopies(card.getId());
			int stars = state.getStars(card.getId());
			CardComponent component = new CardComponent(card, stars, copies > 0, cardWidth,
				plugin.getCardArtService(), state.isShiny(card.getId()),
				state.isGilded(card.getId()));
			component.setToolTipText(cardTooltip(card, copies, stars, copies > 0));
			component.setOnClick(clicked ->
			{
				selectedCard = clicked.equals(selectedCard) ? null : clicked;
				rebuild();
			});
			grid.add(component);
		}

		for (int i = row.size(); i < columns; i++)
		{
			JPanel filler = new JPanel();
			filler.setBackground(ColorScheme.DARK_GRAY_COLOR);
			grid.add(filler);
		}
		return grid;
	}

	private JPanel cardDetail(DopamineState state, Card card)
	{
		int copies = state.getCopies(card.getId());
		int stars = state.getStars(card.getId());
		boolean owned = copies > 0;
		JPanel detail = new JPanel(new BorderLayout(8, 0));
		detail.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		detail.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 2, 0, 0, card.getRarity().getColour()),
			BorderFactory.createEmptyBorder(8, 8, 8, 8)));
		detail.setAlignmentX(Component.LEFT_ALIGNMENT);
		CardComponent big = new CardComponent(card, stars, owned, 72, plugin.getCardArtService(),
			state.isShiny(card.getId()), state.isGilded(card.getId()));
		big.playIntro();
		big.setOnClick(c -> {
			selectedCard = null;
			rebuild();
		});
		detail.add(big, BorderLayout.WEST);
		JPanel text = new JPanel();
		text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
		text.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		JLabel name = new JLabel(owned ? card.getName() : "???");
		name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		name.setForeground(card.getRarity().getColour());
		name.setAlignmentX(Component.LEFT_ALIGNMENT);
		text.add(name);
		JLabel meta = new JLabel(card.getRarity().getDisplayName()
			+ "  •  " + card.getSet().getDisplayName());
		meta.setFont(FontManager.getRunescapeSmallFont());
		meta.setForeground(Color.GRAY);
		meta.setAlignmentX(Component.LEFT_ALIGNMENT);
		text.add(meta);
		text.add(Box.createVerticalStrut(6));
		if (!owned)
		{
			text.add(hint("Not found yet. It comes from " + card.getSet().getDescription()
				.toLowerCase() + ". Try a Curated pack aimed at this set.", detailTextWidth()));
			detail.add(text, BorderLayout.CENTER);
			return capHeight(detail);
		}
		JLabel tier = new JLabel(stars + " / " + Rarity.MAX_STARS + "★   •   "
			+ copies + " copies");
		tier.setFont(FontManager.getRunescapeSmallFont());
		tier.setForeground(Color.LIGHT_GRAY);
		tier.setAlignmentX(Component.LEFT_ALIGNMENT);
		text.add(tier);
		text.add(Box.createVerticalStrut(4));
		text.add(cardEffectLine(state, card, stars));

		int next = card.getRarity().copiesForNextStar(copies);
		if (next > 0)
		{
			int previous = stars == 0 ? 0 : card.getRarity().starThresholds()[stars - 1];
			int span = Math.max(1, next - previous);
			JProgressBar bar = new JProgressBar(0, span);
			bar.setValue(Math.max(0, copies - previous));
			bar.setStringPainted(true);
			bar.setString((next - copies) + " more for " + (stars + 1) + "★");
			bar.setFont(FontManager.getRunescapeSmallFont());
			bar.setForeground(card.getRarity().getColour());
			bar.setBackground(ColorScheme.DARK_GRAY_COLOR);
			bar.setAlignmentX(Component.LEFT_ALIGNMENT);
			bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
			text.add(bar);
		}
		else
		{
			text.add(hint("Fully upgraded.", detailTextWidth()));
		}
		detail.add(text, BorderLayout.CENTER);
		return capHeight(detail);
	}

	private static JPanel capHeight(JPanel panel)
	{
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
		return panel;
	}

	private JComponent cardEffectLine(DopamineState state, Card card, int stars)
	{
		PointSource powers = CollectionBonus.sourceFor(card.getSet());
		StringBuilder text = new StringBuilder();
		String affinity = CardAffinity.describe(card);
		if (affinity != null)
		{
			text.append(affinity).append(": +")
				.append(CardAffinity.percentFor(state, card)).append('%');
			if (stars < Rarity.MAX_STARS)
			{
				text.append(" (+").append(CardAffinity.percentAtStars(stars + 1))
					.append("% at ").append(stars + 1).append("★)");
			}
		}

		for (CardCollection collection : CardCollection.forCard(card))
		{
			if (text.length() > 0)
			{
				text.append("  ");
			}
			text.append(collection.getName()).append(": ")
				.append(collection.ownedIn(state)).append('/')
				.append(collection.size()).append('.');
		}
		WrappedLabel label = new WrappedLabel(text.toString(),
			FontManager.getRunescapeSmallFont(), GOLD, Math.max(60, detailTextWidth()));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}
	private int detailTextWidth()
	{
		return availableWidth() - 106;
	}
	private static int ownedIn(DopamineState state, CardSet set)
	{
		return (int) CardCatalogue.bySet(set).stream().filter(c -> state.owns(c.getId())).count();
	}
	private static String cardTooltip(Card card, int copies, int stars, boolean owned)
	{
		if (!owned)
		{
			return "??? \u2022 " + card.getRarity().getDisplayName() + ", not yet found";
		}
		int next = card.getRarity().copiesForNextStar(copies);
		return card.getName()
			+ "  \u2022  " + card.getRarity().getDisplayName()
			+ "  \u2022  " + stars + "\u2605"
			+ "  \u2022  " + copies + " copies"
			+ (next > 0 ? "  \u2022  " + (next - copies) + " more for " + (stars + 1) + "\u2605"
				: "  \u2022  maxed");
	}
	private void startCascade()
	{
		if (cascadeTimer != null && cascadeTimer.isRunning())
		{
			return;
		}
		if (plugin.getRewards().isEmpty())
		{
			return;
		}

		cascadeTimer = new Timer(CASCADE_INTERVAL_MS, null);
		cascadeTimer.addActionListener(e ->
		{
			Reward reward = plugin.getRewards().claim();
			if (reward == null)
			{
				cascadeTimer.stop();
				rebuild();
				return;
			}

			try
			{
				acceptRevealed(reward);
			}
			catch (RuntimeException ex)
			{
				LOGGER.warn("Reveal effects failed for {}", reward.getTitle(), ex);
			}
			if (selectedTab == Tab.PLAY)
			{
				rebuild();
			}
		});
		cascadeTimer.start();
	}

	private void revealEverythingNow()
	{
		if (cascadeTimer != null)
		{
			cascadeTimer.stop();
		}
		for (Reward reward : plugin.getRewards().claimAll())
		{
			acceptRevealed(reward);
		}
		rebuild();
	}
	private void acceptRevealed(Reward reward)
	{
		plugin.flash(reward);
	}
	private JPanel revealQueueStrip()
	{
		int depth = plugin.getRewards().depth();
		if (depth <= 0)
		{
			return null;
		}

		JPanel header = new JPanel(new BorderLayout(4, 0));
		header.setBackground(ColorScheme.DARK_GRAY_COLOR);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

		JLabel label = new JLabel(depth + (depth == 1 ? " card waiting" : " cards waiting"));
		label.setFont(FontManager.getRunescapeBoldFont());
		label.setForeground(GOLD);
		header.add(label, BorderLayout.WEST);

		JButton skip = new JButton(config.autoReveal() ? "Skip" : "Reveal");
		skip.setFont(FontManager.getRunescapeSmallFont());
		skip.setFocusPainted(false);
		skip.addActionListener(e -> revealEverythingNow());
		header.add(skip, BorderLayout.EAST);
		return header;
	}


	private JPanel buildQuantitySelector()
	{
		JPanel row = new JPanel(new GridLayout(1, 4, 3, 0));
		row.setBackground(ColorScheme.DARK_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
		for (int quantity : new int[]{1, 5, 10, 25})
		{
			JButton button = new JButton("x" + quantity);
			button.setFont(FontManager.getRunescapeSmallFont());
			button.setForeground(buyQuantity == quantity ? GOLD : Color.LIGHT_GRAY);
			button.setFocusPainted(false);
			button.addActionListener(e ->
			{
				buyQuantity = quantity;
				rebuild();
			});
			row.add(button);
		}
		return row;
	}
	private PointsHeader pointsLine(DopamineState state)
	{
		double perHour = plugin.getIncomeTracker().totalPerHour(state.getTick());
		PointsHeader header = new PointsHeader(state.getPoints(), perHour,
			plugin.getClickState() != null
				&& plugin.getClickState().isSurging(System.currentTimeMillis()));
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
		return header;
	}
	private WrappedLabel featLine(DopamineState state)
	{
		int ranks = Feats.tiersEarned(state);
		Feat closest = null;
		double best = -1d;
		for (Feat feat : Feat.values())
		{
			long progress = Feats.progressOf(state, feat);
			long next = feat.nextThreshold(progress);
			if (next <= 0)
			{
				continue;
			}
			double fraction = progress / (double) next;
			if (fraction > best)
			{
				best = fraction;
				closest = feat;
			}
		}

		String next = closest == null
			? "every rank earned"
			: "next: " + closest.getDisplayName() + " at "
				+ BigNumbers.format(closest.nextThreshold(Feats.progressOf(state, closest)))
				+ " " + closest.getTrack().getUnit();
		return hint("Feats x" + String.format("%.2f", Feats.multiplierFor(state))
			+ " from " + ranks + " ranks, " + next);
	}

	private WrappedLabel milestoneLine(DopamineState state)
	{
		double next = Milestones.nextAt(state.getLifetimePoints());
		return hint("Milestone bonus x"
			+ String.format("%.1f", Milestones.globalMultiplier(state.getLifetimePoints()))
			+ (next > 0 ? ", next at " + BigNumbers.format(next) + " lifetime" : ", all earned"));
	}


	private void buildFeatsTab(DopamineState state)
	{
		featsContent.add(featsToggle());
		featsContent.add(Box.createVerticalStrut(8));

		if (showingAchievements)
		{
			buildAchievements(state);
			return;
		}

		int earned = Feats.tiersEarned(state);
		int total = Feat.totalTiers();

		JLabel header = new JLabel(Feats.titleFor(state));
		header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		header.setForeground(GOLD);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		featsContent.add(header);
		featsContent.add(Box.createVerticalStrut(3));

		JProgressBar overall = new JProgressBar(0, Math.max(1, total));
		overall.setValue(earned);
		overall.setStringPainted(true);
		overall.setString(earned + "/" + total + " ranks earned");
		overall.setFont(FontManager.getRunescapeSmallFont());
		overall.setForeground(GOLD);
		overall.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		overall.setAlignmentX(Component.LEFT_ALIGNMENT);
		overall.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		featsContent.add(overall);
		featsContent.add(Box.createVerticalStrut(5));
		featsContent.add(hint("x" + String.format("%.2f", Feats.multiplierFor(state))
			+ " to everything you earn. Feats cannot be bought, only played for."));
		featsContent.add(Box.createVerticalStrut(8));

		for (Feat feat : Feat.values())
		{
			featsContent.add(featRow(state, feat));
			featsContent.add(Box.createVerticalStrut(CARD_GAP));
		}
	}

	private JPanel featsToggle()
	{
		JPanel row = new JPanel(new GridLayout(1, 2, 4, 0));
		row.setBackground(ColorScheme.DARK_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
		row.add(toggleButton("Ranks", !showingAchievements, () -> showingAchievements = false));
		row.add(toggleButton("Achievements", showingAchievements, () -> showingAchievements = true));
		return row;
	}

	private JButton toggleButton(String text, boolean active, Runnable onClick)
	{
		JButton button = new JButton(text);
		button.setFont(FontManager.getRunescapeSmallFont());
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
		button.setBackground(active ? ColorScheme.DARKER_GRAY_HOVER_COLOR
			: ColorScheme.DARKER_GRAY_COLOR);
		button.setForeground(active ? GOLD : Color.LIGHT_GRAY);
		button.addActionListener(e ->
		{
			onClick.run();
			rebuild();
		});
		return button;
	}

	private void buildAchievements(DopamineState state)
	{
		int earned = 0;
		for (Achievement achievement : Achievement.values())
		{
			earned += state.hasAchievement(achievement.name()) ? 1 : 0;
		}

		JProgressBar overall = new JProgressBar(0, Achievement.values().length);
		overall.setValue(earned);
		overall.setStringPainted(true);
		overall.setString(earned + "/" + Achievement.values().length + " earned");
		overall.setFont(FontManager.getRunescapeSmallFont());
		overall.setForeground(GOLD);
		overall.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		overall.setAlignmentX(Component.LEFT_ALIGNMENT);
		overall.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
		featsContent.add(overall);
		featsContent.add(Box.createVerticalStrut(5));
		featsContent.add(hint("Can you complete them all?"));
		featsContent.add(Box.createVerticalStrut(8));

		for (Achievement achievement : Achievement.values())
		{
			featsContent.add(achievementRow(state, achievement));
			featsContent.add(Box.createVerticalStrut(2));
		}
	}

	private JPanel achievementRow(DopamineState state, Achievement achievement)
	{
		boolean earned = state.hasAchievement(achievement.name());
		boolean secret = achievement.isHidden() && !earned;

		JPanel row = new JPanel(new BorderLayout(6, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
		row.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 2, 0, 0, earned ? GOLD : Color.DARK_GRAY),
			BorderFactory.createEmptyBorder(4, 6, 4, 6)));

		JLabel name = new JLabel(secret ? "Hidden" : achievement.getDisplayName());
		name.setFont(FontManager.getRunescapeSmallFont());
		name.setForeground(earned ? GOLD : Color.LIGHT_GRAY);
		row.add(name, BorderLayout.NORTH);

		JLabel detail = new JLabel(secret
			? "Earn it to find out what it was"
			: achievement.getDescription());
		detail.setFont(FontManager.getRunescapeSmallFont());
		detail.setForeground(Color.GRAY);
		row.add(detail, BorderLayout.SOUTH);
		return row;
	}

	private FeatRow featRow(DopamineState state, Feat feat)
	{
		long progress = Feats.progressOf(state, feat);
		int tier = Feats.tierOf(state, feat);
		long next = feat.nextThreshold(progress);
		String unit = feat.getTrack().getUnit();

		String detail = tier >= feat.maxTier()
			? "Mastered  •  " + BigNumbers.format(progress) + " " + unit
			: BigNumbers.format(progress) + " / " + BigNumbers.format(next) + " " + unit;

		FeatRow row = new FeatRow(feat, tier, detail,
			next <= 0 ? 1d : progress / (double) next);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setToolTipText(feat.getDescription()
			+ "  •  each rank adds "
			+ Math.round(Feat.BONUS_PER_TIER * 100d) + "% to everything you earn");
		return row;
	}

	private ShopRow sized(ShopRow row)
	{
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ShopRow.HEIGHT));
		return row;
	}
	private SectionHeader sectionLabel(String text)
	{
		return sectionLabel(text, null);
	}
	private SectionHeader sectionLabel(String text, String trailing)
	{
		SectionHeader header = new SectionHeader(text, trailing);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
		return header;
	}
	private WrappedLabel hint(String text)
	{
		return hint(text, availableWidth());
	}
	private WrappedLabel hint(String text, int width)
	{
		WrappedLabel label = new WrappedLabel(text, FontManager.getRunescapeSmallFont(),
			new Color(0x86, 0x86, 0x8E), Math.max(60, width));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		return label;
	}
	private int availableWidth()
	{
		int viewport = scrollPane.getViewport().getWidth();
		return viewport > 40 ? viewport - 8 : PANEL_WIDTH - 30;
	}
}
