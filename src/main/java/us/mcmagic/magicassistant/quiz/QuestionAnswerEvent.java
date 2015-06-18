package us.mcmagic.magicassistant.quiz;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by Marc on 6/3/15
 */
public class QuestionAnswerEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private Question question;
    private int answer;

    public QuestionAnswerEvent(Player who, Question question, int answer) {
        super(who);
        this.question = question;
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public int getAnswer() {
        return answer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
