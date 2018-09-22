package notepad;


import util.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class Notepad extends JFrame {

    private static final String DEFAULT_NAME = "Notepad";
    private JTextArea textArea;
    private JTextField resultMessageFile;
    private JFileChooser jFileChooser;
    private Font font;
    private notepad.util.BraceChecker braceChecker;
    private File file;
    private NotepadMenuBar notepadMenu;


    public Notepad() {
        super(DEFAULT_NAME);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        notepadMenu = new NotepadMenuBar();
        jFileChooser = new JFileChooser();
        textArea = new JTextArea();
        resultMessageFile = new JTextField();
        font = new Font("Font.PLAIN", Font.PLAIN, 22);
        braceChecker = notepad.util.BraceChecker.getInstance();
        textArea.setFont(font);
        JPanel jPanel = new JPanel();
        GridLayout layout = new GridLayout(1, 1);
        jPanel.setLayout(layout);
        jPanel.add(resultMessageFile);
        add(textArea, BorderLayout.CENTER);
        add(jPanel, BorderLayout.SOUTH);


        setJMenuBar(notepadMenu);
        // Add ActionListener
        notepadMenu.search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearchAction(e);
            }
        });
        notepadMenu.mItemEn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notepadMenu.updateMenusLabels(LanguageType.EN);
            }
        });
        notepadMenu.mItemAm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notepadMenu.updateMenusLabels(LanguageType.AM);
            }
        });
        notepadMenu.mItemRu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notepadMenu.updateMenusLabels(LanguageType.RU);
            }
        });

        notepadMenu.newFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newAction(e);
            }
        });
        notepadMenu.openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAction(e);
            }
        });

        notepadMenu.saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAction(e);
            }
        });

        notepadMenu.savaAsFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savaAsFileAction(e);
            }
        });

        notepadMenu.exitFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExitAction();
            }
        });
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocation(100, 100);
        setVisible(true);
        resultMessageFile.setFont(font);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleDocumentUpdate();
            }

            
            @Override
            public void removeUpdate(DocumentEvent e) {
                handleDocumentUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleDocumentUpdate();
            }
        });
    }

    private void handleSearchAction(ActionEvent e) {
        notepad.util.SearchFrame searchFrame = new notepad.util.SearchFrame();
    }


    void handleDocumentUpdate() {
        if (!braceChecker.parse(textArea.getText())) {
            resultMessageFile.setForeground(Color.red);
            resultMessageFile.setText(braceChecker.getMessage());
        } else {
            resultMessageFile.setForeground(Color.darkGray);
            resultMessageFile.setText("No Error");
        }
    }

    private void handleExitAction() {
        if (isChanged() && !handleSaveActionConfirm(ActionType.EXIT)) {
            return;
        }
        exit();
    }

    private boolean handleSaveActionConfirm(ActionType actionType) {
        switch (askSave()) {
            case JOptionPane.CANCEL_OPTION:
                return false;
            case JOptionPane.YES_OPTION:
                if ((ActionType.SAVE_AS == actionType) || isNewMode()) {
                    saveAs();
                } else {
                    save();
                }
        }

        return true;
    }

    private void savaAsFileAction(ActionEvent e) {
        saveAs();
    }

    private void saveAction(ActionEvent e) {
        save();
    }


    private void openAction(ActionEvent e) {
        if (isChanged()) {
            if (!handleSaveActionConfirm(ActionType.OPEN)) {
                return;
            }
        }
        open();
    }

    private void newAction(ActionEvent e) {
        if (isChanged() && !handleSaveActionConfirm(ActionType.NEW)) {
            return;
        }
        newFile();
    }

    public void exit() {
        System.exit(0);
    }

    public void save() {
        if (file != null) {
            write(file, textArea.getText());
        } else {
            saveAs();
        }
    }

    public void saveAs() {
        int choice = jFileChooser.showSaveDialog(textArea);
        if (choice == JFileChooser.APPROVE_OPTION) {
            file = jFileChooser.getSelectedFile();
            write(jFileChooser.getSelectedFile());
            setTitle(file.getName());
        }
    }

    public void newFile() {
        setTitle(DEFAULT_NAME);
        textArea.setText("");
        file = null;
    }

    public void open() {
        if (jFileChooser.showOpenDialog(jFileChooser) == JFileChooser.APPROVE_OPTION) {
            file = jFileChooser.getSelectedFile();
            textArea.setText(read(file));
            setTitle(file.getName());
        }
    }

    public void write(File file) {
        write(file, textArea.getText());
    }

    public void write(File file, String text) {
        String path;
        if (!file.getName().contains(".txt")) {
            path = file.getAbsolutePath() + ".txt";
        } else {
            path = file.getAbsolutePath();
        }
        try (FileOutputStream outputStream = new FileOutputStream(path)) {
            outputStream.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read(File file) {
        byte[] b = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(b);
    }

    private boolean isLoadedTextChenged(File file) {
        if (file == null) {
            return false;
        }
        return !textArea.getText().equals(read(file));
    }

    private int askSave() {
        int returnVal = JOptionPane.showConfirmDialog(null, "Do you want save file");
        return returnVal;
    }

    private boolean isChanged() {
        if (isNewMode() && textArea.getText().length() > 0) {
            return true;
        } else if (!isNewMode() && textArea.getText().length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isNewMode() {
        return file == null;
    }


    public static void main(String[] args) {
        Notepad n = new Notepad();
    }
}

