package us.mcmagic.magicassistant.quiz;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Marc on 6/3/15
 */
public class QuizManager implements Listener {
    private List<Question> questions = new ArrayList<>();
    private HashMap<UUID, QuizSession> sessions = new HashMap<>();

    public QuizManager() {
        initialize();
    }

    public void initialize() {
        questions.clear();
        questions.add(new Question("Have you been to Walt Disney World?", Arrays.asList("Yes", "No")));
        questions.add(new Question("What gender are you?", Arrays.asList("Boy", "Girl", "I don't want to answer")));
        questions.add(new Question("What age group are you in?", Arrays.asList("8 and under", "9-11", "12-14",
                "15 and above", "I don't want to answer")));
        questions.add(new Question("What is your favorite park in Walt Disney World?", Arrays.asList("Magic Kingdom",
                "Epcot", "Hollywood Studios", "Animal Kingdom", "I can't pick!")));
        questions.add(new Question("Have you ever been on a Disney Cruise Line ship?", Arrays.asList("Yes!", "No!",
                "I really want to, does that count?")));
        questions.add(new Question("Which Disney/Pixar movie is your favorite?", Arrays.asList("Monsters, Inc", "Cars",
                "The Incredibles", "Wall E")));
        questions.add(new Question("Which Disney Animation movie is your favorite?", Arrays.asList("Big Hero 6", "Frozen",
                "Tangled", "The Lion King")));
        questions.add(new Question("Was it hard to pick your favorite movies?", Arrays.asList("Yes!", "Is that even a question?",
                "I picked randomly!")));
        questions.add(new Question("Where did you hear about MCMagic?", Arrays.asList("From a Friend", "YouTube",
                "A Blog/Post about MCMagic", "Other")));
        questions.add(new Question("Would you download our Resource Pack?", Arrays.asList("Yes", "No")));
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/qzans ")) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        String msg = event.getMessage();
        String[] list = msg.split(" ");
        Integer qid = Integer.parseInt(list[1]);
        Integer aid = Integer.parseInt(list[2]);
        QuizSession session = getSession(player);
        if (session == null) {
            return;
        }
        if (qid != session.getCurrentQuestion()) {
            return;
        }
        session.answerQuestion(qid, aid);
        messageQuestion(player, qid + 1);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
    }

    private void messageQuestion(Player player, int qid) {
        QuizSession session = getSession(player);
        if (session == null) {
            return;
        }
        if (qid == 10) {
            finished(player);
            return;
        }
        Question question = questions.get(qid - 1);
        if (question == null) {
            return;
        }
        session.nextQuestion();
        HashMap<Integer, String> choices = question.getChoices();
        FormattedMessage msg = new FormattedMessage("-----------------------------------------------------\n")
                .color(ChatColor.YELLOW).then("\n\n\n\n\nQuestion " + session.getCurrentQuestion() + ": ")
                .color(ChatColor.YELLOW).then(question.getText() + "\n").color(ChatColor.GREEN).then("\n");
        int amount = 0;
        for (Map.Entry<Integer, String> entry : new HashSet<>(choices.entrySet())) {
            msg.then("- " + entry.getValue() + "\n").color(ChatColor.GREEN).command("/qzans " + qid + " " + (amount + 1))
                    .tooltip(ChatColor.GREEN + "Click to choose choice #" + (amount + 1));
            amount++;
        }
        for (int i = 0; i < (7 - amount); i++) {
            msg.then("\n");
        }
        msg.send(player);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
    }

    private void finished(Player player) {
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Thank you for taking our Quiz!");
        QuizSession session = getSession(player);
        if (session == null) {
            return;
        }
        String quiz = "";
        try (Connection connection = MCMagicCore.permSqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET quiz=? WHERE uuid=?");
            sql.setString(1, "");
            sql.setString(2, player.getUniqueId().toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private QuizSession getSession(Player player) {
        return sessions.get(player.getUniqueId());
    }

    private void answerQuestion(Player player, int question, int answer) {
        Question q = questions.get(question - 1);
        if (q == null) {
            return;
        }
        String choice = q.getChoice(answer);
        QuestionAnswerEvent event = new QuestionAnswerEvent(player, q, answer);
    }

    public void start(Player player) {
        QuizSession session = new QuizSession(player.getUniqueId());
        sessions.put(player.getUniqueId(), session);
        messageQuestion(player, 1);
    }
}