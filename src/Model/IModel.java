package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

/**
 * Created by Maor on 6/6/2018.
 */
public interface IModel {
    void generateMaze(int rows, int columns);
    Maze getMaze();
    void startServers();
    void stopServers();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    void solveMaze();
    void moveCharacter(KeyCode direction);
    Solution getSolution();
    void load_Maze();
    void save_Maze();
    void closeApp();
    void properties();
}