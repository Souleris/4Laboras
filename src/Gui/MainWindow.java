package Gui;

import Gui.KsSwing.MyException;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import Lab4Gecys.MyPanel;
import studijosKTU.Ks;

/**
 * @author darius.matulis@ktu.lt
 */
public final class MainWindow extends JFrame implements ActionListener {

    private static final ResourceBundle rb = ResourceBundle.getBundle(Resources.class.getCanonicalName());
    private JMenuBar jMenuBar1;
    private JMenu[] mainMenu;
    private JMenuItem[] jMenuGroup;
    private final MyPanel lab4;

    public MainWindow() {
        lab4 = new MyPanel();
        lab4.initComponents();
    }

    public void initComponents() {
        // Sukuriama meniu juosta
        jMenuBar1 = new JMenuBar();
        // Sukuriami meniu skirsiniai
        int[][] keys = (int[][]) rb.getObject("keys");
        mainMenu = new JMenu[rb.getStringArray("lblMenus").length];
        for (int i = 0; i < rb.getStringArray("lblMenus").length; i++) {
            mainMenu[i] = new JMenu(rb.getStringArray("lblMenus")[i]);
            // Sukuriami submeniu skirsiniai  
            String[][] strMas = ((String[][]) rb.getObject("lblMenuItems"));
            jMenuGroup = new JMenuItem[strMas[i].length];
            for (int j = 0; j < strMas[i].length; j++) {
                mainMenu[i].add(jMenuGroup[j] = new JMenuItem(strMas[i][j]));
                jMenuGroup[j].addActionListener(this);
                if (i == 0 && j == 1) {
                    mainMenu[i].addSeparator();
                }
                if (keys[i][j] != -1) {
                    jMenuGroup[j].setAccelerator(KeyStroke.getKeyStroke(keys[i][j], KeyEvent.CTRL_MASK));
                }
            }
            jMenuBar1.add(mainMenu[i]);
        }
        // Meniu juosta patalpinama šiame freime
        setJMenuBar(jMenuBar1);
        // Šio freimo "viduje" talpinamas lab5 panelis
        Container cp = getContentPane();
        cp.add(lab4);
        // setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            Object command = ae.getSource();
            if (command.equals(mainMenu[0].getMenuComponent(0))) {
                fileChooseMenu();
            }
            if (command.equals(mainMenu[0].getMenuComponent(1))) {
                KsSwing.ounerr(lab4.getTaOutput(), rb.getStringArray("msgs")[0]);
            }
            if (command.equals(mainMenu[0].getMenuComponent(3))) {
                System.exit(0);
            }
            if (command.equals(mainMenu[1].getMenuComponent(0))) {
                JOptionPane op = new JOptionPane(rb.getString("lblAuthor"));
                op.setOptions(new String[]{"OK"});
                JDialog dialog = op.createDialog(((String[][]) rb.getObject("lblMenuItems"))[1][0]);
                dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
                dialog.setLocationRelativeTo(this);
                dialog.setEnabled(true);
                dialog.setVisible(true);
            }
        } catch (MyException e) {
            KsSwing.ounerr(lab4.getTaOutput(), e.getMessage());
        } catch (Exception e) {
            KsSwing.ounerr(lab4.getTaOutput(), rb.getStringArray("msgs")[11]);
            e.printStackTrace(System.out);
        }
    }

    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel( //   UIManager.getCrossPlatformLookAndFeelClassName()
                    // Arba sitaip, tada swing komponentu isvaizda priklausys
                    // nuo naudojamos OS:
                    UIManager.getSystemLookAndFeelClassName()
            // Arba taip:
            //  "com.sun.java.swing.plaf.motif.MotifLookAndFeel"
            // Linux'e dar taip:
            //  "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
            );
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Ks.ou(ex.getMessage());
        }
        MainWindow mainwindow = new MainWindow();
        mainwindow.initComponents();
        mainwindow.setLocation(50, 50);
        mainwindow.setTitle(rb.getString("lblTitle"));
        mainwindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainwindow.setPreferredSize(new Dimension(1000, 650));
        mainwindow.pack();
        mainwindow.setVisible(true);
    }

    private void fileChooseMenu() throws MyException {
        JFileChooser fc = new JFileChooser(".");
        // Nuimamas "all Files" filtras
        // fc.setAcceptAllFileFilterUsed(false);
        // Papildoma mūsų sukurtais filtrais
        fc.addChoosableFileFilter(new MyFilter());
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            lab4.treeGeneration(file.getName());
        }
    }

    /**
     * Privati klasė, aprašanti failų pasirinkimo dialogo filtrus
     */
    private class MyFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            // Rodomos tik direktorijos ir txt failai
            return file.isDirectory() || filename.endsWith(".txt");
        }

        @Override
        public String getDescription() {
            return "*.txt";
        }
    }
}
