package com.android.settings.display;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

import vendor.lineage.touch.IHighTouchPollingRate;

public class HighTouchPollingRatePreferenceController extends TogglePreferenceController {

    private static final String SERVICE_NAME =
            IHighTouchPollingRate.DESCRIPTOR + "/default";
    private static final String SETTINGS_KEY = "high_touch_polling_rate";

    public HighTouchPollingRatePreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    private IHighTouchPollingRate getService() {
        IBinder binder = ServiceManager.checkService(SERVICE_NAME);
        return binder != null ? IHighTouchPollingRate.Stub.asInterface(binder) : null;
    }

    @Override
    public int getAvailabilityStatus() {
        return getService() != null ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isChecked() {
        return Settings.Secure.getInt(mContext.getContentResolver(), SETTINGS_KEY, 0) == 1;
    }

    @Override
    public boolean setChecked(boolean value) {
        IHighTouchPollingRate service = getService();
        if (service == null) {
            return false;
        }
        try {
            service.setEnabled(value);
        } catch (RemoteException | RuntimeException e) {
            return false;
        }
        Settings.Secure.putInt(mContext.getContentResolver(), SETTINGS_KEY, value ? 1 : 0);
        return true;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_display;
    }
}
