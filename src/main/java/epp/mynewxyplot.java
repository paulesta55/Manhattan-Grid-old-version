package epp;

/**
 * Created by placisadmin on 28/02/2017.
 */
import epp.MMap;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import java.awt.*;

/**
 * Created by placisadmin on 06/12/2016.
 */
import org.jfree.chart.*;

import java.awt.RenderingHints;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Locale;
import java.util.List;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.seamcat.model.Scenario;

public class mynewxyplot extends ChartPanel
{

    private MMap map;



    public mynewxyplot(JFreeChart chart, int matrix[][], int nbLines, int nbColums, int MaxFloor, int nb_building)
    {
        this(chart,matrix, nbLines, nbColums, MaxFloor,nb_building,
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



    }

    public mynewxyplot(JFreeChart chart, boolean useBuffer,int matrix[][], int nbLines, int nbColums, int MaxFloor, int nb_building)
    {
        this(chart,matrix,  nbLines, nbColums, MaxFloor,nb_building,
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
    }

    public mynewxyplot(JFreeChart chart,int matrix[][], int nbLines, int nbColums, int MaxFloor,int nb_building,
                       int width, int height,
                       int minimumDrawWidth, int minimumDrawHeight,
                       int maximumDrawWidth, int maximumDrawHeight,
                       boolean useBuffer,
                       boolean properties, boolean save, boolean print, boolean zoom,
                       boolean tooltips)
    {
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
                tooltips);

        map=new MMap(matrix,  nbLines, nbColums, MaxFloor);
        map.setVisible(true);

        // Disable inefficient EntityCollection
        this.getChartRenderingInfo().setEntityCollection(null);
        XYPlotWithZoomableBackgroundImage myplot = (XYPlotWithZoomableBackgroundImage) chart.getPlot();


        map.repaint();
        System.out.println("image size = " + map.getSize());

        /*Snapshot!!!!!*/
        myplot.setBackgroundImage(map.takeSnapShot(nb_building,nb_building));

        //Add all Transmitters,Receivers,Interference Links
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false,true); //We want No lines but shapes

        //Set the color of renderers
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.ORANGE);
        renderer.setSeriesPaint(3, Color.BLACK);
        myplot.setRenderer(renderer);

        //set the preferred size(initial size) of the chart
        this.setPreferredSize(new Dimension(600,270));//600 et 270
        //this.setMaximumSize(new Dimension(600, 100));
        this.setVerticalAxisTrace(false);
        this.setHorizontalAxisTrace(false);

        //Set the initial chart Range
        //myplot.getDomainAxis().setRange(-4.0,4.0);
        //myplot.getRangeAxis().setRange(-4.0,4.0);
    }
}
