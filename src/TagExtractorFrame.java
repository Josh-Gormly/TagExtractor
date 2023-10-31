import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Scanner;
import java.util.TreeMap;



public class TagExtractorFrame extends JFrame {
    ArrayList<String> stopWords = new ArrayList<>();
    TreeMap<String, Integer> map = new TreeMap<>();
    String[] words;
    boolean halt;
    JPanel mainPnl;
    JPanel chooserPnl;
    JPanel txtAreaPnl;
    JPanel ctrlPnl;
    JButton txtFileBtn;
    JButton stopFileBtn;
    JButton startBtn;
    JButton quitBtn;
    JButton clearBtn;
    JButton saveBtn;

    JFileChooser txtFileChooser;
    JFileChooser stopFileChooser;
    JFileChooser saveTxtFile;
    File txtSelectedFile;
    File stopSelectedFile;
    JTextArea txtArea;
    JScrollPane scroller;
    File workingDirectory = new File(System.getProperty("user.dir"));
    TagExtractorFrame() {
        setTitle("The Tag Extractor");
        mainPnl = new JPanel();
        setLayout(new BorderLayout());
        createTxtAreaPnl();
        setResizable(false);
        mainPnl.add(txtAreaPnl, BorderLayout.CENTER);
        createChooserPnl();
        mainPnl.add(chooserPnl, BorderLayout.WEST);
        createCtrlPnl();
        mainPnl.add(ctrlPnl, BorderLayout.EAST);
        add(mainPnl);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setSize(3 * (screenWidth / 4), 3 * (screenHeight / 4));
        setLocationRelativeTo(null);
    }

    private void createChooserPnl() {
        chooserPnl = new JPanel();
        chooserPnl.setLayout(new GridLayout(3, 1));
        txtFileBtn = new JButton();
        txtFileBtn.setText("Select Text File");
        txtFileBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        txtFileBtn.addActionListener((ActionEvent ae) ->
        {
            txtFileChooser = new JFileChooser();
            txtFileChooser.setCurrentDirectory(workingDirectory);
            txtFileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
            int result = txtFileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                txtSelectedFile = txtFileChooser.getSelectedFile();
                txtFileBtn.setText(txtSelectedFile.getName());
            } else if (result == JFileChooser.CANCEL_OPTION) {
                stopSelectedFile = null;
            }
        });
        stopFileBtn = new JButton();
        stopFileBtn.setText("Select stop file");
        stopFileBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        stopFileBtn.addActionListener((ActionEvent ae) -> {
            stopFileChooser = new JFileChooser();
            stopFileChooser.setCurrentDirectory(workingDirectory);
            stopFileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
            int result = stopFileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                stopSelectedFile = stopFileChooser.getSelectedFile();
                stopFileBtn.setText(stopSelectedFile.getName());
            } else if (result == JFileChooser.CANCEL_OPTION) {
                stopSelectedFile = null;
            }
        });
        startBtn = new JButton();
        startBtn.setText("Start");
        startBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        startBtn.addActionListener((ActionEvent ae) ->
        {
            if (stopSelectedFile == null && txtSelectedFile == null) {
                JOptionPane.showMessageDialog(null, "Select txt and noise file before starting tag extractor", "[Error]", JOptionPane.ERROR_MESSAGE);
            } else if (stopSelectedFile == null) {
                JOptionPane.showMessageDialog(null, "Select noise file before starting tag extractor", "[Error]", JOptionPane.ERROR_MESSAGE);
            } else if (txtSelectedFile == null) {
                JOptionPane.showMessageDialog(null, "Select txt file before starting tag extractor", "[Error]", JOptionPane.ERROR_MESSAGE);
            } else {
                txtArea.append("_______________________________________________\n       File Name: " + txtSelectedFile.getName() + "\n_______________________________________________\n");
                txtArea.append("\n");
                validWords();
                display();
            }
        });
        chooserPnl.add(txtFileBtn);
        chooserPnl.add(stopFileBtn);
        chooserPnl.add(startBtn);
    }

    private void createCtrlPnl() {
        ctrlPnl = new JPanel();
        ctrlPnl.setLayout(new GridLayout(3, 1));
        clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        clearBtn.addActionListener((ActionEvent ae) ->
        {
            txtArea.setText(" ");
            txtSelectedFile = null;
            txtFileBtn.setText("Select Text File");
            stopSelectedFile = null;
            stopFileBtn.setText("Select Tag File");
        });
        quitBtn = new JButton("Quit");
        quitBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        quitBtn.addActionListener((ActionEvent ae) -> System.exit(0));

        saveBtn = new JButton("Save");
        saveBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        saveBtn.addActionListener((ActionEvent ae) ->
        {
            if (txtArea.getText().equals(" ")) {
                JOptionPane.showMessageDialog(null, "File is null. Please start tag extractor", "[Error]", JOptionPane.ERROR_MESSAGE);
            } else {
                saveTxtFile = new JFileChooser();
                saveTxtFile.setCurrentDirectory(workingDirectory);
                saveTxtFile.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
                int result = saveTxtFile.showSaveDialog(null);
                File file = saveTxtFile.getSelectedFile();
                BufferedWriter writer;
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        writer = new BufferedWriter(new FileWriter(file));
                        writer.write(txtArea.getText());
                        writer.close();
                        JOptionPane.showMessageDialog(null, "The File Saved Successfully", "Completed!!", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "The File Couldn't be Saved", "[Error]", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        ctrlPnl.add(saveBtn);
        ctrlPnl.add(clearBtn);
        ctrlPnl.add(quitBtn);
    }

    private void createTxtAreaPnl() {
        txtAreaPnl = new JPanel();
        txtArea = new JTextArea(20, 35);
        txtArea.setEditable(false);
        txtArea.setFont(new Font("Times New Roman", Font.BOLD, 18));
        scroller = new JScrollPane(txtArea);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        txtAreaPnl.add(scroller);
    }

    private void display() {
        for (String key : map.keySet()) {
            if (key.length() > 2) {
                txtArea.append(" Word \"" + key + "\"                           detected    " + map.get(key) + "times!\n");
            }
        }
    }

    private void validWords() {
        Scanner stopWordScanner = null;
        try {
            stopWordScanner = new Scanner(stopSelectedFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        while (true) {
            assert stopWordScanner != null;
            if (!stopWordScanner.hasNextLine()) break;
            String line = stopWordScanner.nextLine();
            stopWords.add(line.toLowerCase());
        }
        Scanner txtFileScanner = null;
        try {
            txtFileScanner = new Scanner(txtSelectedFile);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        while (true) {
            assert txtFileScanner != null;
            if (!txtFileScanner.hasNextLine()) break;
            String line = txtFileScanner.nextLine().toLowerCase();
            words = line.split("[^a-zA-Z]");
            for (String word : words)
            {
                halt = false;
                for (String stop : stopWords)
                {
                    if(word.equals(stop))
                    {
                        halt = true;
                        break;
                    }
                }
                if(!halt)
                {
                    if (!map.containsKey(word))
                    {
                        map.put(word, 1);
                    }
                    else
                    {
                        map.put(word, map.get(word) + 1);
                    }
                }
            }
        }
    }
}