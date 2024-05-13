package com.halenteck.commonUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.halenteck.CombatGame.Location;
import com.halenteck.server.Server;
import com.halenteck.server.UserCharacterData;
import com.halenteck.server.UserData;
import com.halenteck.CombatGame.Character;

public class CharacterCollection extends JFrame{

    private UserCharacterData[] characters = Server.getUserData().getCharacters();

    public CharacterCollection() {

        setTitle("Character Collection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // shortcut for returning to user card
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    new UserCard();
                    dispose();
                }
            }
        });

        // main panel
        JPanel collectionPanel = new JPanel(new GridLayout(3,2));

        for (int i = 0; i < 5; i++) {
            if (i < characters.length) {
                collectionPanel.add(createCharacterPanel(i));
            }
            else{
                ImageIcon blurryIcon = new ImageIcon(getClass().getResource("/characters/antidote/blurry.png"));

                new JLabel();
                JLabel blurryLabel;
                blurryIcon = switch (i) {
                    case 1 -> new ImageIcon(getClass().getResource("/characters/antidote/blurry.png"));
                    case 2 -> new ImageIcon(getClass().getResource("/characters/nazi/blurry.png"));
                    case 3 -> new ImageIcon(getClass().getResource("/characters/globalwarming/blurry.png"));
                    case 4 -> new ImageIcon(getClass().getResource("/characters/boss/blurry.png"));
                    default -> blurryIcon;
                };
                Image scaledBlurryImage = blurryIcon.getImage().getScaledInstance(700, 250, Image.SCALE_SMOOTH);
                ImageIcon scaledBlurryIcon = new ImageIcon(scaledBlurryImage);
                blurryLabel = new JLabel(scaledBlurryIcon);
                blurryLabel.setBorder(BorderFactory.createTitledBorder("Locked"));
                collectionPanel.add(blurryLabel);
            }
        }

        JButton returnButton = new JButton("Return to Stats Page");
        returnButton.setFont(new Font("Sans Serif", Font.BOLD, 20));
        returnButton.addActionListener(e -> {
            new UserCard();
            dispose();
        });

        collectionPanel.add(returnButton);

        add(collectionPanel, BorderLayout.CENTER); // game in the background to be added to other sections later

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    public JPanel createCharacterPanel(int characterID) {

        // character-based panel
        JPanel characterPanel = new JPanel(new GridLayout(1,3));
        Character character = Character.characters.get((byte) characterID);
        Location[] locations = character.maps;

        JPanel mainInfoPanel = new JPanel(new BorderLayout());
        // character image
        ImageIcon characterIcon = new ImageIcon(getClass().getResource(character.resourcePath + "skin.png"));
        Image scaledCharacter = characterIcon.getImage().getScaledInstance(70, 150, Image.SCALE_SMOOTH); // Scales to 150 width, 100 height while maintaining aspect ratio
        ImageIcon scaledCharacterIcon = new ImageIcon(scaledCharacter); // Create a new ImageIcon from the scaled image
        JLabel iconLabel = new JLabel(scaledCharacterIcon);
        mainInfoPanel.add(iconLabel, BorderLayout.CENTER);

        JPanel abilityPanel = new JPanel(new BorderLayout());
        ImageIcon abilityIcon = new ImageIcon(getClass().getResource(character.resourcePath + "specialability.png"));
        Image scaledAbility = abilityIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH); // Scales to 150 width, 100 height while maintaining aspect ratio
        ImageIcon scaledAbilityIcon = new ImageIcon(scaledAbility); // Create a new ImageIcon from the scaled image
        JLabel specialAbility = new JLabel(scaledAbilityIcon);
        JLabel abilityLabel = new JLabel("", SwingConstants.CENTER);
        specialAbility.setToolTipText(character.ability.name);
        if (characters[characterID].isSpecialAbilityUnlocked()) {
            abilityLabel.setText("Unlocked");
        }
        else {
            abilityLabel.setText("Locked");
        }
        abilityPanel.add(specialAbility, BorderLayout.CENTER);
        abilityPanel.add(abilityLabel, BorderLayout.SOUTH);
        mainInfoPanel.add(abilityPanel, BorderLayout.SOUTH);

//        // tool tip for character description
//        JTextArea characterInfo = new JTextArea(character.getDescription());
//        characterInfo.setLineWrap(true); // Enable line wrapping
//        characterInfo.setWrapStyleWord(true); // Wrap text at word boundaries (optional)
//        characterInfo.setEditable(false);
//        characterInfo.setFont(new Font("Sans Serif", Font.PLAIN, 15));
//        imageLabel.setToolTipText(character.getDescription()); // info when mouse is hovered on character image
        characterPanel.add(mainInfoPanel);

        // character name and items
        JPanel infoPanel = new JPanel(new GridLayout(2,1));
        JLabel characterLabel = new JLabel("Items", SwingConstants.CENTER);
        characterLabel.setFont(new Font("Sans Serif", Font.BOLD, 18));
        infoPanel.add(characterLabel);

        JPanel itemPanel = new JPanel(new GridLayout(2,3));
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        for (int i = 0; i < 5; i++) {

            if (i == 3) { // fourth item is centered, empty panel needed before it can be placed
                itemPanel.add(new JPanel()); // empty panel
            }

            else {
                JCheckBox itemBox = new JCheckBox();
                if (i <= 2) {
                    itemBox.setText(locations[i].getAward());
                }
                else {
                    itemBox.setText(locations[3].getAward());
                }
                if (Server.getUserData().getCharacters()[characterID].getProgress() > 60) {
                    itemBox.setSelected(true);
                } else if (Server.getUserData().getCharacters()[characterID].getProgress() > 35 && i < 3) {
                    itemBox.setSelected(true);
                } else if (Server.getUserData().getCharacters()[characterID].getProgress() > 15 && i < 2) {
                    itemBox.setSelected(true);
                } else if (Server.getUserData().getCharacters()[characterID].getProgress() > 0 && i < 1) {
                    itemBox.setSelected(true);
                }
                itemBox.setEnabled(false);
                itemPanel.add(itemBox);
            }
        }

        itemPanel.add(new JPanel()); // empty panel
        infoPanel.add(itemPanel);
        characterPanel.add(infoPanel);

        // weapon panel
        JPanel weaponPanel = new JPanel(new GridLayout(2,1));
        JLabel weaponLabel = new JLabel("Weapons", SwingConstants.CENTER);
        weaponLabel.setFont(new Font("Sans Serif", Font.BOLD, 18));
        weaponPanel.add(weaponLabel);
        JPanel weaponDisplayPanel = new JPanel(new GridLayout(1,2));

        for (int i = 0; i < 2; i++) {
            JPanel weaponInfoPanel = new JPanel(new GridLayout(2,1));
            // weapon image
            ImageIcon weaponIcon = new ImageIcon(getClass().getResource(character.resourcePath + "weapon" + (i + 1) + ".png"));
            Image scaled = weaponIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Scales to 150 width, 100 height while maintaining aspect ratio
            ImageIcon scaledWeaponIcon = new ImageIcon(scaled); // Create a new ImageIcon from the scaled image
            JLabel weaponIconLabel = new JLabel(scaledWeaponIcon);
            weaponInfoPanel.add(weaponIconLabel);
            if (characters[characterID].getUnlockedWeapons()[i]) {
                weaponInfoPanel.add(new JLabel("Unlocked", SwingConstants.CENTER));
            }
            else {
                weaponInfoPanel.add(new JLabel("Locked", SwingConstants.CENTER));
            }
            weaponInfoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            weaponDisplayPanel.add(weaponInfoPanel);
        }

        weaponPanel.add(weaponDisplayPanel);
        characterPanel.add(weaponPanel);
        characterPanel.setBorder(BorderFactory.createTitledBorder(character.name));

        return characterPanel;
    }

}
