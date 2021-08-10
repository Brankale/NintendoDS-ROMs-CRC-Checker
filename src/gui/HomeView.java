package gui;

import gui.models.Parameters;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HomeView extends JFrame {

    private final String DAT_DOWNLOAD_PAGE = "https://datomatic.no-intro.org/index.php?page=download&s=64";
    private final String NO_INTRO_NAME_CONVENTION = "https://datomatic.no-intro.org/stuff/The%20Official%20No-Intro%20Convention%20(20071030).pdf";

    private JTextField romFolder;
    private JButton romFolderBtn;
    private JButton datFileBtn;
    private JTextField datFile;
    private JButton checkCrcBtn;
    private JPanel home;
    private JCheckBox fixRomName;
    private JLabel downloadLink;
    private JCheckBox trimRegionAndLanguagesCheckBox;
    private JLabel noIntroNameConventionLabel;
    private JCheckBox hideWarningsCheckBox;

    public HomeView() {

        String html = "<html>(<a href=\"" + DAT_DOWNLOAD_PAGE + "\">Download</a>)</html>";
        downloadLink.setText(html);

        html = "<html>(<a href=\"" + NO_INTRO_NAME_CONVENTION + "\">Find out more</a>)</html>";
        noIntroNameConventionLabel.setText(html);

        romFolderBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                romFolder.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        datFileBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("DAT FILES", "dat");
            chooser.setFileFilter(filter);
            chooser.setAcceptAllFileFilterUsed(false);

            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                datFile.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        downloadLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(DAT_DOWNLOAD_PAGE));
                    } catch (IOException | URISyntaxException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                downloadLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });

        noIntroNameConventionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(NO_INTRO_NAME_CONVENTION));
                    } catch (IOException | URISyntaxException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                noIntroNameConventionLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });

        checkCrcBtn.addActionListener(e -> {
            if (canDoCrcCheck()) {
                File romsDir = new File(romFolder.getText());
                File datFile = new File(this.datFile.getText());

                Parameters params = new Parameters.Builder(romsDir, datFile)
                        .noIntroNameConvention(fixRomName.isSelected())
                        .trimRegion(trimRegionAndLanguagesCheckBox.isSelected())
                        .hideWarnings(hideWarningsCheckBox.isSelected())
                        .build();

                new CrcCheckView(params);
            } else {
                String message = "Select both ROM folder and DAT file";
                JOptionPane.showMessageDialog(this, message);
            }
        });

        setContentPane(home);
        setTitle("NDS ROMs CRC Check");
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        fixRomName.addActionListener(e -> {
            trimRegionAndLanguagesCheckBox.setEnabled(!trimRegionAndLanguagesCheckBox.isEnabled());
        });

        romFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);

                    int returnVal = chooser.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        romFolder.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                }
            }
        });

        datFile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);

                    int returnVal = chooser.showOpenDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        romFolder.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                }
            }
        });
    }

    boolean canDoCrcCheck() {
        return !romFolder.getText().isEmpty() & !datFile.getText().isEmpty();
    }

}
