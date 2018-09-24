package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args){
        JFrame frame = new JFrame("OCD Installer");
        Gui gui = new Gui();
        frame.add(gui);
        frame.pack();
        frame.setSize(250, 200);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
