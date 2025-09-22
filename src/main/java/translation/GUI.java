package translation;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// TODO Task D: Update the GUI for the program to align with UI shown in the README example.
//            Currently, the program only uses the CanadaTranslator and the user has
//            to manually enter the language code they want to use for the translation.
//            See the examples package for some code snippets that may be useful when updating
//            the GUI.
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            Translator translator = new JSONTranslator();
            CountryCodeConverter countryConv = new CountryCodeConverter();
            LanguageCodeConverter langConv = new LanguageCodeConverter();

            List<String> countryNames = new ArrayList<>();
            for (String c3 : translator.getCountryCodes()) {
                String name = countryConv.fromCountryCode(c3);
                if (name != null) countryNames.add(name);
            }
            Collections.sort(countryNames);

            List<String> languageNames = new ArrayList<>();
            for (String lc : translator.getLanguageCodes()) {
                String lname = langConv.fromLanguageCode(lc);
                if (lname != null) languageNames.add(lname);
            }
            Collections.sort(languageNames);
            /*
            JPanel countryPanel = new JPanel();
            JTextField countryField = new JTextField(10);
            countryField.setText("can");
            countryField.setEditable(false); // we only support the "can" country code for now
            countryPanel.add(new JLabel("Country:"));
            countryPanel.add(countryField);

             */

            JPanel countryPanel = new JPanel();
            countryPanel.add(new JLabel("Country:"));
            JList<String> countryList = new JList<>(countryNames.toArray(new String[0]));
            countryList.setVisibleRowCount(8);
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane countryScroll = new JScrollPane(countryList);
            countryPanel.add(countryScroll);
/*
            JPanel languagePanel = new JPanel();
            JTextField languageField = new JTextField(10);
            languagePanel.add(new JLabel("Language:"));
            languagePanel.add(languageField);

 */

            JPanel languagePanel = new JPanel();
            languagePanel.add(new JLabel("Language:"));
            JComboBox<String> languageBox = new JComboBox<>(languageNames.toArray(new String[0]));
            languagePanel.add(languageBox);

            JPanel buttonPanel = new JPanel();
            JButton submit = new JButton("Submit");
            buttonPanel.add(submit);

            JLabel resultLabelText = new JLabel("Translation:");
            buttonPanel.add(resultLabelText);
            JLabel resultLabel = new JLabel("\t\t\t\t\t\t\t");
            buttonPanel.add(resultLabel);


            // adding listener for when the user clicks the submit button
            submit.addActionListener(e -> {
                String countryName = countryList.getSelectedValue();
                String langName = (String) languageBox.getSelectedItem();

                String result = " ";
                if (countryName != null && langName != null) {
                    String c3 = countryConv.fromCountry(countryName);
                    String lc = langConv.fromLanguage(langName);
                    String translated = translator.translate(c3, lc);
                    result = (translated != null) ? translated : "no translation found!";
                }
                resultLabel.setText(result);
            });

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(countryPanel);
            mainPanel.add(languagePanel);
            mainPanel.add(buttonPanel);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);


        });
    }
}
