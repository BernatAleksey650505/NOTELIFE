package NoteSystem;

import static NoteSystem.NoteSystem.defaultPath;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class Form_EditNoteProperties extends JDialog {

    NoteSystem mainForm;
    Note note;

    public Form_EditNoteProperties(NoteSystem MainWindow, int selected) {
        super();
        this.mainForm = MainWindow;
        note = mainForm.noteList.list.get(selected);
        genGUI();
    }

    public void genGUI() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(400, 260);
       // setTitle("Edit Note Properties");

        JPanel contentPanel = (JPanel) getContentPane();
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));


        final JLabel selectErrorLabel = new JLabel(" ") {
            {
                setFont(new Font("Arial", Font.PLAIN, 14));
                setForeground(Color.RED);
            }
        };
        final JLabel titleErrorLabel = new JLabel(" ") {
            {
                setFont(new Font("Arial", Font.PLAIN, 14));
                setForeground(Color.RED);
            }
        };
        final JLabel descriptionErrorLabel = new JLabel(" ") {
            {
                setFont(new Font("Arial", Font.PLAIN, 14));
                setForeground(Color.RED);
            }
        };


        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));

        final JLabel titleLabel = new JLabel("Edit Note Properties");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        JPanel noteTitlePanel = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                return max;
            }
        };
        noteTitlePanel.setLayout(new BoxLayout(noteTitlePanel, BoxLayout.X_AXIS));

        final JLabel noteTitleLabel = new JLabel("Title:  ");
        noteTitlePanel.add(noteTitleLabel);

        noteTitlePanel.add(Box.createRigidArea(new Dimension(90, 2)));

        final JTextField noteTitleField = new JTextField(note.title);
        noteTitlePanel.add(noteTitleField);

        JPanel noteDescriptionPanel = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                return max;
            }
        };
        noteDescriptionPanel.setLayout(new BoxLayout(noteDescriptionPanel, BoxLayout.X_AXIS));

        final JLabel noteDescriptionLabel = new JLabel("Description:  ");
        noteDescriptionPanel.add(noteDescriptionLabel);

        noteDescriptionPanel.add(Box.createRigidArea(new Dimension(50, 2)));

        final JTextField noteDescriptionField = new JTextField(note.description);
        noteDescriptionPanel.add(noteDescriptionField);

        JPanel noteTagsPanel = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                return max;
            }
        };

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                boolean error = false;

                if (noteTitleField.getText() == null || noteTitleField.getText().length() == 0) {
                    error = true;
                    titleErrorLabel.setText("Please enter a title.");
                } else if (new File(defaultPath + "/" + noteTitleField.getText()).exists() && !noteTitleField.getText().equals(note.title)) {
                    error = true;
                    titleErrorLabel.setText("A note with this title exists! Please choose a new title!");
                } else {
                    titleErrorLabel.setText(" ");
                }


                if (noteDescriptionField.getText() == null || noteDescriptionField.getText().length() == 0) {
                    error = true;
                    descriptionErrorLabel.setText("Please enter a description.");
                } else {
                    descriptionErrorLabel.setText(" ");
                }

                if (!error) {
                    ArrayList<String> infoContents = new ArrayList<>();
                    File dataFile = new File(defaultPath + "/" + note.title + "/data.info");
                    try (Scanner inputScanner = new Scanner(dataFile)){
                        while (inputScanner.hasNext()) {
                            infoContents.add(inputScanner.nextLine());
                        }
                        inputScanner.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Form_EditNoteProperties.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    infoContents.set(2, "Title=" + noteTitleField.getText());
                    infoContents.set(3, "Description=" + noteDescriptionField.getText());


                    try(PrintStream outStream = new PrintStream(dataFile)){
                        for(int i = 0; i<infoContents.size(); i++){
                            outStream.println(infoContents.get(i));
                        }
                        outStream.close();
                    }catch(FileNotFoundException ex){
                        Logger.getLogger(Form_EditNoteProperties.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    File noteDirectory = new File(defaultPath + "/" + note.title);
                    noteDirectory.renameTo(new File(defaultPath + "/" + noteTitleField.getText()));

                    Form_NoteAdded noteAdded = new Form_NoteAdded(mainForm, "Saved");
                    dispose();
                }
            }

        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                dispose();
            }

        });

        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(30, 30)));
        buttonPanel.add(cancelButton);


        JPanel titleError = new JPanel();
        titleError.setLayout(new BoxLayout(titleError, BoxLayout.X_AXIS));
        titleError.add(titleErrorLabel);
        titleError.add(Box.createHorizontalGlue());

        JPanel descriptionError = new JPanel();
        descriptionError.setLayout(new BoxLayout(descriptionError, BoxLayout.X_AXIS));
        descriptionError.add(descriptionErrorLabel);
        descriptionError.add(Box.createHorizontalGlue());

        contentPanel.add(titlePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        contentPanel.add(noteTitlePanel);
        contentPanel.add(titleError);
        contentPanel.add(noteDescriptionPanel);
        contentPanel.add(descriptionError);

        contentPanel.add(buttonPanel);

        setModal(true);
        setVisible(true);
    }
}
