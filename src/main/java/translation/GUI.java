package translation;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.awt.*;

/**
 * Task D GUI:
 * - Show country names (not alpha3 codes) in a JList.
 * - Keep using alpha3 codes internally via a name→code map.
 * - Use JSONTranslator to retrieve data and translate in real time.
 */
public class GUI {

    private final Translator translator = new JSONTranslator();

    private JList<String> listCountries;
    private JComboBox<String> comboLang;
    private JLabel result;

    // name -> alpha3 code mapping used to convert UI selection back to codes
    private final Map<String, String> nameToCode = new LinkedHashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().createAndShow());
    }

    private void createAndShow() {
        // === Data sources ===
        java.util.List<String> countryCodes = translator.getCountryCodes();     // alpha3 codes
        java.util.List<String> languageCodes = translator.getLanguageCodes();   // iso639-1 codes

        // Build name -> code mapping using English names from JSON (fallback to code)
        nameToCode.clear();
        java.util.List<String> countryNames = new ArrayList<>();
        for (String code: countryCodes) {
            String nameEn = translator.translate(code, "en");
            if (nameEn == null || nameEn.isBlank()) {
                nameEn = code.toUpperCase();
            }
            nameToCode.put(nameEn, code);
            countryNames.add(nameEn);
        }
        Collections.sort(countryNames); // nicer ordering for users

        // === Country list (JList shows names) ===
        listCountries = new JList<>(countryNames.toArray(new String[0]));
        listCountries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCountries.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refresh();
        });

        JScrollPane countryScroll = new JScrollPane(listCountries);
        countryScroll.setPreferredSize(new Dimension(260, 360));
        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.add(new JLabel("Countries:"), BorderLayout.NORTH);
        left.add(countryScroll, BorderLayout.CENTER);

        // === Language dropdown (still shows codes for simplicity) ===
        comboLang = new JComboBox<>(languageCodes.toArray(new String[0]));
        comboLang.addActionListener(e -> refresh());
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRight.add(new JLabel("Language code:"));
        topRight.add(comboLang);

        // === Result area ===
        result = new JLabel("—");
        result.setFont(result.getFont().deriveFont(Font.BOLD, 16f));
        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomRight.add(new JLabel("Translation: "));
        bottomRight.add(result);

        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.add(topRight, BorderLayout.NORTH);
        right.add(bottomRight, BorderLayout.SOUTH);

        // === Frame layout ===
        JFrame frame = new JFrame("Country Name Translator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(12, 12));
        frame.add(left, BorderLayout.WEST);
        frame.add(right, BorderLayout.CENTER);

        // Initial selections: first country & first language
        if (!countryNames.isEmpty()) listCountries.setSelectedIndex(0);
        if (!languageCodes.isEmpty()) comboLang.setSelectedIndex(0);
        refresh();

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /** Update the translation label based on current selections. */
    private void refresh() {
        String selectedName = listCountries.getSelectedValue();
        Object langObj = comboLang.getSelectedItem();
        if (selectedName == null || langObj == null) {
            result.setText("—");
            return;
        }
        String countryCode = nameToCode.get(selectedName);
        String languageCode = langObj.toString();

        String translated = translator.translate(
                countryCode.toLowerCase(), languageCode.toLowerCase());
        result.setText(translated != null ? translated : "(no translation found)");
    }
}
