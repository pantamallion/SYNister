package SYNister;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * GUI
 *
 * @author Arnav Raha
 */


public class GUI implements ActionListener{


    private JFrame frame;
    private JPanel panel;

    private JLabel invLabel;
    private JTextField invText;

    private JLabel inLabel;
    private JTextField inText;

    private JLabel outLabel;
    private JTextField outText;

    private JLabel errorLabel;
    private Main main = new Main();


    public GUI() {

        frame = new JFrame();
        panel = new JPanel();
        frame.setSize(600, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        invLabel = new JLabel("Inventory:");
        invLabel.setBounds(35,15,80,35);
        panel.add(invLabel);

        invText = new JTextField(20);
        invText.setBounds(100, 20, 450, 30);
        panel.add(invText);

        inLabel = new JLabel("Input:");
        inLabel.setBounds(60,60,80,35);
        panel.add(inLabel);

        inText = new JTextField(20);
        inText.setBounds(100, 60, 450, 30);
        panel.add(inText);

        outLabel = new JLabel("Output:");
        outLabel.setBounds(50,90,80,35);
        panel.add(outLabel);

        outText = new JTextField(20);
        outText.setBounds(100, 90, 450, 30);
        panel.add(outText);

        JButton button = new JButton("Generate Labsheets");
        button.setBounds(220,130,200,50);
        button.addActionListener(this);
        panel.add(button);

        errorLabel = new JLabel();
        errorLabel.setBounds(120,150,400,100);
        panel.add(errorLabel);


        frame.add(panel, BorderLayout.CENTER);


        frame.setTitle("SYNister");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        new GUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        errorLabel.setText("");
        try {

            //check if inventory file is specified
            //if blank will assume last entered file if applicable
            if (!main.isInit()) {
                if(invText.getText().length() == 0) {
                    throw new Exception("Please specify Inventory folder");
                }
            }

            //resets inventory to currently specified
            if(invText.getText().length() != 0) {
                String inv = invText.getText();

                //ensures path is in proper format
                if (inv.charAt(inv.length()-1) != '/') {
                    inv = inv+"/";
                }

                main.initiate(inv);
            }

            if(inText.getText().length() == 0) {
                throw new Exception("Please specify Input folder of construction files");
            }
            if(outText.getText().length() == 0) {
                throw new Exception("Please specify Output folder");
            }
            main.run(inText.getText(), outText.getText());
        } catch (Exception exc) {
            errorLabel.setText("ERROR: " + exc.toString().split(":")[1]);
        }



    }
}
