package epp;

/**
 * Created by placisadmin on 28/02/2017.
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.io.File;

/**
 * Created by NingD on 2016/2/19.
 */
public class ManhattanChart extends JPanel
{
    private static final ResourceBundle STRINGLIST = ResourceBundle.getBundle("stringlist", Locale.ENGLISH);

    private final DecimalFormat formatter;
    private final JFreeChart chart;
    private final LightweightChartPanel chartPanel;
    private JPanel charts;
    private JPanel DataPanel;

    //private EventStatisticsPanel eventStatsPanel;
    //private EventStatusPanel eventStatusPanel;
    //private int eventsToBeCalculated;
    //private JSplitPane labelsPanel;

    //private int maxEventsStep = 1000;
    //private int maxEventsToPlot;

    //private int percentageFactor;

    /*public static final String DRSS     = "dRSS";
    public static final String UNWANTED = "iRSS Unwanted";
    public static final String BLOCKING = "iRSS Blocking";
    public static final String SELECTIVITY = "iRSS Selectivity";*/

    //SeamcatDistributionPlotRssPanel rssPanel = new SeamcatDistributionPlotRssPanel();

    //private Workspace workspace;
    private ScenarioOutlineModel scenarioOutlineModel = new ScenarioOutlineModel();

    private JSplitPane splitPaneH;

    public ManhattanChart()
    {
        super(true);

        formatter = new DecimalFormat();
        formatter.setMinimumFractionDigits(1);
        formatter.setMaximumFractionDigits(3);
        formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        /*this.workspace = workspace;
        setLinkTitles();
        eventStatsPanel = new EventStatisticsPanel(workspace);
        initComponents();
        maxEventsToPlot = maxEventsStep;*/

        chart = ChartFactory.createScatterPlot(STRINGLIST.getString("SCENARIO_PLOT_TITLE"), STRINGLIST.getString("SCENARIO_PLOT_AXIX_TITLE_X"), STRINGLIST.getString("SCENARIO_PLOT_AXIX_TITLE_Y"), scenarioOutlineModel, PlotOrientation.VERTICAL, true, true, false);

        setToolTipGenerator();
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        System.out.println((new File("")).getAbsolutePath());
        xyPlot.setBackgroundImage(Toolkit.getDefaultToolkit().getImage("test1.png"));
        xyPlot.getRenderer().setSeriesPaint(3, new Color(255, 204, 51));

        xyPlot.setRangeCrosshairVisible(true);
        xyPlot.setDomainCrosshairVisible(true);

        NumberAxis domainAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
        domainAxis.setAutoRangeStickyZero(true);
        domainAxis.setAutoRangeIncludesZero(true);

        //chartPanel = new org.seamcat.epp.LightweightChartPanel(chart,grid);
        chartPanel = new epp.LightweightChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 500));
        chartPanel.setVerticalAxisTrace(false);
        chartPanel.setHorizontalAxisTrace(false);

        Font f = new Font(this.getFont().getName(), this.getFont().getStyle(), 10);

        //DiscreteFunctionGraph.applyStyles(chartPanel, f, true);

        charts = new JPanel(new BorderLayout());

        charts.add(chartPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new BorderPanel(charts, STRINGLIST.getString("SCENARIO_TITLE")), BorderLayout.CENTER);

        splitPaneH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPaneH.setLeftComponent(centerPanel);
        //splitPaneH.setRightComponent(rssPanel);

        //add(labelsPanel, BorderLayout.NORTH);
        add(splitPaneH, BorderLayout.CENTER);

        splitPaneH.setDividerLocation( 800 );
        addComponentListener( new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPaneH.setDividerLocation( 0.5 );
            }
        });
        //setModel( workspace.getScenarioOutlineModel() );
    }

    private void setToolTipGenerator() {
        final DecimalFormat formatter = new DecimalFormat();
        formatter.setMinimumFractionDigits(1);
        formatter.setMaximumFractionDigits(3);
        formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        XYToolTipGenerator toolTipGenerator = new XYToolTipGenerator() {

            public String generateToolTip(XYDataset xyDataset, int series, int item) {
                StringBuilder sb = new StringBuilder();
                String customText = "";

                String seriesString = xyDataset.getSeriesKey(series).toString();
                seriesString = seriesString.replaceAll("<", "&lt;");
                seriesString = seriesString.replaceAll(">", "&gt;");

                Number x = xyDataset.getX(series, item);
                Number y = xyDataset.getY(series, item);

                XYSeries xySeries = scenarioOutlineModel.getSeries(series);
                if (xySeries instanceof ExtendableXYSeries) {
                    ExtendableXYSeries xySeries2 = (ExtendableXYSeries) xySeries;
                    java.util.List<Argument> args = xySeries2.getArgsForPoint(x, y);
                    if (args != null) {
                        // customText = argumentToHTMLTable(args);
                    }
                }

                sb.append("<html><body>").append(seriesString).append(" (");
                sb.append(formatter.format(x.doubleValue())).append(", ");
                sb.append(formatter.format(y.doubleValue()));
                sb.append(") ").append(customText).append("</body></html>");

                return sb.toString();
            }
        };

        chart.getXYPlot().getRenderer().setBaseToolTipGenerator(toolTipGenerator);
    }
}

