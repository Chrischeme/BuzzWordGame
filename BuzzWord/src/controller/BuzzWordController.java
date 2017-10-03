package controller;

import apptemplate.AppTemplate;
import buzzword.BuzzWord;
import com.sun.xml.internal.bind.v2.model.core.ID;
import data.GameData;
import gui.Workspace;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import propertymanager.PropertyManager;
import ui.AppGUI;
import ui.AppMessageDialogSingleton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static settings.AppPropertyType.*;

/**
 * @author Ritwik Banerjee
 */
public class BuzzWordController implements FileController {

    private AppTemplate appTemplate;
    private GameData    gamedata;
    private boolean     gameStart;

    public BuzzWordController (AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
        gamedata = new GameData(appTemplate);
        gameStart = false;
    }

    public boolean getGameStart() {
        return gameStart;
    }

    public GameData getGameData() {
        return gamedata;
    }
    public void setIDPW (String ID, String PW) {
        gamedata.setID(ID);
        gamedata.setPW(PW);
    }

    public void setID (String ID) {
        gamedata.setID(ID);
    }

    @Override
    public void handleNewRequest() {
        appTemplate.getDataComponent().reset();
        appTemplate.getWorkspaceComponent().reloadWorkspace();
        ensureActivatedWorkspace();

        Workspace gameWorkspace = (Workspace) appTemplate.getWorkspaceComponent();
    }

    private void ensureActivatedWorkspace() {
        appTemplate.getWorkspaceComponent().activateWorkspace(appTemplate.getGUI().getAppPane());
    }

    @Override
    public void handleSaveRequest() throws IOException {
        File selectedFile = new File("./saved/" + gamedata.getID() + ".json");
        if (!selectedFile.exists()) {
            saveWork(selectedFile);
        }
        else {
            Workspace ws = (Workspace) appTemplate.getWorkspaceComponent();
            ws.createOverLay();
        }
    }
    public void handleSaveRequest2() throws IOException {
        File selectedFile = new File("./saved/" + gamedata.getID() + ".json");
        saveWork2(selectedFile);
    }

    private void saveWork(File selectedFile) throws IOException {
        appTemplate.getFileComponent()
                .saveData(appTemplate.getDataComponent(), Paths.get(selectedFile.getAbsolutePath()));
    }
    private void saveWork2(File selectedFile) throws IOException {
        appTemplate.getFileComponent()
                .saveData1(appTemplate.getDataComponent(), Paths.get(selectedFile.getAbsolutePath()));
    }

    public boolean handleSaveRequest1() throws IOException {
        File selectedFile = new File("./saved/" + gamedata.getID() + ".json");
        if (selectedFile.exists()) {
            saveWork1(selectedFile);
            return true;
        }
        else {
            Workspace ws = (Workspace) appTemplate.getWorkspaceComponent();
            ws.createOverLay1();
            return false;
        }
    }

    private void saveWork1(File selectedFile) throws IOException {
        appTemplate.getFileComponent()
                .loadData(gamedata, Paths.get(selectedFile.getAbsolutePath()));

    }

    @Override
    public void handleLoadRequest() throws IOException {

    }

    @Override
    public void handleExitRequest() {

    }
}
