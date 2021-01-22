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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;

/**
 *
 * @author Cássio Conceição
 * @since 2021
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class NFe {

    public static final String URL_CONSULTA = "https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx";
    public static final String VERSAO_NFE = "4.00";
    public static final String VERSAO_SISTEMA = "nfe-2021";

    public static Integer TP_AMB;
    public static Integer SERIE;
    public static String CNPJ;
    public static Integer UF;
    public static Integer MUNICIPIO;
    public static Integer FRETE_DEFAULT;
    public static Integer CFOP_DEFAULT;
    public static Integer ICMS_DEFAULT;
    public static Integer ICMS_ORIGEM_DEFAULT;
    public static String NATUREZA_OPERACAO;
    public static String CSC_TOKEN_PROD;
    public static String CSC_TOKEN_HOMO;
    public static String PIN_SMARTCARD;
    public static String CA_CERTS;
    public static String CA_CERTS_PASS;
    public static Boolean DEBUG = false;

    /**
     * Carrega configurações NF-e
     *
     * @throws IOException
     */
    public static void loadConfig() throws IOException {

        File file = new File("config", "nfe.properties");

        if (!file.exists()) {

            file.getParentFile().mkdirs();

            Properties props = new Properties();

            props.setProperty("pin_smartcard", "1234");
            props.setProperty("debug", "false");
            props.setProperty("ca_certs", "cacerts");
            props.setProperty("ca_certs_pass", "changeit");
            props.setProperty("serie", "1");
            props.setProperty("municipio", "");
            props.setProperty("uf", "43");
            props.setProperty("cnpj", "");
            props.setProperty("tp_amb", "2");
            props.setProperty("csc_token_prod", "");
            props.setProperty("csc_token_homo", "");
            props.setProperty("frete_default", "9");
            props.setProperty("cfop_default", "5102");
            props.setProperty("icms_default", "102");
            props.setProperty("icms_origem_default", "0");
            props.setProperty("natureza_operacao", "VENDA DE MERCADORIA");

            props.store(new FileOutputStream(file), "Configuração NF-e");
        }

        System.getProperties().load(new FileInputStream(file));

        DEBUG = Boolean.valueOf(System.getProperty("debug"));
        TP_AMB = Integer.valueOf(System.getProperty("tp_amb"));
        SERIE = Integer.valueOf(System.getProperty("serie"));
        CNPJ = System.getProperty("cnpj");
        UF = Integer.valueOf(System.getProperty("uf"));
        MUNICIPIO = Integer.valueOf(System.getProperty("municipio"));
        FRETE_DEFAULT = Integer.valueOf(System.getProperty("frete_default"));
        CFOP_DEFAULT = Integer.valueOf(System.getProperty("cfop_default"));
        ICMS_DEFAULT = Integer.valueOf(System.getProperty("icms_default"));
        ICMS_ORIGEM_DEFAULT = Integer.valueOf(System.getProperty("icms_origem_default"));
        NATUREZA_OPERACAO = System.getProperty("natureza_operacao");
        CSC_TOKEN_PROD = System.getProperty("csc_token_prod");
        CSC_TOKEN_HOMO = System.getProperty("csc_token_homo");
        PIN_SMARTCARD = System.getProperty("pin_smartcard");
        CA_CERTS = System.getProperty("ca_certs");
        CA_CERTS_PASS = System.getProperty("ca_certs_pass");
    }

    /**
     * Calcula módulo 11
     *
     * @param chave
     * @return int
     */
    public static int modulo11(String chave) {

        int total = 0;
        int peso = 2;

        for (int i = 0; i < chave.length(); i++) {

            total += (chave.charAt((chave.length() - 1) - i) - '0') * peso;
            peso++;

            if (peso == 10) {
                peso = 2;
            }
        }

        int resto = total % 11;

        return (resto == 0 || resto == 1) ? 0 : (11 - resto);
    }

    /**
     * Verifica se 'string' é um CPF válido
     *
     * @param cpf
     *
     * @return boolean
     * @throws NFeException
     */
    public static boolean isCPF(String cpf) throws NFeException {

        String[] notValid = {"00000000000", "11111111111", "22222222222", "33333333333", "44444444444", "55555555555", "66666666666", "77777777777", "88888888888", "99999999999"};

        if (cpf == null) {
            return false;
        }

        cpf = cpf.replace(".", "").replace(" ", "").replace("-", "");

        if (Arrays.asList(notValid).contains(cpf) || cpf.length() != 11) {
            return false;
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {

            sm = 0;
            peso = 10;

            for (i = 0; i < 9; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);

            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char) (r + 48);
            }

            sm = 0;
            peso = 11;

            for (i = 0; i < 10; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);

            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));

        } catch (InputMismatchException ex) {
            throw new NFeException(ex);
        }
    }

    /**
     * Transforma objeto (DOMSource | JAXBSource) em (StreamResult | DOMResult)
     *
     * @param <T>
     * @param source
     * @param result DOMResult (Document) | StreamResult (ByteArrayStreamResult)
     * @return javax.xml.transform.Result
     * @throws NFeException
     */
    public static <T extends Result> Result transform(Source source, T result) throws NFeException {

        try {

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.transform(source, result);

            if (result instanceof DOMResult) {

                DOMResult domResult = (DOMResult) result;

                if (domResult.getNode() instanceof Document) {

                    Document document = (Document) domResult.getNode();

                    if (document.getDocumentElement().hasAttribute("xmlns:ns2")) {
                        document.getDocumentElement().removeAttribute("xmlns:ns2");
                    }
                }
            }

            return result;

        } catch (TransformerException ex) {
            throw new NFeException(ex);
        }
    }

    /**
     * Formata valor dois decimais
     *
     * @param number
     * @return String "0.00"
     */
    public static String format2Digits(Number number) {

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        return nf.format(number).trim();
    }

    /**
     * Formata valor 4 decimais
     *
     * @param number
     * @return String "0.0000"
     */
    public static String format4Digits(Number number) {

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(4);

        return nf.format(number).trim();
    }

    public static Date getDH() {
        return null;
    }
}
