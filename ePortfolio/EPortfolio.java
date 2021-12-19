/**
 * @author Tyler Belluomini
 * @since 2021-11-02
 * Program allows user to manage a portfolio. Can buy/sell. update prices and search for investments
 * in portfolio
 */
package ePortfolio;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class EPortfolio extends JFrame{
    public static final int HEIGHT = 450;
    public static final int WIDTH = 600;

    private JPanel defaultMenu;
    private JPanel buy;
    private JPanel sell;
    private JPanel update;
    private JPanel gains;
    private JPanel search;

    private static ArrayList<Investment> investments = new ArrayList<Investment>();
    private static String fileName;
    private static double soldGains = 0;
    private static int curIndex = 0;

    //When buy menu is selected, makes buy visible and everything else not visible
    private class BuyListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            defaultMenu.setVisible(false);
            buy.setVisible(true);
            sell.setVisible(false);
            update.setVisible(false);
            gains.setVisible(false);
            search.setVisible(false);

        }
    }

    //When sell menu is selected, makes buy visible and everything else not visible
    private class SellListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            defaultMenu.setVisible(false);
            buy.setVisible(false);
            sell.setVisible(true);
            update.setVisible(false);
            gains.setVisible(false);
            search.setVisible(false);
        }
    }

    //When update menu is selected, makes buy visible and everything else not visible
    private class UpdateListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            defaultMenu.setVisible(false);
            buy.setVisible(false);
            sell.setVisible(false);
            update.setVisible(true);
            gains.setVisible(false);
            search.setVisible(false);
        }
    }

    //When gains menu is selected, makes buy visible and everything else not visible
    private class GainsListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            defaultMenu.setVisible(false);
            buy.setVisible(false);
            sell.setVisible(false);
            update.setVisible(false);
            gains.setVisible(true);
            search.setVisible(false);

            //Adds all gains and prints in textfield
            double gainsNum = 0;
            for (int i = 0; i < investments.size(); i++) {
                gainsNum += investments.get(i).getBookValue();
            }
            gainsNum += soldGains;
            Component[] components = gains.getComponents();
            Component component = null;
            for (int i = 0; i < components.length; i++) {
                component = components[i];
                if (component instanceof JTextField) {
                    ((JTextField)component).setText(String.format("%.2f", gainsNum));
                }
            }

            //Prints individual gains
            for (int i = 0; i < components.length; i++) {
                component = components[i];
                if (component instanceof JScrollPane) {
                    JViewport viewPort = ((JScrollPane)component).getViewport();
                    JTextArea textArea = (JTextArea)viewPort.getView();
                    textArea.setText("");
                    for (int j = 0; j < investments.size(); j++) {
                        textArea.append(investments.get(j).toString());
                    }
                }
            }
        } 
    }

    //When search menu is selected, makes buy visible and everything else not visible
    private class SearchListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            defaultMenu.setVisible(false);
            buy.setVisible(false);
            sell.setVisible(false);
            update.setVisible(false);
            gains.setVisible(false);
            search.setVisible(true);
        }
    }

    //Reads in investments, creates gui
    public static void main(String args[]) {
        readInvestments(args[0]);
        fileName = args[0];
        EPortfolio gui = new EPortfolio();
        gui.setVisible(true);
    }

    //Constructor for gui
    public EPortfolio() {
        super("EPortfolio Manager");
        setSize(WIDTH, HEIGHT);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        //Writes investments to disk when exiting
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                writeInvestments(fileName);
                System.exit(0);
            }
        });

        //Adds all menus
        defaultMenu = new JPanel();
        defaultMenu.setVisible(true);
        JLabel defaultMessage = new JLabel("<html>Welcome to ePortfolio.<br><br><br>" 
                            + "Choose a command from the “Commands” menu to buy or sell<br>an investment,"
                            +" update prices for all investments, get gain for the<br>portfolio, search "
                            +"for relevant investments, or quit the program.</html>");
        //defaultMessage.setFont(defaultMessage.getFont().deriveFont());
        defaultMessage.setHorizontalAlignment(JLabel.LEFT);
        defaultMenu.add(defaultMessage);
        add(defaultMenu);

        add(getBuyPanel());
        add(getSellPanel());
        add(getUpdatePanel());
        add(getGainPanel());
        add(getSearchPanel());

        JMenu stockMenu = new JMenu("Commands");

        //Adds listener to JMenu and adds JMenu
        JMenuItem buyStock = new JMenuItem("Buy");
        buyStock.addActionListener(new BuyListener());
        stockMenu.add(buyStock);

        JMenuItem sellStock = new JMenuItem("Sell");
        sellStock.addActionListener(new SellListener());
        stockMenu.add(sellStock);

        JMenuItem update = new JMenuItem("Update Stocks");
        update.addActionListener(new UpdateListener());
        stockMenu.add(update);

        JMenuItem gains = new JMenuItem("Get Gains");
        gains.addActionListener(new GainsListener());
        stockMenu.add(gains);

        JMenuItem search = new JMenuItem("Search");
        search.addActionListener(new SearchListener());
        stockMenu.add(search);

        JMenuBar bar = new JMenuBar();
        bar.add(stockMenu);
        setJMenuBar(bar);
    }
    
    //Creates and formats the buy panel. Includes button responses to buy investments
    public JPanel getBuyPanel() {
        buy = new JPanel();
        buy.setVisible(false);
        buy.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;

        JLabel buyInstruct = new JLabel("Buying an investment");
        JLabel[] info = new JLabel[5];
        info[0] = new JLabel("Type", SwingConstants.LEFT);
        info[1] = new JLabel("Symbol", SwingConstants.LEFT);
        info[2] = new JLabel("Name", SwingConstants.LEFT);
        info[3] = new JLabel("Quantity", SwingConstants.LEFT);
        info[4] = new JLabel("Price", SwingConstants.LEFT);
        String[] types = {"Stock", "MutualFund"};
        JComboBox<String> type = new JComboBox<String>(types);
        JTextField[] fields = new JTextField[4];
        fields[0] = new JTextField("", 10);
        fields[1] = new JTextField("", 30);
        fields[2] = new JTextField("", 6);
        fields[3] = new JTextField("", 6);
        JButton buyButton = new JButton("Buy");
        buyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Tries to buy a stock
                Component[] components = buy.getComponents();
                ArrayList<JTextField> stockInfo = new ArrayList<JTextField>();
                JComboBox<?> typeBox;
                JTextArea messageArea;
                int type = 0, quantity = 0;
                String symbol, name;
                double price = 0;

                for (int i = 0; i < components.length; i++) {
                    if (components[i] instanceof JTextField) {
                        stockInfo.add(((JTextField)components[i]));
                    }
                    else if (components[i] instanceof JComboBox<?>) {
                        typeBox = (JComboBox<?>)components[i];
                        type = typeBox.getSelectedIndex();
                    }
                }
                JViewport viewport = ((JScrollPane)components[14]).getViewport();
                messageArea = (JTextArea)viewport.getView();
                messageArea.setText("");

                symbol = stockInfo.get(0).getText();
                name = stockInfo.get(1).getText();
                try {
                    quantity = Integer.parseInt(stockInfo.get(2).getText());
                        
                    if (quantity <= 0) {
                         messageArea.setText("Error. Quantity must be a positive number.");
                    }
                    else {
                        try {
                            price = Double.parseDouble(stockInfo.get(3).getText());

                            if (price <= 0) {
                                messageArea.setText("Error. Price must be a positive number");
                            }
                            else {
                                if (type == 0) {
                                    int index = getIndex(symbol, investments);

                                    //Stock does not exist, buying new
                                    if (index == -1) {
                                        Stock stock = new Stock(symbol, name, quantity, price);
                                        investments.add(stock);
                                        messageArea.append("Purchased " + quantity + " shares of  " + name + "(" + symbol
                                        + ") at $" + price + ".\n");
                                    }
                                    else {
                                        investments.get(index).updateQuantity(price, quantity);
                                        messageArea.append("Purchased " + quantity + " additional shares of  " + name + "(" 
                                        + symbol + ") at $" + price + ".\n");
                                    }
                                }
                                else {
                                    int index = getIndex(symbol, investments);

                                    //Mutualfund does not exist, buying new
                                    if (index == -1) {
                                        MutualFund fund = new MutualFund(symbol, name, quantity, price);
                                        investments.add(fund);
                                        messageArea.append("Purchased " + quantity + " shares of  " + name + "(" + symbol
                                        + ") at $" + price + ".\n");
                                    }
                                    else {
                                        investments.get(index).updateQuantity(price, quantity);
                                        messageArea.append("Purchased " + quantity + " additional shares of  " + name + "(" 
                                        + symbol + ") at $" + price + ".\n");
                                    }
                                }

                                for (int i = 0; i < stockInfo.size(); i++) {
                                    stockInfo.get(i).setText("");
                                }
                            }
                        } catch (Exception ex1) {
                            messageArea.append("Error. Price must be a number\n");
                        }
                    }
                } catch (Exception ex) {
                    messageArea.append("Error. Quantity must be a whole number\n");
                }
            }
        });
        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setText("");
                }
            }
        });
        JLabel message = new JLabel("Messages", SwingConstants.LEFT);
        JTextArea messageArea = new JTextArea(5, 30);
        messageArea.setEditable(false);
        JScrollPane messageScroll = new JScrollPane(messageArea);

        c.insets = new Insets(10, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridwidth = 2;
        buy.add(buyInstruct, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 20, 0, 0);
        buy.add(info[0], c);
        c.gridx = 1;
        c.gridy = 1;
        buy.add(type, c);
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        buy.add(reset, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        buy.add(info[1], c);
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 75;
        buy.add(fields[0], c);
        c.gridx = 0;
        c.gridy = 3;
        c.ipadx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        buy.add(info[2], c);
        c.gridx = 1;
        c.gridy = 3;
        c.ipadx = 150;
        buy.add(fields[1], c);
        c.gridx = 0;
        c.gridy = 4;
        c.ipadx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        buy.add(info[3], c);
        c.gridx = 1;
        c.gridy = 4;
        c.ipadx = 60;
        buy.add(fields[2], c);
        c.gridx = 2;
        c.gridy = 4;
        c.weightx = 1.0;
        c.ipadx = 15;
        c.anchor = GridBagConstraints.CENTER;
        buy.add(buyButton, c);
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0;
        c.ipadx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        buy.add(info[4], c);
        c.gridx = 1;
        c.gridy = 5;
        c.ipadx = 60;
        buy.add(fields[3], c);
        c.gridx = 0;
        c.gridy = 6;
        c.ipadx = 0;
        c.insets = new Insets(20, 10, 0, 0);
        buy.add(message, c);
        c.gridy = 7;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 0, 0);
        c.gridwidth = 4;
        c.weighty = 0.5;
        buy.add(messageScroll, c);

        return buy;
    }

    //Creates and formats sell panel. Includes button responses to sell investments
    public JPanel getSellPanel() {
        sell = new JPanel();
        sell.setVisible(false);
        sell.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;

        JLabel sellInstruct = new JLabel("Selling an investment");
        JLabel[] info = new JLabel[3];
        info[0] = new JLabel("Symbol", SwingConstants.LEFT);
        info[1] = new JLabel("Quantity", SwingConstants.LEFT);
        info[2] = new JLabel("Price", SwingConstants.LEFT);

        JTextField[] fields = new JTextField[3];
        fields[0] = new JTextField();
        fields[1] = new JTextField();
        fields[2] = new JTextField();

        JButton sellButton = new JButton("Sell");
        sellButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent b) {
                String symbol;
                int quantity = 0, index = 0;
                double price = 0;

                Component[] components = sell.getComponents();
                ArrayList<JTextField> stockInfo = new ArrayList<JTextField>();
                JTextArea messageArea;

                for (int i = 0; i < components.length; i++) {
                    if (components[i] instanceof JTextField) {
                        stockInfo.add(((JTextField)components[i]));
                    }
                    else if (components[i] instanceof JScrollPane) {
                        index = i;
                    }
                }
                JViewport viewport = ((JScrollPane)components[index]).getViewport();
                messageArea = (JTextArea)viewport.getView();
                messageArea.setText("");

                symbol = stockInfo.get(0).getText();
                try {
                    
                    quantity = Integer.parseInt(stockInfo.get(1).getText());

                    if (quantity <= 0) {
                        messageArea.setText("Error. Quantity must be a positive number");
                    }
                    else {
                        try {
                            price = Double.parseDouble(stockInfo.get(2).getText());

                            if (price <= 0) {
                                messageArea.setText("Error. Price must be a positive number.");
                            }
                            else {

                                index = getIndex(symbol, investments);

                                if (index == -1) {
                                    messageArea.setText("Error. Investment does not exist.");
                                }
                                else if (quantity == investments.get(index).getQuantity()) {
                                    investments.get(index).sell(price, quantity);
                                    soldGains += investments.get(index).getBookValue();
                                    messageArea.append("Sold " + quantity + " shares of  " + investments.get(index).getName() + "(" 
                                        + symbol+ ") at $" + price + ".\nInvestment deleted from portfolio.");
                                    investments.remove(index);
                                    
                                    for (int i = 0; i < stockInfo.size(); i++) {
                                        stockInfo.get(i).setText("");
                                    }
                                }
                                else {
                                    investments.get(index).sell(price, quantity);
                                    messageArea.append("Sold " + quantity + " shares of  " + investments.get(index).getName() + "(" 
                                        + symbol+ ") at $" + price + ".\n");
                                    
                                    for (int i = 0; i < stockInfo.size(); i++) {
                                    stockInfo.get(i).setText("");
                                    }
                                }
                            }
                        } catch (Exception ex3) {
                            messageArea.append("Error. Price must be a number\n");
                        }
                    }
                } catch (Exception ex2) {
                    messageArea.append("Error. Quantity must be a whole number\n");
                }

            }
        });
        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setText("");
                }
            }
        });
        JLabel message = new JLabel("Messages", SwingConstants.LEFT);
        JTextArea messageArea = new JTextArea(5, 30);
        messageArea.setEditable(false);
        JScrollPane messageScroll = new JScrollPane(messageArea);

        c.insets = new Insets(10, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridwidth = 2;
        sell.add(sellInstruct, c);
        c.insets = new Insets(20, 30, 0, 0);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        sell.add(info[0], c);
        c.gridx = 1;
        c.gridy = 1;
        c.ipadx = 75;
        sell.add(fields[0], c);
        c.gridx = 2;
        c.gridy = 1;
        c.ipadx = 0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        sell.add(reset, c);
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.LINE_START;
        sell.add(info[1], c);
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 60;
        sell.add(fields[1], c);
        c.gridx = 0;
        c.gridy = 3;
        c.ipadx = 0;
        sell.add(info[2], c);
        c.gridx = 1;
        c.gridy = 3;
        c.ipadx = 60;
        sell.add(fields[2], c);
        c.gridx = 2;
        c.gridy = 3;
        c.ipadx = 0;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.CENTER;
        sell.add(sellButton, c);
        c.gridx = 0;
        c.gridy = 4;
        c.ipadx = 0;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(20, 10, 0, 0);
        sell.add(message, c);
        c.gridy = 5;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 0, 0);
        c.gridwidth = 4;
        c.weighty = 0.5;
        sell.add(messageScroll, c);

        return sell;
    }

    //Creates and formats update panel. Includes ability to update prices
    public JPanel getUpdatePanel() {
        update = new JPanel();
        update.setVisible(false);
        update.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;

        JLabel updateInstruct = new JLabel("Updating investments");
        JLabel[] info = new JLabel[3];
        info[0] = new JLabel("Symbol", SwingConstants.LEFT);
        info[1] = new JLabel("Name", SwingConstants.LEFT);
        info[2] = new JLabel("Price", SwingConstants.LEFT);

        JTextField[] fields = new JTextField[3];
        fields[0] = new JTextField();
        fields[0].setEditable(false);
        fields[0].setText(investments.get(0).getSymbol());
        fields[1] = new JTextField();
        fields[1].setEditable(false);
        fields[1].setText(investments.get(0).getName());
        fields[2] = new JTextField();

        JButton prevButton = new JButton("Prev");
        prevButton.setEnabled(false);
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                Component[] components = update.getComponents();
                ArrayList<JButton> buttons = new ArrayList<JButton>();
                ArrayList<JTextField> infos = new ArrayList<JTextField>();
                JTextArea messageArea;
                int index = 0;

                for (int i = 0; i < components.length; i++) {
                    if (components[i] instanceof JButton) {
                        buttons.add((JButton)components[i]);
                    }
                    else if (components[i] instanceof JTextField) {
                        infos.add((JTextField)components[i]);
                    }
                    else if (components[i] instanceof JScrollPane) {
                        index = i;
                    }
                }

                JViewport viewport = ((JScrollPane)components[index]).getViewport();
                messageArea = (JTextArea)viewport.getView();
                messageArea.setText("");

                if (curIndex > 0) {
                    curIndex--;
                    buttons.get(1).setEnabled(true);

                    infos.get(0).setText(investments.get(curIndex).getSymbol());
                    infos.get(1).setText(investments.get(curIndex).getName());
                    infos.get(2).setText("");

                    if (curIndex == 0) {
                        buttons.get(0).setEnabled(false);
                    }
                }
            }
        });
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent d) {
                
                Component[] components = update.getComponents();
                ArrayList<JButton> buttons = new ArrayList<JButton>();
                ArrayList<JTextField> infos = new ArrayList<JTextField>();
                JTextArea messageArea;
                int index = 0;

                for (int i = 0; i < components.length; i++) {
                    if (components[i] instanceof JButton) {
                        buttons.add((JButton)components[i]);
                    }
                    else if (components[i] instanceof JTextField) {
                        infos.add((JTextField)components[i]);
                    }
                    else if (components[i] instanceof JScrollPane) {
                        index = i;
                    }
                }

                JViewport viewport = ((JScrollPane)components[index]).getViewport();
                messageArea = (JTextArea)viewport.getView();
                messageArea.setText("");

                if (curIndex < investments.size()) {
                    curIndex++;
                    buttons.get(0).setEnabled(true);

                    infos.get(0).setText(investments.get(curIndex).getSymbol());
                    infos.get(1).setText(investments.get(curIndex).getName());
                    infos.get(2).setText("");

                    if (curIndex == (investments.size()-1)) {
                        buttons.get(1).setEnabled(false);
                    }
                }
            }
        });
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent c) {

                Component[] components = update.getComponents();
                ArrayList<JButton> buttons = new ArrayList<JButton>();
                ArrayList<JTextField> infos = new ArrayList<JTextField>();
                JTextArea messageArea;
                int index = 0;
                double price = 0;

                for (int i = 0; i < components.length; i++) {
                    if (components[i] instanceof JButton) {
                        buttons.add((JButton)components[i]);
                    }
                    else if (components[i] instanceof JTextField) {
                        infos.add((JTextField)components[i]);
                    }
                    else if (components[i] instanceof JScrollPane) {
                        index = i;
                    }
                }

                JViewport viewport = ((JScrollPane)components[index]).getViewport();
                messageArea = (JTextArea)viewport.getView();
                messageArea.setText("");

                try {

                    while (price <= 0) {
                        price = Double.parseDouble(infos.get(2).getText());

                        if (price <= 0) {
                            messageArea.setText("Error. Price must be positive.");
                        }
                    }

                    investments.get(curIndex).updatePrice(price);
                    messageArea.setText("Price succesfully updated.");
                    infos.get(2).setText("");
                    
                } catch(Exception f) {
                    messageArea.setText("Error. Price must be a number.");
                }
            }
        });
        JLabel message = new JLabel("Messages", SwingConstants.LEFT);
        JTextArea messageArea = new JTextArea(5, 30);
        messageArea.setEditable(false);
        JScrollPane messageScroll = new JScrollPane(messageArea);

        c.insets = new Insets(10, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridwidth = 2;
        update.add(updateInstruct, c);
        c.insets = new Insets(20, 30, 0, 0);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        update.add(info[0], c);
        c.gridx = 1;
        c.gridy = 1;
        c.ipadx = 75;
        update.add(fields[0], c);
        c.gridx = 2;
        c.gridy = 1;
        c.ipadx = 0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        update.add(prevButton, c);
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.LINE_START;
        update.add(info[1], c);
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 150;
        update.add(fields[1], c);
        c.gridx = 2;
        c.gridy = 2;
        c.ipadx = 0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        update.add(nextButton, c);
        c.gridx = 0;
        c.gridy = 3;
        c.ipadx = 0;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.LINE_START;
        update.add(info[2], c);
        c.gridx = 1;
        c.gridy = 3;
        c.ipadx = 60;
        update.add(fields[2], c);
        c.gridx = 2;
        c.gridy = 3;
        c.ipadx = 0;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.CENTER;
        update.add(saveButton, c);
        c.gridx = 0;
        c.gridy = 4;
        c.ipadx = 0;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(20, 10, 0, 0);
        update.add(message, c);
        c.gridy = 5;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 0, 0);
        c.gridwidth = 4;
        c.weighty = 0.5;
        update.add(messageScroll, c);

        return update;
    }

    //Creates and formats gains panel, prints out all gains
    public JPanel getGainPanel() {
        gains = new JPanel();
        gains.setVisible(false);
        gains.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;

        JLabel gettingGain = new JLabel("Getting total gain");
        JLabel totalGain = new JLabel("Total gain");
        JTextField gainText = new JTextField();
        gainText.setEditable(false);

        JLabel message = new JLabel("Individual gains", SwingConstants.LEFT);
        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane messageScroll = new JScrollPane(messageArea);

        c.insets = new Insets(10, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridwidth = 2;
        gains.add(gettingGain, c);
        c.insets = new Insets(20, 30, 0, 0);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        gains.add(totalGain, c);
        c.gridx = 1;
        c.gridy = 1;
        c.ipadx = 90;
        gains.add(gainText, c);
        c.gridx = 0;
        c.gridy = 2;
        c.ipadx = 0;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(20, 10, 0, 0);
        gains.add(message, c);
        c.gridy = 3;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 0, 0);
        c.gridwidth = 3;
        c.weighty = 0.5;
        c.weightx = 1;
        gains.add(messageScroll, c);

        return gains;
    }

    //Creates and formats search panel. Allows user to search
    public JPanel getSearchPanel() {
        search = new JPanel();
        search.setVisible(false);
        search.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;

        JLabel searchInstruct = new JLabel("Searching investments");
        JLabel[] info = new JLabel[4];
        info[0] = new JLabel("Symbol", SwingConstants.LEFT);
        info[1] = new JLabel("<html>Name<br>Keywords</html>", SwingConstants.LEFT);
        info[2] = new JLabel("Low Price", SwingConstants.LEFT);
        info[3] = new JLabel("High Price", SwingConstants.LEFT);
        JTextField[] fields = new JTextField[4];
        fields[0] = new JTextField("", 10);
        fields[1] = new JTextField("", 30);
        fields[2] = new JTextField("", 6);
        fields[3] = new JTextField("", 6);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String symbol;
                String[] keywords;
                double lowPrice = 0, highPrice = Double.MAX_VALUE;
                int index = 0;

                Component[] components = search.getComponents();
                ArrayList<JTextField> infos = new ArrayList<JTextField>();
                JTextArea messageArea;

                for (int i = 0; i < components.length; i++) {
                    if (components[i] instanceof JTextField) {
                        infos.add((JTextField)components[i]);
                    }
                    else if (components[i] instanceof JScrollPane) {
                        index = i;
                    }
                }

                JViewport viewport = ((JScrollPane)components[index]).getViewport();
                messageArea = (JTextArea)viewport.getView();
                messageArea.setText("");

                symbol = infos.get(0).getText();
                keywords = infos.get(1).getText().split("[ ]+");

                try {
                    if (!infos.get(2).getText().equals("")) {
                        lowPrice = Double.parseDouble(infos.get(2).getText());
                    }
                    else {
                        lowPrice = 0;
                    }

                    if (lowPrice < 0) {
                        messageArea.setText("Error. Price must be positive or 0.");
                    }
                    else {
                        try {
                            if (!infos.get(3).getText().equals("")) {
                                highPrice = Double.parseDouble(infos.get(3).getText());
                            }
                            else {
                                highPrice = Double.MAX_VALUE;
                            }                         

                            if (highPrice < lowPrice) {
                                messageArea.setText("Error. High price cannot be lower than low Price");
                            }
                            else {
                                messageArea.setText("");
                                if (symbol.length() > 0) {
                                    for (int i = 0; i < investments.size(); i++) {
                                        if (investments.get(i).getSymbol().equalsIgnoreCase(symbol)) {
                                            if (lowPrice <= investments.get(i).getPrice() && investments.get(i).getPrice() <= highPrice) {
                                                int check = -1;
                                                String[] keys = investments.get(i).getName().split("[ ]+");

                                                for (int j = 0; j < keywords.length; j++) {
                                                    check = -1;
                                                    for (int k = 0; k < keys.length; k++) {
                                                        if (keywords[j].equalsIgnoreCase(keys[k])) {
                                                            check = 1;
                                                            break;
                                                        }
                                                    }
                                                    if (check == -1) {
                                                        break;
                                                    }
                                                }

                                                if (check == 1) {
                                                    messageArea.append(investments.get(i).toString());
                                                }
                                            }
                                        }
                                    }
                                }
                                else {
                                    for (int i = 0; i < investments.size(); i++) {
                                        if (lowPrice <= investments.get(i).getPrice() && investments.get(i).getPrice() <= highPrice) {
                                            int check = -1;
                                            String[] keys = investments.get(i).getName().split("[ ]+");

                                            for (int j = 0; j < keywords.length; j++) {
                                                check = -1;
                                                for (int k = 0; k < keys.length; k++) {
                                                    if (keywords[j].equalsIgnoreCase(keys[k])) {
                                                        check = 1;
                                                        break;
                                                    }
                                                }
                                                if (check == -1) {
                                                    break;
                                                }
                                            }

                                            if (check == 1) {
                                                messageArea.append(investments.get(i).toString());
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception g) {
                            messageArea.setText("Error. High Price must be a number.");
                        }

                    }

                } catch (Exception f) {
                    messageArea.setText("Error. Low Price must be a number.");
                }

            }
        });
        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setText("");
                }
            }
        });
        JLabel message = new JLabel("Messages", SwingConstants.LEFT);
        JTextArea messageArea = new JTextArea(5, 30);
        messageArea.setEditable(false);
        JScrollPane messageScroll = new JScrollPane(messageArea);

        c.insets = new Insets(10, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridwidth = 2;
        search.add(searchInstruct, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 20, 0, 0);
        search.add(info[0], c);
        c.gridx = 1;
        c.gridy = 1;
        c.ipadx = 75;
        search.add(fields[0], c);
        c.gridx = 2;
        c.gridy = 1;
        c.ipadx = 0;
        c.anchor = GridBagConstraints.CENTER;
        search.add(reset, c);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        search.add(info[1], c);
        c.gridx = 1;
        c.gridy = 2;
        c.ipadx = 150;
        search.add(fields[1], c);
        c.gridx = 0;
        c.gridy = 3;
        c.ipadx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        search.add(info[2], c);
        c.gridx = 1;
        c.gridy = 3;
        c.ipadx = 60;
        search.add(fields[2], c);
        c.gridx = 2;
        c.gridy = 3;
        c.weightx = 1.0;
        c.ipadx = 0;
        c.anchor = GridBagConstraints.CENTER;
        search.add(searchButton, c);
        c.gridx = 0;
        c.gridy = 4;
        c.ipadx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        search.add(info[3], c);
        c.gridx = 1;
        c.gridy = 4;
        c.ipadx = 60;
        search.add(fields[3], c);
        c.gridx = 0;
        c.gridy = 6;
        c.ipadx = 0;
        c.insets = new Insets(20, 10, 0, 0);
        c.anchor = GridBagConstraints.LINE_START;
        search.add(message, c);
        c.gridy = 7;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 0, 0);
        c.gridwidth = 4;
        c.weighty = 0.5;
        search.add(messageScroll, c);

        return search;
    }

    /**
     * Function reads in investments from file
     * @param fileName File to take investments from
     * @return ArrayList of investments from file
     */
    private static void readInvestments(String fileName) {
        String type, symbol, name;
        int quantity;
        double price, bookValue;
        Scanner inputStream = null;

        try {
            inputStream = new Scanner(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("Error. Input stream could not be opened.");
        }

        if (inputStream != null) {
            while (inputStream.hasNextLine()) {
                type = inputStream.nextLine();
                symbol = inputStream.nextLine();
                name = inputStream.nextLine();
                quantity = Integer.parseInt(inputStream.nextLine());
                price = Double.parseDouble(inputStream.nextLine());
                bookValue = Double.parseDouble(inputStream.nextLine());

                if (type.equalsIgnoreCase("stock")) {
                    Stock tempStock = new Stock(symbol, name, quantity, price, bookValue);
                    investments.add(tempStock);
                }
                else {
                    MutualFund tempFund = new MutualFund(symbol, name, quantity, price, bookValue);
                    investments.add(tempFund);
                }
            }
        }
        
        if (inputStream != null) {
            inputStream.close();
        }
        return;
    }

    /**
    *  Function to write investments to desk
    *  @param fileName File to write investments to
     */
    private static void writeInvestments(String file) {
        PrintWriter outputStream = null;
        try {
            outputStream = new PrintWriter(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            System.out.println("Error writing investments to disk.");
            return;
        }

        if (outputStream != null) {
        for (int i = 0; i < investments.size(); i++) {
                if (investments.get(i) instanceof Stock) {
                    outputStream.println("Stock");
                }
                else {
                    outputStream.println("MutualFund");
                }
                outputStream.println(investments.get(i).getSymbol());
                outputStream.println(investments.get(i).getName());
                outputStream.println(investments.get(i).getQuantity());
                outputStream.println(investments.get(i).getPrice());
                outputStream.println(investments.get(i).getBookValue());
            }

            outputStream.close();
        }
        return;
    }

    //Delete getIndexMutual and getIndexStock once investments arraylist fully implemented
    /**
     * Gets index of investment in arraylist from symbol
     * @param symbol Symbol of investment to check
     * @param investments Arraylist of investments
     * @return index if found, else -1
     */
    private static int getIndex(String symbol, ArrayList<Investment> investments) {
        for (int i = 0; i < investments.size(); i++) {
            if (investments.get(i).getSymbol().equalsIgnoreCase(symbol)) {
                return i;
            }
        }

        return -1;
    }
}


