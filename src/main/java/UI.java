import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class UI {
    private JButton selectFilesButton;
    private JList list1;
    private JButton startButton;
    private JPanel pane;
    static public JFrame frame;

    public UI() {
        selectFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {//Method called when Select Files button is clicked
                //Select files
                File[] files = selectFiles();
                //Display selected files
                displaySelectedFiles(files);
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {//Method called when Start button is clicked
                Words result = new Words(); //This is the main list of words that will hold the result
                ArrayList<String> fileList = getSelectedFiles(); // List of selected files
                ArrayList<Thread> threadList = new ArrayList<>(); //A pool of Threads for each file
                ArrayList<Words> subLists = new ArrayList<>(); //A pool of sub-lists of words, gotten from each file
                //Ask the user to specify where the result must be saved
                String filePath = getFolderPath();
                //If the user has clicked Cancel button, stop this method
                if (filePath == null)
                    return;
                //For each file, create a thread that will scan for words
                fileList.parallelStream().forEach((s) -> {
                    threadList.add(new Thread(() -> {
                        subLists.add(wordScan(s));
                    }));
                });
                //start these threats at the same time and wait for them to finish creating the sub-lists
                threadList.parallelStream().forEach((t) -> {
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                });
                //unite all sub-lists into a single list
                subLists.stream().forEach((w) -> {
                    result.add(w);
                });
                //Save the result
                try {
                    //Save the words which begin with a-g letters
                    saveToFiles(result, filePath + "/resultAG.txt", "^[a-g]+$");
                    //Save the words which begin with h-n letters
                    saveToFiles(result, filePath + "/resultHN.txt", "^[h-n]+$");
                    //Save the words which begin with o-u letters
                    saveToFiles(result, filePath + "/resultOU.txt", "^[o-u]+$");
                    //Save the words which begin with v-z letters
                    saveToFiles(result, filePath + "/resultVZ.txt", "^[v-z]+$");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    public static void main(String[] args) {
        //Set up the UI
        frame = new JFrame("UI");
        frame.setContentPane(new UI().pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        //Set up the UI for the non-static components
        startButton = new JButton();
        startButton.setEnabled(false);
    }

    public File[] selectFiles() {
        //Method prompts the user to select files and then returns the files the user has selected
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.showOpenDialog(frame);
        return chooser.getSelectedFiles();
    }

    public void displaySelectedFiles(File[] files) {
        //Displays the given files on the UI
        ArrayList<String> tempList = new ArrayList<String>(); // Create a temporal list tempList
        if (files != null)
            if (files.length != 0) {

                for (File f : files) {
                    if (!f.isDirectory()) {
                        tempList.add(f.getAbsolutePath());
                    }
                }
                list1.setListData(tempList.toArray(new String[tempList.size()]));

            }
        if (tempList.isEmpty())
            startButton.setEnabled(false);
        else
            startButton.setEnabled(true);
    }

    public ArrayList<String> getSelectedFiles() {
        //Return a list of file paths, that UI is displaying
        ArrayList<String> tempList = new ArrayList<String>(); // Create a temporal list tempList
        for (int i = 0; i < list1.getModel().getSize(); i++) { // Take every test in available tests list...
            tempList.add(list1.getModel().getElementAt(i).toString()); // ...and add them to tempList
        }
        return tempList;
    }

    public Words wordScan(String s) {
        //Scans the given file paths file for words
        Words temp = new Words();
        try {
            File f = new File(s);
            Scanner input = new Scanner(f);
            while (input.hasNext()) {
                temp.add(input.next().toLowerCase().replaceAll("[^A-Za-z0-9]", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return temp;
        }
    }

    public void saveToFiles(Words ws, String s, String regex) throws IOException {
        //Saves the given list to a given folder path
        //Will save only the words where the first letter matches the given regex
        if (ws.list.isEmpty())
            return;
        File file = new File(s);
        FileWriter fileWriter;
        if (!file.exists()) {
            file.createNewFile();
            fileWriter = new FileWriter(file, true);
        }else{
            fileWriter = new FileWriter(file, false);
        }
        for (WordInFile w : ws.list) {
            if (w.word.substring(0, 1).matches(regex)) {
                fileWriter.write(w.toString() + "\n");//appends the string to the file
                continue;
            }
        }
        fileWriter.close();
    }

    public String getFolderPath() {
        //Prompts the user to specify a folder path and returns it
        String filePath = null;
        while (true) {
            filePath = JOptionPane.showInputDialog("Enter a folder path, where the results will be saved", "C:/temp");
            if (filePath == null)
                return null;
            if (!new File(filePath).exists()) {
                new File(filePath).mkdirs();
            }
            if (!new File(filePath).isDirectory()) {
                JOptionPane.showMessageDialog(pane, filePath + " is not a folder!");
            } else {
                return filePath;
            }
        }
    }
}
