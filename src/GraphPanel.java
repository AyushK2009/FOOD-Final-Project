import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;

/*
The Geometry of the Graphs:
There are many distance variables in this class used to properly format the graphs.
The class works like a coordinate system where the top left is (0,0): x increases to the right and y increases downward (semi-inconvenient for plotting)

- The 4 margins (left/right/top/bottom) space out each subplot so labels and tick numbers have room and don't overlap on the actual graph
- The title space is the part always assigned to the top of the window for the overall title.
- The width and height are the full panel's total pixel size (from getWidth() and getHeight()).
- The subplot height is how tall each of the three stage plots is: (height - titleSpace) / 3.
- The Box variables (boxX, boxY, boxW, boxH) describe the location of a subplot's outer rectangle with respect to the rest of the panel
- plotLeft, plotRight, plotTop, plotBottom describe the inner rectangle of a subplot, derived by subtracting the margins from the overarching box lengths
- plotW and plotH are the width and height of the inner plot rectangle.

For example, plotLeft = boxX + leftMargin shifts the plot's x-pos from the box by leftMargin pixels, which leaves room for the y-axis pixels

Mini diagram of one subplot box:

  (boxX, boxY)
  v
  +--------+--------------------------+-------+  ^
  |        | topMargin (subplot title)|       |  |
  | left   +--------------------------+ right |  |
  | margin |                          | margin|  |
  |        |  INNER PLOT AREA         |       | boxH
  |        |  plotW x plotH           |       |  |
  |        |  (where data go)         |       |  |
  |        +--------------------------+       |  |
  |        |  bottomMargin (x labels) |       |  |
  +--------+--------------------------+-------+  v
  <------------------- boxW ------------------>
                                              ^
                                  (boxX+boxW, boxY+boxH)
 */
public class GraphPanel extends JPanel {

    // Pixel padding that leaves space for axis labeling. 

    private static final int leftMargin = 80;     // used for y-axis tick numbers
    private static final int rightMargin = 30;    // make sure the graph doesn't run off screen
    private static final int topMargin = 35;      // above plot for the sub-plot title and to leave space between plots
    private static final int bottomMargin = 50;   // below plot for x-axis labels and to leave space between plots
    private static final int titleSpace = 25;     // very top of the window for the overall title

    // Number of intervals between tick marks on each axis
    private static final int numTicks = 5;

    // Data collected from simulation
    private ArrayList<Double> times;        
    private ArrayList<Double> positions;   
    private double displayScale;            
    private int stage1to2Index;             
    private int stage2to3Index;             
    private States.Planet origin;          
    private States.Planet destination;      

    public GraphPanel(ArrayList<Double> times, ArrayList<Double> positions, double displayScale,
                      int stage1to2Index, int stage2to3Index,
                      States.Planet origin, States.Planet destination) {
        this.times = times;
        this.positions = positions;
        this.displayScale = displayScale;
        this.stage1to2Index = stage1to2Index;
        this.stage2to3Index = stage2to3Index;
        this.origin = origin;
        this.destination = destination;
    }

    // Swing calls this every time it needs to redraw the panel.
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        // Space necessary for 3 subplots, not including title
        int subplotHeight = (height - titleSpace) / 3;

        // Title
        g.setColor(Color.BLACK);
        g.drawString("Rocket Trajectory: " + origin + " to " + destination, width / 2 - 80, 18);

        // Takeoff Graph uses data from index 0 till where stage 2 ends 
        int stage1End;
        if (stage1to2Index >= 0) {
            stage1End = stage1to2Index;
        } else {
            stage1End = times.size();
        }
        drawStagePlot(g, "Stage 1: Takeoff",
                      0, stage1End,
                      0, titleSpace, width, subplotHeight);

        // Cruise Graph uses data from end of stage 1 till where stage 2 ends. If stage 2 never happned(crash), no graph is plotted
        if (stage1to2Index >= 0) { // If rocket didn't crash
            int stage2End;
            if (stage2to3Index >= 0) { // If rocket completed stage 2
                stage2End = stage2to3Index;
            } else { // Plot whatever is in stage 2 even if not completed
                stage2End = times.size();
            }
            drawStagePlot(g, "Stage 2: Cruise",
                          stage1to2Index, stage2End,
                          0, titleSpace + subplotHeight, width, subplotHeight);
        } else {
            drawEmptyMessage(g, "Stage 2: Cruise (not reached)",
                             0, titleSpace + subplotHeight, width, subplotHeight);
        }

        // Landing Graph uses data from end of stage 2 till where stage 3 ends.
        if (stage2to3Index >= 0) {
            drawStagePlot(g, "Stage 3: Landing",
                          stage2to3Index, times.size(),
                          0, titleSpace + 2 * subplotHeight, width, subplotHeight);
        } else {
            drawEmptyMessage(g, "Stage 3: Landing (not reached)",
                             0, titleSpace + 2 * subplotHeight, width, subplotHeight);
        }
    }

    // Draws one sub-plot inside the rectangle defined by the box variables X, Y, W, and H
    private void drawStagePlot(Graphics g, String stageLabel, int startIdx, int endIdx,
                               int boxX, int boxY, int boxW, int boxH) {

        // If there's no data, don't draw a graph
        if (startIdx >= endIdx) {
            drawEmptyMessage(g, stageLabel + " (no data)", boxX, boxY, boxW, boxH);
            return;
        }

        // Find the region where the actual data will be graphed (ignoring spacing margins and room for labels and axes)
        int plotLeft = boxX + leftMargin;
        int plotRight = boxX + boxW - rightMargin;
        int plotTop = boxY + topMargin;
        int plotBottom = boxY + boxH - bottomMargin;
        int plotW = plotRight - plotLeft;
        int plotH = plotBottom - plotTop;

        // Find the min and max (t, pos) so the graph fills the entire box
        double minTime = times.get(startIdx) * displayScale;
        double maxTime = times.get(endIdx - 1) * displayScale;
        double minPos  = positions.get(startIdx) * displayScale;
        double maxPos  = positions.get(endIdx - 1) * displayScale;
        double timeRange = maxTime - minTime;
        double posRange  = maxPos - minPos;
        
        if (timeRange == 0) timeRange = 1;
        if (posRange == 0) posRange = 1;

        // Sub-plot title
        g.setColor(Color.BLACK);
        g.drawString(stageLabel, plotLeft, boxY + 14);

        // x-axis and y-axis label
        g.drawString("Position (m)", plotLeft - 55, plotTop - 8);
        g.drawString("Time (s)", plotLeft + plotW / 2 - 20, plotBottom + 35);

        // x-axis and y-axis lines
        g.drawLine(plotLeft, plotBottom, plotRight, plotBottom);
        g.drawLine(plotLeft, plotTop, plotLeft, plotBottom);

        // Tick marks and numbers
        for (int i = 0; i <= numTicks; i++) {
            double frac = (double) i / numTicks;  // 0.0, 0.2, 0.4, 0.6, 0.8, 1.0

            // x-axis tick 
            int x = plotLeft + (int) (frac * plotW);
            g.drawLine(x, plotBottom, x, plotBottom + 5);

            // Use formatNumber() to show a number like 31.1k instead of 31066
            String xLabel = formatNumber(minTime + frac * timeRange);
            g.drawString(xLabel, x - xLabel.length() * 3, plotBottom + 18);

            // y-axis tick 
            int y = plotBottom - (int) (frac * plotH);
            g.drawLine(plotLeft - 5, y, plotLeft, y);

            // align the y-axis label so its right edge lines up at the axis line
            String yLabel = formatNumber(minPos + frac * posRange);
            g.drawString(yLabel, plotLeft - 10 - yLabel.length() * 7, y + 5);
        }

        // plot each (t, pos) coordinate as a small blue dot.
        g.setColor(Color.BLUE);
        for (int i = startIdx; i < endIdx; i++) {
            double t = times.get(i) * displayScale;
            double p = positions.get(i) * displayScale;
            int xPix = plotLeft + (int) (((t - minTime) / timeRange) * plotW);

            // Y is flipped because y increases downard in the class when we want it to increase upward
            int yPix = plotBottom - (int) (((p - minPos) / posRange) * plotH);

            // fillOval draws a small filled circle
            g.fillOval(xPix - 2, yPix - 2, 4, 4);
        }
    }

    // Formats a number using k (thousand)/M(million)/B(billion) suffixes for readability.
    private String formatNumber(double value) {
        double abs = Math.abs(value);
        if (abs >= 1e9) {
            return Math.round(value / 1e8) / 10.0 + "B";
        } else if (abs >= 1e6) {
            return Math.round(value / 1e5) / 10.0 + "M";
        } else if (abs >= 1e3) {
            return Math.round(value / 100) / 10.0 + "k";
        } else {
            return Math.round(value) + "";
        }
    }

    // If a stage is never reached, print a message saying so instead of a graph
    private void drawEmptyMessage(Graphics g, String message, int boxX, int boxY, int boxW, int boxH) {
        g.setColor(Color.GRAY);
        g.drawString(message, boxX + boxW / 2 - 100, boxY + boxH / 2);
    }
}
