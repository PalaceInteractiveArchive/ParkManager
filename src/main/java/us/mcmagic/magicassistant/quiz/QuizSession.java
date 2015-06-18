package us.mcmagic.magicassistant.quiz;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marc on 6/3/15
 */
public class QuizSession {
    private UUID uuid;
    private HashMap<Integer, Integer> answers = new HashMap<>();
    private int currentQuestion = 0;

    public QuizSession(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void nextQuestion() {
        currentQuestion++;
    }

    public void answerQuestion(Integer id, Integer answer) {
        answers.put(id, answer);
    }
}