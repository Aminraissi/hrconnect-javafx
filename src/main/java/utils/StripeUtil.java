package utils;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class StripeUtil {
    static {
        Stripe.apiKey = "sk_test_51QwgetJMVj2I5XFwa8GqwGmCXlYRKmpVrSwJDcZEYYlnBeTjEagY12XbQ7ovrOCcRBdpBIP16tqlYxaIF1eHGzYS00IqFgtoc9";
    }

    public static String createPaymentIntent(long amount) {

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount * 100)
                    .setCurrency("usd")
                    .addPaymentMethodType("card")
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return intent.getClientSecret();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
