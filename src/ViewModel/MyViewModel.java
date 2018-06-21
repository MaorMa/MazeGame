package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Maor on 6/6/2018.
 */
public class MyViewModel extends Observable implements Observer {
    private IModel model;

    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;

    public StringProperty characterPositionRow = new SimpleStringProperty("1"); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty("1"); //For Binding

    public MyViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(characterPositionRowIndex + "");
            characterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(characterPositionColumnIndex + "");
            setChanged();
            notifyObservers();
        }
    }

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public void solveMaze() {
        model.solveMaze();
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public void exit(){
        model.closeApp();
    }

    public void load_Maze() {
        model.load_Maze();
    }

    public void save_Maze() {
        model.save_Maze();
    }

    public void properties() {
        model.properties();
    }
}