package flashcards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CardService {
    private List<Card> cards;

    public CardService() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void addCard(String string) {
        String title = string.split(":")[0].trim();
        String definition = string.split(":")[1].trim();
        int stat = Integer.parseInt(string.split(":")[2].trim());
        Card card = getByTitle(title);
        if (card != null) {
            card.setStat(stat);
            card.setDefinition(definition);
        } else {
            cards.add(new Card(title, definition, stat));
        }
    }

    public void remove(Card card) {
        cards.remove(card);
    }

    public String remove(String title) {
        Card card = getByTitle(title);
        if (card != null) {
            remove(card);
        }
        return card == null ? null : card.getCard();
    }

    public boolean containsCardTitle(String title) {
        for (Card card: cards) {
            if (title.equals(card.getCard())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsCardDefinition(String definition) {
        for (Card card: cards) {
            if (definition.equals(card.getDefinition())) {
                return true;
            }
        }
        return false;
    }

    public List<String> listCards() {
        List<String> list = new ArrayList<>();
        for (Card card: cards) {
            list.add(card.getAsString());
        }
        return list;
    }

    public Card getByTitle (String title) {
        for (Card c: cards) {
            if (c.getCard().equals(title)) {
                return c;
            }
        }
        return null;
    }

    public Card getByDefinition (String definition) {
        for (Card c: cards) {
            if (c.getDefinition().equals(definition)) {
                return c;
            }
        }
        return null;
    }

    public Iterator<Card> getIterator() {
        return cards.iterator();
    }

    public List<Card> getHardest() {
        List<Card> hardest = null;
        int max = 0;
        for (Card c: cards) {
            max = Math.max(max, c.getStat());
        }
        if (max > 0) {
            hardest = new ArrayList<>();
            for (Card c: cards) {
                if (max == c.getStat()) {
                    hardest.add(c);
                }
            }
        }
        return hardest;
    }

    public void resetStat() {
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setStat(0);
        }
    }
}
