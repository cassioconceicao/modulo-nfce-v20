/*
 * Copyright (C) 2020 ctecinf.com.br
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.ctecinf.nfe;

import br.inf.portalfiscal.nfe.v100.evento.TEnvEvento;
import br.inf.portalfiscal.nfe.v400.autorizacao.TEnviNFe;
import br.inf.portalfiscal.nfe.v400.inutilizacao.TInutNFe;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import org.w3._2000._09.xmldsig_.KeyInfoType;
import org.w3._2000._09.xmldsig_.ReferenceType;
import org.w3._2000._09.xmldsig_.SignatureType;
import org.w3._2000._09.xmldsig_.SignatureValueType;
import org.w3._2000._09.xmldsig_.SignedInfoType;
import org.w3._2000._09.xmldsig_.TransformType;
import org.w3._2000._09.xmldsig_.TransformsType;
import org.w3._2000._09.xmldsig_.X509DataType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import sun.security.pkcs11.SunPKCS11;

/**
 *
 * @author Cássio Conceição
 * @since 25/08/2020
 * @version 2008
 * @see http://ctecinf.com.br/
 */
public class Certificate {

    private static final String TAG_NFE = "NFe";
    private static final String TAG_ID_NFE = "infNFe";

    private static final String TAG_EVENTO = "evento";
    private static final String TAG_ID_EVENTO = "infEvento";

    private static final String TAG_INUT = "inutNFe";
    private static final String TAG_ID_INUT = "infInut";

    public static final XMLSignatureFactory SIGNATURE_FACTORY = XMLSignatureFactory.getInstance("DOM");
    public static KeyStore.PrivateKeyEntry PRIVATE_KEY_ENTER;
    public static List<Transform> TRANSFORMS;

    private XMLSignature xmlSignature;
    private Reference reference;
    private X509Data x509Data;

    /**
     *
     * @return SignatyreType
     */
    public SignatureType getSignatureType() {

        SignedInfoType.CanonicalizationMethod canonicalizationMethod = new SignedInfoType.CanonicalizationMethod();
        canonicalizationMethod.setAlgorithm(xmlSignature.getSignedInfo().getCanonicalizationMethod().getAlgorithm());

        SignedInfoType.SignatureMethod signatureMethod = new SignedInfoType.SignatureMethod();
        signatureMethod.setAlgorithm(xmlSignature.getSignedInfo().getSignatureMethod().getAlgorithm());

        List<TransformType> transformsType = new ArrayList();

        TRANSFORMS.stream().map((transform) -> {
            TransformType transformType = new TransformType();
            transformType.setAlgorithm(transform.getAlgorithm());
            return transformType;
        }).forEach((transformType) -> {
            transformsType.add(transformType);
        });

        TransformsType transformType = new TransformsType();
        transformType.getTransform().addAll(transformsType);

        ReferenceType.DigestMethod digestMethod = new ReferenceType.DigestMethod();
        digestMethod.setAlgorithm(reference.getDigestMethod().getAlgorithm());

        ReferenceType referenceType = new ReferenceType();
        referenceType.setId(reference.getId());
        referenceType.setURI(reference.getURI());
        referenceType.setTransforms(transformType);
        referenceType.setDigestMethod(digestMethod);
        referenceType.setDigestValue(reference.getDigestValue());
        referenceType.setType(reference.getType());

        SignedInfoType signedInfoType = new SignedInfoType();
        signedInfoType.setCanonicalizationMethod(canonicalizationMethod);
        signedInfoType.setSignatureMethod(signatureMethod);
        signedInfoType.setReference(referenceType);

        SignatureValueType signatureValueType = new SignatureValueType();
        signatureValueType.setValue(xmlSignature.getSignatureValue().getValue());

        X509DataType x509DataType = new X509DataType();
        x509DataType.setX509Certificate(((X509Certificate) x509Data.getContent().get(0)).getSignature());

        KeyInfoType keyInfoType = new KeyInfoType();
        keyInfoType.setId(xmlSignature.getKeyInfo().getId());
        keyInfoType.setX509Data(x509DataType);

        SignatureType signatureType = new SignatureType();
        signatureType.setId(xmlSignature.getId());
        signatureType.setSignedInfo(signedInfoType);
        signatureType.setSignatureValue(signatureValueType);
        signatureType.setKeyInfo(keyInfoType);

        return signatureType;
    }

    /**
     * Assina NF-e para autorização
     *
     * @param nfe
     * @return
     * @throws NFeException
     */
    public Document signNFe(TEnviNFe nfe) throws NFeException {
        return sign(parse(TEnviNFe.class, nfe), TAG_NFE, TAG_ID_NFE);
    }

    /**
     * Assina inutilização
     *
     * @param inut
     * @return
     * @throws NFeException
     */
    public Document signInut(TInutNFe inut) throws NFeException {
        return sign(parse(TInutNFe.class, inut), TAG_INUT, TAG_ID_INUT);
    }

    /**
     * Assina evento
     *
     * @param evento
     * @return
     * @throws NFeException
     */
    public Document signEvento(TEnvEvento evento) throws NFeException {
        return sign(parse(TEnvEvento.class, evento), TAG_EVENTO, TAG_ID_EVENTO);
    }

    private Document sign(Document document, String tag, String tagId) throws NFeException {

        try {

            KeyInfoFactory keyInfoFactory = SIGNATURE_FACTORY.getKeyInfoFactory();

            reference = SIGNATURE_FACTORY.newReference("#" + getId(document, tagId), getDigestMethod(), TRANSFORMS, null, null);
            x509Data = keyInfoFactory.newX509Data(Arrays.asList((X509Certificate) PRIVATE_KEY_ENTER.getCertificate()));
            xmlSignature = SIGNATURE_FACTORY.newXMLSignature(getSignedInfo(reference), keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data)));
            xmlSignature.sign(new DOMSignContext(PRIVATE_KEY_ENTER.getPrivateKey(), document.getElementsByTagName(tag).item(0)));

            return document;

        } catch (MarshalException | XMLSignatureException | NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            throw new NFeException(ex);
        }
    }

    private Document parse(Class model, Object obj) throws NFeException {

        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);

            Document document = documentBuilderFactory.newDocumentBuilder().newDocument();

            NFe.transform(new JAXBSource(JAXBContext.newInstance(model), obj), new DOMResult(document));

            return document;

        } catch (ParserConfigurationException | JAXBException ex) {
            throw new NFeException(ex);
        }
    }

    private String getId(Document document, String tagId) {
        NodeList elements = document.getElementsByTagName(tagId);
        Element el = (Element) elements.item(0);
        el.setIdAttribute("Id", true);
        return el.getAttribute("Id");
    }

    private SignedInfo getSignedInfo(Reference reference) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return SIGNATURE_FACTORY.newSignedInfo(getCanonicalizationMethod(), getSignatureMethod(), Collections.singletonList(reference));
    }

    private DigestMethod getDigestMethod() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return SIGNATURE_FACTORY.newDigestMethod(DigestMethod.SHA1, null);
    }

    private CanonicalizationMethod getCanonicalizationMethod() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return SIGNATURE_FACTORY.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
    }

    private SignatureMethod getSignatureMethod() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return SIGNATURE_FACTORY.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
    }

    /**
     * Carrega certificado do leitor
     *
     * @throws NFeException
     */
    public static void load() throws NFeException {

        try {

            File smartCardLib;

            if (System.getProperty("os.name").equalsIgnoreCase("linux")) {
                smartCardLib = new File("lib", "libaetpkss.so.3");
            } else {
                smartCardLib = new File("lib", "aetpkss1.dll");
            }

            if (!smartCardLib.exists()) {

                if (!smartCardLib.getParentFile().exists()) {
                    smartCardLib.getParentFile().mkdirs();
                }

                if (System.getProperty("os.name").equalsIgnoreCase("linux")) {
                    throw new NFeException("Baixe o arquivo \"libaetpkss.so.3\" no site: http://ctecinf.com.br/lib e coloque no diretório \"lib\" da aplicação.");
                } else {
                    throw new NFeException("Baixe o arquivo \"aetpkss1.dll\" no site: http://ctecinf.com.br/lib e coloque no diretório \"lib\" da aplicação.");
                }
            }

            smartCardLib.setReadable(true, false);
            smartCardLib.setWritable(true, false);
            smartCardLib.setExecutable(true, false);

            Provider provider = new SunPKCS11(new ByteArrayInputStream(("name=SmartCard\nlibrary=" + smartCardLib.getAbsolutePath() + "\nshowInfo=" + NFe.DEBUG).getBytes("UTF-8")));
            Security.addProvider(provider);

            KeyStore keyStore = KeyStore.getInstance("PKCS11");

            keyStore.load(null, NFe.PIN_SMARTCARD.toCharArray());

            Enumeration<String> aliasesEnum = keyStore.aliases();

            while (aliasesEnum.hasMoreElements()) {

                String alias = (String) aliasesEnum.nextElement();

                if (keyStore.isKeyEntry(alias)) {
                    PRIVATE_KEY_ENTER = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(NFe.PIN_SMARTCARD.toCharArray()));
                    break;
                }
            }

            TRANSFORMS = Arrays.asList(SIGNATURE_FACTORY.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null), SIGNATURE_FACTORY.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (TransformParameterSpec) null));

            // Registra Leitor smartcard
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            System.setProperty("javax.net.ssl.keyStoreType", keyStore.getType());
            System.setProperty("javax.net.ssl.keyStore", "NONE");
            System.setProperty("javax.net.ssl.keyStorePassword", NFe.PIN_SMARTCARD);
            System.setProperty("javax.net.ssl.keyStoreProvider", keyStore.getProvider().getName());

            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");
            System.setProperty("javax.net.ssl.trustStore", System.getProperty("pin_smartcard"));
            System.setProperty("javax.net.ssl.trustStorePassword", System.getProperty("pin_smartcard"));

        } catch (NFeException | KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableEntryException | InvalidAlgorithmParameterException ex) {
            throw new NFeException(ex);
        }
    }
}
