public class WordInFile {
    public String word;
    public int count;

    WordInFile(String s) {
        word = s;
        count = 1;
    }

    @Override
    public String toString() {
        //returns the objects description in String format
        return word + " - " + count;
    }
}
