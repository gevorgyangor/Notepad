package notepad.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class SearchFrame {

    static FileFolderFilter filter = new FileFolderFilter();
    private JTextField filenameField;
    private JTextField directoryField;
    private JTextArea resultArea;
    private Font font;
    private ArrayList<String> resultList;

    public SearchFrame() {

        JFrame searchFrame = new JFrame();
        JPanel inputPanel = new JPanel();
        GridLayout layout = new GridLayout(1, 2, 15, 15);
        inputPanel.setLayout(layout);
        filenameField = new JTextField();
        directoryField = new JTextField();
        inputPanel.add(new JLabel("File Name"));
        inputPanel.add(filenameField);
        JButton searchButton = new JButton("Search");
        inputPanel.add(new JLabel("Directory"));
        inputPanel.add(directoryField);
        inputPanel.add(searchButton);
        searchFrame.add(inputPanel, BorderLayout.NORTH);
        resultArea = new JTextArea();
        JPanel tFPanel = new JPanel();
        tFPanel.add(resultArea);
        searchFrame.add(tFPanel, BorderLayout.CENTER);
        JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.getViewport().add(resultArea);
        searchFrame.add(scrollPane1, BorderLayout.CENTER);
        resultArea.setEditable(false);
        searchFrame.setLocation(200, 200);
        searchFrame.setSize(600, 400);
        searchFrame.setVisible(true);
        font = new Font("Font.PLAIN", Font.PLAIN, 14);

        resultArea.setFont(font);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search(directoryField.getText(), filenameField.getText());
                printResult();
            }
        });

    }


    public void printResult() {
        StringBuffer buf = new StringBuffer();
        if (resultList.isEmpty()) {
            resultArea.setText("File Not Found");
        } else {
            for (String s : resultList) {
                buf.append(s + "\n");
            }

            resultList = new ArrayList<String>();
            resultArea.setText(buf.toString());
        }
    }

    private void search0(ArrayList<String> result, File searchDIr, String searchmask) {

        File[] listFiles = searchDIr.listFiles(filter);
        if (listFiles == null) return;
        searchmask = searchmask.toLowerCase();
        for (File f : listFiles) {
            if (f.isFile()) {
                String name = f.getName().toLowerCase();
                if (searchmask.startsWith("*") && searchmask.endsWith("*")) {
                    if (name.contains(searchmask.substring(1, searchmask.length() - 1))) {
                        result.add(f.getAbsolutePath());
                    }
                } else if (searchmask.startsWith("*")) {
                    if (name.endsWith(searchmask.substring(1, searchmask.length()))) {
                        result.add(f.getAbsolutePath());
                    }
                } else if (searchmask.endsWith("*")) {
                    if (name.startsWith(searchmask.substring(0, searchmask.length() - 1))) {
                        result.add(f.getAbsolutePath());
                    }
                } else {
                    if (name.contains(searchmask)) {
                        result.add(f.getAbsolutePath());
                    }
                }
            }
        }
        File[] listFolders = searchDIr.listFiles();
        for (File f : listFolders) {
            if (f.isDirectory()) {
                search0(result, f.getAbsoluteFile(), searchmask);
            }
        }

    }

    private ArrayList search(String searchPath, String searchmask) {
        if (searchmask == null || searchPath == null) {
            throw new InvalidParameterException("Failed to find file");
        }
        File searchDir = new File(searchPath);
        if (!searchDir.exists() || searchDir.isFile()) {
            throw new InvalidParameterException("Failed to find a file : Search path is not directory");
        }
        resultList = new ArrayList<String>();

        search0(resultList, searchDir, searchmask);
        return resultList;
    }
}

class FileFolderFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return false;
        } else return true;
    }
}