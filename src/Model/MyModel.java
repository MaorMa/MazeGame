package Model;

import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.input.KeyCode;
import Server.Server;
import Server.Configurations;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import Client.*;
import javafx.stage.FileChooser;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Maor on 6/6/2018.
 */
public class MyModel extends Observable implements IModel {

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    private int characterPositionRow;
    private int characterPositionColumn;
    private Maze maze;
    private Solution solution;

    public MyModel() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        startServers();
    }

    public void startServers() {
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
    }

    public void stopServers() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
        threadPool.shutdown();
    }

    /**
     * Handle "Exit" or "X" button
     */
    public void closeApp() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to quit the game?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            this.stopServers();
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void generateMaze(int width, int height) {
        //Generate maze
        threadPool.execute(() -> {
            CommunicateWithServer_MazeGenerating(width, height);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers();
        });
    }

    /**
     *
     * @return - 2D array for maze for Display
     */
    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public void load_Maze() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load maze...:-)");
        File file = chooser.showOpenDialog(null);
        if(file != null) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                Maze maze = (Maze)objectInputStream.readObject();
                objectInputStream.close();
                this.maze = maze;
                characterPositionRow = maze.getStartPosition().getRowIndex();
                characterPositionColumn = maze.getStartPosition().getColumnIndex();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public void save_Maze() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save maze... :-)");
        File file = chooser.showSaveDialog(null);
        if(file != null) {
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
                objectOutputStream.writeObject(this.maze);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void properties() {
        List<String> choices = new ArrayList<>();
        choices.add("BestFS");
        choices.add("BFS");
        choices.add("DFS");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("BestFS", choices);
        dialog.setTitle("Properties Box");
        dialog.setHeaderText("Algorithm name");
        dialog.setContentText("Please Choose algorithm:");
        Optional<String> result = dialog.showAndWait();
        Configurations c = Configurations.getInstance();
        if (result.isPresent()){
            c.setProperties(result.get(),"3", "MyMazeGenerator");
        }
        setChanged();
        notifyObservers();
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    /**
     * Handle the numpad presses
     * @param movement - movement key
     */
    @Override
    public void moveCharacter(KeyCode movement) {
        boolean rightKey = false;//make sure only the numpad buttons are pressed
        switch (movement) {
            case NUMPAD8://up
                if(checkStep(characterPositionRow-1,characterPositionColumn)) {
                    characterPositionRow--;
                    rightKey = true;
                }
                break;
            case UP://up
                if(checkStep(characterPositionRow-1,characterPositionColumn)) {
                    characterPositionRow--;
                    rightKey = true;
                }
                break;
            case NUMPAD2://down
                if(checkStep(characterPositionRow+1,characterPositionColumn)) {
                    characterPositionRow++;
                    rightKey = true;
                }
                break;
            case DOWN://down
                if(checkStep(characterPositionRow+1,characterPositionColumn)) {
                    characterPositionRow++;
                    rightKey = true;
                }
                break;
            case NUMPAD6://right
                if(checkStep(characterPositionRow,characterPositionColumn+1)) {
                    characterPositionColumn++;
                    rightKey = true;
                }
                break;
            case RIGHT://right
                if(checkStep(characterPositionRow,characterPositionColumn+1)) {
                    characterPositionColumn++;
                    rightKey = true;
                }
                break;
            case NUMPAD4://left
                if(checkStep(characterPositionRow,characterPositionColumn-1)) {
                    characterPositionColumn--;
                    rightKey = true;
                }
                break;

            case LEFT://left
                if(checkStep(characterPositionRow,characterPositionColumn-1)) {
                    characterPositionColumn--;
                    rightKey = true;
                }
                break;
            case NUMPAD9://diagonal - up right
                if(checkStep(characterPositionRow-1,characterPositionColumn+1)) {
                    characterPositionRow--;
                    characterPositionColumn++;
                    rightKey = true;
                }
                break;
            case NUMPAD7://diagonal - up left
                if(checkStep(characterPositionRow-1,characterPositionColumn-1)) {
                    characterPositionRow--;
                    characterPositionColumn--;
                    rightKey = true;
                }
                break;
            case NUMPAD3://diagonal - down right
                if(checkStep(characterPositionRow+1,characterPositionColumn+1)) {
                    characterPositionRow++;
                    characterPositionColumn++;
                    rightKey = true;
                }
                break;
            case NUMPAD1://diagonal - down left
                if(checkStep(characterPositionRow+1,characterPositionColumn-1)) {
                    characterPositionRow++;
                    characterPositionColumn--;
                    rightKey = true;
                }
                break;
        }
        if(rightKey){
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Check if new position if valid
     * @param row - curr row
     * @param col - curr col
     * @return
     */
    private boolean checkStep(int row,int col) {
        if (row<this.maze.getMazeLength() && row>=0 &&
                col<this.maze.getMazeWidth() && col>=0 &&
                this.maze.getCellValue(row, col) == 0)
            return true;
        return false;
    }


    private void CommunicateWithServer_MazeGenerating(int width, int height) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[(width*height)+12]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        Maze maze = new Maze(decompressedMaze);
                        setMaze(maze);
                        characterPositionRow = maze.getStartPosition().getRowIndex();
                        characterPositionColumn = maze.getStartPosition().getColumnIndex();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();

                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        solution = mazeSolution;
                        setSolution(solution);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void setMaze(Maze maze){
        this.maze = maze;
    }

    private void setSolution(Solution solution){
        this.solution = solution;
    }

    /**
     *  Solve the maze
     */
    @Override
    public void solveMaze() {
        threadPool.execute(() -> {
            CommunicateWithServer_SolveSearchProblem();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers();
        });
    }

}