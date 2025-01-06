import scala.io.{Source, StdIn}
import java.io.{File, PrintWriter}
import scala.util.Random

case class PlayerScore(name: String, gamesPlayed: Int, wins: Int, totalScore: Int)

object HangmanGame {
  // Hangman ASCII art
  val hangmanStages: List[String] = List(
    """
      |-----
      |
      |
      |
      |
    """,
    """
      |-----
      |    O
      |
      |
      |
    """,
    """
      |-----
      |    O
      |    |
      |
      |
    """,
    """
      |-----
      |    O
      |   /|
      |
      |
    """,
    """
      |-----
      |    O
      |   /|\
      |
      |
    """,
    """
      |-----
      |    O
      |   /|\
      |   /
      |
    """,
    """
      |-----
      |    O
      |   /|\
      |   / \
      |
    """
  )

  // Scoreboard file
  val scoreboardFile = "scoreboard.txt"

  def main(args: Array[String]): Unit = {
    println("Welcome to Hangman!")
    mainMenu()
  }

  def mainMenu(): Unit = {
    var running = true
    while (running) {
      println("\nChoose an option:")
      println("1. Start a new game")
      println("2. View scoreboard")
      println("3. Exit")

      StdIn.readLine() match {
        case "1" => startNewGame()
        case "2" => displayScoreboard()
        case "3" =>
          println("Goodbye!")
          running = false
        case _ => println("Invalid option. Try again.")
      }
    }
  }

  def startNewGame(): Unit = {
  val scoreboard = loadScoreboard()

  println("Enter your name:")
  val playerName = StdIn.readLine()

  val existingPlayer = scoreboard.find(_.name == playerName)
  val resetScore = existingPlayer.isDefined && {
    println(s"Warning: The name '$playerName' already exists in the scoreboard.")
    println("If you proceed, your score will be reset to zero.")
    println("Do you want to use a different name? (yes/no)")
    val choice = StdIn.readLine().toLowerCase
    if (choice == "yes") {
      println("Enter a new name:")
      startNewGame() // Restart the name input process
      return
    } else {
      println(s"Proceeding with name '$playerName'. Your score will be reset.")
      true
    }
  }

  var keepPlaying = true
  var currentScore = 0
  var gamesPlayed = 0
  var wins = 0

  while (keepPlaying) {
    val word = getRandomWord("word_list.txt")
    val result = playGame(word, playerName)
    currentScore += result
    gamesPlayed += 1

    if (result > 0) {
      wins += 1
      println(s"Congratulations! You won this round. Current score: $currentScore")
    } else {
      println(s"Game over! Final score: $currentScore")
      keepPlaying = false
    }
  }

  val updatedPlayerScore = if (resetScore) {
    // Reset score for existing player
    PlayerScore(playerName, gamesPlayed = gamesPlayed, wins = wins, totalScore = currentScore)
  } else {
    // Update score for existing or new player
    existingPlayer match {
      case Some(player) =>
        player.copy(
          gamesPlayed = player.gamesPlayed + gamesPlayed,
          wins = player.wins + wins,
          totalScore = player.totalScore + currentScore
        )
      case None =>
        PlayerScore(playerName, gamesPlayed = gamesPlayed, wins = wins, totalScore = currentScore)
    }
  }

  saveScoreboard(scoreboard.filterNot(_.name == playerName) :+ updatedPlayerScore)
} 

  def playGame(word: String, playerName: String): Int = {
    //println(word)
    val wordState = Array.fill(word.length)('_') // Hidden word
    var attemptsLeft = 6
    var guessedLetters = Set.empty[Char]
    var score = 0

    while (attemptsLeft > 0 && wordState.contains('_')) {
      println("\n" + hangmanStages(6 - attemptsLeft))
      println("Current word: " + wordState.mkString(" "))
      println(s"Guessed letters: ${guessedLetters.mkString(", ")}")
      println(s"Attempts left: $attemptsLeft")
      println("Guess a letter:")

      val guess = StdIn.readLine().headOption.getOrElse(' ')
      if (guessedLetters.contains(guess)) {
        println("You already guessed that letter!")
      } else {
        guessedLetters += guess
        if (word.contains(guess)) {
          for (i <- word.indices if word(i) == guess) {
            wordState(i) = guess
            score += 10 // Add points for correct guesses
          }
          println(s"Good guess!")
        } else {
          attemptsLeft -= 1
          println(s"Wrong guess!")
        }
      }
    }

    if (!wordState.contains('_')) {
      println(s"Congratulations, $playerName! You guessed the word: $word")
      score
    } else {
      println(hangmanStages.last)
      println(s"Game over! The word was: $word")
      0
    }
  }

  def getRandomWord(filePath: String): String = {
    try {
      val source = Source.fromFile(filePath)
      val words = source.getLines().map(_.trim).filter(_.length > 5).toList
      source.close()

      if (words.nonEmpty) words(Random.nextInt(words.length))
      else throw new Exception("Word list is empty!")
    } catch {
      case ex: Exception =>
        println(s"Error loading words from file: ${ex.getMessage}")
        "default" // Fallback word
    }
  }

  def loadScoreboard(): List[PlayerScore] = {
    try {
      val source = Source.fromFile(scoreboardFile)
      val scores = source.getLines().map { line =>
        val parts = line.split(",")
        PlayerScore(parts(0), parts(1).toInt, parts(2).toInt, parts(3).toInt)
      }.toList
      source.close()
      scores
    } catch {
      case _: Exception =>
        println("No existing scoreboard found. Starting fresh.")
        List.empty[PlayerScore]
    }
  }

  def saveScoreboard(scoreboard: List[PlayerScore]): Unit = {
    val writer = new PrintWriter(new File(scoreboardFile))
    scoreboard.foreach { player =>
      writer.println(s"${player.name},${player.gamesPlayed},${player.wins},${player.totalScore}")
    }
    writer.close()
  }

  def displayScoreboard(): Unit = {
    val scoreboard = loadScoreboard()
    val sortedScoreboard = scoreboard.sortBy(-_.totalScore) // Sort by totalScore in descending order
    println("\nScoreboard:")
    sortedScoreboard.foreach { player =>
      println(s"${player.name} - Games Played: ${player.gamesPlayed}, Wins: ${player.wins}, Total Score: ${player.totalScore}")
    }
    println()
  }
}
