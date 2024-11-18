package in.gov.pocra.training.util.cal_decorators;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

import in.gov.pocra.training.R;

/**
 * Use a custom selector
 */
public class MySelectorDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private HashSet<CalendarDay> dates;

    public MySelectorDecorator(Activity context, Collection<CalendarDay> date) {
        drawable = context.getResources().getDrawable(R.drawable.my_selector);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        boolean result = dates.contains(day);
        return result;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
