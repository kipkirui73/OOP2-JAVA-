import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SocialPollApp("Social Commonality Poll");
        });
    }
}

class SocialPollApp extends JFrame {

    private JLabel questionLabel;
    private JButton submitButton;
    private JLabel countLabel;
    private Map<String, Integer> voteCounts;
    private int totalVotes;
    private ButtonGroup choicesGroup;
    private JRadioButton[] radioButtons;
    private String selectedChoice;

    public SocialPollApp(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(600, 250);
        createGUI();
        setVisible(true);
    }

    private void createGUI() {
        questionLabel = new JLabel("Is tribal commonality highly conducive for bonding?");
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(questionLabel);

        String[] choices = {
                "Yes",
                "No",
                "Depends, some people are just socially impotent."};

        choicesGroup = new ButtonGroup();
        radioButtons = new JRadioButton[choices.length];

        for (int i = 0; i < choices.length; i++) {
            radioButtons[i] = new JRadioButton(choices[i]);
            radioButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            choicesGroup.add(radioButtons[i]);
            add(radioButtons[i]);


            radioButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedChoice = ((JRadioButton) e.getSource()).getText();
                }
            });
        }

        voteCounts = new HashMap<>();
        for (String choice : choices) {
            voteCounts.put(choice, 0);
        }

        totalVotes = 0;
        countLabel = new JLabel("Total Votes: 0");
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(countLabel);

        submitButton = new JButton("Submit Vote");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedChoice != null) {
                    voteCounts.put(selectedChoice, voteCounts.get(selectedChoice) + 1);
                    totalVotes++;
                    countLabel.setText("Total Votes: " + totalVotes);


                    choicesGroup.clearSelection();
                    selectedChoice = null;

                    if (totalVotes >= 10) {
                        showResultsFrame();
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(SocialPollApp.this, "Please select an option before voting.");
                }
            }
        });
    }

    private String getSelectedChoice() {
        return selectedChoice;
    }

    private void showResultsFrame() {
        JFrame resultsFrame = new JFrame("Poll Results");
        resultsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        resultsFrame.setLayout(new GridLayout(voteCounts.size(), 2, 10, 10));
        resultsFrame.setSize(600, 200);

        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            String choice = entry.getKey();
            int votes = entry.getValue();
            int percent = 0;
            if (totalVotes > 0) {
                percent = (int) Math.round((votes * 100.0) / totalVotes);
            }

            JLabel choiceLabel = new JLabel(choice + ": " + percent + "% (" + votes + " votes)");

            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue(percent);
            progressBar.setStringPainted(true);

            resultsFrame.add(choiceLabel);
            resultsFrame.add(progressBar);
        }

        resultsFrame.setVisible(true);
    }
}