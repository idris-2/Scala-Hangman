import scala.io.{Source, StdIn}
import scala.util.Random

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

  def main(args: Array[String]): Unit = {
    println("Welcome to Hangman!")
    mainMenu()
  }

  def mainMenu(): Unit = {
    println("Choose an option:")
    println("1. Start a new game")
    println("2. Continue saved game")
    println("3. View scoreboard")
    println("4. Exit")

    StdIn.readLine() match {
      case "1" => startNewGame()
      case "2" => println("Feature not implemented yet.") // Placeholder
      case "3" => println("Feature not implemented yet.") // Placeholder
      case "4" => println("Goodbye!")
      case _   => println("Invalid option. Try again."); mainMenu()
    }
  }

  def startNewGame(): Unit = {
    println("Enter your name:")
    val playerName = StdIn.readLine()
    println(s"Hello, $playerName! Starting a new game...")

    val word = getRandomWord("word_list.txt") // Specify the file path
    playGame(word, playerName)
  }

  def getRandomWord(filePath: String): String = {
    try {
      val source = Source.fromFile(filePath)
      val words = source.getLines().filter(_.length > 5).toList // Only include words longer than 5 letters
      source.close()
      if (words.nonEmpty) words(Random.nextInt(words.length))
      else throw new Exception("Word list is empty!")
    } catch {
      case ex: Exception =>
        println(s"Error loading words from file: ${ex.getMessage}")
        "default" // Fallback word in case of error
    }
  }

  def playGame(word: String, playerName: String): Unit = {
    val wordState = Array.fill(word.length)('_') // Hidden word
    var attemptsLeft = 6
    var guessedLetters = Set.empty[Char]

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
          }
          println(s"Good guess!")
        } else {
          attemptsLeft -= 1
          println(s"Wrong guess!")
        }
      }
    }

    if (!wordState.contains('_')) {
      println(s"Congratulations, $playerName! You guessed the word: ${word}")
    } else {
      println(hangmanStages.last)
      println(s"Game over! The word was: $word")
    }
  }
}