package gui;

import apptemplate.AppTemplate;
import buzzword.BuzzWord;
import com.sun.org.apache.xpath.internal.operations.Bool;
import components.AppWorkspaceComponent;
import controller.BuzzWordController;
import controller.GameError;
import data.GameData;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.util.Callback;
import propertymanager.PropertyManager;
import ui.AppGUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import static buzzword.BuzzWordProperties.*;
import static settings.AppPropertyType.SAVE_WORK_TITLE;
import static settings.AppPropertyType.WORK_FILE_EXT_DESC;
import static settings.InitializationParameters.APP_IMAGEDIR_PATH;

/**
 * This class serves as the GUI component for the Hangman game.
 *
 * @author Ritwik Banerjee
 */
public class Workspace extends AppWorkspaceComponent {

    AppTemplate app; // the actual application
    AppGUI      gui; // the GUI inside which the application sits
    BuzzWordController controller;
    Label       guiHeadingLabel;
    BorderPane  wS;
    VBox        left;
    StackPane   realMid;
    VBox        middle;
    VBox        right;
    VBox        loginScreen;
    VBox        tvtp;
    HBox        name;
    HBox        password;
    Label       profileName;
    Label       profilePassword;
    Label       modeLabel;
    Label       level;
    Label       timeRem;
    TextField   current;
    Label       totalPoints;
    Label       target;
    TextField   profName;
    PasswordField   profPass;
    Button      x;
    Button      newProfile;
    Button      login;
    Button      mode;
    Button      home;
    Button      help;
    Button      dictionary;
    Button      countries;
    Button      science;
    Button      people;
    Button      play;
    Button      restartGame;
    HashSet<String>     dictionaryH;
    HashSet<String>     countriesH;
    HashSet<String>     scienceH;
    HashSet<String>     peopleH;
    HashSet<String>     currentHS;
    Canvas      c;
    Canvas      c1;
    Canvas      c2;
    TableView   tV;
    Boolean     paused = true;
    int         sec = 60;
    Timer       timer;
    final int   NUMBER_OF_COUNTRIES = 165;
    String      word;
    String      word1;
    String      alph = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String      currentLetters;
    char[][]    gameGrid;
    int[][]     visited;
    ObservableList<WordPoints> wordP;
    char[] cubes;
    boolean found;
    HashSet<String> wordsInside;
    int[] path;
    int targetScore;
    boolean gameEnd;


    public Workspace (AppTemplate initApp) throws IOException {
        app = initApp;
        gui = app.getGUI();
        layoutGUI();
        setupHandlers();
    }

    private void setupHandlers() {

    }

    public void start () {
        controller = new BuzzWordController(app);
        controller.handleNewRequest();
    }

    private void layoutGUI() {
        gameEnd = false;

        dictionaryH = new HashSet<String>();
        countriesH = new HashSet<String>();
        peopleH = new HashSet<String>();
        scienceH = new HashSet<String>();
        URL wordsResource = getClass().getClassLoader().
                getResource("words/names.txt");
        assert wordsResource != null;
        for (int i = 0; i < 5163; i++) {
            try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
                String s = lines.skip(i).findFirst().get();
                if (s.length() > 3) {
                    peopleH.add(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        wordsResource = getClass().getClassLoader().
                getResource("words/" + "countries" + ".txt");
        assert wordsResource != null;
        for (int i = 0; i < NUMBER_OF_COUNTRIES; i++) {
            try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
                countriesH.add(lines.skip(i).findFirst().get());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        wordsResource = getClass().getClassLoader().
                getResource("words/" + "science" + ".txt");
        assert wordsResource != null;
        for (int i = 0; i < 146; i++) {
            try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
                scienceH.add(lines.skip(i).findFirst().get());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }


        wordsResource = getClass().getClassLoader().
                getResource("words/dictionary.txt");
        assert wordsResource != null;
        for (int i = 0; i < 61336; i++) {
            try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
                String s = lines.skip(i).findFirst().get();
                if (s.length() >= 3) {
                    dictionaryH.add(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        PropertyManager propertyManager = PropertyManager.getManager();
        guiHeadingLabel = new Label(propertyManager.getPropertyValue(WORKSPACE_HEADING_LABEL));
        guiHeadingLabel.setFont(Font.font("Monospaced", 40));
        guiHeadingLabel.setTextFill(Color.WHITE);
        wS = new BorderPane();
        realMid = new StackPane();
        level = new Label();

        left = new VBox();
        left.setMinHeight(primaryScreenBounds.getHeight());
        left.setMinWidth(200);
        left.setStyle("-fx-background-color: #cccccc;");

        Label space = new Label(" ");
        left.setSpacing(50);
        newProfile = new Button("Create New Profile");
        newProfile.setTextFill(Color.WHITE);
        newProfile.setStyle("-fx-background-color: #b2b2b2;");
        newProfile.setPrefSize(200, 30);
        newProfile.setFont(Font.font("Monospaced", 15));
        newProfile.setOnMouseEntered(e -> {
            newProfile.setCursor(Cursor.HAND);
        });
        newProfile.setOnMouseClicked(e -> {
            if (realMid.getChildren().contains(loginScreen)) {
                realMid.getChildren().remove(loginScreen);
            }
            else {
                loginScreen = new VBox();
                loginScreen.setMaxSize(400, 300);
                loginScreen.setSpacing(30);
                name = new HBox();
                password = new HBox();
                loginScreen.setAlignment(Pos.CENTER);
                loginScreen.setOpacity(.75);
                profileName = new Label("Profile Name       ");
                profileName.setFont(Font.font("Monospaced", 15));
                profileName.setTextFill(Color.WHITE);
                profilePassword = new Label("Profile Password   ");
                profilePassword.setFont(Font.font("Monospaced", 15));
                profilePassword.setTextFill(Color.WHITE);
                profName = new TextField();
                profName.setStyle("-fx-font-weight: bold");
                profName.setStyle("-fx-background-color: #cccccc");
                profPass = new PasswordField();
                profPass.setStyle("-fx-background-color: #cccccc");
                name.getChildren().addAll(profileName, profName);
                password.getChildren().addAll(profilePassword, profPass);
                Button lgin = new Button("Create");
                Button cancel = new Button("Cancel");
                lgin.setStyle("-fx-background-color: #cccccc;");
                lgin.setTextFill(Color.BLACK);
                cancel.setStyle("-fx-background-color: #cccccc;");
                cancel.setTextFill(Color.BLACK);
                lgin.setOnMouseEntered(ev -> {
                    lgin.setCursor(Cursor.HAND);
                });
                lgin.setOnMouseClicked(ev -> {
                    try {
                        controller.setIDPW(profName.getText(), profPass.getText());
                        controller.handleSaveRequest();
                    } catch (IOException e1) {
                        System.out.println("Error in creating");
                    }
                    realMid.getChildren().remove(loginScreen);
                });
                cancel.setOnMouseEntered(ev -> {
                    cancel.setCursor(Cursor.HAND);
                });
                cancel.setOnMouseClicked(ev -> {
                    realMid.getChildren().remove(loginScreen);
                });

                HBox buttons = new HBox();
                buttons.setAlignment(Pos.CENTER);
                buttons.setSpacing(50);
                buttons.getChildren().addAll(lgin, cancel);
                loginScreen.getChildren().addAll(name, password, buttons);
                loginScreen.setStyle("-fx-background-color: #000000;");
                realMid.setAlignment(Pos.CENTER);
                realMid.getChildren().add(loginScreen);

                /*
                loginScreen.setOnKeyPressed(ev -> {
                    System.out.println("s");
                    if (ev.getCode() == KeyCode.A) {
                        System.out.println("s");
                        realMid.getChildren().remove(loginScreen);
                    }
                    else {

                    }
                });
                */
            }
        });
        login = new Button("Login");
        login.setTextFill(Color.WHITE);
        login.setStyle("-fx-background-color: #b2b2b2;");
        login.setPrefSize(200, 30);
        login.setFont(Font.font("Monospaced", 15));
        login.setOnMouseEntered(e -> {
            login.setCursor(Cursor.HAND);
        });
        login.setOnMouseClicked(e -> {
            if (login.getText().equals("Login")) {
                if (realMid.getChildren().contains(loginScreen)) {
                    realMid.getChildren().remove(loginScreen);
                } else {
                    loginScreen = new VBox();
                    loginScreen.setMaxSize(400, 300);
                    loginScreen.setSpacing(30);
                    name = new HBox();
                    password = new HBox();
                    loginScreen.setAlignment(Pos.CENTER);
                    loginScreen.setOpacity(.75);
                    profileName = new Label("Profile Name       ");
                    profileName.setFont(Font.font("Monospaced", 15));
                    profileName.setTextFill(Color.WHITE);
                    profilePassword = new Label("Profile Password   ");
                    profilePassword.setFont(Font.font("Monospaced", 15));
                    profilePassword.setTextFill(Color.WHITE);
                    profName = new TextField();
                    profName.setStyle("-fx-font-weight: bold");
                    profName.setStyle("-fx-background-color: #cccccc");
                    profPass = new PasswordField();
                    profPass.setStyle("-fx-background-color: #cccccc");
                    name.getChildren().addAll(profileName, profName);
                    password.getChildren().addAll(profilePassword, profPass);
                    Button lgin = new Button("Login");
                    Button cancel = new Button("Cancel");
                    lgin.setStyle("-fx-background-color: #cccccc;");
                    lgin.setTextFill(Color.BLACK);
                    cancel.setStyle("-fx-background-color: #cccccc;");
                    cancel.setTextFill(Color.BLACK);
                    lgin.setOnMouseEntered(ev -> {
                        lgin.setCursor(Cursor.HAND);
                    });
                    lgin.setOnMouseClicked(ev -> {
                            try {
                                controller.setID(profName.getText());
                                if (controller.handleSaveRequest1()) {
                                    MessageDigest mD = MessageDigest.getInstance("MD5");
                                    mD.update(profPass.getText().getBytes(), 0, profPass.getText().length());
                                    String hashedPass = new BigInteger(1, mD.digest()).toString(16);
                                    if (hashedPass.equals(controller.getGameData().getPW())) {
                                        realMid.getChildren().remove(loginScreen);
                                        login.setText(profName.getText());
                                        mode.setVisible(true);
                                        dictionary.setVisible(true);
                                        countries.setVisible(true);
                                        science.setVisible(true);
                                        people.setVisible(true);
                                        newProfile.setVisible(false);
                                    } else {
                                        createOverLay1();
                                    }
                                }
                            } catch (IOException e1) {
                                System.out.println("Error in loading profile");
                            } catch (NoSuchAlgorithmException e1) {
                                e1.printStackTrace();
                            }
                            realMid.getChildren().remove(loginScreen);
                    });
                    cancel.setOnMouseEntered(ev -> {
                        cancel.setCursor(Cursor.HAND);
                    });
                    cancel.setOnMouseClicked(ev -> {
                        realMid.getChildren().remove(loginScreen);
                    });

                    HBox buttons = new HBox();
                    buttons.setAlignment(Pos.CENTER);
                    buttons.setSpacing(50);
                    buttons.getChildren().addAll(lgin, cancel);
                    loginScreen.getChildren().addAll(name, password, buttons);
                    loginScreen.setStyle("-fx-background-color: #000000;");
                    realMid.setAlignment(Pos.CENTER);
                    realMid.getChildren().add(loginScreen);
                }
            }
            else {
                createOverLay7();
            }
        });
        mode = new Button("Select Mode");
        dictionary = new Button("English Dictionary");
        countries = new Button("Countries");
        science = new Button("Science");
        people = new Button("Names");
        mode.setStyle("-fx-background-color: #b2b2b2;");
        dictionary.setStyle("-fx-background-color: #b2b2b2;");
        countries.setStyle("-fx-background-color: #b2b2b2;");
        science.setStyle("-fx-background-color: #b2b2b2;");
        people.setStyle("-fx-background-color: #b2b2b2;");
        mode.setTextFill(Color.WHITE);
        dictionary.setTextFill(Color.RED);
        countries.setTextFill(Color.RED);
        science.setTextFill(Color.RED);
        people.setTextFill(Color.RED);
        mode.setPrefSize(200, 30);
        dictionary.setPrefSize(200, 30);
        countries.setPrefSize(200, 30);
        science.setPrefSize(200, 30);
        people.setPrefSize(200, 30);
        mode.setVisible(false);
        dictionary.setVisible(false);
        countries.setVisible(false);
        science.setVisible(false);
        people.setVisible(false);
        dictionary.setOnMouseEntered(ev -> {
            dictionary.setCursor(Cursor.HAND);
        });
        countries.setOnMouseEntered(ev -> {
            countries.setCursor(Cursor.HAND);
        });
        science.setOnMouseEntered(ev -> {
            science.setCursor(Cursor.HAND);
        });
        people.setOnMouseEntered(ev -> {
            people.setCursor(Cursor.HAND);
        });
        modeLabel = new Label();
        dictionary.setOnMouseClicked(e -> {
            home.setVisible(true);
            modeLabel.setText("English Dictionary");
            modeLabel.setFont(Font.font("Monospaced", 40));
            modeLabel.setTextFill(Color.WHITE);
            word = "dictionary";
            currentHS = dictionaryH;
            initLevelSelection(0);
        });
        countries.setOnMouseClicked(e -> {
            home.setVisible(true);
            modeLabel.setText("Countries");
            modeLabel.setFont(Font.font("Monospaced", 40));
            modeLabel.setTextFill(Color.WHITE);
            word = "countries";
            currentHS = countriesH;
            initLevelSelection(1);
        });
        science.setOnMouseClicked(e -> {
            home.setVisible(true);
            modeLabel.setText("Science");
            modeLabel.setFont(Font.font("Monospaced", 40));
            modeLabel.setTextFill(Color.WHITE);
            word = "science";
            currentHS = scienceH;
            initLevelSelection(2);
        });
        people.setOnMouseClicked(e -> {
            home.setVisible(true);
            modeLabel.setText("Names");
            modeLabel.setFont(Font.font("Monospaced", 40));
            modeLabel.setTextFill(Color.WHITE);
            word = "names";
            currentHS = peopleH;
            initLevelSelection(3);
        });

        VBox modeBox = new VBox();
        modeBox.getChildren().addAll(mode, dictionary, countries, science, people);

        home = new Button("Home");
        home.setStyle("-fx-background-color: #b2b2b2;");
        home.setTextFill(Color.WHITE);
        home.setPrefSize(200, 30);
        home.setVisible(false);
        home.setFont(Font.font("Monospaced", 15));
        home.setOnMouseEntered(e -> {
            home.setCursor(Cursor.HAND);
        });
        home.setOnMouseClicked(e -> {
            if (middle.getChildren().contains(c1)) {
                middle.getChildren().removeAll(modeLabel, c1);
                middle.getChildren().add(c);
            }
            else if (paused) {
                middle.getChildren().removeAll(modeLabel, level, play);
                right.getChildren().removeAll(timeRem, current, tvtp, target, restartGame);
            }
        });

        help = new Button("Help");
        help.setStyle("-fx-background-color: #b2b2b2;");
        help.setTextFill(Color.WHITE);
        help.setPrefSize(200, 30);
        help.setFont(Font.font("Monospaced", 15));
        help.setOnMouseEntered(e -> {
            help.setCursor(Cursor.HAND);
        });
        help.setOnMouseClicked(e -> {
            createOverLay6();
        });

        left.getChildren().addAll(space, newProfile, login, modeBox, home, help);

        middle = new VBox();
        middle.setAlignment(Pos.TOP_CENTER);
        c = new Canvas(550, 550);
        GraphicsContext gc = c.getGraphicsContext2D();
        initCanvasWords(gc);

        middle.setSpacing(50);
        middle.getChildren().addAll(guiHeadingLabel, c);

        x = new Button("X");
        x.setFont(Font.font("Monospaced", 20));
        x.setOnMouseEntered(e -> {
            x.setCursor(Cursor.HAND);
        });
        app.getGUI().getPrimaryScene().setOnKeyPressed(e -> {
            final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
            final KeyCombination keyComb2 = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
            if (e.getCode() == KeyCode.ESCAPE) {
                if (!paused) {
                    middle.getChildren().removeAll(c2, level, play);
                    middle.getChildren().addAll(c, level, play);
                    Image tempImage = new Image("Arrow.png");
                    play.setGraphic(new ImageView(tempImage));
                    paused = true;
                }
                createOverLay2();
            }
            else if (keyComb1.match(e)) {
                if (realMid.getChildren().contains(loginScreen)) {
                    realMid.getChildren().remove(loginScreen);
                }
                else {
                    loginScreen = new VBox();
                    loginScreen.setMaxSize(400, 300);
                    loginScreen.setSpacing(30);
                    name = new HBox();
                    password = new HBox();
                    loginScreen.setAlignment(Pos.CENTER);
                    loginScreen.setOpacity(.75);
                    profileName = new Label("Profile Name       ");
                    profileName.setFont(Font.font("Monospaced", 15));
                    profileName.setTextFill(Color.WHITE);
                    profilePassword = new Label("Profile Password   ");
                    profilePassword.setFont(Font.font("Monospaced", 15));
                    profilePassword.setTextFill(Color.WHITE);
                    profName = new TextField();
                    profName.setStyle("-fx-font-weight: bold");
                    profName.setStyle("-fx-background-color: #cccccc");
                    profPass = new PasswordField();
                    profPass.setStyle("-fx-background-color: #cccccc");
                    name.getChildren().addAll(profileName, profName);
                    password.getChildren().addAll(profilePassword, profPass);
                    Button lgin = new Button("Login");
                    Button cancel = new Button("Cancel");
                    lgin.setStyle("-fx-background-color: #cccccc;");
                    lgin.setTextFill(Color.BLACK);
                    cancel.setStyle("-fx-background-color: #cccccc;");
                    cancel.setTextFill(Color.BLACK);
                    lgin.setOnMouseEntered(ev -> {
                        lgin.setCursor(Cursor.HAND);
                    });
                    lgin.setOnMouseClicked(ev -> {
                        try {
                            controller.setID(profName.getText());
                            if (controller.handleSaveRequest1()) {
                                MessageDigest mD = MessageDigest.getInstance("MD5");
                                mD.update(profPass.getText().getBytes(), 0, profPass.getText().length());
                                String hashedPass = new BigInteger(1, mD.digest()).toString(16);
                                if (hashedPass.equals(controller.getGameData().getPW())) {
                                    realMid.getChildren().remove(loginScreen);
                                    login.setText(profName.getText());
                                    mode.setVisible(true);
                                    dictionary.setVisible(true);
                                    countries.setVisible(true);
                                    science.setVisible(true);
                                    people.setVisible(true);
                                    newProfile.setVisible(false);
                                } else {
                                    createOverLay1();
                                }
                            }
                        } catch (IOException e1) {
                            System.out.println("Error in loading profile");
                        } catch (NoSuchAlgorithmException e1) {
                            e1.printStackTrace();
                        }
                        realMid.getChildren().remove(loginScreen);
                    });
                    cancel.setOnMouseEntered(ev -> {
                        cancel.setCursor(Cursor.HAND);
                    });
                    cancel.setOnMouseClicked(ev -> {
                        realMid.getChildren().remove(loginScreen);
                    });

                    HBox buttons = new HBox();
                    buttons.setAlignment(Pos.CENTER);
                    buttons.setSpacing(50);
                    buttons.getChildren().addAll(lgin, cancel);
                    loginScreen.getChildren().addAll(name, password, buttons);
                    loginScreen.setStyle("-fx-background-color: #000000;");
                    realMid.setAlignment(Pos.CENTER);
                    realMid.getChildren().add(loginScreen);
                }
            }
            else if (keyComb2.match(e)) {
                if (realMid.getChildren().contains(loginScreen)) {
                    realMid.getChildren().remove(loginScreen);
                }
                else {
                    loginScreen = new VBox();
                    loginScreen.setMaxSize(400, 300);
                    loginScreen.setSpacing(30);
                    name = new HBox();
                    password = new HBox();
                    loginScreen.setAlignment(Pos.CENTER);
                    loginScreen.setOpacity(.75);
                    profileName = new Label("Profile Name       ");
                    profileName.setFont(Font.font("Monospaced", 15));
                    profileName.setTextFill(Color.WHITE);
                    profilePassword = new Label("Profile Password   ");
                    profilePassword.setFont(Font.font("Monospaced", 15));
                    profilePassword.setTextFill(Color.WHITE);
                    profName = new TextField();
                    profName.setStyle("-fx-font-weight: bold");
                    profName.setStyle("-fx-background-color: #cccccc");
                    profPass = new PasswordField();
                    profPass.setStyle("-fx-background-color: #cccccc");
                    name.getChildren().addAll(profileName, profName);
                    password.getChildren().addAll(profilePassword, profPass);
                    Button lgin = new Button("Create");
                    Button cancel = new Button("Cancel");
                    lgin.setStyle("-fx-background-color: #cccccc;");
                    lgin.setTextFill(Color.BLACK);
                    cancel.setStyle("-fx-background-color: #cccccc;");
                    cancel.setTextFill(Color.BLACK);
                    lgin.setOnMouseEntered(ev -> {
                        lgin.setCursor(Cursor.HAND);
                    });
                    lgin.setOnMouseClicked(ev -> {
                        try {
                            controller.setIDPW(profName.getText(), profPass.getText());
                            controller.handleSaveRequest();
                        } catch (IOException e1) {
                            System.out.println("Error in creating");
                        }
                        realMid.getChildren().remove(loginScreen);
                    });
                    cancel.setOnMouseEntered(ev -> {
                        cancel.setCursor(Cursor.HAND);
                    });
                    cancel.setOnMouseClicked(ev -> {
                        realMid.getChildren().remove(loginScreen);
                    });

                    HBox buttons = new HBox();
                    buttons.setAlignment(Pos.CENTER);
                    buttons.setSpacing(50);
                    buttons.getChildren().addAll(lgin, cancel);
                    loginScreen.getChildren().addAll(name, password, buttons);
                    loginScreen.setStyle("-fx-background-color: #000000;");
                    realMid.setAlignment(Pos.CENTER);
                    realMid.getChildren().add(loginScreen);
                }
            }
        });
        x.setOnMouseClicked(e -> {
            if (!paused) {
                middle.getChildren().removeAll(c2, level, play);
                middle.getChildren().addAll(c, level, play);
                Image tempImage = new Image("Arrow.png");
                play.setGraphic(new ImageView(tempImage));
                paused = true;
            }
            createOverLay2();
        });
        x.setStyle("-fx-background-color: transparent;");
        x.setTextFill(Color.WHITE);

        right = new VBox();
        right.setAlignment(Pos.TOP_RIGHT);
        x.setAlignment(Pos.TOP_RIGHT);
        right.getChildren().addAll(x);

        realMid.getChildren().add(middle);
        wS.setRight(right);
        wS.setCenter(realMid);
        wS.setLeft(left);
        workspace = new VBox();
        workspace.getChildren().add(wS);
    }

    private void initCanvasWords (GraphicsContext gc) {
        gc.setFill(Paint.valueOf("#444444"));
        gc.fillOval(0, 0, 100, 100);
        gc.fillOval(150, 0, 100, 100);
        gc.fillOval(300, 0, 100, 100);
        gc.fillOval(450, 0, 100, 100);
        gc.fillOval(0, 150, 100, 100);
        gc.fillOval(150, 150, 100, 100);
        gc.fillOval(300, 150, 100, 100);
        gc.fillOval(450, 150, 100, 100);
        gc.fillOval(0, 300, 100, 100);
        gc.fillOval(150, 300, 100, 100);
        gc.fillOval(300, 300, 100, 100);
        gc.fillOval(450, 300, 100, 100);
        gc.fillOval(0, 450, 100, 100);
        gc.fillOval(150, 450, 100, 100);
        gc.fillOval(300, 450, 100, 100);
        gc.fillOval(450, 450, 100, 100);
        gc.setFill(Color.WHITE);
        gc.fillText("B", 50, 50);
        gc.fillText("U", 200, 50);
        gc.fillText("Z", 50, 200);
        gc.fillText("Z", 200, 200);
        gc.fillText("W", 350, 350);
        gc.fillText("O", 500, 350);
        gc.fillText("R", 350, 500);
        gc.fillText("D", 500, 500);
    }

    private void initLevelSelection(int l) {
        if (middle.getChildren().contains(c1)) {
            middle.getChildren().removeAll(modeLabel, c1);
        }
        middle.getChildren().remove(c);
        c1 = new Canvas(550, 275);
        GraphicsContext gc = c1.getGraphicsContext2D();
        gc.setFill(Paint.valueOf("#444444"));
        gc.fillOval(0, 0, 100, 100);
        gc.fillOval(150, 0, 100, 100);
        gc.fillOval(300, 0, 100, 100);
        gc.fillOval(450, 0, 100, 100);

        gc.setFill(Color.WHITE);
        for (int i = 0; i < 4; i++) {
            if (i < controller.getGameData().getLevels()[l]) {
                gc.fillOval(i * 150, 0, 100, 100);
            }
        }

        gc.setFill(Color.BLACK);
        gc.fillText("1", 50, 50);
        gc.fillText("2", 200, 50);
        gc.fillText("3", 350, 50);
        gc.fillText("4", 500, 50);

        c1.setOnMouseMoved(e -> {
            if (e.getX() < 100 && e.getY() < 100) {
                c1.setCursor(Cursor.HAND);
            }
            else if ((e.getX() < 250 && e.getY() < 100) &&
                    (e.getX() > 150)) {
                c1.setCursor(Cursor.HAND);
            }
            else if ((e.getX() < 400 && e.getY() < 100) &&
                    (e.getX() > 300)) {
                c1.setCursor(Cursor.HAND);
            }
            else if ((e.getX() < 550 && e.getY() < 100) &&
                    (e.getX() > 450)) {
                c1.setCursor(Cursor.HAND);
            }
            else {
                c1.setCursor(Cursor.DEFAULT);
            }
        });
        c1.setOnMouseClicked(e -> {
            if (e.getX() < 100 && e.getY() < 100) {
                if (controller.getGameData().getLevels()[l] >= 1) {
                    initGameSelection(1);
                }
            }
            else if ((e.getX() < 250 && e.getY() < 100) &&
                    (e.getX() > 150)) {
                if (controller.getGameData().getLevels()[l] >= 2) {
                    initGameSelection(2);
                }
            }
            else if ((e.getX() < 400 && e.getY() < 100) &&
                    (e.getX() > 300)) {
                if (controller.getGameData().getLevels()[l] >= 3) {
                    initGameSelection(3);
                }
            }
            else if ((e.getX() < 550 && e.getY() < 100) &&
                    (e.getX() > 450)) {
                if (controller.getGameData().getLevels()[l] >= 4) {
                    initGameSelection(4);
                }
            }
        });

        middle.getChildren().addAll(modeLabel, c1);
    }

    private void initGameSelection(int l) {
        sec = 60 - ((l - 1) * 10);
        word1 = setWord(word);
        while (word1.length() > 5 || word1.length() < 3) {
            word1 = setWord(word);
        }
        word1 = word1.toUpperCase();
        System.out.println(word1);
        gameGrid = new char [4][4];

        Random ran = new Random();
        int temp1 = ran.nextInt(4);
        int temp2 = ran.nextInt(4);
        int temp3 = ran.nextInt(4);
        int temp4 = 0;
        gameGrid[temp1][temp2] = word1.charAt(temp4);
        temp4++;

        while (temp4 < word1.length()) {
            if ((temp1 == 0) && (temp2 == 0) && (gameGrid[1][0] != 0) &&
                    (gameGrid[0][1] != 0) && word1.length() != temp4 + 1) {
                temp4 = 0;
                gameGrid = new char [4][4];
                temp1 = ran.nextInt(4);
                temp2 = ran.nextInt(4);
                gameGrid[temp1][temp2] = word1.charAt(temp4);
            }
            else if ((temp1 == 3) && (temp2 == 0) && (gameGrid[2][0] != 0) &&
                    (gameGrid[3][1] != 0) && word1.length() != temp4 + 1) {
                temp4 = 0;
                gameGrid = new char [4][4];
                temp1 = ran.nextInt(4);
                temp2 = ran.nextInt(4);
                gameGrid[temp1][temp2] = word1.charAt(temp4);
            }
            else if ((temp1 == 0) && (temp2 == 3) && (gameGrid[0][2] != 0) &&
                    (gameGrid[1][3] != 0) && word1.length() != temp4 + 1) {
                temp4 = 0;
                gameGrid = new char [4][4];
                temp1 = ran.nextInt(4);
                temp2 = ran.nextInt(4);
                gameGrid[temp1][temp2] = word1.charAt(temp4);
            }
            else if ((temp1 == 3) && (temp2 == 3) && (gameGrid[2][3] != 0) &&
                    (gameGrid[3][2] != 0) && word1.length() != temp4 + 1) {
                temp4 = 0;
                gameGrid = new char [4][4];
                temp1 = ran.nextInt(4);
                temp2 = ran.nextInt(4);
                gameGrid[temp1][temp2] = word1.charAt(temp4);
            }
            else if ((temp3 == 0) && (temp1 - 1 > -1) && (gameGrid[temp1 - 1][temp2] == 0)) {
                gameGrid[temp1 - 1][temp2] = word1.charAt(temp4);
                temp1--;
            }
            else if ((temp3 == 1) && (temp2 - 1 > -1) && (gameGrid[temp1][temp2 - 1] == 0)) {
                gameGrid[temp1][temp2 - 1] = word1.charAt(temp4);
                temp2--;
            }
            else if ((temp3 == 2) && (temp1 + 1 < 4) && (gameGrid[temp1 + 1][temp2] == 0)) {
                gameGrid[temp1 + 1][temp2] = word1.charAt(temp4);
                temp1++;
            }
            else if ((temp3 == 3) && (temp2 + 1 < 4) && (gameGrid[temp1][temp2 + 1] == 0)) {
                gameGrid[temp1][temp2 + 1] = word1.charAt(temp4);
                temp2++;
            }
            else {
                temp4--;
                temp3 = ran.nextInt(4);
            }
            temp4++;
        }

        cubes = new char[16];
        found = false;
        wordsInside = new HashSet<String>();
        path = new int[16];

        for (int i = 0; gameGrid.length > i; i++) {
            for (int j = 0; gameGrid[0].length > j; j++) {
                if (gameGrid[i][j] == 0) {
                    int k = ran.nextInt(25);
                    gameGrid[i][j] = alph.charAt(k);
                }
            }
        }

        for (int i = 0; gameGrid.length > i; i++) {
            for (int j = 0; gameGrid[0].length > j; j++) {
                System.out.print(Character.toString(gameGrid[i][j]));
                cubes[i*4 + j] = (gameGrid[i][j]);
            }
            System.out.println();
        }

        for (char c : cubes) {
            System.out.print(c);
        }
        System.out.println();

        if (word.equals("dictionary")) {
            for (String s : dictionaryH) {
                if (s.length() > 2) {
                    path = new int[16];
                    for (int i = 0; i < path.length; i++) {
                        path[i] = -1;
                    }
                    found = false;
                    s = s.toUpperCase();
                    int i = 0;
                    for (char c : cubes) {
                        if (s.charAt(0) == c) {
                            dfs(s, 0, 0, i / 4, i % 4);
                        }
                        i++;
                        if (found) break;
                    }
                    if (found) {
                        wordsInside.add(s);
                    }
                }
            }
        }
        else if (word.equals("countries")) {
            for (String s : countriesH) {
                if (s.length() > 2) {
                    path = new int[16];
                    for (int i = 0; i < path.length; i++) {
                        path[i] = -1;
                    }
                    found = false;
                    s = s.toUpperCase();
                    int i = 0;
                    for (char c : cubes) {
                        if (s.charAt(0) == c) {
                            dfs(s, 0, 0, i / 4, i % 4);
                        }
                        i++;
                        if (found) break;
                    }
                    if (found) {
                        wordsInside.add(s);
                    }
                }
            }
        }
        else if (word.equals("names")) {
            for (String s : peopleH) {
                if (s.length() > 2) {
                    path = new int[16];
                    for (int i = 0; i < path.length; i++) {
                        path[i] = -1;
                    }
                    found = false;
                    s = s.toUpperCase();
                    int i = 0;
                    for (char c : cubes) {
                        if (s.charAt(0) == c) {
                            dfs(s, 0, 0, i / 4, i % 4);
                        }
                        i++;
                        if (found) break;
                    }
                    if (found) {
                        wordsInside.add(s);
                    }
                }
            }
        }
        else {
            for (String s : scienceH) {
                if (s.length() > 2) {
                    path = new int[16];
                    for (int i = 0; i < path.length; i++) {
                        path[i] = -1;
                    }
                    found = false;
                    s = s.toUpperCase();
                    int i = 0;
                    for (char c : cubes) {
                        if (s.charAt(0) == c) {
                            dfs(s, 0, 0, i / 4, i % 4);
                        }
                        i++;
                        if (found) break;
                    }
                    if (found) {
                        wordsInside.add(s);
                    }
                }
            }
        }

        for (String s: wordsInside) {
            System.out.println(s);
        }


        middle.getChildren().remove(c1);
        c2 = new Canvas(550, 550);
        GraphicsContext gc = c2.getGraphicsContext2D();
        gc.setFill(Paint.valueOf("#444444"));
        gc.fillOval(0, 0, 100, 100);
        gc.fillOval(150, 0, 100, 100);
        gc.fillOval(300, 0, 100, 100);
        gc.fillOval(450, 0, 100, 100);
        gc.fillOval(0, 150, 100, 100);
        gc.fillOval(150, 150, 100, 100);
        gc.fillOval(300, 150, 100, 100);
        gc.fillOval(450, 150, 100, 100);
        gc.fillOval(0, 300, 100, 100);
        gc.fillOval(150, 300, 100, 100);
        gc.fillOval(300, 300, 100, 100);
        gc.fillOval(450, 300, 100, 100);
        gc.fillOval(0, 450, 100, 100);
        gc.fillOval(150, 450, 100, 100);
        gc.fillOval(300, 450, 100, 100);
        gc.fillOval(450, 450, 100, 100);
        gc.fillRect(95, 50, 60, 5);
        gc.fillRect(245, 50, 60, 5);
        gc.fillRect(395, 50, 60, 5);
        gc.fillRect(95, 200, 60, 5);
        gc.fillRect(245, 200, 60, 5);
        gc.fillRect(395, 200, 60, 5);
        gc.fillRect(95, 350, 60, 5);
        gc.fillRect(245, 350, 60, 5);
        gc.fillRect(395, 350, 60, 5);
        gc.fillRect(95, 500, 60, 5);
        gc.fillRect(245, 500, 60, 5);
        gc.fillRect(395, 500, 60, 5);
        gc.fillRect(50, 95, 5, 60);
        gc.fillRect(50, 245, 5, 60);
        gc.fillRect(50, 395, 5, 60);
        gc.fillRect(200, 95, 5, 60);
        gc.fillRect(200, 245, 5, 60);
        gc.fillRect(200, 395, 5, 60);
        gc.fillRect(350, 95, 5, 60);
        gc.fillRect(350, 245, 5, 60);
        gc.fillRect(350, 395, 5, 60);
        gc.fillRect(500, 95, 5, 60);
        gc.fillRect(500, 245, 5, 60);
        gc.fillRect(500, 395, 5, 60);

        visited = new int [7][7];
        currentLetters = "";
        c2.setOnMouseExited(e -> {
            if (!gameEnd) {
                if (wordsInside.contains(currentLetters)) {
                    WordPoints twP = new WordPoints(currentLetters, Integer.toString((currentLetters.length() * 10) - 20));
                    wordP.add(twP);
                    wordsInside.remove(currentLetters);
                    int tempPoints = 0;
                    if (wordP.size() > 0) {
                        for (int i = 0; i < wordP.size(); i++) {
                            tempPoints = tempPoints + Integer.parseInt(wordP.get(i).getPoints());
                        }
                    }
                    totalPoints.setText("TOTAL                                                                  " +
                            "                   " + tempPoints);
                }
                visited = new int[7][7];
                resetGameGrid();
                currentLetters = "";
            }
        });
        c2.setOnMouseReleased(e -> {
            if (!gameEnd) {
                if (wordsInside.contains(currentLetters)) {
                    WordPoints twP = new WordPoints(currentLetters, Integer.toString((currentLetters.length() * 10) - 20));
                    wordP.add(twP);
                    wordsInside.remove(currentLetters);
                    int tempPoints = 0;
                    if (wordP.size() > 0) {
                        for (int i = 0; i < wordP.size(); i++) {
                            tempPoints = tempPoints + Integer.parseInt(wordP.get(i).getPoints());
                        }
                    }
                    totalPoints.setText("TOTAL                                                                  " +
                            "                   " + tempPoints);
                }
                visited = new int[7][7];
                resetGameGrid();
                currentLetters = "";
            }
        });
        c2.setOnMousePressed(ev -> {
            if (!gameEnd) {
                if (ev.getX() < 100 && ev.getY() < 100) {
                    visited[0][0] = 1;
                    currentLetters = currentLetters + gameGrid[0][0];
                } else if ((ev.getX() < 250 && ev.getY() < 100) &&
                        ev.getX() > 150) {
                    visited[0][2] = 1;
                    currentLetters = currentLetters + gameGrid[0][1];
                } else if ((ev.getX() < 400 && ev.getY() < 100) &&
                        ev.getX() > 300) {
                    visited[0][4] = 1;
                    currentLetters = currentLetters + gameGrid[0][2];
                } else if ((ev.getX() < 550 && ev.getY() < 100) &&
                        ev.getX() > 450) {
                    visited[0][6] = 1;
                    currentLetters = currentLetters + gameGrid[0][3];
                } else if (ev.getX() < 100 && ev.getY() < 250) {
                    visited[2][0] = 1;
                    currentLetters = currentLetters + gameGrid[1][0];
                } else if ((ev.getX() < 250 && ev.getY() < 250) &&
                        ev.getX() > 150) {
                    visited[2][2] = 1;
                    currentLetters = currentLetters + gameGrid[1][1];
                } else if ((ev.getX() < 400 && ev.getY() < 250) &&
                        ev.getX() > 300) {
                    visited[2][4] = 1;
                    currentLetters = currentLetters + gameGrid[1][2];
                } else if ((ev.getX() < 550 && ev.getY() < 250) &&
                        ev.getX() > 450) {
                    visited[2][6] = 1;
                    currentLetters = currentLetters + gameGrid[1][3];
                } else if (ev.getX() < 100 && ev.getY() < 400) {
                    visited[4][0] = 1;
                    currentLetters = currentLetters + gameGrid[2][0];
                } else if ((ev.getX() < 250 && ev.getY() < 400) &&
                        ev.getX() > 150) {
                    visited[4][2] = 1;
                    currentLetters = currentLetters + gameGrid[2][1];
                } else if ((ev.getX() < 400 && ev.getY() < 400) &&
                        ev.getX() > 300) {
                    visited[4][4] = 1;
                    currentLetters = currentLetters + gameGrid[2][2];
                } else if ((ev.getX() < 550 && ev.getY() < 400) &&
                        ev.getX() > 450) {
                    visited[4][6] = 1;
                    currentLetters = currentLetters + gameGrid[2][3];
                } else if (ev.getX() < 100 && ev.getY() < 550) {
                    visited[6][0] = 1;
                    currentLetters = currentLetters + gameGrid[3][0];
                } else if ((ev.getX() < 250 && ev.getY() < 550) &&
                        ev.getX() > 150) {
                    visited[6][2] = 1;
                    currentLetters = currentLetters + gameGrid[3][1];
                } else if ((ev.getX() < 400 && ev.getY() < 550) &&
                        ev.getX() > 300) {
                    currentLetters = currentLetters + gameGrid[3][2];
                    visited[6][4] = 1;
                } else if ((ev.getX() < 550 && ev.getY() < 550) &&
                        ev.getX() > 450) {
                    visited[6][6] = 1;
                    currentLetters = currentLetters + gameGrid[3][3];
                }
                resetGameGrid();
                c2.setOnMouseDragged(e -> {
                    if (e.getX() < 100 && e.getY() < 100) {
                        if (visited[0][0] == 0) {
                            currentLetters = currentLetters + gameGrid[0][0];
                        }
                        visited[0][0] = 1;
                    } else if ((e.getX() < 150 && e.getY() < 70) &&
                            e.getY() > 30 && e.getX() > 100) {
                        visited[0][1] = 1;
                    } else if ((e.getX() < 250 && e.getY() < 100) &&
                            e.getX() > 150) {
                        if (visited[0][2] == 0) {
                            currentLetters = currentLetters + gameGrid[0][1];
                        }
                        visited[0][2] = 1;
                    } else if ((e.getX() < 300 && e.getY() < 70) &&
                            e.getY() > 30 && e.getX() > 250) {
                        visited[0][3] = 1;
                    } else if ((e.getX() < 400 && e.getY() < 100) &&
                            e.getX() > 300) {
                        if (visited[0][4] == 0) {
                            currentLetters = currentLetters + gameGrid[0][2];
                        }
                        visited[0][4] = 1;
                    } else if ((e.getX() < 450 && e.getY() < 70) &&
                            e.getY() > 30 && e.getX() > 400) {
                        visited[0][5] = 1;
                    } else if ((e.getX() < 550 && e.getY() < 100) &&
                            e.getX() > 450) {
                        if (visited[0][6] == 0) {
                            currentLetters = currentLetters + gameGrid[0][3];
                        }
                        visited[0][6] = 1;
                    } else if (e.getX() < 70 && e.getY() < 150 &&
                            e.getX() > 30 && e.getY() > 100) {
                        visited[1][0] = 1;
                    } else if ((e.getX() < 220 && e.getY() < 150) &&
                            (e.getX() > 180) && e.getY() > 100) {
                        visited[1][2] = 1;
                    } else if ((e.getX() < 370 && e.getY() < 150) &&
                            (e.getX() > 330) && e.getY() > 100) {
                        visited[1][4] = 1;
                    } else if ((e.getX() < 520 && e.getY() < 150) &&
                            (e.getX() > 480) && e.getY() > 100) {
                        visited[1][6] = 1;
                    } else if (e.getX() < 100 && e.getY() < 250 &&
                            e.getY() > 150) {
                        if (visited[2][0] == 0) {
                            currentLetters = currentLetters + gameGrid[1][0];
                        }
                        visited[2][0] = 1;
                    } else if ((e.getX() < 150 && e.getY() < 220) &&
                            e.getY() > 180 && e.getX() > 100) {
                        visited[2][1] = 1;
                    } else if ((e.getX() < 250 && e.getY() < 250) &&
                            e.getX() > 150 && e.getY() > 150) {
                        if (visited[2][2] == 0) {
                            currentLetters = currentLetters + gameGrid[1][1];
                        }
                        visited[2][2] = 1;
                    } else if ((e.getX() < 300 && e.getY() < 220) &&
                            e.getY() > 180 && e.getX() > 250) {
                        visited[2][3] = 1;
                    } else if ((e.getX() < 400 && e.getY() < 250) &&
                            e.getX() > 300 && e.getY() > 150) {
                        if (visited[2][4] == 0) {
                            currentLetters = currentLetters + gameGrid[1][2];
                        }
                        visited[2][4] = 1;
                    } else if ((e.getX() < 450 && e.getY() < 220) &&
                            e.getY() > 180 && e.getX() > 400) {
                        visited[2][5] = 1;
                    } else if ((e.getX() < 550 && e.getY() < 250) &&
                            e.getX() > 450 && e.getY() > 150) {
                        if (visited[2][6] == 0) {
                            currentLetters = currentLetters + gameGrid[1][3];
                        }
                        visited[2][6] = 1;
                    } else if (e.getX() < 70 && e.getY() < 300 &&
                            e.getX() > 30 && e.getY() > 250) {
                        visited[3][0] = 1;
                    } else if ((e.getX() < 220 && e.getY() < 300) &&
                            (e.getX() > 180) && e.getY() > 250) {
                        visited[3][2] = 1;
                    } else if ((e.getX() < 370 && e.getY() < 300) &&
                            (e.getX() > 330) && e.getY() > 250) {
                        visited[3][4] = 1;
                    } else if ((e.getX() < 520 && e.getY() < 300) &&
                            (e.getX() > 480) && e.getY() > 250) {
                        visited[3][6] = 1;
                    } else if (e.getX() < 100 && e.getY() < 400 &&
                            e.getY() > 300) {
                        if (visited[4][0] == 0) {
                            currentLetters = currentLetters + gameGrid[2][0];
                        }
                        visited[4][0] = 1;
                    } else if ((e.getX() < 150 && e.getY() < 370) &&
                            e.getY() > 330 && e.getX() > 100) {
                        visited[4][1] = 1;
                    } else if ((e.getX() < 250 && e.getY() < 400) &&
                            e.getX() > 150 && e.getY() > 300) {
                        if (visited[4][2] == 0) {
                            currentLetters = currentLetters + gameGrid[2][1];
                        }
                        visited[4][2] = 1;
                    } else if ((e.getX() < 300 && e.getY() < 370) &&
                            e.getY() > 330 && e.getX() > 250) {
                        visited[4][3] = 1;
                    } else if ((e.getX() < 400 && e.getY() < 400) &&
                            e.getX() > 300 && e.getY() > 300) {
                        if (visited[4][4] == 0) {
                            currentLetters = currentLetters + gameGrid[2][2];
                        }
                        visited[4][4] = 1;
                    } else if ((e.getX() < 450 && e.getY() < 370) &&
                            e.getY() > 330 && e.getX() > 400) {
                        visited[4][5] = 1;
                    } else if ((e.getX() < 550 && e.getY() < 400) &&
                            e.getX() > 450 && e.getY() > 300) {
                        if (visited[4][6] == 0) {
                            currentLetters = currentLetters + gameGrid[2][3];
                        }
                        visited[4][6] = 1;
                    } else if (e.getX() < 70 && e.getY() < 450 &&
                            e.getX() > 30 && e.getY() > 400) {
                        visited[5][0] = 1;
                    } else if ((e.getX() < 220 && e.getY() < 450) &&
                            (e.getX() > 180) && e.getY() > 400) {
                        visited[5][2] = 1;
                    } else if ((e.getX() < 370 && e.getY() < 450) &&
                            (e.getX() > 330) && e.getY() > 400) {
                        visited[5][4] = 1;
                    } else if ((e.getX() < 520 && e.getY() < 450) &&
                            (e.getX() > 480) && e.getY() > 400) {
                        visited[5][6] = 1;
                    } else if (e.getX() < 100 && e.getY() < 550 &&
                            e.getY() > 450) {
                        if (visited[6][0] == 0) {
                            currentLetters = currentLetters + gameGrid[3][0];
                        }
                        visited[6][0] = 1;
                    } else if ((e.getX() < 150 && e.getY() < 520) &&
                            e.getY() > 480 && e.getX() > 100) {
                        visited[6][1] = 1;
                    } else if ((e.getX() < 250 && e.getY() < 550) &&
                            e.getX() > 150 && e.getY() > 450) {
                        if (visited[6][2] == 0) {
                            currentLetters = currentLetters + gameGrid[3][1];
                        }
                        visited[6][2] = 1;
                    } else if ((e.getX() < 300 && e.getY() < 520) &&
                            e.getY() > 480 && e.getX() > 250) {
                        visited[6][3] = 1;
                    } else if ((e.getX() < 400 && e.getY() < 550) &&
                            e.getX() > 300 && e.getY() > 450) {
                        if (visited[6][4] == 0) {
                            currentLetters = currentLetters + gameGrid[3][2];
                        }
                        visited[6][4] = 1;
                    } else if ((e.getX() < 450 && e.getY() < 520) &&
                            e.getY() > 480 && e.getX() > 400) {
                        visited[6][5] = 1;
                    } else if ((e.getX() < 550 && e.getY() < 550) &&
                            e.getX() > 450 && e.getY() > 450) {
                        if (visited[6][6] == 0) {
                            currentLetters = currentLetters + gameGrid[3][3];
                        }
                        visited[6][6] = 1;
                    }
                    resetGameGrid();
                });
            }
        });

        gc.setFill(Color.LIGHTGREEN);
        for (int i = 0; gameGrid.length > i; i++) {
            for (int j = 0; gameGrid[0].length > j; j++) {
                gc.fillText(Character.toString(Character.toUpperCase(gameGrid[j][i])), (i * 150) + 50, (j * 150) + 50);
            }
        }


        level.setText("Level " + l);
        level.setFont(Font.font("Monospaced", 40));
        level.setTextFill(Color.WHITE);
        play = new Button();
        play.setStyle("-fx-background-color: transparent;");
        Image buttonImage = new Image("Arrow.png");
        play.setGraphic(new ImageView(buttonImage));
        play.setOnMouseEntered(e -> {
            play.setCursor(Cursor.HAND);
        });
        play.setOnAction(e -> {
            if (paused) {
                Image tempImage = new Image("Pause.png");
                play.setGraphic(new ImageView(tempImage));
                timer = new java.util.Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        Platform.runLater(() -> {
                            if (!paused) {
                                if (sec != 0) {
                                    sec--;
                                    updateTimeRem(sec);
                                }
                                else {
                                    try {
                                        endGame(l);
                                        timer.cancel();
                                        timer.purge();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                            else {
                                timer.cancel();
                                timer.purge();
                            }
                        });
                    }
                }, 0, 1000);
                middle.getChildren().removeAll(c, level, play);
                middle.getChildren().addAll(c2, level, play);
            }
            else {
                Image tempImage = new Image("Arrow.png");
                play.setGraphic(new ImageView(tempImage));
                middle.getChildren().removeAll(c2, level, play);
                middle.getChildren().addAll(c, level, play);
            }
            paused = !paused;
        });

        middle.getChildren().addAll(c, level, play);

        timeRem = new Label("Time Remaining: " + sec + " seconds");
        timeRem.setTextFill(Color.RED);
        timeRem.setStyle("-fx-background-color: #cccccc;");
        timeRem.setPrefSize(350, 50);
        current = new TextField("");
        current.setEditable(true);
        current.setStyle("-fx-background-color: #444444;");
        current.setPrefSize(350, 50);

        current.setOnKeyReleased(e -> {
            if (!gameEnd) {
                if (e.getCode() == KeyCode.ENTER) {
                    if (wordsInside.contains(current.getText().toUpperCase())) {
                        WordPoints twP = new WordPoints(current.getText().toUpperCase(), Integer.toString((current.getText().length() * 10) - 20));
                        wordP.add(twP);
                        wordsInside.remove(current.getText().toUpperCase());
                        int tempPoints = 0;
                        if (wordP.size() > 0) {
                            for (int i = 0; i < wordP.size(); i++) {
                                tempPoints = tempPoints + Integer.parseInt(wordP.get(i).getPoints());
                            }
                        }
                        totalPoints.setText("TOTAL                                                                  " +
                                "                   " + tempPoints);
                    }
                    visited = new int[7][7];
                    resetGameGrid();
                    current.setText("");
                } else {
                    highlight(current.getText().toUpperCase());
                    visited = new int[7][7];
                }
            }
        });

        tvtp = new VBox();

        wordP = FXCollections.observableArrayList();


        tV = new TableView();
        tV.setStyle("-fx-background-color: Black;");
        TableColumn wordCol = new TableColumn("Words");
        wordCol.setStyle("-fx-background-color: #444444;");
        wordCol.setPrefWidth(300);
        wordCol.setCellValueFactory(new PropertyValueFactory<WordPoints, String>("words"));

        TableColumn pointCol = new TableColumn("Points");
        pointCol.setStyle("-fx-background-color: #444444;");
        pointCol.setPrefWidth(50);
        pointCol.setCellValueFactory(new PropertyValueFactory<WordPoints, Integer>("points"));

        wordCol.setCellFactory(new Callback<TableColumn, TableCell>() {
            public TableCell call(TableColumn param) {
                return new TableCell<WordPoints, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            this.setTextFill(Color.WHITE);
                            setText(item);
                        }
                    }
                };
            }
        });
        pointCol.setCellFactory(new Callback<TableColumn, TableCell>() {
            public TableCell call(TableColumn param) {
                return new TableCell<WordPoints, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            this.setTextFill(Color.WHITE);
                            setText(item);
                        }
                    }
                };
            }
        });
        tV.setItems(wordP);
        tV.getColumns().addAll(wordCol, pointCol);
        int tempPoints = 0;
        if (wordP.size() > 0) {
            for (int i = 0; i < wordP.size(); i++) {
                tempPoints = tempPoints + Integer.parseInt(wordP.get(i).getPoints());
            }
        }
        totalPoints = new Label("TOTAL                                                                  " +
                "                   " + tempPoints);
        totalPoints.setTextFill(Color.WHITE);
        totalPoints.setStyle("-fx-background-color: Black;");
        totalPoints.setPrefSize(350, 50);
        tvtp.getChildren().addAll(tV, totalPoints);
        targetScore = wordsInside.size() * 2;
        target = new Label("Target: " + targetScore + " points");
        target.setStyle("-fx-background-color: #444444");
        target.setTextFill(Color.WHITE);
        target.setPrefSize(350, 50);
        restartGame = new Button ("Restart");
        restartGame.setStyle("-fx-background-color: Black;");
        restartGame.setTextFill(Color.WHITE);
        restartGame.setOnAction(e -> {
            middle.getChildren().removeAll(c2, level, play);
            middle.getChildren().add(c1);
            right.getChildren().removeAll(timeRem, current, tvtp, target, restartGame);
            initGameSelection(l);
        });
        restartGame.setOnMouseEntered(e -> {
            restartGame.setCursor(Cursor.HAND);
        });
        right.setSpacing(50);
        right.getChildren().addAll(timeRem, current, tvtp, target, restartGame);
    }

    private void endGame(int l) throws IOException {
        gameEnd = true;
        int tempPoints = 0;
        if (wordP.size() > 0) {
            for (int i = 0; i < wordP.size(); i++) {
                tempPoints = tempPoints + Integer.parseInt(wordP.get(i).getPoints());
            }
        }
        for (String s : wordsInside) {
            WordPoints tempWordPoint = new WordPoints(s, "N/A");
            wordP.add(tempWordPoint);
        }
        if (tempPoints >= targetScore) {
            createOverLay4();
            if (word.equals("dictionary")) {
                if (l >= controller.getGameData().getLevels()[0]) {
                    controller.getGameData().setLevels(0, l + 1);
                }
            }
            else if (word.equals("countries")) {
                if (l >= controller.getGameData().getLevels()[1]) {
                    controller.getGameData().setLevels(1, l + 1);
                }
            }
            else if (word.equals("science")) {
                if (l >= controller.getGameData().getLevels()[2]) {
                    controller.getGameData().setLevels(2, l + 1);
                }
            }
            else {
                if (l >= controller.getGameData().getLevels()[3]) {
                    controller.getGameData().setLevels(3, l + 1);
                }
            }
            controller.handleSaveRequest2();
        }
        else {
            createOverLay5();
        }
    }

    public void highlight(String s) {
        path = new int[16];
        for (int i = 0; i < path.length; i++) {
            path[i] = -1;
        }
        found = false;
        s = s.toUpperCase();
        int i = 0;
        for (char c : cubes) {
            if (s.charAt(0) == c) {
                dfs(s, 0, 0, i / 4, i % 4);
            }
            i++;
            if (found) break;
        }
        if (found) {
            for (int j = 0; j < path.length; j++) {
                if (path[j] == -1) break;
                visited[(path[j] / 4) * 2][(path[j] % 4) * 2] = 1;
                resetGameGrid();
            }
        }
        else {
            visited = new int[7][7];
            resetGameGrid();
        }
    }



    private void resetGameGrid() {
        GraphicsContext gc = c2.getGraphicsContext2D();
        gc.setFill(Paint.valueOf("#444444"));
        gc.fillOval(0, 0, 100, 100);
        gc.fillOval(150, 0, 100, 100);
        gc.fillOval(300, 0, 100, 100);
        gc.fillOval(450, 0, 100, 100);
        gc.fillOval(0, 150, 100, 100);
        gc.fillOval(150, 150, 100, 100);
        gc.fillOval(300, 150, 100, 100);
        gc.fillOval(450, 150, 100, 100);
        gc.fillOval(0, 300, 100, 100);
        gc.fillOval(150, 300, 100, 100);
        gc.fillOval(300, 300, 100, 100);
        gc.fillOval(450, 300, 100, 100);
        gc.fillOval(0, 450, 100, 100);
        gc.fillOval(150, 450, 100, 100);
        gc.fillOval(300, 450, 100, 100);
        gc.fillOval(450, 450, 100, 100);
        gc.fillRect(95, 50, 60, 5);
        gc.fillRect(245, 50, 60, 5);
        gc.fillRect(395, 50, 60, 5);
        gc.fillRect(95, 200, 60, 5);
        gc.fillRect(245, 200, 60, 5);
        gc.fillRect(395, 200, 60, 5);
        gc.fillRect(95, 350, 60, 5);
        gc.fillRect(245, 350, 60, 5);
        gc.fillRect(395, 350, 60, 5);
        gc.fillRect(95, 500, 60, 5);
        gc.fillRect(245, 500, 60, 5);
        gc.fillRect(395, 500, 60, 5);
        gc.fillRect(50, 95, 5, 60);
        gc.fillRect(50, 245, 5, 60);
        gc.fillRect(50, 395, 5, 60);
        gc.fillRect(200, 95, 5, 60);
        gc.fillRect(200, 245, 5, 60);
        gc.fillRect(200, 395, 5, 60);
        gc.fillRect(350, 95, 5, 60);
        gc.fillRect(350, 245, 5, 60);
        gc.fillRect(350, 395, 5, 60);
        gc.fillRect(500, 95, 5, 60);
        gc.fillRect(500, 245, 5, 60);
        gc.fillRect(500, 395, 5, 60);

        gc.setFill(Color.WHITE);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if ((i == 0 || i == 2 || i == 4 || i == 6) &&
                        (j == 0 || j == 2 || j == 4 || j == 6) &&
                        (visited[i][j] == 1)){
                    gc.fillOval(j * 75, i * 75, 100, 100);
                }
                else if ((i == 1 || i == 3 || i == 5) &&
                        (j == 0 || j == 2 || j == 4 || j == 6) &&
                        (visited[i][j] == 1)){
                    gc.fillRect((j * 75) + 50, ((i - 1) * 75) + 95, 5, 60);                }
                else if ((i == 0 || i == 2 || i == 4 || i == 6) &&
                        (j == 1 || j == 3 || j == 5) &&
                        (visited[i][j] == 1)){
                    gc.fillRect(((j - 1) * 75) + 95, (i * 75) + 50, 60, 5);
                }
            }
        }

        gc.setFill(Color.LIGHTGREEN);
        for (int i = 0; gameGrid.length > i; i++) {
            for (int j = 0; gameGrid[0].length > j; j++) {
                gc.fillText(Character.toString(Character.toUpperCase(gameGrid[j][i])), (i * 150) + 50, (j * 150) + 50);
            }
        }
    }

    public void updateTimeRem(int s) {
        right.getChildren().removeAll(timeRem, current, tvtp, target, restartGame);
        timeRem.setText("Time Remaining: " + s + " seconds");
        right.getChildren().addAll(timeRem, current, tvtp, target, restartGame);
    }

    @Override
    public void initStyle() {
        PropertyManager propertyManager = PropertyManager.getManager();

        gui.getAppPane().setId(propertyManager.getPropertyValue(ROOT_BORDERPANE_ID));

        workspace.getStyleClass().add(CLASS_BORDERED_PANE);
        guiHeadingLabel.getStyleClass().setAll(propertyManager.getPropertyValue(HEADING_LABEL));

    }

    public void createOverLay() {
        VBox tempV = new VBox();
        tempV.setMaxSize(400, 300);
        tempV.setSpacing(30);
        Label tempL = new Label("ID is taken, please choose another ID");
        tempL.setTextFill(Color.RED);
        tempV.setAlignment(Pos.CENTER);
        tempV.setOpacity(.75);
        tempL.setFont(Font.font("Monospaced", 15));
        tempL.setStyle("-fx-background-color: Black;");
        tempV.setStyle("-fx-background-color: Black;");
        Button tempB = new Button ("Return");
        tempB.setTextFill(Color.WHITE);
        tempB.setStyle("-fx-background-color: Black;");
        tempB.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
        });
        tempB.setOnMouseEntered(e -> {
            tempB.setCursor(Cursor.HAND);
        });
        tempB.setStyle("-fx-background-color: #cccccc;");
        tempV.getChildren().addAll(tempL, tempB);
        realMid.getChildren().add(tempV);
    }

    public void createOverLay1() {
        VBox tempV = new VBox();
        tempV.setMaxSize(400, 300);
        tempV.setSpacing(30);
        Label tempL = new Label("Cannot recognize the account");
        tempL.setTextFill(Color.RED);
        tempV.setAlignment(Pos.CENTER);
        tempV.setOpacity(.75);
        tempL.setFont(Font.font("Monospaced", 15));
        tempL.setStyle("-fx-background-color: Black;");
        tempV.setStyle("-fx-background-color: Black;");
        Button tempB = new Button ("Return");
        tempB.setTextFill(Color.WHITE);
        tempB.setStyle("-fx-background-color: Black;");
        tempB.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
        });
        tempB.setOnMouseEntered(e -> {
            tempB.setCursor(Cursor.HAND);
        });
        tempB.setStyle("-fx-background-color: #cccccc;");
        tempV.getChildren().addAll(tempL, tempB);
        realMid.getChildren().add(tempV);
    }

    public void createOverLay2() {
        VBox tempV = new VBox();
        tempV.setMaxSize(400, 300);
        tempV.setSpacing(30);
        Label tempL = new Label("Are you sure you want to quit?");
        tempL.setTextFill(Color.RED);
        tempV.setAlignment(Pos.CENTER);
        tempV.setOpacity(.75);
        tempL.setFont(Font.font("Monospaced", 15));
        tempL.setStyle("-fx-background-color: Black;");
        tempV.setStyle("-fx-background-color: Black;");
        Button tempB = new Button ("Yes");
        tempB.setTextFill(Color.BLACK);
        tempB.setStyle("-fx-background-color: Black;");
        tempB.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
            System.exit(0);
        });
        tempB.setOnMouseEntered(e -> {
            tempB.setCursor(Cursor.HAND);
        });
        tempB.setStyle("-fx-background-color: #cccccc;");
        Button tempB1 = new Button ("No");
        tempB1.setTextFill(Color.BLACK);
        tempB1.setStyle("-fx-background-color: Black;");
        tempB1.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
        });

        tempB1.setOnMouseEntered(e -> {
            tempB1.setCursor(Cursor.HAND);
        });
        tempB1.setStyle("-fx-background-color: #cccccc;");
        HBox tempH = new HBox();
        tempH.setSpacing(30);
        tempH.setAlignment(Pos.CENTER);
        tempH.getChildren().addAll(tempB, tempB1);
        tempV.getChildren().addAll(tempL, tempH);
        realMid.getChildren().add(tempV);
    }

    public void createOverLay3() {
        VBox tempV = new VBox();
        tempV.setMaxSize(400, 300);
        tempV.setSpacing(30);
        Label tempL = new Label("Logout?");
        tempL.setTextFill(Color.RED);
        tempV.setAlignment(Pos.CENTER);
        tempV.setOpacity(.75);
        tempL.setFont(Font.font("Monospaced", 15));
        tempL.setStyle("-fx-background-color: Black;");
        tempV.setStyle("-fx-background-color: Black;");
        Button tempB = new Button ("Yes");
        tempB.setTextFill(Color.BLACK);
        tempB.setStyle("-fx-background-color: Black;");
        tempB.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
            reinitialize();
            ////////
            //
            //
            //
            //
            //
            //
            //
            ///
        });
        tempB.setOnMouseEntered(e -> {
            tempB.setCursor(Cursor.HAND);
        });
        tempB.setStyle("-fx-background-color: #cccccc;");
        Button tempB1 = new Button ("No");
        tempB1.setTextFill(Color.BLACK);
        tempB1.setStyle("-fx-background-color: Black;");
        tempB1.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
        });

        tempB1.setOnMouseEntered(e -> {
            tempB1.setCursor(Cursor.HAND);
        });
        tempB1.setStyle("-fx-background-color: #cccccc;");
        HBox tempH = new HBox();
        tempH.setSpacing(30);
        tempH.setAlignment(Pos.CENTER);
        tempH.getChildren().addAll(tempB, tempB1);
        tempV.getChildren().addAll(tempL, tempH);
        realMid.getChildren().add(tempV);
    }

    public void createOverLay4() {
        VBox tempV = new VBox();
        tempV.setMaxSize(400, 300);
        tempV.setSpacing(30);
        Label tempL = new Label("You Win!");
        tempL.setTextFill(Color.RED);
        tempV.setAlignment(Pos.CENTER);
        tempV.setOpacity(.75);
        tempL.setFont(Font.font("Monospaced", 15));
        tempL.setStyle("-fx-background-color: Black;");
        tempV.setStyle("-fx-background-color: Black;");
        Button tempB = new Button ("Return");
        tempB.setTextFill(Color.WHITE);
        tempB.setStyle("-fx-background-color: Black;");
        tempB.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
        });
        tempB.setOnMouseEntered(e -> {
            tempB.setCursor(Cursor.HAND);
        });
        tempB.setStyle("-fx-background-color: #cccccc;");

        Button tempB1 = new Button ("Next Level");
        tempB1.setTextFill(Color.WHITE);
        tempB1.setStyle("-fx-background-color: Black;");
        tempB1.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);

        });
        tempB1.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
            middle.getChildren().removeAll(c2, level, play);
            right.getChildren().removeAll(timeRem, current, tvtp, target, restartGame);
            middle.getChildren().addAll(c1);
            initGameSelection(level.getText().charAt(6) - 48);
        });

        tempB1.setStyle("-fx-background-color: #cccccc;");
        tempV.getChildren().addAll(tempL, tempB, tempB1);
        realMid.getChildren().add(tempV);
    }

    public void createOverLay5() {
        VBox tempV = new VBox();
        tempV.setMaxSize(400, 300);
        tempV.setSpacing(30);
        Label tempL = new Label("You Lose");
        tempL.setTextFill(Color.RED);
        tempV.setAlignment(Pos.CENTER);
        tempV.setOpacity(.75);
        tempL.setFont(Font.font("Monospaced", 15));
        tempL.setStyle("-fx-background-color: Black;");
        tempV.setStyle("-fx-background-color: Black;");
        Button tempB = new Button ("Return");
        tempB.setTextFill(Color.WHITE);
        tempB.setStyle("-fx-background-color: Black;");
        tempB.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
        });
        tempB.setOnMouseEntered(e -> {
            tempB.setCursor(Cursor.HAND);
        });
        tempB.setStyle("-fx-background-color: #cccccc;");
        tempV.getChildren().addAll(tempL, tempB);
        realMid.getChildren().add(tempV);
    }
    public void createOverLay6() {
        ScrollPane scrollP = new ScrollPane();
        scrollP.setStyle("-fx-background-color: Black;");
        VBox tempV = new VBox();
        tempV.setMaxSize(1000, 300);
        tempV.setSpacing(30);
        Text tempL = new Text("Professaur Inc. provides custom software development approved by the professor, incorporating the\n" +
                "developmental aspects of approved software projects including specification, design, implementation,\n" +
                "and testing. At Professaur Inc., we strive to build applications that exercise the mind while still allowing\n" +
                "enough room for relaxation. In particular, our focus is on educational games.\n" +
                "Educational games should be easy to learn, easy to play, and yet not too easy to master. The mechanics\n" +
                "of the gameplay should be simple, so that the player is only required to perform very simple actions –\n" +
                "a usability aspect that is becoming increasingly important in today’s world of touch screen mobile\n" +
                "devices where most actions should not require more than taps or swipes. In spite of the simplicity of\n" +
                "actions, a game can remain interesting to a player if the intellectual difficulty required to master it is\n" +
                "gradually increased. This is a strategy where players compete with their own past selves. Of course,\n" +
                "intellectual competition can also be introduced in other ways, such as having multiplayer modes.\n" +
                "The primary objective is, however, to make the learning process casual and fun so that players learn\n" +
                "even without consciously realizing it. BuzzWord, a generalization of the popular word game\n" +
                "“Boggle”, aims to fulfil exactly that goal while helping players build up their vocabulary.\n" +
                "Much like Boggle, BuzzWord is a game where players are given a network of connected letters, and\n" +
                "they aim to identify as many words as possible, within a limited amount of time. As players get\n" +
                "promoted from one level to the next, the promotion criteria to get into the subsequent levels become\n" +
                "progressively more difficult. Oh, and the game may ask the player to explain what the word means …\n" +
                "if they want the points, that is.");
        tempL.setFill(Color.RED);
        tempV.setAlignment(Pos.CENTER);
        tempV.setOpacity(.75);
        tempL.setFont(Font.font("Monospaced", 15));
        tempL.setStyle("-fx-background-color: Black;");
        tempV.setStyle("-fx-background-color: Black;");
        Button tempB = new Button ("Return");
        tempB.setTextFill(Color.WHITE);
        tempB.setStyle("-fx-background-color: Black;");
        tempB.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
        });
        tempB.setOnMouseEntered(e -> {
            tempB.setCursor(Cursor.HAND);
        });
        tempB.setStyle("-fx-background-color: #cccccc;");
        scrollP.setContent(tempL);
        tempV.getChildren().addAll(scrollP, tempB);
        realMid.getChildren().add(tempV);
    }

    public void createOverLay7() {
        VBox tempV = new VBox();
        tempV.setMaxSize(400, 300);
        tempV.setSpacing(30);
        Label tempL = new Label("Choose what you wish to do");
        tempL.setTextFill(Color.RED);
        tempV.setAlignment(Pos.CENTER);
        tempV.setOpacity(.75);
        tempL.setFont(Font.font("Monospaced", 15));
        tempL.setStyle("-fx-background-color: Black;");
        tempV.setStyle("-fx-background-color: Black;");
        Button tempB = new Button ("Edit Profile");
        tempB.setTextFill(Color.BLACK);
        tempB.setStyle("-fx-background-color: Black;");
        tempB.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
            createOverLay8();
        });
        tempB.setOnMouseEntered(e -> {
            tempB.setCursor(Cursor.HAND);
        });
        tempB.setStyle("-fx-background-color: #cccccc;");
        Button tempB1 = new Button ("Return");
        tempB1.setTextFill(Color.BLACK);
        tempB1.setStyle("-fx-background-color: Black;");
        tempB1.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
        });

        tempB1.setOnMouseEntered(e -> {
            tempB1.setCursor(Cursor.HAND);
        });
        tempB1.setStyle("-fx-background-color: #cccccc;");
        Button tempB2 = new Button("Log Out");
        tempB2.setTextFill(Color.BLACK);
        tempB2.setStyle("-fx-background-color: Black;");
        tempB2.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
            controller = new BuzzWordController(app);
            controller.handleNewRequest();
            reinitialize();
        });
        tempB2.setOnMouseEntered(e -> {
            tempB2.setCursor(Cursor.HAND);
        });
        tempB2.setStyle("-fx-background-color: #cccccc;");
        HBox tempH = new HBox();
        tempH.setSpacing(30);
        tempH.setAlignment(Pos.CENTER);
        tempH.getChildren().addAll(tempB, tempB2, tempB1);
        tempV.getChildren().addAll(tempL, tempH);
        realMid.getChildren().add(tempV);
    }

    public void createOverLay8() {
        VBox tempV = new VBox();
        tempV.setMaxSize(400, 300);
        tempV.setSpacing(30);
        Label tempL = new Label("Enter new username and password");
        tempL.setTextFill(Color.RED);
        tempV.setAlignment(Pos.CENTER);
        tempV.setOpacity(.75);
        tempL.setFont(Font.font("Monospaced", 15));
        tempL.setStyle("-fx-background-color: Black;");
        tempV.setStyle("-fx-background-color: Black;");
        Button tempB = new Button ("Update");
        tempB.setTextFill(Color.BLACK);
        tempB.setStyle("-fx-background-color: Black;");
        tempB.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
            try {
                File selectedFile = new File("./saved/" + controller.getGameData().getID() + ".json");
                selectedFile.delete();
                login.setText(profName.getText());
                controller.getGameData().setID(profName.getText());
                MessageDigest digest = MessageDigest.getInstance("MD5");
                String tempPwd = profPass.getText();
                digest.update(tempPwd.getBytes(), 0, tempPwd.length());
                String pwd = new BigInteger(1, digest.digest()).toString(16);
                controller.getGameData().setPW(pwd);
                controller.handleSaveRequest2();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
        });
        tempB.setOnMouseEntered(e -> {
            tempB.setCursor(Cursor.HAND);
        });
        tempB.setStyle("-fx-background-color: #cccccc;");
        Button tempB1 = new Button ("Return");
        tempB1.setTextFill(Color.BLACK);
        tempB1.setStyle("-fx-background-color: Black;");
        tempB1.setOnMouseClicked(e -> {
            realMid.getChildren().remove(tempV);
        });

        tempB1.setOnMouseEntered(e -> {
            tempB1.setCursor(Cursor.HAND);
        });
        tempB1.setStyle("-fx-background-color: #cccccc;");
        HBox tempH = new HBox();
        tempH.setSpacing(30);
        tempH.setAlignment(Pos.CENTER);
        tempH.getChildren().addAll(tempB, tempB1);
        tempV.getChildren().addAll(tempL, name, password, tempH);
        realMid.getChildren().add(tempV);
    }
    @Override
    public void reloadWorkspace() {reinitialize();
    }

    public void reinitialize() {
        left = new VBox();
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        left.setMinHeight(primaryScreenBounds.getHeight());
        left.setMinWidth(200);
        left.setStyle("-fx-background-color: #cccccc;");
        left.setSpacing(50);
        Text space = new Text("");
        VBox modeBox = new VBox();
        modeBox.getChildren().addAll(mode, dictionary, countries, science, people);
        newProfile.setVisible(true);
        login.setText("Login");
        mode.setVisible(false);
        dictionary.setVisible(false);
        countries.setVisible(false);
        science.setVisible(false);
        people.setVisible(false);
        home.setVisible(false);
        left.getChildren().addAll(space, newProfile, login, modeBox, home, help);

        middle = new VBox();
        middle.setAlignment(Pos.TOP_CENTER);
        middle.setSpacing(50);
        middle.getChildren().addAll(guiHeadingLabel, c);

        right = new VBox();
        right.setAlignment(Pos.TOP_RIGHT);
        right.getChildren().add(x);

        realMid.getChildren().add(middle);
        wS.setRight(right);
        wS.setCenter(realMid);
        wS.setLeft(left);
    }

    private String setWord(String category) {
        URL wordsResource = getClass().getClassLoader().
                getResource("words/" + category + ".txt");
        assert wordsResource != null;
        int toSkip;
        if (category.equals("dictionary")) {
            toSkip = new Random().nextInt(dictionaryH.size());
        }
        else if (category.equals("countries")) {
            toSkip = new Random().nextInt(countriesH.size());
        }
        else if (category.equals("names")) {
            toSkip = new Random().nextInt(peopleH.size());
        }
        else {
            toSkip = new Random().nextInt(scienceH.size());
        }

        try (Stream<String> lines = Files.lines(Paths.get(wordsResource.toURI()))) {
            return lines.skip(toSkip).findFirst().get();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }

        throw new GameError("Unable to load initial target word.");
    }

    private void dfs (String s, int curChar, int pathIndex, int i, int j) {
        if (i < 0 || j < 0 || i >= 4 || j >= 4) return;
        if (curChar >= s.length()) {
            found = true;
            return;
        }
        for (int n = 0; n < 16; n++) {
            if (path[n] == (i*4)+j) return;
        }
        if (cubes[(i*4)+j] != s.charAt(curChar)) {
            return;
        }
        path[pathIndex] = (i*4)+j;
        for (int ii = -1; ii <= 1; ii++)
            for (int jj = -1; jj <= 1; jj++)
                if (!found) dfs(s, curChar+1, pathIndex+1, i + ii, j + jj);

        if (!found) path[curChar] = -1;
    }
}
