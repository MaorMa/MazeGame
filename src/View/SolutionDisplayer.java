package View;
import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Maor on 6/7/2018.
 */
public class SolutionDisplayer extends Canvas {
    Maze maze;
    private Solution solution;
    private double canvasHeight;
    private double canvasWidth;
    private double cellHeight;
    private double cellWidth;
    boolean animate;

    GraphicsContext gc;

    public void setCanvasHeight(int height){
        setHeight(height);
    }

    public void setCanvasWidth(int width){
        setWidth(width);
    }

    public void clearSolution(){
        this.maze = null;
        this.solution = null;
        animate = true;
    }

    public void setSolution(Solution solution,Maze maze) {
        this.maze = maze;
        this.solution = solution;
        redraw();
    }

    public void redrawWithoutAnimation(){
        animate = false;
        redraw();
    }

    public void redraw() {
        if (maze != null) {
            try {
                Image pathImage = new Image(new FileInputStream(ImageFileNamePath.get()));
                gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                setSizes();
                //Draw Solution
                ArrayList<AState> solutionPath = solution.getSolutionPath();
                for (int i = 0; i < solutionPath.size(); i++) {
                    int x = ((MazeState) solutionPath.get(i)).getPosition().getRowIndex();
                    int y = ((MazeState) solutionPath.get(i)).getPosition().getColumnIndex();
                    if(animate) {
                        Thread.sleep(10);
                    }
                    gc.drawImage(pathImage, y * cellWidth, x * cellHeight, cellWidth, cellHeight);
                }
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
        animate = true;
    }

    /**
     * Clear the Solution if a new maze is being generated
     */
    public void solutionClearRect(){
        if(gc!=null)
            gc.clearRect(0, 0, getWidth(), getHeight());
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
    private StringProperty ImageFileNamePath = new SimpleStringProperty();

    //Set and Get Path file
    public void setImageFileNamePath(String imageFileNamePath) {
        this.ImageFileNamePath.set(imageFileNamePath);
    }

    public String getImageFileNamePath() {
        return this.ImageFileNamePath.get();
    }
}