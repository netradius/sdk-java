package com.netradius;

import net.authorize.Environment;
import net.authorize.UnitTestData;
import net.authorize.api.contract.v1.*;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.util.Constants;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Erik R. Jensen
 */
public class ApiOperationBaseOverrideTest {

	@Test
	public void testAuth() {

		String apiLoginId = UnitTestData.getPropertyFromNames(Constants.ENV_API_LOGINID, Constants.PROP_API_LOGINID);
		String transactionKey = UnitTestData.getPropertyFromNames(Constants.ENV_TRANSACTION_KEY, Constants.PROP_TRANSACTION_KEY);

		MerchantAuthenticationType merchantAuthenticationType  = new MerchantAuthenticationType() ;
		merchantAuthenticationType.setName(apiLoginId);
		merchantAuthenticationType.setTransactionKey(transactionKey);

		PaymentType paymentType = new PaymentType();
		CreditCardType creditCard = new CreditCardType();
		creditCard.setCardNumber("4242424242424242");
		creditCard.setExpirationDate("0822");
		paymentType.setCreditCard(creditCard);

		TransactionRequestType txnRequest = new TransactionRequestType();
		txnRequest.setTransactionType(TransactionTypeEnum.AUTH_ONLY_TRANSACTION.value());
		txnRequest.setPayment(paymentType);
		txnRequest.setAmount(new BigDecimal("5.00").setScale(2, RoundingMode.CEILING));

		CreateTransactionRequest apiRequest = new CreateTransactionRequest();
		apiRequest.setTransactionRequest(txnRequest);
		CreateTransactionController controller = new CreateTransactionController(apiRequest);
		controller.setMerchantAuthenticationOverride(merchantAuthenticationType);
		controller.setEnvironmentOverride(Environment.SANDBOX);
		controller.execute();

		CreateTransactionResponse response = controller.getApiResponse();

		if (response!=null) {
			// If API Response is ok, go ahead and check the transaction response
			if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
				TransactionResponse result = response.getTransactionResponse();
				if(result.getMessages() != null){
					System.out.println("Successfully created transaction with Transaction ID: " + result.getTransId());
					System.out.println("Response Code: " + result.getResponseCode());
					System.out.println("Message Code: " + result.getMessages().getMessage().get(0).getCode());
					System.out.println("Description: " + result.getMessages().getMessage().get(0).getDescription());
					System.out.println("Auth code: " + result.getAuthCode());
				}
				else {
					System.out.println("Failed Transaction.");
					if(response.getTransactionResponse().getErrors() != null){
						System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
						System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
					}
				}
			}
			else {
				System.out.println("Failed Transaction.");
				if(response.getTransactionResponse() != null && response.getTransactionResponse().getErrors() != null){
					System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
					System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
				}
				else {
					System.out.println("Error Code: " + response.getMessages().getMessage().get(0).getCode());
					System.out.println("Error message: " + response.getMessages().getMessage().get(0).getText());
				}
			}
		}
		else {
			System.out.println("Null Response.");
		}


	}

}
