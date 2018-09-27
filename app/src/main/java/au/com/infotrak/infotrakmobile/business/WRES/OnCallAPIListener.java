package au.com.infotrak.infotrakmobile.business.WRES;

/**
 * Created by PaulN on 13/03/2018.
 */

public interface OnCallAPIListener<T> {
    public void onSuccess(T object);
    public void onFailure(Exception e);
}
