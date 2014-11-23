import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.skos.AddAssertion;
import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSAnnotationAssertion;
import org.semanticweb.skos.SKOSChange;
import org.semanticweb.skos.SKOSChangeException;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSCreationException;
import org.semanticweb.skos.SKOSDataFactory;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSEntityAssertion;
import org.semanticweb.skos.SKOSObjectRelationAssertion;
import org.semanticweb.skos.SKOSStorageException;
import org.semanticweb.skosapibinding.SKOSFormatExt;
import org.semanticweb.skosapibinding.SKOSManager;


public class Export {

	
	public static String baseURI = "http://kdm.it/";

    public Export() {
    }
	
    public static void main(String[] args) throws SKOSCreationException{
    	
    	SKOSManager man = new SKOSManager();
    	
    	SKOSDataset vocab = man.createSKOSDataset(URI.create(baseURI));
    	
    	SKOSDataFactory factory = man.getSKOSDataFactory();
    	
    	//creazione concept
    	SKOSConcept concept1 = factory.getSKOSConcept(URI.create(baseURI + "concept1"));
    	SKOSEntityAssertion conAss1 = factory.getSKOSEntityAssertion(concept1);
    	
    	SKOSConcept concept2 = factory.getSKOSConcept(URI.create(baseURI + "concept2"));
    	SKOSEntityAssertion conAss2 = factory.getSKOSEntityAssertion(concept2);
    	
    	//assegnazione PrefLabel
    	SKOSAnnotation labelAnno = factory.getSKOSAnnotation(factory.getSKOSPrefLabelProperty().getURI(), "Some Label", "it");
    	SKOSAnnotationAssertion assertion1 = factory.getSKOSAnnotationAssertion(concept1, labelAnno);
    	
    	SKOSAnnotation labelAnno2 = factory.getSKOSAnnotation(factory.getSKOSPrefLabelProperty().getURI(), "Second Label", "it");
    	SKOSAnnotationAssertion assertion5 = factory.getSKOSAnnotationAssertion(concept2, labelAnno2);
    	
    	//assegnazione AltLabel
    	SKOSAnnotation altLabelAnno = factory.getSKOSAnnotation(factory.getSKOSAltLabelProperty().getURI(), "Another Label");
    	SKOSAnnotationAssertion assertion2 = factory.getSKOSAnnotationAssertion(concept1, altLabelAnno);
    	
    	//assegnazione broader
    	SKOSObjectRelationAssertion assertion3 = factory.getSKOSObjectRelationAssertion(concept2, factory.getSKOSBroaderProperty(), concept1);
    	
    	//assegnazione narrower
    	SKOSObjectRelationAssertion assertion4 = factory.getSKOSObjectRelationAssertion(concept1, factory.getSKOSNarrowerProperty(), concept2);
    	
    	
    	
    	List<SKOSChange> addList = new ArrayList<SKOSChange>();
        addList.add(new AddAssertion(vocab,conAss1));
        addList.add(new AddAssertion(vocab,conAss2));
        addList.add(new AddAssertion(vocab, assertion1));
        addList.add(new AddAssertion(vocab, assertion2));
        addList.add(new AddAssertion(vocab, assertion3));
        addList.add(new AddAssertion(vocab, assertion4));
        addList.add(new AddAssertion(vocab, assertion5));
        
        try {
			man.applyChanges(addList);
		} catch (SKOSChangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.err.println("writing ontology");
        
        try {
        	
			man.save(vocab, SKOSFormatExt.RDFXML, URI.create("file:/Users/Public/Mytestskos.rdf"));
		} catch (SKOSStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
