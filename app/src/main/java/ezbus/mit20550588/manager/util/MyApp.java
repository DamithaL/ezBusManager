package ezbus.mit20550588.manager.util;

import android.app.Application;
import com.stripe.android.PaymentConfiguration;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        UserStateManager.init(getApplicationContext());
        FleetStateManager.init(getApplicationContext());

        // Initialize the Stripe SDK
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51OQMzRK8hceL936mDtIHEiYc5waHaEEgk38PT2gvZAhoKRq3cnkDckasE8LgOSVSCQLsCzIVb6mSm4QUbFAxBg5g00KdBLGVRK"
        );
    }
}
