package demo.pattern.proxy.impl;

import demo.pattern.proxy.TocPayment;

public class AlipayToC implements TocPayment {
    TocPayment tocPayment;

    public AlipayToC(TocPayment tocPayment) {
        this.tocPayment = tocPayment;
    }

    @Override
    public void pay() {
        beforePay();
        tocPayment.pay();
        afterPay();
    }

    private void afterPay() {
        System.out.println("支付给对方");
    }

    private void beforePay() {
        System.out.println("从银行取钱");
    }
}
