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
import br.inf.portalfiscal.nfe.v100.evento.TEvento;
import br.inf.portalfiscal.nfe.v100.evento.TRetEnvEvento;
import br.inf.portalfiscal.nfe.v400.autorizacao.TEnviNFe;
import br.inf.portalfiscal.nfe.v400.autorizacao.TNFe;
import br.inf.portalfiscal.nfe.v400.autorizacao.TRetEnviNFe;
import br.inf.portalfiscal.nfe.v400.inutilizacao.TInutNFe;
import br.inf.portalfiscal.nfe.v400.inutilizacao.TRetInutNFe;
import br.inf.portalfiscal.nfe.v400.status.TConsStatServ;
import br.inf.portalfiscal.nfe.v400.status.TRetConsStatServ;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 *
 * @author Cássio Conceição
 * @since 2020
 * @version 2020
 * @see http://ctecinf.com.br/
 */
public class SOAP {

    public static final String XMLNS_NFE_AUTORIZACAO = "http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4";
    public static final String XMLNS_STATUS = "http://www.portalfiscal.inf.br/nfe/wsdl/NFeStatusServico4";
    public static final String XMLNS_INUTILIZACAO = "http://www.portalfiscal.inf.br/nfe/wsdl/NFeInutilizacao4";
    public static final String XMLNS_EVENTO = "http://www.portalfiscal.inf.br/nfe/wsdl/NFeRecepcaoEvento4";

    public static final String URL_HOMO_AUTORIZACAO = "https://nfce-homologacao.sefazrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx";
    public static final String URL_HOMO_STATUS_SERVICO = "https://nfce-homologacao.sefazrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx";
    public static final String URL_HOMO_INUTILIZACAO = "https://nfce-homologacao.sefazrs.rs.gov.br/ws/nfeinutilizacao/NFeInutilizacao4.asmx";
    public static final String URL_HOMO_RECEPCAO_EVENTO = "https://nfce-homologacao.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx";

    public static final String URL_PROD_AUTORIZACAO = "https://nfce.sefazrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx";
    public static final String URL_PROD_STATUS_SERVICO = "https://nfce.sefazrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx";
    public static final String URL_PROD_INUTILIZACAO = "https://nfce.sefazrs.rs.gov.br/ws/nfeinutilizacao/NFeInutilizacao4.asmx";
    public static final String URL_PROD_RECEPCAO_EVENTO = "https://nfce.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx";

    /**
     * Consulta status do serviço
     *
     * @return
     * @throws br.com.ctecinf.nfe.NFeException
     */
    public static TRetConsStatServ consultarStatusServico() throws NFeException {

        try {

            TConsStatServ statServ = new TConsStatServ();
            statServ.setVersao(NFe.VERSAO_NFE.trim());
            statServ.setCUF(String.valueOf(NFe.UF).trim());
            statServ.setTpAmb(String.valueOf(NFe.TP_AMB).trim());
            statServ.setXServ("STATUS");

            JAXBContext context = JAXBContext.newInstance(TConsStatServ.class);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NFe.transform(new JAXBSource(context, statServ), new StreamResult(byteArrayOutputStream));

            String xml = byteArrayOutputStream.toString();

            URL url = new URL(statServ.getTpAmb().equals("1") ? URL_PROD_STATUS_SERVICO : URL_HOMO_STATUS_SERVICO);

            SOAPMessage response = sendXML(xml, XMLNS_STATUS, url);
            return getResponse(TRetConsStatServ.class, response, XMLNS_STATUS);

        } catch (JAXBException | MalformedURLException ex) {
            throw new NFeException(ex);
        }
    }

    /**
     * Enviar NF-e para autorizar
     *
     * @param nfe
     * @return
     * @throws NFeException
     */
    public static TRetEnviNFe enviarNFCe(TNFe nfe) throws NFeException {

        try {

            TEnviNFe enviNFe = new TEnviNFe();
            enviNFe.setVersao(nfe.getInfNFe().getVersao());
            enviNFe.setIndSinc("1");
            enviNFe.setIdLote(nfe.getInfNFe().getIde().getNNF());
            enviNFe.getNFe().add(nfe);

            Certificate sign = new Certificate();

            Document document = sign.signNFe(enviNFe);

            nfe.setSignature(sign.getSignatureType());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NFe.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));

            return enviarNFCe(byteArrayOutputStream.toString());

        } catch (Exception ex) {
            throw new NFeException(ex);
        }
    }

    /**
     * Enviar NF-e para autorizar
     *
     * @param xml
     * @return
     * @throws NFeException
     */
    public static TRetEnviNFe enviarNFCe(String xml) throws NFeException {

        try {

            if (NFe.DEBUG) {
                System.out.println("Send NFe: " + xml);
            }

            URL url = new URL(NFe.TP_AMB == 1 ? URL_PROD_AUTORIZACAO : URL_HOMO_AUTORIZACAO);

            SOAPMessage response = sendXML(xml, XMLNS_NFE_AUTORIZACAO, url);
            return getResponse(TRetEnviNFe.class, response, XMLNS_NFE_AUTORIZACAO);

        } catch (MalformedURLException ex) {
            throw new NFeException(ex);
        }
    }

    /**
     * Enviar inutilização de numeração
     *
     * @param inut
     * @return
     * @throws NFeException
     */
    public static TRetInutNFe enviarInutNFCe(TInutNFe inut) throws NFeException {

        try {

            Certificate sign = new Certificate();

            Document document = sign.signInut(inut);

            inut.setSignature(sign.getSignatureType());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NFe.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));

            return enviarInutNFCe(byteArrayOutputStream.toString());

        } catch (Exception ex) {
            throw new NFeException(ex);
        }
    }

    /**
     * Enviar inutilização de numeração
     *
     * @param xml
     * @return
     * @throws NFeException
     */
    public static TRetInutNFe enviarInutNFCe(String xml) throws NFeException {

        try {

            if (NFe.DEBUG) {
                System.out.println("Send Inutilização: " + xml);
            }

            URL url = new URL(NFe.TP_AMB == 1 ? URL_PROD_INUTILIZACAO : URL_HOMO_INUTILIZACAO);

            SOAPMessage response = sendXML(xml, XMLNS_INUTILIZACAO, url);
            return getResponse(TRetInutNFe.class, response, XMLNS_INUTILIZACAO);

        } catch (MalformedURLException ex) {
            throw new NFeException(ex);
        }
    }

    /**
     * Enviar evento
     *
     * @param evento
     * @return
     * @throws NFeException
     */
    public static TRetEnvEvento enviarEvento(TEvento evento) throws NFeException {

        try {

            TEnvEvento envEvento = new TEnvEvento();
            envEvento.setVersao(NFe.VERSAO_NFE);
            envEvento.getEvento().add(evento);

            Certificate sign = new Certificate();

            Document document = sign.signEvento(envEvento);

            evento.setSignature(sign.getSignatureType());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NFe.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));

            return enviarEvento(byteArrayOutputStream.toString());

        } catch (Exception ex) {
            throw new NFeException(ex);
        }
    }

    /**
     * Enviar evento
     *
     * @param xml
     * @return
     * @throws NFeException
     */
    public static TRetEnvEvento enviarEvento(String xml) throws NFeException {

        try {

            if (NFe.DEBUG) {
                System.out.println("Send Evento: " + xml);
            }

            URL url = new URL(NFe.TP_AMB == 1 ? URL_PROD_RECEPCAO_EVENTO : URL_HOMO_RECEPCAO_EVENTO);

            SOAPMessage response = sendXML(xml, XMLNS_EVENTO, url);
            return getResponse(TRetEnvEvento.class, response, XMLNS_EVENTO);

        } catch (MalformedURLException ex) {
            throw new NFeException(ex);
        }
    }

    private static <T> T getResponse(Class<T> model, SOAPMessage response, String xmlns) throws NFeException {

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.writeTo(out);

            int begin = out.toString().indexOf("<nfeResultMsg xmlns=\"" + xmlns + "\">");
            int end = out.toString().indexOf("</nfeResultMsg>");

            String result = null;

            if (begin > -1 && end > begin) {
                result = out.toString().substring(begin, end).replace("<nfeResultMsg xmlns=\"" + xmlns + "\">", "");
            }

            if (NFe.DEBUG) {
                System.out.println("Return [" + model.getName() + "]: " + result);
            }

            if (result == null) {
                throw new NFeException("Resposta não encontrada.");
            }

            return (T) JAXBContext.newInstance(model).createUnmarshaller().unmarshal(new ByteArrayInputStream(result.getBytes()));

        } catch (JAXBException | SOAPException | IOException ex) {
            throw new NFeException(ex);
        }
    }

    private static SOAPMessage sendXML(String xml, String xmlns, URL url) throws NFeException {

        try {

            StringBuilder soap = new StringBuilder();

            soap.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            soap.append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
            soap.append("<soap12:Body>");
            soap.append("<nfeDadosMsg xmlns=\"").append(xmlns).append("\">");
            soap.append(xml);
            soap.append("</nfeDadosMsg>");
            soap.append("</soap12:Body>");
            soap.append("</soap12:Envelope>");

            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

            MimeHeaders header = new MimeHeaders();
            header.addHeader("Content-Type", "application/soap+xml; charset=utf-8");

            SOAPMessage request = messageFactory.createMessage(header, new ByteArrayInputStream(soap.toString().getBytes()));

            SOAPConnection connection = SOAPConnectionFactory.newInstance().createConnection();

            return connection.call(request, url);

        } catch (SOAPException | IOException ex) {
            throw new NFeException(ex);
        }
    }
}
