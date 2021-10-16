package demo.pattern.proxy;

import demo.pattern.proxy.impl.AlipayToC;
import demo.pattern.proxy.impl.AlipayTob;
import demo.pattern.proxy.impl.ToBPaymentImpl;
import demo.pattern.proxy.impl.TocPaymentImpl;

public class ProxyDemo {
    public static void main(String[] args) {
        TocPayment tocProxy = new AlipayToC(new TocPaymentImpl());
        tocProxy.pay();
        ToBPayment toBProxy = new AlipayTob(new ToBPaymentImpl());
        toBProxy.pay();
    }
}
