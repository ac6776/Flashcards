package flashcards;

import java.io.*;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String MENU_MESSAGE = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n";
    private static Map<String, String> cards;
    private static List<String> log;
    private static String card;
    private static String definition;
    private static int numberOfQuestions;
    private static Iterator<Map.Entry<String, String>> it;
    private static Map.Entry<String, String> currentCard;

    public static void main(String[] args) {
        String input;

        String message = MENU_MESSAGE;
        InputState state = InputState.INPUT_ACTION;
        cards = new LinkedHashMap<>();
        log = new LinkedList<>();

        while (true) {
            System.out.print(message);
            log.add(message);
            if ("Bye bye!".equals(message)) {
                return;
            }

            input = scanner.nextLine();
            log.add(input + "\n");

            switch (state) {
                case INPUT_ACTION:
                    switch (input) {
                        case "add":
                            message = "The card:\n";
                            state = InputState.CARD_TITLE;
                            break;
                        case "remove":
                            message = "Which card?\n";
                            state = InputState.CARD_TITLE_TO_REMOVE;
                            break;
                        case "import":
                            message = "File name:\n";
                            state = InputState.FILENAME_FOR_IMPORT;
                            break;
                        case "export":
                            message = "File name:\n";
                            state = InputState.FILENAME_FOR_EXPORT;
                            break;
                        case "ask":
                            message = "How many times to ask?\n";
                            state = InputState.NUMBER_OF_QUESTIONS;
                            break;
                        case "exit":
                            message = "Bye bye!\n";
                            break;
                        case "log":
                            message = "File name:\n";
                            state = InputState.FILENAME_FOR_LOG;
                            break;
                        default:
                            message = "Unknown command\n";
                            break;
                    }
                    break;
                case CARD_TITLE:
                    card = input;
                    if (cards.containsKey(card)) {
                        message = String.format("The card \"%s\" already exists.\n", card);
                        message += MENU_MESSAGE;
                        state = InputState.INPUT_ACTION;
                        break;
                    }
                    message = "The definition of the card:\n";
                    state = InputState.CARD_DEFINITION;
                    break;
                case CARD_DEFINITION:
                    definition = input;
                    if (cards.containsValue(definition)) {
                        message = String.format("The definition \"%s\" already exists.\n", definition);
                        message += MENU_MESSAGE;
                        state = InputState.INPUT_ACTION;
                        break;
                    }
                    cards.put(card, definition);
                    message = String.format("The pair (\"%s\":\"%s\") has been added.\n", card, definition);
                    message += MENU_MESSAGE;
                    state = InputState.INPUT_ACTION;
                    break;
                case CARD_TITLE_TO_REMOVE:
                    String removed = cards.remove(input);
                    message = removed == null ?
                            String.format("Can't remove \"%s\": there is no such card.\n", input) :
                            "The card has been removed.";
                    message += MENU_MESSAGE;
                    state = InputState.INPUT_ACTION;
                    break;
                case ANSWER:
                    if (input.equals(currentCard.getValue())) {
                        System.out.println("Correct!");
                    } else {
                        if (cards.containsValue(input)) {
                            for (var el2 : cards.entrySet()) {
                                if (el2.getValue().equals(input)) {
                                    System.out.printf("Wrong. The right answer is \"%s\", " +
                                            "but your definition is correct for \"%s\".\n", currentCard.getValue(), el2.getKey());
                                }
                            }
                        } else {
                            System.out.printf("Wrong. The right answer is \"%s\".\n", currentCard.getValue());
                        }
                    }
                    numberOfQuestions--;
                    if (numberOfQuestions == 0) {
                        message = MENU_MESSAGE;
                        state = InputState.INPUT_ACTION;
                        break;
                    }
                    if (!it.hasNext()) {
                        it = cards.entrySet().iterator();
                    }
                    currentCard = it.next();
                    message = String.format("Print the definition of \"%s\":", currentCard.getKey());
                    break;
                case FILENAME_FOR_EXPORT:
                    message = writeFile(input);
                    message += MENU_MESSAGE;
                    state = InputState.INPUT_ACTION;
                    break;
                case FILENAME_FOR_IMPORT:
                    message = readFile(input);
                    message += MENU_MESSAGE;
                    state = InputState.INPUT_ACTION;
                    break;
                case NUMBER_OF_QUESTIONS:
                    numberOfQuestions = Integer.parseInt(input);
                    it = cards.entrySet().iterator();
                    currentCard = it.next();
                    message = String.format("Print the definition of \"%s\":", currentCard.getKey());
                    state = InputState.ANSWER;
                    break;
                case FILENAME_FOR_LOG:
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(input))) {
                        for (String line: log) {
                            bw.write(line);
                        }
                        bw.flush();
                        message = "The log has been saved.\n";
                    } catch (IOException e) {
                        message = "error\n";
                    }
                    message += MENU_MESSAGE;
                    state = InputState.INPUT_ACTION;
                    break;
            }
        }
    }

    enum InputState {
        INPUT_ACTION,
        CARD_TITLE,
        CARD_DEFINITION,
        CARD_TITLE_TO_REMOVE,
        ANSWER,
        NUMBER_OF_QUESTIONS,
        FILENAME_FOR_EXPORT,
        FILENAME_FOR_IMPORT,
        FILENAME_FOR_LOG
    }

    static String readFile (String file) {
        String message;
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            int counter = 0;
            while ((line = br.readLine()) != null) {
                cards.put(line.split(":")[0].trim(), line.split(":")[1].trim());
                System.out.println(line);
                counter++;
            }
            message = counter + " cards have been loaded.\n";
        } catch (IOException e) {
            message = "File not found.\n";
        }
        return message;
    }

    static String writeFile (String file) {
        String message;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, String> line : cards.entrySet()) {
                bw.write(line.getKey() + " : " + line.getValue() + "\n");
            }
            bw.flush();
            message = cards.size() + " cards have been saved.\n";
        } catch (IOException e) {
            message = "error\n";
        }
        return message;
    }
}
