package demo.pattern.proxy.impl;

import demo.pattern.proxy.TocPayment;

public class TocPaymentImpl implements TocPayment {

    @Override
    public void pay() {
        System.out.println("以用户的名义进行支付");
    }
}
