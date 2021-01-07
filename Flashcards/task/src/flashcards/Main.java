package flashcards;

import java.io.*;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String MENU_MESSAGE = "\nInput the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n";
//    private static Map<String, String> cards;
    private static CardService service;
    private static List<String> log;
    private static String card;
    private static String definition;
    private static int numberOfQuestions;
//    private static Iterator<Map.Entry<String, String>> it;
    private static Iterator<Card> it;
//    private static Map.Entry<String, String> currentCard;
    private static Card currentCard;
    private static String importFile;
    private static String exportFile;

    public static void main(String[] args) {
        //initial variables
        String input;
        String message;
        InputState state = InputState.INPUT_ACTION;
//        cards = new LinkedHashMap<>();
        service = new CardService();
        log = new LinkedList<>();

        //parsing args
        for (int i = 0; i < args.length; i++) {
            if ("-import".equals(args[i])) {
                importFile = args[i + 1];
            }
            if ("-export".equals(args[i])) {
                exportFile = args[i + 1];
            }
        }

        //import cards if specified
        if (importFile != null) {
            message = readFile(importFile);
            System.out.print(message);
            log.add(message);
        }

        //start main cycle
        message = MENU_MESSAGE;
        while (true) {
            System.out.print(message);
            log.add(message);
            if (message.contains("Bye bye!")) {
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
                            message = "";
                            if (exportFile != null) {
                                message = writeFile(exportFile);
                            }
                            message += "Bye bye!\n";
                            break;
                        case "log":
                            message = "File name:\n";
                            state = InputState.FILENAME_FOR_LOG;
                            break;
                        case "hardest card":
                            List<Card> hardest = service.getHardest();
                            if (hardest == null) {
                                message = "There are no cards with errors.\n";
                            } else {
                                if (hardest.size() > 1) {
                                    String merge = hardest.get(0).getCard();
                                    for (int i = 1; i < hardest.size(); i++) {
                                        merge += "\", \"";
                                        merge += hardest.get(i).getCard();
                                    }
                                    message = String.format("The hardest cards are \"%s\". You have %d errors answering them.\n", merge, hardest.get(0).getStat());
                                } else {
                                    message = String.format("The hardest card is \"%s\". You have %d errors answering it.\n", hardest.get(0).getCard(), hardest.get(0).getStat());
                                }
                            }
                            message += MENU_MESSAGE;
                            state = InputState.INPUT_ACTION;
                            break;
                        case "reset stats":
                            service.resetStat();
                            message = "Card statistics have been reset.\n";
                            message += MENU_MESSAGE;
                            state = InputState.INPUT_ACTION;
                            break;
                        default:
                            message = "Unknown command\n";
                            break;
                    }
                    break;
                case CARD_TITLE:
                    card = input;
//                    if (cards.containsKey(card)) {
//                        message = String.format("The card \"%s\" already exists.\n", card);
//                        message += MENU_MESSAGE;
//                        state = InputState.INPUT_ACTION;
//                        break;
//                    }
                    if (service.containsCardTitle(card)) {
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
//                    if (cards.containsValue(definition)) {
//                        message = String.format("The definition \"%s\" already exists.\n", definition);
//                        message += MENU_MESSAGE;
//                        state = InputState.INPUT_ACTION;
//                        break;
//                    }
                    if (service.containsCardDefinition(definition)) {
                        message = String.format("The definition \"%s\" already exists.\n", definition);
                        message += MENU_MESSAGE;
                        state = InputState.INPUT_ACTION;
                        break;
                    }
//                    cards.put(card, definition);
                    service.addCard(new Card(card, definition));
                    message = String.format("The pair (\"%s\":\"%s\") has been added.\n", card, definition);
                    message += MENU_MESSAGE;
                    state = InputState.INPUT_ACTION;
                    break;
                case CARD_TITLE_TO_REMOVE:
//                    String removed = cards.remove(input);
                    String removed = service.remove(input);
                    message = removed == null ?
                            String.format("Can't remove \"%s\": there is no such card.\n", input) :
                            "The card has been removed.";
                    message += MENU_MESSAGE;
                    state = InputState.INPUT_ACTION;
                    break;
                case ANSWER:
//                    if (input.equals(currentCard.getValue())) {
//                        System.out.println("Correct!");
//                    } else {
//                        if (cards.containsValue(input)) {
//                            for (var el2 : cards.entrySet()) {
//                                if (el2.getValue().equals(input)) {
//                                    System.out.printf("Wrong. The right answer is \"%s\", " +
//                                            "but your definition is correct for \"%s\".\n", currentCard.getValue(), el2.getKey());
//                                }
//                            }
//                        } else {
//                            System.out.printf("Wrong. The right answer is \"%s\".\n", currentCard.getValue());
//                        }
//                    }
                    if (input.equals(currentCard.getDefinition())) {
                        message = "Correct!\n";
                    } else {
                        currentCard.setStat(currentCard.getStat() + 1);
                        Card card = service.getByDefinition(input);
                        if (card != null) {
                            message = String.format("Wrong. The right answer is \"%s\", " +
                                    "but your definition is correct for \"%s\".\n", currentCard.getDefinition(), card.getCard());
                        } else {
                            message = String.format("Wrong. The right answer is \"%s\".\n", currentCard.getDefinition());
                        }
                    }
                    numberOfQuestions--;
                    if (numberOfQuestions == 0) {
                        message += MENU_MESSAGE;
                        state = InputState.INPUT_ACTION;
                        break;
                    }
                    if (!it.hasNext()) {
                        it = service.getIterator();
                    }
                    currentCard = it.next();
                    message += String.format("Print the definition of \"%s\":\n", currentCard.getCard());
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
//                    it = cards.entrySet().iterator();
                    it = service.getIterator();
                    currentCard = it.next();
                    message = String.format("Print the definition of \"%s\":\n", currentCard.getCard());
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
//                cards.put(line.split(":")[0].trim(), line.split(":")[1].trim());
//                System.out.println(line);
                service.addCard(line);
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
//            for (Map.Entry<String, String> line : cards.entrySet()) {
//                bw.write(line.getKey() + " : " + line.getValue() + "\n");
//            }
            int counter = 0;
            for (String s: service.listCards()) {
                bw.write(s + "\n");
                counter++;
            }
            bw.flush();
            message = counter + " cards have been saved.\n";
        } catch (IOException e) {
            message = "error\n";
        }
        return message;
    }
}
