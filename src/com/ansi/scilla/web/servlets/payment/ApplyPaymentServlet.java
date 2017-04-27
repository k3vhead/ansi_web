package com.ansi.scilla.web.servlets.payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Payment;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.payment.PaymentType;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.payment.PaymentRequest;
import com.ansi.scilla.web.request.payment.ApplyPaymentRequest;
import com.ansi.scilla.web.response.payment.ApplyPaymentResponse;
import com.ansi.scilla.web.response.payment.PaymentResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;



public class ApplyPaymentServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;



	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		AnsiURL url = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			System.out.println(jsonString);			
			ApplyPaymentRequest paymentRequest = (ApplyPaymentRequest)AppUtils.json2object(jsonString, ApplyPaymentRequest.class);
			url = new AnsiURL(request, "verifyPayment", new String[] {PaymentRequestType.VERIFY.name().toLowerCase(), PaymentRequestType.COMMIT.name().toLowerCase()});
			SessionData sessionData = AppUtils.validateSession(request, Permission.PAYMENT, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			SessionUser sessionUser = sessionData.getUser();

			PaymentRequestType requestType = PaymentRequestType.valueOf(url.getCommand());
			if ( ! StringUtils.isBlank(url.getCommand())) {
				if ( requestType.equals(PaymentRequestType.VERIFY)) {
					doVerify();
				} else if ( requestType.equals(PaymentRequestType.COMMIT)) {
					doCommit();
				} else {
					throw new ResourceNotFoundException();
				}
				ApplyPaymentResponse data = new ApplyPaymentResponse();
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data); 
			} else {
				throw new ResourceNotFoundException();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}



	private void doVerify() {
		// TODO Auto-generated method stub
		
	}



	private void doCommit() {
		throw new RuntimeException("Not yet coded");
	}



	public enum PaymentRequestType {
		VERIFY,
		COMMIT;
	}
}
