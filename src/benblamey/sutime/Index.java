package benblamey.sutime;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import benblamey.sutime.SUTimeMain2.ProcessTextResult;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.time.distributed.DistributedMain;
import edu.stanford.nlp.util.StringUtils;

/**
 * Servlet implementation class Index
 */
@WebServlet("/Index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			doRequest(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			doRequest(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private void doRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// Tries searching the classpath by default (see: IOUtils)

				//SUTime.class.getResource().toString();
		
		//System.err.println(uri);
		
		//System.out.println(SUTime.Ben);


			// String in = "1 ___  ben 2004\n1 ___  Summer 2005";
			
			
			String in= request.getParameter("text");
			
			if (in == null || in.length() == 0) {
				in = "Christmas\n\nSummer 2001";
			}
			
			final int MAX_INPUT_LENGTH = 4000;
			
			if (in.length() > MAX_INPUT_LENGTH) {
				in = in.substring(0, MAX_INPUT_LENGTH) + " ... ";
			}
			
			//String in = IOUtils
					//.slurpFile("C:/work/data/output/tempex/albumnames_final_sorted_dev_sample.txt");
			// String in =
			// "Winter .\n ben .\n ben 2004 .\n Christmas Day 2001 .\n ben '04 .\n '04";
			// //"Can't wait for the Summer! Christmas \n Christmas \n ben \n halloweeen";
			// //props.getProperty("i");
			// String in = "ben '04";

			String date = null;//"2013-04-23";// props.getProperty("date");

			AnnotationPipeline pipeline;
			
			String uri ="edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
				Properties props = StringUtils.argsToProperties(new String[0]);
			props.put(
					"pos.model",
					
					uri);
					//"C:\\work\\code\\3rd_Ben\\stanford_nlp/src/edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");

			pipeline = DistributedMain.getPipeline(props, true);

			//StringWriter stringWriter = new StringWriter();
			//PrintWriter pw = new PrintWriter(stringWriter);

			ProcessTextResult ptr = SUTimeMain2.processText(pipeline, in, date);

			//request.setAttribute("PTR", ptr);
			request.setAttribute("HIGHLIGHTED_HTML", ptr.highlightedHtml);
			request.setAttribute("TEXTAREA_TEXT", ptr.textAreaText);
			request.setAttribute("VIEW_MODELS", ptr.viewmodels);
			
			//System.out.println(stringWriter.getBuffer());


		RequestDispatcher dispatcher = request
				.getRequestDispatcher("WEB-INF/index.jsp");
		dispatcher.forward(request, response);
	}

//	public static void processText(AnnotationPipeline pipeline, String text,
//			PrintWriter pw, String date) throws IOException {
//
//		String[] split = text.split("\n");
//		for (int i = 0; i < split.length; i++) {
//
//			pw.println("<hr/>");
//			pw.println("<div>");
//
//			String line = split[i];
//			line = line.substring(line.indexOf("___") + 5);
//
//			System.err.println("Processing line: " + line);
//
//			pw.println(line);
//
//			// pw.println("<pre>");
//
//			Annotation annotation = SUTimeMain2.textToAnnotation(pipeline, line,
//					date);
//			
//			
//			
//			Document xmlDoc = SUTimeMain2.annotationToXmlDocument(annotation);
//			String string = XMLUtils.documentToString(xmlDoc);
//
//			// String string = textToAnnotatedXml(pipeline, line, date);
//			string = html.getEscapedForPre(string);
//			// pw.println(string);
//			// pw.println("</pre>");
//
//			{
//				// Annotation annotation = textToAnnotation(pipeline, line,
//				// date);
//				List<CoreMap> timexAnnsAll = annotation
//						.get(TimeAnnotations.TimexAnnotations.class);
//				
//				for (CoreMap cm : timexAnnsAll)
//				{
//					
//				}
//				
//				
//				List<Node> timexNodes = SUTimeMain
//						.createTimexNodes(
//								annotation
//										.get(CoreAnnotations.TextAnnotation.class),
//								annotation
//										.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
//								timexAnnsAll);
//				for (Node node : timexNodes) {
//
//					if (!node.getNodeName().equals("TIMEX3")) {
//						continue;
//					}
//
//					String nodeAsString = XMLUtils.nodeToString(node, true);
//
//					pw.println("<pre>");
//					pw.println(html.getEscapedForPre(nodeAsString));
//
//					Node namedItem = node.getAttributes().getNamedItem(
//							"X-GNUPlot-Function");
//
//					if (namedItem != null) {
//						String gnuPlotFunction = namedItem.getNodeValue();
//
//						UserContext uc = new UserContext("tempex"); // dummy
//																	// user.
//						Gnuplot gnuplot = new Gnuplot(uc);
//						gnuplot.plots
//								.add(gnuPlotFunction
//										+ " title \'graph"
//										+ Integer.toString(gnuplot.plots.size())
//										+ "\'");
//						String path = gnuplot.ExportGraph();
//						String timeConstraintPlotPath = UriUtility
//								.windowsPathToURI(path);
//						pw.println("<img src=\"" + timeConstraintPlotPath
//								+ "\"/>");
//					}
//
//
//					pw.println("<pre>");
//
//				}
//			}
//
//			pw.println("</div>");
//		}
//
//
//
//		pw.flush();
//		pw.close();
//	}

}
