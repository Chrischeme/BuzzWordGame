package gui;

import javafx.collections.ObservableList;

/**
 * Created by Chris on 11/14/2016.
 */
public class WordPoints {
    private String words;
    private String points;

    WordPoints() {

    }
    WordPoints(String word, String point) {
        words = word;
        points = point;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
