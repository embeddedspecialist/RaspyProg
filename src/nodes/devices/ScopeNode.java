/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.devices;

import java.awt.BasicStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import nodes.AFONodeWidget;
import nodes.CommonDefinitions;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import sceneManager.AFOGraphPinScene;
import sceneManager.AFOPin;

/**
 *
 * @author amirrix
 */
public class ScopeNode extends GenericNode implements ActionListener {

    protected TimeSeriesCollection graphDataSet;
    protected TimeSeries graphSeries;
    protected JFreeChart myChart;
    protected ChartFrame myChartFrame;


    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ScopeNode(String id, String label, AFOGraphPinScene scene) {
        super(id, label, scene);

        CreateWidget();

        blockType = CommonDefinitions.blockTypes.Scope;
        CreatePropertiesDialog();

        //Preparo per i grafici
        graphSeries = new TimeSeries(comment);
        graphDataSet = new TimeSeriesCollection(graphSeries);

        myChart = ChartFactory.createTimeSeriesChart(comment, "Time", "IN",
                graphDataSet, true, true, false);
        XYPlot plot = myChart.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 35.0);
        axis.setAutoRange(false);
        StandardXYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        plot.setRenderer(0, renderer);

        myChartFrame = new ChartFrame(comment, myChart);
        myChartFrame.pack();
    }

    private void CreateWidget() {
        widget = new AFONodeWidget(getScene());

        ((AFONodeWidget)widget).setName("Scope");

        //Aggiungo alla scena:
        scene.addNode(this);

        String pinLabel = "IN";
        String pinID = "pin"+ ++scene.pinIDCounter;
        AFOPin newPin = new AFOPin(pinID, pinLabel,  AFOPin.E_PinType.PIN_INPUT);
        newPin.setAsBusPin(false);
        newPin.setWidget(((AFONodeWidget)widget).addPin(newPin));
        newPin.setPinIDString("IN1");
        getScene().addPin(this, newPin);
        pinList.add(newPin);

        ((AFONodeWidget)widget).getModuleComment().getActions ().addAction (editorAction);

        
    }

    public void addValue(String value){
        try {
            float val;

            setPinValue("IN1", value);
            
            //Aggiorno il grafico solo se visibile
            if (!myChartFrame.isVisible()) {
                return;
            }
            val = Float.parseFloat(value);
            Second now = new Second();
            graphSeries.addOrUpdate(now, val);
            
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }
    }

    private void CreatePropertiesDialog() {
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void showChartFrame(){
        if (myChartFrame.isVisible()){
            myChartFrame.setVisible(false);
        }
        else {
            myChart.setTitle(comment);
            myChartFrame.setVisible(true);
        }
    }

}
