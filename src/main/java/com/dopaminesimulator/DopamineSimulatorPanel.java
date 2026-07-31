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
import com.dopaminesimulator.core.RewardQueue;
import com.dopaminesimulator.incremental.BigNumbers;
import com.dopaminesimulator.incremental.Milestones;
import com.dopaminesimulator.packs.PackTier;
import com.dopaminesimulator.points.ClickState;
import com.dopaminesimulator.points.PointSource;
import com.dopaminesimulator.ui.CardComponent;
import com.dopaminesimulator.ui.ClickButton;
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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
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
	private static final int RECENT_LIMIT = 5;
	private static final int CARD_MIN_WIDTH = 38;
	private static final int CARD_MAX_WIDTH = 58;
	private static final int CARD_GAP = 3;
	private static final int CARD_MIN_COLUMNS = 3;
	private static final int CARD_MAX_COLUMNS = 10;
	private static final Color GOLD = new Color(0xFF, 0xB3, 0x00);
	private enum Tab
	{
		PLAY, UPGRADES, SHOP, CARDS
	}
	private final DopamineSimulatorPlugin plugin;
	private final DopamineSimulatorConfig config;
	private final Deque<Reward> recent = new ArrayDeque<>();
	private final AtomicBoolean refreshQueued = new AtomicBoolean();

	private Timer cascadeTimer;
	private ClickButton clickButton;
	private final Timer surgeTimer;
	private final JPanel playContent = new JPanel();
	private final JPanel upgradesContent = new JPanel();
	private final JPanel shopContent = new JPanel();
	private final JPanel cardsContent = new JPanel();
	private final JScrollPane scrollPane;
	private Tab selectedTab = Tab.PLAY;
	private Card selectedCard;
	private CardSet selectedSet = CardSet.QUESTS;
	private int buyQuantity = 1;
	private boolean collectionsExpanded;
	DopamineSimulatorPanel(DopamineSimulatorPlugin plugin, DopamineSimulatorConfig config)
	{
		super(false);
		this.plugin = plugin;
		this.config = config;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		for (JPanel tabPanel : new JPanel[]{playContent, upgradesContent, shopContent, cardsContent})
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
		MaterialTab upgradesTab = tab("Upgrades", tabGroup, upgradesContent);
		MaterialTab shopTab = tab("Shop", tabGroup, shopContent);
		MaterialTab cardsTab = tab("Cards", tabGroup, cardsContent);
		playTab.setOnSelectEvent(() -> selectTab(Tab.PLAY));
		upgradesTab.setOnSelectEvent(() -> selectTab(Tab.UPGRADES));
		shopTab.setOnSelectEvent(() -> selectTab(Tab.SHOP));
		cardsTab.setOnSelectEvent(() -> selectTab(Tab.CARDS));

		tabGroup.addTab(playTab);
		tabGroup.addTab(upgradesTab);
		tabGroup.addTab(shopTab);

		tabGroup.addTab(cardsTab);
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
				case UPGRADES:
					buildUpgradesTab(state);
					break;
				case SHOP:
					buildShopTab(state);
					break;
				case CARDS:
					buildCardsTab(state);
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
			case UPGRADES:
				return upgradesContent;
			case SHOP:
				return shopContent;
			case CARDS:
				return cardsContent;
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
		playContent.add(Box.createVerticalStrut(10));
		playContent.add(sectionLabel("Sources"));
		playContent.add(Box.createVerticalStrut(4));
		for (PointSource source : PointSource.values())
		{
			if (state.isSourceUnlocked(source))
			{
				playContent.add(sourceRow(state, source, income));
				playContent.add(Box.createVerticalStrut(3));
			}
		}
		PointSource next = state.nextLockedSource();
		if (next != null)
		{
			playContent.add(Box.createVerticalStrut(4));
			playContent.add(lockedSourceRow(state, next));
		}
		playContent.add(Box.createVerticalStrut(10));
		buildRecentReveals();
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
	private JPanel sourceRow(DopamineState state, PointSource source, IncomeTracker income)
	{
		int level = state.getSourceUpgradeLevel(source);
		double rate = income.perHour(source, state.getTick());
		JPanel row = new JPanel(new BorderLayout(4, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 3, 0, 0, source.getColour()),
			BorderFactory.createEmptyBorder(4, 6, 4, 6)));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
		JPanel text = new JPanel();
		text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
		text.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		JLabel name = new JLabel(source.getDisplayName());
		name.setForeground(source.getColour());
		name.setAlignmentX(Component.LEFT_ALIGNMENT);
		text.add(name);
		JLabel detail = new JLabel(rate > 0
			? BigNumbers.format(rate) + "/hr"
			: source.getDescription());
		detail.setFont(FontManager.getRunescapeSmallFont());
		detail.setForeground(Color.GRAY);
		detail.setAlignmentX(Component.LEFT_ALIGNMENT);
		text.add(detail);
		row.add(text, BorderLayout.CENTER);

		double fromUpgrades = PointSource.multiplierForLevel(level);
		double fromCards = CollectionBonus.multiplierFor(state, source);

		JLabel multiplier = new JLabel(multiplierText(fromUpgrades * fromCards));
		multiplier.setFont(FontManager.getRunescapeSmallFont());
		multiplier.setForeground(level > 0 || fromCards > 1d ? Color.WHITE : Color.DARK_GRAY);
		row.add(multiplier, BorderLayout.EAST);
		CardSet set = CollectionBonus.setFor(source);
		row.setToolTipText("<html>" + source.getDescription()
			+ "<br>" + multiplierText(fromUpgrades) + " from " + level + " upgrades"
			+ "<br>" + multiplierText(fromCards) + " from your " + set.getDisplayName() + " cards"
			+ " (" + state.getStarsInSet(set) + "★, "
			+ CardCollection.completedIn(state, set) + " collections)"
			+ "<br><b>" + multiplierText(fromUpgrades * fromCards) + " total</b></html>");
		return row;
	}
	private JPanel lockedSourceRow(DopamineState state, PointSource source)
	{
		JPanel row = new JPanel(new BorderLayout(4, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		JLabel label = new JLabel("Locked at "
			+ BigNumbers.format(source.getUnlockAtLifetimePoints()) + " lifetime");
		label.setFont(FontManager.getRunescapeSmallFont());
		label.setForeground(Color.GRAY);
		row.add(label, BorderLayout.WEST);
		JProgressBar bar = new JProgressBar(0, 1000);
		bar.setValue((int) Math.min(1000,
			state.getLifetimePoints() / source.getUnlockAtLifetimePoints() * 1000));
		bar.setForeground(Color.DARK_GRAY);
		bar.setBackground(ColorScheme.DARK_GRAY_COLOR);
		bar.setPreferredSize(new Dimension(56, 8));
		row.add(bar, BorderLayout.EAST);

		return row;
	}
	private void buildUpgradesTab(DopamineState state)
	{
		upgradesContent.add(pointsLine(state));
		upgradesContent.add(Box.createVerticalStrut(6));
		upgradesContent.add(buildQuantitySelector());
		upgradesContent.add(Box.createVerticalStrut(8));
		upgradesContent.add(sectionLabel("Multipliers"));
		upgradesContent.add(hint("Permanently increase what an activity is worth."));
		upgradesContent.add(Box.createVerticalStrut(5));
		for (PointSource source : PointSource.values())
		{
			if (state.isSourceUnlocked(source))
			{
				upgradesContent.add(upgradeRow(state, source));
				upgradesContent.add(Box.createVerticalStrut(4));
			}
		}
		upgradesContent.add(Box.createVerticalStrut(10));
		upgradesContent.add(Box.createVerticalStrut(8));
		upgradesContent.add(milestoneLine(state));
	}

	private ShopRow upgradeRow(DopamineState state, PointSource source)
	{
		int level = state.getSourceUpgradeLevel(source);
		double cost = source.upgradeCostForMany(level, buyQuantity);
		boolean affordable = state.getPoints() >= cost;
		String effect = "x" + String.format("%.2f", PointSource.multiplierForLevel(level))
			+ "  →  x" + String.format("%.2f", PointSource.multiplierForLevel(level + buyQuantity));

		ShopRow row = new ShopRow(
			source.getDisplayName() + " upgrade",
			effect,
			cost,
			source.getColour(),
			String.valueOf(level),
			affordable,
			state.getPoints() / cost,
			r -> plugin.buySourceUpgrade(source, buyQuantity));
		row.setIcon(plugin.getGameIcons().forSource(source));
		row.setToolTipText("<html><b>" + source.getDisplayName() + "</b><br>"
			+ source.getDescription() + "<br>Level " + level
			+ " (each level is x" + String.format("%.2f", PointSource.UPGRADE_MULTIPLIER)
			+ ")</html>");
		return sized(row);
	}
	private void buildShopTab(DopamineState state)
	{
		shopContent.add(pointsLine(state));
		shopContent.add(Box.createVerticalStrut(6));

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
			effect.append("  •  x").append((int) tier.getLuck()).append(" odds");
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
		row.setToolTipText("<html>" + tier.getDescription() + "<br>"
			+ BigNumbers.format(tier.getCostPerCard()) + " per card<br>"
			+ (buyQuantity > 1
				? BigNumbers.format(unitCost) + " each, " + BigNumbers.format(cost) + " for "
					+ buyQuantity + "<br>"
				: "")
			+ (tier.isTargetsSet() ? "Draws only from " + selectedSet.getDisplayName()
			: "Draws from every set") + "</html>");
		return sized(row);
	}
	private void buildCardsTab(DopamineState state)
	{
		int stars = state.getTotalStars();
		int maxStars = CardCatalogue.size() * Rarity.MAX_STARS;
		JLabel header = new JLabel(stars + " / " + maxStars + "★");
		header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		header.setForeground(GOLD);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		cardsContent.add(header);

		cardsContent.add(hint(state.getUniqueCardsOwned() + "/" + CardCatalogue.size()
			+ " owned. Duplicates raise a card up to ten tiers"));
		cardsContent.add(Box.createVerticalStrut(8));
		cardsContent.add(buildSetSelector(state));
		cardsContent.add(Box.createVerticalStrut(8));
		buildSelectedSet(state);
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
		List<Card> cards = CardCatalogue.bySet(selectedSet);
		int owned = ownedIn(state, selectedSet);
		int columns = cardColumns();
		int cardWidth = cardWidthFor(columns);
		cardsContent.add(sectionLabel(selectedSet.getDisplayName(),
			owned + "/" + cards.size() + " cards"));
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
		buildCollections(state);
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
		int done = CardCollection.completedIn(state, selectedSet);

		JButton toggle = new JButton("<html>" + (collectionsExpanded ? "▾" : "▸")
			+ " Collections &nbsp; <font color='#909090'>" + done + "/" + collections.size()
			+ " &nbsp; each x1." + String.format("%02d",
				Math.round(CardCollection.BONUS_PER_COLLECTION * 100)) + "</font></html>");
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
		boolean complete = collection.isComplete(state);
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
			? multiplierText(1d + CardCollection.BONUS_PER_COLLECTION)
			: owned + "/" + collection.size());
		progress.setFont(FontManager.getRunescapeSmallFont());
		progress.setForeground(complete ? GOLD : Color.GRAY);
		row.add(progress, BorderLayout.EAST);
		StringBuilder members = new StringBuilder("<html>" + collection.getDescription() + "<br>");
		for (Card card : collection.getCards())
		{
			boolean has = state.owns(card.getId());
			members.append(has ? "<font color='#FFB300'>&#10003; " : "<font color='#808080'>&#8226; ")
				.append(card.getName()).append("</font><br>");
		}
		members.append("</html>");
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
				plugin.getCardArtService());
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
		CardComponent big = new CardComponent(card, stars, owned, 72, plugin.getCardArtService());
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
			return "<html><b>???</b><br>" + card.getRarity().getDisplayName()
				+ ", not yet found</html>";
		}
		int next = card.getRarity().copiesForNextStar(copies);
		return "<html><b>" + card.getName() + "</b><br>"
			+ card.getRarity().getDisplayName() + "  •  " + stars + "★<br>"
			+ copies + " copies"
			+ (next > 0 ? "<br>" + (next - copies) + " more for " + (stars + 1) + "★" : "<br>Maxed")
			+ "</html>";
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
		recent.addFirst(reward);
		while (recent.size() > RECENT_LIMIT)
		{
			recent.removeLast();
		}
	}
	private void buildRecentReveals()
	{
		RewardQueue queue = plugin.getRewards();
		int depth = queue.depth();
		JPanel header = new JPanel(new BorderLayout(4, 0));
		header.setBackground(ColorScheme.DARK_GRAY_COLOR);
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
		JLabel label = new JLabel(depth > 0 ? depth + " revealing..." : "Recent");
		label.setFont(FontManager.getRunescapeBoldFont());
		label.setForeground(depth > 0 ? GOLD : Color.LIGHT_GRAY);
		header.add(label, BorderLayout.WEST);
		if (depth > 0)
		{
			JButton skip = new JButton(config.autoReveal() ? "Skip" : "Reveal");
			skip.setFont(FontManager.getRunescapeSmallFont());
			skip.setFocusPainted(false);
			skip.addActionListener(e -> revealEverythingNow());
			header.add(skip, BorderLayout.EAST);
		}
		playContent.add(header);
		playContent.add(Box.createVerticalStrut(4));
		if (recent.isEmpty())
		{
			playContent.add(hint("Revealed cards appear here."));
			return;
		}
		for (Reward reward : recent)
		{
			playContent.add(rewardRow(reward));
			playContent.add(Box.createVerticalStrut(2));
		}
	}

	private JPanel rewardRow(Reward reward)
	{
		JPanel row = new JPanel(new BorderLayout(4, 0));
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		row.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
		JLabel title = new JLabel(reward.getTitle());
		title.setForeground(reward.getRarity() != null ? reward.getRarity().getColour() : Color.WHITE);
		row.add(title, BorderLayout.WEST);
		JLabel detail = new JLabel(shortDetail(reward));
		detail.setForeground(Color.GRAY);
		detail.setFont(FontManager.getRunescapeSmallFont());
		row.add(detail, BorderLayout.EAST);
		row.setToolTipText(reward.getDetail());
		return row;
	}
	private static String shortDetail(Reward reward)
	{
		switch (reward.getType())
		{
			case NEW_CARD:
				return "NEW";
			case DUPLICATE:
				return "+" + reward.getAmount();
			case STAR_UP:
				return reward.getAmount() + "★";
			case FUSION:
				return "FUSED";
			case SET_COMPLETE:
				return "SET";
			case SOURCE_UNLOCKED:
				return "NEW";
			default:
				return "";
		}
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
	private WrappedLabel milestoneLine(DopamineState state)
	{
		double next = Milestones.nextAt(state.getLifetimePoints());
		return hint("Milestone bonus x"
			+ String.format("%.1f", Milestones.globalMultiplier(state.getLifetimePoints()))
			+ (next > 0 ? ", next at " + BigNumbers.format(next) + " lifetime" : ", all earned"));
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
