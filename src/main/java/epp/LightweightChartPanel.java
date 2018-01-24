package epp;

/**
 * Created by placisadmin on 28/02/2017.
 */
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.panel.Overlay;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

/**
 * Extends ChartPanel to provide extra or faster functionality.
 * - Removes EntityCollection to save memory (especially on large datasets).
 * - Dynamically determines the ChartEntity.
 */
public class LightweightChartPanel extends ChartPanel {
    private static final long serialVersionUID = 1L;
    private ArrayList<Integer> ArrayMatrix = new ArrayList<Integer>();
    // private ArrayList<Point> Transmitter = new ArrayList<Point>();
    // private ArrayList<Point> Receiver = new ArrayList<Point>();

    private JFreeChart chart;

    private Color color = new Color(0, 0, 0);

    private int zoom = 1;
    private int x;
    private int y;
    private int width = 5;
    private int height = 5;
    private int shift_x = 1;
    private int shift_y = 1;
    private int rectx = 100;
    private int recty = 100;

    private int nbLines = 50;
    private int nbColumns = 50;
    private int[][] Matrix = new int[nbLines][nbColumns];

    //private int MaxLines=500;
    //private int MaxColumns=500;

    private int BuildingSize = 10;
    private int AvailableHeightGap;
    private int AvailableWidthGap;
    private int AvailableHeight;
    private int AvailableWidth;
    private int BuildingHeightMeanSize = 3;
    private int BuildingWidthMeanSize = 2;
    private int MaxFloor = 10;

    private double BuildingHeightStdDevSize = 1;
    private double BuildingWidthStdDevSize = 1.5;

    private int Height = 0;
    private int Width = 0;
    private int Floor = 0;

    private int m = 0;
    private int n = 0;

    private int zoommoi=1;

    private int Blue=0;
    private int Red=0;
    private int Green=0;


    /*private Grid grid;
    public LightweightChartPanel(JFreeChart chart, Grid g)
    {
        this(chart,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                DEFAULT_MINIMUM_DRAW_WIDTH,
                DEFAULT_MINIMUM_DRAW_HEIGHT,
                DEFAULT_MAXIMUM_DRAW_WIDTH,
                DEFAULT_MAXIMUM_DRAW_HEIGHT,
                DEFAULT_BUFFER_USED,
                true,  // properties
                true,  // save
                true,  // print
                true,  // zoom
                true   // tooltips
        );
        this.grid=g;
    }*/

    public LightweightChartPanel(JFreeChart chart) {
        this(chart,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                DEFAULT_MINIMUM_DRAW_WIDTH,
                DEFAULT_MINIMUM_DRAW_HEIGHT,
                DEFAULT_MAXIMUM_DRAW_WIDTH,
                DEFAULT_MAXIMUM_DRAW_HEIGHT,
                DEFAULT_BUFFER_USED,
                true,  // properties
                true,  // save
                true,  // print
                true,  // zoom
                true   // tooltips
        );
        this.Create();
        this.setBackground(Color.green);
    }

    public LightweightChartPanel(JFreeChart chart, boolean useBuffer) {
        this(chart,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                DEFAULT_MINIMUM_DRAW_WIDTH,
                DEFAULT_MINIMUM_DRAW_HEIGHT,
                DEFAULT_MAXIMUM_DRAW_WIDTH,
                DEFAULT_MAXIMUM_DRAW_HEIGHT,
                useBuffer,
                true,  // properties
                true,  // save
                true,  // print
                true,  // zoom
                true   // tooltips
        );
        this.Create();
    }

    public LightweightChartPanel(JFreeChart chart,
                                 int width, int height,
                                 int minimumDrawWidth, int minimumDrawHeight,
                                 int maximumDrawWidth, int maximumDrawHeight,
                                 boolean useBuffer,
                                 boolean properties, boolean save, boolean print, boolean zoom,
                                 boolean tooltips) {
        super(chart,
                width,
                height,
                minimumDrawWidth,
                minimumDrawHeight,
                maximumDrawWidth,
                maximumDrawHeight,
                useBuffer,
                properties,
                save,
                print,
                zoom,
                tooltips
        );

        this.Create();
        // Disable inefficient EntityCollection
        this.getChartRenderingInfo().setEntityCollection(null);
    }

    public void mousePressed(MouseEvent e){
        super.mousePressed(e);
        System.out.println(e.getX()+" "+e.getY());
        rectx = e.getX();
        recty = e.getY();
        System.out.println(this.getZoomInFactor()+" "+this.getScaleY());
        zoommoi+=1;
        //repaint();
    }

    /**
     * Gets the tooltip dynamically instead of from the EntityCollection.
     */
    public String getToolTipText(MouseEvent e) {
        ChartEntity entity = this.getChartEntityForPoint(e.getPoint());
        if (entity != null)
            return entity.getToolTipText();
        return null;
    }

    /**
     * - No longer setNotify(true) to force a redraw
     * - Dynamically creates a ChartEntity instead of using this.info
     */
    public void mouseClicked(MouseEvent event) {
        Insets insets = this.getInsets();
        int x = (int) ((event.getX() - insets.left) / this.getScaleX());
        int y = (int) ((event.getY() - insets.top) / this.getScaleY());

        this.setAnchor(new Point2D.Double(x, y));
        if (this.getChart() == null) {
            return;
        }

        Object[] listeners = this.getListeners(ChartMouseListener.class);
        if (listeners.length == 0) {
            return;
        }

        /* Create custom entity */
        ChartEntity entity = this.getChartEntityForPoint(event.getPoint());

        ChartMouseEvent chartEvent = new ChartMouseEvent(this.getChart(), event, entity);
        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            ((ChartMouseListener) listeners[i]).chartMouseClicked(chartEvent);
        }
    }

    protected static int HOTSPOT_SIZE = 5;

    /**
     * Gets the ChartEntity for the corresponding point.
     * It converts the screen (X, Y) into chart area (X, Y)
     * and then looks for a data item that lies inside the hotspot.
     * <p>
     * Inspired by http://www.jfree.org/phpBB2/viewtopic.php?p=69588#69588
     */
    public ChartEntity getChartEntityForPoint(Point point) {
        XYPlot xyPlot = null;
        Rectangle2D screenArea = null;
        try {
            xyPlot = this.getChart().getXYPlot();
            screenArea = this.scale(this.getChartRenderingInfo().getPlotInfo().getDataArea());
        } catch (Exception e) {
            return null;
        }

        double hotspotSizeX = HOTSPOT_SIZE * this.getScaleX();
        double hotspotSizeY = HOTSPOT_SIZE * this.getScaleY();
        double x0 = point.getX();
        double y0 = point.getY();
        double x1 = x0 - hotspotSizeX;
        double y1 = y0 + hotspotSizeY;
        double x2 = x0 + hotspotSizeX;
        double y2 = y0 - hotspotSizeY;
        RectangleEdge xEdge = RectangleEdge.BOTTOM;
        RectangleEdge yEdge = RectangleEdge.LEFT;

        // Switch everything for horizontal charts
        if (xyPlot.getOrientation() == PlotOrientation.HORIZONTAL) {
            hotspotSizeX = HOTSPOT_SIZE * this.getScaleY();
            hotspotSizeY = HOTSPOT_SIZE * this.getScaleX();
            x0 = point.getY();
            y0 = point.getX();
            x1 = x0 + hotspotSizeX;
            y1 = y0 - hotspotSizeY;
            x2 = x0 - hotspotSizeX;
            y2 = y0 + hotspotSizeY;
            xEdge = RectangleEdge.LEFT;
            yEdge = RectangleEdge.BOTTOM;
        }

        // Loop through each dataset
        int datasetCount = xyPlot.getDatasetCount();
        for (int datasetIndex = 0; datasetIndex < datasetCount; datasetIndex++) {
            ValueAxis domainAxis = xyPlot.getDomainAxisForDataset(datasetIndex);
            ValueAxis rangeAxis = xyPlot.getRangeAxisForDataset(datasetIndex);

            double tx1 = domainAxis.java2DToValue(x1, screenArea, xEdge);
            double ty1 = rangeAxis.java2DToValue(y1, screenArea, yEdge);
            double tx2 = domainAxis.java2DToValue(x2, screenArea, xEdge);
            double ty2 = rangeAxis.java2DToValue(y2, screenArea, yEdge);

            XYDataset dataset = xyPlot.getDataset(datasetIndex);
            if (dataset != null) {
                // Loop through each series
                int seriesCount = dataset.getSeriesCount();
                for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                    int itemCount = dataset.getItemCount(seriesIndex);

                    // Loop through each item
                    for (int item = 0; item < itemCount; item++) {
                        double xValue = dataset.getXValue(seriesIndex, item);
                        double yValue = dataset.getYValue(seriesIndex, item);

                        // Does the data point (X, Y) lie in the hotspot (tx1 <= xValue <= tx2) (ty1 <= yValue <= ty2)
                        if ((tx1 <= xValue) && (xValue <= tx2) && (ty1 <= yValue) && (yValue <= ty2)) {
                            String tooltip = null;
                            try {
                                tooltip = xyPlot.getRenderer(datasetIndex).getToolTipGenerator(seriesIndex, item).generateToolTip(dataset, seriesIndex, item);
                            } catch (Exception ignore) {
                            }

                            return new XYItemEntity(new Rectangle(), dataset, seriesIndex, item, tooltip, null);
                        }
                    }
                }
            }
        }

        return null;
    }

    public void Create(){

        AvailableHeightGap = BuildingSize-1;
        AvailableWidthGap = BuildingSize-1;
        AvailableHeight = nbLines;
        AvailableWidth = nbColumns;

        Matrix = new int[nbLines][nbColumns];

        for(int i=1; i<=nbLines; i++){
            for(int j=1; j<=nbColumns; j++){

                if((i%BuildingSize)==0||(j%BuildingSize)==0){
                    Matrix[i-1][j-1]=0;
                }
                else{
                    Matrix[i-1][j-1]=1;
                }
            }
        }

        for(int i=0; i<nbLines; i = i + BuildingSize){
            n=i;

            while(AvailableHeightGap!=0){
                Height = (int)(BuildingHeightMeanSize+BuildingHeightStdDevSize*Math.random());
                if(AvailableHeightGap>=AvailableHeight){
                    AvailableHeightGap=AvailableHeight;
                }
                if(Height>=AvailableHeightGap){
                    Height=AvailableHeightGap;
                }
                AvailableHeightGap=AvailableHeightGap-Height;
                AvailableHeight=AvailableHeight-Height;

                for(int j=0; j<nbColumns; j = j + BuildingSize){
                    m=j;
                    while(AvailableWidthGap!=0){
                        Width=(int)(BuildingWidthMeanSize+BuildingWidthStdDevSize*Math.random());
                        Floor=(int)(2+(MaxFloor-2)*Math.random());
                        if(AvailableWidthGap>=AvailableWidth){
                            AvailableWidthGap=AvailableWidth;
                        }
                        if(Width>=AvailableWidthGap){
                            Width=AvailableWidthGap;
                        }
                        AvailableWidthGap=AvailableWidthGap-Width;
                        AvailableWidth=AvailableWidth-Width;
                        for(int k=n;k<=n+Height-1;k++){
                            for(int l=m;l<=m+Width-1;l++){
                                Matrix[k][l]=Floor;
                            }
                        }

                        m=m+Width;
                    }
                    AvailableWidthGap=BuildingSize-1;
                    AvailableWidth=AvailableWidth-1;

                }
                AvailableWidth=nbColumns;
                n=n+Height;
            }
            AvailableHeightGap=BuildingSize-1;
            AvailableHeight=AvailableHeight-1;
        }
        for (int i=0; i<nbColumns;i++){
            for(int j=0; j<nbLines;j++){
                ArrayMatrix.add(Integer.valueOf(Matrix[j][i]));
            }
        }

    }

    //private GridBagLayout GridBag = new GridBagLayout();
    //private ArrayList<Building> Cellule = new ArrayList<Building>();
    /*
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        XYPlot xyPlot = this.getChart().getXYPlot();
        //xyPlot.drawBackground((Graphics2D) g, new Rectangle2D.Double(100,100,20*zoommoi,30*zoommoi));
        String s = "test"+zoommoi+".png";
        //if(!s.equals("test1.png")) System.out.println(s);
        xyPlot.setBackgroundImage(Toolkit.getDefaultToolkit().getImage(s));
        g.setColor(Color.RED);
        g.drawString("COUCOU",100,100);
        g.drawRect(rectx,recty,100,100);

        int ii = 68;
        int jj = 13;


        for(int i=0; i<nbColumns; i++)
        {
            for(int j=0; j<nbLines; j++)
            {
                if(((double)ArrayMatrix.get(i*nbLines+j)/(double)(MaxFloor))<=0.5){
                    Blue=255-ArrayMatrix.get(i*nbLines+j)*255*2/(MaxFloor);
                    Green=ArrayMatrix.get(i*nbLines+j)*255*2/(MaxFloor);
                    Red=0;
                }
                else
                {
                    Green=255*2-ArrayMatrix.get(i*nbLines+j)*255*2/(MaxFloor);
                    Red=ArrayMatrix.get(i*nbLines+j)*255/(MaxFloor);
                    Blue=0;
                }

                //On ne peut pas dézoomer plus que la largeur et hauteur initiale, sinon écran blanc.
                //Si c'est le cas, on met zoom=1

                if(width*zoom<this.width)
                    zoom=1;
                if (height*zoom<this.height)
                    zoom=1;

                //Calcul des coordonnées x et y;

                x=(ii+i+shift_x)*(width);
                y=(jj+j+shift_y)*(height);

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
        //g.fillOval(nbColumns/2*width, nbLines/2*height, 100, 10);

       // for(int i=0; i<Transmitter.size();i++){
       //     g.drawString("ILT", (int)Transmitter.get(i).getX()*width+nbColumns/2*width-12, -(int)Transmitter.get(i).getY()*height+nbLines/2*height+6);
            //g.drawString("ILT", (int)Transmitter.get(i).getX()*width*zoom+nbColumns/2*width*zoom-12, -(int)Transmitter.get(i).getY()*height*zoom+nbLines/2*height*zoom+6);
            //System.out.println("ILT_X_Grille"+Transmitter.get(i).getX());
            //System.out.println("ILT_Y_Grille"+Transmitter.get(i).getY());

       // }
       // for(int i=0; i<Receiver.size();i++){
        //    g.drawString("VLR", (int)Receiver.get(i).getX()*width+nbColumns/2*width-12, -(int)Receiver.get(i).getY()*height+nbLines/2*height+6);
            //g.drawString("VLR", (int)Receiver.get(i).getX()*width*zoom+nbColumns*zoom/2*width*zoom-12, -(int)Receiver.get(i).getY()*height*zoom+nbLines*zoom/2*height*zoom+6);
            //System.out.println("VLR_X_Grille"+Receiver.get(i).getX());
            //System.out.println("VLR_Y_Grille"+Receiver.get(i).getY());
       // }

    }*/

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.chart != null) {
            Graphics2D g2 = (Graphics2D)g.create();
            Dimension size = this.getSize();
            Insets insets = this.getInsets();
            java.awt.geom.Rectangle2D.Double available = new java.awt.geom.Rectangle2D.Double((double)insets.left, (double)insets.top, size.getWidth() - (double)insets.left - (double)insets.right, size.getHeight() - (double)insets.top - (double)insets.bottom);
            boolean scale = false;
            double drawWidth = available.getWidth();
            double drawHeight = available.getHeight();

            System.out.println(this.getScaleX()+" "+this.getScaleY());

            g.setColor(Color.RED);
            g.drawString("COUCOU",100,100);
            g.drawRect(rectx,recty,(int)(100 * getScaleX()),(int)(100 * getScaleY()));
/*
            java.awt.geom.Rectangle2D.Double chartArea = new java.awt.geom.Rectangle2D.Double(0.0D, 0.0D, drawWidth, drawHeight);
            if(this.useBuffer) {
                if(this.chartBuffer == null || (double)this.chartBufferWidth != available.getWidth() || (double)this.chartBufferHeight != available.getHeight()) {
                    this.chartBufferWidth = (int)available.getWidth();
                    this.chartBufferHeight = (int)available.getHeight();
                    GraphicsConfiguration iterator = g2.getDeviceConfiguration();
                    this.chartBuffer = iterator.createCompatibleImage(this.chartBufferWidth, this.chartBufferHeight, 3);
                    this.refreshBuffer = true;
                }

                if(this.refreshBuffer) {
                    this.refreshBuffer = false;
                    java.awt.geom.Rectangle2D.Double iterator1 = new java.awt.geom.Rectangle2D.Double(0.0D, 0.0D, (double)this.chartBufferWidth, (double)this.chartBufferHeight);
                    Graphics2D overlay = (Graphics2D)this.chartBuffer.getGraphics();
                    Composite savedComposite = overlay.getComposite();
                    overlay.setComposite(AlphaComposite.getInstance(1, 0.0F));
                    Rectangle r = new Rectangle(0, 0, this.chartBufferWidth, this.chartBufferHeight);
                    overlay.fill(r);
                    overlay.setComposite(savedComposite);
                    if(scale) {
                        AffineTransform saved = overlay.getTransform();
                        AffineTransform st = AffineTransform.getScaleInstance(this.scaleX, this.scaleY);
                        overlay.transform(st);
                        this.chart.draw(overlay, chartArea, this.anchor, this.info);
                        overlay.setTransform(saved);
                    } else {
                        this.chart.draw(overlay, iterator1, this.anchor, this.info);
                    }
                }

                g2.drawImage(this.chartBuffer, insets.left, insets.top, this);
            } else {
                AffineTransform iterator2 = g2.getTransform();
                g2.translate(insets.left, insets.top);
                if(scale) {
                    AffineTransform overlay1 = AffineTransform.getScaleInstance(this.scaleX, this.scaleY);
                    g2.transform(overlay1);
                }


                this.chart.draw(g2, chartArea, this.anchor, this.info);
                g2.setTransform(iterator2);
            }

            Iterator iterator3 = this.overlays.iterator();

            while(iterator3.hasNext()) {
                Overlay overlay2 = (Overlay)iterator3.next();
                overlay2.paintOverlay(g2, this);
            }

            this.drawZoomRectangle(g2, !this.useBuffer);
            g2.dispose();
            this.anchor = null;
            this.verticalTraceLine = null;
            this.horizontalTraceLine = null;*/
        }
    }
}
