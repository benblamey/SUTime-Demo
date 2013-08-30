package benblamey.sutime;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Node;

import edu.stanford.nlp.time.XMLUtils;

public class AnnotationViewModel {

	private final Node _node;

	public AnnotationViewModel(Node node) {
		_node = node;
	}

	public String getHTMLTimexXML() {
		String nodeAsString = XMLUtils.nodeToString(_node, true);
		nodeAsString = StringEscapeUtils.escapeHtml4(nodeAsString);
		return nodeAsString;
	}

	public String getGnuplotImage(HttpServletRequest request) throws IOException, InterruptedException {

		Node namedItem = _node.getAttributes().getNamedItem(
				"X-GNUPlot-Function");

		if (namedItem == null) {
			return null;
		}

		// Gets the real path corresponding to the given virtual path.

		final String plotsFolder = "gnuplots/";

		String fullLocalPlotsFolder = request.getServletContext().getRealPath(
				plotsFolder) + "/";

		System.err.println(fullLocalPlotsFolder);

		String gnuPlotFunction = namedItem.getNodeValue();

		if (gnuPlotFunction == null ||gnuPlotFunction.length() ==0 ) {
			return null;
		}
		
		benblamey.gnuplot.Gnuplot gnuplot = new benblamey.gnuplot.Gnuplot(fullLocalPlotsFolder);
		gnuplot.plots.add(gnuPlotFunction + " title \'graph"
				+ Integer.toString(gnuplot.plots.size()) + "\'");
		String plotFileLocalPath = gnuplot.ExportGraph();
		System.err.println(plotFileLocalPath);

		File plotFileLocal = new File(plotFileLocalPath);
		String plotFileVirtual = plotsFolder + plotFileLocal.getName();

		// String timeConstraintPlotPath = UriUtility.windowsPathToURI(path);
		// pw.println("<img src=\"" + timeConstraintPlotPath + "\"/>");

		return plotFileVirtual;

		// gnuplot.plots.add(date);

	}

	// if (!node.getNodeName().equals("TIMEX3")) {
	// continue;
	// }
	//
	//
	//
	//
	//
	//
	// pw.println("<pre>");
	// pw.println(html.getEscapedForPre(nodeAsString));
	//
	//
	//
	//
	// pw.println("<pre>");
}

