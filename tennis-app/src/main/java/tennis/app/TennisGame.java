package tennis.app;

/**
 * Represents a single game of Tennis.
 * This class tracks the score of two players and determines the game's state.
 */
public class TennisGame {

    private int player1Score;
    private int player2Score;
    // currentServer might be added in a future iteration if needed for serve-specific logic
    // private String currentServer;

    /**
     * Constructs a new TennisGame, initializing scores to Love-All (0-0).
     */
    public TennisGame() {
        this.player1Score = 0;
        this.player2Score = 0;
    }

    /**
     * Awards a point to Player 1.
     */
    public void player1ScoresPoint() {
        player1Score++;
    }

    /**
     * Awards a point to Player 2.
     */
    public void player2ScoresPoint() {
        player2Score++;
    }

    /**
     * Gets the current score in tennis terminology.
     *
     * @return The score string (e.g., "Love-All", "Fifteen-Love", "Deuce", "Advantage Player 1", "Game Player 1").
     */
    public String getScore() {
        if (hasWinner()) {
            return "Game " + leadingPlayerName();
        }

        if (isDeuce()) {
            return "Deuce";
        }

        if (hasAdvantage()) {
            return "Advantage " + leadingPlayerName();
        }

        return formatScore(player1Score) + "-" + (player1Score == player2Score ? "All" : formatScore(player2Score));
    }

    private boolean hasWinner() {
        return (player1Score >= 4 || player2Score >= 4) && Math.abs(player1Score - player2Score) >= 2;
    }

    private boolean isDeuce() {
        return player1Score >= 3 && player1Score == player2Score;
    }

    private boolean hasAdvantage() {
        return (player1Score >= 3 || player2Score >= 3) && Math.abs(player1Score - player2Score) == 1;
    }

    private String leadingPlayerName() {
        if (player1Score > player2Score) {
            return "Player 1";
        } else {
            return "Player 2";
        }
    }

    private String formatScore(int score) {
        switch (score) {
            case 0:
                return "Love";
            case 1:
                return "Fifteen";
            case 2:
                return "Thirty";
            case 3:
                return "Forty";
            default:
                // Should not be reached if game logic is correct (handled by Deuce, Advantage, Game)
                return "";
        }
    }

    // Main method for basic verification as requested (optional)
    public static void main(String[] args) {
        TennisGame game = new TennisGame();
        System.out.println(game.getScore()); // Love-All

        game.player1ScoresPoint();
        System.out.println(game.getScore()); // Fifteen-Love

        game.player2ScoresPoint();
        System.out.println(game.getScore()); // Fifteen-All

        game.player1ScoresPoint();
        System.out.println(game.getScore()); // Thirty-Fifteen

        game.player1ScoresPoint();
        System.out.println(game.getScore()); // Forty-Fifteen

        game.player2ScoresPoint();
        System.out.println(game.getScore()); // Forty-Thirty

        game.player2ScoresPoint();
        System.out.println(game.getScore()); // Deuce

        game.player1ScoresPoint();
        System.out.println(game.getScore()); // Advantage Player 1

        game.player2ScoresPoint();
        System.out.println(game.getScore()); // Deuce

        game.player2ScoresPoint();
        System.out.println(game.getScore()); // Advantage Player 2

        game.player2ScoresPoint();
        System.out.println(game.getScore()); // Game Player 2
    }
}
