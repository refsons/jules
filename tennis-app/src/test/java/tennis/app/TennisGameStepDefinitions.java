package tennis.app;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TennisGameStepDefinitions {

    private TennisGame game;

    @Given("a new tennis game")
    public void a_new_tennis_game() {
        game = new TennisGame();
    }

    @When("player 1 scores a point")
    public void player_1_scores_a_point() {
        game.player1ScoresPoint();
    }

    @When("player 2 scores a point")
    public void player_2_scores_a_point() {
        game.player2ScoresPoint();
    }

    @When("player 1 scores {int} points")
    public void player_1_scores_points(Integer points) {
        for (int i = 0; i < points; i++) {
            game.player1ScoresPoint();
        }
    }

    @When("player 2 scores {int} points")
    public void player_2_scores_points(Integer points) {
        for (int i = 0; i < points; i++) {
            game.player2ScoresPoint();
        }
    }

    @Then("the score should be {string}")
    public void the_score_should_be(String expectedScore) {
        assertEquals(expectedScore, game.getScore());
    }
}
