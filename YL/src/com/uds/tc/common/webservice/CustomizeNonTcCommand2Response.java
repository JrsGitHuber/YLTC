
package com.uds.tc.common.webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CustomizeNonTcCommand2Result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
    "customizeNonTcCommand2Result"
})
@XmlRootElement(name = "CustomizeNonTcCommand2Response")
public class CustomizeNonTcCommand2Response {

    @XmlElementRef(name = "CustomizeNonTcCommand2Result", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> customizeNonTcCommand2Result;

    /**
     * 获取customizeNonTcCommand2Result属性的值。
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCustomizeNonTcCommand2Result() {
        return customizeNonTcCommand2Result;
    }

    /**
     * 设置customizeNonTcCommand2Result属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCustomizeNonTcCommand2Result(JAXBElement<String> value) {
        this.customizeNonTcCommand2Result = value;
    }

}
