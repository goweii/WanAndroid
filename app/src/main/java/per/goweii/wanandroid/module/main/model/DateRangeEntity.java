package per.goweii.wanandroid.module.main.model;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import per.goweii.basic.core.base.BaseEntity;

public class DateRangeEntity extends BaseEntity {
    private final Date from;
    private final Date to;

    public DateRangeEntity(@NonNull Date from, @NonNull Date to) {
        if (from.before(to)) {
            this.from = from;
            this.to = to;
        } else {
            this.from = to;
            this.to = from;
        }
    }

    public boolean containsNow() {
        return contains(new Date(System.currentTimeMillis()));
    }

    public boolean contains(@NonNull Date date) {
        if (date.before(from)) {
            return false;
        }
        return !date.after(to);
    }

    /**
     * 202001010000
     * or
     * 202001010000-202001020000
     */
    @SuppressLint("SimpleDateFormat")
    @Nullable
    public static DateRangeEntity parse(String dateRange) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            int index = dateRange.indexOf("-");
            if (index == -1) {
                Date d = sdf.parse(dateRange);
                return new DateRangeEntity(d, d);
            } else {
                Date from = sdf.parse(dateRange.substring(0, index));
                Date to = sdf.parse(dateRange.substring(index + 1));
                return new DateRangeEntity(from, to);
            }
        } catch (ParseException ignore) {
            return null;
        }
    }

    @Nullable
    public static List<DateRangeEntity> parse(@Nullable List<String> dateRanges) {
        if (dateRanges == null) {
            return null;
        }
        List<DateRangeEntity> dateRangeList = new ArrayList<>(dateRanges.size());
        for (String dateRange : dateRanges) {
            DateRangeEntity entity = DateRangeEntity.parse(dateRange);
            if (entity != null) {
                dateRangeList.add(entity);
            }
        }
        return dateRangeList;
    }
}
