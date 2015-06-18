package us.mcmagic.magicassistant.quiz;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Marc on 6/3/15
 */
public class Question {
    private String text;
    private HashMap<Integer, String> choices = new HashMap<>();

    public Question(String text, List<String> choices) {
        this.text = text;
        int i = 0;
        for (String s : choices) {
            this.choices.put(i, s);
            i++;
        }
    }

    public String getText() {
        return text;
    }

    public HashMap<Integer, String> getChoices() {
        return choices;
    }

    public String getChoice(int id) {
        return choices.get(id);
    }
}