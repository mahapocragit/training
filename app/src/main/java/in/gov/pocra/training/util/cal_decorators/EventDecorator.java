package in.gov.pocra.training.util.cal_decorators;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

import in.gov.pocra.training.R;

/**
 * Decorate several days with a dot
 */
public class EventDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private int colors;
    private Activity mContext;
    private HashSet<CalendarDay> dates;

    public EventDecorator(Activity context, int color, Collection<CalendarDay> date) {
        drawable = context.getResources().getDrawable(R.drawable.my_selector);
        this.colors = color;
        this.mContext = context;
        this.dates = new HashSet<>(date);

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        boolean result = dates.contains(day);
        return result;
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.addSpan(new DotSpan(7, colors));  // For Dots in bottom of dots
        view.addSpan(new TextAppearanceSpan(mContext,R.style.CalenderHighliteTextAppearance)); // for selected dated appearience
        view.addSpan(new StyleSpan(Typeface.BOLD));
        //view.addSpan(new RelativeSizeSpan(1.4f));

        // view.setSelectionDrawable(drawable);  // For Custom Bg

    }
}
