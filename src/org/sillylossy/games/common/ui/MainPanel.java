package org.sillylossy.games.common.ui;

import org.sillylossy.games.blackjack.BlackjackGame;
import org.sillylossy.games.blackjack.BlackjackPanel;
import org.sillylossy.games.common.Main;
import org.sillylossy.games.common.game.Game;
import org.sillylossy.games.common.players.Player;
import org.sillylossy.games.videopoker.VideoPokerGame;
import org.sillylossy.games.videopoker.VideoPokerPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Main content panel of GUI.
 */
public class MainPanel extends JPanel {

    /**
     * String ID of game panel.
     */
    private static final String GAME_PANEL = "GamePanel";

    /**
     * String ID of player selection panel.
     */
    private static final String PLAYER_SELECTOR = "PlayerSelectionPanel";

    /**
     * String ID of statistics panel.
     */
    private static final String STAT_PANEL = "StatPanel";

    /**
     * String ID of game selection panel.
     */
    private static final String GAME_SELECTOR = "GameSelector";

    /**
     * Panel's layout manager. Used to switch sub-panels.
     */
    private final CardLayout gameLayout = new CardLayout();

    /**
     * A GUI-list of players.
     */
    private final JList<Player> playersList = new JList<>();

    /**
     * A GUI-table of statistics.
     */
    private final JTable statTable = new JTable();

    /**
     * Reference to game panel of selected game.
     */
    private GamePanel gamePanel;

    /**
     * Constructs main panel: sets layout, adds sub-panels.
     */
    MainPanel() {
        setLayout(gameLayout);
        add(createPlayerSelectionPanel(), PLAYER_SELECTOR);
        add(createStatPanel(), STAT_PANEL);
        add(createGameSelector(), GAME_SELECTOR);
    }

    /**
     * Creates statistics panel.
     */
    private JPanel createStatPanel() {
        JPanel statPanel = new JPanel();
        statPanel.setLayout(new BorderLayout());
        statTable.setEnabled(false);
        statPanel.add(new JScrollPane(statTable));
        JButton btnReturn = new JButton("Return");
        btnReturn.addActionListener(new ReturnButtonListener());
        statPanel.add(btnReturn, BorderLayout.SOUTH);
        return statPanel;
    }

    /**
     * Creates game selection panel.
     *
     * @return a reference to created JPanel
     */
    private JPanel createGameSelector() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JList<Game> gamesList = new JList<>();
        DefaultListModel<Game> listModel = new DefaultListModel<>();
        gamesList.setModel(listModel);
        listModel.addElement(new BlackjackGame());
        listModel.addElement(new VideoPokerGame());
        panel.add(new JScrollPane(gamesList), BorderLayout.CENTER);
        JButton btnAccept = new JButton("Accept");
        btnAccept.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game selected = gamesList.getSelectedValue();
                Main.setGameInstance(selected);
                setGame(selected);
                flipToPlayerSelection();
            }
        });
        panel.add(btnAccept, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Shows game selection panel.
     */
    public void flipToGameSelection() {
        Main.getUI().setTitle("Select a game");
        Main.getGameController().setInGame(false);
        gameLayout.show(this, GAME_SELECTOR);
    }

    /**
     * Shows statistics panel.
     */
    void flipToStatistics() {
        gameLayout.show(this, STAT_PANEL);
    }

    /**
     * Gets a selected player from JList.
     */
    private Player getSelectedPlayer() {
        return playersList.getSelectedValue();
    }

    /**
     * Sets statistics to JTable.
     *
     * @param defaultTableModel filled table data model
     */
    void setStats(DefaultTableModel defaultTableModel) {
        statTable.setModel(defaultTableModel);
    }

    /**
     * Creates player selection panel.
     */
    private JPanel createPlayerSelectionPanel() {
        JPanel playerSelectPanel = new JPanel();
        playerSelectPanel.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        playerSelectPanel.add(new JScrollPane(playersList), BorderLayout.CENTER);
        JButton btnNewPlayer = new JButton("New player");
        btnNewPlayer.addActionListener(new NewPlayerButtonListener());
        buttonPanel.add(btnNewPlayer);
        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.addActionListener(new SelectPlayerButtonListener());
        buttonPanel.add(btnConfirm);
        JButton btnDelete = new JButton("Delete player");
        btnDelete.addActionListener(new DeletePlayerButtonListener());
        buttonPanel.add(btnDelete);
        playerSelectPanel.add(buttonPanel, BorderLayout.SOUTH);
        return playerSelectPanel;
    }

    /**
     * Show game panel.
     */
    void flipToGame() {
        Main.getGameController().setInGame(true);
        Main.getUI().setTitle(Main.getGameInstance().getGameName());
        gameLayout.show(this, GAME_PANEL);
    }

    /**
     * Shows a player selection panel with list filled with registered players.
     */
    void flipToPlayerSelection() {
        Main.getGameController().setInGame(false);
        Main.getUI().setTitle("Select a player");
        DefaultListModel<Player> listModel = new DefaultListModel<>();
        for (Player player : Main.getGameController().getPlayers()) {
            listModel.addElement(player);
        }
        playersList.setModel(listModel);
        gameLayout.show(this, PLAYER_SELECTOR);
    }

    /**
     * Gets a game panel.
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Sets an active game.
     *
     * @param selected a reference to "Game object"
     */
    private void setGame(Game selected) {
        if (selected.getGameName().equals(BlackjackGame.GAME_NAME)) {
            gamePanel = new BlackjackPanel();
        } else if (selected.getGameName().equals(VideoPokerGame.GAME_NAME)){
            gamePanel = new VideoPokerPanel();
        } else {
            throw new UnsupportedOperationException();
        }
        add(gamePanel, GAME_PANEL);
    }

    /**
     * "Delete player" button action listener.
     */
    private final class DeletePlayerButtonListener extends GameListener {
        /**
         * Asks for a confirmation. If an answer is "Yes" then player is deleted.
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            if (getUI().confirm("Are you sure?")) {
                if (!getGameController().deletePlayer(getMainPanel().getSelectedPlayer())) {
                    getUI().alert(getGameController().getLastError());
                }
                getUI().getMainPanel().flipToPlayerSelection();
            }
        }
    }

    /**
     * "Select player" button event listener.
     */
    private final class SelectPlayerButtonListener extends GameListener {
        /**
         * If a player is selected, sets it active. Else shows an error message.
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            Player selected = getSelectedPlayer();
            if (getGameController().setActivePlayer(selected)) {
                flipToGame();
                gamePanel.start();
            } else {
                String error = getGameController().getLastError();
                getUI().alert(error);
            }
        }
    }

    /**
     * "Return" button event listener.
     */
    private final class ReturnButtonListener extends GameListener {
        /**
         * Show game panel, player selection or game selection.
         * Depends on selection states.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (getGameController().isInGame()) {
                flipToGame();
            } else if (getGamePanel() == null) {
                flipToGameSelection();
            } else {
                flipToPlayerSelection();
            }
        }
    }

    /**
     * "New player" button action listener.
     */
    private final class NewPlayerButtonListener extends GameListener {
        /**
         * Registers a player if the player's name matches the necessary conditions.
         * Sets player active. If an error occurs, shows a message.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = JOptionPane.showInputDialog("Enter your name");
            if (name == null || name.isEmpty()) {
                return;
            }
            name = name.trim();
            if (getGameController().register(name)) {
                getMainPanel().flipToGame();
                getGamePanel().start();
            } else {
                String error = getGameController().getLastError();
                getUI().alert(error);
            }
        }
    }
}
