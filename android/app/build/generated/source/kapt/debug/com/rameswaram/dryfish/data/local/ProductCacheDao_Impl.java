package com.rameswaram.dryfish.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ProductCacheDao_Impl implements ProductCacheDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProductCacheEntity> __insertionAdapterOfProductCacheEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearCache;

  private final SharedSQLiteStatement __preparedStmtOfClearExpiredCache;

  public ProductCacheDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProductCacheEntity = new EntityInsertionAdapter<ProductCacheEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `product_cache` (`id`,`slug`,`name`,`name_tamil`,`description`,`short_desc`,`category`,`images_json`,`skus_json`,`tags_json`,`is_featured`,`is_bestseller`,`rating`,`review_count`,`cached_at`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductCacheEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getSlug() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getSlug());
        }
        if (entity.getName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getName());
        }
        if (entity.getNameTamil() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNameTamil());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDescription());
        }
        if (entity.getShortDesc() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getShortDesc());
        }
        if (entity.getCategory() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getCategory());
        }
        if (entity.getImagesJson() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getImagesJson());
        }
        if (entity.getSkusJson() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getSkusJson());
        }
        if (entity.getTagsJson() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getTagsJson());
        }
        final int _tmp = entity.isFeatured() ? 1 : 0;
        statement.bindLong(11, _tmp);
        final int _tmp_1 = entity.isBestseller() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
        statement.bindDouble(13, entity.getRating());
        statement.bindLong(14, entity.getReviewCount());
        statement.bindLong(15, entity.getCachedAt());
      }
    };
    this.__preparedStmtOfClearCache = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM product_cache";
        return _query;
      }
    };
    this.__preparedStmtOfClearExpiredCache = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM product_cache WHERE cached_at < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertProduct(final ProductCacheEntity product,
      final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProductCacheEntity.insert(product);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object insertProducts(final List<ProductCacheEntity> products,
      final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProductCacheEntity.insert(products);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object clearCache(final Continuation<? super Unit> arg0) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearCache.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearCache.release(_stmt);
        }
      }
    }, arg0);
  }

  @Override
  public Object clearExpiredCache(final long expiryTime, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearExpiredCache.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, expiryTime);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearExpiredCache.release(_stmt);
        }
      }
    }, arg1);
  }

  @Override
  public Flow<List<ProductCacheEntity>> getProductsByCategory(final String category) {
    final String _sql = "SELECT * FROM product_cache WHERE category = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (category == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, category);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"product_cache"}, new Callable<List<ProductCacheEntity>>() {
      @Override
      @NonNull
      public List<ProductCacheEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSlug = CursorUtil.getColumnIndexOrThrow(_cursor, "slug");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNameTamil = CursorUtil.getColumnIndexOrThrow(_cursor, "name_tamil");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfShortDesc = CursorUtil.getColumnIndexOrThrow(_cursor, "short_desc");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfImagesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "images_json");
          final int _cursorIndexOfSkusJson = CursorUtil.getColumnIndexOrThrow(_cursor, "skus_json");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags_json");
          final int _cursorIndexOfIsFeatured = CursorUtil.getColumnIndexOrThrow(_cursor, "is_featured");
          final int _cursorIndexOfIsBestseller = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bestseller");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "review_count");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cached_at");
          final List<ProductCacheEntity> _result = new ArrayList<ProductCacheEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductCacheEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpSlug;
            if (_cursor.isNull(_cursorIndexOfSlug)) {
              _tmpSlug = null;
            } else {
              _tmpSlug = _cursor.getString(_cursorIndexOfSlug);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpNameTamil;
            if (_cursor.isNull(_cursorIndexOfNameTamil)) {
              _tmpNameTamil = null;
            } else {
              _tmpNameTamil = _cursor.getString(_cursorIndexOfNameTamil);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpShortDesc;
            if (_cursor.isNull(_cursorIndexOfShortDesc)) {
              _tmpShortDesc = null;
            } else {
              _tmpShortDesc = _cursor.getString(_cursorIndexOfShortDesc);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpImagesJson;
            if (_cursor.isNull(_cursorIndexOfImagesJson)) {
              _tmpImagesJson = null;
            } else {
              _tmpImagesJson = _cursor.getString(_cursorIndexOfImagesJson);
            }
            final String _tmpSkusJson;
            if (_cursor.isNull(_cursorIndexOfSkusJson)) {
              _tmpSkusJson = null;
            } else {
              _tmpSkusJson = _cursor.getString(_cursorIndexOfSkusJson);
            }
            final String _tmpTagsJson;
            if (_cursor.isNull(_cursorIndexOfTagsJson)) {
              _tmpTagsJson = null;
            } else {
              _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
            }
            final boolean _tmpIsFeatured;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFeatured);
            _tmpIsFeatured = _tmp != 0;
            final boolean _tmpIsBestseller;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBestseller);
            _tmpIsBestseller = _tmp_1 != 0;
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new ProductCacheEntity(_tmpId,_tmpSlug,_tmpName,_tmpNameTamil,_tmpDescription,_tmpShortDesc,_tmpCategory,_tmpImagesJson,_tmpSkusJson,_tmpTagsJson,_tmpIsFeatured,_tmpIsBestseller,_tmpRating,_tmpReviewCount,_tmpCachedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ProductCacheEntity>> getFeaturedProducts() {
    final String _sql = "SELECT * FROM product_cache WHERE is_featured = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"product_cache"}, new Callable<List<ProductCacheEntity>>() {
      @Override
      @NonNull
      public List<ProductCacheEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSlug = CursorUtil.getColumnIndexOrThrow(_cursor, "slug");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNameTamil = CursorUtil.getColumnIndexOrThrow(_cursor, "name_tamil");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfShortDesc = CursorUtil.getColumnIndexOrThrow(_cursor, "short_desc");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfImagesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "images_json");
          final int _cursorIndexOfSkusJson = CursorUtil.getColumnIndexOrThrow(_cursor, "skus_json");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags_json");
          final int _cursorIndexOfIsFeatured = CursorUtil.getColumnIndexOrThrow(_cursor, "is_featured");
          final int _cursorIndexOfIsBestseller = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bestseller");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "review_count");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cached_at");
          final List<ProductCacheEntity> _result = new ArrayList<ProductCacheEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductCacheEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpSlug;
            if (_cursor.isNull(_cursorIndexOfSlug)) {
              _tmpSlug = null;
            } else {
              _tmpSlug = _cursor.getString(_cursorIndexOfSlug);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpNameTamil;
            if (_cursor.isNull(_cursorIndexOfNameTamil)) {
              _tmpNameTamil = null;
            } else {
              _tmpNameTamil = _cursor.getString(_cursorIndexOfNameTamil);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpShortDesc;
            if (_cursor.isNull(_cursorIndexOfShortDesc)) {
              _tmpShortDesc = null;
            } else {
              _tmpShortDesc = _cursor.getString(_cursorIndexOfShortDesc);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpImagesJson;
            if (_cursor.isNull(_cursorIndexOfImagesJson)) {
              _tmpImagesJson = null;
            } else {
              _tmpImagesJson = _cursor.getString(_cursorIndexOfImagesJson);
            }
            final String _tmpSkusJson;
            if (_cursor.isNull(_cursorIndexOfSkusJson)) {
              _tmpSkusJson = null;
            } else {
              _tmpSkusJson = _cursor.getString(_cursorIndexOfSkusJson);
            }
            final String _tmpTagsJson;
            if (_cursor.isNull(_cursorIndexOfTagsJson)) {
              _tmpTagsJson = null;
            } else {
              _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
            }
            final boolean _tmpIsFeatured;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFeatured);
            _tmpIsFeatured = _tmp != 0;
            final boolean _tmpIsBestseller;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBestseller);
            _tmpIsBestseller = _tmp_1 != 0;
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new ProductCacheEntity(_tmpId,_tmpSlug,_tmpName,_tmpNameTamil,_tmpDescription,_tmpShortDesc,_tmpCategory,_tmpImagesJson,_tmpSkusJson,_tmpTagsJson,_tmpIsFeatured,_tmpIsBestseller,_tmpRating,_tmpReviewCount,_tmpCachedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ProductCacheEntity>> getBestsellers() {
    final String _sql = "SELECT * FROM product_cache WHERE is_bestseller = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"product_cache"}, new Callable<List<ProductCacheEntity>>() {
      @Override
      @NonNull
      public List<ProductCacheEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSlug = CursorUtil.getColumnIndexOrThrow(_cursor, "slug");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNameTamil = CursorUtil.getColumnIndexOrThrow(_cursor, "name_tamil");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfShortDesc = CursorUtil.getColumnIndexOrThrow(_cursor, "short_desc");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfImagesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "images_json");
          final int _cursorIndexOfSkusJson = CursorUtil.getColumnIndexOrThrow(_cursor, "skus_json");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags_json");
          final int _cursorIndexOfIsFeatured = CursorUtil.getColumnIndexOrThrow(_cursor, "is_featured");
          final int _cursorIndexOfIsBestseller = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bestseller");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "review_count");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cached_at");
          final List<ProductCacheEntity> _result = new ArrayList<ProductCacheEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductCacheEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpSlug;
            if (_cursor.isNull(_cursorIndexOfSlug)) {
              _tmpSlug = null;
            } else {
              _tmpSlug = _cursor.getString(_cursorIndexOfSlug);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpNameTamil;
            if (_cursor.isNull(_cursorIndexOfNameTamil)) {
              _tmpNameTamil = null;
            } else {
              _tmpNameTamil = _cursor.getString(_cursorIndexOfNameTamil);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpShortDesc;
            if (_cursor.isNull(_cursorIndexOfShortDesc)) {
              _tmpShortDesc = null;
            } else {
              _tmpShortDesc = _cursor.getString(_cursorIndexOfShortDesc);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpImagesJson;
            if (_cursor.isNull(_cursorIndexOfImagesJson)) {
              _tmpImagesJson = null;
            } else {
              _tmpImagesJson = _cursor.getString(_cursorIndexOfImagesJson);
            }
            final String _tmpSkusJson;
            if (_cursor.isNull(_cursorIndexOfSkusJson)) {
              _tmpSkusJson = null;
            } else {
              _tmpSkusJson = _cursor.getString(_cursorIndexOfSkusJson);
            }
            final String _tmpTagsJson;
            if (_cursor.isNull(_cursorIndexOfTagsJson)) {
              _tmpTagsJson = null;
            } else {
              _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
            }
            final boolean _tmpIsFeatured;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFeatured);
            _tmpIsFeatured = _tmp != 0;
            final boolean _tmpIsBestseller;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBestseller);
            _tmpIsBestseller = _tmp_1 != 0;
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new ProductCacheEntity(_tmpId,_tmpSlug,_tmpName,_tmpNameTamil,_tmpDescription,_tmpShortDesc,_tmpCategory,_tmpImagesJson,_tmpSkusJson,_tmpTagsJson,_tmpIsFeatured,_tmpIsBestseller,_tmpRating,_tmpReviewCount,_tmpCachedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getProductById(final String id,
      final Continuation<? super ProductCacheEntity> arg1) {
    final String _sql = "SELECT * FROM product_cache WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProductCacheEntity>() {
      @Override
      @Nullable
      public ProductCacheEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSlug = CursorUtil.getColumnIndexOrThrow(_cursor, "slug");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNameTamil = CursorUtil.getColumnIndexOrThrow(_cursor, "name_tamil");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfShortDesc = CursorUtil.getColumnIndexOrThrow(_cursor, "short_desc");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfImagesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "images_json");
          final int _cursorIndexOfSkusJson = CursorUtil.getColumnIndexOrThrow(_cursor, "skus_json");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags_json");
          final int _cursorIndexOfIsFeatured = CursorUtil.getColumnIndexOrThrow(_cursor, "is_featured");
          final int _cursorIndexOfIsBestseller = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bestseller");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "review_count");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cached_at");
          final ProductCacheEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpSlug;
            if (_cursor.isNull(_cursorIndexOfSlug)) {
              _tmpSlug = null;
            } else {
              _tmpSlug = _cursor.getString(_cursorIndexOfSlug);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpNameTamil;
            if (_cursor.isNull(_cursorIndexOfNameTamil)) {
              _tmpNameTamil = null;
            } else {
              _tmpNameTamil = _cursor.getString(_cursorIndexOfNameTamil);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpShortDesc;
            if (_cursor.isNull(_cursorIndexOfShortDesc)) {
              _tmpShortDesc = null;
            } else {
              _tmpShortDesc = _cursor.getString(_cursorIndexOfShortDesc);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpImagesJson;
            if (_cursor.isNull(_cursorIndexOfImagesJson)) {
              _tmpImagesJson = null;
            } else {
              _tmpImagesJson = _cursor.getString(_cursorIndexOfImagesJson);
            }
            final String _tmpSkusJson;
            if (_cursor.isNull(_cursorIndexOfSkusJson)) {
              _tmpSkusJson = null;
            } else {
              _tmpSkusJson = _cursor.getString(_cursorIndexOfSkusJson);
            }
            final String _tmpTagsJson;
            if (_cursor.isNull(_cursorIndexOfTagsJson)) {
              _tmpTagsJson = null;
            } else {
              _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
            }
            final boolean _tmpIsFeatured;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFeatured);
            _tmpIsFeatured = _tmp != 0;
            final boolean _tmpIsBestseller;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBestseller);
            _tmpIsBestseller = _tmp_1 != 0;
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new ProductCacheEntity(_tmpId,_tmpSlug,_tmpName,_tmpNameTamil,_tmpDescription,_tmpShortDesc,_tmpCategory,_tmpImagesJson,_tmpSkusJson,_tmpTagsJson,_tmpIsFeatured,_tmpIsBestseller,_tmpRating,_tmpReviewCount,_tmpCachedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Object getProductBySlug(final String slug,
      final Continuation<? super ProductCacheEntity> arg1) {
    final String _sql = "SELECT * FROM product_cache WHERE slug = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (slug == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, slug);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProductCacheEntity>() {
      @Override
      @Nullable
      public ProductCacheEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSlug = CursorUtil.getColumnIndexOrThrow(_cursor, "slug");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNameTamil = CursorUtil.getColumnIndexOrThrow(_cursor, "name_tamil");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfShortDesc = CursorUtil.getColumnIndexOrThrow(_cursor, "short_desc");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfImagesJson = CursorUtil.getColumnIndexOrThrow(_cursor, "images_json");
          final int _cursorIndexOfSkusJson = CursorUtil.getColumnIndexOrThrow(_cursor, "skus_json");
          final int _cursorIndexOfTagsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "tags_json");
          final int _cursorIndexOfIsFeatured = CursorUtil.getColumnIndexOrThrow(_cursor, "is_featured");
          final int _cursorIndexOfIsBestseller = CursorUtil.getColumnIndexOrThrow(_cursor, "is_bestseller");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "review_count");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cached_at");
          final ProductCacheEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpSlug;
            if (_cursor.isNull(_cursorIndexOfSlug)) {
              _tmpSlug = null;
            } else {
              _tmpSlug = _cursor.getString(_cursorIndexOfSlug);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpNameTamil;
            if (_cursor.isNull(_cursorIndexOfNameTamil)) {
              _tmpNameTamil = null;
            } else {
              _tmpNameTamil = _cursor.getString(_cursorIndexOfNameTamil);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpShortDesc;
            if (_cursor.isNull(_cursorIndexOfShortDesc)) {
              _tmpShortDesc = null;
            } else {
              _tmpShortDesc = _cursor.getString(_cursorIndexOfShortDesc);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpImagesJson;
            if (_cursor.isNull(_cursorIndexOfImagesJson)) {
              _tmpImagesJson = null;
            } else {
              _tmpImagesJson = _cursor.getString(_cursorIndexOfImagesJson);
            }
            final String _tmpSkusJson;
            if (_cursor.isNull(_cursorIndexOfSkusJson)) {
              _tmpSkusJson = null;
            } else {
              _tmpSkusJson = _cursor.getString(_cursorIndexOfSkusJson);
            }
            final String _tmpTagsJson;
            if (_cursor.isNull(_cursorIndexOfTagsJson)) {
              _tmpTagsJson = null;
            } else {
              _tmpTagsJson = _cursor.getString(_cursorIndexOfTagsJson);
            }
            final boolean _tmpIsFeatured;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFeatured);
            _tmpIsFeatured = _tmp != 0;
            final boolean _tmpIsBestseller;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsBestseller);
            _tmpIsBestseller = _tmp_1 != 0;
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new ProductCacheEntity(_tmpId,_tmpSlug,_tmpName,_tmpNameTamil,_tmpDescription,_tmpShortDesc,_tmpCategory,_tmpImagesJson,_tmpSkusJson,_tmpTagsJson,_tmpIsFeatured,_tmpIsBestseller,_tmpRating,_tmpReviewCount,_tmpCachedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Object getCacheSize(final Continuation<? super Integer> arg0) {
    final String _sql = "SELECT COUNT(*) FROM product_cache";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg0);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
