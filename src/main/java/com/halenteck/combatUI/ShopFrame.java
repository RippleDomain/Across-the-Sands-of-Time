package com.halenteck.combatUI;

import com.halenteck.CombatGame.Character;
import com.halenteck.CombatGame.ToolStore;
import com.halenteck.commonUI.GameSelectionMenu;
import com.halenteck.server.Server;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicInteger;

public class ShopFrame extends JFrame {
    static JPanel characterDisplayPanel = new JPanel();
    private static JPanel itemPanel = new JPanel();
    private JLabel currencyLabel = new JLabel("Currency: $" + Server.getUserData().getMoney(), SwingConstants.CENTER);
    private static ShopFrame instance;

    public ShopFrame() {

        instance = this;
        setTitle("Tool Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // shortcut for returning to game selection menu
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    new GameSelectionMenu();
                    dispose();
                }
            }
        });

        // character-based display panels
        JPanel[] characterDisplayPanels = new JPanel[Server.getUserData().getUnlockedCharacterCount()];
        for (int i = 0; i < characterDisplayPanels.length; i++) {
            characterDisplayPanels[i] = createCharacterDisplayPanel(i);
        }

        // shop panel
        JPanel shopPanel = new JPanel(new BorderLayout());
        // main shop panel, goes inside the larger shop panel
        JPanel mainShopPanel = new JPanel(new BorderLayout());

        // id for character and shop menu selection
        AtomicInteger menuSelection = new AtomicInteger(0);
        AtomicInteger characterSelection = new AtomicInteger(0);

        // character item display panels (creates panels for each character)
        JPanel[][] itemPanels = new JPanel[characterDisplayPanels.length][3];
        for (int i = 0; i < characterDisplayPanels.length; i++) {
            itemPanels[i] = createItemPanel(i);
        }

        // initially, first character info (weapon menu) is shown
        itemPanel.add(itemPanels[0][0]);
        mainShopPanel.add(itemPanel, BorderLayout.CENTER);
        characterDisplayPanel.add(characterDisplayPanels[0]);
        add(characterDisplayPanel, BorderLayout.WEST);

        // constant button panel
        JPanel shopButtonPanel = new JPanel(new GridLayout(4, 1));
        currencyLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        currencyLabel.setFont(new Font("Sans Serif", Font.BOLD, 16));
        currencyLabel.setBackground(Color.YELLOW);
        shopButtonPanel.add(currencyLabel);
        JButton weaponButton = new JButton("Weapon");
        weaponButton.setFont(new Font("Sans Serif", Font.BOLD, 16)); // Adjust font size as needed
        weaponButton.setBorder(BorderFactory.createRaisedBevelBorder()); // Adjust border style
        JButton armourButton = new JButton("Armour");
        armourButton.setFont(new Font("Sans Serif", Font.BOLD, 16)); // Adjust font size as needed
        armourButton.setBorder(BorderFactory.createRaisedBevelBorder()); // Adjust border style
        JButton abilityButton = new JButton("Special Ability");
        abilityButton.setFont(new Font("Sans Serif", Font.BOLD, 16)); // Adjust font size as needed
        abilityButton.setBorder(BorderFactory.createRaisedBevelBorder()); // Adjust border style

        weaponButton.addActionListener(e -> {
            weaponButton.setBackground(Color.GREEN);
            armourButton.setBackground(Color.WHITE);
            abilityButton.setBackground(Color.WHITE);
            menuSelection.set(0);
            updatePanels(characterSelection.get(), menuSelection.get());
        });
        shopButtonPanel.add(weaponButton);

        armourButton.addActionListener(e -> {
            armourButton.setBackground(Color.GREEN);
            weaponButton.setBackground(Color.WHITE);
            abilityButton.setBackground(Color.WHITE);
            menuSelection.set(1);
            updatePanels(characterSelection.get(), menuSelection.get());
        });
        shopButtonPanel.add(armourButton);

        abilityButton.addActionListener(e -> {
            abilityButton.setBackground(Color.GREEN);
            armourButton.setBackground(Color.WHITE);
            weaponButton.setBackground(Color.WHITE);
            menuSelection.set(2);
            updatePanels(characterSelection.get(), menuSelection.get());
        });
        shopButtonPanel.add(abilityButton);
        mainShopPanel.add(shopButtonPanel, BorderLayout.WEST);


        // horizontal slider for switching between character-based panels
        JSlider characterSlider = new JSlider(JSlider.HORIZONTAL, 0, characterDisplayPanels.length - 1, 0);
        mainShopPanel.add(characterSlider, BorderLayout.SOUTH);
        characterSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                int newValue = characterSlider.getValue();
                int adjustedValue = Math.min(newValue, itemPanels.length - 1); // ensuring value is within bounds
                characterSelection.set(adjustedValue);
                updatePanels(characterSelection.get(), menuSelection.get());
            }
        });
        characterSlider.setMinorTickSpacing(1);


        // bottom buttons
        JPanel bottomButtonPanel = new JPanel(new GridLayout(2, 5));
        for (int i = 0; i < 5; i++) {
            bottomButtonPanel.add(new JLabel());
        }
        JButton upgradeButton = new JButton("Upgrade");
        upgradeButton.setFont(new Font("Sans Serif", Font.BOLD, 14));
        upgradeButton.addActionListener(e -> {
            ShopFrame.getInstance().setVisible(false);
            UpgradeShopFrame.getInstance().setVisible(true);
        });
        bottomButtonPanel.add(upgradeButton);
        bottomButtonPanel.add(new JLabel());
        bottomButtonPanel.add(new JLabel());
        bottomButtonPanel.add(new JLabel());
        JButton joinBattleButton = new JButton("Join Battle");
        joinBattleButton.setFont(new Font("Sans Serif", Font.BOLD, 14));
        joinBattleButton.addActionListener(e -> {
            try {
                new InGameFrame();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            dispose();
        });
        bottomButtonPanel.add(joinBattleButton);

        shopPanel.add(mainShopPanel, BorderLayout.CENTER);
        shopPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        add(shopPanel, BorderLayout.CENTER);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void updatePanels(int characterId, int shopId) {
        System.out.println("Character ID: " + characterId + ", Shop ID: " + shopId);

        // update currency label
        currencyLabel.setText("Currency: $" + Server.getUserData().getMoney());
        // Update character shop panel based on slider value
        itemPanel.removeAll();
        itemPanel.revalidate();
        JPanel[] panels = createItemPanel(characterId);
        itemPanel.add(panels[shopId]);
        itemPanel.revalidate();  // Inform the panel layout needs to be updated

        // Update character display panel based on slider value
        characterDisplayPanel.removeAll();
        characterDisplayPanel.revalidate();
        characterDisplayPanel.add(createCharacterDisplayPanel(characterId));
        characterDisplayPanel.revalidate();  // Inform the frame layout needs to be updated
        revalidate();
    }

    protected static JPanel[] createItemPanel(int characterIndex) {
        JPanel[] panels = new JPanel[3];

        JPanel weaponPanel = new JPanel(new BorderLayout());
        // price panel
        JPanel weaponPricePanel = new JPanel(new GridLayout(1, 7));
        weaponPricePanel.add(new JLabel("" + characterIndex));
        JLabel weaponPriceLabel1 = new JLabel("$ 100", SwingConstants.LEFT);
        weaponPricePanel.add(weaponPriceLabel1);
        JButton buyGunButton1 = new JButton("BOUGHT");
        buyGunButton1.setEnabled(false);
        weaponPricePanel.add(buyGunButton1);
        weaponPricePanel.add(new JLabel());
        JLabel gunPriceLabel2 = new JLabel("$ 200", SwingConstants.LEFT);
        weaponPricePanel.add(gunPriceLabel2);
        JButton buyGunButton2 = new JButton("BUY");
        buyGunButton2.addActionListener(e -> {
            if (ToolStore.buyWeapon((byte) characterIndex)) {
                instance.updatePanels(characterIndex, 0);
            }
            if (Server.getUserData().getCharacters()[characterIndex].getUnlockedWeapons().length > 1) {
                buyGunButton2.setText("BOUGHT");
                buyGunButton2.setEnabled(false);
            }
        });
        if (Server.getUserData().getCharacters()[characterIndex].getUnlockedWeapons().length > 1) {
            buyGunButton2.setText("BOUGHT");
            buyGunButton2.setEnabled(false);
        }
        weaponPricePanel.add(buyGunButton2);
        weaponPricePanel.add(new JLabel());
        weaponPanel.add(weaponPricePanel, BorderLayout.NORTH);
        // weapon display panel
        JPanel weaponDisplayPanel = new JPanel(new GridLayout(2, 2));
        // weapon image
        for (int i = 0; i < 2; i++) {
            ImageIcon weaponImageIcon = new ImageIcon(ShopFrame.class.getResource("/weapon" + (i + 1) + ".png"));
            Image scaledWeaponImage = weaponImageIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH); // Scales to 150 width, 100 height while maintaining aspect ratio
            ImageIcon scaledWeaponImageIcon = new ImageIcon(scaledWeaponImage); // Create a new ImageIcon from the scaled image
            JLabel weaponImageLabel = new JLabel(scaledWeaponImageIcon);
            weaponDisplayPanel.add(weaponImageLabel);
            weaponImageLabel.setToolTipText("Attack Damage: " + (i + 1) * 10);
        }
        for (int i = 0; i < 2; i++) {
            JLabel weaponNameLabel = new JLabel();
            switch (i) {
                case 0:
                    weaponNameLabel.setText("short-range weapon");
                    break;
                case 1:
                    weaponNameLabel.setText("long-range gun");
                    break;
            }
            weaponDisplayPanel.add(weaponNameLabel);
        }
        weaponPanel.add(weaponDisplayPanel, BorderLayout.CENTER);
        // weapon equip panel
        JPanel weaponEquipPanel = new JPanel(new GridLayout(1, 3));
        for (int i = 0; i < 3; i++) {
            if (Server.getUserData().getLastSelectedCharacter() == characterIndex) {
                JLabel equippedWeaponLabel = new JLabel("Equipped!", SwingConstants.CENTER);
                weaponEquipPanel.add(equippedWeaponLabel);
            } else {
                weaponEquipPanel.add(new JLabel());
            }
        }
        weaponPanel.add(weaponEquipPanel, BorderLayout.SOUTH);
        panels[0] = weaponPanel;

        // armour panel
        JPanel armourPanel = new JPanel(new BorderLayout());
        // armour prices
        JPanel armourPricePanel = new JPanel(new GridLayout(1, 6));
        for (int i = 0; i < 3; i++) {
            JLabel armourPriceLabel = new JLabel("$ " + (10 + i) * 5, SwingConstants.LEFT);
            armourPricePanel.add(armourPriceLabel);
            JButton buyArmourButton = new JButton("BUY");
            int finalI = i;
            buyArmourButton.addActionListener(e -> {
                if (ToolStore.buyArmour((byte) finalI)) {
                    instance.updatePanels(characterIndex, 1);
                }
            });
            if (Server.getUserData().getArmorLevel() >= (finalI + 1)) {
                buyArmourButton.setText("BOUGHT");
                buyArmourButton.setEnabled(false);
            }
            armourPricePanel.add(buyArmourButton);
        }

        weaponPricePanel.add(buyGunButton2);
        armourPanel.add(armourPricePanel, BorderLayout.NORTH);
        // armour display panel
        JPanel armourDisplayPanel = new JPanel(new GridLayout(2, 3));
        // armour image
        for (int i = 0; i < 3; i++) {
            ImageIcon armourImageIcon = new ImageIcon(ShopFrame.class.getResource("/armour" + (i + 1) + ".png"));
            Image scaledArmourImage = armourImageIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH); // Scales to 150 width, 100 height while maintaining aspect ratio
            ImageIcon scaledArmourImageIcon = new ImageIcon(scaledArmourImage); // Create a new ImageIcon from the scaled image
            JLabel armourImageLabel = new JLabel(scaledArmourImageIcon);
            armourDisplayPanel.add(armourImageLabel);
        }
        for (int i = 0; i < 3; i++) {
            JLabel armourNameLabel = new JLabel();
            switch (i) {
                case 0:
                    armourNameLabel.setText("weak armour");
                    break;
                case 1:
                    armourNameLabel.setText("strong armour");
                    break;
                case 2:
                    armourNameLabel.setText("ultra armour");
                    break;
            }
            armourDisplayPanel.add(armourNameLabel);
        }
        armourPanel.add(armourDisplayPanel, BorderLayout.CENTER);
        // armour equip panel
        JPanel armourEquipPanel = new JPanel(new GridLayout(1, 3));
        for (int i = 0; i < 3; i++) {
            if (Server.getUserData().getArmorLevel() - 1 == i) { // armour is the strongest one available
            JLabel equippedArmourLabel = new JLabel("Equipped!", SwingConstants.CENTER);
            armourEquipPanel.add(equippedArmourLabel);
        }
        else {
            armourEquipPanel.add(new JLabel());
        }
        }
        armourPanel.add(armourEquipPanel, BorderLayout.SOUTH);
        panels[1] = armourPanel;

        JPanel abilityPanel = new JPanel(new BorderLayout());
        // ability price panel
        JPanel abilityPricePanel = new JPanel(new GridLayout(1, 6));
        abilityPricePanel.add(new JLabel());
        abilityPricePanel.add(new JLabel());
        JLabel abilityPriceLabel = new JLabel("$ 500", SwingConstants.CENTER);
        abilityPricePanel.add(abilityPriceLabel);
        JButton buyAbilityButton = new JButton("BUY");
        buyAbilityButton.addActionListener(e -> {
            if (ToolStore.buyAbility((byte) characterIndex)) {
                instance.updatePanels(characterIndex, 2);
            }
        });
        if (Server.getUserData().getCharacters()[characterIndex].isSpecialAbilityUnlocked()) {
            buyAbilityButton.setText("BOUGHT");
            buyAbilityButton.setEnabled(false);
        }
        abilityPricePanel.add(buyAbilityButton);
        abilityPricePanel.add(new JLabel());
        abilityPricePanel.add(new JLabel());
        abilityPanel.add(abilityPricePanel, BorderLayout.NORTH);
        // ability display panel
        JPanel abilityDisplayPanel = new JPanel(new GridLayout(2, 3));
        // ability image
        abilityDisplayPanel.add(new JLabel());
        ImageIcon abilityImageIcon = new ImageIcon(ShopFrame.class.getResource("/specialability.png"));
        Image scaledAbilityImage = abilityImageIcon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH); // Scales to 150 width, 100 height while maintaining aspect ratio
        ImageIcon scaledAbilityImageIcon = new ImageIcon(scaledAbilityImage); // Create a new ImageIcon from the scaled image
        JLabel abilityImageLabel = new JLabel(scaledAbilityImageIcon);
        abilityDisplayPanel.add(abilityImageLabel);
        abilityDisplayPanel.add(new JLabel("deals 50-60", SwingConstants.CENTER));
        abilityDisplayPanel.add(new JLabel());
        JLabel abilityNameLabel = new JLabel("death row", SwingConstants.CENTER);
        abilityDisplayPanel.add(abilityNameLabel);
        abilityDisplayPanel.add(new JLabel());
        abilityPanel.add(abilityDisplayPanel, BorderLayout.CENTER);
        // ability info panel
        JPanel abilityInfoPanel = new JPanel(new GridLayout(2, 3));
        abilityInfoPanel.add(new JLabel());
        abilityInfoPanel.add(new JLabel("refreshes per 5 minute in FPS matches", SwingConstants.CENTER));
        abilityInfoPanel.add(new JLabel());
        abilityInfoPanel.add(new JLabel());
        abilityInfoPanel.add(new JLabel("refreshes per 5 move in combat fights", SwingConstants.CENTER));
        abilityInfoPanel.add(new JLabel());
        abilityPanel.add(abilityInfoPanel, BorderLayout.SOUTH);
        panels[2] = abilityPanel;

        return panels;
    }

    protected static JPanel createCharacterDisplayPanel(int characterIndex) {
        Character character = Character.characters.get((byte) characterIndex);
        String characterName = character.name;
        String characterResourcePath = character.resourcePath;

        JPanel characterDisplayPanel = new JPanel(new BorderLayout());
        // level panel
        JPanel levelPanel = new JPanel(new GridLayout(2, 1));
        JLabel levelLabel = new JLabel("Level " + Server.getUserData().getLevel(), SwingConstants.RIGHT);
        JProgressBar xpBar = new JProgressBar(0, 100);
        xpBar.setValue(Server.getUserData().getXp());
        levelPanel.add(levelLabel);
        levelPanel.add(xpBar);
        characterDisplayPanel.add(levelPanel, BorderLayout.NORTH);

        // character panel
        JPanel characterPanel = new JPanel(new BorderLayout());
        // character image & name panel
        JPanel characterImagePanel = new JPanel(new BorderLayout());
        // character image
        System.out.println("Character Resource Path: " + characterResourcePath);
        ImageIcon imageIcon = new ImageIcon(ShopFrame.class.getResource(characterResourcePath + "skin.jpg"));
        Image scaledImage = imageIcon.getImage().getScaledInstance(200, 400, Image.SCALE_SMOOTH); // Scales to 150 width, 100 height while maintaining aspect ratio
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage); // Create a new ImageIcon from the scaled image
        JLabel imageLabel = new JLabel(scaledImageIcon);
        characterImagePanel.add(imageLabel, BorderLayout.CENTER);
        JLabel characterNameLabel = new JLabel(characterName, SwingConstants.CENTER);
        characterImagePanel.add(characterNameLabel, BorderLayout.SOUTH);
        characterPanel.add(characterImagePanel, BorderLayout.CENTER);
        // character progress panel
        JPanel characterProgressPanel = new JPanel(new BorderLayout());
        JProgressBar characterProgressBar = new JProgressBar(0, 100);
        characterProgressBar.setValue(Server.getUserData().getCharacters()[0].getProgress());
        characterProgressPanel.add(characterProgressBar, BorderLayout.CENTER);
        JLabel progressLabel = new JLabel(Server.getUserData().getCharacters()[0].getProgress() + "%", SwingConstants.CENTER);
        characterProgressPanel.add(progressLabel, BorderLayout.EAST);
        characterPanel.add(characterProgressPanel, BorderLayout.SOUTH);
        characterDisplayPanel.add(characterPanel, BorderLayout.CENTER);

        // character stats panel
        JPanel characterStatsPanel = new JPanel(new GridLayout(6, 1));
        JLabel attackLabel = new JLabel("Attack Power");
        JProgressBar attackBar = new JProgressBar(0, 4);
        attackBar.setValue(Server.getUserData().getCharacters()[characterIndex].getAbilityLevels()[0]);
        attackBar.setStringPainted(true);
        attackBar.setBackground(Color.RED);
        characterStatsPanel.add(attackLabel);
        characterStatsPanel.add(attackBar);

        JLabel defenceLabel = new JLabel("Defence Power");
        JProgressBar defenceBar = new JProgressBar(0, 4);
        defenceBar.setValue(Server.getUserData().getCharacters()[characterIndex].getAbilityLevels()[1]);
        defenceBar.setStringPainted(true);
        defenceBar.setBackground(Color.GREEN);
        characterStatsPanel.add(defenceLabel);
        characterStatsPanel.add(defenceBar);

        JLabel mobilityLabel = new JLabel("Mobility");
        JProgressBar mobilityBar = new JProgressBar(0, 4);
        mobilityBar.setValue(Server.getUserData().getCharacters()[characterIndex].getAbilityLevels()[2]);
        mobilityBar.setStringPainted(true);
        mobilityBar.setBackground(Color.BLUE);
        characterStatsPanel.add(mobilityLabel);
        characterStatsPanel.add(mobilityBar);

        characterDisplayPanel.add(characterStatsPanel, BorderLayout.SOUTH);
        return characterDisplayPanel;
    }

    public static ShopFrame getInstance() {
        if (instance == null) {
            instance = new ShopFrame();
        }
        return instance;
    }
}
