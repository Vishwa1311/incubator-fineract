//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.10.15 at 09:35:41 AM IST 
//


package com.finflux.risk.creditbureau.provider.highmark.xsd.old.request;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}HEADER-SEGMENT"/&gt;
 *         &lt;element ref="{}INQUIRY" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "headersegment",
    "inquiry"
})
@XmlRootElement(name = "REQUEST-REQUEST-FILE")
public class REQUESTFILEAT01 {

    @XmlElement(name = "HEADER-SEGMENT", required = true)
    private HEADERSEGMENT headersegment;
    @XmlElement(name = "INQUIRY", required = true)
    private List<INQUIRYAT01> inquiry;

    /**
     * Gets the value of the headersegment property.
     * 
     * @return
     *     possible object is
     *     {@link HEADERSEGMENT }
     *     
     */
    public HEADERSEGMENT getHEADERSEGMENT() {
        return headersegment;
    }

    /**
     * Sets the value of the headersegment property.
     * 
     * @param value
     *     allowed object is
     *     {@link HEADERSEGMENT }
     *     
     */
    public void setHEADERSEGMENT(HEADERSEGMENT value) {
        this.headersegment = value;
    }

    /**
     * Gets the value of the inquiry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inquiry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getINQUIRY().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link INQUIRYAT01 }
     * 
     * 
     */
    public List<INQUIRYAT01> getINQUIRY() {
        if (inquiry == null) {
            inquiry = new ArrayList<>();
        }
        return this.inquiry;
    }
    
    public void setINQUIRY(List<INQUIRYAT01> inquiry){
        this.inquiry = inquiry;
    }

}
