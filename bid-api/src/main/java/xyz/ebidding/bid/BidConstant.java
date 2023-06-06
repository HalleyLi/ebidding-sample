package xyz.ebidding.bid;

public class BidConstant {
    public static final String SERVICE_NAME = "bid-service";

    // Notifications template
    public static final String RESET_PASSWORD_TMPL = "<div>We received a request to reset the password on your bid. To do so, click the below link. If you did not request this change, no action is needed. <br/> <a href=\"%s\">%s</a></div>";
    public static final String ACTIVATE_ACCOUNT_TMPL = "<div><p>Hi %s, and welcome to Ebidding!</p><a href=\"%s\">Please click here to finish setting up your bid.</a></p></div><br/><br/><div>If you have trouble clicking on the link, please copy and paste this link into your browser: <br/><a href=\"%s\">%s</a></div>";
    public static final String CONFIRM_EMAIL_TMPL = "<div>Hi %s!</div>To confirm your new email address, <a href=\"%s\">please click here</a>.</div><br/><br/><div>If you have trouble clicking on the link, please copy and paste this link into your browser: <br/><a href=\"%s\">%s</a></div>";

}
