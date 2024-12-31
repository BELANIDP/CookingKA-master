package android.presenter.fragments.kitchentimer;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel;
import com.whirlpool.hmi.uicomponents.base.ComponentSelectionInterface;
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler;
import com.whirlpool.hmi.utils.list.IntegerRange;
import com.whirlpool.hmi.utils.list.ViewModelListInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment;

/**
 * The SetKitchenTimerTumblersFragment allows the user to set the timer using tumblers that display hours, minutes, and seconds
 * separately. This fragment includes optional quick time buttons to add 30 seconds or 5 minutes, which will update the values
 * shown in the tumbler when pressed. The fragment will call {@link #handleInvalidEntry()} when the time entered is 0 or greater
 * than 24 hours.
 * Updated in v3.31.0 - REBEL-2413: Disable Start when Tumbler is Active
 */
public abstract class CustomAbstractKitchenTimerWithTumblersFragment extends SuperAbstractTimeoutEnableFragment implements View.OnClickListener, ComponentSelectionInterface {
    public static int MAX_KITCHEN_TIMER_IN_SECONDS = 86399;
    public static int MIN_KITCHEN_TIMER_IN_SECONDS = 1;
    private static final int TIME_THRESHOLD = 10;
    private static final String PREFIX_ZERO = "0";
    public KitchenTimerViewModel kitchenTimerVM;

    public CustomAbstractKitchenTimerWithTumblersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initButtons();
    }

    private long getTumblerSelectedTime() {
        int timeValueHour = 0;
        int timeValueMin = 0;
        int timeValSec = 0;

        if(null != provideHourTumbler().getSelectedValue()) {
            timeValueHour = Integer.parseInt(provideHourTumbler().getSelectedValue());
        }
        if(null != provideMinutesTumbler().getSelectedValue())  {
            timeValueMin = Integer.parseInt(provideMinutesTumbler().getSelectedValue());
        }
        if(null != provideSecondsTumbler().getSelectedValue()) {
            timeValSec = Integer.parseInt(provideSecondsTumbler().getSelectedValue());
        }
        return timeValueHour * 3600L
                + timeValueMin * 60L + timeValSec;
    }

    /**
     * Provides the interface to initialise the buttons
     */
    protected void initButtons() {
        if (provideAdd5MinutesButton() != null) {
            Objects.requireNonNull(provideAdd5MinutesButton()).setOnClickListener(this);
        }
        if (provideAdd30SecondsButton() != null) {
            Objects.requireNonNull(provideAdd30SecondsButton()).setOnClickListener(this);
        }

        provideKitchenTimerStartButton().setOnClickListener(this);
        if(provideKitchenTimerUpdateNameButton()!=null){
            provideKitchenTimerUpdateNameButton().setOnClickListener(this);
        }
        provideHourTumbler().setComponentSelectionInterface(index -> {
            // On Changed Hour Tumbler
            //add logic to handle functionality of Kitchen Timer start button
            onTumblerValueUpdate((getTumblerSelectedTime() <= MAX_KITCHEN_TIMER_IN_SECONDS) && (getTumblerSelectedTime() >=MIN_KITCHEN_TIMER_IN_SECONDS));
        });
        // ADDED for REBEL-2413 - Disable Start when Tumbler is Active
        provideHourTumbler().addOnScrollListener(getOnScrollListener());

        provideMinutesTumbler().setComponentSelectionInterface(index -> {
            // On Changed Minutes Tumbler
            //add logic to handle functionality of Kitchen Timer start button
            onTumblerValueUpdate((getTumblerSelectedTime() <= MAX_KITCHEN_TIMER_IN_SECONDS) && (getTumblerSelectedTime()>=MIN_KITCHEN_TIMER_IN_SECONDS));
        });
        // ADDED for REBEL-2413 - Disable Start when Tumbler is Active
        provideMinutesTumbler().addOnScrollListener(getOnScrollListener());

        provideSecondsTumbler().setComponentSelectionInterface(index -> {
            // On Changed Seconds Tumbler
            //add logic to handle functionality of Kitchen Timer start button
            onTumblerValueUpdate((getTumblerSelectedTime() <= MAX_KITCHEN_TIMER_IN_SECONDS) && (getTumblerSelectedTime() >=MIN_KITCHEN_TIMER_IN_SECONDS));
        });
        // ADDED for REBEL-2413 - Disable Start when Tumbler is Active
        provideSecondsTumbler().addOnScrollListener(getOnScrollListener());
    }

    /*
        ADDED for REBEL-2413 - Disable Start when Tumbler is Active
     */
    private RecyclerView.OnScrollListener getOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                boolean isEnabled = provideHourTumbler().getScrollState() == SCROLL_STATE_IDLE &&
                        provideMinutesTumbler().getScrollState() == SCROLL_STATE_IDLE &&
                        provideSecondsTumbler().getScrollState() == SCROLL_STATE_IDLE;
                if(provideAdd5MinutesButton()!=null){
                    Objects.requireNonNull(provideAdd5MinutesButton()).setEnabled(isEnabled);
                }
                if(provideAdd30SecondsButton()!=null){
                    Objects.requireNonNull(provideAdd30SecondsButton()).setEnabled(isEnabled);
                }
            }
        };
    }

    /**
     * Provides interface for the Button On Click activities
     * @param view {@link View}
     */
    @Override
    public void onClick(View view) {
        // On Changed Hour Tumbler
        long tumblerSelectedTime = getTumblerSelectedTime();
        //add logic to handle functionality of Kitchen Timer start button
        boolean validEntry = (tumblerSelectedTime <= MAX_KITCHEN_TIMER_IN_SECONDS) && (tumblerSelectedTime >=MIN_KITCHEN_TIMER_IN_SECONDS);
        if (validEntry) {
            handleValidEntry();
        } else {
            handleInvalidEntry();
        }


        if (view == provideAdd30SecondsButton()) {
            //add logic to handle functionality of +30 sec
            tumblerSelectedTime = tumblerSelectedTime + 30;

            String currentTime = convertToHHMMSS((int) tumblerSelectedTime);
            final int hours = Integer.parseInt(currentTime.substring(0,2));
            final int minutes = Integer.parseInt(currentTime.substring(2,4));
            final int seconds = Integer.parseInt(currentTime.substring(4,6));
            scrollToValues(hours,minutes,seconds);
        } else if (view == provideAdd5MinutesButton()) {
            //add logic to handle functionality of +5 mins
            tumblerSelectedTime = tumblerSelectedTime + 300;

            String currentTime = convertToHHMMSS((int) tumblerSelectedTime);
            final int hours = Integer.parseInt(currentTime.substring(0,2));
            final int minutes = Integer.parseInt(currentTime.substring(2,4));
            final int seconds = Integer.parseInt(currentTime.substring(4,6));
            scrollToValues(hours,minutes,seconds);
        } else if (view == provideKitchenTimerStartButton()) {
            if (validEntry) {
                provideKitchenTimerViewModel().setTimer((int) tumblerSelectedTime);
                handleKitchenTimerStart();
            }
        } else if (view == provideKitchenTimerUpdateNameButton()) {
            handleUpdateNameButtonClick();
        }
    }

    /** @noinspection unused*/
    public void setKitchenTimerViewModel(KitchenTimerViewModel kitchenTimerVM) {
        this.kitchenTimerVM = kitchenTimerVM;
    }

    /**
     * Provides the interface to get the Kitchen Timer View Model
     * @return kitchenTimerVM
     * @noinspection unused
     */
    public KitchenTimerViewModel getKitchenTimerViewModel() {
        return kitchenTimerVM;
    }

    /**
     * Provides the interface to get the Kitchen Timer View Model
     * @return kitchenTimerVM
     */
    abstract public KitchenTimerViewModel provideKitchenTimerViewModel();

    /**
     * Provies interface to scroll tumbler at desired default position
     * @param defaultHours int
     * @param defaultMin int
     * @param defaultSec int
     * @noinspection SameParameterValue
     */
    protected void provideDefault(int defaultHours, int defaultMin, int defaultSec) {
        scrollToValues(defaultHours, defaultMin, defaultSec);
    }

    public void scrollToValues(int defaultHours, int defaultMin, int defaultSec) {
        IntegerRange hours = new IntegerRange(1,0,24,defaultHours);
        List<String> hourstumblet = populateTumblerItemTimeValues(hours);
        initTimeTumbler(provideHourTumbler(), hourstumblet, defaultHours);

        IntegerRange minutes = new IntegerRange(1,0,59,defaultMin);
        List<String> minutestumblet = populateTumblerItemTimeValues(minutes);
        initTimeTumbler(provideMinutesTumbler(), minutestumblet, defaultMin);

        IntegerRange seconds = new IntegerRange(1,0,59,defaultSec);
        List<String> secondstumblet = populateTumblerItemTimeValues(seconds);
        initTimeTumbler(provideSecondsTumbler(), secondstumblet, defaultSec);
    }

    /**
     * Method to populate particular tumblet with Cook time values
     *
     * @param timeValues Cook time range
     * @return List<TumblerElement>
     */
    public static List<String> populateTumblerItemTimeValues(IntegerRange timeValues) {
        List<String> tumblerElements = new ArrayList<>();
        for (int i = timeValues.getMin();
             i <= timeValues.getMax(); i += timeValues.getStep()) {
            if (i < TIME_THRESHOLD) {
                tumblerElements.add(PREFIX_ZERO + i);
            } else {
                tumblerElements.add(String.valueOf(i));
            }
        }
        return tumblerElements;
    }

    /**
     * Method to initialize the Numeric Tumbler.
     *
     * @param tumbler     The Tumbler object to be initialized
     * @param list        List of Tumbler Elements
     * @param timeValues  Temperature options from SDK
     */
    private void initTimeTumbler(BaseTumbler tumbler, List<String> list, int timeValues) {
        String defaultTime = (timeValues >= TIME_THRESHOLD) ? String.valueOf(timeValues) : PREFIX_ZERO + timeValues;
        requireView().post(() -> {
            tumbler.setComponentSelectionInterface(this);
            tumbler.setListObject(new ViewModelListInterface() {
                @Override
                public ArrayList<String> getListItems() {
                    return (ArrayList<String>) list;
                }

                /**
                 * @noinspection DataFlowIssue
                 */
                @Nullable
                @Override
                public String getDefaultString() {
                    return defaultTime;
                }

                @Override
                public Object getValue(int index) {
                    return getListItems().get(index);
                }

                @Override
                public boolean isValid(Object value) {
                    return getListItems().contains(value);
                }
            }, true);
        });
    }


    /**
     * Provides interface to convert seconds in the form of "HHMMSS"
     * @param seconds int
     * @return String representing the time in "HHMMSS"
     */
    private String convertToHHMMSS(int seconds){
        int hours = seconds / 3600;
        int mins = (seconds - hours * 3600) / 60;
        int secs = seconds - hours * 3600 - mins * 60;
        return String.format(Locale.getDefault(), "%02d%02d%02d", hours, mins, secs);
    }

    /**
     * Provides the interface to show Invalid Kitchen Time pop pop up
     */
    protected abstract void handleInvalidEntry();

    /**
     * Provides the interface to handle valid entry of time in tumbler
     */
    protected abstract void handleValidEntry();
    /**
     * Provides the interface to show Bottom sheet pop up from the vertical tumbler
     */
    protected abstract void handleKitchenTimerStart();

    /**
     * Provides the interface to access Hours BaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    public abstract BaseTumbler provideHourTumbler();

    /**
     * Provides the interface to access Minutes BaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    public abstract BaseTumbler provideMinutesTumbler();

    /**
     * Provides the interface to access Seconds BaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    public abstract BaseTumbler provideSecondsTumbler();

    /**
     * Optional method to be able to update the tumblers and add 5 minutes
     * to the current value of the kitchen timer
     */
    public @Nullable View provideAdd5MinutesButton() {
        return null;
    }

    /**
     * Optional method to be able to update the tumblers and add 30 seconds
     * to the current value of the kitchen timer
     */
    public @Nullable View provideAdd30SecondsButton() {
        return null;
    }

    /**
     * To get view id for Kitchen Timer start button
     */
    public abstract View provideKitchenTimerStartButton();

    /**
     * To get view id for Kitchen Timer update name button
     */
    public abstract View provideKitchenTimerUpdateNameButton();

    /**
     * Provides the interface to show Update Name button
     */
    public abstract void handleUpdateNameButtonClick();

    /**
     * Optional: This method is used to check if the current scrolled tumbler value is valid or not.
     * This will help to perform some UI operation like enabling and disabling the buttons
     * @param isValidRange boolean
     * @since v3.44.0
     */
    public void onTumblerValueUpdate(boolean isValidRange){
        //No Op
    }
}


