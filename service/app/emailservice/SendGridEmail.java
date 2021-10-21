package emailservice;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.sunbird.incredible.processor.JsonKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SendGridEmail {
    public static CompletableFuture sendGridEmail(List<String> pdfOrSvg, List<String> recipientId, List<String> recipientName, List<String> courseName) throws Exception {
        CompletableFuture future = new CompletableFuture();
        Request request = new Request();
        Mail mail;
        SendGrid sg;
        com.sendgrid.Response response = null;
        Email from = new Email("");
        from.setName("StackRoute Certification Service");
        int index = 0;
        for (String recipients : recipientId) {
            Email to = new Email(recipients);
            String subject = "Download your certificate here";
//			int index = recipientId.indexOf(recipients);
            String body = "Dear " + recipientName.get(index) + ",<br/></br>" + "\n" + "<p>Congratulations, you have successfully completed a course titled : " + "\n" + courseName.get(index) + "\n" + ".</p> </br></br>" + "<p> You can download your certificate by following below link.</p>" + "\n" + "</br></br>" + pdfOrSvg.get(index) + "\n" + "<p></br></br>" + "Sincere Regards,</p>" + "<p></br>" + "NIIT Ltd</p>";
            Content content = new Content("text/html", body);
            sg = new SendGrid("");
            mail = new Mail(from, subject, to, content);
            request.setMethod(Method.POST);
            request.setEndpoint(JsonKey.SENDGRID_ENDPOINT);
            request.setBody(mail.build());
            response = sg.api(request);
            if (response.getStatusCode() != 202) {
                throw new Exception(response.getBody());
            }
            index++;
        }
        Map res = new HashMap();
        res.put("statusCode", response.getStatusCode());
        res.put("status", "Mail has been sent successfully !!");
        future.complete(res);
        return future;
    }
}
