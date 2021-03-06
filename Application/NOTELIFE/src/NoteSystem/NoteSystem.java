package NoteSystem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class NoteSystem extends JFrame {

    public NoteList noteList;
    public String[] columnNames = {"Title",
        "Description",
        "Date"
    };
    Object[][] data = {

        {"", "", ""}
    };

   public String[] allowedExtensions
          = {"txt"};

    DefaultTableModel dtm;
    JTable selectionTable;
    static String defaultPath = Paths.get("").toAbsolutePath().toString();
    int sortType = 0;
    int editableRow = -2;

    public static NoteSystem MainWindow;

    protected void genGUI() {
        setTitle("The NOTELIFE");
        noteList = new NoteList(defaultPath);
        JFrame frame = new JFrame();
        JPanel contentPanel = (JPanel) frame.getContentPane();
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));

        JLabel title = new JLabel("NOTELIFE");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        titlePanel.add(title);

        JPanel topButtonBar = new JPanel();
        topButtonBar.setLayout(new BoxLayout(topButtonBar, BoxLayout.X_AXIS));

        JLabel sortLabel = new JLabel("Sort by:    ");

        final JComboBox sortBox = new JComboBox() {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                max.width = getPreferredSize().width;
                return max;
            }
        };

        sortBox.addItem("Date - Newest First");
        sortBox.addItem("Date - Oldest First");
        sortBox.addItem("File Size - Largest First");
        sortBox.addItem("File Size - Smallest First");


        JSeparator line = new JSeparator(SwingConstants.VERTICAL) {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                return max;
            }
        };
        line.setPreferredSize(new Dimension(2, 30));

        JSeparator secondLine = new JSeparator(SwingConstants.VERTICAL) {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                max.width = 20;
                return max;
            }
        };
        secondLine.setPreferredSize(new Dimension(2, 30));


        final JTextField searchField = new JTextField("Search") {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                max.width = 100;
                return max;
            }
        };
        searchField.setPreferredSize(new Dimension(180, 25));
        searchField.setForeground(Color.LIGHT_GRAY);
        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                JTextField search = (JTextField) fe.getSource();
                if (search.getText().equals("Search")) {
                    search.setText("");
                    search.setForeground(Color.BLACK);
                }
            }
            

            @Override
            public void focusLost(FocusEvent fe) {
                JTextField search = (JTextField) fe.getSource();
                if(search.getText().equals("")){
                    search.setText("Search");
                    search.setForeground(Color.LIGHT_GRAY);
                }
            }
        });

        searchField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                noteList.searchList(((JTextField) ae.getSource()).getText());
                populateTable();
            }
        });


        final JButton addButton = new JButton("Add Note");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Form_AddNote x = new Form_AddNote(MainWindow);
            }
        });

        final JButton deleteButton = new JButton("Delete Note");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (selectionTable.getSelectedRow() != -1) {
                    Form_DeleteNote form = new Form_DeleteNote(MainWindow);
                    if (form.returnValue) {
                        String noteTitle = noteList.list.get(selectionTable.getSelectedRow()).title;
                        File noteFolder = new File(defaultPath + "/" + noteTitle);
                        System.out.println(deleteDir(noteFolder));
                        noteList.refreshNoteList();
                        populateTable();
                    }
                }
            }
        });
        
        final JButton renameButton = new JButton("Edit Note");
        renameButton.setEnabled(false);
        renameButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (renameButton.getText().equals("Edit Note")) {
                    renameButton.setText("Save");

                    searchField.setEnabled(false);
                    sortBox.setEnabled(false);
                    addButton.setEnabled(false);
                    deleteButton.setEnabled(false);

                    
                    editableRow = selectionTable.getSelectedRow();
                    selectionTable.editCellAt(selectionTable.getSelectedRow(), 0);
                    selectionTable.repaint();
                } else {
                    renameButton.setText("Edit Note");
         
                    searchField.setEnabled(true);
                    sortBox.setEnabled(true);
                    addButton.setEnabled(true);
                    deleteButton.setEnabled(true);

                    
                    selectionTable.editCellAt(0, 2);
                    saveNote(editableRow, (String)selectionTable.getValueAt(editableRow, 0), (String)selectionTable.getValueAt(editableRow, 1));
                    selectionTable.repaint();
                    editableRow = -2;
                }
            }
        });


        topButtonBar.add(sortLabel);
        topButtonBar.add(sortBox);
        topButtonBar.add(Box.createRigidArea(new Dimension(20, 30)));
        topButtonBar.add(line);
        topButtonBar.add(Box.createRigidArea(new Dimension(20, 30)));
        topButtonBar.add(new JLabel("Search:    "));
        topButtonBar.add(searchField);
        topButtonBar.add(Box.createRigidArea(new Dimension(20, 30)));
        topButtonBar.add(secondLine);
        topButtonBar.add(Box.createRigidArea(new Dimension(20, 30)));
        topButtonBar.add(addButton);
        topButtonBar.add(Box.createRigidArea(new Dimension(20, 30)));
        topButtonBar.add(renameButton);
        topButtonBar.add(Box.createRigidArea(new Dimension(20, 30)));
        topButtonBar.add(deleteButton);


        JPanel spacerPanel = new JPanel();
        spacerPanel.setLayout(new BoxLayout(spacerPanel, BoxLayout.X_AXIS));
        JLabel blankLabel = new JLabel(" ");
        spacerPanel.add(blankLabel);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));


        dtm = new DefaultTableModel(data, columnNames) {

        };

        selectionTable = new JTable(dtm) {
            @Override
            public boolean isCellEditable(int row, int col) {
                if ((row == editableRow) && (col == 0 || col == 1)) {
                    return true;
                }
                return false;
            }

            @Override
            public Component prepareEditor(TableCellEditor editor, int row, int col) {
                Component c = super.prepareEditor(editor, row, col);
                c.setBackground(new Color(229, 228, 247));
                return c;
            }

            @Override
            public String getToolTipText(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int column = columnAtPoint(e.getPoint());

                Object value = getValueAt(row, column);
                return value == null ? null : value.toString();
            }
        };
        selectionTable.setDefaultRenderer(Object.class, new CustomTableRenderer(MainWindow));
        selectionTable.setDefaultEditor(Object.class, new CustomTableEditor(new JTextField()));
        selectionTable.setRowHeight(40);
        selectionTable.getTableHeader().setReorderingAllowed(false);
        selectionTable.setRowSelectionAllowed(true);
        selectionTable.setShowVerticalLines(false);
        selectionTable.setShowGrid(false);
        selectionTable.setIntercellSpacing(new Dimension(0, 0));
        selectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionTable.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent ce) {
                //Resize table widths to maintain proportions
                JTable table = (JTable) ce.getSource();
                int totalWidth = table.getWidth();
                table.getColumnModel().getColumn(0).setPreferredWidth(5 * (totalWidth / 16));
                table.getColumnModel().getColumn(1).setPreferredWidth(5 * (totalWidth / 16));
                table.getColumnModel().getColumn(2).setPreferredWidth(3 * (totalWidth / 16));
                table.getColumnModel().getColumn(3).setPreferredWidth(3 * (totalWidth / 16));
            }

            @Override
            public void componentMoved(ComponentEvent ce) {
            }

            @Override
            public void componentShown(ComponentEvent ce) {
            }

            @Override
            public void componentHidden(ComponentEvent ce) {
            }

        });

        selectionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (editableRow != -2) {
                    if (selectionTable.getSelectedRow() != editableRow) {
                        selectionTable.addRowSelectionInterval(editableRow, editableRow);
                    }
                    selectionTable.editCellAt(editableRow, selectionTable.getSelectedColumn());
                }
            }

        });

        selectionTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (editableRow == -2) {
                    if (me.getClickCount() == 2) {
                        Form_ListNoteContents noteContents = new Form_ListNoteContents(MainWindow, selectionTable.getSelectedRow());
                    }

                    if (selectionTable.getSelectedRow() != -1) {
                        deleteButton.setEnabled(true);
                        renameButton.setEnabled(true);

                    } else {
                        deleteButton.setEnabled(false);
                        renameButton.setEnabled(false);

                    }
                }else{
                    selectionTable.editCellAt(editableRow, selectionTable.getSelectedColumn());
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {

            }

            @Override
            public void mouseReleased(MouseEvent me) {

            }

            @Override
            public void mouseEntered(MouseEvent me) {

            }

            @Override
            public void mouseExited(MouseEvent me) {

            }
        });

        JScrollPane tableHolder = new JScrollPane(selectionTable);

        tablePanel.add(tableHolder);


        contentPanel.add(titlePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        contentPanel.add(topButtonBar);
        contentPanel.add(spacerPanel);
        contentPanel.add(tablePanel);

        frame.setSize(800, 600);
        frame.setVisible(true);
        try {
            noteList.refreshNoteList();
        } catch (Exception e) {
            e.printStackTrace();
        }


        sortBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JComboBox x = (JComboBox) ae.getSource();
                sortType = x.getSelectedIndex();
                noteList.sortList(x.getSelectedIndex());
                populateTable();
            }
        });

        noteList.sortList(noteList.SORT_DATE_NEWESTFIRST);

        populateTable();
    }

    public void saveNote(int row, String title, String desc){
        Note newNote = noteList.list.get(row);
        String oldTitle = newNote.title;
        newNote.description = desc;
        newNote.title = title;
        overwriteNote(newNote, oldTitle);
    }
    
    public void overwriteNote(Note newNote, String oldTitle) {
        File noteDirectory = new File(defaultPath + "/"+oldTitle);
        File dataFile = new File(noteDirectory + "/data.info");
        ArrayList<String> info = new ArrayList<>();

        try {
                Scanner infoReader = new Scanner(dataFile);

                while (infoReader.hasNext()) {
                    info.add(infoReader.nextLine());
                }
                infoReader.close();
            } catch (Exception e) {
                e.printStackTrace();
        }
        
        info.set(2, "Title=" + newNote.title);
        info.set(3, "Description=" + newNote.description);

        try{
            PrintStream outStream = new PrintStream(dataFile);
            for(int i = 0; i<info.size(); i++){
                outStream.println(info.get(i));
            }
            outStream.close();
        }catch(Exception e){
        }
        noteDirectory.renameTo(new File(defaultPath + "/" + newNote.title));
    }
    
    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public void populateTable() {
        ArrayList<Note> list = noteList.list;
        ((DefaultTableModel) selectionTable.getModel()).setRowCount(0);
        for (int i = 0; i < list.size(); i++) {
            String[] row = new String[4];
            Note currentNote = list.get(i);

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
            row[0] = currentNote.title;
            row[1] = currentNote.description;
            row[2] = sdf.format(currentNote.date.getTime());
            row[3] = Integer.toString(currentNote.numOfFiles);
            ((DefaultTableModel) selectionTable.getModel()).addRow(new java.util.Vector<String>(java.util.Arrays.asList(row)));

        }
    }

    public void startUpSettingsCheck() throws Exception {
        File settingsFile = new File("settings.info");


        if (!settingsFile.exists()) {
            settingsFile.createNewFile();
            Path currentRelativePath = Paths.get("");
            String currentPath = currentRelativePath.toAbsolutePath().toString();
            PrintStream settingsPrinter = new PrintStream(settingsFile);
            settingsPrinter.println("**Settings.info.**");
            settingsPrinter.println("**This file contains the info about the save path.**");

            settingsPrinter.println("DefaultPath=" + currentPath + "\\save");
            settingsPrinter.close();
        }

        Scanner settingsReader = new Scanner(settingsFile);
        ArrayList<String> settingsList = new ArrayList<>();
        while (settingsReader.hasNext()) {
            settingsList.add(settingsReader.nextLine());
        }

        if (settingsList.size() != 3) {
            System.out.println("FILE CORRUPT - ADD STUFF HERE LATER");
        } else {
            int lineSize = settingsList.get(2).length();
            defaultPath = settingsList.get(2).substring(12, lineSize);
            File defaultFile = new File(defaultPath);
            if (!defaultFile.exists()) {
                boolean success = defaultFile.mkdir();
                if (!success) {
                    System.err.println("ERROR MAKING FOLDER!");
                }
            }

        }

    }
    

    public boolean extensionAllowed(File file) {
        String extension = "";
        int i = file.getPath().lastIndexOf('.');
        if (i > 0) {
            extension = file.getPath().substring(i + 1);
        }

        extension = extension.toLowerCase();
        for (int j = 0; j < allowedExtensions.length; j++) {
            if (extension.equals(allowedExtensions[j])) {
                return true;
            }
        }
        return false;
    }

    public static void copyFile(File from, File to) throws IOException {
        if (!to.exists()) {
            to.createNewFile();
        }
        System.out.println(to.getPath());

        try (
                FileChannel in = new FileInputStream(from).getChannel();
                FileChannel out = new FileOutputStream(to).getChannel()) {
            out.transferFrom(in, 0, in.size());
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run() {
                MainWindow = new NoteSystem();
                try {
                    MainWindow.startUpSettingsCheck();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MainWindow.genGUI();
            }
        });
    }
}
