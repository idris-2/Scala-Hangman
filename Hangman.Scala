import scala.io.{Source, StdIn}
import java.io.{File, PrintWriter}
import scala.util.{Random, Try}
import scala.collection.mutable

case class PlayerScore(name: String, gamesPlayed: Int, wins: Int, totalScore: Int)

object HangmanGame {
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

  val scoreboardFile = "scoreboard.txt"
  val saveFile = "gamesave.txt"

  def main(args: Array[String]): Unit = {
    println("Welcome to Hangman!")
    mainMenu()
  }

  def mainMenu(): Unit = {
    var running = true
    while (running) {
      println("\nChoose an option:")
      println("1. Start a new game")
      println("2. Load saved game")
      println("3. View scoreboard")
      println("4. Exit")

      StdIn.readLine() match {
        case "1" =>
          deleteSaveFile()
          startNewGame()
        case "2" => loadSavedGame()
        case "3" => displayScoreboard()
        case "4" =>
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
        startNewGame()
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
      val result = playGame(word, playerName, Array.fill(word.length)('_'), Set.empty[Char], 6, currentScore, gamesPlayed, wins)
      currentScore += result

      if (result > 0) {
        wins += 1
        println(s"Congratulations! You won this round. Current score: $currentScore")
      } else {
        println(s"Game over! Final score: $currentScore")
        deleteSaveFile()
        keepPlaying = false
      }
    }

    val updatedPlayerScore = if (resetScore) {
      PlayerScore(playerName, gamesPlayed = gamesPlayed, wins = wins, totalScore = currentScore)
    } else {
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

  def playGame(
    word: String,
    playerName: String,
    wordState: Array[Char],
    guessedLetters: Set[Char],
    attemptsLeft: Int,
    currentScore: Int,
    gamesPlayed: Int,
    wins: Int
): Int = {
  println(s"The word is: $word") // Debugging line
  var remainingAttempts = attemptsLeft
  var guessed = guessedLetters
  var score = currentScore

  while (remainingAttempts > 0 && wordState.contains('_')) {
    println("\n" + hangmanStages(6 - remainingAttempts))
    println("Current word: " + wordState.mkString(" "))
    println(s"Guessed letters: ${guessed.mkString(", ")}")
    println(s"Attempts left: $remainingAttempts")
    println("Guess a letter:")

    val guess = StdIn.readLine().headOption.getOrElse(' ')
    if (guess == '|') {
      println("Nice try nerd.")
    } else if (guessed.contains(guess)) {
      println("You already guessed that letter!")
    } else {
      guessed += guess
      if (word.contains(guess)) {
        for (i <- word.indices if word(i) == guess) {
          wordState(i) = guess
          score += 10
        }
        println(s"Good guess!")
      } else {
        remainingAttempts -= 1
        println(s"Wrong guess!")
      }
    }

    saveGame(playerName, word, wordState.mkString, guessed, remainingAttempts, score, gamesPlayed, wins)
  }

  if (!wordState.contains('_')) {
    println(s"Congratulations, $playerName! You guessed the word: $word")
    deleteSaveFile()

    // Update the scoreboard after winning
    val scoreboard = loadScoreboard()
    val existingPlayer = scoreboard.find(_.name == playerName)
    val updatedPlayerScore = existingPlayer match {
      case Some(player) =>
        player.copy(
          gamesPlayed = player.gamesPlayed + 1,
          wins = player.wins + 1,
          totalScore = player.totalScore + score
        )
      case None =>
        PlayerScore(playerName, gamesPlayed = 1, wins = 1, totalScore = score)
    }
    saveScoreboard(scoreboard.filterNot(_.name == playerName) :+ updatedPlayerScore)

    // Prompt for continuation
    println("\nWould you like to play another round? (yes/no)")
    val choice = StdIn.readLine().toLowerCase
    if (choice == "yes" || choice == "y") {
      val newWord = getRandomWord("word_list.txt")
      playGame(newWord, playerName, Array.fill(newWord.length)('_'), Set.empty[Char], 6, score, gamesPlayed + 1, wins + 1)
    } else {
      println("Returning to main menu.")
      0
    }
  } else {
    println(hangmanStages.last)
    println(s"Game over! The word was: $word")
    deleteSaveFile()

    // Update the scoreboard after losing
    val scoreboard = loadScoreboard()
    val existingPlayer = scoreboard.find(_.name == playerName)
    val updatedPlayerScore = existingPlayer match {
      case Some(player) =>
        player.copy(
          gamesPlayed = player.gamesPlayed + 1,
          totalScore = player.totalScore + score
        )
      case None =>
        PlayerScore(playerName, gamesPlayed = 1, wins = wins, totalScore = score)
    }
    saveScoreboard(scoreboard.filterNot(_.name == playerName) :+ updatedPlayerScore)

    println("Returning to main menu.")
    0
  }
}


  def saveGame(playerName: String, word: String, wordState: String, guessedLetters: Set[Char], attemptsLeft: Int, currentScore: Int, gamesPlayed: Int, wins: Int): Unit = {
    val writer = new PrintWriter(new File(saveFile))
    writer.println(s"$playerName|$word|$wordState|${guessedLetters.mkString(",")}|$attemptsLeft|$currentScore|$gamesPlayed|$wins")
    writer.close()
  }

  def loadSavedGame(): Unit = {
  if (!new File(saveFile).exists()) {
    println("No saved game.")
    return
  }

  val saveData = Try(Source.fromFile(saveFile).getLines().next()).getOrElse("")
  if (saveData.isEmpty) {
    println("No saved game.")
    return
  }

  val parts = saveData.split('|')
  if (parts.length != 8) {
    println("Save file is corrupted. Starting a new game.")
    deleteSaveFile()
    return
  }

  val playerName = parts(0)
  val word = parts(1)
  val wordState = parts(2).toCharArray
  val guessedLetters = parts(3).split(",").filter(_.nonEmpty).map(_.head).toSet
  val attemptsLeft = parts(4).toInt
  val currentScore = parts(5).toInt
  val gamesPlayed = parts(6).toInt
  val wins = parts(7).toInt

  println(s"Loading saved game for $playerName...")
  playGame(word, playerName, wordState, guessedLetters, attemptsLeft, currentScore, gamesPlayed, wins)
}


  def deleteSaveFile(): Unit = {
    val file = new File(saveFile)
    if (file.exists()) file.delete()
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
        "default"
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
    val sortedScoreboard = scoreboard.sortBy(-_.totalScore)
    println("\nScoreboard:")
    sortedScoreboard.foreach { player =>
      println(s"${player.name} - Games Played: ${player.gamesPlayed}, Wins: ${player.wins}, Total Score: ${player.totalScore}")
    }
    println()
  }
}
