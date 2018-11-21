package com.flowrithm.daangdiaries.Api;

public class WebApi {
    public static String HOST="https://car-o-share.000webhostapp.com/EventManagement/webservice/";
    public static String DOMAIN="https://car-o-share.000webhostapp.com/";

    public static String REQUEST_FOR_OTP=HOST+"loginWithSMS.php";
    public static String App_LOGIN_LOG=HOST+"AppLoginLogs.php";
    public static String ADD_USER=HOST+"AddUser.php";
    public static String GET_USERS=HOST+"Users.php";
    public static String UPDATE_USERS=HOST+"UserUpdate.php";
    public static String LOGOUT=HOST+"Logout.php";
    public static String GET_BLOGS=HOST+"GetBlogs.php";
    public static String GET_EVENTS=HOST+"GetEvents.php";
    public static String GET_EVENT_DETAIL=HOST+"GetEventDetail.php";
    public static String GET_ISSUED_PASSES=HOST+"IssuedPasses.php";
    public static String GET_EVENT_DATE_PRICE=HOST+"GetEventDatePrice.php";
    public static String ADD_TICKET_PRICE=HOST+"AddPrice.php";
    public static String GET_ROLES=HOST+"GetRoles.php";
    public static String GET_EVENT_TICKET_DATE=HOST+"GetLatestTicketDatePrice.php";
    public static String GET_PROFILE=HOST+"GetProfile.php";
    public static String UPDATE_PROFILE=HOST+"UpdateProfile.php";
    public static String GET_PASSES_DETAIL=HOST+"GetMyPassesNew.php";
    public static String VERIFY_PASS_TICKET=HOST+"GetQRCodeDetail.php";
    public static String REDEEM_PASS_TICKET=HOST+"RedeemQRCode.php";
    public static String GET_ACTIVE_USERS=HOST+"GetActiveUser.php";
    public static String REGISTER_PASS_TICKET=HOST+"AddPasses.php";
    public static String CHECK_MOBILE_NUMBER=HOST+"CheckMobileNumber.php";
    public static String GET_REDEEM_HISTORY=HOST+"GetReedemHistory.php";
    public static String GET_ADMIN_DASHBOARD=HOST+"GetAdminDashboard.php";
    public static String GET_AGENT_DASHBOARD=HOST+"GetAgentDashboard.php";
    public static String GET_Visitor_DASHBOARD=HOST+"GetVisitorDashboard.php";
    public static String POST_ADD_NEW_EVENT=HOST+"AddNewEvent.php";
    public static String PURCHASE_TICKET=HOST+"PurchaseTicketPass.php";
    public static String POST_ADD_BLOG=HOST+"AddBlog.php";
    public static String POST_DELETE_BLOG=HOST+"DeleteBlog.php";
    public static String GENERATE_PAYTM_CHECKSUM=DOMAIN+"EventManagement/Paytm/generateChecksum.php";
    public static String GET_PAYMENT_STATUS=DOMAIN+"EventManagement/Paytm/GetPaymentStatus.php";
    public static String GET_TAXES=HOST+"GetTaxes.php";
    public static String GET_TAXATION_DETAIL=HOST+"GetTaxationDetails.php";
    public static String GET_ORDER_TRANSACTION=HOST+"GetUserTransactionHistory.php";
    public static String GET_ORDER_TRANSACTION_DETAIL=HOST+"GetUserTransactionDetail.php";
}
