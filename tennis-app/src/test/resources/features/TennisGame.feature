Feature: Tennis Game Score Calculation
  As a player
  I want the game to correctly calculate and report scores
  So that we know the state of the game

  Scenario: Starting a new game
    Given a new tennis game
    Then the score should be "Love-All"

  Scenario: Player 1 scores first point
    Given a new tennis game
    When player 1 scores a point
    Then the score should be "Fifteen-Love"

  Scenario: Player 1 scores two points
    Given a new tennis game
    When player 1 scores 2 points
    Then the score should be "Thirty-Love"

  Scenario: Player 1 scores three points
    Given a new tennis game
    When player 1 scores 3 points
    Then the score should be "Forty-Love"

  Scenario: Player 2 scores first point
    Given a new tennis game
    When player 2 scores a point
    Then the score should be "Love-Fifteen"

  Scenario: Player 2 scores two points
    Given a new tennis game
    When player 2 scores 2 points
    Then the score should be "Love-Thirty"

  Scenario: Player 2 scores three points
    Given a new tennis game
    When player 2 scores 3 points
    Then the score should be "Love-Forty"

  Scenario: Players are tied at 1-1
    Given a new tennis game
    When player 1 scores 1 point
    And player 2 scores 1 point
    Then the score should be "Fifteen-All"

  Scenario: Players are tied at 2-2
    Given a new tennis game
    When player 1 scores 2 points
    And player 2 scores 2 points
    Then the score should be "Thirty-All"

  Scenario: Reaching Deuce
    Given a new tennis game
    When player 1 scores 3 points
    And player 2 scores 3 points
    Then the score should be "Deuce"

  Scenario: Player 1 gains Advantage from Deuce
    Given a new tennis game
    When player 1 scores 3 points
    And player 2 scores 3 points
    And player 1 scores a point
    Then the score should be "Advantage Player 1"

  Scenario: Player 2 gains Advantage from Deuce
    Given a new tennis game
    When player 1 scores 3 points
    And player 2 scores 3 points
    And player 2 scores a point
    Then the score should be "Advantage Player 2"

  Scenario: Player 1 wins game from Deuce
    Given a new tennis game
    When player 1 scores 3 points
    And player 2 scores 3 points
    And player 1 scores a point
    And player 1 scores a point
    Then the score should be "Game Player 1"

  Scenario: Player 2 wins game from Deuce
    Given a new tennis game
    When player 1 scores 3 points
    And player 2 scores 3 points
    And player 2 scores a point
    And player 2 scores a point
    Then the score should be "Game Player 2"

  Scenario: Player 1 wins game directly (Forty-Love to Game)
    Given a new tennis game
    When player 1 scores 3 points
    And player 1 scores a point
    Then the score should be "Game Player 1"

  Scenario: Player 2 wins game directly (Love-Forty to Game)
    Given a new tennis game
    When player 2 scores 3 points
    And player 2 scores a point
    Then the score should be "Game Player 2"

  Scenario: Score goes back to Deuce after Advantage Player 1
    Given a new tennis game
    When player 1 scores 3 points
    And player 2 scores 3 points
    And player 1 scores a point
    And player 2 scores a point
    Then the score should be "Deuce"

  Scenario: Score goes back to Deuce after Advantage Player 2
    Given a new tennis game
    When player 1 scores 3 points
    And player 2 scores 3 points
    And player 2 scores a point
    And player 1 scores a point
    Then the score should be "Deuce"
