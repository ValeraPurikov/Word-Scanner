import java.util.ArrayList;

public class Words {
    ArrayList<WordInFile> list = new ArrayList<>();

    public void add(String s) {
        //Adds a string to a list of words, by either creating a new object or increasing the count of existing one
        if (!contains(s)) {
            list.add(new WordInFile(s));
        } else {
            get(s).count++;
        }
    }

    public void add(WordInFile w) {
        //Adds a object to a list of words or increases the count of existing one
        if (!contains(w.word)) {
            list.add(w);
        } else {
            get(w.word).count = get(w.word).count + w.count;
        }
    }

    public void add(Words words) {
        //Adds a list of words to the list of this current object
        words.list.stream().forEach((w) -> {
            add(w);
        });
    }

    public boolean contains(String s) {
        //Checks if a given String is present in the list of words
        for (WordInFile w : list) {
            if (w.word.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public WordInFile get(String s) {
        //Returns an object with a given String
        for (WordInFile w : list) {
            if (w.word.equals(s)) {
                return w;
            }
        }
        return null;
    }
}
