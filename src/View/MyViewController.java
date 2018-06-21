package View;

import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class MyViewController implements Observer, IView {
    @FXML
    BorderPane BasePane = new BorderPane();

    //combo box
    public javafx.scene.control.ComboBox charChoise;
    public javafx.scene.control.ComboBox wallChoise;
    //Buttons
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    //text fields
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    //Objects
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public SolutionDisplayer solutionDisplayer;
    public CharDisplayer charDisplayer;
    //Booleans
    static Boolean generate = false, solve = false, move = false, done=false, load=false;
    //position
    int characterPositionRow, characterPositionColumn;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    Media champ = new Media(new File("resources/champions.mp3").toURI().toString());
    MediaPlayer player2 = new MediaPlayer(champ);

    Media maze_runner = new Media(new File("resources/maze_runner.mp3").toURI().toString());
    MediaPlayer maze_runner_player = new MediaPlayer(maze_runner);

    boolean isPlaying = false;

    @Override
    public void update(Observable o, Object arg) {
        if(o==viewModel){
            if(generate || load) {//generate was pressed
                player2.stop();
                maze_runner_player.stop();
                maze_runner_player.play();
                solutionDisplayer.clearSolution();
                MazeDisplayer(viewModel.getMaze());//maze
                solutionDisplayer.solutionClearRect();//clear old solution
                characterPositionRow = viewModel.getCharacterPositionRow();
                characterPositionColumn = viewModel.getCharacterPositionColumn();
                CharacterDisplayer(characterPositionRow,characterPositionColumn,viewModel.getMaze());//char position
                btn_generateMaze.setDisable(false);
                btn_solveMaze.setDisable(false);
                generate = false;
                load = false;
            }
            else if(solve){//solve was pressed
                SolutionDisplayer(viewModel.getSolution(),viewModel.getMaze());
                btn_solveMaze.setDisable(false);
                btn_generateMaze.setDisable(false);
                solve = false;
            }
            else if(move){//movement was pressed
                characterPositionRow = viewModel.getCharacterPositionRow();
                characterPositionColumn = viewModel.getCharacterPositionColumn();
                CharacterDisplayer(characterPositionRow,characterPositionColumn,viewModel.getMaze());
                if(viewModel.getMaze().getGoalPosition().compareWithInt(characterPositionRow,characterPositionColumn)){
                    maze_runner_player.stop();
                    player2.stop();
                    player2.play();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Hello Thomas...");
                    alert.setHeaderText("You did it!");
                    alert.showAndWait();
                    done = true;
                }
                move = false;
            }
        }
    }

    public void setResizeEvent() {
        BasePane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                //System.out.println("Height: " + newSceneHeight.doubleValue());
                mazeDisplayer.redrawMaze();
                solutionDisplayer.redrawWithoutAnimation();
                charDisplayer.redrawCharacter();
            }
        });
        BasePane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                //System.out.println("Width: " +newSceneWidth.doubleValue());
                mazeDisplayer.redrawMaze();
                solutionDisplayer.redrawWithoutAnimation();
                charDisplayer.redrawCharacter();
            }
        });
    }

    /**
     * Treat key press
     * @param keyEvent - the key that is pressed
     */
    //options to do ----------------------------------------------------------------
    public void KeyPressed(KeyEvent keyEvent) {
        mazeDisplayer.requestFocus();
        if(!done) {
            move = true;
            viewModel.moveCharacter(keyEvent.getCode());
        }
        keyEvent.consume();
    }

    public void MazeGenerator() {
        generate = true;
        done = false;
        solutionDisplayer.clearSolution();
        int height,width;
        if(txtfld_rowsNum.getText().equals("") || Integer.parseInt(txtfld_rowsNum.getText()) < 3) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Minimum size alert!");
            alert.setHeaderText("Minimum row size is 3X3, generating maze with correct size...");
            alert.showAndWait();
            height = 3;//default maze height
        }
        else
            height = Integer.valueOf(txtfld_rowsNum.getText());//Get Maze height
        if(txtfld_columnsNum.getText().equals("") || Integer.parseInt(txtfld_columnsNum.getText()) < 3) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Minimum size alert!");
            alert.setHeaderText("Minimum column size is 3X3, generating maze with correct size...");
            alert.showAndWait();
            width = 3;//default maze width
        }
        else
            width = Integer.valueOf(txtfld_columnsNum.getText());//Get maze width
        btn_generateMaze.setDisable(true);//block "Generate" button
        btn_solveMaze.setDisable(true);//block "Solve" button
        viewModel.generateMaze(width, height);//send to viewModel
    }

    public void MazeSolver() {
        if(viewModel.getMaze()!=null) { //make sure the maze already exists
            solve = true;
            btn_generateMaze.setDisable(true);//block "Generate" button
            btn_solveMaze.setDisable(true);//block "Solve" button
            viewModel.solveMaze();//get Solution
        }
    }

    public void exit() {
        viewModel.exit();
    }


    public void load_Maze(){
        load = true;
        viewModel.load_Maze();
    }

    public void save_maze(){
        viewModel.save_Maze();
    }

    public void about() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("About.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 500, 350);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("About us...");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * Set new Character icon
     */
    public void setCharacter(){
        String newCharacter = (String)charChoise.getValue();
        charDisplayer.setImageFileNameCharacter("resources/"+ newCharacter+ ".png");
        if(viewModel.getMaze()!=null) //make sure the maze already exists
            CharacterDisplayer(characterPositionRow,characterPositionColumn,viewModel.getMaze());//char position
    }

    /**
     * Set new Wall icon
     */
    public void setWall(){
        String newWall = (String)wallChoise.getValue();
        mazeDisplayer.setImageFileNameWall("resources/"+ newWall+ ".png");
        if(viewModel.getMaze()!=null) //make sure the maze already exists
            MazeDisplayer(viewModel.getMaze());//redraw the maze
    }

    //------------------------------------------------------------------------------
    @Override
    public void MazeDisplayer(Maze maze) {
        mazeDisplayer.setMaze(maze);
    }

    private void SolutionDisplayer(Solution solution,Maze maze) {
        solutionDisplayer.setSolution(solution,maze);
    }

    private void CharacterDisplayer(int characterPositionRow, int characterPositionColumn,Maze maze) {
        charDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn, maze);
    }

    public void properties(ActionEvent actionEvent) {
        viewModel.properties();
    }

    public void help() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("Help.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 430, 510);
        } catch (IOException e) {
            //e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Game Instructions");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}