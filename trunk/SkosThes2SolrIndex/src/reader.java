import java.net.URI;

import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSConceptScheme;
import org.semanticweb.skos.SKOSCreationException;
import org.semanticweb.skos.SKOSDataFactory;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSEntity;
import org.semanticweb.skos.SKOSLiteral;
import org.semanticweb.skos.SKOSObject;
import org.semanticweb.skos.SKOSTypedLiteral;
import org.semanticweb.skos.SKOSUntypedLiteral;
import org.semanticweb.skosapibinding.SKOSManager;

public class reader {

    public reader() {
    	
    }

    
    
    public static void main(String[] args) throws SKOSCreationException {
    	
    	SKOSDataFactory factory = null;
    	SKOSDataset vocab = null;
    	SKOSManager man = null;
    	 
			try {
				man = new SKOSManager();
			} catch (SKOSCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         factory = man.getSKOSDataFactory();
         try {
				vocab = man.loadDataset(URI.create("file:/Users/Public/EuroVocSKOS.rdf"));
			} catch (SKOSCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
         
         
         
         
         System.out.println("Read");

             // get all the concepts 
             for (SKOSConcept co : vocab.getSKOSConcepts()) {

                 System.out.println("\tConcept: " + co.getURI());

                 // get any pref labels
                 for (SKOSAnnotation con : co.getSKOSAnnotationsByURI(vocab, factory.getSKOSPrefLabelProperty().getURI())) {
                     if (con.getAnnotationValueAsConstant() instanceof SKOSUntypedLiteral) {
                         SKOSUntypedLiteral unCon = con.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral();
                         System.out.println("\t\tPrefLabel: " + unCon.getLiteral() + " lang: " + unCon.getLang());
                     }
                     else if (con.getAnnotationValueAsConstant() instanceof SKOSTypedLiteral) {
                         SKOSTypedLiteral unCon = con.getAnnotationValueAsConstant().getAsSKOSTypedLiteral();
                         System.out.println("\t\tPrefLabel: " + unCon.getLiteral());
                     }
                 }

                 
                 for (SKOSAnnotation con : co.getSKOSAnnotationsByURI(vocab, factory.getSKOSAltLabelProperty().getURI())) {
                     if (con.getAnnotationValueAsConstant() instanceof SKOSUntypedLiteral) {
                         SKOSUntypedLiteral unCon = con.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral();
                         System.out.println("\t\tAltLabel: " + unCon.getLiteral() + " lang: " + unCon.getLang());
                     }
                     else if (con.getAnnotationValueAsConstant() instanceof SKOSTypedLiteral) {
                         SKOSTypedLiteral unCon = con.getAnnotationValueAsConstant().getAsSKOSTypedLiteral();
                         System.out.println("\t\tAltLabel: " + unCon.getLiteral());
                     }
                 }
                 
                 
                 for (SKOSAnnotation con : co.getSKOSAnnotationsByURI(vocab, factory.getSKOSScopeNoteDataProperty().getURI())) {
                     if (con.getAnnotationValueAsConstant() instanceof SKOSUntypedLiteral) {
                         SKOSUntypedLiteral unCon = con.getAnnotationValueAsConstant().getAsSKOSUntypedLiteral();
                         System.out.println("\t\tScopeNote: " + unCon.getLiteral() + " lang: " + unCon.getLang());
                     }
                     else if (con.getAnnotationValueAsConstant() instanceof SKOSTypedLiteral) {
                         SKOSTypedLiteral unCon = con.getAnnotationValueAsConstant().getAsSKOSTypedLiteral();
                         System.out.println("\t\tScopeNote: " + unCon.getLiteral());
                     }
                 }
                 
                 
                 
                 // get broader concepts from all schemes
                 for (SKOSEntity c : co.getSKOSRelatedEntitiesByProperty(vocab, factory.getSKOSBroaderProperty())) {
                     System.out.println("\t\thasBroader: " + c.getURI());
                     for (SKOSLiteral BR :factory.getSKOSConcept(c.getURI()).getSKOSRelatedConstantByProperty(vocab, factory.getSKOSPrefLabelProperty())) 
             			{
                    	 	System.out.println("Broader: " + BR.getLiteral());
             			}
                 }
                 
                 for (SKOSEntity c : co.getSKOSRelatedEntitiesByProperty(vocab, factory.getSKOSNarrowerProperty())) {
                     System.out.println("\t\thasNarrower: " + c.getURI());
                 }
                 
                 for (SKOSEntity c : co.getSKOSRelatedEntitiesByProperty(vocab, factory.getSKOSRelatedProperty())) {
                     System.out.println("\t\thasRelated: " + c.getURI());
                 }

                 // get label relations
//                 for (SKOSLabelRelation rel : co.getSKOSLabelRelations(vocab)) {
//
//                     Iterator it =  rel.getLabels().iterator();
//                     while (it.hasNext()) {
//                         OWLConstant con  = (OWLConstant) it.next();
//                         System.out.println("Releated Labels: " + con.getLiteral() );
//                     }
//                 }


             }

         }
   
    
    
    
}