
package com.finflux.risk.creditbureau.provider.highmark.xsd.request;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="APPLICANT-NAME">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="NAME1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="NAME2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="NAME3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="NAME4" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="NAME5" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DOB">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DOB-DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="AGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="AGE-AS-ON" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="IDS">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ID" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="TYPE">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;enumeration value="ID01"/>
 *                                   &lt;enumeration value="ID02"/>
 *                                   &lt;enumeration value="ID03"/>
 *                                   &lt;enumeration value="ID04"/>
 *                                   &lt;enumeration value="ID05"/>
 *                                   &lt;enumeration value="ID06"/>
 *                                   &lt;enumeration value="ID07"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="VALUE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RELATIONS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="RELATION" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="TYPE">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;enumeration value="K01"/>
 *                                   &lt;enumeration value="K02"/>
 *                                   &lt;enumeration value="K03"/>
 *                                   &lt;enumeration value="K04"/>
 *                                   &lt;enumeration value="K05"/>
 *                                   &lt;enumeration value="K06"/>
 *                                   &lt;enumeration value="K07"/>
 *                                   &lt;enumeration value="K08"/>
 *                                   &lt;enumeration value="K09"/>
 *                                   &lt;enumeration value="K10"/>
 *                                   &lt;enumeration value="K11"/>
 *                                   &lt;enumeration value="K12"/>
 *                                   &lt;enumeration value="K13"/>
 *                                   &lt;enumeration value="K15"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="KEY-PERSON" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="TYPE">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="K01"/>
 *                         &lt;enumeration value="K02"/>
 *                         &lt;enumeration value="K03"/>
 *                         &lt;enumeration value="K04"/>
 *                         &lt;enumeration value="K05"/>
 *                         &lt;enumeration value="K06"/>
 *                         &lt;enumeration value="K07"/>
 *                         &lt;enumeration value="K08"/>
 *                         &lt;enumeration value="K09"/>
 *                         &lt;enumeration value="K10"/>
 *                         &lt;enumeration value="K11"/>
 *                         &lt;enumeration value="K12"/>
 *                         &lt;enumeration value="K13"/>
 *                         &lt;enumeration value="K15"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="NOMINEE" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="TYPE">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="K01"/>
 *                         &lt;enumeration value="K02"/>
 *                         &lt;enumeration value="K03"/>
 *                         &lt;enumeration value="K04"/>
 *                         &lt;enumeration value="K05"/>
 *                         &lt;enumeration value="K06"/>
 *                         &lt;enumeration value="K07"/>
 *                         &lt;enumeration value="K08"/>
 *                         &lt;enumeration value="K09"/>
 *                         &lt;enumeration value="K10"/>
 *                         &lt;enumeration value="K11"/>
 *                         &lt;enumeration value="K12"/>
 *                         &lt;enumeration value="K13"/>
 *                         &lt;enumeration value="K15"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PHONES" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PHONE" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="TELE-NO" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
 *                             &lt;element name="TELE-NO-TYPE">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;enumeration value="P01"/>
 *                                   &lt;enumeration value="P02"/>
 *                                   &lt;enumeration value="P03"/>
 *                                   &lt;enumeration value="P04"/>
 *                                   &lt;enumeration value="P05"/>
 *                                   &lt;enumeration value="P06"/>
 *                                   &lt;enumeration value="P07"/>
 *                                   &lt;enumeration value="P08"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="GENDER" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="G01"/>
 *               &lt;enumeration value="G02"/>
 *               &lt;enumeration value="G03"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "applicantname",
    "dob",
    "ids",
    "relations",
    "keyperson",
    "nominee",
    "phones",
    "gender"
})
@XmlRootElement(name = "APPLICANT-SEGMENT")
public class APPLICANTSEGMENT {

    @XmlElement(name = "APPLICANT-NAME", required = true)
    protected APPLICANTSEGMENT.APPLICANTNAME applicantname;
    @XmlElement(name = "DOB", required = true)
    protected APPLICANTSEGMENT.DOB dob;
    @XmlElement(name = "IDS", required = true)
    protected APPLICANTSEGMENT.IDS ids;
    @XmlElement(name = "RELATIONS")
    protected APPLICANTSEGMENT.RELATIONS relations;
    @XmlElement(name = "KEY-PERSON")
    protected APPLICANTSEGMENT.KEYPERSON keyperson;
    @XmlElement(name = "NOMINEE")
    protected APPLICANTSEGMENT.NOMINEE nominee;
    @XmlElement(name = "PHONES")
    protected APPLICANTSEGMENT.PHONES phones;
    @XmlElement(name = "GENDER")
    protected String gender;

    /**
     * Gets the value of the applicantname property.
     * 
     * @return
     *     possible object is
     *     {@link APPLICANTSEGMENT.APPLICANTNAME }
     *     
     */
    public APPLICANTSEGMENT.APPLICANTNAME getAPPLICANTNAME() {
        return applicantname;
    }

    /**
     * Sets the value of the applicantname property.
     * 
     * @param value
     *     allowed object is
     *     {@link APPLICANTSEGMENT.APPLICANTNAME }
     *     
     */
    public void setAPPLICANTNAME(APPLICANTSEGMENT.APPLICANTNAME value) {
        this.applicantname = value;
    }

    /**
     * Gets the value of the dob property.
     * 
     * @return
     *     possible object is
     *     {@link APPLICANTSEGMENT.DOB }
     *     
     */
    public APPLICANTSEGMENT.DOB getDOB() {
        return dob;
    }

    /**
     * Sets the value of the dob property.
     * 
     * @param value
     *     allowed object is
     *     {@link APPLICANTSEGMENT.DOB }
     *     
     */
    public void setDOB(APPLICANTSEGMENT.DOB value) {
        this.dob = value;
    }

    /**
     * Gets the value of the ids property.
     * 
     * @return
     *     possible object is
     *     {@link APPLICANTSEGMENT.IDS }
     *     
     */
    public APPLICANTSEGMENT.IDS getIDS() {
        return ids;
    }

    /**
     * Sets the value of the ids property.
     * 
     * @param value
     *     allowed object is
     *     {@link APPLICANTSEGMENT.IDS }
     *     
     */
    public void setIDS(APPLICANTSEGMENT.IDS value) {
        this.ids = value;
    }

    /**
     * Gets the value of the relations property.
     * 
     * @return
     *     possible object is
     *     {@link APPLICANTSEGMENT.RELATIONS }
     *     
     */
    public APPLICANTSEGMENT.RELATIONS getRELATIONS() {
        return relations;
    }

    /**
     * Sets the value of the relations property.
     * 
     * @param value
     *     allowed object is
     *     {@link APPLICANTSEGMENT.RELATIONS }
     *     
     */
    public void setRELATIONS(APPLICANTSEGMENT.RELATIONS value) {
        this.relations = value;
    }

    /**
     * Gets the value of the keyperson property.
     * 
     * @return
     *     possible object is
     *     {@link APPLICANTSEGMENT.KEYPERSON }
     *     
     */
    public APPLICANTSEGMENT.KEYPERSON getKEYPERSON() {
        return keyperson;
    }

    /**
     * Sets the value of the keyperson property.
     * 
     * @param value
     *     allowed object is
     *     {@link APPLICANTSEGMENT.KEYPERSON }
     *     
     */
    public void setKEYPERSON(APPLICANTSEGMENT.KEYPERSON value) {
        this.keyperson = value;
    }

    /**
     * Gets the value of the nominee property.
     * 
     * @return
     *     possible object is
     *     {@link APPLICANTSEGMENT.NOMINEE }
     *     
     */
    public APPLICANTSEGMENT.NOMINEE getNOMINEE() {
        return nominee;
    }

    /**
     * Sets the value of the nominee property.
     * 
     * @param value
     *     allowed object is
     *     {@link APPLICANTSEGMENT.NOMINEE }
     *     
     */
    public void setNOMINEE(APPLICANTSEGMENT.NOMINEE value) {
        this.nominee = value;
    }

    /**
     * Gets the value of the phones property.
     * 
     * @return
     *     possible object is
     *     {@link APPLICANTSEGMENT.PHONES }
     *     
     */
    public APPLICANTSEGMENT.PHONES getPHONES() {
        return phones;
    }

    /**
     * Sets the value of the phones property.
     * 
     * @param value
     *     allowed object is
     *     {@link APPLICANTSEGMENT.PHONES }
     *     
     */
    public void setPHONES(APPLICANTSEGMENT.PHONES value) {
        this.phones = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGENDER() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGENDER(String value) {
        this.gender = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="NAME1" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="NAME2" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="NAME3" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="NAME4" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="NAME5" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name1",
        "name2",
        "name3",
        "name4",
        "name5"
    })
    public static class APPLICANTNAME {

        @XmlElement(name = "NAME1", required = true)
        protected String name1;
        @XmlElement(name = "NAME2", required = true)
        protected String name2;
        @XmlElement(name = "NAME3", required = true)
        protected String name3;
        @XmlElement(name = "NAME4", required = true)
        protected String name4;
        @XmlElement(name = "NAME5", required = true)
        protected String name5;

        /**
         * Gets the value of the name1 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNAME1() {
            return name1;
        }

        /**
         * Sets the value of the name1 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNAME1(String value) {
            this.name1 = value;
        }

        /**
         * Gets the value of the name2 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNAME2() {
            return name2;
        }

        /**
         * Sets the value of the name2 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNAME2(String value) {
            this.name2 = value;
        }

        /**
         * Gets the value of the name3 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNAME3() {
            return name3;
        }

        /**
         * Sets the value of the name3 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNAME3(String value) {
            this.name3 = value;
        }

        /**
         * Gets the value of the name4 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNAME4() {
            return name4;
        }

        /**
         * Sets the value of the name4 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNAME4(String value) {
            this.name4 = value;
        }

        /**
         * Gets the value of the name5 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNAME5() {
            return name5;
        }

        /**
         * Sets the value of the name5 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNAME5(String value) {
            this.name5 = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="DOB-DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="AGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="AGE-AS-ON" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "dobdate",
        "age",
        "ageason"
    })
    public static class DOB {

        @XmlElement(name = "DOB-DATE")
        protected String dobdate;
        @XmlElement(name = "AGE")
        protected String age;
        @XmlElement(name = "AGE-AS-ON")
        protected String ageason;

        /**
         * Gets the value of the dobdate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDOBDATE() {
            return dobdate;
        }

        /**
         * Sets the value of the dobdate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDOBDATE(String value) {
            this.dobdate = value;
        }

        /**
         * Gets the value of the age property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAGE() {
            return age;
        }

        /**
         * Sets the value of the age property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAGE(String value) {
            this.age = value;
        }

        /**
         * Gets the value of the ageason property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAGEASON() {
            return ageason;
        }

        /**
         * Sets the value of the ageason property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAGEASON(String value) {
            this.ageason = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ID" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="TYPE">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;enumeration value="ID01"/>
     *                         &lt;enumeration value="ID02"/>
     *                         &lt;enumeration value="ID03"/>
     *                         &lt;enumeration value="ID04"/>
     *                         &lt;enumeration value="ID05"/>
     *                         &lt;enumeration value="ID06"/>
     *                         &lt;enumeration value="ID07"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="VALUE" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "id"
    })
    public static class IDS {

        @XmlElement(name = "ID", required = true)
        protected List<APPLICANTSEGMENT.IDS.ID> id;

        /**
         * Gets the value of the id property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the id property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link APPLICANTSEGMENT.IDS.ID }
         * 
         * 
         */
        public List<APPLICANTSEGMENT.IDS.ID> getID() {
            if (id == null) {
                id = new ArrayList<APPLICANTSEGMENT.IDS.ID>();
            }
            return this.id;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="TYPE">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;enumeration value="ID01"/>
         *               &lt;enumeration value="ID02"/>
         *               &lt;enumeration value="ID03"/>
         *               &lt;enumeration value="ID04"/>
         *               &lt;enumeration value="ID05"/>
         *               &lt;enumeration value="ID06"/>
         *               &lt;enumeration value="ID07"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="VALUE" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "type",
            "value"
        })
        public static class ID {

            @XmlElement(name = "TYPE", required = true)
            protected String type;
            @XmlElement(name = "VALUE", required = true)
            protected String value;

            /**
             * Gets the value of the type property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTYPE() {
                return type;
            }

            /**
             * Sets the value of the type property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTYPE(String value) {
                this.type = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getVALUE() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setVALUE(String value) {
                this.value = value;
            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="TYPE">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="K01"/>
     *               &lt;enumeration value="K02"/>
     *               &lt;enumeration value="K03"/>
     *               &lt;enumeration value="K04"/>
     *               &lt;enumeration value="K05"/>
     *               &lt;enumeration value="K06"/>
     *               &lt;enumeration value="K07"/>
     *               &lt;enumeration value="K08"/>
     *               &lt;enumeration value="K09"/>
     *               &lt;enumeration value="K10"/>
     *               &lt;enumeration value="K11"/>
     *               &lt;enumeration value="K12"/>
     *               &lt;enumeration value="K13"/>
     *               &lt;enumeration value="K15"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "type"
    })
    public static class KEYPERSON {

        @XmlElement(name = "NAME", required = true)
        protected String name;
        @XmlElement(name = "TYPE", required = true)
        protected String type;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNAME() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNAME(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTYPE() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTYPE(String value) {
            this.type = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="TYPE">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="K01"/>
     *               &lt;enumeration value="K02"/>
     *               &lt;enumeration value="K03"/>
     *               &lt;enumeration value="K04"/>
     *               &lt;enumeration value="K05"/>
     *               &lt;enumeration value="K06"/>
     *               &lt;enumeration value="K07"/>
     *               &lt;enumeration value="K08"/>
     *               &lt;enumeration value="K09"/>
     *               &lt;enumeration value="K10"/>
     *               &lt;enumeration value="K11"/>
     *               &lt;enumeration value="K12"/>
     *               &lt;enumeration value="K13"/>
     *               &lt;enumeration value="K15"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "type"
    })
    public static class NOMINEE {

        @XmlElement(name = "NAME", required = true)
        protected String name;
        @XmlElement(name = "TYPE", required = true)
        protected String type;

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNAME() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNAME(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTYPE() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTYPE(String value) {
            this.type = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="PHONE" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="TELE-NO" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
     *                   &lt;element name="TELE-NO-TYPE">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;enumeration value="P01"/>
     *                         &lt;enumeration value="P02"/>
     *                         &lt;enumeration value="P03"/>
     *                         &lt;enumeration value="P04"/>
     *                         &lt;enumeration value="P05"/>
     *                         &lt;enumeration value="P06"/>
     *                         &lt;enumeration value="P07"/>
     *                         &lt;enumeration value="P08"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "phone"
    })
    public static class PHONES {

        @XmlElement(name = "PHONE", required = true)
        protected List<APPLICANTSEGMENT.PHONES.PHONE> phone;

        /**
         * Gets the value of the phone property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the phone property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPHONE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link APPLICANTSEGMENT.PHONES.PHONE }
         * 
         * 
         */
        public List<APPLICANTSEGMENT.PHONES.PHONE> getPHONE() {
            if (phone == null) {
                phone = new ArrayList<APPLICANTSEGMENT.PHONES.PHONE>();
            }
            return this.phone;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="TELE-NO" type="{http://www.w3.org/2001/XMLSchema}unsignedLong"/>
         *         &lt;element name="TELE-NO-TYPE">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;enumeration value="P01"/>
         *               &lt;enumeration value="P02"/>
         *               &lt;enumeration value="P03"/>
         *               &lt;enumeration value="P04"/>
         *               &lt;enumeration value="P05"/>
         *               &lt;enumeration value="P06"/>
         *               &lt;enumeration value="P07"/>
         *               &lt;enumeration value="P08"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "teleno",
            "telenotype"
        })
        public static class PHONE {

            @XmlElement(name = "TELE-NO", required = true)
            @XmlSchemaType(name = "unsignedLong")
            protected BigInteger teleno;
            @XmlElement(name = "TELE-NO-TYPE", required = true)
            protected String telenotype;

            /**
             * Gets the value of the teleno property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getTELENO() {
                return teleno;
            }

            /**
             * Sets the value of the teleno property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setTELENO(BigInteger value) {
                this.teleno = value;
            }

            /**
             * Gets the value of the telenotype property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTELENOTYPE() {
                return telenotype;
            }

            /**
             * Sets the value of the telenotype property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTELENOTYPE(String value) {
                this.telenotype = value;
            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="RELATION" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="TYPE">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;enumeration value="K01"/>
     *                         &lt;enumeration value="K02"/>
     *                         &lt;enumeration value="K03"/>
     *                         &lt;enumeration value="K04"/>
     *                         &lt;enumeration value="K05"/>
     *                         &lt;enumeration value="K06"/>
     *                         &lt;enumeration value="K07"/>
     *                         &lt;enumeration value="K08"/>
     *                         &lt;enumeration value="K09"/>
     *                         &lt;enumeration value="K10"/>
     *                         &lt;enumeration value="K11"/>
     *                         &lt;enumeration value="K12"/>
     *                         &lt;enumeration value="K13"/>
     *                         &lt;enumeration value="K15"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "relation"
    })
    public static class RELATIONS {

        @XmlElement(name = "RELATION", required = true)
        protected List<APPLICANTSEGMENT.RELATIONS.RELATION> relation;

        /**
         * Gets the value of the relation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the relation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRELATION().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link APPLICANTSEGMENT.RELATIONS.RELATION }
         * 
         * 
         */
        public List<APPLICANTSEGMENT.RELATIONS.RELATION> getRELATION() {
            if (relation == null) {
                relation = new ArrayList<APPLICANTSEGMENT.RELATIONS.RELATION>();
            }
            return this.relation;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="TYPE">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;enumeration value="K01"/>
         *               &lt;enumeration value="K02"/>
         *               &lt;enumeration value="K03"/>
         *               &lt;enumeration value="K04"/>
         *               &lt;enumeration value="K05"/>
         *               &lt;enumeration value="K06"/>
         *               &lt;enumeration value="K07"/>
         *               &lt;enumeration value="K08"/>
         *               &lt;enumeration value="K09"/>
         *               &lt;enumeration value="K10"/>
         *               &lt;enumeration value="K11"/>
         *               &lt;enumeration value="K12"/>
         *               &lt;enumeration value="K13"/>
         *               &lt;enumeration value="K15"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "name",
            "type"
        })
        public static class RELATION {

            @XmlElement(name = "NAME", required = true)
            protected String name;
            @XmlElement(name = "TYPE", required = true)
            protected String type;

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNAME() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNAME(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the type property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTYPE() {
                return type;
            }

            /**
             * Sets the value of the type property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTYPE(String value) {
                this.type = value;
            }

        }

    }

}
