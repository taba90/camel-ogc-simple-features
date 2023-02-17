package it.fox.gis.camel.expressions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.camel.Expression;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.spi.Metadata;

/** ECQL expression definition. */
@Metadata(firstVersion = "1.0.0", label = "language,ogcECQL", title = "OGC ECQL")
@XmlRootElement(name = "ogcECQL")
@XmlAccessorType(XmlAccessType.FIELD)
public class ECQLExpressionDefinition extends ExpressionDefinition {

    @XmlAttribute(name = "nsURI")
    private String nsURI;

    @XmlAttribute(name = "nsPrefix")
    private String nsPrefix;

    public ECQLExpressionDefinition() {}

    public ECQLExpressionDefinition(String expression) {
        super(expression);
    }

    public ECQLExpressionDefinition(Expression expression) {
        setExpressionValue(expression);
    }

    @Override
    public String getLanguage() {
        return "ogcECQL";
    }

    public String getNsURI() {
        return nsURI;
    }

    public void setNsURI(String nsURI) {
        this.nsURI = nsURI;
    }

    public String getNsPrefix() {
        return nsPrefix;
    }

    public void setNsPrefix(String nsPrefix) {
        this.nsPrefix = nsPrefix;
    }
}
