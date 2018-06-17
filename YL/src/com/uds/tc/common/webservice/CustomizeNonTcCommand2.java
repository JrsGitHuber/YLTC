
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
 *         &lt;element name="commandId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="command1" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *         &lt;element name="command2" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *         &lt;element name="command3" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
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
    "commandId",
    "command1",
    "command2",
    "command3"
})
@XmlRootElement(name = "CustomizeNonTcCommand2")
public class CustomizeNonTcCommand2 {

    @XmlElementRef(name = "commandId", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<String> commandId;
    @XmlElementRef(name = "command1", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<Object> command1;
    @XmlElementRef(name = "command2", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<Object> command2;
    @XmlElementRef(name = "command3", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<Object> command3;

    /**
     * 获取commandId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCommandId() {
        return commandId;
    }

    /**
     * 设置commandId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCommandId(JAXBElement<String> value) {
        this.commandId = value;
    }

    /**
     * 获取command1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public JAXBElement<Object> getCommand1() {
        return command1;
    }

    /**
     * 设置command1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public void setCommand1(JAXBElement<Object> value) {
        this.command1 = value;
    }

    /**
     * 获取command2属性的值。
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public JAXBElement<Object> getCommand2() {
        return command2;
    }

    /**
     * 设置command2属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public void setCommand2(JAXBElement<Object> value) {
        this.command2 = value;
    }

    /**
     * 获取command3属性的值。
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public JAXBElement<Object> getCommand3() {
        return command3;
    }

    /**
     * 设置command3属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     
     */
    public void setCommand3(JAXBElement<Object> value) {
        this.command3 = value;
    }

}
