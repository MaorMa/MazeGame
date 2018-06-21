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
public class MazeDisplayer extends Canvas {
    private Maze maze;

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

    public StringProperty height = new SimpleStringProperty("0"); //For Binding
    public StringProperty length = new SimpleStringProperty("0"); //For Binding

    public void setMaze(Maze maze) {
        this.maze = maze;
        setSizes();
        redrawMaze();
    }

    public void redrawMaze() {
        if (maze != null) {
            setSizes();
            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image startImage = new Image(new FileInputStream(ImageFileNameStart.get()));
                Image finishImage = new Image(new FileInputStream(ImageFileNameFinish.get()));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.getMazeLength(); i++) {
                    for (int j = 0; j < maze.getMazeWidth(); j++) {
                        if (maze.getCellValue(i,j) == 1) {
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                        else if(maze.getStartPosition().compareWithInt(i,j)){
                            gc.drawImage(startImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                        else if(maze.getGoalPosition().compareWithInt(i,j)){
                            gc.drawImage(finishImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the sizes of the canvas Height and Width
     */
    private void setSizes(){
        this.canvasHeight = getHeight();
        this.canvasWidth = getWidth();
        this.cellWidth = canvasWidth / maze.getMazeWidth();
        this.cellHeight = canvasHeight / maze.getMazeLength();
        height.set(this.cellHeight + "");
        length.set(this.cellWidth + "");
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameStart = new SimpleStringProperty();
    private StringProperty ImageFileNameFinish = new SimpleStringProperty();

    //Set and Get Wall file
    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNameStart(String imageFileNameStart) {
        this.ImageFileNameStart.set(imageFileNameStart);
    }

    public void setImageFileNameFinish(String imageFileNameF) {
        this.ImageFileNameFinish.set(imageFileNameF);
    }

    public String getImageFileNameWall() {
        return this.ImageFileNameWall.get();
    }

    public String getImageFileNameStart() {
        return this.ImageFileNameStart.get();
    }

    public String getImageFileNameFinish() {
        return this.ImageFileNameFinish.get();
    }

    //endregion
}