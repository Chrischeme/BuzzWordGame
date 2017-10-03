package data;

import apptemplate.AppTemplate;
import components.AppDataComponent;
import controller.GameError;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Ritwik Banerjee
 */
public class GameData implements AppDataComponent {

    public  AppTemplate    appTemplate;
    private String         ID;
    private String         PW;
    private int[]          levels;

    public GameData(AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
        this.appTemplate.setGameData(this);
        this.ID = "";
        this.PW = "";
        this.levels = new int[]{1, 1, 1, 1};
    }

    @Override
    public void reset() {
        this.appTemplate.setGameData(this);
        this.ID = "";
        this.PW = "";
        this.levels = new int[]{1, 1, 1, 1};
        appTemplate.getWorkspaceComponent().reloadWorkspace();
    }
    public void setLevels(int i, int l) {
        levels[i] = l;
    }

    public int[] getLevels () {
        return levels;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPW() {
        return PW;
    }

    public void setPW(String PW) {
        this.PW = PW;
    }
}
