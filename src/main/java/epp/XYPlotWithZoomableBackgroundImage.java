package epp;

/**
 * Created by placisadmin on 28/02/2017.
 */
import java.awt.*;
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
import org.jfree.ui.about.SystemProperties;


public class XYPlotWithZoomableBackgroundImage extends XYPlot
{

    private boolean zoomableBackgroundImage;
    private int zoom_counter=0;
    private double minX, minY, maxX, maxY;

    public XYPlotWithZoomableBackgroundImage(XYDataset dataset,
                                             ValueAxis domainAxis,
                                             ValueAxis rangeAxis,
                                             XYItemRenderer renderer,
                                             boolean zoomableBackgroundImage,
                                             double minX, double minY, double maxX, double maxY
                                            )
    {
        super(dataset, domainAxis, rangeAxis, renderer);
        this.zoomableBackgroundImage = zoomableBackgroundImage;
        this.minX=minX;
        this.minY=minY;
        this.maxX=maxX;
        this.maxY=maxY;

    }

    /**
     * Draws the background image (if there is one) aligned within the
     * specified area.
     *
     * @param g2 the graphics device.
     * @param area the area.
     *
     * @see #getBackgroundImage()
     * @see #getBackgroundImageAlignment()
     * @see #getBackgroundImageAlpha()
     */

    public void drawBackgroundImage(Graphics2D g2, Rectangle2D area)
    {

        // if background image is not zoomable call Plot.drawBackgroundImage(g2,area);
        if(!zoomableBackgroundImage)
        {
            super.drawBackgroundImage(g2,area);
            System.out.println("not zoomable");
        }

        else
        {
            //Set the ManhattanGrid as Background image
            Image backgroundImage = super.getBackgroundImage();

            float backgroundAlpha = super.getBackgroundAlpha();
            int backgroundImageAlignment = super.getBackgroundImageAlignment();

            if (backgroundImage != null)   // make sure there is a background image
            {
                Composite originalComposite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC,backgroundAlpha));

                //Creat a new Rectangle to draw the grid(initially same size as the Manhattan Grid, cause no zoom)
                Rectangle2D dest = new Rectangle2D.Double(0.0, 0.0, backgroundImage.getWidth(null), backgroundImage.getHeight(null));
                Align.align(dest, area, backgroundImageAlignment);

                //Calculate the initial chart size
                double chart_w = Math.abs(1.1*minX) + Math.abs(maxX*1.1);      //chart abs. width
                double chart_h = Math.abs(minY*1.1*0) + Math.abs(maxY*1.1);      //chart abs. heigth
                System.out.println("chart_w = " + chart_w);
                System.out.println("chart_h = " + chart_h);

                //Manhattan Grid(Background) Size
                float background_w = backgroundImage.getWidth(null);          //width
                float background_h = backgroundImage.getHeight(null);         //heigth
                System.out.println("background_w = " + background_w);
                System.out.println("background_h = " + background_h);

                //get X Axis & Y Axis
                ValueAxis xAxis = getDomainAxis();
                ValueAxis yAxis = getRangeAxis();

                //scale the zoom rectangle to size of our manhattan grid
                float x_min_rat = Math.abs((float)((minX - xAxis.getLowerBound()) / (chart_w/background_w)));
                float x_max_rat = Math.abs((float)((maxX - xAxis.getUpperBound()) / (chart_w/background_w)));
                System.out.println("x_min_rat:  " + x_min_rat);
                System.out.println("x_max_rat:  " + x_max_rat);

                float y_min_rat = Math.abs((float)((minY - yAxis.getLowerBound()) / (chart_h/background_h)));
                float y_max_rat = Math.abs((float)((maxY - yAxis.getUpperBound()) / (chart_h/background_h)));
                System.out.println("y_min_rat:  " + y_min_rat);
                System.out.println("y_max_rat:  " + y_max_rat);

                //Now, we draw a new rect(new manhattan) from src rect of pic to the new dest rect in g2
                ((Graphics) g2).drawImage(backgroundImage,
                        (int) dest.getX(),                          //dest x1 corner1
                        (int) dest.getY(),                          //dest y1
                        (int)(dest.getX() + dest.getWidth() + 1),   //dest x2 corner2
                        (int)(dest.getY() + dest.getHeight() + 1),  //dest y2
                        (int) Math.round(x_min_rat),                //src  x1 corner1
                        (int) Math.round(y_max_rat),                //src  y1
                        (int) Math.round( background_w-(x_max_rat) ),         //src  x2 corner2
                        (int) Math.round( background_h-(y_min_rat) ),         //src  y2
                        null);

                /* @param       img the specified image to be drawn. This method does
                    *                  nothing if <code>img</code> is null.
                    * @param       dx1 the <i>x</i> coordinate of the first corner of the
                *                    destination rectangle.
                * @param       dy1 the <i>y</i> coordinate of the first corner of the
                *                    destination rectangle.
                * @param       dx2 the <i>x</i> coordinate of the second corner of the
                *                    destination rectangle.
                * @param       dy2 the <i>y</i> coordinate of the second corner of the
                *                    destination rectangle.
                * @param       sx1 the <i>x</i> coordinate of the first corner of the
                *                    source rectangle.
                * @param       sy1 the <i>y</i> coordinate of the first corner of the
                *                    source rectangle.
                * @param       sx2 the <i>x</i> coordinate of the second corner of the
                *                    source rectangle.
                * @param       sy2 the <i>y</i> coordinate of the second corner of the
                *                    source rectangle.
                * @param       observer object to be notified as more of the image is
                    *                    scaled and converted.
                    *                    */
                g2.setComposite(originalComposite);
            }
        }
        System.out.println("Ning is Zooming "+zoom_counter+"times!!!!!!!");
        zoom_counter++;
    }
}


/*if (backgroundImage != null)
 {

 // get background image alignment
 int backgroundImageAlignment = this.getBackgroundImageAlignment();

 // get background image alpha
 float backgroundImageAlpha = this.getBackgroundImageAlpha();



 // get X Axis
 ValueAxis xAxis = getDomainAxis();


 ValueAxis yAxis = getRangeAxis();

 // get full X Range
 Range xRange = getDataRange(xAxis);

 // get full Y Range
 Range yRange = getDataRange(yAxis);

 // get full x range value
 double xDataUpperBound = xRange.getUpperBound();
 double xDataLowerBound = xRange.getLowerBound();
 double xRangeValue = xDataUpperBound - xDataLowerBound;

 // get full y range value
 double yDataUpperBound = yRange.getUpperBound();
 double yDataLowerBound = yRange.getLowerBound();
 double yRangeValue = yDataUpperBound - yDataLowerBound;

 //yDataLowerBound=0.0;

 // get current min X
 double xmin = xAxis.getLowerBound();

 // get current max X
 double xmax = xAxis.getUpperBound();

 // get current min Y
 double ymin = yAxis.getLowerBound();

 // get current max Y
 double ymax = yAxis.getUpperBound();

 if(yRangeValue < Math.abs(ymax - ymin)){
 yRangeValue = Math.abs(ymax - ymin);
 }

 if(xRangeValue < Math.abs(xmax - xmin)){
 xRangeValue = Math.abs(xmax - xmin);
 }

 // get original image width
 int originalImageWidth = backgroundImage.getWidth(null);

 // get original image height
 int originalImageHeight = backgroundImage.getHeight(null);
 System.out.println("ImageWidth " + originalImageWidth);
 System.out.println("ImageHeight " + originalImageHeight);


 // we are starting at top left for image and bottom left for drawing graph


 // move to point 0,0
 double xmin2 = xmin - xDataLowerBound;
 double xmax2 = xmax - xDataLowerBound;
 double ymin2 = ymin - yDataLowerBound;
 double ymax2 = ymax - yDataLowerBound;

 // flip the y axis
 double xmin3 = xmin2;
 double xmax3 = xmax2;
 double ymin3 = yRangeValue - ymax2;
 double ymax3 = yRangeValue - ymin2;

 System.out.println("xmax " + xmax);
 System.out.println("xmin " + xmin);
 System.out.println("ymax " + ymax);
 System.out.println("ymin " + ymin);

 System.out.println("xmax2 " + xmax2);
 System.out.println("xmin2 " + xmin2);
 System.out.println("ymax2 " + ymax2);
 System.out.println("ymin2 " + ymin2);

 System.out.println("xmin3 " + xmin3);
 System.out.println("xmax3 " + xmax3);
 System.out.println("ymin3 " + ymin3);
 System.out.println("ymax3 " + ymax3);

 System.out.println("xDataUpperBound"+xDataUpperBound);
 System.out.println("xDataLowerBound" + xDataLowerBound);
 System.out.println("yDataLowerBound" + yDataLowerBound);
 System.out.println("xRangeValue" + xRangeValue);
 System.out.println("yRangeValue" + yRangeValue);




 BufferedImage bi = new BufferedImage(originalImageWidth, originalImageHeight, BufferedImage.TYPE_INT_ARGB); // ARGB to support transparency if in original image

 Graphics2D g = bi.createGraphics();

 g.drawImage(backgroundImage, 0, 0, null);
 g.dispose();

 double newXMin = ((xmin3/xRangeValue) * originalImageWidth);
 double newXMax = ((xmax3/xRangeValue) * originalImageWidth);
 double newYMin = ((ymin3/yRangeValue) * originalImageHeight);
 double newYMax = ((ymax3/yRangeValue) * originalImageHeight);



 if(newXMin < 0){
 newXMin = 0;
 }
 if(newYMin < 0){
 newYMin = 0;
 }

 System.out.println("newXMin " + newXMin);
 System.out.println("newXMax " + newXMax);
 System.out.println("newYMin " + newYMin);
 System.out.println("newYMax " + newYMax);
 System.out.println("(newXMax - newXMin) " + (newXMax - newXMin));
 System.out.println("(newYMax - newYMin) " + (newYMax - newYMin));

 double subImageWidth = (newXMax - newXMin);
 double subImageHeight = (newYMax - newYMin);


 if((newYMin + subImageHeight) > (bi.getHeight())){
 subImageHeight = bi.getHeight() - newYMin;
 }

 if((newXMin +subImageWidth) > bi.getWidth()){
 subImageWidth = bi.getWidth() - newXMin;
 }
 System.out.println("sub Height " + subImageHeight);
 System.out.println("sub Width " + subImageWidth);
 BufferedImage bi2 = bi.getSubimage((int) (newXMin), (int) (newYMin), (int) (subImageWidth), (int) (subImageHeight));

 Composite originalComposite = g2.getComposite();
 g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
 backgroundImageAlpha));
 Rectangle2D dest = new Rectangle2D.Double(0.0, 0.0, bi2.getWidth(null), bi2.getHeight(null));
 Align.align(dest, area, backgroundImageAlignment);
 g2.drawImage(bi2, (int) dest.getX(), (int) dest.getY(), (int) dest.getWidth() + 1, (int) dest.getHeight() + 1, null);

 g2.setComposite(originalComposite);
 }*/


