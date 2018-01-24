package epp;

/**
 * Created by placisadmin on 28/02/2017.
 */
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Align;
import java.awt.Graphics;

/**
 * Created by NingD on 2016/5/10.
 */
public class myXYPlot extends XYPlot {

    public Double X_Min;    //chart overall (0% zoom) x min val
    public Double X_Max;   // ..
    public Double Y_Min;    // ..
    public Double Y_Max;   //..
    public myXYPlot(XYDataset dataset, ValueAxis domainAxis,
                    ValueAxis rangeAxis, XYItemRenderer renderer) {
        super(dataset, domainAxis, rangeAxis, renderer);
    }

    /**
     * Draws the background image (if there is one) aligned within the specified
     * area and zoomed to the current zoom level.
     *
     * @param g2
     *            the graphics device.
     * @param area
     *            the area.
     */
    public void drawBackgroundImage(Graphics2D g2, Rectangle2D area)
    {
        Image backgroundImage = super.getBackgroundImage();
        float backgroundAlpha = super.getBackgroundAlpha();
        int backgroundImageAlignment = super.getBackgroundImageAlignment();

        if (backgroundImage != null)
        {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC,backgroundAlpha));
            Rectangle2D dest = new Rectangle2D.Double(0.0, 0.0, backgroundImage.getWidth(null), backgroundImage.getHeight(null));
            Align.align(dest, area, backgroundImageAlignment);

            double cw = Math.abs(X_Min) + Math.abs(X_Max);      //chart abs. width
            double ch = Math.abs(Y_Min) + Math.abs(Y_Max);      //chart abs. heigth
            float bw = backgroundImage.getWidth(null);          //pic width
            float bh = backgroundImage.getHeight(null);         //pic heigth

            //scale zoom rectangle to size of pic (all 4 edges)
            float x_min_rat = Math.abs(
                    (float)((X_Min - getDomainAxis().getLowerBound()) / (cw/bw))
            );
            float x_max_rat = Math.abs(
                    (float)((X_Max - getDomainAxis().getUpperBound()) / (cw/bw))
            );

            float y_min_rat = Math.abs(
                    (float)((Y_Min - getRangeAxis().getLowerBound()) / (ch/bh))
            );
            float y_max_rat = Math.abs(
                    (float)((Y_Max - getRangeAxis().getUpperBound()) / (ch/bh))
            );
            //and draw from src rect of pic to dest rect in g2
            ((Graphics) g2).drawImage(backgroundImage,
                    (int) dest.getX(),                     //dest x1
                    (int) dest.getY(),                     //dest y1
                    (int)(dest.getX() + dest.getWidth() + 1),      //dest x2
                    (int)(dest.getY() + dest.getHeight() + 1),      //dest y2
                    (int) Math.round(x_min_rat),               //src  x1
                    (int) Math.round(y_max_rat),               //src  y1
                    (int) Math.round( bw-(x_max_rat) ),         //src  x2
                    (int) Math.round( bh-(y_min_rat) ),         //src  y2
                    null);

            g2.setComposite(originalComposite);
        }
    }

}

