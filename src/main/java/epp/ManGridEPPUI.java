package epp;
import epp.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.seamcat.model.Scenario;
import org.seamcat.model.factory.Factory;
import org.seamcat.model.functions.Function;
import org.seamcat.model.functions.Point2D;
import org.seamcat.model.plugin.Config;
import org.seamcat.model.plugin.antenna.ITU_R_F1336_4_rec_2_Input;
import org.seamcat.model.plugin.eventprocessing.PanelDefinition;
import org.seamcat.model.plugin.eventprocessing.Panels;
import org.seamcat.model.plugin.eventprocessing.PostProcessing;
import org.seamcat.model.plugin.eventprocessing.PostProcessingUI;
import org.seamcat.model.plugin.propagation.P1546ver1Input;
import org.seamcat.model.simulation.result.*;
import org.seamcat.model.types.AntennaGain;
import org.seamcat.model.types.InterferenceLink;
import org.seamcat.model.types.PropagationModel;
import org.seamcat.model.types.result.Results;
import org.seamcat.model.types.result.ScatterDiagramResultType;
import org.seamcat.model.types.result.VectorResult;
import java.lang.Math;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static org.seamcat.model.factory.Factory.build;
import static org.seamcat.model.factory.Factory.results;
import static org.seamcat.model.factory.Factory.when;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RefineryUtilities;



/**
 * Created by placisadmin on 03/01/2017.
 */
public class ManGridEPPUI extends JFrame implements PostProcessingUI  {
    private JSplitPane split;
    private JPanel plotPanel = new JPanel(new BorderLayout());
    private GridBagLayout gridBagLayout = new GridBagLayout();
    private JLabel gridLabel = new JLabel("Manhattan Grid");
    private JLabel gridLegend = new JLabel("Number of floors");
    private Panels panels;
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


    private ManGridEPPUIinput inputPanel;

    private double[] vectorVLR_X;
    private double[] vectorVLR_Y;
    private double[] vectorVLT_X;
    private double[] vectorVLT_Y;
    private double[] vectorILR_X;
    private double[] vectorILR_Y;
    private double[] vectorILT_X;
    private double[] vectorILT_Y;
    private double maxX,minX;
    private double maxY,minY;
    private double x_size, y_size;
    private int nbpixelX=600;
    private int nbpixelY=270;


    private int nb_building=405;
    private int nbLines = 200;
    private int nbColumns = 200;
    private int maxFloor = 10;
    private int[][] Matrix = new int[nbLines][nbColumns];
    private int[][] Matrix2=new int[nbpixelX][nbpixelY];
    private ArrayList<String> blocks= new ArrayList<String>();



    public String getTitle() {
        return "ManGridEPPUI";
    }

    public void buildUI(Scenario scenario, JPanel canvas, Panels panels) {
        this.panels = panels;
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.add(panels.get("Input").getPanel());
        split.add(plotPanel);
        canvas.setLayout( new BorderLayout() );
        canvas.add( split, BorderLayout.CENTER );
    }

    public PanelDefinition[] panelDefinitions() {
        return new PanelDefinition[]{
                new PanelDefinition("Input",ManGridEPPUIinput.class)
        };
    }
    //Search the max of a table
    public double getMaxTab(double[] tab)
    {
        double max = tab[0];

        for(int i=0; i<tab.length; i++)
        {
            if(tab[i]>max)
                max=tab[i];
        }
        return max;

    }
    //Search the min of a table
    public double getMinTab(double[] tab)
    {
        double min = tab[0];

        for(int i=0; i<tab.length; i++)
        {
            if(tab[i]<min)
                min=tab[i];
        }
        return min;

    }

    @PostProcessing(order = 2, name = "Plot pattern")
    public void preparePlot(Scenario scenario,Results results, ManGridEPP.Input input) {
        //Use the ManGridEPP results
        VectorResult posVLR_X = results.findVector(input.VLRX).getValue();
        VectorResult posVLR_Y = results.findVector(input.VLRY).getValue();
        VectorResult posVLT_X = results.findVector(input.VLTX).getValue();
        VectorResult posVLT_Y = results.findVector(input.VLTY).getValue();
        VectorResult posILR_X = results.findVector(input.ILRX).getValue();
        VectorResult posILR_Y = results.findVector(input.ILRY).getValue();
        VectorResult posILT_X = results.findVector(input.ILTX).getValue();
        VectorResult posILT_Y = results.findVector(input.ILTY).getValue();

        vectorVLR_X = posVLR_X.asArray();
        vectorVLR_Y = posVLR_Y.asArray();
        vectorVLT_X = posVLT_X.asArray();
        vectorVLT_Y = posVLT_Y.asArray();
        vectorILR_X= posILR_X.asArray();
        vectorILR_Y = posILR_Y.asArray();
        vectorILT_X= posILT_X.asArray();
        vectorILT_Y= posILT_Y.asArray();



        double maxVLRX=getMaxTab(vectorVLR_X);
        double maxVLTX=getMaxTab(vectorVLT_X);
        double maxILTX=getMaxTab(vectorILT_X);
        double maxILRX=getMaxTab(vectorILR_X);
        double[] tabX={maxILRX,maxILTX,maxVLRX,maxVLTX};
        maxX= getMaxTab(tabX);

        double maxVLRY=getMaxTab(vectorVLR_Y);
        double maxVLTY=getMaxTab(vectorVLT_Y);
        double maxILTY=getMaxTab(vectorILT_Y);
        double maxILRY=getMaxTab(vectorILR_Y);
        double[] tabY={maxILRY,maxILTY,maxVLRY,maxVLTY};
        maxY= getMaxTab(tabY);

        double minVLRX=getMinTab(vectorVLR_X);
        double minVLTX=getMinTab(vectorVLT_X);
        double minILTX=getMinTab(vectorILT_X);
        double minILRX=getMinTab(vectorILR_X);
        double[] tabminX={minILRX,minILTX,minVLRX,minVLTX};
        minX= getMinTab(tabminX);

        double minVLRY=getMinTab(vectorVLR_Y);
        double minVLTY=getMinTab(vectorVLT_Y);
        double minILTY=getMinTab(vectorILT_Y);
        double minILRY=getMinTab(vectorILR_Y);
        double[] tabminY={minILRY,minILTY,minVLRY,minVLTY};
        minY= getMinTab(tabminY);

         x_size=(Math.abs(minX)+maxX)*1.1;
         y_size=(Math.abs(minY)+maxX)*1.1;

        inputPanel = (ManGridEPPUIinput) panels.get("Input").getModel();


        nb_building=inputPanel.nb_building();

        dothis(scenario);
    }

    private void dothis(Scenario scenario) {
        generatePlot();
    }


    private void generatePlot() {
        int red, green, blue;
        plotPanel.removeAll();
        XYSeriesCollection dataset = createDataset();
        String plane = "horizontal";
        XYSeries pattern = new XYSeries(plane);


        creatematrix();
        plotPanel.setLayout(gridBagLayout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = nbColumns;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        plotPanel.add(gridLabel,gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        plotPanel.add(gridLegend,gbc);

        gbc.gridwidth = 1;
        gbc.gridheight = 100;
        gbc.ipady = 0;
        gbc.ipadx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;

        NumberAxis xAxis=new NumberAxis("X Distance (km)");
        NumberAxis yAxis=new NumberAxis("Y Distance (km)");
        XYItemRenderer itemrenderer = new XYLineAndShapeRenderer(true,false);

        final XYPlotWithZoomableBackgroundImage myplot = new XYPlotWithZoomableBackgroundImage((XYDataset)dataset, xAxis, yAxis, itemrenderer, true, minX, minY, maxX,maxY);// final
        JFreeChart chart = new JFreeChart("Manhattan Grid for SEAMCAT", JFreeChart.DEFAULT_TITLE_FONT, myplot, true);
        mynewxyplot chartPanel = new mynewxyplot(chart, Matrix, nbLines, nbColumns, maxFloor, nb_building);

        plotPanel.add(chartPanel,gbc);
        plotPanel.revalidate();
        plotPanel.repaint();

        gbc.gridheight = 1;

        for(int i=0; i<100; i++){
            gbc.gridy = i+1;
            gbc.gridx = 1;
            gbc.insets = new Insets(0, 40, 0, 0);
            if(i<=100/2){
                red=255-i*255*2/100;
                green=i*255*2/100;
                blue=0;
            }
            else{
                green=255*2-i*255*2/100;
                blue=i*255/100;
                red=0;
            }
            JPanel panel = new JPanel();
            panel.setMaximumSize(new Dimension(50,50/(maxFloor+1)));
            panel.setMinimumSize(new Dimension(50,50/(maxFloor+1)));
            panel.setPreferredSize(new Dimension(50,50/(maxFloor+1)));
            panel.setBackground(new Color(red,green,blue));
            plotPanel.add(panel, gbc);
        }

        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.gridheight = 2;

        gbc.gridheight = 1;
        for(int i=0; i<=maxFloor; i++){
            gbc.gridx = 2;
            gbc.gridy = i*100/maxFloor+1;
            plotPanel.add(new JLabel(String.valueOf(maxFloor-i)), gbc);
        }



    }

    private XYSeriesCollection createDataset()
    {
        int length_VLRX = vectorVLR_X.length;
        System.out.println("nb of events: "+length_VLRX);

        final XYSeries VLR = new XYSeries("VLR");
        final XYSeries ILR = new XYSeries("ILR");
        final XYSeries VLT = new XYSeries("VLT");
        final XYSeries ILT = new XYSeries("ILT");

        for(int numevent=0;numevent<length_VLRX;numevent++)
        {
            VLR.add(vectorVLR_X[numevent],vectorVLR_Y[numevent]);
            VLT.add(vectorVLT_X[numevent],vectorVLT_Y[numevent]);
            ILR.add(vectorILR_X[numevent],vectorILR_Y[numevent]);
            ILT.add(vectorILT_X[numevent],vectorILT_Y[numevent]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ILT);
        dataset.addSeries(VLT);
        dataset.addSeries(ILR);
        dataset.addSeries(VLR);

        return dataset;

    }
    public void creatematrix() {
        AvailableHeightGap = BuildingSize - 1;
        AvailableWidthGap = BuildingSize - 1;
        AvailableHeight = nbLines;
        AvailableWidth = nbColumns;



        Matrix = new int[nbLines][nbColumns];

        for (int i = 0; i <= nbLines - 1; i++) {
            for (int j = 0; j <= nbColumns - 1; j++) {

                if ((i % BuildingSize) == 0 || (j % BuildingSize) == 0) {
                    Matrix[i][j] = 0;//contient les coordonnées des rues
                } else {
                    Matrix[i][j] = 1;
                }
            }
        }


            for (int i = 0; i < nbLines; i = i + BuildingSize) {
                n = i;

                while (AvailableHeightGap != 0) {
                    Height = (int) (BuildingHeightMeanSize + BuildingHeightStdDevSize * Math.random());
                    if (AvailableHeightGap >= AvailableHeight) {
                        AvailableHeightGap = AvailableHeight;
                    }
                    if (Height >= AvailableHeightGap) {
                        Height = AvailableHeightGap;
                    }
                    AvailableHeightGap = AvailableHeightGap - Height;
                    AvailableHeight = AvailableHeight - Height;

                    for (int j = 0; j < nbColumns; j = j + BuildingSize) {
                        m = j;
                        while (AvailableWidthGap != 0) {
                            Width = (int) (BuildingWidthMeanSize + BuildingWidthStdDevSize * Math.random());
                            Floor = (int) (2 + (MaxFloor - 2) * Math.random());
                            if (AvailableWidthGap >= AvailableWidth) {
                                AvailableWidthGap = AvailableWidth;
                            }
                            if (Width >= AvailableWidthGap) {
                                Width = AvailableWidthGap;
                            }
                            AvailableWidthGap = AvailableWidthGap - Width;
                            AvailableWidth = AvailableWidth - Width;
                            for (int k = n; k <= n + Height - 1; k++) {
                                for (int l = m; l <= m + Width - 1; l++)
                                    Matrix[k][l] = Floor;
                            }

                            m = m + Width;
                        }
                        AvailableWidthGap = BuildingSize - 1;
                        AvailableWidth = AvailableWidth - 1;

                    }
                    AvailableWidth = nbColumns;
                    n = n + Height;
                }
                AvailableHeightGap = BuildingSize - 1;
                AvailableHeight = AvailableHeight - 1;
            }


        System.out.println("GapHeath"+AvailableHeightGap+"GapWidth"+AvailableWidthGap);
        System.out.println("Heath"+AvailableHeight+"Width"+AvailableWidth);
    }

/*
private void grid_creation(){
    //Numbers of pixel for each groups( blocks, roads...)
     x_size=(Math.abs(minX)+maxX)*1.1;
     y_size=(Math.abs(minY)+maxX)*1.1;
    System.out.println("xsize"+x_size);
    System.out.println("ysize"+y_size);
    //input = (ManGridEPPUIinput) panels.get("Input").getModel();
     Xonepix= x_size/nbpixelX;
     Yonepix= y_size/nbpixelY;
    System.out.println("Xonepixel"+Xonepix);
    System.out.println("Yonepixel"+Yonepix);

    a=0;
    b=0;
    //a is used for the x label
    //b is used for the y label
   //road_width=inputPanel.road_width()/1000;
    road_width=0.03;
    System.out.println("rue"+road_width);

    road_nb_pixel_x=(int) Math.round(road_width/Xonepix);
    road_nb_pixel_y=(int) Math.round(road_width/Yonepix);
     //nb_blocks=inputPanel.nb_blocks();
    nb_blocks=3;
    System.out.println("nb blockd"+nb_blocks);
    block_size_x= (x_size-(nb_blocks-1)*(road_width))/nb_blocks;
    System.out.println("block size"+block_size_x);
    //road_width divise by 1000 because the road is in meter
    block_size_y= (y_size-(nb_blocks-1)*road_width)/nb_blocks;

     block_nb_pixel_x= (int) Math.round(block_size_x/Xonepix);
     block_nb_pixel_y= (int) Math.round(block_size_y/Yonepix);
    System.out.println("nb pixel block x"+block_nb_pixel_x);
    System.out.println("nb pixel block y"+block_nb_pixel_y);
    System.out.println("nb pixel road x"+road_nb_pixel_x);
    System.out.println("nb pixel road y"+road_nb_pixel_y);
    pixelVLTX= (int) Math.round(nbpixelX/x_size*(Math.abs(minX)/1.1));
    pixelVLTY= (int) Math.round(nbpixelY/y_size*(Math.abs(minY)/1.1));
    // System.out.println("maxX"+maxX+"maxY"+maxY);
    System.out.println("pixel numéro pour x"+pixelVLTX);
    System.out.println("pixel numéro pour y"+pixelVLTY);
    Matrix2 = new int[nbpixelX][nbpixelY];
    boolean indoor;

    //
    //if(inputPanel.vLT_position()== ManGridEPPUIinput.VLT_position.Indoor) {
    if(indoor= true){
        System.out.println("Indoor");
        //Begin on the left down of the block
        a = pixelVLTX - block_nb_pixel_x / 2;
        System.out.println("a"+a);
        b = pixelVLTY - block_nb_pixel_y / 2;
        //System.out.println(Matrix2);

       for (int l=b;l<nbpixelY;l++) {

               //the next block copy a line with block and road on a block y
               for(int lg=l; lg<= l+block_nb_pixel_y;lg++) {
                    if(lg<nbpixelY) {
                        // complete a line
                        while (a < nbpixelX) {
                            a = fillblock_x_right(Matrix2, a, lg, block_nb_pixel_x);
                            a = fillroad_x_right(Matrix2, a, lg, road_nb_pixel_x);
                        }
                        a = pixelVLTX - block_nb_pixel_x / 2;
                        while (a >= 0) {
                            a = fillroad_x_left(Matrix2, a, lg, road_nb_pixel_x);
                            a = fillblock_x_left(Matrix2, a, lg, block_nb_pixel_x);
                        }
                    }
               }
               if(l+block_nb_pixel_y<nbpixelY) {
                   l = l + block_nb_pixel_y;
               }
               else{
                   l=nbpixelY-1;
               }
           //fill the road
            for(int j = l;j<=l+road_nb_pixel_y;j++){
                if(j<nbpixelY) {
                    for (int i = 0; i < nbpixelX; i++)
                        Matrix2[i][j] = 0;
                }
            }
           if(l+road_nb_pixel_y<nbpixelY) {
               l=l+road_nb_pixel_y;
           }
           else{
               l=nbpixelY-1;
           }

        }

        a = pixelVLTX - block_nb_pixel_x / 2;
        b = pixelVLTY - block_nb_pixel_y / 2;

        for(int k=b-1;k>= 0;k--) {
            //this for create the road
            for(int j = k;j<=k-road_nb_pixel_y;j--){
                if(j>=0) {
                    for (int i = 0; i < nbpixelX; i++)
                        Matrix2[i][j] = 0;
                }
            }
            if(k-road_nb_pixel_y>0)
            {
            k=k-road_nb_pixel_y;}
            else
                k=0;
            //the next block copy a line on a block size
            for(int lg=k; lg<= k+block_nb_pixel_y;lg++) {
                if(lg<nbpixelY) {
                    // complete a line
                    while (a < nbpixelX) {
                        a = fillblock_x_right(Matrix2, a, lg, block_nb_pixel_x);
                        a = fillroad_x_right(Matrix2, a, lg, road_nb_pixel_x);
                    }
                    a = pixelVLTX - block_nb_pixel_x / 2;
                    while (a >= 0) {
                        a = fillroad_x_left(Matrix2, a, lg, road_nb_pixel_x);
                        a = fillblock_x_left(Matrix2, a, lg, block_nb_pixel_x);
                    }
                }
            }
            if(k-block_nb_pixel_y>0)
            {
                k=k-block_nb_pixel_y-1;}
            else
                k=0;

        }

    }
    else {
        System.out.println("Outdoor");
        //Begin also on the left down of the block
        a = pixelVLTX + road_nb_pixel_x / 2;
        b = pixelVLTY - road_nb_pixel_y / 2;

        for (int lgo = b; lgo < nbpixelY; lgo++) {

            //the next block copy a lign with block and road on a block y
            for (lgo = b; lgo <= b + block_nb_pixel_y; lgo++) {
                if (lgo < nbpixelY) {
                    // complete a lign
                    while (a < nbpixelX) {
                        a = fillblock_x_right(Matrix2, a, lgo, block_nb_pixel_x);
                        a = fillroad_x_right(Matrix2, a, lgo, road_nb_pixel_x);
                    }
                    while (a >= 0) {
                        a = fillroad_x_left(Matrix2, a, lgo, road_nb_pixel_x);
                        a = fillblock_x_left(Matrix2, a, lgo, block_nb_pixel_x);
                    }
                }
            }
            if (b + block_nb_pixel_y < nbpixelY) {
                b = b + block_nb_pixel_y;
            } else {
                b = nbpixelY-1;
            }
            //fill the road
            for (int j = b; j <= b + road_nb_pixel_y; j++) {
                if (j < nbpixelY) {
                    for (int i = 0; i <nbpixelX; i++)
                        Matrix2[i][j] = 0;
                }
            }
            if (b + road_nb_pixel_y <nbpixelY) {
                b = b + road_nb_pixel_y;
            } else {
                b = nbpixelY-1;
            }

        }
        a = pixelVLTX + road_nb_pixel_x / 2;
        b = pixelVLTY - road_nb_pixel_y / 2;

        for (int k = b - 1; k >= 0; k--) {
            //this for create the road
            for (int j = b - 1; j <= b - road_nb_pixel_y; j--) {
                if (j >= 0) {
                    for (int i = 0; i < nbpixelX; i++)
                        Matrix2[i][j] = 0;
                }
            }
            if (b - 1 - road_nb_pixel_y > 0) {
                b = b - road_nb_pixel_y - 1;
            } else
                k = 0;
            //the next block copy a lign on a block size
            while (k >= b - block_nb_pixel_y - 1) {
                // complete a lign
                if (k > 0) {
                    while (a < nbpixelX) {
                        a = fillblock_x_right(Matrix2, a, k, block_nb_pixel_x);
                        a = fillroad_x_right(Matrix2, a, k, road_nb_pixel_x);
                    }
                    while (a >= 0) {
                        a = fillroad_x_left(Matrix2, a, k, road_nb_pixel_x);
                        a = fillblock_x_left(Matrix2, a, k, block_nb_pixel_x);
                    }
                }
            }
            if (b - 1 - block_nb_pixel_y > 0) {
                b = b - block_nb_pixel_y - 1;
            } else
                k = 0;

        }
    }

    for(int j=0;j<nbpixelY;j++){
        for(int i=0;i<nbpixelX;i++){
            System.out.print(Matrix2[i][j]+" ");
        }
        System.out.println("");
    }
    }

private int fillblock_x_right(int[][] Matrix,int x_pixel,int y_pixel,int block_pixel_size_x)
{
    for(int i=x_pixel;i<x_pixel+block_pixel_size_x;i++) {
        if (i < nbpixelX) {
            //System.out.println ("i="+i+" y_pixel"+y_pixel);
            Matrix[i][y_pixel] = 1;

        }

    }
    if(x_pixel+block_pixel_size_x<nbpixelX)
        return x_pixel+block_pixel_size_x;
    else
        return nbpixelX-1;

}

private int fillblock_x_left(int[][] Matrix,int x_pixel,int y_pixel,int block_pixel_size_x) {
    for (int i = x_pixel; i < x_pixel - block_pixel_size_x; i--) {
        if (i >= 0)
            Matrix[i][y_pixel] = 1;
    }
    if(x_pixel-block_pixel_size_x>0)
        return x_pixel-block_pixel_size_x;
    else
        return 0;

    }

    private int fillroad_x_right(int[][] Matrix,int x_pixel,int y_pixel,int road_pixel_size_x)
    {

        for(int i=x_pixel;i<x_pixel+road_pixel_size_x;i++)
        {
            if(i<nbpixelX)
                Matrix[i][y_pixel] = 0;
        }
        if(x_pixel-road_pixel_size_x<nbpixelX)
            return x_pixel+road_pixel_size_x;
        else
            return nbpixelX-1;
    }

    private int fillroad_x_left(int[][] Matrix,int x_pixel,int y_pixel,int road_pixel_size_x)
    {
        for(int i=x_pixel;i<x_pixel-road_pixel_size_x;i--) {
            if(i>=0)
                Matrix[i][y_pixel] = 0;
        }
        if(x_pixel-road_pixel_size_x>0)
        return x_pixel-road_pixel_size_x;
        else
            return 0;
    }
*/

}
