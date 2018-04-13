package hu.d2.offsiteinc.remote;

import hu.d2.offsiteinc.app.singleton.SettingsSingleton;

/**
 * Created by szidonia.laszlo on 2017. 11. 10..
 */

public class AddFileSOAP {


    public static String SOAP_ACTION = "urn:processDocument";

    public static String getSoapPayload(String ticketID, String generatedName , String fileNameWithExtension, String base64, String urlname){

        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:max=\"http://www.ibm.com/maximo\">\n"+
                "   <soapenv:Header/>\n"+
                "   <soapenv:Body>\n"+
                "      <max:UpdateMOB_INC2 creationDateTime=\"\" baseLanguage=\"\" transLanguage=\"\" messageID=\"\" maximoVersion=\"\">\n"+
                "         <max:MOB_INC2Set>\n"+

                "            <max:INCIDENT  action=\"Change\"\n"+
                "                        transLanguage=\"en\">\n"+
                "                        <max:TICKETID>"+ticketID+"</max:TICKETID>\n"+
                "                        <max:CLASS\n"+
                "                            maxvalue=\"INCIDENT\">INCIDENT\n"+
                "                            </max:CLASS>\n"+
                "                        <max:DOCLINKS\n"+
                "                            action=\"Add\"\n"+
                "                            relationship=\"\"\n"+
                "                            deleteForInsert=\"no\">\n"+
                "                            <max:DESCRIPTION>"+fileNameWithExtension+"</max:DESCRIPTION>\n"+
                "                            <max:DOCTYPE>\n"+
                "                                Attachments\n"+
                "                                </max:DOCTYPE>\n"+
                "                            <max:DOCUMENT>"+generatedName +"</max:DOCUMENT>\n"+
                "                            <max:DOCUMENTDATA>"+base64+"</max:DOCUMENTDATA>\n"+
                "                            <max:OWNERTABLE>\n"+
                "                                INCIDENT\n"+
                "                                </max:OWNERTABLE>\n"+
                "                            <max:REFERENCE>"+ticketID+"</max:REFERENCE>\n"+
                "                            <max:URLTYPE>\n"+
                "                                FILE\n"+
                "                                </max:URLTYPE>\n"+
                "                            <max:URLNAME>"+urlname+"</max:URLNAME>\n"+
                "                            <max:WEBURL>"+ SettingsSingleton.getInstance().getWebUrl()+fileNameWithExtension+" </max:WEBURL>\n"+
                "                            </max:DOCLINKS>\n"+
                "            </max:INCIDENT>\n"+
                "         </max:MOB_INC2Set>\n"+
                "      </max:UpdateMOB_INC2>\n"+
                "   </soapenv:Body>\n"+
                "</soapenv:Envelope>";
    }
}


