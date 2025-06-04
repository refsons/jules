package tennis.app;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.session.Session;
import io.micronaut.session.http.HttpSessionFilter;
import io.micronaut.views.ModelAndView;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for the Tennis Game web interface.
 */
@Controller("/game")
public class GameController {

    private static final String SESSION_KEY_GAME = "tennisGame";

    /**
     * Handles GET requests to display the game score.
     * Retrieves the game from session or creates a new one if not found.
     * @param request The HTTP request, used to access the session.
     * @return ModelAndView for the game view, displaying the current score.
     */
    @Get("/")
    public ModelAndView<Map<String, Object>> showGame(HttpRequest<?> request) {
        Session session = request.getAttributes().get(HttpSessionFilter.SESSION_ATTRIBUTE, Session.class)
                .orElseThrow(() -> new IllegalStateException("Session not found in request attributes. Ensure session handling is enabled."));

        TennisGame game = session.get(SESSION_KEY_GAME, TennisGame.class).orElseGet(() -> {
            TennisGame newGame = new TennisGame();
            session.put(SESSION_KEY_GAME, newGame);
            return newGame;
        });
        return new ModelAndView<>("game", Map.of("score", game.getScore()));
    }

    /**
     * Handles POST requests when Player 1 scores a point.
     * Updates the score and redirects to the game view.
     * @param request The HTTP request, used to access the session.
     * @return HttpResponse redirecting to the main game page.
     */
    @Post("/player1-scores")
    public HttpResponse<?> player1Scores(HttpRequest<?> request) {
        Session session = request.getAttributes().get(HttpSessionFilter.SESSION_ATTRIBUTE, Session.class)
                .orElseThrow(() -> new IllegalStateException("Session not found in request attributes."));

        Optional<TennisGame> gameOptional = session.get(SESSION_KEY_GAME, TennisGame.class);
        if (gameOptional.isEmpty()) {
            // Session expired or game not found, redirect to start a new game
            return HttpResponse.seeOther(URI.create("/game/"));
        }

        TennisGame game = gameOptional.get();
        game.player1ScoresPoint();
        session.put(SESSION_KEY_GAME, game); // Store updated game back in session

        return HttpResponse.seeOther(URI.create("/game/"));
    }

    /**
     * Handles POST requests when Player 2 scores a point.
     * Updates the score and redirects to the game view.
     * @param request The HTTP request, used to access the session.
     * @return HttpResponse redirecting to the main game page.
     */
    @Post("/player2-scores")
    public HttpResponse<?> player2Scores(HttpRequest<?> request) {
        Session session = request.getAttributes().get(HttpSessionFilter.SESSION_ATTRIBUTE, Session.class)
                .orElseThrow(() -> new IllegalStateException("Session not found in request attributes."));

        Optional<TennisGame> gameOptional = session.get(SESSION_KEY_GAME, TennisGame.class);
        if (gameOptional.isEmpty()) {
            // Session expired or game not found, redirect to start a new game
            return HttpResponse.seeOther(URI.create("/game/"));
        }

        TennisGame game = gameOptional.get();
        game.player2ScoresPoint();
        session.put(SESSION_KEY_GAME, game); // Store updated game back in session

        return HttpResponse.seeOther(URI.create("/game/"));
    }
}
