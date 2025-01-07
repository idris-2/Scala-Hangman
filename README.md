
# **Scala Hangman Game**

## **Overview**

This project is a text-based implementation of the classic Hangman game written in Scala. The game offers both single-player and multiplayer modes, featuring a save-and-load system, a scoreboard, and cross-platform terminal compatibility. Players can guess letters to uncover a hidden word within a limited number of attempts, earning points for correct guesses and completed words.

---

## **Features**

### **Single-Player Mode**
- Guess letters to uncover a randomly selected word.
- Save and load progress to continue later.
- Track player performance with a scoreboard that displays games played, wins, and total scores.

### **Multiplayer Mode**
- Two players take turns entering words for the other to guess.
- Scores are calculated based on correct guesses and round outcomes.
- Play continues until one player decides to quit.

### **Other Features**
- Cross-platform terminal clearing (Linux/Mac/Windows).
- ASCII art representation of the Hangman figure.
- Robust error handling for invalid inputs and corrupted save files.

---

## **Installation and Setup**

### **Prerequisites**
- **Scala**: Ensure Scala is installed on your system. You can download it from [Scala Downloads](https://www.scala-lang.org/download/).
- **Text Editor/IDE**: Any text editor or IDE that supports Scala (e.g., IntelliJ IDEA, VS Code).

### **Clone the Repository**
```bash
git clone https://github.com/yourusername/scala-hangman.git
cd scala-hangman
```

### **Compile the Code**
```bash
scalac Hangman.Scala
```

### **Run the Game**
```bash
scala HangmanGame
```

---

## **How to Play**

### **Single-Player Mode**
1. Choose the "Start a new game" option.
2. Enter your name (if it already exists in the scoreboard, you'll be prompted to reset or use a new name).
3. Guess letters to uncover the hidden word.
4. Save your progress anytime and resume later using the "Load saved game" option.

### **Multiplayer Mode**
1. Choose the "Multiplayer Mode" option (if implemented separately).
2. Player 1 enters a word for Player 2 to guess.
3. The screen clears, and Player 2 guesses letters until the word is guessed or attempts run out.
4. Players alternate roles until one decides to quit.

---

## **Game Rules**

- **Scoring**:
  - 10 points for each correctly guessed letter.
  - 50 bonus points for guessing the entire word.
  - If the guesser fails, the word giver earns 50 points.

- **Attempts**:
  - Players have 6 attempts to guess the word.
  - Each incorrect guess draws a part of the Hangman figure.

---

## **Files**

- **`Hangman.Scala`**: The main source code for the game.
- **`word_list.txt`**: A text file containing words for the single-player mode.
- **`scoreboard.txt`**: Stores player statistics for the scoreboard.
- **`gamesave.txt`**: Temporarily stores game state for the save-and-load feature.

---

## **Cross-Platform Compatibility**

The game uses:
- **`clear`** command for Linux/Mac.
- **`cls`** command for Windows.

The program automatically detects the OS and uses the appropriate command to clear the terminal.

---

## **Known Issues**

- Multiplayer mode does not integrate with the scoreboard or save/load system, as it is designed to be independent.
- The `word_list.txt` file must exist in the same directory as the program and contain words longer than 5 characters.

---

## **Future Enhancements**

- Add difficulty levels for single-player mode.
- Implement a graphical user interface (GUI) version of the game.
- Enhance multiplayer mode with additional scoring options and tracking.

---

## **Contributing**

Feel free to fork the repository and submit pull requests for any improvements or bug fixes. Contributions are always welcome!

---

## **License**

This project is licensed under the MIT License. See the `LICENSE` file for more details.

---

## **Acknowledgments**

- ASCII art inspired by various online sources.
- Functional programming concepts learned from the book *Functional Programming in Scala*.
