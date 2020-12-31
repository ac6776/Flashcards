package flashcards;

public class Card {
    private String card;
    private String definition;
    private int stat;

    public Card(String card, String definition) {
        this.card = card;
        this.definition = definition;
        this.stat = 0;
    }

    public Card(String card, String definition, int stat) {
        this.card = card;
        this.definition = definition;
        this.stat = stat;
    }

    public String getCard() {
        return card;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getAsString() {
        return card + " : " + definition + " : " + stat;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }
}
