package flashcards;

public class Card {
    private String card;
    private String definition;

    public Card(String card, String definition) {
        this.card = card;
        this.definition = definition;
    }

    public String getCard() {
        return card;
    }

    public String getDefinition() {
        return definition;
    }

    public boolean checkAnswer(String answer) {
        return answer.equalsIgnoreCase(definition);
    }
}
