package au.com.infotrak.infotrakmobile.entityclasses;

/**
 * Created by SamuelC on 17/03/2015.
 */
public class Customer {
    private long _customerid;
    private String _customername;

    public Customer(long customerid, String customername) {
        _customerid = customerid;
        _customername = customername;
    }

    public long GetCustomerId() {
        return _customerid;
    }

    public String GetCustomerName() {
        return _customername;
    }


}
