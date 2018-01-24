package epp;

/**
 * Created by placisadmin on 28/02/2017.
 */
import org.seamcat.model.plugin.eventprocessing.PanelDefinition;
import org.seamcat.model.plugin.eventprocessing.Panels;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
/*import java.awt.event.*;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;*/

import javax.swing.*;

import java.awt.Color;


public class MMap extends JPanel{ //implements MouseListener, MouseMotionListener{

    private static final long serialVersionUID = 1L;
    private ArrayList<Integer> Matrix = new ArrayList<Integer>();
    private ArrayList<Point> Transmitter = new ArrayList<Point>();
    private ArrayList<Point> Receiver = new ArrayList<Point>();
    private int Blue=0;
    private int Red=0;
    private int Green=0;
    private int MaxFloor=0;
    private Color color = new Color(0,0,0);
    private int nbLines;
    private int nbColumns;
    private int zoom=1;
    private int x;
    private int y;
    private int width=10;
    private int height=10;
    private int shift_x=1;
    private int shift_y=1;
    private Panels panel;
    private ManGridEPPUIinput input;

    public MMap(){
        super();
    }

    public MMap(int matrix[][],int nbLines,int nbColumns, int MaxFloor){
        super();
        //this.width=windowSizeW/nbColumns;
        //this.height=windowSizeH/nbLines;
        this.setPreferredSize(new Dimension(nbColumns * width, nbLines * height));
        this.setMaximumSize(new Dimension(nbColumns*width,nbLines*height));
        this.setMinimumSize(new Dimension(nbColumns*width,nbLines*height));
        this.MaxFloor=MaxFloor;
        this.nbColumns=nbColumns;
        this.nbLines=nbLines;
        //addMouseListener(this);
        //addMouseMotionListener(this);

        for (int i=0; i<nbColumns;i++){
            for(int j=0; j<nbLines;j++){
                Matrix.add(Integer.valueOf(matrix[j][i]));
            }
        }




    }

    /*

    @Override
    public void mouseClicked(MouseEvent e) {
        int buttonDown=e.getButton(); //Enregistre le bouton de la souris enfoncé

        if (buttonDown == MouseEvent.BUTTON1) {
            zoom *= 2;
            repaint();
        }
        else if (buttonDown == MouseEvent.BUTTON3){
            zoom /= 2;
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // shift_x += 10;
        // shift_y += 10;
        // repaint();

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    */

    public void paintComponent(Graphics g){

        for(int i=0; i<nbColumns; i++){
            for(int j=0; j<nbLines; j++){
                if(((double)Matrix.get(i*nbLines+j)/(double)(MaxFloor))<=0.5){
                    Blue=255-Matrix.get(i*nbLines+j)*255*2/(MaxFloor);
                    Green=Matrix.get(i*nbLines+j)*255*2/(MaxFloor);
                    Red=0;
                }
                else{
                    Green=255*2-Matrix.get(i*nbLines+j)*255*2/(MaxFloor);
                    Red=Matrix.get(i*nbLines+j)*255/(MaxFloor);
                    Blue=0;
                }

                //On ne peut pas dézoomer plus que la largeur et hauteur initiale, sinon écran blanc.
                //Si c'est le cas, on met zoom=1

                if(width*zoom<this.width)
                    zoom=1;
                if (height*zoom<this.height)
                    zoom=1;

                //Calcul des coordonnées x et y;

                x=(i+shift_x)*(width);
                y=(j+shift_y)*(height);

                //x=i*(width*zoom);
                //y=j*(height*zoom);

                color=new Color(Red,Green,Blue);
                g.setColor(color);
                g.fillRect(x, y, width*zoom, height*zoom);
            }
        }
        color=new Color(0,0,0);
        g.setColor(color);
        g.setFont(new Font("Text",1,15));
        g.fillOval(nbColumns/2*width, nbLines/2*height, 10, 10);
        for(int i=0; i<Transmitter.size();i++){
            g.drawString("ILT", (int)Transmitter.get(i).getX()*width+nbColumns/2*width-12, -(int)Transmitter.get(i).getY()*height+nbLines/2*height+6);
            //g.drawString("ILT", (int)Transmitter.get(i).getX()*width*zoom+nbColumns/2*width*zoom-12, -(int)Transmitter.get(i).getY()*height*zoom+nbLines/2*height*zoom+6);
            //System.out.println("ILT_X_Grille"+Transmitter.get(i).getX());
            //System.out.println("ILT_Y_Grille"+Transmitter.get(i).getY());

        }
        for(int i=0; i<Receiver.size();i++){
            g.drawString("VLR", (int)Receiver.get(i).getX()*width+nbColumns/2*width-12, -(int)Receiver.get(i).getY()*height+nbLines/2*height+6);
            //g.drawString("VLR", (int)Receiver.get(i).getX()*width*zoom+nbColumns*zoom/2*width*zoom-12, -(int)Receiver.get(i).getY()*height*zoom+nbLines*zoom/2*height*zoom+6);
            //System.out.println("VLR_X_Grille"+Receiver.get(i).getX());
            //System.out.println("VLR_Y_Grille"+Receiver.get(i).getY());
        }
    }


    public int getCellWidth(){
        return width;
    }
    public int getCellHeight(){
        return height;
    }
    public void addTransmitter(Point p){
        Transmitter.add(p);
        repaint();
    }
    public void addReceiver(Point p){
        Receiver.add(p);
        repaint();
    }

    public Image takeSnapShot(int width, int height) {
        this.setSize(width, height);
        BufferedImage bufImage = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB);
        this.paint(bufImage.createGraphics());
        return bufImage;
    }
}