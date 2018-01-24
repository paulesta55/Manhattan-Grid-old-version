package epp;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Building extends JPanel{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String texte;

    public Building(){
        super();
        this.setFont(new Font("Building",5,12));
        texte = new String();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawString(texte,1,this.getHeight());
        System.out.println(texte);
    }

    public void setText(String text){
        texte=text;
    }

}