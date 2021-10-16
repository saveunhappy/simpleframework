package demo.pattern.proxy.impl;

import demo.pattern.proxy.ToBPayment;

public class AlipayTob implements ToBPayment {
    ToBPayment toBPayment;

    public AlipayTob(ToBPayment toBPayment) {
        this.toBPayment = toBPayment;
    }



    @Override
    public void pay() {
        beforePay();
        toBPayment.pay();
        afterPay();
    }

    private void afterPay() {
        System.out.println("支付给对方");
    }

    private void beforePay() {
        System.out.println("从银行取钱");
    }
}
