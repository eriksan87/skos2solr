/*
 *    Input: file SKOS contenente un thesaurus e indirizzo del core di Solr su cui andare ad indicizzare.
 *    
 *    La classe legge il file in formato SKOS e carica tutte le informazioni in esso presenti nel core di Solr inerente al Thesaurus.
 *    
 */

import org.semanticweb.skos.*;
import org.semanticweb.skosapibinding.SKOSManager;
import org.xml.sax.SAXException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;


public class SkosThes2SolrIndex {

	private SKOSDataset dataSet;
	private SKOSDataFactory df;
	
    public SkosThes2SolrIndex()  {
    	
    	  SolrServer server = new HttpSolrServer("http://localhost:8983/solr/thesaurus");
    	  
    	  String MicTh="";
      
            SKOSManager man = null;
			try {
				man = new SKOSManager();
			} catch (SKOSCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            df = man.getSKOSDataFactory();
            try {
				dataSet = man.loadDataset(URI.create("file:/Users/Windows/Desktop/docCorpus/thes/EuroVocSKOS.rdf"));
			} catch (SKOSCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
            // posso prendere tutti i concetti mediante il for
            for (SKOSConcept concepts : dataSet.getSKOSConcepts()) {
                System.out.println("*********************************************************************");
                SolrInputDocument doc = new SolrInputDocument();
                
//*************************************************************************************************************************
            	
            	for (SKOSAnnotation anno : concepts.getSKOSAnnotations(dataSet)) {
            		String ID=null;
                	String annotazione_corrente=null;
                	if (anno.isAnnotationByConstant()) { // prendo il literal dell'annotazione corrente
                    	annotazione_corrente= anno.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral().getLiteral();
                     }
                	else
                	{
                		annotazione_corrente=anno.getAnnotationValue().getURI().toString();
                	}
                	
            	if(anno.getURI().getFragment().toString().compareTo("prefLabel")==0) //se l'annotazione corrente è una preflabel allora lo metto nell'id
                {
                	ID=annotazione_corrente;
                	System.out.println("ID="+ID);
                	doc.addField("concept",ID);
                	try {
						Stemmer a=new Stemmer(ID);
						doc.addField("descrittore",a.getIndexS());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	       
                }
            	
            	if(anno.getURI().getFragment().toString().compareTo("altLabel")==0) //se l'annotazione corrente è un' Altlabel allora lo metto nell'id
                {
                	System.out.println("altLabel="+annotazione_corrente);
                	doc.addField("altlabel", annotazione_corrente);
                 
                }
            
            	
            	if(anno.getURI().getFragment().toString().compareTo("inScheme")==0) //recupero il microthesauro
                {
            		for (SKOSLiteral MT :df.getSKOSConcept(URI.create(annotazione_corrente)).getSKOSRelatedConstantByProperty(dataSet, df.getSKOSPrefLabelProperty())) 
            		{
                        System.out.println(" Microthes: " + MT.getLiteral());
                        doc.addField("mt", MT.getLiteral());
                        MicTh=MT.getLiteral();
                        
                        
                    }
            		
                }
            	
            	if(anno.getURI().getFragment().toString().compareTo("related")==0) //recupero i related
                {
            		for (SKOSLiteral RL :df.getSKOSConcept(URI.create(annotazione_corrente)).getSKOSRelatedConstantByProperty(dataSet, df.getSKOSPrefLabelProperty())) 
            		{
                        System.out.println("Related: " + RL.getLiteral());
                        doc.addField("related", RL.getLiteral());
                    }
            		
                }
            	
            	if(anno.getURI().getFragment().toString().compareTo("narrower")==0) //recupero i narrower
                {
            		for (SKOSLiteral NR :df.getSKOSConcept(URI.create(annotazione_corrente)).getSKOSRelatedConstantByProperty(dataSet, df.getSKOSPrefLabelProperty())) 
            		{
                        System.out.println("Narrower: " + NR.getLiteral());
                        doc.addField("narrower", NR.getLiteral());
                    }
            		
                }
            	
            	if(anno.getURI().getFragment().toString().compareTo("broader")==0) //recupero i broader
                {
            		for (SKOSLiteral BR :df.getSKOSConcept(URI.create(annotazione_corrente)).getSKOSRelatedConstantByProperty(dataSet, df.getSKOSPrefLabelProperty())) 
            		{
            			
                        System.out.println("Broader: " + BR.getLiteral());
                        doc.addField("broader", BR.getLiteral());
                        
                    }
            		
                }
            	
            	if(anno.getURI().getFragment().toString().compareTo("scopeNote")==0) //recupero gli scopeNote
                {
                        System.out.println(" ScopeNote: " + annotazione_corrente);
                        doc.addField("note", annotazione_corrente);
                }
            }
            	//*********************************
                 
                ArrayList<String> broaders = new ArrayList<String>();
            	broaders = getBroader(concepts);
            	String nuova="";
            	for(int i=0; i<broaders.size();i++)
            	{
            		System.out.println("gerarchia: "+broaders.get(i));
            		String[] parts= broaders.get(i).split("/");
            		
            		for(int j=parts.length-1; j>=0; j--)
            		{
            			if(j==0)
            			nuova+=parts[j];
            			else{
            				nuova+=parts[j]+"/";
            			}
            			
            		}
            		
            		//nuova= MicTh+"/"+nuova;
            		System.out.println("gerarchiaNuova: "+nuova);
            		doc.addField("hierarchy", nuova);
            		nuova="";
            		
            	}
            	
            	try {
     			   server.add(doc);
     			} catch (SolrServerException e) {
     				// TODO Auto-generated catch block
     				e.printStackTrace();
     			} catch (IOException e) {
     				// TODO Auto-generated catch block
     				e.printStackTrace();
     			}
            	
             }
            
            try {
            	server.optimize();
    			server.commit();
    		} catch (SolrServerException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
    


    public ArrayList<String> getBroader(SKOSConcept c)
    {
    	String prefLabel=null;
    	for(SKOSAnnotation annotation : c.getSKOSAnnotations(dataSet))
    	{
    		if(annotation.getURI().getFragment().toString().compareTo("prefLabel")==0) 
    		{
    			prefLabel = annotation.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral().getLiteral(); 
    		}
    	}
    	
    	
    	ArrayList<String> result = new ArrayList<String>();
    	for(SKOSAnnotation annotation : c.getSKOSAnnotations(dataSet))
    	{
    		if(annotation.getURI().getFragment().toString().compareTo("broader")==0)
    		{
    			ArrayList<String> aux = new ArrayList<String>();
    			
    			aux = getBroader(df.getSKOSConcept(URI.create(annotation.getAnnotationValue().getURI().toString())));
    			
    			for(int i = 0; i<aux.size(); i++)
    			{
    				String h = prefLabel+"/"+aux.get(i);
    				result.add(h);
    			}
    		}
    	}
    	if(result.size()==0)
    		result.add(prefLabel);
    	return result;
    }
    
    


    public static void main(String[] args) {
    
    long inizio= System.nanoTime();
        SkosThes2SolrIndex exp = new SkosThes2SolrIndex();
    long fine= System.nanoTime();
    System.out.println(fine-inizio);
    }
}