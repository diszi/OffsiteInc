package hu.d2.offsiteinc.remote;


import java.util.Date;

import hu.d2.offsiteinc.util.EnvironmentTool;
import hu.d2.offsiteinc.util.UIConstans;

/**
 * Created by csabinko on 2017.09.19..
 */

public class UpdatePrioritySOAP {

    public static String SOAP_ACTION = "urn:processDocument";


    public static String getSoapPayload(String ticketId, String priorty){
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:max=\"http://www.ibm.com/maximo\">\n" +
                "\n" +
                "   <soapenv:Header/>\n" +
                "\n" +
                "   <soapenv:Body>\n" +
                "\n" +
                "      <max:UpdateMOB_INC2 creationDateTime=\"\" baseLanguage=\"EN\" transLanguage=\"EN\" messageID=\"\" maximoVersion=\"\">\n" +
                "\n" +
                "         <max:MOB_INC2Set>\n" +
                "\n" +
                "            <max:INCIDENT action=\"Change\"  deleteForInsert=\"\" relationship=\"\" transLanguage=\"EN\">\n" +
                "\n" +
                "  <max:MAXINTERRORMSG></max:MAXINTERRORMSG>\n" +
                "\n" +
                "  <max:CLASS maxvalue=\"INCIDENT\" >INCIDENT</max:CLASS>\n" +
                "\n" +
                " \n" +
                "\n" +
                "  <max:INTERNALPRIORITY maxvalue=\""+priorty+"\" changed=\"True\" >"+priorty+"</max:INTERNALPRIORITY>\n" +
                "\n" +
                "      <max:STATUSDATE changed=\"true\">"+ EnvironmentTool.convertDate(new Date(), UIConstans.DATE_PATTERN_STANDARD)+"</max:STATUSDATE>\n" +
                "\n" +
                "  <max:TICKETID >"+ticketId+"</max:TICKETID>\n" +
                "\n" +
                " \n" +
                "\n" +
                "            </max:INCIDENT>\n" +
                "\n" +
                "         </max:MOB_INC2Set>\n" +
                "\n" +
                "      </max:UpdateMOB_INC2>\n" +
                "\n" +
                "   </soapenv:Body>\n" +
                "\n" +
                "</soapenv:Envelope>";
    }
}
