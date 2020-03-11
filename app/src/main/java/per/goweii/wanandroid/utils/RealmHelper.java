package per.goweii.wanandroid.utils;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import per.goweii.wanandroid.module.mine.model.ReadLaterEntity;

/**
 * @author CuiZhen
 * @date 2019/5/26
 * GitHub: https://github.com/goweii
 */
public class RealmHelper {

    private final Realm mRealm;

    private RealmHelper() {
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("wanandroid.realm")
                .schemaVersion(1)
                .build();
        mRealm = Realm.getInstance(configuration);
    }

    public static RealmHelper create() {
        return new RealmHelper();
    }

    public void destroy() {
        mRealm.close();
    }

    public void add(String title, String link) {
        final int userId;
        if (UserUtils.getInstance().isLogin()) {
            userId = UserUtils.getInstance().getLoginBean().getId();
        } else {
            userId = 0;
        }
        final long time = System.currentTimeMillis();
        ReadLaterEntity entity = mRealm
                .where(ReadLaterEntity.class)
                .equalTo("userId", userId)
                .equalTo("link", link)
                .findFirst();
        if (entity != null) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    entity.setTitle(title);
                    entity.setTime(time);
                }
            });
        } else {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    ReadLaterEntity entity = realm.createObject(ReadLaterEntity.class);
                    entity.setUserId(userId);
                    entity.setTitle(title);
                    entity.setLink(link);
                    entity.setTime(time);
                    realm.copyToRealm(entity);
                }
            });
        }
    }

    public void delete(String link) {
        final RealmResults<ReadLaterEntity> readLaterEntities = mRealm.where(ReadLaterEntity.class)
                .equalTo("link", link)
                .findAll();
        if (readLaterEntities.size() == 0) {
            return;
        }
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                readLaterEntities.deleteFromRealm(0);
            }
        });
    }


    public RealmResults<ReadLaterEntity> get() {
        final int userId;
        if (UserUtils.getInstance().isLogin()) {
            userId = UserUtils.getInstance().getLoginBean().getId();
        } else {
            userId = 0;
        }
        RealmResults<ReadLaterEntity> readLaterEntities = mRealm.where(ReadLaterEntity.class)
                .equalTo("userId", userId)
                .findAll()
                .sort("time", Sort.DESCENDING);
        return readLaterEntities;
    }

    public List<ReadLaterEntity> get(@IntRange(from = 0) int page, int perPageCount) {
        RealmResults<ReadLaterEntity> readLaterEntities = get();
        final int firstIndex = perPageCount * (page);
        final int lastIndex = firstIndex + perPageCount;
        if (readLaterEntities.size() - 1 < firstIndex) {
            return new ArrayList<>(0);
        } else {
            List<ReadLaterEntity> list = new ArrayList<>(perPageCount);
            if (readLaterEntities.size() - 1 <= lastIndex) {
                list.addAll(firstIndex, readLaterEntities);
            } else {
                for (int i = firstIndex; i <= lastIndex; i++) {
                    list.add(readLaterEntities.get(i));
                }
            }
            return list;
        }
    }

}
