package it.av.youeat.service.todb;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.DataRistorante;
import it.av.youeat.ocm.model.ProvIta;
import it.av.youeat.service.DataRistoranteService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class RunImportFromYellow {

    
    //@Qualifier("jpaDaoSupport")
    private DataRistoranteService dataRistoranteService;
    
    
    public void setUp() throws YoueatException {
    }


    public void tearDown() throws YoueatException {
    }

    //@Test
    public void similarToMain() {
    	ImportFromYellowThreadsProvinces provinces = new ImportFromYellowThreadsProvinces();
    	try {
    		List<ProvIta> provs = dataRistoranteService.getAllProv();
    		int pages = 0;
    		List<DataRistorante> ristos = new ArrayList<DataRistorante>();

    	/*	for (ProvIta provIta : provs) {
    			pages = provinces.getPages("http://www.paginegialle.it/pgol/1-009152600/4-ristoranti%20-%20trattorie%20ed%20osterie/3-"+ provIta.getProv() + "/?mr=30");
    	        ImportFromYellowThreads yellowThreads = new ImportFromYellowThreads();
    	        ristos.addAll(yellowThreads.runImport(pages, "http://www.paginegialle.it/pgol/1-009152600/4-ristoranti%20-%20trattorie%20ed%20osterie/3-"+ provIta.getProv() + "/"));
        		for (DataRistorante dataRistorante : ristos) {
        			dataRistoranteService.insert(dataRistorante);
                }
        		System.out.println("imported: " + provIta.getProv());
        		ristos.clear();
    		}*/

    		//int gorizia = 0;
/*    		for (ProvIta provIta : provs) {
    			System.out.println("importing: " + provIta.getProv());
    			pages = provinces.getPages("http://www.paginegialle.it/pgol/1-006776600/4-pizzerie/3-"+ provIta.getProv() + "/?mr=30");
    			System.out.println("pages: " + pages);
    	        ImportFromYellowThreads yellowThreads = new ImportFromYellowThreads();
    	        ristos.addAll(yellowThreads.runImport(pages, "http://www.paginegialle.it/pgol/1-006776600/4-pizzerie/3-"+ provIta.getProv() + "/"));
        		for (DataRistorante dataRistorante : ristos) {
    	               dataRistoranteService.insert(dataRistorante);
                }
        		System.out.println("imported: " + provIta.getProv());
        		ristos.clear();
    		}*/
    		
    		for (ProvIta provIta : provs) {
    			System.out.println("importing: " + provIta.getProv());
    			/*if(gorizia == 0){
    				gorizia = provIta.getProv().equalsIgnoreCase("PO")? 1: 0;
    	      		System.out.println("skipped: " + provIta.getProv());
    			}
    			else{*/
    			pages = provinces.getPages("http://www.paginegialle.it/pgol/1-001205100/4-bar e caffe'/3-"+ provIta.getProv() + "/?mr=30");
    			System.out.println("pages: " + pages);
    	        ImportFromYellowThreads yellowThreads = new ImportFromYellowThreads();
    	        ristos.addAll(yellowThreads.runImport(pages, "http://www.paginegialle.it/pgol/1-001205100/4-bar e caffe'/3-"+ provIta.getProv() + "/"));
        		for (DataRistorante dataRistorante : ristos) {
    	               //dataRistoranteService.insert(dataRistorante);
                }
        		System.out.println("imported: " + provIta.getProv());
        		ristos.clear();
    			/*}*/
    		}
    		
    		

    		
        } catch (XPathExpressionException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (SAXException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (ParserConfigurationException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (TransformerException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (YoueatException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
    	
    /*	try {
	        for (int i = 1; i <= 1902; i++) {
	        	ImportFromYellow importFromYellow = new ImportFromYellow();
	        	List<DataRistorante> ristos = importFromYellow.runImport(i);
	        	assertTrue(ristos.size() > 0);
	        	System.out.println("Iteration: " + i);
		        for (DataRistorante dataRistorante : ristos) {
		            try {
		                dataRistoranteService.insert(dataRistorante);
	                } catch (JackWicketException e) {
		                e.printStackTrace();
		                fail(e.getMessage());
	                }
	            }
		        System.out.println("Iteration: " + i);
            }
	        
    	} catch (XPathExpressionException e) {
        	e.printStackTrace();
        	fail(e.getMessage());
        } catch (SAXException e) {
	        e.printStackTrace();
	        fail(e.getMessage());
        } catch (IOException e) {
	        e.printStackTrace();
	        fail(e.getMessage());
        } catch (ParserConfigurationException e) {
	        e.printStackTrace();
	        fail(e.getMessage());
        } catch (TransformerException e) {
	        e.printStackTrace();
	        fail(e.getMessage());
        } catch (URISyntaxException e) {
	        e.printStackTrace();
	        fail(e.getMessage());
        }*/
    }
}
