
package com.uds.tc.common.webservice;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.uds.tc.common.webservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyType");
    private final static QName _AnyURI_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "base64Binary");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "boolean");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "dateTime");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "decimal");
    private final static QName _Double_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _String_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedByte");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedInt");
    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedLong");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedShort");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "duration");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _SendSpecialCommandCommandId_QNAME = new QName("http://tempuri.org/", "commandId");
    private final static QName _SendSpecialCommandCommand1_QNAME = new QName("http://tempuri.org/", "command1");
    private final static QName _SendSpecialCommandResponseSendSpecialCommandResult_QNAME = new QName("http://tempuri.org/", "SendSpecialCommandResult");
    private final static QName _SendSpecialCommand2Command2_QNAME = new QName("http://tempuri.org/", "command2");
    private final static QName _SendSpecialCommand2ResponseSendSpecialCommand2Result_QNAME = new QName("http://tempuri.org/", "SendSpecialCommand2Result");
    private final static QName _SendSpecialCommand3Command3_QNAME = new QName("http://tempuri.org/", "command3");
    private final static QName _SendSpecialCommand3ResponseSendSpecialCommand3Result_QNAME = new QName("http://tempuri.org/", "SendSpecialCommand3Result");
    private final static QName _DownloadFileCommandResponseDownloadFileCommandResult_QNAME = new QName("http://tempuri.org/", "DownloadFileCommandResult");
    private final static QName _CustomizeCommandResponseCustomizeCommandResult_QNAME = new QName("http://tempuri.org/", "CustomizeCommandResult");
    private final static QName _CustomizeNonTcCommandResponseCustomizeNonTcCommandResult_QNAME = new QName("http://tempuri.org/", "CustomizeNonTcCommandResult");
    private final static QName _CustomizeNonTcCommand2ResponseCustomizeNonTcCommand2Result_QNAME = new QName("http://tempuri.org/", "CustomizeNonTcCommand2Result");
    private final static QName _CustomizeNonTcCommand3ResponseCustomizeNonTcCommand3Result_QNAME = new QName("http://tempuri.org/", "CustomizeNonTcCommand3Result");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.uds.tc.common.webservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendSpecialCommand }
     * 
     */
    public SendSpecialCommand createSendSpecialCommand() {
        return new SendSpecialCommand();
    }

    /**
     * Create an instance of {@link SendSpecialCommandResponse }
     * 
     */
    public SendSpecialCommandResponse createSendSpecialCommandResponse() {
        return new SendSpecialCommandResponse();
    }

    /**
     * Create an instance of {@link SendSpecialCommand2 }
     * 
     */
    public SendSpecialCommand2 createSendSpecialCommand2() {
        return new SendSpecialCommand2();
    }

    /**
     * Create an instance of {@link SendSpecialCommand2Response }
     * 
     */
    public SendSpecialCommand2Response createSendSpecialCommand2Response() {
        return new SendSpecialCommand2Response();
    }

    /**
     * Create an instance of {@link SendSpecialCommand3 }
     * 
     */
    public SendSpecialCommand3 createSendSpecialCommand3() {
        return new SendSpecialCommand3();
    }

    /**
     * Create an instance of {@link SendSpecialCommand3Response }
     * 
     */
    public SendSpecialCommand3Response createSendSpecialCommand3Response() {
        return new SendSpecialCommand3Response();
    }

    /**
     * Create an instance of {@link DownloadFileCommand }
     * 
     */
    public DownloadFileCommand createDownloadFileCommand() {
        return new DownloadFileCommand();
    }

    /**
     * Create an instance of {@link DownloadFileCommandResponse }
     * 
     */
    public DownloadFileCommandResponse createDownloadFileCommandResponse() {
        return new DownloadFileCommandResponse();
    }

    /**
     * Create an instance of {@link CustomizeCommand }
     * 
     */
    public CustomizeCommand createCustomizeCommand() {
        return new CustomizeCommand();
    }

    /**
     * Create an instance of {@link CustomizeCommandResponse }
     * 
     */
    public CustomizeCommandResponse createCustomizeCommandResponse() {
        return new CustomizeCommandResponse();
    }

    /**
     * Create an instance of {@link CustomizeNonTcCommand }
     * 
     */
    public CustomizeNonTcCommand createCustomizeNonTcCommand() {
        return new CustomizeNonTcCommand();
    }

    /**
     * Create an instance of {@link CustomizeNonTcCommandResponse }
     * 
     */
    public CustomizeNonTcCommandResponse createCustomizeNonTcCommandResponse() {
        return new CustomizeNonTcCommandResponse();
    }

    /**
     * Create an instance of {@link CustomizeNonTcCommand2 }
     * 
     */
    public CustomizeNonTcCommand2 createCustomizeNonTcCommand2() {
        return new CustomizeNonTcCommand2();
    }

    /**
     * Create an instance of {@link CustomizeNonTcCommand2Response }
     * 
     */
    public CustomizeNonTcCommand2Response createCustomizeNonTcCommand2Response() {
        return new CustomizeNonTcCommand2Response();
    }

    /**
     * Create an instance of {@link CustomizeNonTcCommand3 }
     * 
     */
    public CustomizeNonTcCommand3 createCustomizeNonTcCommand3() {
        return new CustomizeNonTcCommand3();
    }

    /**
     * Create an instance of {@link CustomizeNonTcCommand3Response }
     * 
     */
    public CustomizeNonTcCommand3Response createCustomizeNonTcCommand3Response() {
        return new CustomizeNonTcCommand3Response();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "commandId", scope = SendSpecialCommand.class)
    public JAXBElement<String> createSendSpecialCommandCommandId(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommandId_QNAME, String.class, SendSpecialCommand.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command1", scope = SendSpecialCommand.class)
    public JAXBElement<String> createSendSpecialCommandCommand1(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommand1_QNAME, String.class, SendSpecialCommand.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "SendSpecialCommandResult", scope = SendSpecialCommandResponse.class)
    public JAXBElement<String> createSendSpecialCommandResponseSendSpecialCommandResult(String value) {
        return new JAXBElement<String>(_SendSpecialCommandResponseSendSpecialCommandResult_QNAME, String.class, SendSpecialCommandResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "commandId", scope = SendSpecialCommand2 .class)
    public JAXBElement<String> createSendSpecialCommand2CommandId(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommandId_QNAME, String.class, SendSpecialCommand2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command1", scope = SendSpecialCommand2 .class)
    public JAXBElement<String> createSendSpecialCommand2Command1(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommand1_QNAME, String.class, SendSpecialCommand2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command2", scope = SendSpecialCommand2 .class)
    public JAXBElement<String> createSendSpecialCommand2Command2(String value) {
        return new JAXBElement<String>(_SendSpecialCommand2Command2_QNAME, String.class, SendSpecialCommand2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "SendSpecialCommand2Result", scope = SendSpecialCommand2Response.class)
    public JAXBElement<String> createSendSpecialCommand2ResponseSendSpecialCommand2Result(String value) {
        return new JAXBElement<String>(_SendSpecialCommand2ResponseSendSpecialCommand2Result_QNAME, String.class, SendSpecialCommand2Response.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "commandId", scope = SendSpecialCommand3 .class)
    public JAXBElement<String> createSendSpecialCommand3CommandId(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommandId_QNAME, String.class, SendSpecialCommand3 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command1", scope = SendSpecialCommand3 .class)
    public JAXBElement<String> createSendSpecialCommand3Command1(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommand1_QNAME, String.class, SendSpecialCommand3 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command2", scope = SendSpecialCommand3 .class)
    public JAXBElement<String> createSendSpecialCommand3Command2(String value) {
        return new JAXBElement<String>(_SendSpecialCommand2Command2_QNAME, String.class, SendSpecialCommand3 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command3", scope = SendSpecialCommand3 .class)
    public JAXBElement<String> createSendSpecialCommand3Command3(String value) {
        return new JAXBElement<String>(_SendSpecialCommand3Command3_QNAME, String.class, SendSpecialCommand3 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "SendSpecialCommand3Result", scope = SendSpecialCommand3Response.class)
    public JAXBElement<String> createSendSpecialCommand3ResponseSendSpecialCommand3Result(String value) {
        return new JAXBElement<String>(_SendSpecialCommand3ResponseSendSpecialCommand3Result_QNAME, String.class, SendSpecialCommand3Response.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "commandId", scope = DownloadFileCommand.class)
    public JAXBElement<String> createDownloadFileCommandCommandId(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommandId_QNAME, String.class, DownloadFileCommand.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command1", scope = DownloadFileCommand.class)
    public JAXBElement<String> createDownloadFileCommandCommand1(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommand1_QNAME, String.class, DownloadFileCommand.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "DownloadFileCommandResult", scope = DownloadFileCommandResponse.class)
    public JAXBElement<byte[]> createDownloadFileCommandResponseDownloadFileCommandResult(byte[] value) {
        return new JAXBElement<byte[]>(_DownloadFileCommandResponseDownloadFileCommandResult_QNAME, byte[].class, DownloadFileCommandResponse.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "commandId", scope = CustomizeCommand.class)
    public JAXBElement<String> createCustomizeCommandCommandId(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommandId_QNAME, String.class, CustomizeCommand.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command1", scope = CustomizeCommand.class)
    public JAXBElement<String> createCustomizeCommandCommand1(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommand1_QNAME, String.class, CustomizeCommand.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "CustomizeCommandResult", scope = CustomizeCommandResponse.class)
    public JAXBElement<String> createCustomizeCommandResponseCustomizeCommandResult(String value) {
        return new JAXBElement<String>(_CustomizeCommandResponseCustomizeCommandResult_QNAME, String.class, CustomizeCommandResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "commandId", scope = CustomizeNonTcCommand.class)
    public JAXBElement<String> createCustomizeNonTcCommandCommandId(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommandId_QNAME, String.class, CustomizeNonTcCommand.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command1", scope = CustomizeNonTcCommand.class)
    public JAXBElement<String> createCustomizeNonTcCommandCommand1(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommand1_QNAME, String.class, CustomizeNonTcCommand.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command2", scope = CustomizeNonTcCommand.class)
    public JAXBElement<String> createCustomizeNonTcCommandCommand2(String value) {
        return new JAXBElement<String>(_SendSpecialCommand2Command2_QNAME, String.class, CustomizeNonTcCommand.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command3", scope = CustomizeNonTcCommand.class)
    public JAXBElement<String> createCustomizeNonTcCommandCommand3(String value) {
        return new JAXBElement<String>(_SendSpecialCommand3Command3_QNAME, String.class, CustomizeNonTcCommand.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "CustomizeNonTcCommandResult", scope = CustomizeNonTcCommandResponse.class)
    public JAXBElement<String> createCustomizeNonTcCommandResponseCustomizeNonTcCommandResult(String value) {
        return new JAXBElement<String>(_CustomizeNonTcCommandResponseCustomizeNonTcCommandResult_QNAME, String.class, CustomizeNonTcCommandResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "commandId", scope = CustomizeNonTcCommand2 .class)
    public JAXBElement<String> createCustomizeNonTcCommand2CommandId(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommandId_QNAME, String.class, CustomizeNonTcCommand2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command1", scope = CustomizeNonTcCommand2 .class)
    public JAXBElement<Object> createCustomizeNonTcCommand2Command1(Object value) {
        return new JAXBElement<Object>(_SendSpecialCommandCommand1_QNAME, Object.class, CustomizeNonTcCommand2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command2", scope = CustomizeNonTcCommand2 .class)
    public JAXBElement<Object> createCustomizeNonTcCommand2Command2(Object value) {
        return new JAXBElement<Object>(_SendSpecialCommand2Command2_QNAME, Object.class, CustomizeNonTcCommand2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command3", scope = CustomizeNonTcCommand2 .class)
    public JAXBElement<Object> createCustomizeNonTcCommand2Command3(Object value) {
        return new JAXBElement<Object>(_SendSpecialCommand3Command3_QNAME, Object.class, CustomizeNonTcCommand2 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "CustomizeNonTcCommand2Result", scope = CustomizeNonTcCommand2Response.class)
    public JAXBElement<String> createCustomizeNonTcCommand2ResponseCustomizeNonTcCommand2Result(String value) {
        return new JAXBElement<String>(_CustomizeNonTcCommand2ResponseCustomizeNonTcCommand2Result_QNAME, String.class, CustomizeNonTcCommand2Response.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "commandId", scope = CustomizeNonTcCommand3 .class)
    public JAXBElement<String> createCustomizeNonTcCommand3CommandId(String value) {
        return new JAXBElement<String>(_SendSpecialCommandCommandId_QNAME, String.class, CustomizeNonTcCommand3 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command1", scope = CustomizeNonTcCommand3 .class)
    public JAXBElement<byte[]> createCustomizeNonTcCommand3Command1(byte[] value) {
        return new JAXBElement<byte[]>(_SendSpecialCommandCommand1_QNAME, byte[].class, CustomizeNonTcCommand3 .class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command2", scope = CustomizeNonTcCommand3 .class)
    public JAXBElement<byte[]> createCustomizeNonTcCommand3Command2(byte[] value) {
        return new JAXBElement<byte[]>(_SendSpecialCommand2Command2_QNAME, byte[].class, CustomizeNonTcCommand3 .class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "command3", scope = CustomizeNonTcCommand3 .class)
    public JAXBElement<byte[]> createCustomizeNonTcCommand3Command3(byte[] value) {
        return new JAXBElement<byte[]>(_SendSpecialCommand3Command3_QNAME, byte[].class, CustomizeNonTcCommand3 .class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "CustomizeNonTcCommand3Result", scope = CustomizeNonTcCommand3Response.class)
    public JAXBElement<String> createCustomizeNonTcCommand3ResponseCustomizeNonTcCommand3Result(String value) {
        return new JAXBElement<String>(_CustomizeNonTcCommand3ResponseCustomizeNonTcCommand3Result_QNAME, String.class, CustomizeNonTcCommand3Response.class, value);
    }

}
