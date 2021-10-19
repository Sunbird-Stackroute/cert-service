package org.sunbird.incredible.processor.views;

import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.visitor.BaseVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HTMLTemplateValidator {

    private static Logger logger = LoggerFactory.getLogger(HTMLTemplateValidator.class);

    /**
     *  variables present in the html template
     */
    private static Set<String> htmlTemplateVariable;


    public HTMLTemplateValidator(String htmlString) {
        storeAllHTMLTemplateVariables(htmlString);
    }


    public Set<String> validate() {
        Set<String> invalidVariables = new HashSet<>();
        Iterator<String> iterator = htmlTemplateVariable.iterator();
        List<String> validHtmlVars = HTMLVars.get();
        while (iterator.hasNext()) {
            String templateVars = iterator.next();
            if (!validHtmlVars.contains(templateVars)) {
                invalidVariables.add(templateVars);
            }
        }
        return invalidVariables;
    }


    private static ParserVisitor visitor = new BaseVisitor() {
        @Override
        public Object visit(final ASTReference node, final Object data) {
            htmlTemplateVariable.add(node.literal());
            return null;
        }
    };

    /**
     * to get all the reference variables present in htmlString
     *
     * @param htmlString html file read in the form of string
     * @return set of reference variables
     */
    public static Set<String> storeAllHTMLTemplateVariables(String htmlString) {
        htmlTemplateVariable = new HashSet<>();
        RuntimeInstance runtimeInstance = new RuntimeInstance();
        SimpleNode node = null;
        try {
            node = runtimeInstance.parse(htmlString, null);
        } catch (ParseException e) {
            logger.debug("exception while storing template variables");
        }
        visitor.visit(node, null);
        return htmlTemplateVariable;
    }


}