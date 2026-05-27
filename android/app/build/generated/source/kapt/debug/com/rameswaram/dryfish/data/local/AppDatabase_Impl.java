package com.rameswaram.dryfish.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CartDao _cartDao;

  private volatile ProductCacheDao _productCacheDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cart_items` (`id` TEXT NOT NULL, `product_id` TEXT NOT NULL, `product_name` TEXT NOT NULL, `product_image` TEXT NOT NULL, `selected_sku_id` TEXT NOT NULL, `weight` TEXT NOT NULL, `price` REAL NOT NULL, `mrp` REAL NOT NULL, `quantity` INTEGER NOT NULL, `max_quantity` INTEGER NOT NULL, `product_slug` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `product_cache` (`id` TEXT NOT NULL, `slug` TEXT NOT NULL, `name` TEXT NOT NULL, `name_tamil` TEXT, `description` TEXT, `short_desc` TEXT, `category` TEXT NOT NULL, `images_json` TEXT NOT NULL, `skus_json` TEXT NOT NULL, `tags_json` TEXT NOT NULL, `is_featured` INTEGER NOT NULL, `is_bestseller` INTEGER NOT NULL, `rating` REAL NOT NULL, `review_count` INTEGER NOT NULL, `cached_at` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '43d4202208fdff365ae4e9316798f842')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cart_items`");
        db.execSQL("DROP TABLE IF EXISTS `product_cache`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCartItems = new HashMap<String, TableInfo.Column>(11);
        _columnsCartItems.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItems.put("product_id", new TableInfo.Column("product_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItems.put("product_name", new TableInfo.Column("product_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItems.put("product_image", new TableInfo.Column("product_image", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItems.put("selected_sku_id", new TableInfo.Column("selected_sku_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItems.put("weight", new TableInfo.Column("weight", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItems.put("price", new TableInfo.Column("price", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItems.put("mrp", new TableInfo.Column("mrp", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItems.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItems.put("max_quantity", new TableInfo.Column("max_quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItems.put("product_slug", new TableInfo.Column("product_slug", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCartItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCartItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCartItems = new TableInfo("cart_items", _columnsCartItems, _foreignKeysCartItems, _indicesCartItems);
        final TableInfo _existingCartItems = TableInfo.read(db, "cart_items");
        if (!_infoCartItems.equals(_existingCartItems)) {
          return new RoomOpenHelper.ValidationResult(false, "cart_items(com.rameswaram.dryfish.data.local.CartEntity).\n"
                  + " Expected:\n" + _infoCartItems + "\n"
                  + " Found:\n" + _existingCartItems);
        }
        final HashMap<String, TableInfo.Column> _columnsProductCache = new HashMap<String, TableInfo.Column>(15);
        _columnsProductCache.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("slug", new TableInfo.Column("slug", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("name_tamil", new TableInfo.Column("name_tamil", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("short_desc", new TableInfo.Column("short_desc", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("images_json", new TableInfo.Column("images_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("skus_json", new TableInfo.Column("skus_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("tags_json", new TableInfo.Column("tags_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("is_featured", new TableInfo.Column("is_featured", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("is_bestseller", new TableInfo.Column("is_bestseller", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("rating", new TableInfo.Column("rating", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("review_count", new TableInfo.Column("review_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProductCache.put("cached_at", new TableInfo.Column("cached_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProductCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProductCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoProductCache = new TableInfo("product_cache", _columnsProductCache, _foreignKeysProductCache, _indicesProductCache);
        final TableInfo _existingProductCache = TableInfo.read(db, "product_cache");
        if (!_infoProductCache.equals(_existingProductCache)) {
          return new RoomOpenHelper.ValidationResult(false, "product_cache(com.rameswaram.dryfish.data.local.ProductCacheEntity).\n"
                  + " Expected:\n" + _infoProductCache + "\n"
                  + " Found:\n" + _existingProductCache);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "43d4202208fdff365ae4e9316798f842", "df60b90b6679dfb3d8a4f8713ddc859a");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cart_items","product_cache");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `cart_items`");
      _db.execSQL("DELETE FROM `product_cache`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CartDao.class, CartDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProductCacheDao.class, ProductCacheDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CartDao cartDao() {
    if (_cartDao != null) {
      return _cartDao;
    } else {
      synchronized(this) {
        if(_cartDao == null) {
          _cartDao = new CartDao_Impl(this);
        }
        return _cartDao;
      }
    }
  }

  @Override
  public ProductCacheDao productCacheDao() {
    if (_productCacheDao != null) {
      return _productCacheDao;
    } else {
      synchronized(this) {
        if(_productCacheDao == null) {
          _productCacheDao = new ProductCacheDao_Impl(this);
        }
        return _productCacheDao;
      }
    }
  }
}
