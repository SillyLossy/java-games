package org.sillylossy.games.common;

import org.sillylossy.games.common.game.Game;
import org.sillylossy.games.common.game.GameController;
import org.sillylossy.games.common.ui.GameInterface;
import org.sillylossy.games.common.util.FileSerializer;

import java.awt.*;

/**
 * Contains program entry point and some basic logic.
 */
public class Main {

    /**
     * Path to the file from where game data should be read / written to.
     */
    public static final String FILE_DATA = "data.bin";

    /**
     * Reference to a game controller assigned to an application.
     */
    private static GameController controller;

    /**
     * Reference to a GUI instance assigned to an application.
     */
    private static GameInterface ui;

    /**
     * Current game instance.
     */
    private static Game gameInstance;

    /**
     * Gets a reference to a game controller.
     */
    public static GameController getGameController() {
        return controller;
    }

    /**
     * Gets a reference to a game instance.
     */
    public static Game getGameInstance() {
        return gameInstance;
    }

    /**
     * Sets a game instance with value from param.
     */
    public static void setGameInstance(Game gameInstance) {
        Main.gameInstance = gameInstance;
    }

    /**
     * Gets a reference to a game user interface assigned to a application instance.
     *
     * @return a reference to a game user interface
     */
    public static GameInterface getUI() {
        return ui;
    }

    /**
     * A program's entry point. This method loads / creates a game controller and constructs a GUI.
     *
     * @param args command line args array
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    controller = loadData();
                    ui = new GameInterface();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Saves game data to a file. Runs in a separate thread.
     */
    public static void saveData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileSerializer.serialize(controller);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Loads game data from a file.
     */
    private static GameController loadData() {
        try {
            return FileSerializer.deserialize();
        } catch (Exception e) {
            return new GameController();
        }
    }
}
