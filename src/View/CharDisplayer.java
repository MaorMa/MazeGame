package View;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Maor on 6/7/2018.
 */
public class CharDisplayer extends Canvas {
    private Maze maze;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;

    private double canvasHeight;
    private double canvasWidth;
    private double cellHeight;
    private double cellWidth;

    public void setCanvasHeight(int height){
        setHeight(height);
    }

    public void setCanvasWidth(int width){
        setWidth(width);
    }

    public void setCharacterPosition(int row, int column, Maze maze) {
        this.maze = maze;
        characterPositionRow = row;
        characterPositionColumn = column;
        redrawCharacter();
    }

    public void redrawCharacter() {
        if (maze != null) {
            try {
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                setSizes();
                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    /**
     * Set the sizes of the canvas Height and Width
     */
    private void setSizes(){
        this.canvasHeight = getHeight();
        this.canvasWidth = getWidth();
        this.cellWidth = canvasWidth / maze.getMazeWidth();
        this.cellHeight = canvasHeight / maze.getMazeLength();
    }

    //region Properties
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    //Set and Get Wall file
    public void setImageFileNameCharacter(String imageFileNamechar) {
        this.ImageFileNameCharacter.set(imageFileNamechar);
    }

    public String getImageFileNameCharacter() {
        return this.ImageFileNameCharacter.get();
    }
    //endregion
}