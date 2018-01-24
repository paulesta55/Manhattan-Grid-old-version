package epp;

/**
 * Created by placisadmin on 28/02/2017.
 */
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class Canvas extends JPanel {

    public Canvas() {
        this.setSize(new Dimension(500, 300));
    }



    public void paintComponent(Graphics g) {
        g.setColor(Color.red);
        g.drawRect(10, 20, 100, 100);
        g.setColor(Color.green);
        g.fillRect(200, 20, 100, 100);
    }


    /*
    public Image capture(){
        Rectangle r = new Rectangle(375,50,600,300);
        BufferedImage bi = ScreenImage.createImage(r);
        ScreenImage.writeImage(bi,"capture.jpg");
    }
    */

    public Image takeSnapShot() {
        BufferedImage bufImage = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB);
        this.paint(bufImage.createGraphics());
        return bufImage;
    }


    public void saveComponentAsJPEG(Component myComponent, String filename) {
        Dimension size = myComponent.getSize();
        BufferedImage myImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = myImage.createGraphics();
        try{

            OutputStream out = new FileOutputStream(filename);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(myImage);
            out.close();

        } catch (Exception e) {
            System.out.println(e);
        }



    }

}


